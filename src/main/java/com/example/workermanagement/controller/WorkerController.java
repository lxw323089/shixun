package com.example.workermanagement.controller;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.Result;
import com.example.workermanagement.config.JwtUtil;
import com.example.workermanagement.entity.Worker;
import com.example.workermanagement.service.OperationLogService;
import com.example.workermanagement.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
public class WorkerController {
    
    private final WorkerService workerService;
    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public Result<PageResult<Worker>> getWorkers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status) {
        Page<Worker> workerPage = workerService.getWorkers(page, pageSize, name, departmentId, status);
        PageResult<Worker> result = new PageResult<>(
                workerPage.getContent(),
                workerPage.getTotalElements(),
                page,
                pageSize
        );
        return Result.success(result);
    }
    
    @GetMapping("/all")
    public Result<List<Worker>> getAllWorkers() {
        List<Worker> workers = workerService.getAllWorkers();
        return Result.success(workers);
    }
    
    @GetMapping("/{id}")
    public Result<Worker> getWorkerById(@PathVariable Long id) {
        return workerService.getWorkerById(id)
                .map(Result::success)
                .orElse(Result.error("员工不存在"));
    }
    
    @PostMapping
    public Result<Worker> createWorker(@RequestBody Worker worker,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Worker created = workerService.createWorker(worker);
        logOperation(authHeader, "新增", "新增员工：" + worker.getName());
        return Result.success("创建成功", created);
    }
    
    @PutMapping("/{id}")
    public Result<Worker> updateWorker(@PathVariable Long id,
                                        @RequestBody Worker worker,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Worker updated = workerService.updateWorker(id, worker);
            logOperation(authHeader, "修改", "修改员工信息：" + worker.getName());
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteWorker(@PathVariable Long id,
                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Worker worker = workerService.getWorkerById(id).orElse(null);
            workerService.deleteWorker(id);
            if (worker != null) {
                logOperation(authHeader, "删除", "删除员工：" + worker.getName());
            }
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    private void logOperation(String authHeader, String operation, String content) {
        Long userId = null;
        String username = "system";
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                userId = jwtUtil.extractUserId(token);
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                System.err.println("操作日志记录失败：解析token异常 - " + e.getMessage());
            }
        }
        try {
            operationLogService.addLog(userId, username, "员工管理", operation, content);
        } catch (Exception e) {
            System.err.println("操作日志记录失败：写入异常 - " + e.getMessage());
        }
    }
}
