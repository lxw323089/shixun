package com.example.workermanagement.controller;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.Result;
import com.example.workermanagement.config.JwtUtil;
import com.example.workermanagement.entity.User;
import com.example.workermanagement.service.OperationLogService;
import com.example.workermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;
    
    @GetMapping
    public Result<PageResult<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<User> userPage = userService.getUsers(page, pageSize);
        PageResult<User> result = new PageResult<>(
                userPage.getContent(),
                userPage.getTotalElements(),
                page,
                pageSize
        );
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> {
                    user.setPassword(null);
                    return Result.success(user);
                })
                .orElse(Result.error("用户不存在"));
    }
    
    @PostMapping
    public Result<User> createUser(@RequestBody User user,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User created = userService.createUser(user);
            created.setPassword(null);
            logOperation(authHeader, "新增", "新增用户：" + user.getUsername());
            return Result.success("创建成功", created);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id,
                                    @RequestBody User user,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User updated = userService.updateUser(id, user);
            updated.setPassword(null);
            logOperation(authHeader, "修改", "修改用户：" + user.getUsername());
            return Result.success("更新成功", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reset-password")
    public Result<User> resetPassword(@PathVariable Long id,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User updated = userService.resetPassword(id);
            updated.setPassword(null);
            logOperation(authHeader, "重置密码", "重置用户密码：" + updated.getUsername());
            return Result.success("密码已重置为123456", updated);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = userService.getUserById(id).orElse(null);
            userService.deleteUser(id);
            if (user != null) {
                logOperation(authHeader, "删除", "删除用户：" + user.getUsername());
            }
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                       @RequestBody Map<String, String> body) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);
            String nickname = body.get("nickname");
            User updated = userService.updateProfile(userId, nickname);
            updated.setPassword(null);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                     @RequestParam("file") MultipartFile file) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未登录");
        }
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);

            String original = file.getOriginalFilename();
            String ext = original != null && original.contains(".")
                    ? original.substring(original.lastIndexOf(".")) : ".jpg";
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            String dirPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatar";
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();
            file.transferTo(new File(dir, fileName));

            String avatarUrl = "/uploads/avatar/" + fileName;
            User updated = userService.updateAvatar(userId, avatarUrl);
            updated.setPassword(null);
            return Result.success("上传成功", Map.of("avatar", avatarUrl));
        } catch (IOException e) {
            return Result.error("上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
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
            operationLogService.addLog(userId, username, "系统用户", operation, content);
        } catch (Exception e) {
            System.err.println("操作日志记录失败：写入异常 - " + e.getMessage());
        }
    }
}
