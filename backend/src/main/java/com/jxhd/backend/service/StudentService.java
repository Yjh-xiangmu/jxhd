package com.jxhd.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.dto.ParentBindDTO;
import com.jxhd.backend.dto.StudentDTO;
import com.jxhd.backend.vo.StudentDetailVO;
import com.jxhd.backend.vo.StudentVO;

public interface StudentService {

    /** 分页查询幼儿列表 */
    IPage<StudentVO> page(int pageNum, int pageSize, Long classId, String keyword, Integer status);

    /** 查询幼儿详情（含绑定家长） */
    StudentDetailVO detail(Long studentId);

    /** 新增幼儿 */
    void add(StudentDTO dto);

    /** 修改幼儿信息 */
    void update(StudentDTO dto);

    /** 删除幼儿 */
    void delete(Long id);

    /** 转班 */
    void changeClass(Long studentId, Long newClassId);

    /** 管理员手动绑定家长 */
    void bindParent(ParentBindDTO dto);

    /** 解绑家长 */
    void unbindParent(Long bindId);

    /** 审核家长绑定申请 */
    void auditBind(Long bindId, Integer status, Long auditorId);
}