package com.edgar.direwolves.cmd;

import com.edgar.direwolves.core.cmd.ApiCmd;
import com.edgar.direwolves.core.cmd.ApiCmdFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * DeleteApiCmd的工厂类.
 *
 * @author Edgar  Date 2017/1/19
 */
public class DeleteApiCmdFactory implements ApiCmdFactory {
  @Override
  public ApiCmd create(Vertx vertx, JsonObject config) {
    return new DeleteApiCmd();
  }
}
