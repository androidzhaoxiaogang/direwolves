package com.edgar.direwolves.dispatch.verticle;

import com.google.common.base.Strings;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.edgar.direwolves.core.cache.RedisProvider;
import com.edgar.direwolves.dispatch.handler.BaseHandler;
import com.edgar.direwolves.dispatch.handler.DispatchHandler;
import com.edgar.direwolves.dispatch.handler.FailureHandler;
import com.edgar.direwolves.metric.ApiMetrics;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.serviceproxy.ProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Edgar on 2016/9/12.
 *
 * @author Edgar  Date 2016/9/12
 */
public class ApiDispatchVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiDispatchVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    LOGGER.info("\n"
                + "                    _ _                 _       _               \n"
                + "                   | (_)               | |     | |              \n"
                + "                 __| |_ ___ _ __   __ _| |_ ___| |__   ___ _ __ \n"
                + "                / _` | / __| '_ \\ / _` | __/ __| '_ \\ / _ \\ '__|\n"
                + "               | (_| | \\__ \\ |_) | (_| | || (__| | | |  __/ |   \n"
                + "                \\__,_|_|___/ .__/ \\__,_|\\__\\___|_| |_|\\___|_|   \n"
                + "                           | |                                  \n"
                + "                           |_|                              ");

    LOGGER.info("config->{}", config().encodePrettily());

    RedisProvider redisProvider = RedisProvider.create(vertx, config());

    String namespace = config().getString("project.namespace", "");
    String address = RedisProvider.class.getName();
    if (!Strings.isNullOrEmpty(namespace)) {
      address = namespace + "." + address;
    }

    ProxyHelper.registerService(RedisProvider.class, vertx, redisProvider,
                                address);

    //API Metrics
    String regisryName = System.getProperty("vertx.metrics.options.registryName", "my-register");
    MetricRegistry registry = SharedMetricRegistries.getOrCreate(regisryName);
    ApiMetrics.create(registry, namespace, 10000);

    //Diapatch
    DispatchHandler dispatchHandler = DispatchHandler.create(vertx, config());

    int port = config().getInteger("http.port", 8080);

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

    router.route().handler(BaseHandler.create());
    router.route().handler(ResponseTimeHandler.create());

    //API拦截
    router.route().handler(dispatchHandler)
            .failureHandler(FailureHandler.create());

    vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(port, ar -> {
              if (ar.succeeded()) {
                LOGGER.info("---| [Diaptacher Start] [OK] [{}]", port);
                startFuture.complete();
              } else {
                LOGGER.error("---| [Diaptacher Start] [FAILED] [{}]", port, ar.cause());
                startFuture.fail(ar.cause());
              }
            });
  }
}
