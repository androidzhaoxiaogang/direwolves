package com.edgar.direwolves.plugin.arg;

import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.util.validation.Rule;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by edgar on 16-10-22.
 */
public class BodyArgPluginTest {

  @Test
  public void testDecode() {
    JsonObject jsonObject = new JsonObject();
    JsonObject arg1 = new JsonObject()
            .put("name", "macAddress")
            .put("default_value", "FFFFFFFFFFFF")
            .put("rules", new JsonObject().put("required", true)
                    .put("regex", "[0-9A-F]{16}"));
    JsonObject arg2 = new JsonObject()
            .put("name", "type")
            .put("rules", new JsonObject().put("required", true)
                    .put("integer", true));
    JsonObject arg3 = new JsonObject()
            .put("name", "barcode");
    JsonArray urlArgs = new JsonArray()
            .add(arg1).add(arg2).add(arg3);
    jsonObject.put("body_arg", urlArgs);
    BodyArgPlugin plugin = (BodyArgPlugin) new BodyArgPluginFactory().decode(jsonObject);
    Assert.assertEquals(3, plugin.parameters().size());
  }

  @Test
  public void testEncode() {
    BodyArgPlugin bodyArgPlugin =
            (BodyArgPlugin) ApiPlugin.create(BodyArgPlugin.class.getSimpleName());
    bodyArgPlugin.add(Parameter.create("macAddress", "FFFFFFFFFFFF")
                              .addRule(Rule.regex("[0-9A-F]{16}")));
    bodyArgPlugin.add(Parameter.create("type", null)
                              .addRule(Rule.required())
                              .addRule(Rule.integer()));
    bodyArgPlugin.add(Parameter.create("barcode", null));
    JsonObject jsonObject = bodyArgPlugin.encode();
    Assert.assertTrue(jsonObject.containsKey("body_arg"));
    JsonArray jsonArray = jsonObject.getJsonArray("body_arg");
    Assert.assertEquals(3, jsonArray.size());
    System.out.println(jsonArray);
  }

  @Test
  public void testRemoveParamter() {
    BodyArgPlugin bodyArgPlugin =
            (BodyArgPlugin) ApiPlugin.create(BodyArgPlugin.class.getSimpleName());
    bodyArgPlugin.add(Parameter.create("macAddress", "FFFFFFFFFFFF")
                              .addRule(Rule.regex("[0-9A-F]{16}")));
    bodyArgPlugin.add(Parameter.create("type", null)
                              .addRule(Rule.required())
                              .addRule(Rule.integer()));
    bodyArgPlugin.add(Parameter.create("barcode", null));

    Assert.assertEquals(3, bodyArgPlugin.parameters().size());
    bodyArgPlugin.remove("type");
    Assert.assertEquals(2, bodyArgPlugin.parameters().size());
  }

  @Test
  public void testClearParamter() {
    BodyArgPlugin bodyArgPlugin =
            (BodyArgPlugin) ApiPlugin.create(BodyArgPlugin.class.getSimpleName());
    bodyArgPlugin.add(Parameter.create("macAddress", "FFFFFFFFFFFF")
                              .addRule(Rule.regex("[0-9A-F]{16}")));
    bodyArgPlugin.add(Parameter.create("type", null)
                              .addRule(Rule.required())
                              .addRule(Rule.integer()));
    bodyArgPlugin.add(Parameter.create("barcode", null));

    Assert.assertEquals(3, bodyArgPlugin.parameters().size());
    bodyArgPlugin.clear();
    Assert.assertEquals(0, bodyArgPlugin.parameters().size());
  }
}
