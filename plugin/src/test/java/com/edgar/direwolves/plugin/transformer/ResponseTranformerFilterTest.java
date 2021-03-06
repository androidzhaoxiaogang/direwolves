package com.edgar.direwolves.plugin.transformer;

import com.edgar.direwolves.core.definition.HttpEndpoint;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

import com.edgar.direwolves.core.definition.ApiDefinition;
import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.direwolves.core.definition.Endpoint;
import com.edgar.direwolves.core.dispatch.ApiContext;
import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.dispatch.Result;
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
import org.junit.Assert;
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
public class ResponseTranformerFilterTest {

  private final List<Filter> filters = new ArrayList<>();

  ResponseTransformerFilter filter;

  private ApiContext apiContext;

  private Vertx vertx;

  @Before
  public void setUp() {
    vertx = Vertx.vertx();

    filter = new ResponseTransformerFilter();

    filters.clear();
    filters.add(filter);
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void testOrderAndType(TestContext testContext) {
    Assert.assertEquals(1000, filter.order());
    Assert.assertEquals(Filter.POST, filter.type());
  }


  @Test
  public void testResponseTransformer(TestContext testContext) {
    ResponseTransformerPlugin plugin = (ResponseTransformerPlugin) ApiPlugin
            .create(ResponseTransformerPlugin.class.getSimpleName());
    plugin.removeHeader("h3");
    plugin.removeHeader("h4");
    plugin.removeBody("b3");
    plugin.removeBody("b4");
    plugin.replaceHeader("h5", "rh5");
    plugin.replaceHeader("h6", "rh6");
    plugin.replaceBody("b5", "rb5");
    plugin.replaceBody("b6", "rb6");
    plugin.addHeader("h2", "h2");
    plugin.addHeader("h1", "h1");
    plugin.addBody("b1", "b1");
    plugin.addBody("b2", "b2");
    apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());

    com.edgar.direwolves.core.definition.HttpEndpoint httpEndpoint =
            HttpEndpoint.http("add_device", HttpMethod.GET, "devices/", "device");
    ApiDefinition definition = ApiDefinition
            .create("add_device", HttpMethod.GET, "devices/", Lists.newArrayList(httpEndpoint));
    definition.addPlugin(plugin);
    apiContext.setApiDefinition(definition);
    apiContext.setResult(Result.createJsonObject(
            200, new JsonObject().put("foo", "bar").put("b3", "b3").put("b5", "b5"),
            ImmutableMultimap.of("h3", "h3", "h6", "h6")));

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              System.out.println(result);
              testContext.assertEquals(3, result.header().keys().size());
              testContext.assertEquals(4, result.responseObject().size());
              testContext.assertFalse(result.header().containsKey("rh5"));
              testContext.assertTrue(result.header().containsKey("rh6"));
              testContext.assertTrue(result.responseObject().containsKey("rb5"));
              testContext.assertFalse(result.responseObject().containsKey("rb6"));
              async.complete();
            }).onFailure(t -> {
      t.printStackTrace();
      testContext.fail();
    });
  }

  @Test
  public void testJsonArrayIncludeBody(TestContext testContext) {
    ResponseTransformerPlugin plugin = (ResponseTransformerPlugin) ApiPlugin
            .create(ResponseTransformerPlugin.class.getSimpleName());
    plugin.removeHeader("h3");
    plugin.removeHeader("h4");
    plugin.removeBody("b3");
    plugin.removeBody("b4");
    plugin.replaceHeader("h5", "rh5");
    plugin.replaceHeader("h6", "rh6");
    plugin.replaceBody("b5", "rb5");
    plugin.replaceBody("b6", "rb6");
    plugin.addHeader("h2", "h2");
    plugin.addHeader("h1", "h1");
    plugin.addBody("b1", "b1");
    plugin.addBody("b2", "b2");
    apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", null, null, new JsonObject());

    com.edgar.direwolves.core.definition.HttpEndpoint httpEndpoint =
            HttpEndpoint.http("add_device", HttpMethod.GET, "devices/", "device");
    ApiDefinition definition = ApiDefinition
            .create("add_device", HttpMethod.GET, "devices/", Lists.newArrayList(httpEndpoint));
    definition.addPlugin(plugin);
    apiContext.setApiDefinition(definition);
    apiContext.setResult(Result.createJsonArray(
            200, new JsonArray().add(1),
            ImmutableMultimap.of("h3", "h3", "h6", "h6")));

    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              Result result = context.result();
              testContext.assertEquals(3, result.header().keys().size());
              testContext.assertEquals(1, result.responseArray().size());
              testContext.assertFalse(result.header().containsKey("rh5"));
              testContext.assertTrue(result.header().containsKey("rh6"));
              async.complete();
            }).onFailure(t -> {
      t.printStackTrace();
      testContext.fail();
    });
  }
}