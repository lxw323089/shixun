package com.example.workermanagement.service;

import com.example.workermanagement.entity.User;
import com.example.workermanagement.repository.UserRepository;
import com.example.workermanagement.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    
    public Page<User> getUsers(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        return userRepository.findAll(pageable);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!existing.getUsername().equals(user.getUsername()) 
                && userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        if ("admin".equals(existing.getRole()) && !"admin".equals(user.getRole())
                && userRepository.countByRole("admin") <= 1) {
            throw new RuntimeException("修改失败：系统至少需要保留一名管理员");
        }
        
        existing.setUsername(user.getUsername());
        existing.setRole(user.getRole());
        existing.setWorkerId(user.getWorkerId());
        if (user.getWorkerId() != null) {
            workerRepository.findById(user.getWorkerId()).ifPresent(w -> existing.setWorkerName(w.getName()));
        } else {
            existing.setWorkerName(null);
        }
        existing.setStatus(user.getStatus());
        
        return userRepository.save(existing);
    }
    
    public User resetPassword(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setPassword("123456");
        return userRepository.save(existing);
    }
    
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equals(existing.getRole()) && userRepository.countByRole("admin") <= 1) {
            throw new RuntimeException("删除失败：系统至少需要保留一名管理员");
        }
        userRepository.deleteById(id);
    }

    public User updateProfile(Long userId, String nickname, String username) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (username != null && !username.isEmpty()
                && !username.equals(existing.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new RuntimeException("用户名已存在");
            }
            existing.setUsername(username);
        }

        if (nickname != null) {
            existing.setNickname(nickname);
        }

        return userRepository.save(existing);
    }

    public User updateAvatar(Long userId, String avatar) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setAvatar(avatar);
        return userRepository.save(existing);
    }
}
