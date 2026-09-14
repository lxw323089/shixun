package com.example.workermanagement.entity;

import lombok.Data;

@Data
public class Attendance {

    private Long id;

    private Long workerId;

    private String workerName;

    private String departmentName;

    private String date;

    private String status;

    private String checkIn;

    private String checkOut;

    private String remark;

    private String createTime;
}
