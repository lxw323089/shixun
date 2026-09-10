package com.example.workermanagement.service;

import com.example.workermanagement.entity.Worker;
import com.example.workermanagement.repository.WorkerRepository;
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
public class WorkerService {
    
    private final WorkerRepository workerRepository;
    
    public Page<Worker> getWorkers(int page, int pageSize, String name, Long departmentId, String status) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        
        Specification<Worker> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null && !name.isEmpty()) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            if (departmentId != null) {
                predicates.add(cb.equal(root.get("departmentId"), departmentId));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return workerRepository.findAll(spec, pageable);
    }
    
    public List<Worker> getAllWorkers() {
        return workerRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
    
    public Optional<Worker> getWorkerById(Long id) {
        return workerRepository.findById(id);
    }
    
    public Worker createWorker(Worker worker) {
        return workerRepository.save(worker);
    }
    
    public Worker updateWorker(Long id, Worker worker) {
        Worker existing = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在"));
        
        existing.setDepartmentId(worker.getDepartmentId());
        existing.setDepartmentName(worker.getDepartmentName());
        existing.setName(worker.getName());
        existing.setGender(worker.getGender());
        existing.setPhone(worker.getPhone());
        existing.setIdCard(worker.getIdCard());
        existing.setEntryDate(worker.getEntryDate());
        existing.setPosition(worker.getPosition());
        existing.setBaseSalary(worker.getBaseSalary());
        existing.setStatus(worker.getStatus());
        
        return workerRepository.save(existing);
    }
    
    public void deleteWorker(Long id) {
        if (!workerRepository.existsById(id)) {
            throw new RuntimeException("员工不存在");
        }
        workerRepository.deleteById(id);
    }
    
    public long countByStatus(String status) {
        return workerRepository.countByStatus(status);
    }
}
