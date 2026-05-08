package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.dto.ActivityDTO;
import com.jxhd.backend.entity.*;
import com.jxhd.backend.mapper.*;
import com.jxhd.backend.service.ActivityService;
import com.jxhd.backend.vo.ActivityVO;
import com.jxhd.backend.vo.MySignupVO;
import com.jxhd.backend.vo.SignupVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final CampusActivityMapper activityMapper;
    private final ActivitySignupMapper signupMapper;
    private final UserMapper userMapper;
    private final StudentMapper studentMapper;
    private final ClassMapper classMapper;

    @Override
    public List<ActivityVO> list(Integer status) {
        LambdaQueryWrapper<CampusActivity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(CampusActivity::getStatus, status);
        wrapper.orderByDesc(CampusActivity::getCreateTime);
        List<CampusActivity> activities = activityMapper.selectList(wrapper);
        if (activities.isEmpty()) return List.of();

        // 批量加载作者
        List<Long> authorIds = activities.stream().map(CampusActivity::getAuthorId).distinct().collect(Collectors.toList());
        Map<Long, String> authorMap = userMapper.selectBatchIds(authorIds).stream()
                .collect(Collectors.toMap(User::getId,
                        u -> StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername()));

        // 批量统计报名人数
        List<Long> actIds = activities.stream().map(CampusActivity::getId).collect(Collectors.toList());
        Map<Long, Long> signupCountMap = signupMapper.selectList(
                new LambdaQueryWrapper<ActivitySignup>().in(ActivitySignup::getActivityId, actIds))
                .stream().collect(Collectors.groupingBy(ActivitySignup::getActivityId, Collectors.counting()));

        return activities.stream().map(a -> {
            ActivityVO vo = new ActivityVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setDescription(a.getDescription());
            vo.setLocation(a.getLocation());
            vo.setActivityTime(a.getActivityTime());
            vo.setSignupDeadline(a.getSignupDeadline());
            vo.setMaxCount(a.getMaxCount());
            vo.setStatus(a.getStatus());
            vo.setAuthorId(a.getAuthorId());
            vo.setAuthorName(authorMap.getOrDefault(a.getAuthorId(), "未知"));
            vo.setSignupCount(signupCountMap.getOrDefault(a.getId(), 0L).intValue());
            vo.setCreateTime(a.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void add(ActivityDTO dto, Long authorId) {
        CampusActivity a = new CampusActivity();
        a.setTitle(dto.getTitle());
        a.setDescription(dto.getDescription());
        a.setLocation(dto.getLocation());
        a.setActivityTime(dto.getActivityTime());
        a.setSignupDeadline(dto.getSignupDeadline());
        a.setMaxCount(dto.getMaxCount());
        a.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        a.setAuthorId(authorId);
        activityMapper.insert(a);
    }

    @Override
    public void update(Long id, ActivityDTO dto) {
        CampusActivity a = activityMapper.selectById(id);
        if (a == null) throw new RuntimeException("活动不存在");
        if (StringUtils.hasText(dto.getTitle())) a.setTitle(dto.getTitle());
        if (dto.getDescription() != null)        a.setDescription(dto.getDescription());
        if (dto.getLocation() != null)           a.setLocation(dto.getLocation());
        if (dto.getActivityTime() != null)       a.setActivityTime(dto.getActivityTime());
        if (dto.getSignupDeadline() != null)     a.setSignupDeadline(dto.getSignupDeadline());
        a.setMaxCount(dto.getMaxCount());
        if (dto.getStatus() != null)             a.setStatus(dto.getStatus());
        activityMapper.updateById(a);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        CampusActivity a = activityMapper.selectById(id);
        if (a == null) throw new RuntimeException("活动不存在");
        a.setStatus(status);
        activityMapper.updateById(a);
    }

    @Override
    public void delete(Long id) {
        // 删除报名记录一并清除
        signupMapper.delete(new LambdaQueryWrapper<ActivitySignup>().eq(ActivitySignup::getActivityId, id));
        activityMapper.deleteById(id);
    }

    @Override
    public void signup(Long activityId, Long parentId, Long studentId, String remark) {
        CampusActivity activity = activityMapper.selectById(activityId);
        if (activity == null) throw new RuntimeException("活动不存在");
        if (activity.getStatus() != 1) throw new RuntimeException("活动未开放报名");

        // 检查名额
        if (activity.getMaxCount() != null) {
            long count = signupMapper.selectCount(
                    new LambdaQueryWrapper<ActivitySignup>().eq(ActivitySignup::getActivityId, activityId));
            if (count >= activity.getMaxCount()) throw new RuntimeException("活动名额已满");
        }

        // 防重复报名（同一家长同一幼儿）
        long exist = signupMapper.selectCount(
                new LambdaQueryWrapper<ActivitySignup>()
                        .eq(ActivitySignup::getActivityId, activityId)
                        .eq(ActivitySignup::getParentId, parentId)
                        .eq(ActivitySignup::getStudentId, studentId));
        if (exist > 0) throw new RuntimeException("已报名，请勿重复提交");

        ActivitySignup s = new ActivitySignup();
        s.setActivityId(activityId);
        s.setParentId(parentId);
        s.setStudentId(studentId);
        s.setRemark(remark);
        s.setCreateTime(java.time.LocalDateTime.now());
        signupMapper.insert(s);
    }

    @Override
    public void cancelSignup(Long signupId, Long parentId) {
        ActivitySignup s = signupMapper.selectById(signupId);
        if (s == null) throw new RuntimeException("报名记录不存在");
        if (!s.getParentId().equals(parentId)) throw new RuntimeException("无权取消他人的报名");
        signupMapper.deleteById(signupId);
    }

    @Override
    public List<MySignupVO> mySignups(Long parentId) {
        List<ActivitySignup> signups = signupMapper.selectList(
                new LambdaQueryWrapper<ActivitySignup>()
                        .eq(ActivitySignup::getParentId, parentId)
                        .orderByDesc(ActivitySignup::getCreateTime));
        if (signups.isEmpty()) return List.of();

        List<Long> actIds = signups.stream().map(ActivitySignup::getActivityId).distinct().collect(Collectors.toList());
        Map<Long, CampusActivity> actMap = activityMapper.selectBatchIds(actIds).stream()
                .collect(Collectors.toMap(CampusActivity::getId, a -> a));

        List<Long> stuIds = signups.stream().map(ActivitySignup::getStudentId).distinct().collect(Collectors.toList());
        Map<Long, String> stuNameMap = studentMapper.selectBatchIds(stuIds).stream()
                .collect(Collectors.toMap(Student::getId, Student::getName));

        return signups.stream().map(s -> {
            MySignupVO vo = new MySignupVO();
            vo.setSignupId(s.getId());
            vo.setActivityId(s.getActivityId());
            vo.setStudentName(stuNameMap.getOrDefault(s.getStudentId(), "-"));
            vo.setRemark(s.getRemark());
            vo.setSignupTime(s.getCreateTime());
            CampusActivity a = actMap.get(s.getActivityId());
            if (a != null) {
                vo.setActivityTitle(a.getTitle());
                vo.setActivityLocation(a.getLocation());
                vo.setActivityTime(a.getActivityTime());
                vo.setActivityStatus(a.getStatus());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SignupVO> listSignups(Long activityId) {
        List<ActivitySignup> signups = signupMapper.selectList(
                new LambdaQueryWrapper<ActivitySignup>()
                        .eq(ActivitySignup::getActivityId, activityId)
                        .orderByAsc(ActivitySignup::getCreateTime));
        if (signups.isEmpty()) return List.of();

        // 批量加载家长信息
        List<Long> parentIds = signups.stream().map(ActivitySignup::getParentId).distinct().collect(Collectors.toList());
        Map<Long, User> parentMap = userMapper.selectBatchIds(parentIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 批量加载幼儿信息
        List<Long> studentIds = signups.stream().map(ActivitySignup::getStudentId).distinct().collect(Collectors.toList());
        Map<Long, Student> studentMap = studentMapper.selectBatchIds(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, s -> s));

        // 批量加载班级名称
        List<Long> classIds = studentMap.values().stream()
                .filter(s -> s.getClassId() != null)
                .map(Student::getClassId).distinct().collect(Collectors.toList());
        Map<Long, String> classNameMap = classIds.isEmpty() ? Map.of() :
                classMapper.selectBatchIds(classIds).stream()
                        .collect(Collectors.toMap(SysClass::getId, SysClass::getClassName));

        return signups.stream().map(s -> {
            SignupVO vo = new SignupVO();
            vo.setId(s.getId());
            vo.setParentId(s.getParentId());
            User parent = parentMap.get(s.getParentId());
            if (parent != null) {
                vo.setParentName(StringUtils.hasText(parent.getRealName()) ? parent.getRealName() : parent.getUsername());
                vo.setParentPhone(parent.getPhone());
            }
            vo.setStudentId(s.getStudentId());
            Student student = studentMap.get(s.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getName());
                vo.setClassName(student.getClassId() != null ? classNameMap.getOrDefault(student.getClassId(), "-") : "-");
            }
            vo.setRemark(s.getRemark());
            vo.setCreateTime(s.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }
}
