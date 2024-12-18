package main.java.ui.common;

import main.java.entity.RepairOrder;
import main.java.entity.WorkOrder;
import main.java.service.RepairOrderService;
import main.java.service.WorkOrderService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class RepairDetailDialog extends JDialog {
    private WorkOrder workOrder;
    private RepairOrder repairOrder;
    private WorkOrderService workOrderService;
    private RepairOrderService repairOrderService;

    public RepairDetailDialog(JFrame parent, Integer repairOrderId) {
        super(parent, "维修单详情", true);
        this.workOrderService = new WorkOrderService();
        this.repairOrderService = new RepairOrderService();

        // 获取维修单信息
        this.repairOrder = repairOrderService.getById(repairOrderId);
        this.workOrder = workOrderService.getByRepairOrderId(repairOrderId);

        setSize(500, 700);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        // 主面板使用 GridBagLayout 实现更灵活的布局
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 报修信息部分
        addSection(mainPanel, gbc, "报修信息", createRepairInfoPanel());

        // 维修信息部分
        if (workOrder != null) {
            addSection(mainPanel, gbc, "维修信息", createWorkInfoPanel());
        }

        // 添加滚动面板
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // 关闭按钮
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSection(JPanel panel, GridBagConstraints gbc, String title, JPanel content) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        // 添加分隔线和标题
        if (gbc.gridy > 0) {
            JSeparator separator = new JSeparator();
            separator.setPreferredSize(new Dimension(1, 10));
            gbc.insets = new Insets(15, 5, 5, 5);
            panel.add(separator, gbc);
            gbc.gridy++;
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        panel.add(titleLabel, gbc);

        // 添加内容面板
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(content, gbc);
    }

    private JPanel createRepairInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 添加报修信息字段
        addField(panel, gbc, "报修单号：", String.valueOf(repairOrder.getId()));
        addField(panel, gbc, "楼号：", repairOrder.getBuildingNo());
        addField(panel, gbc, "房号：", repairOrder.getRoomNo());
        addField(panel, gbc, "报修内容：", repairOrder.getDescription());
        addField(panel, gbc, "报修人：", repairOrder.getReporterName());
        addField(panel, gbc, "联系电话：", repairOrder.getContactPhone());
        addField(panel, gbc, "报修时间：", dateFormat.format(repairOrder.getReportTime()));
        addField(panel, gbc, "状态：", repairOrder.getStatus());

        // 添加现场照片
        if (repairOrder.getImagePath() != null && !repairOrder.getImagePath().isEmpty()) {
            gbc.gridy++;
            gbc.gridx = 0;
            panel.add(new JLabel("现场照片："), gbc);

            gbc.gridx = 1;
            try {
                ImageIcon originalIcon = new ImageIcon(repairOrder.getImagePath());
                Image image = originalIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                panel.add(imageLabel, gbc);
            } catch (Exception e) {
                panel.add(new JLabel("无法加载图片"), gbc);
            }
        }

        return panel;
    }

    private JPanel createWorkInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 添加维修信息字段
        addField(panel, gbc, "故障分析：", workOrder.getFaultAnalysis());
        addField(panel, gbc, "维修过程：", workOrder.getRepairProcess());
        addField(panel, gbc, "维修结果：", workOrder.getRepairResult());

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        if (value != null && value.length() > 50) {
            // 对于长文本，使用文本区域
            JTextArea textArea = new JTextArea(value);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setRows(3);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(300, 60));
            panel.add(scrollPane, gbc);
        } else {
            // 对于短文本，使用标签
            panel.add(new JLabel(value != null ? value : ""), gbc);
        }
    }
}