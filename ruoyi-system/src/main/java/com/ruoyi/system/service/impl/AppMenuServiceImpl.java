package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AppMenu;
import com.ruoyi.system.mapper.AppMenuMapper;
import com.ruoyi.system.service.IAppMenuService;
import com.ruoyi.system.enums.MenuType;
import com.ruoyi.system.domain.vo.AppMenuVo;
import com.ruoyi.system.domain.vo.AppMenuGroupVo;

@Service
public class AppMenuServiceImpl implements IAppMenuService {
    @Autowired
    private AppMenuMapper appMenuMapper;

    @Override
    public AppMenu selectById(Long id) {
        AppMenu m = appMenuMapper.selectById(id);
        applyDesc(m);
        return m;
    }

    @Override
    public List<AppMenu> selectList(AppMenu query) {
        List<AppMenu> list = appMenuMapper.selectList(query);
        return list.stream().peek(this::applyDesc).collect(Collectors.toList());
    }

    @Override
    public List<AppMenuGroupVo> selectVisibleGrouped() {
        AppMenu query = new AppMenu();
        query.setStatus(1);
        List<AppMenu> all = appMenuMapper.selectList(query);
        all.forEach(this::applyDesc);

        List<AppMenu> roots = all.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() == 0L)
                .sorted(java.util.Comparator.comparing(AppMenu::getSort, java.util.Comparator.nullsLast(Integer::compareTo)))
                .collect(java.util.stream.Collectors.toList());

        java.util.Map<Long, List<AppMenu>> childrenByRoot = all.stream()
                .filter(m -> m.getParentId() != null && m.getParentId() != 0L)
                .collect(java.util.stream.Collectors.groupingBy(AppMenu::getParentId));

        return roots.stream().map(root -> {
            List<AppMenu> children = childrenByRoot.getOrDefault(root.getId(), java.util.Collections.emptyList())
                    .stream()
                    .sorted(java.util.Comparator.comparing(AppMenu::getSort, java.util.Comparator.nullsLast(Integer::compareTo)))
                    .collect(java.util.stream.Collectors.toList());

            AppMenuGroupVo g = new AppMenuGroupVo();
            g.setGroupId(root.getId());
            g.setGroupTitle(root.getTitle());
            g.setMenuType(root.getMenuType());
            MenuType mt = MenuType.fromCode(root.getMenuType());
            g.setMenuTypeDesc(mt != null ? mt.getDesc() : null);

            List<AppMenuVo> items = children.stream().map(m -> {
                AppMenuVo vo = new AppMenuVo();
                vo.setId(m.getId());
                vo.setTitle(m.getTitle());
                vo.setIcon(m.getIcon());
                vo.setType(m.getType());
                vo.setPath(m.getPath());
                vo.setSort(m.getSort());
                return vo;
            }).collect(java.util.stream.Collectors.toList());
            g.setItems(items);
            return g;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public int insert(AppMenu entity) { return appMenuMapper.insert(entity); }

    @Override
    public int update(AppMenu entity) { return appMenuMapper.update(entity); }

    @Override
    public int deleteByIds(Long[] ids) { return appMenuMapper.deleteByIds(ids); }

    @Override
    public int updateStatus(Long id, Integer status) { return appMenuMapper.updateStatus(id, status); }

    private void applyDesc(AppMenu m) {
        if (m == null) return;
        MenuType t = MenuType.fromCode(m.getMenuType());
        if (t != null) m.setMenuTypeDesc(t.getDesc());
    }
}
