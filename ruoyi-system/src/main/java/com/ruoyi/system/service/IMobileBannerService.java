package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.MobileBanner;

public interface IMobileBannerService {
    MobileBanner selectById(Long id);
    List<MobileBanner> selectList(MobileBanner query);
    int insert(MobileBanner entity);
    int update(MobileBanner entity);
    int deleteByIds(Long[] ids);
    int updateStatus(Long id, Integer status);
}
