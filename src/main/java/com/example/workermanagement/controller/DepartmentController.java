package com.example.workermanagement.controller;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.Result;
import com.example.workermanagement.config.JwtUtil;
import com.example.workermanagement.entity.Department;
import com.example.workermanagement.service.DepartmentService;
import com.example.workermanagement.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public Result<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }
    
    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(Result::success)
                .orElse(Result.error("部门不存在"));
    }
    
    @PostMapping
    public Result<Department> createDepartment(@RequestBody Department department,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Department created = departmentService.createDepartment(department);
            logOperation(authHeader, "新增", "新增部门：" + department.getName());
            return Result.success("创建成功", created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Department> updateDepartment(@PathVariable Long id,
                                                @RequestBody Department department,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Department updated = departmentService.updateDepartment(id, department);
            logOperation(authHeader, "修改", "修改部门：" + department.getName());
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Long id,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Department dept = departmentService.getDepartmentById(id).orElse(null);
            departmentService.deleteDepartment(id);
            if (dept != null) {
                logOperation(authHeader, "删除", "删除部门：" + dept.getName());
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
            operationLogService.addLog(userId, username, "部门管理", operation, content);
        } catch (Exception e) {
            System.err.println("操作日志记录失败：写入异常 - " + e.getMessage());
        }
    }
}
