package main.java.ui.maintenance;

import main.java.entity.User;
import main.java.entity.WorkOrder;
import main.java.service.WorkOrderService;
import main.java.ui.common.MessageDialog;
import main.java.ui.common.RepairDetailDialog;
import main.java.ui.common.RoleSwitchButton;
import main.java.util.Constants;
import main.java.util.FrameUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MaintenanceMainFrame extends JFrame {
    private User currentUser;
    private WorkOrderService workOrderService;
    private JTable workOrderTable;
    private DefaultTableModel tableModel;
    private JButton completeButton;    // 填写维修单按钮
    private JButton viewDetailButton;  // 查看详情按钮

    public MaintenanceMainFrame(User user) {
        FrameUtil.setupFrame(this);

        this.currentUser = user;
        this.workOrderService = new WorkOrderService();

        setTitle("学生宿舍报修系统 - 维修员界面");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        loadWorkOrders();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // 添加角色切换按钮
        toolBar.add(new RoleSwitchButton(currentUser, this));
        toolBar.addSeparator();

        // 工号显示
        toolBar.add(new JLabel("工号："));
        JTextField workNoField = new JTextField(10);
        workNoField.setEditable(false);
        workNoField.setText(String.valueOf(currentUser.getId()));
        toolBar.add(workNoField);

        add(toolBar, BorderLayout.NORTH);

        // 报修单列表
        String[] columns = {"工单号", "报修单号", "楼号", "房号", "报修内容", "报修人", "派工时间", "状态", "现场照片"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 8 ? ImageIcon.class : Object.class;
            }
        };

        workOrderTable = new JTable(tableModel);
        workOrderTable.setRowHeight(60);  // 设置行高以便显示图片
        workOrderTable.getColumnModel().getColumn(8).setPreferredWidth(100);  // 设置图片列宽度

        add(new JScrollPane(workOrderTable), BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 查看详情按钮
        viewDetailButton = new JButton("查看详情");
        viewDetailButton.addActionListener(e -> showRepairDetail());
        viewDetailButton.setEnabled(false);

        // 填写维修单按钮
        completeButton = new JButton("填写维修单");
        completeButton.addActionListener(e -> showCompleteDialog());
        completeButton.setEnabled(false);

        buttonPanel.add(viewDetailButton);
        buttonPanel.add(completeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 添加表格选择监听器
        workOrderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }

    private void updateButtonStates() {
        int selectedRow = workOrderTable.getSelectedRow();
        if (selectedRow != -1) {
            String status = (String) tableModel.getValueAt(selectedRow, 7);
            completeButton.setEnabled(Constants.WORK_STATUS_PENDING.equals(status));
            viewDetailButton.setEnabled(Constants.WORK_STATUS_COMPLETED.equals(status));
        } else {
            completeButton.setEnabled(false);
            viewDetailButton.setEnabled(false);
        }
    }

    private void loadWorkOrders() {
        tableModel.setRowCount(0);
        List<WorkOrder> orders = workOrderService.getByMaintenanceId(currentUser.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (WorkOrder order : orders) {
            // 处理图片
            ImageIcon imageIcon = null;
            if (order.getRepairOrder().getImagePath() != null && !order.getRepairOrder().getImagePath().isEmpty()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(order.getRepairOrder().getImagePath());
                    Image image = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Object[] rowData = {
                    order.getId(),
                    order.getRepairOrderId(),
                    order.getRepairOrder().getBuildingNo(),
                    order.getRepairOrder().getRoomNo(),
                    order.getRepairOrder().getDescription(),
                    order.getRepairOrder().getReporterName(),
                    dateFormat.format(order.getAssignTime()),
                    order.getStatus(),
                    imageIcon  // 添加图片
            };
            tableModel.addRow(rowData);
        }
        updateButtonStates();
    }

    private void showRepairDetail() {
        int selectedRow = workOrderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个工单！");
            return;
        }

        Integer repairOrderId = (Integer) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if (!Constants.WORK_STATUS_COMPLETED.equals(status)) {
            MessageDialog.showError(this, "只有已完成的工单才能查看维修详情！");
            return;
        }

        new RepairDetailDialog(this, repairOrderId).setVisible(true);
    }

    private void showCompleteDialog() {
        int selectedRow = workOrderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个工单！");
            return;
        }

        Integer workOrderId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if (!Constants.WORK_STATUS_PENDING.equals(status)) {
            MessageDialog.showError(this, "只能处理未完成的工单！");
            return;
        }

        CompleteWorkDialog dialog = new CompleteWorkDialog(this, workOrderId);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            loadWorkOrders();
        }
    }
}