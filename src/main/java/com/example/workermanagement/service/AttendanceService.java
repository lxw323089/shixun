package com.example.workermanagement.service;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.TimeUtil;
import com.example.workermanagement.entity.Attendance;
import com.example.workermanagement.mapper.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;

    public PageResult<Attendance> getAttendances(int page, int pageSize, String workerName, String date, String status) {
        int offset = (page - 1) * pageSize;
        List<Attendance> list = attendanceMapper.selectPage(offset, pageSize, workerName, date, status);
        long total = attendanceMapper.count(workerName, date, status);
        return new PageResult<>(list, total, page, pageSize);
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return Optional.ofNullable(attendanceMapper.selectById(id));
    }

    public Attendance createAttendance(Attendance attendance) {
        if (attendance.getCreateTime() == null) {
            attendance.setCreateTime(TimeUtil.now());
        }
        attendanceMapper.insert(attendance);
        return attendance;
    }

    public Attendance updateAttendance(Long id, Attendance attendance) {
        Attendance existing = getAttendanceById(id)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在"));

        existing.setWorkerId(attendance.getWorkerId());
        existing.setWorkerName(attendance.getWorkerName());
        existing.setDepartmentName(attendance.getDepartmentName());
        existing.setDate(attendance.getDate());
        existing.setStatus(attendance.getStatus());
        existing.setCheckIn(attendance.getCheckIn());
        existing.setCheckOut(attendance.getCheckOut());
        existing.setRemark(attendance.getRemark());

        attendanceMapper.update(existing);
        return existing;
    }

    public void deleteAttendance(Long id) {
        if (attendanceMapper.selectById(id) == null) {
            throw new RuntimeException("考勤记录不存在");
        }
        attendanceMapper.deleteById(id);
    }
}
