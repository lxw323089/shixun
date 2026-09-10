package com.example.workermanagement.service;

import com.example.workermanagement.entity.User;
import com.example.workermanagement.repository.UserRepository;
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
        
        existing.setUsername(user.getUsername());
        existing.setRole(user.getRole());
        existing.setWorkerId(user.getWorkerId());
        existing.setWorkerName(user.getWorkerName());
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
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    public User updateProfile(Long userId, String nickname) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setNickname(nickname);
        return userRepository.save(existing);
    }

    public User updateAvatar(Long userId, String avatar) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setAvatar(avatar);
        return userRepository.save(existing);
    }
}
