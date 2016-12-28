package com.edgar.direwolves.plugin.transformer;

import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.direwolves.core.definition.ApiPluginFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.stream.Collectors;

/**
 * Request Transformer的工厂类.
 * json配置
 * <pre>
 *     "request_transformer" : [
 * {
 * "name" : "add_device",
 * "header.remove" : ["h3", "h4"],
 * "query.remove" : ["q3", "q4"],
 * "body.remove" : ["p3", "p4"],
 * "header.replace" : ["h5:v2", "h6:v1"],
 * "query.replace" : ["q5:v2", "q6:v1"],
 * "body.replace" : ["p5:v2", "p6:v1"],
 * "header.add" : ["h1:v2", "h2:v1"],
 * "query.add" : ["q1:v2", "q2:v1"],
 * "body.add" : ["p1:v2", "p2:v1"]
 * }
 * ]
 * </pre>
 * <p/>
 * Created by edgar on 16-10-23.
 */
public class RequestTransformerPluginFactory implements ApiPluginFactory<RequestTransformerPlugin> {
  @Override
  public String name() {
    return RequestTransformerPlugin.class.getSimpleName();
  }

  @Override
  public ApiPlugin create() {
    return new RequestTransformerPluginImpl();
  }

  @Override
  public RequestTransformerPlugin decode(JsonObject jsonObject) {
    if (!jsonObject.containsKey("request_transformer")) {
      return null;
    }
    RequestTransformerPlugin plugin = new RequestTransformerPluginImpl();
    JsonArray jsonArray = jsonObject.getJsonArray("request_transformer", new JsonArray());
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject request = jsonArray.getJsonObject(i);
      String name = request.getString("name");
      RequestTransformer transformer = RequestTransformer.create(name);
      removeBody(request, transformer);
      removeHeader(request, transformer);
      removeParam(request, transformer);

      addBody(request, transformer);
      addHeader(request, transformer);
      addParam(request, transformer);

      plugin.addTransformer(transformer);
    }

    return plugin;
  }

  @Override
  public JsonObject encode(RequestTransformerPlugin plugin) {
    JsonArray jsonArray = new JsonArray();
    plugin.transformers().stream()
        .map(t -> toJson(t)
        ).forEach(j -> jsonArray.add(j));
    return new JsonObject().put("request_transformer", jsonArray);
  }

  private JsonObject toJson(RequestTransformer transformer) {
    return new JsonObject()
        .put("name", transformer.name())
        .put("header.remove", transformer.headerRemoved())
        .put("query.remove", transformer.paramRemoved())
        .put("body.remove", transformer.bodyRemoved())
        .put("header.add", transformer.headerAdded()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("query.add", transformer.paramAdded()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()))
        .put("body.add", transformer.bodyAdded()
            .stream()
            .map(entry -> entry.getKey() + ":" + entry.getValue())
            .collect(Collectors.toList()));
  }

  private void removeHeader(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray removes = endpoint.getJsonArray("header.remove", new JsonArray());
    for (int j = 0; j < removes.size(); j++) {
      transformer.removeHeader(removes.getString(j));
    }
  }

  private void removeParam(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray removes = endpoint.getJsonArray("query.remove", new JsonArray());
    for (int j = 0; j < removes.size(); j++) {
      transformer.removeParam(removes.getString(j));
    }
  }

  private void removeBody(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray removes = endpoint.getJsonArray("body.remove", new JsonArray());
    for (int j = 0; j < removes.size(); j++) {
      transformer.removeBody(removes.getString(j));
    }
  }

  private void addHeader(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray adds = endpoint.getJsonArray("header.add", new JsonArray());
    for (int j = 0; j < adds.size(); j++) {
      String value = adds.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer.addHeader(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void addParam(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray adds = endpoint.getJsonArray("query.add", new JsonArray());
    for (int j = 0; j < adds.size(); j++) {
      String value = adds.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer.addParam(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }

  private void addBody(JsonObject endpoint, RequestTransformer transformer) {
    JsonArray adds = endpoint.getJsonArray("body.add", new JsonArray());
    for (int j = 0; j < adds.size(); j++) {
      String value = adds.getString(j);
      Iterable<String> iterable =
          Splitter.on(":").omitEmptyStrings().trimResults().split(value);
      transformer.addBody(Iterables.get(iterable, 0), Iterables.get(iterable, 1));
    }
  }
}
