package main.java.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIUtil {
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void centerWindow(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - window.getWidth()) / 2;
        int y = (screenSize.height - window.getHeight()) / 2;
        window.setLocation(x, y);
    }
    
    public static ImageIcon scaleImage(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image image = icon.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(image);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static JFileChooser createImageFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", Constants.IMAGE_EXTENSIONS));
        return fileChooser;
    }
    
    public static Border createErrorBorder() {
        return BorderFactory.createLineBorder(Color.RED);
    }
    
    public static Border getDefaultTextFieldBorder() {
        return UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border");
    }
    
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "错误", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "提示", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "确认", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
} 