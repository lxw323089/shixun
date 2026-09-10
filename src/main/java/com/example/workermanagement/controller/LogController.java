package com.example.workermanagement.controller;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.Result;
import com.example.workermanagement.entity.OperationLog;
import com.example.workermanagement.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    
    private final OperationLogService operationLogService;
    
    @GetMapping
    public Result<PageResult<OperationLog>> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<OperationLog> logPage = operationLogService.getLogs(page, pageSize);
        PageResult<OperationLog> result = new PageResult<>(
                logPage.getContent(),
                logPage.getTotalElements(),
                page,
                pageSize
        );
        return Result.success(result);
    }
}
