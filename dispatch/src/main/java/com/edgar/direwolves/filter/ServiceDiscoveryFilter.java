package com.edgar.direwolves.filter;

import com.edgar.direwolves.core.definition.HttpEndpoint;
import com.edgar.direwolves.core.dispatch.ApiContext;
import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.rpc.HttpRpcRequest;
import com.edgar.direwolves.core.rpc.RpcRequest;
import com.edgar.direwolves.filter.servicediscovery.RecordSelect;
import com.edgar.util.exception.DefaultErrorCode;
import com.edgar.util.exception.SystemException;
import com.edgar.util.vertx.task.Task;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Edgar on 2016/11/18.
 *
 * @author Edgar  Date 2016/11/18
 */
public class ServiceDiscoveryFilter implements Filter {

  private Vertx vertx;

  private RecordSelect recordSelect = RecordSelect.create();

  ServiceDiscoveryFilter(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    recordSelect.config(vertx, config);
  }

  @Override
  public String type() {
    return PRE;
  }

  @Override
  public int order() {
    return 9000;
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    return apiContext.requests().size() == 0;
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    List<Future<Record>> futures = new ArrayList<>();
    apiContext.apiDefinition().endpoints().stream()
        .filter(e -> e instanceof HttpEndpoint)
        .map(e -> ((HttpEndpoint) e).service())
        .collect(Collectors.toSet())
        .forEach(s -> futures.add(serviceFuture(s)));

    Task.par(futures)
        .andThen(records -> {
          apiContext.apiDefinition().endpoints().stream()
              .filter(e -> e instanceof HttpEndpoint)
              .map(e -> toRpc(apiContext, (HttpEndpoint) e, records))
              .forEach(req -> apiContext.addRequest(req));
        })
        .andThen(records -> completeFuture.complete(apiContext))
        .onFailure(throwable -> completeFuture.fail(throwable));
  }

  private Future<Record> serviceFuture(String service) {
    //服务发现
    Future<Record> serviceFuture = Future.future();

    Future<Record> future = recordSelect.select(service);
    future.setHandler(ar -> {
      if (ar.succeeded()) {
        Record record = ar.result();
        if (record == null) {
          serviceFuture.fail(SystemException.create(DefaultErrorCode.UNKOWN_REMOTE));
        } else {
          serviceFuture.complete(record);
        }
      } else {
        serviceFuture.fail(SystemException.create(DefaultErrorCode.UNKOWN_REMOTE));
      }
    });
    return serviceFuture;
  }

  private RpcRequest toRpc(ApiContext apiContext, HttpEndpoint endpoint, List<Record> records) {
    HttpRpcRequest httpRpcRequest =
        HttpRpcRequest.create(UUID.randomUUID().toString(), endpoint.name());
    httpRpcRequest.setPath(endpoint.path());
    httpRpcRequest.setHttpMethod(endpoint.method());
    httpRpcRequest.addParams(apiContext.params());
    httpRpcRequest.addHeaders(apiContext.headers());
    httpRpcRequest.addHeader("x-request-id", httpRpcRequest.getId());
    httpRpcRequest.setBody(apiContext.body());
    List<Record> recordList = records.stream()
        .filter(r -> endpoint.service().equalsIgnoreCase(r.getName()))
        .collect(Collectors.toList());
    if (recordList.isEmpty()) {
      throw SystemException.create(DefaultErrorCode.UNKOWN_REMOTE);
    }
    Record record = recordList.get(0);
    httpRpcRequest.setHost(record.getLocation().getString("host"));
    httpRpcRequest.setPort(record.getLocation().getInteger("port"));
    return httpRpcRequest;
  }
}
