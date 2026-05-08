package com.jxhd.backend.service;

import com.jxhd.backend.dto.ScheduleDTO;
import com.jxhd.backend.vo.ClassVO;
import com.jxhd.backend.vo.ScheduleVO;

import java.util.List;

public interface ScheduleService {
    List<ScheduleVO> getByClass(Long classId);
    void saveEntry(ScheduleDTO dto);
    void deleteEntry(Long id);
    /** 获取教师主要班级的课表（优先 is_head=1） */
    ClassVO getTeacherClass(Long teacherId);
}
