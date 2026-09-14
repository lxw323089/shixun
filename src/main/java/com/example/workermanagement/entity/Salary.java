package com.example.workermanagement.entity;

import lombok.Data;

@Data
public class Salary {

    private Long id;

    private Long workerId;

    private String workerName;

    private String departmentName;

    private String month;

    private Double baseSalary;

    private Double bonus;

    private Double deduction;

    private Double total;

    private String status;

    private String createTime;
}
