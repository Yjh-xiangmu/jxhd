package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxhd.backend.dto.GrowthRecordDTO;
import com.jxhd.backend.entity.GrowthRecord;
import com.jxhd.backend.entity.SysClass;
import com.jxhd.backend.entity.Student;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.ClassMapper;
import com.jxhd.backend.mapper.GrowthRecordMapper;
import com.jxhd.backend.mapper.StudentMapper;
import com.jxhd.backend.mapper.UserMapper;
import com.jxhd.backend.service.GrowthRecordService;
import com.jxhd.backend.vo.GrowthRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrowthRecordServiceImpl implements GrowthRecordService {

    private final GrowthRecordMapper growthRecordMapper;
    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<GrowthRecordVO> pageByClass(int pageNum, int pageSize, Long classId) {
        LambdaQueryWrapper<GrowthRecord> wrapper = new LambdaQueryWrapper<GrowthRecord>()
                .eq(classId != null, GrowthRecord::getClassId, classId)
                .orderByDesc(GrowthRecord::getCreateTime);

        IPage<GrowthRecord> rawPage = growthRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return rawPage.convert(r -> toVO(r, buildContext(rawPage.getRecords())));
    }

    @Override
    public List<GrowthRecordVO> listByStudent(Long studentId) {
        List<GrowthRecord> records = growthRecordMapper.selectList(
                new LambdaQueryWrapper<GrowthRecord>()
                        .eq(GrowthRecord::getStudentId, studentId)
                        .orderByDesc(GrowthRecord::getCreateTime));
        if (records.isEmpty()) return List.of();
        Context ctx = buildContext(records);
        return records.stream().map(r -> toVO(r, ctx)).collect(Collectors.toList());
    }

    @Override
    public void add(GrowthRecordDTO dto, Long authorId, Long classId) {
        GrowthRecord r = new GrowthRecord();
        r.setStudentId(dto.getStudentId());
        r.setClassId(classId);
        r.setAuthorId(authorId);
        r.setContent(dto.getContent());
        r.setImages(dto.getImages() != null ? dto.getImages() : List.of());
        r.setCreateTime(LocalDateTime.now());
        growthRecordMapper.insert(r);
    }

    @Override
    public void delete(Long id, Long operatorId) {
        GrowthRecord r = growthRecordMapper.selectById(id);
        if (r == null) throw new RuntimeException("记录不存在");
        if (!r.getAuthorId().equals(operatorId)) throw new RuntimeException("无权删除他人发布的动态");
        growthRecordMapper.deleteById(id);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private record Context(Map<Long, Student> students, Map<Long, SysClass> classes, Map<Long, User> users) {}

    private Context buildContext(List<GrowthRecord> records) {
        List<Long> studentIds = records.stream().map(GrowthRecord::getStudentId).distinct().collect(Collectors.toList());
        List<Long> classIds   = records.stream().map(GrowthRecord::getClassId).distinct().collect(Collectors.toList());
        List<Long> authorIds  = records.stream().map(GrowthRecord::getAuthorId).distinct().collect(Collectors.toList());

        Map<Long, Student>  students = studentIds.isEmpty() ? Map.of() : studentMapper.selectBatchIds(studentIds).stream().collect(Collectors.toMap(Student::getId, s -> s));
        Map<Long, SysClass> classes  = classIds.isEmpty()   ? Map.of() : classMapper.selectBatchIds(classIds).stream().collect(Collectors.toMap(SysClass::getId, c -> c));
        Map<Long, User>     users    = authorIds.isEmpty()   ? Map.of() : userMapper.selectBatchIds(authorIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        return new Context(students, classes, users);
    }

    private GrowthRecordVO toVO(GrowthRecord r, Context ctx) {
        GrowthRecordVO vo = new GrowthRecordVO();
        vo.setId(r.getId());
        vo.setStudentId(r.getStudentId());
        vo.setClassId(r.getClassId());
        vo.setAuthorId(r.getAuthorId());
        vo.setContent(r.getContent());
        vo.setImages(r.getImages() != null ? r.getImages() : List.of());
        vo.setCreateTime(r.getCreateTime());

        Student s = ctx.students().get(r.getStudentId());
        vo.setStudentName(s != null ? s.getName() : "-");

        SysClass c = ctx.classes().get(r.getClassId());
        vo.setClassName(c != null ? c.getClassName() : "-");

        User u = ctx.users().get(r.getAuthorId());
        if (u != null) vo.setAuthorName(org.springframework.util.StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername());

        return vo;
    }
}
