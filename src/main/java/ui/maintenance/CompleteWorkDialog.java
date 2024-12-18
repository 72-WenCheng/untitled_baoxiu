package main.java.ui.maintenance;

import main.java.service.WorkOrderService;
import main.java.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;

public class CompleteWorkDialog extends JDialog {
    private Integer workOrderId;
    private WorkOrderService workOrderService;
    private boolean submitted = false;
    private JTextArea faultAnalysisArea;
    private JTextArea repairProcessArea;
    private JTextArea repairResultArea;
    
    public CompleteWorkDialog(JFrame parent, Integer workOrderId) {
        super(parent, "填写维修单", true);
        this.workOrderId = workOrderId;
        this.workOrderService = new WorkOrderService();
        
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // 故障分析
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("故障分析："), gbc);
        gbc.gridy = 1;
        faultAnalysisArea = new JTextArea(8, 30);
        faultAnalysisArea.setLineWrap(true);
        faultAnalysisArea.setWrapStyleWord(true);
        JScrollPane scrollPane1 = new JScrollPane(faultAnalysisArea);
        scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        formPanel.add(scrollPane1, gbc);
        
        // 维修过程
        gbc.gridy = 2;
        formPanel.add(new JLabel("维修过程："), gbc);
        gbc.gridy = 3;
        repairProcessArea = new JTextArea(8, 30);
        repairProcessArea.setLineWrap(true);
        repairProcessArea.setWrapStyleWord(true);
        JScrollPane scrollPane2 = new JScrollPane(repairProcessArea);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        formPanel.add(scrollPane2, gbc);
        
        // 维修结果
        gbc.gridy = 4;
        formPanel.add(new JLabel("维修结果："), gbc);
        gbc.gridy = 5;
        repairResultArea = new JTextArea(8, 30);
        repairResultArea.setLineWrap(true);
        repairResultArea.setWrapStyleWord(true);
        JScrollPane scrollPane3 = new JScrollPane(repairResultArea);
        scrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        formPanel.add(scrollPane3, gbc);

        // 设置对话框大小
        setSize(500, 700);
        add(formPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("提交");
        JButton cancelButton = new JButton("取消");
        
        submitButton.addActionListener(e -> submit());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void submit() {
        // 验证输入
        String faultAnalysis = faultAnalysisArea.getText().trim();
        String repairProcess = repairProcessArea.getText().trim();
        String repairResult = repairResultArea.getText().trim();
        
        if (faultAnalysis.isEmpty() || repairProcess.isEmpty() || repairResult.isEmpty()) {
            MessageDialog.showError(this, "请填写所有字段！");
            return;
        }
        
        // 提交维修单
        workOrderService.completeWork(workOrderId, faultAnalysis, repairProcess, repairResult);
        submitted = true;
        dispose();
        MessageDialog.showInfo(this, "维修单已提交！");
    }
    
    public boolean isSubmitted() {
        return submitted;
    }

} 