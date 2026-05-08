package com.jxhd.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.dto.NewsDTO;
import com.jxhd.backend.vo.NewsVO;

public interface NewsService {
    IPage<NewsVO> page(int pageNum, int pageSize, String type, String keyword, Integer status);
    void add(NewsDTO dto, Long authorId);
    void update(Long id, NewsDTO dto);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
}
