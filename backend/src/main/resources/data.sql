INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '新生报到', '包含报到须知、材料清单与流程说明') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '新生报到') LIMIT 1;

INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '课程安排', '展示学期课程表与时间地点') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '课程安排') LIMIT 1;

INSERT INTO record_entity (name, description)
SELECT * FROM (SELECT '活动通知', '校园活动与社团招新信息') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM record_entity WHERE name = '活动通知') LIMIT 1;

INSERT INTO waste_bucket (bucket_no, category, weight, has_special_ingredients, special_ingredients_remark, experiment_name, experiment_time, lab_room, student_id, student_name, student_contact, qr_code, status, submitted_at, created_at, updated_at)
SELECT * FROM (SELECT 'WB-2025-001', 'ACID', 5.50, false, NULL, '酸碱滴定实验', '2025-06-01 14:30:00', '化学楼A-301', '2023001', '张三', '13800000001', 'QR-WB-2025-001', 'SAFETY_CHECKED', '2025-06-01 16:00:00', '2025-06-01 16:00:00', '2025-06-01 17:30:00') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM waste_bucket WHERE bucket_no = 'WB-2025-001') LIMIT 1;

UPDATE waste_bucket SET safety_officer_id = 'SAFE001', safety_officer_name = '李安全', safety_checked_at = '2025-06-01 17:30:00', safety_remark = '检查通过，分类正确'
WHERE bucket_no = 'WB-2025-001' AND safety_officer_id IS NULL;

INSERT INTO waste_bucket (bucket_no, category, weight, has_special_ingredients, special_ingredients_remark, experiment_name, experiment_time, lab_room, student_id, student_name, student_contact, qr_code, status, submitted_at, safety_officer_id, safety_officer_name, safety_checked_at, safety_remark, category_correction_remark, created_at, updated_at)
SELECT * FROM (SELECT 'WB-2025-002', 'ORGANIC', 3.20, true, '含有少量苯系物', '有机合成实验', '2025-06-02 10:00:00', '化学楼B-205', '2023002', '李四', '13800000002', 'QR-WB-2025-002', 'IN_STORAGE', '2025-06-02 12:00:00', 'SAFE001', '李安全', '2025-06-02 13:00:00', '原分类为酸性废液，经检查修正为有机废液', '学生误将有机废液归类为酸性，已纠正', '2025-06-02 12:00:00', '2025-06-02 14:00:00') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM waste_bucket WHERE bucket_no = 'WB-2025-002') LIMIT 1;

UPDATE waste_bucket SET storage_operator_id = 'STOR001', storage_operator_name = '王保管', stored_at = '2025-06-02 14:00:00'
WHERE bucket_no = 'WB-2025-002' AND storage_operator_id IS NULL;

INSERT INTO waste_bucket (bucket_no, category, weight, has_special_ingredients, special_ingredients_remark, experiment_name, experiment_time, lab_room, student_id, student_name, student_contact, qr_code, status, submitted_at, created_at, updated_at)
SELECT * FROM (SELECT 'WB-2025-003', 'HEAVY_METAL', 2.80, true, '含铜离子和铬离子', '重金属检测实验', '2025-06-03 09:00:00', '环境楼C-102', '2023003', '王五', '13800000003', 'QR-WB-2025-003', 'SUBMITTED', '2025-06-03 11:30:00', '2025-06-03 11:30:00', '2025-06-03 11:30:00') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM waste_bucket WHERE bucket_no = 'WB-2025-003') LIMIT 1;

INSERT INTO waste_bucket (bucket_no, category, weight, has_special_ingredients, special_ingredients_remark, experiment_name, experiment_time, lab_room, student_id, student_name, student_contact, qr_code, status, submitted_at, safety_officer_id, safety_officer_name, safety_checked_at, storage_operator_id, storage_operator_name, stored_at, recycler_id, recycler_name, collected_at, created_at, updated_at)
SELECT * FROM (SELECT 'WB-2025-004', 'ALKALI', 4.00, false, NULL, '中和反应实验', '2025-05-20 15:00:00', '化学楼A-301', '2022001', '赵六', '13800000004', 'QR-WB-2025-004', 'COLLECTED', '2025-05-20 17:00:00', 'SAFE002', '钱安全', '2025-05-20 18:00:00', 'STOR001', '王保管', '2025-05-21 09:00:00', 'REC001', '孙回收', '2025-06-05 10:00:00', '2025-05-20 17:00:00', '2025-06-05 10:00:00') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM waste_bucket WHERE bucket_no = 'WB-2025-004') LIMIT 1;
