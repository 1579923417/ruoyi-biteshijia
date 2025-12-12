package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.system.domain.vo.SysNoticeAppVo;
import com.ruoyi.system.domain.vo.SysNoticeSimpleVo;
import com.ruoyi.system.enums.NoticeType;

/**
 * 公告 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 查询app公告列表
     * @return 公告集合
     */
    @Override
    public List<SysNoticeAppVo> selectAppVisibleList()
    {
        List<SysNotice> list = noticeMapper.selectAppVisibleList();
        return list.stream().map(n -> {
            SysNoticeAppVo vo = new SysNoticeAppVo();
            vo.setId(n.getNoticeId());
            vo.setTitle(n.getNoticeTitle());
            NoticeType t;
            try { t = NoticeType.fromCode(n.getNoticeType()); } catch (Exception e) { t = null; }
            vo.setTypeDesc(t != null ? t.getDescription() : n.getNoticeType());
            vo.setContent(n.getNoticeContent());
            vo.setCreateTime(n.getCreateTime());
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<SysNoticeSimpleVo> selectAppSimpleList()
    {
        List<SysNotice> list = noticeMapper.selectAppVisibleList();
        return list.stream().map(n -> {
            SysNoticeSimpleVo vo = new SysNoticeSimpleVo();
            vo.setId(n.getNoticeId());
            vo.setTitle(n.getNoticeTitle());
            vo.setCreateTime(n.getCreateTime());
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public SysNoticeAppVo selectAppNoticeDetail(Long id)
    {
        SysNotice n = noticeMapper.selectNoticeById(id);
        if (n == null) {
            return null;
        }
        SysNoticeAppVo vo = new SysNoticeAppVo();
        vo.setId(n.getNoticeId());
        vo.setTitle(n.getNoticeTitle());
        NoticeType t;
        try { t = NoticeType.fromCode(n.getNoticeType()); } catch (Exception e) { t = null; }
        vo.setTypeDesc(t != null ? t.getDescription() : n.getNoticeType());
        vo.setContent(n.getNoticeContent());
        vo.setCreateTime(n.getCreateTime());
        return vo;
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }
}
