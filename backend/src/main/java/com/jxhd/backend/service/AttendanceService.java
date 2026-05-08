package com.jxhd.backend.service;

import com.jxhd.backend.dto.AttendanceDTO;
import com.jxhd.backend.vo.AttendanceVO;

import java.util.List;

public interface AttendanceService {
    /** 获取某班某天所有学生的考勤状态 */
    List<AttendanceVO> getDailyAttendance(Long classId, String date);
    /** 记录或更新单个学生考勤 */
    void recordAttendance(AttendanceDTO dto, Long recorderId);
    /** 批量标记出勤（一键全勤） */
    void batchPresent(Long classId, String date, List<Long> studentIds, Long recorderId);
}
