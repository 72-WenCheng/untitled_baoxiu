package main.java.mapper;

import main.java.entity.WorkOrder;

import java.util.List;

public interface WorkOrderMapper {
    void insert(WorkOrder order);
    void update(WorkOrder order);
    WorkOrder getById(Integer id);
    List<WorkOrder> getByMaintenanceId(Integer maintenanceId);
    List<WorkOrder> getByStatus(String status);
    List<WorkOrder> getByRepairOrderId(Integer repairOrderId);
}
