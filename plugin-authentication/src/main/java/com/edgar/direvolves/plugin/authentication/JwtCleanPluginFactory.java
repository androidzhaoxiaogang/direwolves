package com.edgar.direvolves.plugin.authentication;

import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.direwolves.core.definition.ApiPluginFactory;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2016/10/31.
 *
 * @author Edgar  Date 2016/10/31
 */
public class JwtCleanPluginFactory implements ApiPluginFactory {
  @Override
  public String name() {
    return JwtCleanPlugin.class.getSimpleName();
  }

  @Override
  public ApiPlugin create() {
    return new JwtCleanPlugin();
  }

  @Override
  public ApiPlugin decode(JsonObject jsonObject) {
    if (jsonObject.getBoolean("jwt_clean", false)) {
      return new JwtCleanPlugin();
    }
    return null;
  }

  @Override
  public JsonObject encode(ApiPlugin plugin) {
    return new JsonObject().put("jwt_clean", true);
  }
}
