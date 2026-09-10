package com.example.workermanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "salaries")
public class Salary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "worker_id")
    private Long workerId;
    
    @Column(name = "worker_name")
    private String workerName;
    
    @Column(name = "department_name")
    private String departmentName;
    
    private String month;
    
    @Column(name = "base_salary")
    private Double baseSalary;
    
    private Double bonus;
    
    private Double deduction;
    
    private Double total;
    
    private String status;
    
    @Column(name = "create_time")
    private String createTime;
    
    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
