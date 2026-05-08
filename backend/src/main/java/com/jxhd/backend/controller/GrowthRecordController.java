package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.dto.GrowthRecordDTO;
import com.jxhd.backend.entity.ParentStudent;
import com.jxhd.backend.entity.TeacherClass;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.ParentStudentMapper;
import com.jxhd.backend.mapper.TeacherClassMapper;
import com.jxhd.backend.service.GrowthRecordService;
import com.jxhd.backend.service.LogService;
import com.jxhd.backend.vo.GrowthRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/growth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class GrowthRecordController {

    private final GrowthRecordService growthRecordService;
    private final TeacherClassMapper teacherClassMapper;
    private final ParentStudentMapper parentStudentMapper;
    private final LogService logService;

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    // ── 图片上传 ──────────────────────────────────────────────────────────────

    @PostMapping("/upload-image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                      HttpSession session) {
        if (session.getAttribute("currentUser") == null) return Result.error(401, "未登录");
        if (file.isEmpty()) return Result.error(400, "文件为空");

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.')) : ".jpg";
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        String absDir = Paths.get(System.getProperty("user.dir"), uploadPath)
                .toAbsolutePath().toString();
        File dir = new File(absDir);
        if (!dir.exists()) dir.mkdirs();

        try {
            file.transferTo(new File(dir, filename));
        } catch (IOException e) {
            return Result.error(500, "文件保存失败: " + e.getMessage());
        }
        return Result.success("/uploads/" + filename);
    }

    // ── 教师：按班级分页查动态 ─────────────────────────────────────────────────

    @GetMapping
    public Result<IPage<GrowthRecordVO>> page(@RequestParam(defaultValue = "1")  int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam(required = false)    Long classId,
                                              HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");

        Long effectiveClassId = classId;
        if (effectiveClassId == null && "teacher".equals(user.getRole())) {
            effectiveClassId = getTeacherClassId(user.getId());
        }
        return Result.success(growthRecordService.pageByClass(pageNum, pageSize, effectiveClassId));
    }

    // ── 家长：查自己孩子的动态 ────────────────────────────────────────────────

    @GetMapping("/my-child/{studentId}")
    public Result<List<GrowthRecordVO>> myChildRecords(@PathVariable Long studentId,
                                                        HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");

        // 验证家长确实绑定了这个孩子（已通过）
        if ("parent".equals(user.getRole())) {
            long cnt = parentStudentMapper.selectCount(
                    new LambdaQueryWrapper<ParentStudent>()
                            .eq(ParentStudent::getParentId, user.getId())
                            .eq(ParentStudent::getStudentId, studentId)
                            .eq(ParentStudent::getStatus, 1));
            if (cnt == 0) return Result.error(403, "无权查看");
        }
        return Result.success(growthRecordService.listByStudent(studentId));
    }

    // ── 家长：查所有已绑定孩子的动态（合并） ─────────────────────────────────

    @GetMapping("/my-children")
    public Result<List<GrowthRecordVO>> myChildrenRecords(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return Result.error(401, "未登录");

        List<Long> studentIds = parentStudentMapper.selectList(
                        new LambdaQueryWrapper<ParentStudent>()
                                .eq(ParentStudent::getParentId, user.getId())
                                .eq(ParentStudent::getStatus, 1))
                .stream().map(ParentStudent::getStudentId).collect(Collectors.toList());

        if (studentIds.isEmpty()) return Result.success(List.of());

        List<com.jxhd.backend.vo.GrowthRecordVO> all = studentIds.stream()
                .flatMap(sid -> growthRecordService.listByStudent(sid).stream())
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());

        return Result.success(all);
    }

    // ── 教师：发布动态 ────────────────────────────────────────────────────────

    @PostMapping
    public Result<Void> add(@RequestBody GrowthRecordDTO dto, HttpSession session, HttpServletRequest request) {
        User teacher = (User) session.getAttribute("currentUser");
        if (teacher == null) return Result.error(401, "未登录");
        if (!"teacher".equals(teacher.getRole())) return Result.error(403, "仅教师可发布");

        Long classId = getTeacherClassId(teacher.getId());
        if (classId == null) return Result.error(400, "教师未分配班级");

        if (!StringUtils.hasText(dto.getContent())) return Result.error(400, "内容不能为空");
        if (dto.getStudentId() == null) return Result.error(400, "请选择幼儿");

        growthRecordService.add(dto, teacher.getId(), classId);
        logService.record(teacher, "成长记录", "发布动态", "幼儿ID：" + dto.getStudentId(), request);
        return Result.success(null);
    }

    // ── 教师：删除动态 ────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        User teacher = (User) session.getAttribute("currentUser");
        if (teacher == null) return Result.error(401, "未登录");
        growthRecordService.delete(id, teacher.getId());
        logService.record(teacher, "成长记录", "删除动态", "记录ID：" + id, request);
        return Result.success(null);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Long getTeacherClassId(Long teacherId) {
        List<TeacherClass> list = teacherClassMapper.selectList(
                new LambdaQueryWrapper<TeacherClass>()
                        .eq(TeacherClass::getTeacherId, teacherId)
                        .orderByDesc(TeacherClass::getIsHead));
        return list.isEmpty() ? null : list.get(0).getClassId();
    }
}
