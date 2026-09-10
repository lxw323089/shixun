package com.example.workermanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "workers")
public class Worker {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "department_id")
    private Long departmentId;
    
    @Column(name = "department_name")
    private String departmentName;
    
    @Column(nullable = false)
    private String name;
    
    private String gender;
    
    private String phone;
    
    @Column(name = "id_card")
    private String idCard;
    
    @Column(name = "entry_date")
    private String entryDate;
    
    private String position;
    
    @Column(name = "base_salary")
    private Double baseSalary;
    
    private String status;
    
    @Column(name = "create_time")
    private String createTime;
    
    @Column(name = "update_time")
    private String updateTime;
    
    @PrePersist
    protected void onCreate() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.createTime = now;
        this.updateTime = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
