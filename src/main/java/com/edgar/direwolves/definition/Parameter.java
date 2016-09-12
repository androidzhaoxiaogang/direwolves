package com.edgar.direwolves.definition;

import com.edgar.util.validation.Rule;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Parameter是一个键值对，其中键是参数名，值对应着参数对配置属性
 * <ul>
 * <li>name 参数的名称，必填项</li>
 * <li>default 默认值，如果参数没有定义默认值，那么默认值用null</li>
 * <li>rule 校验规则，由下列校验规则 required，prohibited，optional，min, max, minLength, maxLength, regex, email, integer, equals</li>
 * </ul>
 *
 * @author Edgar  Date 2016/9/8
 */
public class Parameter {

    private final String name;

    private final Object defaultValue;

    private final List<Rule> rules = new ArrayList<>();

    public Parameter(String name, Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Parameter addRule(Rule rule) {
        Preconditions.checkNotNull(rule, "rule cannot be null");
        rules.add(rule);
        return this;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public List<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Parameter")
                .add("name", name)
                .add("defaultValue", defaultValue)
                .add("rules", rules)
                .toString();
    }
}
