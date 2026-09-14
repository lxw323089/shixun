package com.example.workermanagement.entity;

import lombok.Data;

@Data
public class Worker {

    private Long id;

    private Long departmentId;

    private String departmentName;

    private String name;

    private String gender;

    private String phone;

    private String idCard;

    private String entryDate;

    private String position;

    private Double baseSalary;

    private String status;

    private String createTime;

    private String updateTime;
}
