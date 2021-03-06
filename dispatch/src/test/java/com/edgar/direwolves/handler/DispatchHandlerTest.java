package com.edgar.direwolves.handler;

import static org.awaitility.Awaitility.await;

import com.google.common.collect.Lists;

import com.edgar.direwolves.ApiUtils;
import com.edgar.direwolves.core.definition.ApiDefinition;
import com.edgar.direwolves.core.definition.Endpoint;
import com.edgar.direwolves.dispatch.verticle.ApiDispatchVerticle;
import com.edgar.direwolves.filter.servicediscovery.MockConsulHttpVerticle;
import com.edgar.direwolves.verticle.ApiDefinitionRegistry;
import com.edgar.direwolves.verticle.ApiDefinitionVerticle;
import com.edgar.util.base.Randoms;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Edgar on 2017/1/3.
 *
 * @author Edgar  Date 2017/1/3
 */
@RunWith(VertxUnitRunner.class)
public class DispatchHandlerTest {

  Vertx vertx;

  int port = Integer.parseInt(Randoms.randomNumber(4));

  MockConsulHttpVerticle mockConsulHttpVerticle;

  AtomicBoolean started = new AtomicBoolean();

  @Before
  public void setUp(TestContext testContext) {
    vertx = Vertx.vertx();

    JsonObject config = new JsonObject()
            .put("service.discovery", "consul://localhost:8500")
            .put("http.port", port)
            .put("consul.port", 8500);
    vertx.deployVerticle(ApiDispatchVerticle.class.getName(),
                         new DeploymentOptions().setConfig(config),
                         ar -> {
                           started.set(true);
                         });

    mockConsulHttpVerticle = new MockConsulHttpVerticle();
    vertx.deployVerticle(mockConsulHttpVerticle,
                         new DeploymentOptions().setConfig(config),
                         testContext.asyncAssertSuccess());
    add2Servers();

    vertx.deployVerticle(ApiDefinitionVerticle.class.getName(), testContext.asyncAssertSuccess());

    ApiUtils.registerApi();

    vertx.deployVerticle(DeviceHttpVerticle.class.getName(),
                         new DeploymentOptions().setConfig(new JsonObject().put("http.port",
                                                                                9001)).setWorker
                                 (true),
                         testContext.asyncAssertSuccess());
    await().until(() -> started.get());
  }

  private void add2Servers() {
    mockConsulHttpVerticle.addService(new JsonObject()
                                              .put("Node", "u221")
                                              .put("Address", "localhost")
                                              .put("ServiceID", "u221:device:9001")
                                              .put("ServiceName", "device")
                                              .put("ServiceTags", new JsonArray())
                                              .put("ServicePort", 9001));
    mockConsulHttpVerticle.addService((new JsonObject()
            .put("Node", "u222")
            .put("Address", "localhost")
            .put("ServiceID", "u222:device:9002")
            .put("ServiceName", "user")
            .put("ServiceTags", new JsonArray())
            .put("ServicePort", 9002)));
    try {
      TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @After
  public void tearDown(TestContext testContext) {
    vertx.close(ar -> {
      started.set(false);
    });
    await().until(() -> !started.get());
  }

  @Test
  public void testGetError(TestContext testContext) {
    Async async = testContext.async();
    vertx.createHttpClient()
            .get(port, "localhost", "/devices/failed?timestamp=" + Instant.now().getEpochSecond(),
                 resp -> {
                   resp.bodyHandler(body -> {
                     System.out.println(body.toString());
                     testContext.assertTrue(resp.statusCode() == 400);
                     String reqId = resp.getHeader("x-request-id");
                     testContext.assertNotNull(reqId);
                     async.complete();
                   });
                 }).end();
  }

  @Test
  public void testGetArray(TestContext testContext) {
    Async async = testContext.async();
    vertx.createHttpClient()
            .get(port, "localhost", "/devices?timestamp=" + Instant.now().getEpochSecond(),
                 resp -> {
                   resp.bodyHandler(body -> {
                     System.out.println(body.toString());
                     testContext.assertTrue(resp.statusCode() < 300);
                     JsonArray jsonArray = new JsonArray(body.toString());
                     testContext.assertEquals(2, jsonArray.size());
                     String reqId = resp.getHeader("x-request-id");
                     testContext.assertNotNull(reqId);
                     async.complete();
                   });
                 }).end();
  }

  @Test
  public void testGetObject(TestContext testContext) {
    Async async = testContext.async();
    int userId = Integer.parseInt(Randoms.randomNumber(5));
    vertx.createHttpClient()
            .get(port, "localhost",
                 "/devices/" + userId + "?timestamp="
                 + Instant.now().getEpochSecond(),
                 resp -> {
                   resp.bodyHandler(body -> {
                     System.out.println(body.toString());
                     testContext.assertTrue(resp.statusCode() < 300);
                     JsonObject jsonObject = new JsonObject(body.toString());
                     testContext.assertEquals(userId + "", jsonObject.getString("id"));
                     String reqId = resp.getHeader("x-request-id");
                     testContext.assertNotNull(reqId);
                     async.complete();
                   });
                 }).end();
  }

  @Test
  public void testPostObject(TestContext testContext) {
    Async async = testContext.async();
    vertx.createHttpClient()
            .post(port, "localhost",
                  "/devices?timestamp="
                  + Instant.now().getEpochSecond(),
                  resp -> {
                    resp.bodyHandler(body -> {
                      System.out.println(body.toString());
                      testContext.assertTrue(resp.statusCode() < 300);
                      JsonObject jsonObject = new JsonObject(body.toString());
                      testContext.assertEquals("bar",
                                               jsonObject.getJsonObject("body").getString("foo"));
                      String reqId = resp.getHeader("x-request-id");
                      testContext.assertNotNull(reqId);
                      async.complete();
                    });
                  }).setChunked(true)
            .write(new JsonObject().put("foo", "bar").encode()).end();
  }

  @Test
  public void testPutObject(TestContext testContext) {
    Async async = testContext.async();
    int userId = Integer.parseInt(Randoms.randomNumber(5));
    vertx.createHttpClient()
            .put(port, "localhost",
                 "/devices/" + userId + "?timestamp="
                 + Instant.now().getEpochSecond(),
                 resp -> {
                   resp.bodyHandler(body -> {
                     System.out.println(body.toString());
                     testContext.assertTrue(resp.statusCode() < 300);
                     JsonObject jsonObject = new JsonObject(body.toString());
                     testContext.assertEquals("bar",
                                              jsonObject.getJsonObject("body").getString("foo"));
                     String reqId = resp.getHeader("x-request-id");
                     testContext.assertNotNull(reqId);
                     async.complete();
                   });
                 }).setChunked(true)
            .end(new JsonObject().put("foo", "bar").encode());
  }
}
