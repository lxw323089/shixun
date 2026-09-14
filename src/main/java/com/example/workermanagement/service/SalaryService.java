package com.example.workermanagement.service;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.TimeUtil;
import com.example.workermanagement.entity.Salary;
import com.example.workermanagement.mapper.SalaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryMapper salaryMapper;

    public PageResult<Salary> getSalaries(int page, int pageSize, String workerName, String month, String status) {
        int offset = (page - 1) * pageSize;
        List<Salary> list = salaryMapper.selectPage(offset, pageSize, workerName, month, status);
        long total = salaryMapper.count(workerName, month, status);
        return new PageResult<>(list, total, page, pageSize);
    }

    public List<Salary> getSalariesByWorkerId(Long workerId) {
        return salaryMapper.selectByWorkerId(workerId);
    }

    public Optional<Salary> getSalaryById(Long id) {
        return Optional.ofNullable(salaryMapper.selectById(id));
    }

    public Salary createSalary(Salary salary) {
        if (salary.getTotal() == null) {
            double total = (salary.getBaseSalary() != null ? salary.getBaseSalary() : 0)
                    + (salary.getBonus() != null ? salary.getBonus() : 0)
                    - (salary.getDeduction() != null ? salary.getDeduction() : 0);
            salary.setTotal(total);
        }
        if (salary.getCreateTime() == null) {
            salary.setCreateTime(TimeUtil.now());
        }
        salaryMapper.insert(salary);
        return salary;
    }

    public Salary updateSalary(Long id, Salary salary) {
        Salary existing = getSalaryById(id)
                .orElseThrow(() -> new RuntimeException("工资记录不存在"));

        existing.setWorkerId(salary.getWorkerId());
        existing.setWorkerName(salary.getWorkerName());
        existing.setDepartmentName(salary.getDepartmentName());
        existing.setMonth(salary.getMonth());
        existing.setBaseSalary(salary.getBaseSalary());
        existing.setBonus(salary.getBonus());
        existing.setDeduction(salary.getDeduction());

        double total = (salary.getBaseSalary() != null ? salary.getBaseSalary() : 0)
                + (salary.getBonus() != null ? salary.getBonus() : 0)
                - (salary.getDeduction() != null ? salary.getDeduction() : 0);
        existing.setTotal(total);

        existing.setStatus(salary.getStatus());

        salaryMapper.update(existing);
        return existing;
    }

    public Salary paySalary(Long id) {
        Salary existing = getSalaryById(id)
                .orElseThrow(() -> new RuntimeException("工资记录不存在"));
        existing.setStatus("已发放");
        salaryMapper.update(existing);
        return existing;
    }

    public void deleteSalary(Long id) {
        if (salaryMapper.selectById(id) == null) {
            throw new RuntimeException("工资记录不存在");
        }
        salaryMapper.deleteById(id);
    }
}
