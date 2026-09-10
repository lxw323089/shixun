package com.example.workermanagement.service;

import com.example.workermanagement.entity.OperationLog;
import com.example.workermanagement.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OperationLogService {
    
    private final OperationLogRepository operationLogRepository;
    
    public void addLog(Long userId, String username, String module, String operation, String content) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setModule(module);
        log.setOperation(operation);
        log.setContent(content);
        operationLogRepository.save(log);
    }
    
    public Page<OperationLog> getLogs(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return operationLogRepository.findAll(pageable);
    }
}
