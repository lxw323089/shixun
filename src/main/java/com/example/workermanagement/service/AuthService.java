package com.example.workermanagement.service;

import com.example.workermanagement.config.JwtUtil;
import com.example.workermanagement.entity.User;
import com.example.workermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OperationLogService operationLogService;
    
    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!"启用".equals(user.getStatus())) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        operationLogService.addLog(user.getId(), user.getUsername(), "系统登录", "登录", "用户登录系统");
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        user.setPassword(null);
        result.put("user", user);
        
        return result;
    }
    
    public void logout(Long userId, String username) {
        operationLogService.addLog(userId, username, "系统登录", "登出", "用户退出系统");
    }
}
