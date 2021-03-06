package com.edgar.direwolves.plugin.arg;

import com.google.common.collect.ArrayListMultimap;

import com.edgar.direwolves.core.dispatch.ApiContext;
import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.util.validation.ValidationException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * 严格校验参数.
 * 如果请求体或者请求参数中包括了未定义的参数，直接抛出ValidationException
 * 该filter可以接受下列的配置参数
 * <pre>
 *   strict_arg bool值是否启用，默认值false
 *   strict_arg.query.excludes 数组，请求参数中允许的例外
 *   strict_arg.body.excludes 数组，请求体中允许的例外
 * </pre>
 * 该filter的order=99
 * Created by edgar on 16-10-28.
 */
public class StrictArgFilter implements Filter {

  private final Set<String> excludeQuery = new HashSet<>();

  private final Set<String> excludeBody = new HashSet<>();

  private final boolean enabled;

  StrictArgFilter(JsonObject config) {
    JsonArray queryArray = config.getJsonArray("strict_arg.query.excludes", new JsonArray());
    for (int i = 0; i < queryArray.size(); i++) {
      excludeQuery.add(queryArray.getString(i));
    }
    JsonArray bodyArray = config.getJsonArray("strict_arg.body.excludes", new JsonArray());
    for (int i = 0; i < bodyArray.size(); i++) {
      excludeBody.add(bodyArray.getString(i));
    }
    this.enabled = config.getBoolean("strict_arg", false);
  }

  @Override
  public String type() {
    return PRE;
  }

  @Override
  public int order() {
    return 99;
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    StrictArgPlugin plugin
            = (StrictArgPlugin) apiContext.apiDefinition()
            .plugin(StrictArgPlugin.class.getSimpleName());
    if (plugin == null) {
      return enabled;
    } else {
      return plugin.strict();
    }
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    UrlArgPlugin urlArgPlugin =
            (UrlArgPlugin) apiContext.apiDefinition().plugin(UrlArgPlugin.class.getSimpleName());

    ArrayListMultimap error = ArrayListMultimap.create();
    if (!apiContext.params().isEmpty()) {
      apiContext.params().keySet().stream()
              .filter(k -> testUrlArg(k, urlArgPlugin))
              .forEach(k -> error.put(k, "prohibited"));
    }
    if (!error.isEmpty()) {
      throw new ValidationException(error);
    }

    BodyArgPlugin bodyArgPlugin =
            (BodyArgPlugin) apiContext.apiDefinition().plugin(BodyArgPlugin.class.getSimpleName());
    ArrayListMultimap bodyError = ArrayListMultimap.create();
    if (apiContext.body() != null) {
      apiContext.body().fieldNames().stream()
              .filter(k -> testBodyArg(k, bodyArgPlugin))
              .forEach(k -> bodyError.put(k, "prohibited"));
    }
    if (!bodyError.isEmpty()) {
      throw new ValidationException(bodyError);
    }
    completeFuture.complete(apiContext);
  }

  private boolean testBodyArg(String argName, BodyArgPlugin plugin) {
    if ("".equalsIgnoreCase(argName)) {
      return false;
    }
    if (excludeBody.contains(argName)) {
      return false;
    }
    return plugin == null || plugin.parameter(argName) == null;
  }

  private boolean testUrlArg(String argName, UrlArgPlugin plugin) {
    if (excludeQuery.contains(argName)) {
      return false;
    }
    return plugin == null || plugin.parameter(argName) == null;
  }

}
