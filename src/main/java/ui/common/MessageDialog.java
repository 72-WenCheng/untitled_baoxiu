package main.java.ui.common;

import javax.swing.*;
import java.awt.*;

public class MessageDialog {
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "确认", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
} 