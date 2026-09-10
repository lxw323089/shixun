package com.example.workermanagement.config;

import com.example.workermanagement.entity.*;
import com.example.workermanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final DepartmentRepository departmentRepository;
    private final WorkerRepository workerRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryRepository salaryRepository;
    private final UserRepository userRepository;
    private final OperationLogRepository operationLogRepository;
    
    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return;
        }
        
        initDepartments();
        initWorkers();
        initAttendances();
        initSalaries();
        initUsers();
        initOperationLogs();
    }
    
    private void initDepartments() {
        Department d1 = new Department();
        d1.setName("生产部");
        d1.setDescription("负责产品生产制造");
        d1.setCreateTime("2024-01-01 09:00:00");
        d1.setUpdateTime("2024-01-01 09:00:00");
        
        Department d2 = new Department();
        d2.setName("技术部");
        d2.setDescription("负责技术研发和支持");
        d2.setCreateTime("2024-01-01 09:00:00");
        d2.setUpdateTime("2024-01-01 09:00:00");
        
        Department d3 = new Department();
        d3.setName("财务部");
        d3.setDescription("负责财务核算和管理");
        d3.setCreateTime("2024-01-01 09:00:00");
        d3.setUpdateTime("2024-01-01 09:00:00");
        
        Department d4 = new Department();
        d4.setName("人事部");
        d4.setDescription("负责人员招聘和管理");
        d4.setCreateTime("2024-01-01 09:00:00");
        d4.setUpdateTime("2024-01-01 09:00:00");
        
        Department d5 = new Department();
        d5.setName("质检部");
        d5.setDescription("负责产品质量检验");
        d5.setCreateTime("2024-01-15 10:00:00");
        d5.setUpdateTime("2024-01-15 10:00:00");
        
        departmentRepository.saveAll(Arrays.asList(d1, d2, d3, d4, d5));
    }
    
    private void initWorkers() {
        Worker w1 = createWorker(1L, "生产部", "张三", "男", "13800138001", "110101199001011234", "2023-03-15", "生产组长", 6500.0, "在职");
        Worker w2 = createWorker(1L, "生产部", "李四", "男", "13800138002", "110101199102022345", "2023-04-20", "操作工", 5000.0, "在职");
        Worker w3 = createWorker(2L, "技术部", "王五", "男", "13800138003", "110101198903033456", "2022-06-10", "技术工程师", 8000.0, "在职");
        Worker w4 = createWorker(2L, "技术部", "赵六", "女", "13800138004", "110101199204044567", "2023-08-01", "技术员", 6000.0, "在职");
        Worker w5 = createWorker(3L, "财务部", "钱七", "女", "13800138005", "110101198805055678", "2021-09-15", "财务主管", 7500.0, "在职");
        Worker w6 = createWorker(3L, "财务部", "孙八", "女", "13800138006", "110101199306066789", "2023-02-28", "会计", 5500.0, "在职");
        Worker w7 = createWorker(4L, "人事部", "周九", "男", "13800138007", "110101199007077890", "2022-11-20", "人事专员", 5200.0, "在职");
        Worker w8 = createWorker(5L, "质检部", "吴十", "男", "13800138008", "110101199108088901", "2023-05-10", "质检员", 4800.0, "在职");
        Worker w9 = createWorker(1L, "生产部", "郑十一", "男", "13800138009", "110101199409099012", "2024-01-10", "操作工", 5000.0, "在职");
        w9.setCreateTime("2024-01-10 09:00:00");
        w9.setUpdateTime("2024-01-10 09:00:00");
        
        Worker w10 = createWorker(2L, "技术部", "冯十二", "女", "13800138010", "110101199510100123", "2024-02-01", "技术员", 5800.0, "离职");
        w10.setCreateTime("2024-02-01 09:00:00");
        w10.setUpdateTime("2024-06-15 09:00:00");
        
        workerRepository.saveAll(Arrays.asList(w1, w2, w3, w4, w5, w6, w7, w8, w9, w10));
    }
    
    private Worker createWorker(Long deptId, String deptName, String name, String gender, String phone, 
                                 String idCard, String entryDate, String position, Double baseSalary, String status) {
        Worker w = new Worker();
        w.setDepartmentId(deptId);
        w.setDepartmentName(deptName);
        w.setName(name);
        w.setGender(gender);
        w.setPhone(phone);
        w.setIdCard(idCard);
        w.setEntryDate(entryDate);
        w.setPosition(position);
        w.setBaseSalary(baseSalary);
        w.setStatus(status);
        w.setCreateTime("2024-01-01 09:00:00");
        w.setUpdateTime("2024-01-01 09:00:00");
        return w;
    }
    
    private void initAttendances() {
        Attendance a1 = createAttendance(1L, "张三", "生产部", "2024-09-01", "正常", "08:00:00", "17:30:00", "");
        Attendance a2 = createAttendance(2L, "李四", "生产部", "2024-09-01", "迟到", "08:35:00", "17:30:00", "地铁延误");
        Attendance a3 = createAttendance(3L, "王五", "技术部", "2024-09-01", "正常", "07:55:00", "18:00:00", "加班半小时");
        Attendance a4 = createAttendance(4L, "赵六", "技术部", "2024-09-01", "正常", "08:05:00", "17:35:00", "");
        Attendance a5 = createAttendance(5L, "钱七", "财务部", "2024-09-01", "正常", "08:00:00", "17:30:00", "");
        Attendance a6 = createAttendance(1L, "张三", "生产部", "2024-09-02", "正常", "07:58:00", "17:32:00", "");
        Attendance a7 = createAttendance(2L, "李四", "生产部", "2024-09-02", "缺勤", "", "", "事假");
        Attendance a8 = createAttendance(6L, "孙八", "财务部", "2024-09-01", "早退", "08:00:00", "16:45:00", "家中有事");
        Attendance a9 = createAttendance(7L, "周九", "人事部", "2024-09-01", "正常", "07:50:00", "17:40:00", "");
        Attendance a10 = createAttendance(8L, "吴十", "质检部", "2024-09-01", "正常", "08:02:00", "17:30:00", "");
        
        attendanceRepository.saveAll(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10));
    }
    
    private Attendance createAttendance(Long workerId, String workerName, String deptName, String date,
                                         String status, String checkIn, String checkOut, String remark) {
        Attendance a = new Attendance();
        a.setWorkerId(workerId);
        a.setWorkerName(workerName);
        a.setDepartmentName(deptName);
        a.setDate(date);
        a.setStatus(status);
        a.setCheckIn(checkIn);
        a.setCheckOut(checkOut);
        a.setRemark(remark);
        a.setCreateTime("2024-09-01 17:30:00");
        return a;
    }
    
    private void initSalaries() {
        Salary s1 = createSalary(1L, "张三", "生产部", "2024-08", 6500.0, 800.0, 200.0, "已发放");
        Salary s2 = createSalary(2L, "李四", "生产部", "2024-08", 5000.0, 300.0, 150.0, "已发放");
        Salary s3 = createSalary(3L, "王五", "技术部", "2024-08", 8000.0, 1200.0, 300.0, "已发放");
        Salary s4 = createSalary(4L, "赵六", "技术部", "2024-08", 6000.0, 500.0, 100.0, "已发放");
        Salary s5 = createSalary(5L, "钱七", "财务部", "2024-08", 7500.0, 1000.0, 250.0, "已发放");
        Salary s6 = createSalary(6L, "孙八", "财务部", "2024-08", 5500.0, 400.0, 180.0, "已发放");
        Salary s7 = createSalary(7L, "周九", "人事部", "2024-08", 5200.0, 350.0, 120.0, "已发放");
        Salary s8 = createSalary(8L, "吴十", "质检部", "2024-08", 4800.0, 200.0, 80.0, "已发放");
        
        Salary s9 = createSalary(1L, "张三", "生产部", "2024-09", 6500.0, 0.0, 0.0, "待发放");
        Salary s10 = createSalary(2L, "李四", "生产部", "2024-09", 5000.0, 0.0, 0.0, "待发放");
        s9.setCreateTime("2024-09-05 10:00:00");
        s10.setCreateTime("2024-09-05 10:00:00");
        
        salaryRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10));
    }
    
    private Salary createSalary(Long workerId, String workerName, String deptName, String month,
                                 Double baseSalary, Double bonus, Double deduction, String status) {
        Salary s = new Salary();
        s.setWorkerId(workerId);
        s.setWorkerName(workerName);
        s.setDepartmentName(deptName);
        s.setMonth(month);
        s.setBaseSalary(baseSalary);
        s.setBonus(bonus);
        s.setDeduction(deduction);
        s.setTotal(baseSalary + bonus - deduction);
        s.setStatus(status);
        s.setCreateTime("2024-08-31 10:00:00");
        return s;
    }
    
    private void initUsers() {
        User u1 = new User();
        u1.setUsername("admin");
        u1.setPassword("admin123");
        u1.setRole("admin");
        u1.setWorkerId(null);
        u1.setStatus("启用");
        u1.setCreateTime("2024-01-01 09:00:00");
        
        User u2 = new User();
        u2.setUsername("zhangsan");
        u2.setPassword("123456");
        u2.setRole("worker");
        u2.setWorkerId(1L);
        u2.setWorkerName("张三");
        u2.setStatus("启用");
        u2.setCreateTime("2024-01-01 09:00:00");
        
        User u3 = new User();
        u3.setUsername("lisi");
        u3.setPassword("123456");
        u3.setRole("worker");
        u3.setWorkerId(2L);
        u3.setWorkerName("李四");
        u3.setStatus("启用");
        u3.setCreateTime("2024-01-01 09:00:00");
        
        User u4 = new User();
        u4.setUsername("wangwu");
        u4.setPassword("123456");
        u4.setRole("worker");
        u4.setWorkerId(3L);
        u4.setWorkerName("王五");
        u4.setStatus("启用");
        u4.setCreateTime("2024-01-01 09:00:00");
        
        User u5 = new User();
        u5.setUsername("zhaoliu");
        u5.setPassword("123456");
        u5.setRole("worker");
        u5.setWorkerId(4L);
        u5.setWorkerName("赵六");
        u5.setStatus("启用");
        u5.setCreateTime("2024-01-01 09:00:00");
        
        User u6 = new User();
        u6.setUsername("qianqi");
        u6.setPassword("123456");
        u6.setRole("worker");
        u6.setWorkerId(5L);
        u6.setWorkerName("钱七");
        u6.setStatus("启用");
        u6.setCreateTime("2024-01-01 09:00:00");
        
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4, u5, u6));
    }
    
    private void initOperationLogs() {
        OperationLog l1 = createLog(1L, "admin", "部门管理", "新增", "新增部门：质检部", "2024-09-01 09:30:00");
        OperationLog l2 = createLog(1L, "admin", "员工管理", "新增", "新增员工：郑十一", "2024-09-01 10:15:00");
        OperationLog l3 = createLog(1L, "admin", "员工管理", "修改", "修改员工信息：张三", "2024-09-01 11:00:00");
        OperationLog l4 = createLog(1L, "admin", "考勤管理", "录入", "录入9月1日考勤数据", "2024-09-01 18:00:00");
        OperationLog l5 = createLog(1L, "admin", "工资管理", "核算", "核算8月工资", "2024-08-31 14:00:00");
        OperationLog l6 = createLog(1L, "admin", "工资管理", "发放", "发放8月工资", "2024-08-31 16:00:00");
        OperationLog l7 = createLog(1L, "admin", "系统用户", "新增", "新增用户：zhangsan", "2024-01-01 09:00:00");
        OperationLog l8 = createLog(1L, "admin", "系统用户", "权限分配", "分配管理员权限给admin", "2024-01-01 09:05:00");
        OperationLog l9 = createLog(1L, "admin", "员工管理", "删除", "删除员工：冯十二", "2024-06-15 09:30:00");
        OperationLog l10 = createLog(1L, "admin", "部门管理", "修改", "修改部门描述：技术部", "2024-07-10 10:20:00");
        
        operationLogRepository.saveAll(Arrays.asList(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10));
    }
    
    private OperationLog createLog(Long userId, String username, String module, String operation,
                                    String content, String createTime) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setModule(module);
        log.setOperation(operation);
        log.setContent(content);
        log.setCreateTime(createTime);
        return log;
    }
}
