package main.java.ui.student;

import main.java.entity.RepairOrder;
import main.java.entity.User;
import main.java.service.RepairOrderService;
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

public class StudentMainFrame extends JFrame {
    private User currentUser;
    private RepairOrderService repairOrderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JButton submitButton;  // 新增提交按钮作为成员变量

    public StudentMainFrame(User user) {
        this.currentUser = user;
        this.repairOrderService = new RepairOrderService();

        FrameUtil.setupFrame(this);  // 添加水印和样式设置
        setTitle("学生宿舍报修系统 - 学生界面");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadOrders();
    }

    private void initComponents() {
        // 工具栏
        JToolBar toolBar = new JToolBar();
        JButton newOrderBtn = new JButton("新建报修");
        newOrderBtn.addActionListener(e -> showNewOrderDialog());
        toolBar.add(newOrderBtn);
        add(toolBar, BorderLayout.NORTH);

        // 添加角色切换按钮
        toolBar.add(new RoleSwitchButton(currentUser, this));
        toolBar.addSeparator();

        // 报修单列表
        String[] columnNames = {"报修编号", "楼号", "房号", "报修内容", "报修时间", "联系电话", "状态", "姓名", "现场照片"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 8) { // 现场照片列
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(60);  // 设置足够的行高来显示图片
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);
/*

 ] */
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 添加修改按钮
        JButton editButton = new JButton("修改");
        editButton.addActionListener(e -> editOrder());
        editButton.setEnabled(false);  // 初始状态下禁用
        buttonPanel.add(editButton);

        // 查看详情按钮
        JButton viewDetailButton = new JButton("查看详情");
        viewDetailButton.addActionListener(e -> showRepairDetail());
        buttonPanel.add(viewDetailButton);

        // 提交按钮
        submitButton = new JButton("提交");
        submitButton.addActionListener(e -> submitOrder());
        submitButton.setEnabled(false);  // 初始状态下禁用
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 添加表格选择监听器
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    String status = (String) tableModel.getValueAt(selectedRow, 6);
                    // 只有草稿状态的报修单可以修改和提交
                    boolean isDraft = Constants.STATUS_DRAFT.equals(status);
                    editButton.setEnabled(isDraft);
                    submitButton.setEnabled(isDraft);
                } else {
                    submitButton.setEnabled(false);
                    editButton.setEnabled(false);
                }
            }
        });
    }

    // 添加修改报修单的方法
    private void editOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个报修单！");
            return;
        }

        Integer orderId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        if (!Constants.STATUS_DRAFT.equals(status)) {
            MessageDialog.showError(this, "只有草稿状态的报修单才能修改！");
            return;
        }

        // 获取完整的报修单信息
        RepairOrder order = repairOrderService.getById(orderId);
        if (order != null) {
            // 打开修改对话框，传入当前报修单信息
            NewRepairOrderDialog dialog = new NewRepairOrderDialog(this, currentUser, order);
            dialog.setVisible(true);
            if (dialog.isSubmitted()) {
                loadOrders();  // 刷新列表
            }
        }
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        List<RepairOrder> orders = repairOrderService.getByReporterId(currentUser.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (RepairOrder order : orders) {

            // 处理图片
            ImageIcon imageIcon = null;
            if (order.getImagePath() != null && !order.getImagePath().isEmpty()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(order.getImagePath());
                    Image image = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Object[] rowData = {
                    order.getId(),
                    order.getBuildingNo(),
                    order.getRoomNo(),
                    order.getDescription(),
//                    dateFormat.format(order.getReportTime()),  // 格式化日期
                    dateFormat.format(order.getReportTime()),
                    order.getContactPhone(),
                    order.getStatus(),
                    order.getReporterName(),
                    imageIcon
            };
            tableModel.addRow(rowData);
        }
    }

    private void showNewOrderDialog() {
        NewRepairOrderDialog dialog = new NewRepairOrderDialog(this, currentUser);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            loadOrders();
        }
    }

    private void showRepairDetail() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个报修单！");
            return;
        }

        Integer orderId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        // 只有已完成的报修单才能查看维修信息
        if (!Constants.STATUS_COMPLETED.equals(status)) {
            MessageDialog.showError(this, "只有维修结束的报修单才能查看维修信息！");
            return;
        }

        new RepairDetailDialog(this, orderId).setVisible(true);
    }

    private void submitOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个报修单！");
            return;
        }

        Integer orderId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 6);

        // 根据不同状态给出不同的提示信息
        if (!Constants.STATUS_DRAFT.equals(status)) {
            if (Constants.STATUS_PENDING.equals(status)) {
                MessageDialog.showError(this, "该报修单已提交，正在等待处理！");
            } else if (Constants.STATUS_ASSIGNED.equals(status)) {
                MessageDialog.showError(this, "该报修单已分配维修人员，正在处理中！");
            } else if (Constants.STATUS_COMPLETED.equals(status)) {
                MessageDialog.showError(this, "该报修单已完成维修！");
            } else {
                MessageDialog.showError(this, "只有草稿状态的报修单才能提交！");
            }
            return;
        }

        if (MessageDialog.showConfirm(this, "确定要提交这个报修单吗？")) {
            RepairOrder order = repairOrderService.getById(orderId);
            order.setStatus(Constants.STATUS_PENDING);
            repairOrderService.updateOrder(order);
            loadOrders();
            MessageDialog.showInfo(this, "报修单已提交！");
        }
    }
}