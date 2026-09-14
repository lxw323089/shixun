package com.example.workermanagement.entity;

import lombok.Data;

@Data
public class Department {

    private Long id;

    private String name;

    private String description;

    private String createTime;

    private String updateTime;
}
