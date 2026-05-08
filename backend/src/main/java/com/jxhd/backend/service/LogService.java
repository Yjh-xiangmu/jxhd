package com.jxhd.backend.service;

import com.jxhd.backend.entity.OperationLog;
import com.jxhd.backend.entity.User;
import com.jxhd.backend.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {

    private final OperationLogMapper logMapper;

    @Async
    public void record(User user, String module, String action, String detail, HttpServletRequest request) {
        try {
            OperationLog log = new OperationLog();
            if (user != null) {
                log.setUserId(user.getId());
                log.setUsername(user.getUsername());
            }
            log.setModule(module);
            log.setAction(action);
            log.setDetail(detail);
            log.setIp(getClientIp(request));
            log.setCreateTime(LocalDateTime.now());
            logMapper.insert(log);
        } catch (Exception ignored) {}
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) return null;
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return ip;
    }
}
