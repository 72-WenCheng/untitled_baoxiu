package main.java.ui.common;

import main.java.entity.RepairOrder;
import main.java.service.RepairOrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class StatisticsDialog extends JDialog {
    private RepairOrderService repairOrderService;
    private DefaultTableModel statusModel;
    private DefaultTableModel locationModel;
    private DefaultTableModel trendModel;

    public StatisticsDialog(JFrame parent) {
        super(parent, "统计报表", true);
        this.repairOrderService = new RepairOrderService();

        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // 状态统计面板
        tabbedPane.addTab("报修状态统计", createStatusPanel());

        // 位置统计面板
        tabbedPane.addTab("报修位置统计", createLocationPanel());

        // 时间趋势面板
        tabbedPane.addTab("报修趋势统计", createTrendPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 添加导出按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("导出Excel");
        exportButton.addActionListener(e -> exportToExcel());
        buttonPanel.add(exportButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"状态", "数量", "占比"};
        statusModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(statusModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLocationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"楼号", "报修数量", "占比"};
        locationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(locationModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTrendPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"月份", "报修数量", "环比增长"};
        trendModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(trendModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        List<RepairOrder> allOrders = repairOrderService.getAllOrderByTime();

        // 状态统计
        Map<String, Long> statusStats = allOrders.stream()
                .collect(Collectors.groupingBy(RepairOrder::getStatus, Collectors.counting()));
        double total = allOrders.size();
        statusStats.forEach((status, count) -> {
            double percentage = count / total * 100;
            statusModel.addRow(new Object[]{
                    status,
                    count,
                    String.format("%.2f%%", percentage)
            });
        });

        // 位置统计
        Map<String, Long> locationStats = allOrders.stream()
                .collect(Collectors.groupingBy(RepairOrder::getBuildingNo, Collectors.counting()));
        locationStats.forEach((building, count) -> {
            double percentage = count / total * 100;
            locationModel.addRow(new Object[]{
                    building,
                    count,
                    String.format("%.2f%%", percentage)
            });
        });

        // 时间趋势统计（按月）
        TreeMap<String, Long> trendStats = allOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> String.format("%tY-%tm", order.getReportTime(), order.getReportTime()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        Long lastCount = null;
        for (Map.Entry<String, Long> entry : trendStats.entrySet()) {
            String growth = "-";
            if (lastCount != null) {
                double growthRate = (entry.getValue() - lastCount) * 100.0 / lastCount;
                growth = String.format("%.2f%%", growthRate);
            }
            trendModel.addRow(new Object[]{
                    entry.getKey(),
                    entry.getValue(),
                    growth
            });
            lastCount = entry.getValue();
        }
    }

    private void exportToExcel() {
        // TODO: 实现导出Excel功能
        JOptionPane.showMessageDialog(this, "导出功能开发中...", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
}