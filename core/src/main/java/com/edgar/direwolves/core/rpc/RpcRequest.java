package com.edgar.direwolves.core.rpc;

import io.vertx.core.json.JsonObject;

public interface RpcRequest {

  /**
   * @return id
   */
  String id();

  /**
   * @return 名称
   */
  String name();

  /**
   * @return 类型
   */
  String type();

  /**
   * 复制RPC请求
   *
   * @return RpcRequest
   */
  RpcRequest copy();

}