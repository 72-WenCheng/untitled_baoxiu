package main.java.mapper;

import main.java.entity.RepairOrder;

import java.util.List;

public interface RepairOrderMapper {
    void insert(RepairOrder order);
    void update(RepairOrder order);
    RepairOrder getById(Integer id);
    List<RepairOrder> getByReporterId(Integer reporterId);
    List<RepairOrder> getByStatus(String status);
    List<RepairOrder> getAllOrderByTime();
} 