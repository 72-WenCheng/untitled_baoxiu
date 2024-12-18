package main.java.ui.admin;

import main.java.entity.RepairOrder;
import main.java.entity.User;
import main.java.service.RepairOrderService;
import main.java.service.UserService;
import main.java.ui.common.MessageDialog;
import main.java.ui.common.RepairDetailDialog;
import main.java.ui.common.RoleSwitchButton;
import main.java.ui.common.StatisticsDialog;
import main.java.util.Constants;
import main.java.util.FrameUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;


public class AdminMainFrame extends JFrame {
    private User currentUser;
    private RepairOrderService repairOrderService;
    private UserService userService;
    private JTable repairOrderTable;
    private DefaultTableModel repairOrderTableModel;
    private JButton viewDetailButton;  // 查看详情按钮
    private JButton assignButton;    // 派工按钮

    public AdminMainFrame(User user) {
        this.currentUser = user;
        this.repairOrderService = new RepairOrderService();
        this.userService = new UserService();

        FrameUtil.setupFrame(this);  // 添加水印和样式设置
        setTitle("学生宿舍报修系统 - 管理员界面");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadRepairOrders();
        setVisible(true);
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("报修管理", createRepairManagePanel());
        tabbedPane.addTab("用户管理", createUserManagePanel());
        add(tabbedPane);
    }

    private JPanel createRepairManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"全部", "待受理", "已派工", "维修结束"});
        JLabel filterLabel = new JLabel("状态过滤：");
        toolBar.add(filterLabel);
        toolBar.add(statusFilter);
        statusFilter.addActionListener(e -> filterRepairOrders((String)statusFilter.getSelectedItem()));

        toolBar.addSeparator();
        toolBar.add(new RoleSwitchButton(currentUser, this));
        toolBar.addSeparator();

        JButton statsButton = new JButton("统计报表");
        statsButton.addActionListener(e -> showStatisticsDialog());
        toolBar.add(statsButton);

        panel.add(toolBar, BorderLayout.NORTH);

        // 报修单列表
        String[] columns = {"报修编号", "楼号", "房号", "报修内容", "报修人", "报修时间", "联系电话", "状态", "现场照片"};
        repairOrderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 8 ? ImageIcon.class : Object.class;
            }
        };

        repairOrderTable = new JTable(repairOrderTableModel);
        repairOrderTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(repairOrderTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        viewDetailButton = new JButton("查看详情");
        viewDetailButton.addActionListener(e -> showRepairDetail());
        viewDetailButton.setEnabled(false);
        buttonPanel.add(viewDetailButton);

        assignButton = new JButton("派工");
        assignButton.addActionListener(e -> showAssignDialog());
        assignButton.setEnabled(false);
        buttonPanel.add(assignButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 添加表格选择监听器
        repairOrderTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates());

        return panel;
    }

    private JPanel createUserManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addStudentButton = new JButton("添加学生");
        addStudentButton.addActionListener(e -> showAddUserDialog("student"));
        toolBar.add(addStudentButton);
        toolBar.addSeparator();

        JButton addMaintenanceButton = new JButton("添加维修员");
        addMaintenanceButton.addActionListener(e -> showAddUserDialog("maintenance"));
        toolBar.add(addMaintenanceButton);
        toolBar.addSeparator();

        JButton deleteButton = new JButton("删除用户");
        deleteButton.addActionListener(e -> deleteSelectedUser());
        toolBar.add(deleteButton);

        JButton toggleStatusButton = new JButton("切换状态");
        toggleStatusButton.addActionListener(e -> toggleUserStatus());
        toolBar.add(toggleStatusButton);

        panel.add(toolBar, BorderLayout.NORTH);

        // 用户列表表格
        String[] columns = {"ID", "用户名", "角色", "联系电话", "状态"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable userTable = new JTable(tableModel);
        userTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 加载用户数据
        loadUsers(tableModel);
        return panel;
    }

    private void deleteSelectedUser() {
        JTable userTable = getUserTable();
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择要删除的用户！");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        Integer userId = (Integer) model.getValueAt(selectedRow, 0);
        String username = (String) model.getValueAt(selectedRow, 1);
        String role = (String) model.getValueAt(selectedRow, 2);
        String status = (String) model.getValueAt(selectedRow, 4);

        // 检查用户状态
        if ("启用".equals(status)) {
            MessageDialog.showError(this, "启用状态的用户不能删除，请先禁用该用户！");
            return;
        }

        // 确认删除
        if (MessageDialog.showConfirm(this,
                String.format("确定要删除%s【%s】吗？", role, username))) {
            try {
                userService.delete(userId);
                loadUsers(model);  // 重新加载用户列表
                MessageDialog.showInfo(this, "删除成功！");
            } catch (Exception e) {
                // 如果是外键约束错误，显示友好提示
                FrameUtil.showError("该用户存在关联的维修工单数据，不能直接删除！");
            }
        }
    }

    private void showAddUserDialog(String role) {
        AddUserDialog dialog = new AddUserDialog(this, role);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            // 刷新用户列表
            DefaultTableModel model = (DefaultTableModel) ((JTable)((JScrollPane)
                    ((JPanel)((JTabbedPane)getContentPane().getComponent(0))
                            .getComponentAt(1)).getComponent(1)).getViewport().getView()).getModel();
            loadUsers(model);
        }
    }

    public void loadUsers(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try {
            List<User> users = userService.getAllUsers();
            for (User user : users) {
                // 只显示学生和维修员
                if ("student".equals(user.getRole()) || "maintenance".equals(user.getRole())) {
                    Object[] rowData = {
                            user.getId(),
                            user.getUsername(),
                            "student".equals(user.getRole()) ? "学生" : "维修员",
                            user.getPhone(),
                            "enabled".equals(user.getStatus()) ? "启用" : "禁用"
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (Exception e) {
            MessageDialog.showError(this, "加载用户列表失败：" + e.getMessage());
        }
    }


    private void toggleUserStatus() {
        JTable userTable = getUserTable();
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择要操作的用户！");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        Integer userId = (Integer) model.getValueAt(selectedRow, 0);
        String currentStatus = "启用".equals(model.getValueAt(selectedRow, 4)) ? "enabled" : "disabled";
        String newStatus = "enabled".equals(currentStatus) ? "disabled" : "enabled";
        String statusText = "enabled".equals(newStatus) ? "启用" : "禁用";

        try {
            userService.updateStatus(userId, newStatus);
            loadUsers(model);  // 重新加载用户列表
            MessageDialog.showInfo(this, "用户状态已更新为：" + statusText);
        } catch (Exception e) {
            MessageDialog.showError(this, "更新状态失败：" + e.getMessage());
        }
    }

    private JTable getUserTable() {
        return ((JTable)((JScrollPane)
                ((JPanel)((JTabbedPane)getContentPane().getComponent(0))
                        .getComponentAt(1)).getComponent(1)).getViewport().getView());
    }

    private void updateButtonStates() {
        int selectedRow = repairOrderTable.getSelectedRow();
        if (selectedRow != -1) {
            String status = (String) repairOrderTableModel.getValueAt(selectedRow, 7);
            // 所有状态的报修单都可以查看详情
            viewDetailButton.setEnabled(true);
            // 只有待受理状态的报修单可以派工
            assignButton.setEnabled(Constants.STATUS_PENDING.equals(status));
        } else {
            // 没有选中行时禁用所有按钮
            viewDetailButton.setEnabled(false);
            assignButton.setEnabled(false);
        }
    }

    private void loadRepairOrders() {
        // 保存当前的行高和列宽设置
        int rowHeight = repairOrderTable.getRowHeight();
        int imageColumnWidth = repairOrderTable.getColumnModel().getColumn(8).getPreferredWidth();
        repairOrderTableModel.setRowCount(0);
        List<RepairOrder> orders = repairOrderService.getAllOrderByTime();
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

            // 过滤掉草状态的报修单
            if (!Constants.STATUS_DRAFT.equals(order.getStatus())) {
                Object[] rowData = {
                        order.getId(),
                        order.getBuildingNo(),
                        order.getRoomNo(),
                        order.getDescription(),
                        order.getReporterName(),
                        dateFormat.format(order.getReportTime()),  // 格式化日期
                        order.getContactPhone(),
                        order.getStatus(),
                        imageIcon  // 添加图片
                };
                repairOrderTableModel.addRow(rowData);
            }
            // 恢复行高和列宽设置
            repairOrderTable.setRowHeight(rowHeight);
            repairOrderTable.getColumnModel().getColumn(8).setPreferredWidth(imageColumnWidth);
        }
        // 更新按钮状态
        updateButtonStates();
    }

    private void filterRepairOrders(String status) {
        if ("全部".equals(status)) {
            loadRepairOrders();
            return;
        }

        String realStatus = null;
        switch (status) {
            case "待受理": realStatus = Constants.STATUS_PENDING; break;
            case "已派工": realStatus = Constants.STATUS_ASSIGNED; break;
            case "维修结束": realStatus = Constants.STATUS_COMPLETED; break;
        }

        if (realStatus != null) {
            // 保持当前的行高和列宽设置
            int rowHeight = repairOrderTable.getRowHeight();
            int imageColumnWidth = repairOrderTable.getColumnModel().getColumn(8).getPreferredWidth();
            
            repairOrderTableModel.setRowCount(0);
            List<RepairOrder> orders = repairOrderService.getByStatus(realStatus);
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
                        order.getReporterName(),
                        dateFormat.format(order.getReportTime()),
                        order.getContactPhone(),
                        order.getStatus(),
                        imageIcon  // 添加图片
                };
                repairOrderTableModel.addRow(rowData);
            }
            
            // 恢复行高和列宽设置
            repairOrderTable.setRowHeight(rowHeight);
            repairOrderTable.getColumnModel().getColumn(8).setPreferredWidth(imageColumnWidth);
        }
        // 更新按钮状态
        updateButtonStates();
    }

//    public Class<?> getColumnClass(int column) {
//        // 将最后一列（现场照片）设置为 ImageIcon 类型
//        return column == 8 ? ImageIcon.class : Object.class;
//    }

    private void showRepairDetail() {
        int selectedRow = repairOrderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个报修单！");
            return;
        }

        Integer orderId = (Integer) repairOrderTableModel.getValueAt(selectedRow, 0);
        new RepairDetailDialog(this, orderId).setVisible(true);
    }

    private void showAssignDialog() {
        int selectedRow = repairOrderTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showError(this, "请选择一个报修单");
            return;
        }

        Integer orderId = (Integer) repairOrderTableModel.getValueAt(selectedRow, 0);
        String status = (String) repairOrderTableModel.getValueAt(selectedRow, 7);

        // 只有待受理的报修单才能派工
        if (!Constants.STATUS_PENDING.equals(status)) {
            MessageDialog.showError(this, "只有待受理的报修单才能派工！");
            return;
        }

        AssignWorkDialog dialog = new AssignWorkDialog(this, orderId);
        dialog.setVisible(true);
        if (dialog.isAssigned()) {
            loadRepairOrders();
        }
    }

    private void showStatisticsDialog() {
        new StatisticsDialog(this).setVisible(true);
    }
}

    // 添加按钮渲染器
//    class ButtonRenderer extends JButton implements TableCellRenderer {
//        public ButtonRenderer() {
//            setOpaque(true);
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value,
//                                                       boolean isSelected, boolean hasFocus, int row, int column) {
//            String status = (String) table.getValueAt(row, 4);
//            setText("enabled".equals(status) ? "禁用" : "启用");
//            return this;
//        }
//    }