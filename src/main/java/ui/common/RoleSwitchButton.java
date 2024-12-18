package main.java.ui.common;

import main.java.entity.User;
import main.java.service.UserService;
import main.java.ui.admin.AdminMainFrame;
import main.java.ui.maintenance.MaintenanceMainFrame;
import main.java.ui.student.StudentMainFrame;
import main.java.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoleSwitchButton extends JButton implements ActionListener {
    private User currentUser;
    private JFrame currentFrame;
    private UserService userService;

    public RoleSwitchButton(User user, JFrame frame) {
        this.currentUser = user;
        this.currentFrame = frame;
        this.userService = new UserService();

        setText("切换角色: " + getRoleName(user.getRole()));
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String[] roles = {
                Constants.ROLE_STUDENT,
                Constants.ROLE_ADMIN,
                Constants.ROLE_MAINTENANCE
        };
        String[] roleNames = {
                Constants.ROLE_NAME_STUDENT,
                Constants.ROLE_NAME_ADMIN,
                Constants.ROLE_NAME_MAINTENANCE
        };

        // 创建角色选择对话框
        String selectedRole = (String) JOptionPane.showInputDialog(
                currentFrame,
                "请选择要切换的角色：",
                "切换角色",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roleNames,
                getRoleName(currentUser.getRole())
        );

        if (selectedRole != null) {
            // 根据选择的角色名称获取角色值
            String newRole = roles[getIndex(roleNames, selectedRole)];
            if (!newRole.equals(currentUser.getRole())) {
                // 显示登录对话框
                showLoginDialog(newRole);
            }
        }
    }

    private void showLoginDialog(String newRole) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        panel.add(new JLabel("用户名："));
        panel.add(usernameField);
        panel.add(new JLabel("密码："));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(currentFrame, panel,
                "请输入登录信息", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // 验证登录
            User user = userService.login(username, password, newRole);
            if (user != null) {
                switchRole(user);
            } else {
                MessageDialog.showError(currentFrame, "用户名或密码错误！");
            }
        }
    }

    private void switchRole(User user) {
        JFrame newFrame = null;

        // 创建新角色对应的主界面
        switch (user.getRole()) {
            case Constants.ROLE_STUDENT:
                newFrame = new StudentMainFrame(user);
                break;
            case Constants.ROLE_ADMIN:
                newFrame = new AdminMainFrame(user);
                break;
            case Constants.ROLE_MAINTENANCE:
                newFrame = new MaintenanceMainFrame(user);
                break;
        }

        if (newFrame != null) {
            newFrame.setVisible(true);
            currentFrame.dispose();
        }
    }

    private String getRoleName(String role) {
        switch (role) {
            case Constants.ROLE_STUDENT:
                return Constants.ROLE_NAME_STUDENT;
            case Constants.ROLE_ADMIN:
                return Constants.ROLE_NAME_ADMIN;
            case Constants.ROLE_MAINTENANCE:
                return Constants.ROLE_NAME_MAINTENANCE;
            default:
                return role;
        }
    }

    private int getIndex(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return 0;
    }
}