package com.jxhd.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxhd.backend.common.Result;
import com.jxhd.backend.entity.OperationLog;
import com.jxhd.backend.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class LogController {

    private final OperationLogMapper logMapper;

    @GetMapping
    public Result<IPage<OperationLog>> page(
            @RequestParam(defaultValue = "1")  int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .like(StringUtils.hasText(username), OperationLog::getUsername, username)
                .like(StringUtils.hasText(module),   OperationLog::getModule,   module)
                .ge(StringUtils.hasText(dateFrom), OperationLog::getCreateTime,
                        StringUtils.hasText(dateFrom) ? LocalDateTime.parse(dateFrom + "T00:00:00") : null)
                .le(StringUtils.hasText(dateTo),   OperationLog::getCreateTime,
                        StringUtils.hasText(dateTo)   ? LocalDateTime.parse(dateTo   + "T23:59:59") : null)
                .orderByDesc(OperationLog::getCreateTime);

        return Result.success(logMapper.selectPage(new Page<>(pageNum, pageSize), wrapper));
    }
}
