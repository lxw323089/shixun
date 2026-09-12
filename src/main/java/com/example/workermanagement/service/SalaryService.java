package com.example.workermanagement.service;

import com.example.workermanagement.entity.Salary;
import com.example.workermanagement.repository.SalaryRepository;
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
public class SalaryService {
    
    private final SalaryRepository salaryRepository;
    
    public Page<Salary> getSalaries(int page, int pageSize, String workerName, String month, String status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<Salary> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (workerName != null && !workerName.isEmpty()) {
                predicates.add(cb.like(root.get("workerName"), "%" + workerName + "%"));
            }
            if (month != null && !month.isEmpty()) {
                predicates.add(cb.equal(root.get("month"), month));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return salaryRepository.findAll(spec, pageable);
    }
    
    public List<Salary> getSalariesByWorkerId(Long workerId) {
        return salaryRepository.findByWorkerId(workerId);
    }

    public Optional<Salary> getSalaryById(Long id) {
        return salaryRepository.findById(id);
    }
    
    public Salary createSalary(Salary salary) {
        if (salary.getTotal() == null) {
            double total = (salary.getBaseSalary() != null ? salary.getBaseSalary() : 0)
                    + (salary.getBonus() != null ? salary.getBonus() : 0)
                    - (salary.getDeduction() != null ? salary.getDeduction() : 0);
            salary.setTotal(total);
        }
        return salaryRepository.save(salary);
    }
    
    public Salary updateSalary(Long id, Salary salary) {
        Salary existing = salaryRepository.findById(id)
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
        
        return salaryRepository.save(existing);
    }
    
    public Salary paySalary(Long id) {
        Salary existing = salaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工资记录不存在"));
        existing.setStatus("已发放");
        return salaryRepository.save(existing);
    }
    
    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new RuntimeException("工资记录不存在");
        }
        salaryRepository.deleteById(id);
    }
}
