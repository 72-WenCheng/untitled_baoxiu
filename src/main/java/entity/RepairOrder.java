package main.java.entity;

import java.util.Date;

public class RepairOrder {
    private Integer id;
    private String buildingNo;
    private String roomNo;
    private String description;
    private Integer reporterId;
    private Date reportTime;
    private String contactPhone;
    private String status;
    private String imagePath;
    private String reporterName;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getBuildingNo() {
        return buildingNo;
    }
    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }
    public String getRoomNo() {
        return roomNo;
    }
    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getReporterId() {
        return reporterId;
    }
    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }
    public Date getReportTime() {
        return reportTime;
    }
    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }
    public String getContactPhone() {
        return contactPhone;
    }
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getReporterName() {
        return reporterName;
    }
    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }
}