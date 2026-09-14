package com.example.workermanagement.service;

import com.example.workermanagement.common.TimeUtil;
import com.example.workermanagement.entity.Department;
import com.example.workermanagement.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentMapper departmentMapper;

    public List<Department> getAllDepartments() {
        return departmentMapper.selectAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return Optional.ofNullable(departmentMapper.selectById(id));
    }

    public Department createDepartment(Department department) {
        if (departmentMapper.selectByName(department.getName()) != null) {
            throw new RuntimeException("部门名称已存在");
        }
        String now = TimeUtil.now();
        if (department.getCreateTime() == null) {
            department.setCreateTime(now);
        }
        department.setUpdateTime(now);
        departmentMapper.insert(department);
        return department;
    }

    public Department updateDepartment(Long id, Department department) {
        Department existing = getDepartmentById(id)
                .orElseThrow(() -> new RuntimeException("部门不存在"));

        if (!existing.getName().equals(department.getName())
                && departmentMapper.selectByName(department.getName()) != null) {
            throw new RuntimeException("部门名称已存在");
        }

        existing.setName(department.getName());
        existing.setDescription(department.getDescription());
        existing.setUpdateTime(TimeUtil.now());

        departmentMapper.update(existing);
        return existing;
    }

    public void deleteDepartment(Long id) {
        if (departmentMapper.selectById(id) == null) {
            throw new RuntimeException("部门不存在");
        }
        departmentMapper.deleteById(id);
    }
}
