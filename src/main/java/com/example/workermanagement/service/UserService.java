package com.example.workermanagement.service;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.TimeUtil;
import com.example.workermanagement.entity.User;
import com.example.workermanagement.mapper.UserMapper;
import com.example.workermanagement.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final WorkerMapper workerMapper;

    public PageResult<User> getUsers(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> list = userMapper.selectPage(offset, pageSize);
        long total = userMapper.count();
        return new PageResult<>(list, total, page, pageSize);
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userMapper.selectByUsername(username));
    }

    public User createUser(User user) {
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        if (user.getCreateTime() == null) {
            user.setCreateTime(TimeUtil.now());
        }
        userMapper.insert(user);
        return user;
    }

    public User updateUser(Long id, User user) {
        User existing = getUserById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!existing.getUsername().equals(user.getUsername())
                && userMapper.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        if ("admin".equals(existing.getRole()) && !"admin".equals(user.getRole())
                && userMapper.countByRole("admin") <= 1) {
            throw new RuntimeException("修改失败：系统至少需要保留一名管理员");
        }

        existing.setUsername(user.getUsername());
        existing.setRole(user.getRole());
        existing.setWorkerId(user.getWorkerId());
        if (user.getWorkerId() != null) {
            com.example.workermanagement.entity.Worker worker = workerMapper.selectById(user.getWorkerId());
            if (worker != null) {
                existing.setWorkerName(worker.getName());
            }
        } else {
            existing.setWorkerName(null);
        }
        existing.setStatus(user.getStatus());

        userMapper.update(existing);
        return existing;
    }

    public User resetPassword(Long id) {
        User existing = getUserById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setPassword("123456");
        userMapper.update(existing);
        return existing;
    }

    public void deleteUser(Long id) {
        User existing = getUserById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if ("admin".equals(existing.getRole()) && userMapper.countByRole("admin") <= 1) {
            throw new RuntimeException("删除失败：系统至少需要保留一名管理员");
        }
        userMapper.deleteById(id);
    }

    public User updateProfile(Long userId, String nickname, String username) {
        User existing = getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (username != null && !username.isEmpty()
                && !username.equals(existing.getUsername())) {
            if (userMapper.existsByUsername(username)) {
                throw new RuntimeException("用户名已存在");
            }
            existing.setUsername(username);
        }

        if (nickname != null) {
            existing.setNickname(nickname);
        }

        userMapper.update(existing);
        return existing;
    }

    public User updateAvatar(Long userId, String avatar) {
        User existing = getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        existing.setAvatar(avatar);
        userMapper.update(existing);
        return existing;
    }
}
