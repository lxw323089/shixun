package com.example.workermanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String role;
    
    private String nickname;
    
    private String avatar;
    
    @Column(name = "worker_id")
    private Long workerId;
    
    @Column(name = "worker_name")
    private String workerName;
    
    private String status;
    
    @Column(name = "create_time")
    private String createTime;
    
    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
