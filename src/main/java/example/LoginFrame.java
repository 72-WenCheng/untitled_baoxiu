package main.java.example;

import main.java.entity.User;
import main.java.service.UserService;
import main.java.ui.admin.AdminMainFrame;
import main.java.ui.common.MessageDialog;
import main.java.ui.maintenance.MaintenanceMainFrame;
import main.java.ui.student.StudentMainFrame;
import main.java.util.Constants;
import main.java.util.FrameUtil;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<RoleItem> roleComboBox;

    public LoginFrame() {
        FrameUtil.setupFrame(this);
        userService = new UserService();
        setTitle("学生宿舍报修系统 - 登录");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;  // 让组件水平填充

        // 统一边框样式和大小
        Dimension fieldSize = new Dimension(200, 25);  // 统一大小
        UIManager.put("TextField.border", BorderFactory.createLineBorder(Color.GRAY));
        UIManager.put("PasswordField.border", BorderFactory.createLineBorder(Color.GRAY));
        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(Color.GRAY));

        // 用户名
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(fieldSize);
        mainPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("密码："), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(fieldSize);
        mainPanel.add(passwordField, gbc);

        // 角色选择
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("角色："), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new RoleItem[] {
                new RoleItem("学生", Constants.ROLE_STUDENT),
                new RoleItem("管理员", Constants.ROLE_ADMIN),
                new RoleItem("维修员", Constants.ROLE_MAINTENANCE)
        });
        roleComboBox.setPreferredSize(fieldSize);
        mainPanel.add(roleComboBox, gbc);

        // 登录按钮
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;  // 按钮不需要填充
        JButton loginButton = new JButton("登录");
        loginButton.setPreferredSize(new Dimension(80, 30));  // 设置按钮大小
        loginButton.addActionListener(e -> login());
        mainPanel.add(loginButton, gbc);
        add(mainPanel);

        // 回车键触发登录
        getRootPane().setDefaultButton(loginButton);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = ((RoleItem)roleComboBox.getSelectedItem()).getValue();

        if (username.isEmpty() || password.isEmpty()) {
            MessageDialog.showError(this, "请输入用户名和密码！");
            return;
        }

        User user = userService.login(username, password, role);
        if (user == null) {
            MessageDialog.showError(this, "用户名或密码错误，或角色选择有误！");
            return;
        }

        // 根据用户角色打开相应的主界面
        switch (user.getRole()) {
            case Constants.ROLE_STUDENT:
                new StudentMainFrame(user).setVisible(true);
                break;
            case Constants.ROLE_ADMIN:
                new AdminMainFrame(user).setVisible(true);
                break;
            case Constants.ROLE_MAINTENANCE:
                new MaintenanceMainFrame(user).setVisible(true);
                break;
        }

        this.dispose();
    }

    // 角色选项的包装类
    private static class RoleItem {
        private String name;
        private String value;

        public RoleItem(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return name;
        }
    }
}