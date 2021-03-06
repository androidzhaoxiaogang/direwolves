package com.edgar.direwolves.plugin.ip;

import com.edgar.direwolves.core.definition.ApiPlugin;

import java.util.List;

/**
 * IP限制策略.
 * 该插件对应的JSON配置的key为<b>ip_restriction</b>，它有两个属性:
 * <pre>
 *   whitelist 白名单列表
 *   blacklist 黑名单列表
 * </pre>
 * json配置:
 * <pre>
 * "ip_restriction" : {
 *    "whitelist" : ["192.168.0.1", "10.4.7.*"],
 *    "blacklist" : ["192.168.0.100"]
 * }
 * </pre>
 *
 * @author Edgar  Date 2016/9/14
 */
public interface IpRestriction extends ApiPlugin {

  /**
   * 增加白名单.
   * 如果黑名单中存在该IP，从黑名单删除.
   * 每个接口最多允许添加100个白名单，超过100个白名单应该采用其他方式。
   *
   * @param ip ip地址，未做严格校验.允许使用一个完整的IP地址192.168.1.1或者使用通配符192.168.1.*
   * @return IpRestrictionPlugin
   */
  IpRestriction addWhitelist(String ip);

  /**
   * 增加黑名单.
   * 如果白名单中存在该IP，从白名单中删除.
   * 每个接口最多允许添加100个黑名单，超过100个黑名单应该采用其他方式。
   *
   * @param ip ip地址，未做严格校验.允许使用一个完整的IP地址192.168.1.1或者使用通配符192.168.1.*
   * @return IpRestrictionPlugin
   */
  IpRestriction addBlacklist(String ip);

  /**
   * 删除白名单.
   *
   * @param ip ip地址，未做严格校验.允许使用一个完整的IP地址192.168.1.1或者使用通配符192.168.1.*
   * @return IpRestrictionPlugin
   */
  IpRestriction removeWhitelist(String ip);

  /**
   * 删除黑名单.
   *
   * @param ip ip地址，未做严格校验.允许使用一个完整的IP地址192.168.1.1或者使用通配符192.168.1.*
   * @return IpRestrictionPlugin
   */
  IpRestriction removeBlacklist(String ip);

  /**
   * 删除所有白名单.
   *
   * @return IpRestrictionPlugin
   */
  IpRestriction clearWhitelist();

  /**
   * 删除所有黑名单.
   *
   * @return IpRestrictionPlugin
   */
  IpRestriction clearBlacklist();

  /**
   * @return 白名单列表
   */
  List<String> whitelist();

  /**
   * @return 黑名单列表
   */
  List<String> blacklist();

  static IpRestriction create() {
    return new IpRestrictionImpl();
  }

  @Override
  default String name() {
    return IpRestriction.class.getSimpleName();
  }
}
