package com.example.workermanagement.service;

import com.example.workermanagement.entity.Attendance;
import com.example.workermanagement.repository.AttendanceRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    
    public Page<Attendance> getAttendances(int page, int pageSize, String workerName, String date, String status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "date"));
        
        Specification<Attendance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (workerName != null && !workerName.isEmpty()) {
                predicates.add(cb.like(root.get("workerName"), "%" + workerName + "%"));
            }
            if (date != null && !date.isEmpty()) {
                predicates.add(cb.equal(root.get("date"), date));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return attendanceRepository.findAll(spec, pageable);
    }
    
    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }
    
    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
    
    public Attendance updateAttendance(Long id, Attendance attendance) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在"));
        
        existing.setWorkerId(attendance.getWorkerId());
        existing.setWorkerName(attendance.getWorkerName());
        existing.setDepartmentName(attendance.getDepartmentName());
        existing.setDate(attendance.getDate());
        existing.setStatus(attendance.getStatus());
        existing.setCheckIn(attendance.getCheckIn());
        existing.setCheckOut(attendance.getCheckOut());
        existing.setRemark(attendance.getRemark());
        
        return attendanceRepository.save(existing);
    }
    
    public void deleteAttendance(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException("考勤记录不存在");
        }
        attendanceRepository.deleteById(id);
    }
}
