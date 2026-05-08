package com.jxhd.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxhd.backend.dto.AttendanceDTO;
import com.jxhd.backend.entity.AttendanceRecord;
import com.jxhd.backend.entity.Student;
import com.jxhd.backend.mapper.AttendanceRecordMapper;
import com.jxhd.backend.mapper.StudentMapper;
import com.jxhd.backend.service.AttendanceService;
import com.jxhd.backend.vo.AttendanceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRecordMapper attendanceMapper;
    private final StudentMapper studentMapper;

    @Override
    public List<AttendanceVO> getDailyAttendance(Long classId, String date) {
        LocalDate localDate = LocalDate.parse(date);

        // 查该班所有在园幼儿
        List<Student> students = studentMapper.selectList(
                new LambdaQueryWrapper<Student>()
                        .eq(Student::getClassId, classId)
                        .eq(Student::getStatus, 1)
                        .orderByAsc(Student::getName));

        if (students.isEmpty()) return List.of();

        // 查该班当天所有考勤记录
        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        Map<Long, AttendanceRecord> recordMap = attendanceMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecord>()
                        .in(AttendanceRecord::getStudentId, studentIds)
                        .eq(AttendanceRecord::getDate, localDate))
                .stream().collect(Collectors.toMap(AttendanceRecord::getStudentId, r -> r));

        return students.stream().map(s -> {
            AttendanceVO vo = new AttendanceVO();
            vo.setStudentId(s.getId());
            vo.setStudentName(s.getName());
            vo.setGender(s.getGender());
            vo.setBirthday(s.getBirthday());
            vo.setRemark(s.getRemark());

            AttendanceRecord record = recordMap.get(s.getId());
            if (record != null) {
                vo.setAttendanceId(record.getId());
                vo.setStatus(record.getStatus());
                vo.setAbsenceReason(record.getAbsenceReason());
                vo.setContactMethod(record.getContactMethod());
                vo.setContactNote(record.getContactNote());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void recordAttendance(AttendanceDTO dto, Long recorderId) {
        LocalDate date = LocalDate.parse(dto.getDate());

        // upsert：已有记录则更新，否则新增
        AttendanceRecord existing = attendanceMapper.selectOne(
                new LambdaQueryWrapper<AttendanceRecord>()
                        .eq(AttendanceRecord::getStudentId, dto.getStudentId())
                        .eq(AttendanceRecord::getDate, date));

        if (existing != null) {
            existing.setStatus(dto.getStatus());
            existing.setAbsenceReason(dto.getAbsenceReason());
            existing.setContactMethod(dto.getContactMethod());
            existing.setContactNote(dto.getContactNote());
            existing.setRecorderId(recorderId);
            attendanceMapper.updateById(existing);
        } else {
            AttendanceRecord r = new AttendanceRecord();
            r.setStudentId(dto.getStudentId());
            r.setClassId(dto.getClassId());
            r.setDate(date);
            r.setStatus(dto.getStatus());
            r.setAbsenceReason(dto.getAbsenceReason());
            r.setContactMethod(dto.getContactMethod());
            r.setContactNote(dto.getContactNote());
            r.setRecorderId(recorderId);
            attendanceMapper.insert(r);
        }
    }

    @Override
    public void batchPresent(Long classId, String date, List<Long> studentIds, Long recorderId) {
        LocalDate localDate = LocalDate.parse(date);
        for (Long sid : studentIds) {
            AttendanceRecord existing = attendanceMapper.selectOne(
                    new LambdaQueryWrapper<AttendanceRecord>()
                            .eq(AttendanceRecord::getStudentId, sid)
                            .eq(AttendanceRecord::getDate, localDate));
            if (existing != null) {
                existing.setStatus(1);
                existing.setAbsenceReason(null);
                existing.setContactMethod(null);
                existing.setContactNote(null);
                existing.setRecorderId(recorderId);
                attendanceMapper.updateById(existing);
            } else {
                AttendanceRecord r = new AttendanceRecord();
                r.setStudentId(sid);
                r.setClassId(classId);
                r.setDate(localDate);
                r.setStatus(1);
                r.setRecorderId(recorderId);
                attendanceMapper.insert(r);
            }
        }
    }
}
