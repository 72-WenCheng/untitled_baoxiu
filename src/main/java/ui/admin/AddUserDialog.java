package main.java.ui.admin;

import main.java.entity.User;
import main.java.service.UserService;
import main.java.ui.common.MessageDialog;
import main.java.util.FrameUtil;
import javax.swing.*;
import java.awt.*;

public class AddUserDialog extends JDialog {
    private UserService userService;
    private String role;
    private boolean submitted = false;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    
    public AddUserDialog(JFrame parent, String role) {
        super(parent, "添加" + ("student".equals(role) ? "学生" : "维修员"), true);
        this.role = role;
        this.userService = new UserService();
        
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 用户名
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);
        
        // 密码
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("密码："), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);
        
        // 确认密码
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("确认密码："), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        formPanel.add(confirmPasswordField, gbc);
        
        // 联系电话
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("联系电话："), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> save());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void save() {
        // 验证输入
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String phone = phoneField.getText().trim();

        // 验证手机号
        if (!FrameUtil.isValidPhone(phone)) {
            FrameUtil.showPhoneError();
            phoneField.requestFocus();
            return;
        }

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            MessageDialog.showError(this, "请填写所有字段！");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            MessageDialog.showError(this, "两次输入的密码不一致！");
            return;
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setPhone(phone);
        user.setStatus("enabled");
        
        try {
            userService.insert(user);
            submitted = true;
            dispose();
            MessageDialog.showInfo(this, "添加成功！");
        } catch (Exception e) {
            MessageDialog.showError(this, "添加失败：" + e.getMessage());
        }
    }
    
    public boolean isSubmitted() {
        return submitted;
    }
} 