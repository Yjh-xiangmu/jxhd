package com.jxhd.backend.service;

import com.jxhd.backend.dto.ClassDTO;
import com.jxhd.backend.vo.ClassVO;

import java.util.List;

public interface ClassService {

    /** 查询所有班级（带班主任名+学生数） */
    List<ClassVO> listAll(String keyword, Integer status);

    /** 新增班级 */
    void add(ClassDTO dto);

    /** 修改班级 */
    void update(ClassDTO dto);

    /** 启用/禁用 */
    void updateStatus(Long id, Integer status);

    /** 删除班级（班级下有幼儿时禁止删除） */
    void delete(Long id);

    /** 查询所有教师列表（用于下拉选班主任） */
    List<com.jxhd.backend.entity.User> listTeachers();
}