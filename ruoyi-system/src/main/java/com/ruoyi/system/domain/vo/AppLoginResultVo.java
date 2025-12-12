package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppLoginResultVo {
    private String token;
    private Integer expire;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Integer getExpire() { return expire; }
    public void setExpire(Integer expire) { this.expire = expire; }
}
