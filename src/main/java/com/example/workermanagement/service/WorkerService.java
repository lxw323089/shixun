package com.example.workermanagement.service;

import com.example.workermanagement.common.PageResult;
import com.example.workermanagement.common.TimeUtil;
import com.example.workermanagement.entity.Worker;
import com.example.workermanagement.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerMapper workerMapper;

    public PageResult<Worker> getWorkers(int page, int pageSize, String name, Long departmentId, String status) {
        int offset = (page - 1) * pageSize;
        List<Worker> list = workerMapper.selectPage(offset, pageSize, name, departmentId, status);
        long total = workerMapper.count(name, departmentId, status);
        return new PageResult<>(list, total, page, pageSize);
    }

    public List<Worker> getAllWorkers() {
        return workerMapper.selectAll();
    }

    public Optional<Worker> getWorkerById(Long id) {
        return Optional.ofNullable(workerMapper.selectById(id));
    }

    public Worker createWorker(Worker worker) {
        String now = TimeUtil.now();
        if (worker.getCreateTime() == null) {
            worker.setCreateTime(now);
        }
        worker.setUpdateTime(now);
        workerMapper.insert(worker);
        return worker;
    }

    public Worker updateWorker(Long id, Worker worker) {
        Worker existing = getWorkerById(id)
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
        existing.setUpdateTime(TimeUtil.now());

        workerMapper.update(existing);
        return existing;
    }

    public void deleteWorker(Long id) {
        if (workerMapper.selectById(id) == null) {
            throw new RuntimeException("员工不存在");
        }
        workerMapper.deleteById(id);
    }
}
