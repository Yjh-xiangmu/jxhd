package com.jxhd.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.dto.GrowthRecordDTO;
import com.jxhd.backend.vo.GrowthRecordVO;

import java.util.List;

public interface GrowthRecordService {
    IPage<GrowthRecordVO> pageByClass(int pageNum, int pageSize, Long classId);
    List<GrowthRecordVO> listByStudent(Long studentId);
    void add(GrowthRecordDTO dto, Long authorId, Long classId);
    void delete(Long id, Long operatorId);
}
