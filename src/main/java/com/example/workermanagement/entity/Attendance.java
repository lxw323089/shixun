package com.example.workermanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "attendances")
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "worker_id")
    private Long workerId;
    
    @Column(name = "worker_name")
    private String workerName;
    
    @Column(name = "department_name")
    private String departmentName;
    
    private String date;
    
    private String status;
    
    @Column(name = "check_in")
    private String checkIn;
    
    @Column(name = "check_out")
    private String checkOut;
    
    private String remark;
    
    @Column(name = "create_time")
    private String createTime;
    
    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
