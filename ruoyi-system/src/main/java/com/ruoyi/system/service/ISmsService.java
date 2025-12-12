package com.ruoyi.system.service;

public interface ISmsService {
    boolean sendLoginCode(String phone, String code);
}

