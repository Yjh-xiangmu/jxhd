package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.dto.ClassDTO;
import com.jxhd.backend.entity.SysClass;
import com.jxhd.backend.entity.Student;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.ClassMapper;
import com.jxhd.backend.mapper.StudentMapper;
import com.jxhd.backend.mapper.UserMapper;
import com.jxhd.backend.service.ClassService;
import com.jxhd.backend.vo.ClassVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassMapper classMapper;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;

    @Override
    public List<ClassVO> listAll(String keyword, Integer status) {
        // 1. 查班级列表
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysClass::getClassName, keyword);
        }
        if (status != null) {
            wrapper.eq(SysClass::getStatus, status);
        }
        wrapper.orderByDesc(SysClass::getCreateTime);
        List<SysClass> classes = classMapper.selectList(wrapper);

        if (classes.isEmpty()) return List.of();

        // 2. 批量查班主任信息
        List<Long> teacherIds = classes.stream()
                .filter(c -> c.getHeadTeacherId() != null)
                .map(SysClass::getHeadTeacherId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> teacherMap = teacherIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(teacherIds).stream()
                        .collect(Collectors.toMap(User::getId,
                                u -> StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername()));

        // 3. 批量统计各班在园学生数
        List<Long> classIds = classes.stream().map(SysClass::getId).collect(Collectors.toList());
        Map<Long, Long> countMap = studentMapper.selectList(
                new LambdaQueryWrapper<Student>()
                        .in(Student::getClassId, classIds)
                        .eq(Student::getStatus, 1)
        ).stream().collect(Collectors.groupingBy(Student::getClassId, Collectors.counting()));

        // 4. 组装 VO
        return classes.stream().map(c -> {
            ClassVO vo = new ClassVO();
            vo.setId(c.getId());
            vo.setClassName(c.getClassName());
            vo.setGrade(c.getGrade());
            vo.setHeadTeacherId(c.getHeadTeacherId());
            vo.setHeadTeacherName(c.getHeadTeacherId() != null
                    ? teacherMap.getOrDefault(c.getHeadTeacherId(), "未知") : null);
            vo.setSemester(c.getSemester());
            vo.setStatus(c.getStatus());
            vo.setStudentCount(countMap.getOrDefault(c.getId(), 0L).intValue());
            vo.setCreateTime(c.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void add(ClassDTO dto) {
        // 班级名不能重复
        LambdaQueryWrapper<SysClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysClass::getClassName, dto.getClassName());
        if (classMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("班级名称已存在");
        }
        SysClass c = new SysClass();
        c.setClassName(dto.getClassName());
        c.setGrade(dto.getGrade());
        c.setHeadTeacherId(dto.getHeadTeacherId());
        c.setSemester(dto.getSemester());
        c.setStatus(1);
        classMapper.insert(c);
    }

    @Override
    public void update(ClassDTO dto) {
        SysClass c = classMapper.selectById(dto.getId());
        if (c == null) throw new RuntimeException("班级不存在");
        if (StringUtils.hasText(dto.getClassName())) c.setClassName(dto.getClassName());
        if (StringUtils.hasText(dto.getGrade()))     c.setGrade(dto.getGrade());
        if (StringUtils.hasText(dto.getSemester()))  c.setSemester(dto.getSemester());
        c.setHeadTeacherId(dto.getHeadTeacherId());  // 允许置空（移除班主任）
        classMapper.updateById(c);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysClass c = classMapper.selectById(id);
        if (c == null) throw new RuntimeException("班级不存在");
        c.setStatus(status);
        classMapper.updateById(c);
    }

    @Override
    public void delete(Long id) {
        // 班级下有幼儿时不能删
        Long count = studentMapper.selectCount(
                new LambdaQueryWrapper<Student>().eq(Student::getClassId, id));
        if (count > 0) {
            throw new RuntimeException("班级下还有 " + count + " 名幼儿，请先转移或移除幼儿后再删除");
        }
        classMapper.deleteById(id);
    }

    @Override
    public List<User> listTeachers() {
        return userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, "teacher")
                        .eq(User::getStatus, 1)
                        .orderByAsc(User::getRealName));
    }
}