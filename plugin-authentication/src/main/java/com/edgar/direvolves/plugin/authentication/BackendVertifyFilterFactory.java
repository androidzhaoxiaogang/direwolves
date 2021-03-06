package com.edgar.direvolves.plugin.authentication;

import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.dispatch.FilterFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * backendVertifyFilter的工厂类.
 * Created by edgar on 16-12-11.
 */
public class BackendVertifyFilterFactory implements FilterFactory {
  @Override
  public String name() {
    return BackendVertifyFilter.class.getSimpleName();
  }

  @Override
  public Filter create(Vertx vertx, JsonObject config) {
    return new BackendVertifyFilter(vertx, config);
  }
}
