package com.edgar.direwolves.plugin.authorization;

import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.dispatch.FilterFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by edgar on 16-12-22.
 */
public class AuthoriseFilterFactory implements FilterFactory {
  @Override
  public String name() {
    return AuthoriseFilter.class.getSimpleName();
  }

  @Override
  public Filter create(Vertx vertx, JsonObject config) {
    return new AuthoriseFilter(vertx, config);
  }
}
