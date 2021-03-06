package com.edgar.direwolves.plugin.authorization;

import com.edgar.direwolves.core.definition.ApiPlugin;

/**
 * 权限校验策略.
 * 该插件对应的JSON配置的key为<b>scope</b>
 * <pre>
 *   scope 接口的权限值 默认值 default
 * </pre>
 * <p>
 * Created by edgar on 16-12-25.
 */
public interface AuthorisePlugin extends ApiPlugin {

  static AuthorisePlugin create(String scope) {
    return new AuthorisePluginImpl(scope);
  }

  String scope();

  @Override
  default String name() {
    return AuthorisePlugin.class.getSimpleName();
  }
}
