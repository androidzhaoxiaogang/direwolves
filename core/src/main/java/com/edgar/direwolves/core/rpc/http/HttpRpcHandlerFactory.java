package com.edgar.direwolves.core.rpc.http;

import com.edgar.direwolves.core.definition.HttpEndpoint;
import com.edgar.direwolves.core.rpc.RpcHandler;
import com.edgar.direwolves.core.rpc.RpcHandlerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by edgar on 16-12-31.
 */
public class HttpRpcHandlerFactory implements RpcHandlerFactory {
  @Override
  public String type() {
    return HttpEndpoint.TYPE;
  }

  @Override
  public RpcHandler create(Vertx vertx, JsonObject config) {
    return new HttpRpcHandler(vertx, config);
  }
}
