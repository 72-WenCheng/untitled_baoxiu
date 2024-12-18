package main.java.example;

import main.java.util.UIUtil;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 设置系统外观
        UIUtil.setLookAndFeel();
        
        // 启动登录界面
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
} 