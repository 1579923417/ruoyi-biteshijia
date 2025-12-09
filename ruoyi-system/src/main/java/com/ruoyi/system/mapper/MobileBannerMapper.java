package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MobileBanner;
import org.apache.ibatis.annotations.Param;

public interface MobileBannerMapper {
    MobileBanner selectById(Long id);
    List<MobileBanner> selectList(MobileBanner query);
    int insert(MobileBanner entity);
    int update(MobileBanner entity);
    int deleteByIds(Long[] ids);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
