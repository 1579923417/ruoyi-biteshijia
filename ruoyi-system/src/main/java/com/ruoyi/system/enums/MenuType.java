package com.ruoyi.system.enums;

public enum MenuType {
    HOME_GRID(1, "首页宫格"),
    TAB_BAR(2, "底部TabBar"),
    MINER_TOP(3, "矿工页面菜单"),
    PROFILE_MENU(4, "我的页面菜单"),
    SETTING_MENU(5, "设置页面菜单");

    private final int code;
    private final String desc;

    MenuType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() { return code; }
    public String getDesc() { return desc; }

    public static MenuType fromCode(Integer code) {
        if (code == null) return null;
        for (MenuType t : values()) {
            if (t.code == code) return t;
        }
        return null;
    }
}

