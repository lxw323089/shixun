package com.example.workermanagement.service;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.TimeUtil;
import com.example.workermanagement.entity.OperationLog;
import com.example.workermanagement.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public void addLog(Long userId, String username, String module, String operation, String content) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setModule(module);
        log.setOperation(operation);
        log.setContent(content);
        log.setCreateTime(TimeUtil.now());
        operationLogMapper.insert(log);
    }

    public PageResult<OperationLog> getLogs(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<OperationLog> list = operationLogMapper.selectPage(offset, pageSize);
        long total = operationLogMapper.count();
        return new PageResult<>(list, total, page, pageSize);
    }
}
