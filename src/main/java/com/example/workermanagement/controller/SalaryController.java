package com.example.workermanagement.controller;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.Result;
import com.example.workermanagement.config.JwtUtil;
import com.example.workermanagement.entity.Salary;
import com.example.workermanagement.service.OperationLogService;
import com.example.workermanagement.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {
    
    private final SalaryService salaryService;
    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public Result<PageResult<Salary>> getSalaries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String workerName,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String status) {
        Page<Salary> salaryPage = salaryService.getSalaries(page, pageSize, workerName, month, status);
        PageResult<Salary> result = new PageResult<>(
                salaryPage.getContent(),
                salaryPage.getTotalElements(),
                page,
                pageSize
        );
        return Result.success(result);
    }
    
    @GetMapping("/worker/{workerId}")
    public Result<List<Salary>> getSalariesByWorkerId(@PathVariable Long workerId) {
        List<Salary> salaries = salaryService.getSalariesByWorkerId(workerId);
        return Result.success(salaries);
    }
    
    @GetMapping("/{id}")
    public Result<Salary> getSalaryById(@PathVariable Long id) {
        return salaryService.getSalaryById(id)
                .map(Result::success)
                .orElse(Result.error("工资记录不存在"));
    }
    
    @PostMapping
    public Result<Salary> createSalary(@RequestBody Salary salary,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Salary created = salaryService.createSalary(salary);
        logOperation(authHeader, "核算", "核算" + salary.getMonth() + "工资：" + salary.getWorkerName());
        return Result.success("创建成功", created);
    }
    
    @PutMapping("/{id}")
    public Result<Salary> updateSalary(@PathVariable Long id,
                                        @RequestBody Salary salary,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Salary updated = salaryService.updateSalary(id, salary);
            logOperation(authHeader, "修改", "修改工资：" + salary.getWorkerName() + " " + salary.getMonth());
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/pay")
    public Result<Salary> paySalary(@PathVariable Long id,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Salary paid = salaryService.paySalary(id);
            logOperation(authHeader, "发放", "发放工资：" + paid.getWorkerName() + " " + paid.getMonth());
            return Result.success("发放成功", paid);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteSalary(@PathVariable Long id,
                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Salary sal = salaryService.getSalaryById(id).orElse(null);
            salaryService.deleteSalary(id);
            if (sal != null) {
                logOperation(authHeader, "删除", "删除工资：" + sal.getWorkerName() + " " + sal.getMonth());
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
            operationLogService.addLog(userId, username, "工资管理", operation, content);
        } catch (Exception e) {
            System.err.println("操作日志记录失败：写入异常 - " + e.getMessage());
        }
    }
}
