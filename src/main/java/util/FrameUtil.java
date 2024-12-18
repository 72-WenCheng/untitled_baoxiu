package main.java.util;

import javax.swing.*;
import java.awt.*;

public class FrameUtil {
    private static JLabel watermarkLabel;

    public static void showError(String message) {
        JOptionPane.showMessageDialog(
                null,
                message,
                "错误提示",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static boolean isValidPhone(String phone) {
        // 手机号码格式：1开头的11位数字
        return phone != null && phone.matches("^1\\d{10}$");
    }

    public static void showPhoneError() {
        showError("请输入正确的手机号码格式！(以1开头的11位数字)");
    }

    public static void setupFrame(JFrame frame) {
        try {
            // 设置现代风格的Look and Feel
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            // 设置全局字体
            Font defaultFont = new Font("微软雅黑", Font.PLAIN, 12);
            Font boldFont = new Font("微软雅黑", Font.BOLD, 12);
            UIManager.put("Button.font", boldFont);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", boldFont);
            UIManager.put("ComboBox.font", defaultFont);
            
            // 设置面板样式
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("OptionPane.background", Color.WHITE);
            UIManager.put("ScrollPane.background", Color.WHITE);
            UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(new Color(229, 231, 235)));
            
            // 刷新UI
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 创建水印标签
        JLabel watermarkLabel = new JLabel("彭文成-2431020120511-计算机应用工程（专升本）") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        watermarkLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        watermarkLabel.setForeground(new Color(128, 128, 128, 180));
        watermarkLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 将水印添加到分层窗格的最上层
        JLayeredPane layeredPane = frame.getLayeredPane();
        layeredPane.add(watermarkLabel, JLayeredPane.POPUP_LAYER);

        // 设置水印位置（居中）
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                Dimension size = frame.getSize();
                watermarkLabel.setBounds(
                        (size.width - 380) / 2,
                        (size.height - 30) / 2,
                        380,
                        30
                );
            }
        });
    }
}