package com.ruoyi.system.enums;

public enum NoticeType {
    NOTICE("1", "通知"),
    ANNOUNCEMENT("2", "公告"),
    APP("3", "APP通知");

    private final String code;
    private final String description;

    NoticeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static NoticeType fromCode(String code) {
        for (NoticeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown NoticeType code: " + code);
    }
}

