package com.ruoyi.system.enums;

/**
 * 网站配置分组枚举
 */
public enum ConfigGroupEnum {
    APP("app", "APP配置"),
    SMS("sms", "短信配置"),
    PROXY("proxy", "代理配置"),
    AGREEMENT("agreement", "协议"),
    SYSTEM("system", "系统配置"),
    UPLOAD("upload", "上传配置"),
    CUSTOMER_SERVICE("customer_service", "客服配置");

    private final String value;
    private final String label;

    ConfigGroupEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() { return value; }
    public String label() { return label; }
}

