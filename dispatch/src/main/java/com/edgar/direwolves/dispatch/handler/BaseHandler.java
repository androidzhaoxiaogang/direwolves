package com.edgar.direwolves.dispatch.handler;

import com.google.common.base.Joiner;

import com.edgar.direwolves.metric.ApiMetrics;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * http请求的辅助类.所有的请求都可以接受这个处理类，该类主要做一下通用的设置然后就会将请求传递给下一个处理类.
 * <p>
 * 设置响应的content-type为application/json;charset=utf-8
 *
 * @author Edgar  Date 2016/2/18
 */
public class BaseHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseHandler.class);

  public static Handler<RoutingContext> create() {
    return new BaseHandler();
  }

  @Override
  public void handle(RoutingContext rc) {
    if ("1.0".equalsIgnoreCase(rc.request().getParam("v"))) {
      rc.response().setChunked(true)
              .putHeader("content-type", "application/json");
    } else {
      rc.response().setChunked(true)
              .putHeader("content-type", "application/json;charset=utf-8");
    }

    String id = UUID.randomUUID().toString();
    rc.put("x-request-id", id);
    long start = System.currentTimeMillis();
    rc.put("x-request-time", start);
    LOGGER.info("---> [{}] [{}] [{}] [{}] [{}] [{}]", id,
                rc.request().scheme(),
                rc.request().method().name() + " " + rc.normalisedPath(),
                mutiMapToString(rc.request().headers(), "no header"),
                mutiMapToString(rc.request().params(), "no param"),
                (rc.getBody() == null || rc.getBody().length() == 0) ? "no body" : rc.getBody()
                        .toString()
    );

    rc.addBodyEndHandler(v -> {
      LOGGER.info("<--- [{}] [{}] [{}] [{}] [{}ms] [{} bytes]", id,
                  rc.request().scheme(),
                  rc.response().getStatusCode(),
                  mutiMapToString(rc.response().headers(), "no header"),
                  System.currentTimeMillis() - start,
                  rc.response().bytesWritten()
      );
      ApiMetrics.instance()
              .response(id, rc.response().getStatusCode(), System.currentTimeMillis() - start);
    });
    rc.next();
  }

  private String mutiMapToString(MultiMap map, String defaultString) {
    StringBuilder s = new StringBuilder();
    for (String key : map.names()) {
      s.append(key)
              .append(":")
              .append(Joiner.on(",").join(map.getAll(key)))
              .append(";");
    }
    if (s.length() == 0) {
      return defaultString;
    }
    return s.toString();
  }

}