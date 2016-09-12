package com.edgar.direwolves.definition;

import com.google.common.collect.Lists;
import io.vertx.core.http.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar on 2016/9/8.
 *
 * @author Edgar  Date 2016/9/8
 */
public class ApiDefinitionTest {

    @Test
    public void testBuild() {

        HttpEndpoint httpEndpoint = HttpEndpoint.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setService("device")
                .setArray(true).build();

        ApiDefinition apiDefinition = ApiDefinition.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setEndpoints(Lists.newArrayList(httpEndpoint))
                .build();
        Assert.assertEquals("/devices", apiDefinition.getPath());
        Assert.assertEquals("default", apiDefinition.getScope());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEndpointsShouldNotEmpty() {

        ApiDefinition apiDefinition = ApiDefinition.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setEndpoints(Lists.newArrayList())
                .build();
        Assert.fail();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEndpointsShouldImmutable() {
        HttpEndpoint httpEndpoint = HttpEndpoint.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setService("device")
                .setArray(true).build();
        ApiDefinition apiDefinition = ApiDefinition.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setEndpoints(Lists.newArrayList(httpEndpoint))
                .build();

        httpEndpoint = HttpEndpoint.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setService("device")
                .build();
        apiDefinition.getEndpoints().add(httpEndpoint);

        Assert.fail();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUrlArgsShouldImmutable() {
        HttpEndpoint httpEndpoint = HttpEndpoint.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setService("device")
                .setArray(true).build();
        ApiDefinition apiDefinition = ApiDefinition.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setEndpoints(Lists.newArrayList(httpEndpoint))
                .setUrlArgs(Lists.newArrayList(new Parameter("username", null)))
                .build();

        apiDefinition.getUrlArgs().add(new Parameter("password", null));
        Assert.fail();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBodyArgsShouldImmutable() {
        HttpEndpoint httpEndpoint = HttpEndpoint.builder().setName("get_device")
                .setMethod(HttpMethod.GET)
                .setPath("devices/")
                .setService("device")
                .setArray(true).build();
        ApiDefinition apiDefinition = ApiDefinition.builder().setName("get_device")
                .setMethod(HttpMethod.POST)
                .setPath("devices/")
                .setEndpoints(Lists.newArrayList(httpEndpoint))
                .setBodyArgs(Lists.newArrayList(new Parameter("username", null)))
                .build();

        apiDefinition.getBodyArgs().add(new Parameter("password", null));
        Assert.fail();
    }
}
