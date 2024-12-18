-- 插入初始用户数据
INSERT INTO users (username, password, role, phone) VALUES
('admin', '111', 'admin', '13977531577'),           -- 超级管理员账号
('student01', '111', 'student', '13877551369'),    -- 学生账号
('maintenance01', '111', 'maintenance', '18599635035'),  -- 维修员账号
('student02', '111', 'student', '17756998522'),    -- 学生账号
('maintenance02', '111', 'maintenance', '19856998982');  -- 维修员账号

-- 插入一些测试报修单数据
INSERT INTO repair_orders (building_no, room_no, description, reporter_id, report_time, contact_phone, status) VALUES
('A栋', '101', '空调不制冷', 2, NOW(), '13877551369', '待受理'),
('B栋', '202', '水管漏水', 4, NOW(), '17756998522', '草稿'),
('C栋', '103', '床板坏了', 2, NOW(), '13877551369', '待受理'),
('D栋', '203', '网口连接不良', 4, NOW(), '17756998522', '草稿'),
('E栋', '105', '宿舍门拉条坏了', 2, NOW(), '13877551369', '待受理'),
('F栋', '206', '冲水阀烂掉', 4, NOW(), '17756998522', '草稿');