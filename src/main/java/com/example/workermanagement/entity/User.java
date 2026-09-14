package com.example.workermanagement.entity;

import lombok.Data;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private String role;

    private String nickname;

    private String avatar;

    private Long workerId;

    private String workerName;

    private String status;

    private String createTime;
}
