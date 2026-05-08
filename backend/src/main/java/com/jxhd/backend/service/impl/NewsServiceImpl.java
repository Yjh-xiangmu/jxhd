package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxhd.backend.dto.NewsDTO;
import com.jxhd.backend.entity.CampusNews;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.CampusNewsMapper;
import com.jxhd.backend.mapper.UserMapper;
import com.jxhd.backend.service.NewsService;
import com.jxhd.backend.vo.NewsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final CampusNewsMapper newsMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<NewsVO> page(int pageNum, int pageSize, String type, String keyword, Integer status) {
        LambdaQueryWrapper<CampusNews> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) wrapper.eq(CampusNews::getType, type);
        if (status != null) wrapper.eq(CampusNews::getStatus, status);
        if (StringUtils.hasText(keyword)) wrapper.like(CampusNews::getTitle, keyword);
        wrapper.orderByDesc(CampusNews::getCreateTime);

        IPage<CampusNews> rawPage = newsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 批量加载作者信息
        List<Long> authorIds = rawPage.getRecords().stream()
                .map(CampusNews::getAuthorId).distinct().collect(Collectors.toList());
        Map<Long, String> authorMap = authorIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(authorIds).stream()
                        .collect(Collectors.toMap(User::getId,
                                u -> StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername()));

        IPage<NewsVO> voPage = rawPage.convert(n -> {
            NewsVO vo = new NewsVO();
            vo.setId(n.getId());
            vo.setTitle(n.getTitle());
            vo.setType(n.getType());
            vo.setContent(n.getContent());
            vo.setStatus(n.getStatus());
            vo.setAuthorId(n.getAuthorId());
            vo.setAuthorName(authorMap.getOrDefault(n.getAuthorId(), "未知"));
            vo.setViewCount(n.getViewCount());
            vo.setCreateTime(n.getCreateTime());
            vo.setUpdateTime(n.getUpdateTime());
            return vo;
        });
        return voPage;
    }

    @Override
    public void add(NewsDTO dto, Long authorId) {
        CampusNews news = new CampusNews();
        news.setTitle(dto.getTitle());
        news.setType(StringUtils.hasText(dto.getType()) ? dto.getType() : "announcement");
        news.setContent(dto.getContent());
        news.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        news.setAuthorId(authorId);
        news.setViewCount(0);
        newsMapper.insert(news);
    }

    @Override
    public void update(Long id, NewsDTO dto) {
        CampusNews news = newsMapper.selectById(id);
        if (news == null) throw new RuntimeException("资讯不存在");
        if (StringUtils.hasText(dto.getTitle())) news.setTitle(dto.getTitle());
        if (StringUtils.hasText(dto.getType()))  news.setType(dto.getType());
        if (dto.getContent() != null)            news.setContent(dto.getContent());
        if (dto.getStatus() != null)             news.setStatus(dto.getStatus());
        newsMapper.updateById(news);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        CampusNews news = newsMapper.selectById(id);
        if (news == null) throw new RuntimeException("资讯不存在");
        news.setStatus(status);
        newsMapper.updateById(news);
    }

    @Override
    public void delete(Long id) {
        newsMapper.deleteById(id);
    }
}
