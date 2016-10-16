package com.edgar.direwolves.definition;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.vertx.core.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP的远程调用定义.
 *
 * @author Edgar  Date 2016/9/12
 */
class HttpEndpointImpl implements HttpEndpoint {

    /**
     * 服务名
     */
    private final String name;

    /**
     * 请求方法 GET | POST | DELETE | PUT
     */
    private final HttpMethod method;

    /**
     * 远程rest路径
     * 示例：/tasks
     * 示例：/tasks/$1/abandon，$1表示当前请求上下文中的$1变量
     */
    private final String path;

//    /**
//     * 描述
//     */
//    private final String description;

    /**
     * 远程服务名，可以通过服务发现机制查找到对应的服务地址
     */
    private final String service;

    private final List<String> reqHeadersRemove = new ArrayList<>();

    private final List<Map.Entry<String, String>> reqHeadersAdd = new ArrayList<>();

    private final List<Map.Entry<String, String>> reqHeadersReplace = new ArrayList<>();

    private final List<String> urlArgsRemove = new ArrayList<>();

    private final List<Map.Entry<String, String>> urlArgsAdd = new ArrayList<>();

    private final List<Map.Entry<String, String>> urlArgsReplace = new ArrayList<>();

    private final List<String> bodyArgsRemove = new ArrayList<>();

    private final List<Map.Entry<String, String>> bodyArgsAdd = new ArrayList<>();

    private final List<Map.Entry<String, String>> bodyArgsReplace = new ArrayList<>();

    private final List<String> respHeadersRemove = new ArrayList<>();

    private final List<Map.Entry<String, String>> respHeadersAdd = new ArrayList<>();

    private final List<Map.Entry<String, String>> respHeadersReplace = new ArrayList<>();

    private final List<String> bodyRemove = new ArrayList<>();

    private final List<Map.Entry<String, String>> bodyAdd = new ArrayList<>();

    private final List<Map.Entry<String, String>> bodyReplace = new ArrayList<>();

    HttpEndpointImpl(String name, HttpMethod method, String path, String service) {
        Preconditions.checkNotNull(name, "name can not be null");
        Preconditions.checkNotNull(method, "method can not be null");
        Preconditions.checkNotNull(path, "path can not be null");
        Preconditions.checkNotNull(service, "service can not be null");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        this.name = name;
        this.method = method;
        this.path = path;
        this.service = service;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String service() {
        return service;
    }

    @Override
    public HttpEndpoint addReqHeader(String key, String value) {
        reqHeadersAdd.add(Maps.immutableEntry(key, value));
        return this;
    }

    @Override
    public HttpEndpoint replaceReqHeader(String key, String value) {
        reqHeadersReplace.add(Maps.immutableEntry(key, value));
        return this;
    }

    @Override
    public HttpEndpoint removeReqHeader(String key) {
        reqHeadersRemove.add(key);
        return this;
    }

    @Override
    public HttpEndpoint addReqUrlArg(String key, String value) {
        urlArgsAdd.add(Maps.immutableEntry(key, value));
        return this;
    }

    @Override
    public HttpEndpoint replaceReqUrlArg(String key, String value) {
        urlArgsReplace.add(Maps.immutableEntry(key, value));
        return this;
    }

    @Override
    public HttpEndpoint removeReqUrlArg(String key) {
        urlArgsRemove.add(key);
        return this;
    }

    @Override
    public HttpEndpoint addReqBodyArg(String key, String value) {
        bodyArgsAdd.add(Maps.immutableEntry(key, value));
        return this;
    }

    @Override
    public HttpEndpoint replaceReqBodyArg(String key, String value) {
        bodyArgsReplace.add(Maps.immutableEntry(key, value));
        return this;
    }

    @Override
    public HttpEndpoint removeReqBodyArg(String key) {
        bodyArgsRemove.add(key);
        return this;
    }

    @Override
    public List<Map.Entry<String, String>> reqBodyArgsReplace() {
        return bodyArgsReplace;
    }

    @Override
    public List<Map.Entry<String, String>> reqBodyArgsAdd() {
        return bodyArgsAdd;
    }

    @Override
    public List<String> reqBodyArgsRemove() {
        return bodyArgsRemove;
    }

    @Override
    public List<Map.Entry<String, String>> reqUrlArgsReplace() {
        return urlArgsReplace;
    }

    @Override
    public List<Map.Entry<String, String>> reqUrlArgsAdd() {
        return urlArgsAdd;
    }

    @Override
    public List<String> reqUrlArgsRemove() {
        return urlArgsRemove;
    }

    @Override
    public List<Map.Entry<String, String>> reqHeadersReplace() {
        return reqHeadersReplace;
    }

    @Override
    public List<Map.Entry<String, String>> reqHeadersAdd() {
        return reqHeadersAdd;
    }

    @Override
    public List<String> reqHeadersRemove() {
        return reqHeadersRemove;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("HttpEndpoint")
                .add("name", name)
                .add("service", service)
                .add("path", path)
                .add("method", method)
                .add("reqHeadersRemove", reqHeadersRemove)
                .add("reqHeadersReplace", reqHeadersReplace)
                .add("reqHeadersAdd", reqHeadersAdd)
                .add("reqUrlArgsRemove", urlArgsRemove)
                .add("reqUrlArgsReplace", urlArgsReplace)
                .add("reqUrlArgsAdd", urlArgsAdd)
                .add("reqBodyArgsRemove", bodyArgsRemove)
                .add("reqBodyArgsReplace", bodyArgsReplace)
                .add("reqBodyArgsAdd", bodyArgsAdd)
                .toString();
    }

//    @Override
//    public List<Map.Entry<String, String>> respBodyReplace() {
//        return bodyReplace;
//    }
//
//    @Override
//    public List<Map.Entry<String, String>> respBodyAdd() {
//        return bodyAdd;
//    }
//
//    @Override
//    public List<String> respBodyRemove() {
//        return bodyRemove;
//    }
//
//    @Override
//    public List<Map.Entry<String, String>> respHeadersReplace() {
//        return respHeadersReplace;
//    }
//
//    @Override
//    public List<Map.Entry<String, String>> respHeadersAdd() {
//        return respHeadersAdd;
//    }
//
//    @Override
//    public List<String> respHeadersRemove() {
//        return respHeadersRemove;
//    }
//
//    @Override
//    public HttpEndpoint addRespHeader(String key, String value) {
//        respHeadersAdd.add(Maps.immutableEntry(key, value));
//        return this;
//    }
//
//    @Override
//    public HttpEndpoint replaceRespHeader(String key, String value) {
//        respHeadersReplace.add(Maps.immutableEntry(key, value));
//        return this;
//    }
//
//    @Override
//    public HttpEndpoint removeRespHeader(String key) {
//        respHeadersRemove.add(key);
//        return this;
//    }
//
//    @Override
//    public HttpEndpoint addRespBody(String key, String value) {
//        bodyAdd.add(Maps.immutableEntry(key, value));
//        return this;
//    }
//
//    @Override
//    public HttpEndpoint replaceRespBody(String key, String value) {
//        bodyReplace.add(Maps.immutableEntry(key, value));
//        return this;
//    }
//
//    @Override
//    public HttpEndpoint removeRespBody(String key) {
//        bodyRemove.add(key);
//        return this;
//    }


}
