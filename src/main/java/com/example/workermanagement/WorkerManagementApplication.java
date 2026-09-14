package com.example.workermanagement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.workermanagement.mapper")
public class WorkerManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerManagementApplication.class, args);
    }
}
