package com.example.workermanagement.controller;

import com.example.workermanagement.common.Result;
import com.example.workermanagement.entity.Department;
import com.example.workermanagement.entity.OperationLog;
import com.example.workermanagement.entity.Worker;
import com.example.workermanagement.service.DepartmentService;
import com.example.workermanagement.service.OperationLogService;
import com.example.workermanagement.service.SalaryService;
import com.example.workermanagement.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DepartmentService departmentService;
    private final WorkerService workerService;
    private final SalaryService salaryService;
    private final OperationLogService operationLogService;
    
    @GetMapping("/stats")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Department> departments = departmentService.getAllDepartments();
        stats.put("departmentCount", departments.size());
        
        long activeWorkers = workerService.countByStatus("在职");
        stats.put("workerCount", activeWorkers);
        
        stats.put("attendanceRate", "95.6%");
        
        double totalSalary = salaryService.getTotalSalaryByMonth("2024-09");
        stats.put("totalSalary", totalSalary);
        
        return Result.success(stats);
    }
    
    @GetMapping("/recent-workers")
    public Result<List<Worker>> getRecentWorkers() {
        List<Worker> workers = workerService.getAllWorkers();
        List<Worker> recent = workers.size() > 5 ? workers.subList(0, 5) : workers;
        return Result.success(recent);
    }
    
    @GetMapping("/recent-logs")
    public Result<List<OperationLog>> getRecentLogs() {
        Page<OperationLog> logPage = operationLogService.getLogs(1, 5);
        return Result.success(logPage.getContent());
    }
}
