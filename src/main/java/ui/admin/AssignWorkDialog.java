package main.java.ui.admin;

import main.java.entity.User;
import main.java.service.UserService;
import main.java.service.WorkOrderService;
import main.java.ui.common.MessageDialog;
import main.java.util.Constants;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignWorkDialog extends JDialog {
    private Integer repairOrderId;
    private UserService userService;
    private WorkOrderService workOrderService;
    private boolean assigned = false;
    private JComboBox<UserComboItem> maintenanceCombo;
    
    public AssignWorkDialog(JFrame parent, Integer repairOrderId) {
        super(parent, "派工", true);
        this.repairOrderId = repairOrderId;
        this.userService = new UserService();
        this.workOrderService = new WorkOrderService();
        
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        loadMaintenanceStaff();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 维修员选择
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("选择维修员："), gbc);
        
        gbc.gridx = 1;
        maintenanceCombo = new JComboBox<>();
        maintenanceCombo.setFocusable(false);
        mainPanel.add(maintenanceCombo, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        
        okButton.setFocusable(false);
        cancelButton.setFocusable(false);
        
        okButton.addActionListener(e -> assign());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadMaintenanceStaff() {
        List<User> maintenanceStaff = userService.getAllByRole(Constants.ROLE_MAINTENANCE);
        if (maintenanceStaff.isEmpty()) {
            MessageDialog.showError(this, "没有找到可用的维修员！");
            return;
        }

        // 清空现有选项
        maintenanceCombo.removeAllItems();

        for (User staff : maintenanceStaff) {
            String displayText = staff.getUsername() + " (" + staff.getPhone() + ")";
            maintenanceCombo.addItem(new UserComboItem(staff, displayText));
        }
    }
    
    private void assign() {
        UserComboItem selectedItem = (UserComboItem) maintenanceCombo.getSelectedItem();
        if (selectedItem == null) {
            MessageDialog.showError(this, "请选择维修员！");
            return;
        }
        
        try {
            workOrderService.assignWork(repairOrderId, selectedItem.getUser().getId());
            assigned = true;
            dispose();
            MessageDialog.showInfo(this, "派工成功！");
        } catch (Exception e) {
            MessageDialog.showError(this, "派工失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 用于ComboBox的包装类
    private static class UserComboItem {
        private User user;
        private String displayText;
        
        public UserComboItem(User user, String displayText) {
            this.user = user;
            this.displayText = displayText;
        }

        public User getUser() {
            return user;
        }

        public String toString() {
            return displayText;
        }
    }

    public boolean isAssigned() {
        return assigned;
    }

} 