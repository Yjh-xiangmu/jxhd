package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.NewsDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.NewsService;
import com.jxhd.backend.vo.NewsVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /** 分页查询资讯列表 */
    @GetMapping
    public Result<IPage<NewsVO>> list(
            @RequestParam(defaultValue = "1")  int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(newsService.page(pageNum, pageSize, type, keyword, status));
    }

    /** 新增资讯 */
    @PostMapping
    public Result<Void> add(@RequestBody NewsDTO dto, HttpSession session) {
        Long authorId = getCurrentUserId(session);
        newsService.add(dto, authorId);
        return Result.success();
    }

    /** 修改资讯 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody NewsDTO dto) {
        newsService.update(id, dto);
        return Result.success();
    }

    /** 发布 / 设为草稿 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        newsService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /** 删除资讯 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return Result.success();
    }

    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null ? user.getId() : 1L;
    }
}
