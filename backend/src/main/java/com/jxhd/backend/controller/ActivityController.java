package com.jxhd.backend.controller;

import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.ActivityDTO;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.service.ActivityService;
import com.jxhd.backend.vo.ActivityVO;
import com.jxhd.backend.vo.MySignupVO;
import com.jxhd.backend.vo.SignupVO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /** 查询活动列表 */
    @GetMapping
    public Result<List<ActivityVO>> list(@RequestParam(required = false) Integer status) {
        return Result.success(activityService.list(status));
    }

    /** 新增活动 */
    @PostMapping
    public Result<Void> add(@RequestBody ActivityDTO dto, HttpSession session) {
        Long authorId = getCurrentUserId(session);
        activityService.add(dto, authorId);
        return Result.success();
    }

    /** 修改活动 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ActivityDTO dto) {
        activityService.update(id, dto);
        return Result.success();
    }

    /** 更新活动状态（结束/重新开放/草稿） */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        activityService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /** 删除活动（同时清除报名记录） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success();
    }

    /** 查看某活动的报名列表 */
    @GetMapping("/{id}/signups")
    public Result<List<SignupVO>> signups(@PathVariable Long id) {
        return Result.success(activityService.listSignups(id));
    }

    /** 家长报名 */
    @PostMapping("/{id}/signup")
    public Result<Void> signup(@PathVariable Long id,
                               @RequestBody Map<String, Object> body,
                               HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");
        Long studentId = Long.valueOf(body.get("studentId").toString());
        String remark  = body.getOrDefault("remark", "").toString();
        activityService.signup(id, user.getId(), studentId, remark);
        return Result.success();
    }

    /** 家长取消报名 */
    @DeleteMapping("/signup/{signupId}")
    public Result<Void> cancelSignup(@PathVariable Long signupId, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");
        activityService.cancelSignup(signupId, user.getId());
        return Result.success();
    }

    /** 家长查看我的报名 */
    @GetMapping("/my-signups")
    public Result<List<MySignupVO>> mySignups(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");
        return Result.success(activityService.mySignups(user.getId()));
    }

    private Long getCurrentUserId(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null ? user.getId() : 1L;
    }
}
