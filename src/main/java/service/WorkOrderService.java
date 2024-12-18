package main.java.service;

import main.java.entity.RepairOrder;
import main.java.entity.WorkOrder;
import main.java.mapper.RepairOrderMapper;
import main.java.mapper.WorkOrderMapper;
import main.java.util.Constants;
import main.java.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import java.util.Date;
import java.util.List;

import static main.java.service.UserService.getReadableErrorMessage;

public class WorkOrderService {
    public void assignWork(Integer repairOrderId, Integer maintenanceId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            try{
            WorkOrderMapper workMapper = session.getMapper(WorkOrderMapper.class);
            RepairOrderMapper repairMapper = session.getMapper(RepairOrderMapper.class);

            // 创建工单
            WorkOrder workOrder = new WorkOrder();
            workOrder.setRepairOrderId(repairOrderId);
            workOrder.setMaintenanceId(maintenanceId);
            workOrder.setAssignTime(new Date());
            workOrder.setStatus(Constants.WORK_STATUS_PENDING);
            workMapper.insert(workOrder);

            // 更新报修单状态
            RepairOrder repairOrder = repairMapper.getById(repairOrderId);
            repairOrder.setStatus(Constants.STATUS_ASSIGNED);
            repairMapper.update(repairOrder);

            session.commit();
            } catch (Exception e) {
                session.rollback();
                throw new RuntimeException("派工失败：" + getReadableErrorMessage(e));
            }
        }
    }

    public void completeWork(Integer workOrderId, String faultAnalysis,
                           String repairProcess, String repairResult) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            WorkOrderMapper workMapper = session.getMapper(WorkOrderMapper.class);
            RepairOrderMapper repairMapper = session.getMapper(RepairOrderMapper.class);

            // 更新工单
            WorkOrder workOrder = workMapper.getById(workOrderId);
            workOrder.setFaultAnalysis(faultAnalysis);
            workOrder.setRepairProcess(repairProcess);
            workOrder.setRepairResult(repairResult);
            workOrder.setStatus(Constants.WORK_STATUS_COMPLETED);
            workMapper.update(workOrder);

            // 更新报修单状态
            RepairOrder repairOrder = repairMapper.getById(workOrder.getRepairOrderId());
            repairOrder.setStatus(Constants.STATUS_COMPLETED);
            repairMapper.update(repairOrder);

            session.commit();
        }
    }

    public List<WorkOrder> getByMaintenanceId(Integer maintenanceId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            WorkOrderMapper mapper = session.getMapper(WorkOrderMapper.class);
            return mapper.getByMaintenanceId(maintenanceId);
        }
    }


    public WorkOrder getByRepairOrderId(Integer repairOrderId) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            WorkOrderMapper mapper = session.getMapper(WorkOrderMapper.class);
            List<WorkOrder> orders = mapper.getByRepairOrderId(repairOrderId);
            if (orders != null && !orders.isEmpty()) {
                // 返回最新的工单
                return orders.get(0);
            }
            return null;
        }
    }

    public WorkOrder getById(Integer id) {
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            WorkOrderMapper mapper = session.getMapper(WorkOrderMapper.class);
            return mapper.getById(id);
        }
    }
}