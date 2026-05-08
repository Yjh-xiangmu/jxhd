package com.jxhd.backend.service;

import com.jxhd.backend.dto.ActivityDTO;
import com.jxhd.backend.vo.ActivityVO;
import com.jxhd.backend.vo.MySignupVO;
import com.jxhd.backend.vo.SignupVO;

import java.util.List;

public interface ActivityService {
    List<ActivityVO> list(Integer status);
    void add(ActivityDTO dto, Long authorId);
    void update(Long id, ActivityDTO dto);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
    List<SignupVO> listSignups(Long activityId);
    void signup(Long activityId, Long parentId, Long studentId, String remark);
    void cancelSignup(Long signupId, Long parentId);
    List<MySignupVO> mySignups(Long parentId);
}
