package com.edgar.direwolves.filter;

import com.edgar.direwolves.core.dispatch.ApiContext;
import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.dispatch.Result;
import com.edgar.direwolves.core.rpc.RpcResponse;
import com.edgar.direwolves.core.rpc.http.HttpRpcRequest;
import com.edgar.direwolves.core.utils.Filters;
import com.edgar.util.vertx.task.Task;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2016/9/20.
 *
 * @author Edgar  Date 2016/9/20
 */
@RunWith(VertxUnitRunner.class)
public class ExtractRpcResponseFilterTest {

  private final List<Filter> filters = new ArrayList<>();

  Filter filter;

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();

    filter = Filter.create(ExtractResultFilter.class.getSimpleName(), vertx, new JsonObject());

    filters.clear();
    filters.add(filter);
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }


  @Test
  public void singleJsonObjectShouldAlwaysReturnTheJsonObject(TestContext testContext) {

    ApiContext apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());
    RpcResponse rpcResponse = RpcResponse
            .createJsonObject("1", 200, new JsonObject().put("foo", "bar"), 0);

    apiContext.addResponse(rpcResponse);

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              testContext.assertEquals("bar", result.responseObject().getString("foo"));
              async.complete();
            }).onFailure(t -> testContext.fail());
  }

  @Test
  public void singleJsonArrayShouldAlwaysReturnTheJsonArray(TestContext testContext) {

    ApiContext apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());
    RpcResponse rpcResponse = RpcResponse
            .createJsonArray("1", 200, new JsonArray().add(1).add(2), 0);

    apiContext.addResponse(rpcResponse);

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              testContext.assertEquals(2, result.responseArray().size());
              async.complete();
            }).onFailure(t -> testContext.fail());
  }

  @Test
  public void twoSucceedResponseShouldReturnJsonObject(TestContext testContext) {

    ApiContext apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());
    RpcResponse rpcResponse = RpcResponse
            .createJsonObject("1", 200, new JsonObject().put("foo", "bar"), 0);
    apiContext.addResponse(rpcResponse);
    rpcResponse = RpcResponse.createJsonArray("2", 202, new JsonArray().add(1).add(2), 0);
    apiContext.addResponse(rpcResponse);

    apiContext.requests().add(HttpRpcRequest.create("1", "a"));
    apiContext.requests().add(HttpRpcRequest.create("2", "b"));

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              testContext.assertEquals(2, result.responseObject().size());
              testContext.assertEquals(200, result.statusCode());
              testContext.assertEquals("bar", result.responseObject().getJsonObject("a")
                      .getString("foo"));
              testContext.assertEquals(2, result.responseObject().getJsonArray("b").size());
              async.complete();
            }).onFailure(t -> testContext.fail());

  }

  @Test
  public void onceFailedResponseShouldReturnTheFailedJsonObject(TestContext testContext) {

    ApiContext apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());
    RpcResponse rpcResponse = RpcResponse
            .createJsonObject("1", 200, new JsonObject().put("foo", "bar"), 0);
    apiContext.addResponse(rpcResponse);
    rpcResponse = RpcResponse.createJsonObject("2", 400, new JsonObject().put("bar", "foo"), 0);
    apiContext.addResponse(rpcResponse);

    apiContext.requests().add(HttpRpcRequest.create("1", "a"));
    apiContext.requests().add(HttpRpcRequest.create("2", "b"));

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              testContext.assertEquals(1, result.responseObject().size());
              testContext.assertEquals("foo", result.responseObject().getString("bar"));
              async.complete();
            }).onFailure(t -> testContext.fail());
  }

  @Test
  public void twoFailedResponseShouldReturnTheFirstFailedJsonObject(TestContext testContext) {

    ApiContext apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());
    RpcResponse rpcResponse = RpcResponse
            .createJsonObject("1", 403, new JsonObject().put("foo", "bar"), 0);
    apiContext.addResponse(rpcResponse);
    rpcResponse = RpcResponse.createJsonObject("2", 400, new JsonObject().put("bar", "foo"), 0);
    apiContext.addResponse(rpcResponse);

    apiContext.requests().add(HttpRpcRequest.create("1", "a"));
    apiContext.requests().add(HttpRpcRequest.create("2", "b"));

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              testContext.assertEquals(1, result.responseObject().size());
              testContext.assertEquals("bar", result.responseObject().getString("foo"));
              async.complete();
            }).onFailure(t -> testContext.fail());
  }
}