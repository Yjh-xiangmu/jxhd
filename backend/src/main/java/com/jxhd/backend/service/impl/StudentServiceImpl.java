package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxhd.backend.dto.ParentBindDTO;
import com.jxhd.backend.dto.StudentDTO;
import com.jxhd.backend.entity.ParentStudent;
import com.jxhd.backend.entity.SysClass;
import com.jxhd.backend.entity.Student;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.ClassMapper;
import com.jxhd.backend.mapper.ParentStudentMapper;
import com.jxhd.backend.mapper.StudentMapper;
import com.jxhd.backend.mapper.UserMapper;
import com.jxhd.backend.service.StudentService;
import com.jxhd.backend.vo.StudentDetailVO;
import com.jxhd.backend.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;
    private final ParentStudentMapper parentStudentMapper;
    private final UserMapper userMapper;

    @Override
    public IPage<StudentVO> page(int pageNum, int pageSize, Long classId, String keyword, Integer status) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        if (classId != null) wrapper.eq(Student::getClassId, classId);
        if (status  != null) wrapper.eq(Student::getStatus, status);
        if (StringUtils.hasText(keyword)) wrapper.like(Student::getName, keyword);
        wrapper.orderByDesc(Student::getCreateTime);

        IPage<Student> rawPage = studentMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<Long> classIds = rawPage.getRecords().stream()
                .filter(s -> s.getClassId() != null)
                .map(Student::getClassId).distinct().collect(Collectors.toList());
        Map<Long, String> classNameMap = classIds.isEmpty() ? Map.of() :
                classMapper.selectBatchIds(classIds).stream()
                        .collect(Collectors.toMap(SysClass::getId, SysClass::getClassName));

        return rawPage.convert(s -> {
            StudentVO vo = new StudentVO();
            vo.setId(s.getId());
            vo.setName(s.getName());
            vo.setGender(s.getGender());
            vo.setGenderLabel(s.getGender() == null ? "-" : s.getGender() == 1 ? "男" : "女");
            vo.setBirthday(s.getBirthday());
            vo.setClassId(s.getClassId());
            vo.setClassName(s.getClassId() != null
                    ? classNameMap.getOrDefault(s.getClassId(), "未知") : "未分班");
            vo.setRemark(s.getRemark());
            vo.setStatus(s.getStatus());
            vo.setCreateTime(s.getCreateTime());
            return vo;
        });
    }

    @Override
    public StudentDetailVO detail(Long studentId) {
        Student s = studentMapper.selectById(studentId);
        if (s == null) throw new RuntimeException("幼儿不存在");

        StudentDetailVO vo = new StudentDetailVO();
        vo.setId(s.getId());
        vo.setName(s.getName());
        vo.setGender(s.getGender());
        vo.setGenderLabel(s.getGender() == null ? "-" : s.getGender() == 1 ? "男" : "女");
        vo.setBirthday(s.getBirthday());
        vo.setClassId(s.getClassId());
        vo.setRemark(s.getRemark());
        vo.setStatus(s.getStatus());
        vo.setCreateTime(s.getCreateTime());

        // 查班级名
        if (s.getClassId() != null) {
            SysClass cls = classMapper.selectById(s.getClassId());
            vo.setClassName(cls != null ? cls.getClassName() : "未知");
        } else {
            vo.setClassName("未分班");
        }

        // 查绑定的家长列表
        List<ParentStudent> binds = parentStudentMapper.selectList(
                new LambdaQueryWrapper<ParentStudent>()
                        .eq(ParentStudent::getStudentId, studentId)
                        .orderByAsc(ParentStudent::getApplyTime));

        if (!binds.isEmpty()) {
            List<Long> parentIds = binds.stream().map(ParentStudent::getParentId).collect(Collectors.toList());
            Map<Long, User> userMap = userMapper.selectBatchIds(parentIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

            List<StudentDetailVO.ParentInfo> parents = binds.stream().map(b -> {
                StudentDetailVO.ParentInfo pi = new StudentDetailVO.ParentInfo();
                pi.setBindId(b.getId());
                pi.setParentId(b.getParentId());
                pi.setRelation(b.getRelation());
                pi.setStatus(b.getStatus());
                pi.setStatusLabel(b.getStatus() == 0 ? "待审核" : b.getStatus() == 1 ? "已通过" : "已拒绝");

                User u = userMap.get(b.getParentId());
                if (u != null) {
                    pi.setRealName(u.getRealName());
                    pi.setUsername(u.getUsername());
                    pi.setPhone(u.getPhone());
                }
                return pi;
            }).collect(Collectors.toList());
            vo.setParents(parents);
        } else {
            vo.setParents(List.of());
        }

        return vo;
    }

    @Override
    public void add(StudentDTO dto) {
        Student s = new Student();
        s.setName(dto.getName());
        s.setGender(dto.getGender());
        s.setBirthday(dto.getBirthday());
        s.setClassId(dto.getClassId());
        s.setRemark(dto.getRemark());
        s.setStatus(1);
        studentMapper.insert(s);
    }

    @Override
    public void update(StudentDTO dto) {
        Student s = studentMapper.selectById(dto.getId());
        if (s == null) throw new RuntimeException("幼儿不存在");
        if (StringUtils.hasText(dto.getName())) s.setName(dto.getName());
        if (dto.getGender()   != null) s.setGender(dto.getGender());
        if (dto.getBirthday() != null) s.setBirthday(dto.getBirthday());
        if (dto.getClassId()  != null) s.setClassId(dto.getClassId());
        if (dto.getStatus()   != null) s.setStatus(dto.getStatus());
        s.setRemark(dto.getRemark());
        studentMapper.updateById(s);
    }

    @Override
    public void delete(Long id) {
        // 先删绑定关系
        parentStudentMapper.delete(
                new LambdaQueryWrapper<ParentStudent>().eq(ParentStudent::getStudentId, id));
        studentMapper.deleteById(id);
    }

    @Override
    public void changeClass(Long studentId, Long newClassId) {
        Student s = studentMapper.selectById(studentId);
        if (s == null) throw new RuntimeException("幼儿不存在");
        s.setClassId(newClassId);
        studentMapper.updateById(s);
    }

    @Override
    public void bindParent(ParentBindDTO dto) {
        // 检查家长是否存在
        User parent = userMapper.selectById(dto.getParentId());
        if (parent == null || !"parent".equals(parent.getRole())) {
            throw new RuntimeException("家长账号不存在");
        }
        // 检查是否已绑定
        Long count = parentStudentMapper.selectCount(
                new LambdaQueryWrapper<ParentStudent>()
                        .eq(ParentStudent::getParentId, dto.getParentId())
                        .eq(ParentStudent::getStudentId, dto.getStudentId()));
        if (count > 0) throw new RuntimeException("该家长已绑定此幼儿");

        ParentStudent ps = new ParentStudent();
        ps.setParentId(dto.getParentId());
        ps.setStudentId(dto.getStudentId());
        ps.setRelation(dto.getRelation());
        ps.setStatus(1); // 管理员手动绑定直接通过
        ps.setApplyTime(LocalDateTime.now());
        ps.setAuditTime(LocalDateTime.now());
        parentStudentMapper.insert(ps);
    }

    @Override
    public void unbindParent(Long bindId) {
        parentStudentMapper.deleteById(bindId);
    }

    @Override
    public void auditBind(Long bindId, Integer status, Long auditorId) {
        ParentStudent ps = parentStudentMapper.selectById(bindId);
        if (ps == null) throw new RuntimeException("绑定记录不存在");
        ps.setStatus(status);
        ps.setAuditTime(LocalDateTime.now());
        ps.setAuditBy(auditorId);
        parentStudentMapper.updateById(ps);
    }
}