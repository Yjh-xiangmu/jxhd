package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.dto.ScheduleDTO;
import com.jxhd.backend.entity.ClassSchedule;
import com.jxhd.backend.entity.SysClass;
import com.jxhd.backend.entity.TeacherClass;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.ClassMapper;
import com.jxhd.backend.mapper.ClassScheduleMapper;
import com.jxhd.backend.mapper.TeacherClassMapper;
import com.jxhd.backend.mapper.UserMapper;
import com.jxhd.backend.service.ScheduleService;
import com.jxhd.backend.vo.ClassVO;
import com.jxhd.backend.vo.ScheduleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ClassScheduleMapper scheduleMapper;
    private final ClassMapper classMapper;
    private final UserMapper userMapper;
    private final TeacherClassMapper teacherClassMapper;

    @Override
    public List<ScheduleVO> getByClass(Long classId) {
        List<ClassSchedule> list = scheduleMapper.selectList(
                new LambdaQueryWrapper<ClassSchedule>()
                        .eq(ClassSchedule::getClassId, classId)
                        .orderByAsc(ClassSchedule::getDayOfWeek)
                        .orderByAsc(ClassSchedule::getPeriodNo));
        if (list.isEmpty()) return List.of();

        // 加载任课教师信息
        List<Long> tIds = list.stream().filter(s -> s.getTeacherId() != null)
                .map(ClassSchedule::getTeacherId).distinct().collect(Collectors.toList());
        Map<Long, String> teacherMap = tIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(tIds).stream()
                        .collect(Collectors.toMap(User::getId,
                                u -> StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername()));

        // 加载班级名（单次查询）
        SysClass cls = classMapper.selectById(classId);
        String className = cls != null ? cls.getClassName() : "-";

        return list.stream().map(s -> {
            ScheduleVO vo = new ScheduleVO();
            vo.setId(s.getId());
            vo.setClassId(s.getClassId());
            vo.setClassName(className);
            vo.setDayOfWeek(s.getDayOfWeek());
            vo.setPeriodNo(s.getPeriodNo());
            vo.setSubject(s.getSubject());
            vo.setTeacherId(s.getTeacherId());
            vo.setTeacherName(s.getTeacherId() != null ? teacherMap.getOrDefault(s.getTeacherId(), "未知") : null);
            vo.setStartTime(s.getStartTime());
            vo.setEndTime(s.getEndTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void saveEntry(ScheduleDTO dto) {
        if (dto.getId() != null) {
            // 更新
            ClassSchedule s = scheduleMapper.selectById(dto.getId());
            if (s == null) throw new RuntimeException("课表条目不存在");
            s.setSubject(dto.getSubject());
            s.setTeacherId(dto.getTeacherId());
            s.setStartTime(dto.getStartTime());
            s.setEndTime(dto.getEndTime());
            scheduleMapper.updateById(s);
        } else {
            // 新增（若已存在相同 classId+day+period 则覆盖）
            LambdaQueryWrapper<ClassSchedule> w = new LambdaQueryWrapper<ClassSchedule>()
                    .eq(ClassSchedule::getClassId, dto.getClassId())
                    .eq(ClassSchedule::getDayOfWeek, dto.getDayOfWeek())
                    .eq(ClassSchedule::getPeriodNo, dto.getPeriodNo());
            ClassSchedule existing = scheduleMapper.selectOne(w);
            if (existing != null) {
                existing.setSubject(dto.getSubject());
                existing.setTeacherId(dto.getTeacherId());
                existing.setStartTime(dto.getStartTime());
                existing.setEndTime(dto.getEndTime());
                scheduleMapper.updateById(existing);
            } else {
                ClassSchedule s = new ClassSchedule();
                s.setClassId(dto.getClassId());
                s.setDayOfWeek(dto.getDayOfWeek());
                s.setPeriodNo(dto.getPeriodNo());
                s.setSubject(dto.getSubject());
                s.setTeacherId(dto.getTeacherId());
                s.setStartTime(dto.getStartTime());
                s.setEndTime(dto.getEndTime());
                scheduleMapper.insert(s);
            }
        }
    }

    @Override
    public void deleteEntry(Long id) {
        scheduleMapper.deleteById(id);
    }

    @Override
    public ClassVO getTeacherClass(Long teacherId) {
        // 优先取 is_head=1 的班级，否则取第一个
        List<TeacherClass> tcList = teacherClassMapper.selectList(
                new LambdaQueryWrapper<TeacherClass>()
                        .eq(TeacherClass::getTeacherId, teacherId)
                        .orderByDesc(TeacherClass::getIsHead));
        if (tcList.isEmpty()) return null;

        Long classId = tcList.get(0).getClassId();
        SysClass cls = classMapper.selectById(classId);
        if (cls == null) return null;

        ClassVO vo = new ClassVO();
        vo.setId(cls.getId());
        vo.setClassName(cls.getClassName());
        vo.setGrade(cls.getGrade());
        vo.setSemester(cls.getSemester());
        vo.setStatus(cls.getStatus());
        vo.setHeadTeacherId(cls.getHeadTeacherId());
        return vo;
    }
}
