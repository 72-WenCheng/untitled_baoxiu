package main.java.util;

public class Constants {
    // 用户角色
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MAINTENANCE = "maintenance";

    // 角色显示名称
    public static final String ROLE_NAME_STUDENT = "学生";
    public static final String ROLE_NAME_ADMIN = "管理员";
    public static final String ROLE_NAME_MAINTENANCE = "维修员";

    // 报修单状态
    public static final String STATUS_DRAFT = "草稿";         // 草稿
    public static final String STATUS_PENDING = "待受理";     // 待受理
    public static final String STATUS_ASSIGNED = "已派工";    // 已派工
    public static final String STATUS_COMPLETED = "维修结束";   // 维修结束

    // 工单状态
    public static final String WORK_STATUS_PENDING = "未处理";    // 未处理
    public static final String WORK_STATUS_COMPLETED = "已处理";  // 已处理

    // 界面相关常量
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int DIALOG_WIDTH = 400;
    public static final int DIALOG_HEIGHT = 500;

    // 图片相关常量
    public static final int IMAGE_PREVIEW_WIDTH = 200;
    public static final int IMAGE_PREVIEW_HEIGHT = 150;
    public static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};
}