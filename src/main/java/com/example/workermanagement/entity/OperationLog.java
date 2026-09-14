package com.example.workermanagement.entity;

import lombok.Data;

@Data
public class OperationLog {

    private Long id;

    private Long userId;

    private String username;

    private String module;

    private String operation;

    private String content;

    private String createTime;
}
