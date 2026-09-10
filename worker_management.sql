-- ============================================================
-- 员工管理系统 数据库脚本
-- 数据库: worker_management
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `worker_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `worker_management`;

-- -----------------------------------------------------------
-- 1. 部门表 departments
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `departments`;
CREATE TABLE `departments` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `name`         VARCHAR(100) NOT NULL COMMENT '部门名称',
  `description`  VARCHAR(255) DEFAULT NULL COMMENT '部门描述',
  `create_time`  VARCHAR(20)  DEFAULT NULL COMMENT '创建时间',
  `update_time`  VARCHAR(20)  DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- -----------------------------------------------------------
-- 2. 员工表 workers
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `workers`;
CREATE TABLE `workers` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `department_id`   BIGINT       DEFAULT NULL COMMENT '部门ID',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
  `name`            VARCHAR(50)  NOT NULL COMMENT '姓名',
  `gender`          VARCHAR(10)  DEFAULT NULL COMMENT '性别',
  `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `id_card`         VARCHAR(20)  DEFAULT NULL COMMENT '身份证号',
  `entry_date`      VARCHAR(20)  DEFAULT NULL COMMENT '入职日期',
  `position`        VARCHAR(50)  DEFAULT NULL COMMENT '职位',
  `base_salary`     DOUBLE       DEFAULT NULL COMMENT '基本工资',
  `status`          VARCHAR(20)  DEFAULT NULL COMMENT '状态',
  `create_time`     VARCHAR(20)  DEFAULT NULL COMMENT '创建时间',
  `update_time`     VARCHAR(20)  DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- -----------------------------------------------------------
-- 3. 考勤表 attendances
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `attendances`;
CREATE TABLE `attendances` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '考勤ID',
  `worker_id`       BIGINT       DEFAULT NULL COMMENT '员工ID',
  `worker_name`     VARCHAR(50)  DEFAULT NULL COMMENT '员工姓名',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
  `date`            VARCHAR(20)  DEFAULT NULL COMMENT '考勤日期',
  `status`          VARCHAR(20)  DEFAULT NULL COMMENT '考勤状态',
  `check_in`        VARCHAR(20)  DEFAULT NULL COMMENT '签到时间',
  `check_out`       VARCHAR(20)  DEFAULT NULL COMMENT '签退时间',
  `remark`          VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time`     VARCHAR(20)  DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- -----------------------------------------------------------
-- 4. 工资表 salaries
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `salaries`;
CREATE TABLE `salaries` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '工资ID',
  `worker_id`       BIGINT       DEFAULT NULL COMMENT '员工ID',
  `worker_name`     VARCHAR(50)  DEFAULT NULL COMMENT '员工姓名',
  `department_name` VARCHAR(100) DEFAULT NULL COMMENT '部门名称',
  `month`           VARCHAR(10)  DEFAULT NULL COMMENT '工资月份',
  `base_salary`     DOUBLE       DEFAULT NULL COMMENT '基本工资',
  `bonus`           DOUBLE       DEFAULT NULL COMMENT '奖金',
  `deduction`       DOUBLE       DEFAULT NULL COMMENT '扣款',
  `total`           DOUBLE       DEFAULT NULL COMMENT '实发工资',
  `status`          VARCHAR(20)  DEFAULT NULL COMMENT '状态',
  `create_time`     VARCHAR(20)  DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工资表';

-- -----------------------------------------------------------
-- 5. 用户表 users
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
  `password`    VARCHAR(100) NOT NULL COMMENT '密码',
  `role`        VARCHAR(20)  DEFAULT NULL COMMENT '角色',
  `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
  `worker_id`   BIGINT       DEFAULT NULL COMMENT '关联员工ID',
  `worker_name` VARCHAR(50)  DEFAULT NULL COMMENT '关联员工姓名',
  `status`      VARCHAR(20)  DEFAULT NULL COMMENT '状态',
  `create_time` VARCHAR(20)  DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------------
-- 6. 操作日志表 operation_logs
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `operation_logs`;
CREATE TABLE `operation_logs` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id`     BIGINT       DEFAULT NULL COMMENT '操作用户ID',
  `username`    VARCHAR(50)  DEFAULT NULL COMMENT '操作用户名',
  `module`      VARCHAR(50)  DEFAULT NULL COMMENT '操作模块',
  `operation`   VARCHAR(50)  DEFAULT NULL COMMENT '操作类型',
  `content`     TEXT         DEFAULT NULL COMMENT '操作内容',
  `create_time` VARCHAR(20)  DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 部门数据
INSERT INTO `departments` (`id`, `name`, `description`, `create_time`, `update_time`) VALUES
(1, '生产部', '负责产品生产制造',     '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(2, '技术部', '负责技术研发和支持',   '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(3, '财务部', '负责财务核算和管理',   '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(4, '人事部', '负责人员招聘和管理',   '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(5, '质检部', '负责产品质量检验',     '2024-01-15 10:00:00', '2024-01-15 10:00:00');

-- 员工数据
INSERT INTO `workers` (`id`, `department_id`, `department_name`, `name`, `gender`, `phone`, `id_card`, `entry_date`, `position`, `base_salary`, `status`, `create_time`, `update_time`) VALUES
(1,  1, '生产部', '张三',   '男', '13800138001', '110101199001011234', '2023-03-15', '生产组长',  6500, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(2,  1, '生产部', '李四',   '男', '13800138002', '110101199102022345', '2023-04-20', '操作工',    5000, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(3,  2, '技术部', '王五',   '男', '13800138003', '110101198903033456', '2022-06-10', '技术工程师', 8000, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(4,  2, '技术部', '赵六',   '女', '13800138004', '110101199204044567', '2023-08-01', '技术员',    6000, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(5,  3, '财务部', '钱七',   '女', '13800138005', '110101198805055678', '2021-09-15', '财务主管',  7500, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(6,  3, '财务部', '孙八',   '女', '13800138006', '110101199306066789', '2023-02-28', '会计',      5500, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(7,  4, '人事部', '周九',   '男', '13800138007', '110101199007077890', '2022-11-20', '人事专员',  5200, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(8,  5, '质检部', '吴十',   '男', '13800138008', '110101199108088901', '2023-05-10', '质检员',    4800, '在职', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
(9,  1, '生产部', '郑十一', '男', '13800138009', '110101199409099012', '2024-01-10', '操作工',    5000, '在职', '2024-01-10 09:00:00', '2024-01-10 09:00:00'),
(10, 2, '技术部', '冯十二', '女', '13800138010', '110101199510100123', '2024-02-01', '技术员',    5800, '离职', '2024-02-01 09:00:00', '2024-06-15 09:00:00');

-- 考勤数据
INSERT INTO `attendances` (`id`, `worker_id`, `worker_name`, `department_name`, `date`, `status`, `check_in`, `check_out`, `remark`, `create_time`) VALUES
(1, 1, '张三', '生产部', '2024-09-01', '正常', '08:00:00', '17:30:00', '',           '2024-09-01 17:30:00'),
(2, 2, '李四', '生产部', '2024-09-01', '迟到', '08:35:00', '17:30:00', '地铁延误',   '2024-09-01 17:30:00'),
(3, 3, '王五', '技术部', '2024-09-01', '正常', '07:55:00', '18:00:00', '加班半小时', '2024-09-01 17:30:00'),
(4, 4, '赵六', '技术部', '2024-09-01', '正常', '08:05:00', '17:35:00', '',           '2024-09-01 17:30:00'),
(5, 5, '钱七', '财务部', '2024-09-01', '正常', '08:00:00', '17:30:00', '',           '2024-09-01 17:30:00'),
(6, 1, '张三', '生产部', '2024-09-02', '正常', '07:58:00', '17:32:00', '',           '2024-09-01 17:30:00'),
(7, 2, '李四', '生产部', '2024-09-02', '缺勤', '',         '',         '事假',       '2024-09-01 17:30:00'),
(8, 6, '孙八', '财务部', '2024-09-01', '早退', '08:00:00', '16:45:00', '家中有事',   '2024-09-01 17:30:00'),
(9, 7, '周九', '人事部', '2024-09-01', '正常', '07:50:00', '17:40:00', '',           '2024-09-01 17:30:00'),
(10,8, '吴十', '质检部', '2024-09-01', '正常', '08:02:00', '17:30:00', '',           '2024-09-01 17:30:00');

-- 工资数据
INSERT INTO `salaries` (`id`, `worker_id`, `worker_name`, `department_name`, `month`, `base_salary`, `bonus`, `deduction`, `total`, `status`, `create_time`) VALUES
(1,  1, '张三', '生产部', '2024-08', 6500, 800,  200, 7100, '已发放', '2024-08-31 10:00:00'),
(2,  2, '李四', '生产部', '2024-08', 5000, 300,  150, 5150, '已发放', '2024-08-31 10:00:00'),
(3,  3, '王五', '技术部', '2024-08', 8000, 1200, 300, 8900, '已发放', '2024-08-31 10:00:00'),
(4,  4, '赵六', '技术部', '2024-08', 6000, 500,  100, 6400, '已发放', '2024-08-31 10:00:00'),
(5,  5, '钱七', '财务部', '2024-08', 7500, 1000, 250, 8250, '已发放', '2024-08-31 10:00:00'),
(6,  6, '孙八', '财务部', '2024-08', 5500, 400,  180, 5720, '已发放', '2024-08-31 10:00:00'),
(7,  7, '周九', '人事部', '2024-08', 5200, 350,  120, 5430, '已发放', '2024-08-31 10:00:00'),
(8,  8, '吴十', '质检部', '2024-08', 4800, 200,  80,  4920, '已发放', '2024-08-31 10:00:00'),
(9,  1, '张三', '生产部', '2024-09', 6500, 0,    0,   6500, '待发放', '2024-09-05 10:00:00'),
(10, 2, '李四', '生产部', '2024-09', 5000, 0,    0,   5000, '待发放', '2024-09-05 10:00:00');

-- 用户数据
INSERT INTO `users` (`id`, `username`, `password`, `role`, `worker_id`, `worker_name`, `status`, `create_time`) VALUES
(1, 'admin',    'admin123', 'admin',  NULL, NULL,   '启用', '2024-01-01 09:00:00'),
(2, 'zhangsan', '123456',   'worker', 1,    '张三', '启用', '2024-01-01 09:00:00'),
(3, 'lisi',     '123456',   'worker', 2,    '李四', '启用', '2024-01-01 09:00:00'),
(4, 'wangwu',   '123456',   'worker', 3,    '王五', '启用', '2024-01-01 09:00:00'),
(5, 'zhaoliu',  '123456',   'worker', 4,    '赵六', '启用', '2024-01-01 09:00:00'),
(6, 'qianqi',   '123456',   'worker', 5,    '钱七', '启用', '2024-01-01 09:00:00');

-- 操作日志数据
INSERT INTO `operation_logs` (`id`, `user_id`, `username`, `module`, `operation`, `content`, `create_time`) VALUES
(1,  1, 'admin', '部门管理', '新增',     '新增部门：质检部',           '2024-09-01 09:30:00'),
(2,  1, 'admin', '员工管理', '新增',     '新增员工：郑十一',           '2024-09-01 10:15:00'),
(3,  1, 'admin', '员工管理', '修改',     '修改员工信息：张三',         '2024-09-01 11:00:00'),
(4,  1, 'admin', '考勤管理', '录入',     '录入9月1日考勤数据',         '2024-09-01 18:00:00'),
(5,  1, 'admin', '工资管理', '核算',     '核算8月工资',                '2024-08-31 14:00:00'),
(6,  1, 'admin', '工资管理', '发放',     '发放8月工资',                '2024-08-31 16:00:00'),
(7,  1, 'admin', '系统用户', '新增',     '新增用户：zhangsan',         '2024-01-01 09:00:00'),
(8,  1, 'admin', '系统用户', '权限分配', '分配管理员权限给admin',      '2024-01-01 09:05:00'),
(9,  1, 'admin', '员工管理', '删除',     '删除员工：冯十二',           '2024-06-15 09:30:00'),
(10, 1, 'admin', '部门管理', '修改',     '修改部门描述：技术部',       '2024-07-10 10:20:00');
