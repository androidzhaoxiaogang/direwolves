package com.edgar.direwolves.plugin.transformer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.edgar.direwolves.core.dispatch.ApiContext;
import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.rpc.RpcRequest;
import com.edgar.direwolves.core.rpc.eventbus.EventbusRpcRequest;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Collection;

/**
 * 将RpcRequest中的请求头，请求参数，请求体按照RequestTransformerPlugin中的配置处理.
 * <p>
 * 执行的顺序为: remove add
 * <p>
 * 该filter的order=15000
 * Created by edgar on 16-9-20.
 */
public class EventbusRequestTransformerFilter implements Filter {

  EventbusRequestTransformerFilter() {
  }

  @Override
  public String type() {
    return PRE;
  }

  @Override
  public int order() {
    return 15000;
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    if (apiContext.apiDefinition() == null) {
      return false;
    }
    return apiContext.apiDefinition()
                   .plugin(RequestTransformerPlugin.class.getSimpleName()) != null
           && apiContext.requests().size() > 0
           && apiContext.requests().stream()
                   .anyMatch(e -> e instanceof EventbusRpcRequest);
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    for (int i = 0; i < apiContext.requests().size(); i++) {
      RpcRequest request = apiContext.requests().get(i);
      if (request instanceof EventbusRpcRequest) {
        transformer(apiContext, (EventbusRpcRequest) request);
      }
    }
    completeFuture.complete(apiContext);
  }


  private void transformer(ApiContext apiContext, EventbusRpcRequest request) {
    String name = request.name();
    RequestTransformerPlugin plugin =
            (RequestTransformerPlugin) apiContext.apiDefinition()
                    .plugin(RequestTransformerPlugin.class.getSimpleName());
    RequestTransformer transformer = plugin.transformer(name);
    if (transformer != null) {
      Multimap<String, String> headers = tranformerHeaders(request.headers(), transformer);
      request.clearHeaders().addHeaders(headers);
      if (request.message() != null) {
        JsonObject body = tranformerBody(request.message(), transformer);
        request.replaceMessage(body);
      }
    }
  }

  private Multimap<String, String> tranformerHeaders(Multimap<String, String> headers,
                                                     RequestTransformer transformer) {
    Multimap<String, String> newHeader = ArrayListMultimap.create(headers);
    transformer.headerRemoved().forEach(h -> newHeader.removeAll(h));
    transformer.headerReplaced().forEach(entry -> {
      if (newHeader.containsKey(entry.getKey())) {
        Collection<String> values = newHeader.removeAll(entry.getKey());
        newHeader.putAll(entry.getValue(), values);
      }
    });
    transformer.headerAdded().forEach(
            entry -> newHeader.replaceValues(entry.getKey(), Lists.newArrayList(entry.getValue())));
    return newHeader;
  }

  private JsonObject tranformerBody(final JsonObject body,
                                    RequestTransformer transformer) {
    JsonObject newBody = body.copy();
    transformer.bodyRemoved().forEach(b -> newBody.remove(b));
    transformer.bodyReplaced().forEach(entry -> {
      if (newBody.containsKey(entry.getKey())) {
        Object value =  newBody.remove(entry.getKey());
        newBody.put(entry.getValue(), value);
      }
    });
    transformer.bodyAdded().forEach(entry -> newBody.put(entry.getKey(), entry.getValue()));
    return newBody;
  }


}