package main.java.ui.student;

import main.java.entity.RepairOrder;
import main.java.entity.User;
import main.java.service.RepairOrderService;
import main.java.ui.common.MessageDialog;
import main.java.util.Constants;
import main.java.util.ValidationUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Date;

public class NewRepairOrderDialog extends JDialog {
    private User currentUser;
    private RepairOrderService repairOrderService;
    private boolean submitted = false;
    private JTextField buildingNoField;
    private JTextField roomNoField;
    private JTextArea descriptionArea;
    private JTextField contactPhoneField;
    private JTextField imagePathField;
    private JTextField usernameField;
    private JButton browseButton;
    private JButton saveButton;
    private JButton submitButton;
    private JLabel imagePreview;
    private RepairOrder existingOrder;  // 添加这个字段来保存正在编辑的报修单

    public NewRepairOrderDialog(JFrame parent, User user) {
        super(parent, "新建报修单", true);
        this.currentUser = user;
        this.repairOrderService = new RepairOrderService();
        this.existingOrder = null;

        setSize(400, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    // 新增构造函数用于修改报修单
    public NewRepairOrderDialog(JFrame parent, User user, RepairOrder order) {
        super(parent, "修改报修单", true);
        this.currentUser = user;
        this.repairOrderService = new RepairOrderService();
        this.existingOrder = order;

        setSize(400, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initComponents();

        // 填充现有数据
        buildingNoField.setText(order.getBuildingNo());
        roomNoField.setText(order.getRoomNo());
        descriptionArea.setText(order.getDescription());
        if (order.getImagePath() != null) {
            imagePathField.setText(order.getImagePath());
            updateImagePreview(new File(order.getImagePath()));
        }

        // 修改按钮文本
        submitButton.setText("保存修改");
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 楼号
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("楼号："), gbc);
        gbc.gridx = 1;
        buildingNoField = new JTextField(10);
        formPanel.add(buildingNoField, gbc);

        // 房号
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("房号："), gbc);
        gbc.gridx = 1;
        roomNoField = new JTextField(10);
        formPanel.add(roomNoField, gbc);

        // 报修内容
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("报修内容："), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        // 联系电话
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("联系电话："), gbc);
        gbc.gridx = 1;
        contactPhoneField = new JTextField(15);
        contactPhoneField.setText(currentUser.getPhone()); // 默认使用用户电话
        contactPhoneField.setEditable(false);  // 设置为不可编辑
        contactPhoneField.setBackground(new Color(240, 240, 240));  // 设置灰色背景
        formPanel.add(contactPhoneField, gbc);

        // 姓名
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("姓名："), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);  // 修改变量名避免重复使用contactPhoneField
        usernameField.setText(currentUser.getUsername()); // 默认使用用户名
        usernameField.setEditable(false);  // 设置为不可编辑
        usernameField.setBackground(new Color(240, 240, 240));  // 设置灰色背景
        formPanel.add(usernameField, gbc);

        // 现场图片
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("现场图片："), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel selectPanel = new JPanel(new BorderLayout(5, 0));
        imagePathField = new JTextField(20);
        browseButton = new JButton("浏览...");
        browseButton.addActionListener(e -> browseImage());
        selectPanel.add(imagePathField, BorderLayout.CENTER);
        selectPanel.add(browseButton, BorderLayout.EAST);

        imagePreview = new JLabel();
        imagePreview.setPreferredSize(new Dimension(200, 150));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);

        imagePanel.add(selectPanel, BorderLayout.NORTH);
        imagePanel.add(imagePreview, BorderLayout.CENTER);
        formPanel.add(imagePanel, gbc);

        // 添加输入验证
        buildingNoField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateBuildingNo();
            }
        });

        roomNoField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateRoomNo();
            }
        });

        contactPhoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validatePhone();
            }
        });

        add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (existingOrder != null) {
            // 修改模式只显示保存修改和取消按钮
            submitButton = new JButton("保存修改");
            submitButton.setFocusable(false);
            JButton cancelButton = new JButton("取消");
            cancelButton.setFocusable(false);
            submitButton.addActionListener(e -> save(true));
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(submitButton);
            buttonPanel.add(cancelButton);
        } else {
            // 新建模式显示保存草稿、提交和取消按钮
            saveButton = new JButton("保存草稿");
            submitButton = new JButton("提交");
            JButton cancelButton = new JButton("取消");

            saveButton.setFocusable(false);
            submitButton.setFocusable(false);
            cancelButton.setFocusable(false);

            saveButton.addActionListener(e -> save(false));
            submitButton.addActionListener(e -> save(true));
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(submitButton);
            buttonPanel.add(cancelButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "图片文件", "jpg", "jpeg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            imagePathField.setText(file.getAbsolutePath());
            updateImagePreview(file);
        }
    }

    private void updateImagePreview(File file) {
        try {
            ImageIcon icon = new ImageIcon(file.getPath());
            Image image = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            imagePreview.setIcon(new ImageIcon(image));
        } catch (Exception e) {
            imagePreview.setIcon(null);
            imagePreview.setText("无法加载图片");
        }
    }

    private boolean validateBuildingNo() {
        String buildingNo = buildingNoField.getText().trim();
        boolean valid = ValidationUtil.isValidBuildingNo(buildingNo);
        buildingNoField.setBorder(valid ?
            UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border") :
            BorderFactory.createLineBorder(Color.RED));
        return valid;
    }

    private boolean validateRoomNo() {
        String roomNo = roomNoField.getText().trim();
        boolean valid = ValidationUtil.isValidRoomNo(roomNo);
        roomNoField.setBorder(valid ?
            UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border") :
            BorderFactory.createLineBorder(Color.RED));
        return valid;
    }

    private boolean validatePhone() {
        String phone = contactPhoneField.getText().trim();
        boolean valid = ValidationUtil.isValidPhone(phone);
        contactPhoneField.setBorder(valid ?
            UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border") :
            BorderFactory.createLineBorder(Color.RED));
        return valid;
    }

    private void save(boolean isSubmit) {
        // 验证输入
        if (!validateBuildingNo() || !validateRoomNo() || !validatePhone() ||
                descriptionArea.getText().trim().isEmpty()) {
            MessageDialog.showError(this, "请正确填写所有必填字段！");
            return;
        }

        try {
            if (existingOrder != null) {
                // 修改模式
                existingOrder.setBuildingNo(buildingNoField.getText().trim());
                existingOrder.setRoomNo(roomNoField.getText().trim());
                existingOrder.setDescription(descriptionArea.getText().trim());
                existingOrder.setImagePath(imagePathField.getText().trim());
                existingOrder.setContactPhone(contactPhoneField.getText().trim());

                repairOrderService.updateOrder(existingOrder);
                MessageDialog.showInfo(this, "报修单已修改！");
                submitted = true;
                dispose();
            } else {
                // 新建模式
                RepairOrder order = new RepairOrder();
                order.setBuildingNo(buildingNoField.getText().trim());
                order.setRoomNo(roomNoField.getText().trim());
                order.setDescription(descriptionArea.getText().trim());
                order.setImagePath(imagePathField.getText().trim());
                order.setContactPhone(contactPhoneField.getText().trim());
                order.setReporterId(currentUser.getId());
                order.setReportTime(new Date());

                if (isSubmit) {
                    order.setStatus(Constants.STATUS_PENDING);  // 直接提交
                    MessageDialog.showInfo(this, "报修单已提交！");
                } else {
                    order.setStatus(Constants.STATUS_DRAFT);  // 保存为草稿
                    MessageDialog.showInfo(this, "报修单已保存为草稿！");
                }

                repairOrderService.createOrder(order);
                submitted = true;
                dispose();
            }
        } catch (Exception e) {
            MessageDialog.showError(this, "保存失败：" + e.getMessage());
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}