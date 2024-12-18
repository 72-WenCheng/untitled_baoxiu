package main.java.entity;

import java.util.Date;

public class WorkOrder {
    private Integer id;
    private Integer repairOrderId;
    private Integer maintenanceId;
    private Date assignTime;
    private String status;
    private String faultAnalysis;
    private String repairProcess;
    private String repairResult;
    private String maintenanceName;
    private RepairOrder repairOrder;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRepairOrderId() { return repairOrderId; }
    public void setRepairOrderId(Integer repairOrderId) { this.repairOrderId = repairOrderId; }
    public Integer getMaintenanceId() { return maintenanceId; }
    public void setMaintenanceId(Integer maintenanceId) { this.maintenanceId = maintenanceId; }
    public Date getAssignTime() { return assignTime; }
    public void setAssignTime(Date assignTime) { this.assignTime = assignTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFaultAnalysis() { return faultAnalysis; }
    public void setFaultAnalysis(String faultAnalysis) { this.faultAnalysis = faultAnalysis; }
    public String getRepairProcess() { return repairProcess; }
    public void setRepairProcess(String repairProcess) { this.repairProcess = repairProcess; }
    public String getRepairResult() { return repairResult; }
    public void setRepairResult(String repairResult) { this.repairResult = repairResult; }
    public String getMaintenanceName() { return maintenanceName; }
    public void setMaintenanceName(String maintenanceName) { this.maintenanceName = maintenanceName; }
    public RepairOrder getRepairOrder() { return repairOrder; }
    public void setRepairOrder(RepairOrder repairOrder) { this.repairOrder = repairOrder; }
}