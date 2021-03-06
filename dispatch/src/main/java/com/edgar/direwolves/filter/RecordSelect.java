package com.edgar.direwolves.filter;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * 根据服务名获取一个服务实例的接口.
 * <p>
 * 目前仅支持consul的服务发现，如果不想使用consul，可以自定义一个http服务，模拟consul的http查询.可以参考MockConsulHttpVerticle的实现..
 *
 * @author Edgar  Date 2016/10/12
 */
public interface RecordSelect {

  /**
   * 根据服务名获取一个服务实例.
   *
   * @param service 服务名
   * @return 服务实例的future
   */
  Future<Record> select(String service);

  static RecordSelect create(Vertx vertx, JsonObject config) {
    return new RecordSelectImpl(vertx, config);
  }
}
