package com.example.workermanagement.controller;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.Result;
import com.example.workermanagement.config.JwtUtil;
import com.example.workermanagement.entity.Attendance;
import com.example.workermanagement.service.AttendanceService;
import com.example.workermanagement.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public Result<PageResult<Attendance>> getAttendances(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String workerName,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        Page<Attendance> attendancePage = attendanceService.getAttendances(page, pageSize, workerName, date, status);
        PageResult<Attendance> result = new PageResult<>(
                attendancePage.getContent(),
                attendancePage.getTotalElements(),
                page,
                pageSize
        );
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<Attendance> getAttendanceById(@PathVariable Long id) {
        return attendanceService.getAttendanceById(id)
                .map(Result::success)
                .orElse(Result.error("考勤记录不存在"));
    }
    
    @PostMapping
    public Result<Attendance> createAttendance(@RequestBody Attendance attendance,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Attendance created = attendanceService.createAttendance(attendance);
        logOperation(authHeader, "录入", "录入" + attendance.getDate() + "考勤：" + attendance.getWorkerName());
        return Result.success("创建成功", created);
    }
    
    @PutMapping("/{id}")
    public Result<Attendance> updateAttendance(@PathVariable Long id,
                                                @RequestBody Attendance attendance,
                                                @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Attendance updated = attendanceService.updateAttendance(id, attendance);
            logOperation(authHeader, "修改", "修改考勤记录：" + attendance.getWorkerName() + " " + attendance.getDate());
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteAttendance(@PathVariable Long id,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Attendance att = attendanceService.getAttendanceById(id).orElse(null);
            attendanceService.deleteAttendance(id);
            if (att != null) {
                logOperation(authHeader, "删除", "删除考勤记录：" + att.getWorkerName() + " " + att.getDate());
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
            operationLogService.addLog(userId, username, "考勤管理", operation, content);
        } catch (Exception e) {
            System.err.println("操作日志记录失败：写入异常 - " + e.getMessage());
        }
    }
}
