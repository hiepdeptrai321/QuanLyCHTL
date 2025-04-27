package gui;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import connectDB.ConnectDB;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import com.toedter.calendar.JDateChooser;// Using java.util.Date for JTextField parsing/formatting simplicity

public class KhachHang_GUI extends JPanel implements ActionListener {

    private Connection conn;
    private KhachHang_DAO khachHangDAO;
    private DefaultTableModel tableModel;
    private JTable tableKhachHang;

    // Details Panel Components
    private JTextField txtMa, txtDiemTichLuy, txtHangThanhVien,txtDiaChi, txtSoLanMuaHang,
                       txtHoTen, txtSdt, txtEmail, txtMaNV;
	JDateChooser txtNgayDangKy;
	JDateChooser txtNamSinh;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private JSplitPane centerSplitPane;
	private JTextField txtSearch;
	private JButton btnSearch;

    public KhachHang_GUI() {
        // Cố gắng thiết lập Look and Feel đẹp hơn (ví dụ: Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // Hoặc "Windows", "Metal", "CDE/Motif"
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nếu không tìm thấy Look and Feel hoặc có lỗi, sử dụng default
            e.printStackTrace();
        }


        // Khởi tạo kết nối cơ sở dữ liệu
        ConnectDB.getInstance().connect();
        
        this.conn = ConnectDB.getConnection();
    
        if (this.conn != null) {
             this.khachHangDAO = new KhachHang_DAO(this.conn);
        } else {
            // Xử lý khi không kết nối được database, ví dụ hiển thị thông báo và thoát
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu. Ứng dụng sẽ thoát.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
        }

        initComponents();
        // Chỉ tải dữ liệu nếu DAO đã được khởi tạo thành công
        if (this.khachHangDAO != null) {
            loadCustomerData();
            addTableSelectionListener();
        } else {
           
             System.err.println("Không tải dữ liệu do lỗi kết nối database.");
             tableKhachHang.setEnabled(false); // Vô hiệu hóa bảng
             setFieldsEditable(false); 
             btnThem.setEnabled(false);
             btnSua.setEnabled(false);
             btnXoa.setEnabled(false);
        }
    }
    public static void main(String[] args) {
        // Chạy giao diện trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
        	JFrame frame = new JFrame("Quản Lý Khách Hàng"); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

            // 2. Tạo JPanel KhachHang_GUI
            KhachHang_GUI khachHangPanel = new KhachHang_GUI();

            frame.add(khachHangPanel);

            int dai = 1400; 
            int rong = 800; 
            frame.setSize(dai, rong);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true); 
        });
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // North Panel (Title)
        JPanel northPanel = new JPanel();
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(50, 100, 150));
        northPanel.add(lblTitle);
        this.add(northPanel, BorderLayout.NORTH);
        

        // Center Panel with JSplitPane
        centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.9);  
        centerSplitPane.setDividerSize(8);    
        centerSplitPane.setDividerLocation(0.9); 

        // Left Panel (Table)
        JPanel tablePanel = createTablePanel();
        centerSplitPane.setLeftComponent(tablePanel);

        // Right Panel (Details)
     // Create a container panel for the right side (statistics + details)
        JPanel rightSideContainer = new JPanel(new BorderLayout());

        // Create the customer statistics panel
        JPanel customerStatsPanel = createCustomerStatisticsPanel();

        // Create the details panel (this part remains the same)
        JPanel detailsPanel = createDetailsPanel();

        // Add the statistics panel to the NORTH of the container
        rightSideContainer.add(customerStatsPanel, BorderLayout.NORTH);

        // Add the details panel to the CENTER of the container
        rightSideContainer.add(detailsPanel, BorderLayout.CENTER); 

        centerSplitPane.setRightComponent(rightSideContainer);

        this.add(centerSplitPane, BorderLayout.CENTER); 

        setFieldsEditable(true);
        txtMa.setEditable(true);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        btnThem.setEnabled(true);
        btnXoaTrang.setEnabled(true);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Danh sách Khách Hàng"),
            new EmptyBorder(5, 5, 5, 5)));
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Điều chỉnh khoảng cách

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        txtSearch = new JTextField(56);  // Tạo text field cho ô tìm kiếm
        btnSearch = new JButton("Tìm kiếm"); // Tạo nút tìm kiếm

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

        String[] columnNames = {
            "Mã", "Họ tên", "SĐT", "Email", "Năm sinh", "Địa chỉ",
            "Ngày ĐK", "Điểm TL", "Hạng TV", "Số lần mua", "Mã NV"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };

        tableKhachHang = new JTable(tableModel);
        // Set table row sorter
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tableKhachHang.setRowSorter(sorter);

        // Tăng chiều cao dòng cho bảng
        tableKhachHang.setRowHeight(30);
        // Điều chỉnh font cho header bảng
        tableKhachHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableKhachHang.getTableHeader().setBackground(new Color(220, 220, 220));

        // Tạo sự kiện cho việc sắp xếp khi người dùng click vào cột
        tableKhachHang.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tableKhachHang.getColumnModel().getColumnIndexAtX(e.getX());
                if (column != -1) {
                    sorter.toggleSortOrder(column); // Thực hiện sắp xếp theo cột được chọn
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    private JPanel createCustomerStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Thống Kê "), // Tiêu đề cho panel thống kê khách hàng
                new EmptyBorder(5, 5, 5, 5)));

        // Thêm ComboBox với các phương thức truy vấn/lọc cho khách hàng
        String[] queryOptions = {
            "Tất cả khách hàng",
            "Khách hàng theo Hạng thành viên",
            "Khách hàng theo Mã NV tạo"
            // Có thể thêm các tùy chọn khác như: "Khách hàng có điểm > X", "Khách hàng có số lần mua > Y",...
        };
        JComboBox<String> comboQueryOptions = new JComboBox<>(queryOptions);
        comboQueryOptions.setSelectedIndex(0); // Mặc định chọn "Tất cả khách hàng"

        // Label điều kiện
        JLabel lblCondition = new JLabel("Bộ lọc:");

        // ComboBox để chọn điều kiện cụ thể (ví dụ: Hạng Vàng, Mã NV123)
        JComboBox<String> comboCondition = new JComboBox<>();
        comboCondition.setEnabled(false); // Không cho phép chọn khi chưa chọn một bộ lọc cụ thể

        // Thêm nút Reset/Tải lại
        JButton btnReset = new JButton("Tải lại bảng");

        // Panel cho ComboBox và nút
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Điều chỉnh khoảng cách
        topPanel.add(comboQueryOptions);
        topPanel.add(lblCondition);
        topPanel.add(comboCondition);
        topPanel.add(btnReset);

        panel.add(topPanel, BorderLayout.NORTH);

        // Hành động khi chọn phương thức truy vấn/lọc chính
        comboQueryOptions.addActionListener(e -> {
            String selectedOption = (String) comboQueryOptions.getSelectedItem();
            // Cập nhật dữ liệu cho combo điều kiện dựa trên lựa chọn chính
            updateQueryConditionComboBoxForCustomers(selectedOption, comboCondition);
            // Ngay lập tức cập nhật bảng dựa trên lựa chọn mới (và điều kiện mặc định đầu tiên nếu có)
             if (comboCondition.getItemCount() > 0 && comboCondition.isEnabled()) {
                 updateCustomerTableDataBasedOnQuery(selectedOption, comboCondition.getSelectedItem());
             } else {
                 updateCustomerTableDataBasedOnQuery(selectedOption, null); // Không có điều kiện cụ thể
             }
        });

        // Hành động khi chọn điều kiện cụ thể (ví dụ: chọn Hạng Vàng)
        comboCondition.addActionListener(e -> {
            String selectedOption = (String) comboQueryOptions.getSelectedItem();
            String selectedCondition = (String) comboCondition.getSelectedItem();
            // Chỉ cập nhật bảng nếu ComboBox điều kiện đang được bật
            if (comboCondition.isEnabled()) {
                updateCustomerTableDataBasedOnQuery(selectedOption, selectedCondition);
            }
        });


        // Hành động khi nhấn nút Reset
        btnReset.addActionListener(e -> {
            comboQueryOptions.setSelectedIndex(0); // Quay về tùy chọn "Tất cả"
            comboCondition.removeAllItems(); // Xóa các mục trong combo điều kiện
            comboCondition.setEnabled(false); // Tắt combo điều kiện
            loadCustomerData();  // Tải lại dữ liệu ban đầu vào bảng (tất cả khách hàng)
        });

        return panel;
    }
 // Phương thức để cập nhật dữ liệu cho JComboBox điều kiện dựa trên lựa chọn thống kê chính
    private void updateQueryConditionComboBoxForCustomers(String selectedOption, JComboBox<String> comboCondition) {
        comboCondition.removeAllItems();  
        comboCondition.setEnabled(false); 

        try {
            switch (selectedOption) {
                case "Tất cả khách hàng":
                    // Không cần điều kiện, tắt combobox
                    break;
                case "Khách hàng theo Hạng thành viên":
                    comboCondition.setEnabled(true);
                    // Gọi DAO để lấy danh sách các hạng thành viên khác nhau
                    List<String> memberRanks = khachHangDAO.getDistinctMemberRanks(); // Cần thêm phương thức này trong DAO
                    for (String rank : memberRanks) {
                        comboCondition.addItem(rank);
                    }
                    break;
                case "Khách hàng theo Mã NV tạo":
                     comboCondition.setEnabled(true);
                    // Gọi DAO để lấy danh sách các mã NV khác nhau đã tạo khách hàng
                    List<String> employeeIDs = khachHangDAO.getDistinctEmployeeIDs(); // Cần thêm phương thức này trong DAO
                     for (String empID : employeeIDs) {
                        comboCondition.addItem(empID);
                    }
                    break;
                // Thêm các case khác nếu có thêm tùy chọn lọc
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu cho ComboBox bộ lọc Khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
 // Phương thức để cập nhật dữ liệu trong tableKhachHang dựa trên lựa chọn thống kê/lọc
    private void updateCustomerTableDataBasedOnQuery(String selectedOption, Object condition) {
        tableModel.setRowCount(0);  // Xóa các dòng hiện tại trong bảng

        if (khachHangDAO == null) {
             System.err.println("KhachHangDAO chưa được khởi tạo. Không thể tải dữ liệu.");
             return;
        }

        try {
            List<KhachHang> khachHangList = null;
            switch (selectedOption) {
                case "Tất cả khách hàng":
                    khachHangList = khachHangDAO.getAll(); // Giả định phương thức này đã có
                    break;
                case "Khách hàng theo Hạng thành viên":
                    // Chắc chắn condition là String nếu ComboBox được bật và có item
                     if (condition instanceof String) {
                         khachHangList = khachHangDAO.getCustomersByMemberRank((String) condition); // Cần thêm phương thức này trong DAO
                     }
                    break;
                case "Khách hàng theo Mã NV tạo":
                    // Chắc chắn condition là String nếu ComboBox được bật và có item
                    if (condition instanceof String) {
                         khachHangList = khachHangDAO.getCustomersByEmployee((String) condition); // Cần thêm phương thức này trong DAO
                    }
                    break;
                // Thêm các case khác nếu có thêm tùy chọn lọc
            }

            // Cập nhật bảng với dữ liệu mới nếu danh sách không rỗng hoặc null
            if (khachHangList != null) {
                 for (KhachHang kh : khachHangList) {
                    Object[] rowData = {
                            kh.getMa(),
                            kh.getHoTen(),
                            kh.getSdt(),
                            kh.getEmail(),
                            // Cần định dạng Date nếu không nó sẽ hiển thị đối tượng Date
                            // Format Date to String "yyyy-MM-dd"
                            kh.getNamSinh() != null ? new SimpleDateFormat("yyyy-MM-dd").format(kh.getNamSinh()) : null,
                            kh.getDiaChi(),
                            // Format Date to String "yyyy-MM-dd"
                            kh.getNgayDangKy() != null ? new SimpleDateFormat("yyyy-MM-dd").format(kh.getNgayDangKy()) : null,
                            kh.getDiemTichLuy(),
                            kh.getHangThanhVien(),
                            kh.getSoLanMuaHang(),
                            kh.getMaNV()
                    };
                    tableModel.addRow(rowData);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu Khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Thông tin chi tiết Khách Hàng"),
            new EmptyBorder(5, 5, 5, 5)));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 5, 5); // Padding giữa các components
        gbc.anchor = GridBagConstraints.WEST; // Căn lề trái cho components

        // Khởi tạo Text Fields
        int tfCols = 25;
        txtMa = new JTextField(tfCols);
        txtHoTen = new JTextField(tfCols);
        txtSdt = new JTextField(tfCols);
        txtEmail = new JTextField(tfCols);
        txtNamSinh = new JDateChooser();  // Changed to JDateChooser
        txtDiaChi = new JTextField(tfCols); 
        txtNgayDangKy = new JDateChooser();  // Changed to JDateChooser
        txtDiemTichLuy = new JTextField(tfCols);
        txtHangThanhVien = new JTextField(tfCols);
        txtSoLanMuaHang = new JTextField(tfCols);
        txtMaNV = new JTextField(tfCols);

        // Thêm Tooltip cho các trường ngày tháng
//        txtNgayDangKy.setToolTipText("Định dạng: yyyy-MM-dd");
//        txtNamSinh.setToolTipText("Định dạng: yyyy-MM-dd");
        JPanel wrapFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapFields.add(fieldsPanel);

        int row = 0;


        addField(fieldsPanel, gbc, new JLabel("Mã KH:"), txtMa, row++);
        addField(fieldsPanel, gbc, new JLabel("Họ tên:"), txtHoTen, row++);
        addField(fieldsPanel, gbc, new JLabel("SĐT:"), txtSdt, row++);
        addField(fieldsPanel, gbc, new JLabel("Email:"), txtEmail, row++);
        addField(fieldsPanel, gbc, new JLabel("Năm sinh:"), txtNamSinh, row++);
        addField(fieldsPanel, gbc, new JLabel("Địa chỉ:"), txtDiaChi, row++); // Added DiaChi field
        addField(fieldsPanel, gbc, new JLabel("Ngày Đăng ký:"), txtNgayDangKy, row++);
        addField(fieldsPanel, gbc, new JLabel("Điểm tích lũy:"), txtDiemTichLuy, row++);
        addField(fieldsPanel, gbc, new JLabel("Hạng thành viên:"), txtHangThanhVien, row++);
        addField(fieldsPanel, gbc, new JLabel("Số lần mua hàng:"), txtSoLanMuaHang, row++);
        addField(fieldsPanel, gbc, new JLabel("Mã NV (người tạo):"), txtMaNV, row++);
        
        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        fieldsPanel.add(new JPanel(), gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); 
        Dimension buttonSize = new Dimension(100, 30); 
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnXoaTrang = new JButton("Xóa trắng");

        btnThem.setPreferredSize(buttonSize);
        btnSua.setPreferredSize(buttonSize);
        btnXoa.setPreferredSize(buttonSize);
        btnXoaTrang.setPreferredSize(buttonSize);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnXoaTrang);

     //   panel.add(fieldsPanel, BorderLayout.CENTER); 
        panel.add(wrapFields, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH); 

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnSearch.addActionListener(this);


        return panel;
    }

     // Helper method to add a label and a text field using GridBagLayout
    private void addField(JPanel panel, GridBagConstraints gbc, JLabel label, JComponent component, int row) {
        gbc.gridx = 0; // Cột 0 cho Label
        gbc.gridy = row; // Dòng hiện tại
        gbc.weightx = 0; 
        gbc.fill = GridBagConstraints.NONE; 
        panel.add(label, gbc);

        gbc.gridx = 1; // Cột 1 cho TextField
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        panel.add(component, gbc);
    }


    private void setFieldsEditable(boolean editable) {
    	txtHoTen.setEditable(editable);
        txtSdt.setEditable(editable);
        txtEmail.setEditable(editable);
       // txtNamSinh.setEditable(editable);
        txtDiaChi.setEditable(editable); 
       // txtNgayDangKy.setEditable(editable);
        txtDiemTichLuy.setEditable(editable);
        txtHangThanhVien.setEditable(editable);
        txtSoLanMuaHang.setEditable(editable);
        txtMaNV.setEditable(editable);
         // ko co txtma
    }

    private void loadCustomerData() {
        // Clear existing data
        tableModel.setRowCount(0);
        try {
            if (khachHangDAO != null) {
                List<KhachHang> khachHangList = khachHangDAO.getAll();
                for (KhachHang kh : khachHangList) {
                	Object[] rowData = {
                            kh.getMa(),
                            kh.getHoTen(),
                            kh.getSdt(),
                            kh.getEmail(),
                            kh.getNamSinh() != null ? dateFormat.format(kh.getNamSinh()) : "",
                            kh.getDiaChi(), // Added DiaChi data
                            kh.getNgayDangKy() != null ? dateFormat.format(kh.getNgayDangKy()) : "",
                            kh.getDiemTichLuy(),
                            kh.getHangThanhVien(),
                            kh.getSoLanMuaHang(),
                            kh.getMaNV()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTableSelectionListener() {
        tableKhachHang.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tableKhachHang.getSelectedRow();
                    if (selectedRow != -1) {
                        String maKhachHang = tableModel.getValueAt(selectedRow, 0).toString();
                        try {
                            if (khachHangDAO != null) {
                                KhachHang selectedKhachHang = khachHangDAO.getById(maKhachHang);
                                if (selectedKhachHang != null) {
                                    populateDetailsPanel(selectedKhachHang); // Populate details when row is selected
                                    setFieldsEditable(true); 
                                    txtMa.setEditable(false); 
                                    btnSua.setEnabled(true);
                                    btnXoa.setEnabled(true);                                  
                                    btnThem.setEnabled(false);
                                } else {
                                    clearFields();
                                    setFieldsEditable(true); 
                                    txtMa.setEditable(false);
                                    btnSua.setEnabled(false);
                                    btnXoa.setEnabled(false);
                                    btnThem.setEnabled(true);
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(KhachHang_GUI.this, "Lỗi khi lấy thông tin chi tiết khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                            clearFields();
                            setFieldsEditable(false);
                            txtMa.setEditable(false);
                            btnSua.setEnabled(false);
                            btnXoa.setEnabled(false);
                            btnThem.setEnabled(true);
                        }
                    } else {
                        clearFields();                        
                        setFieldsEditable(true);
                        txtMa.setEditable(true);
                        btnThem.setEnabled(true);
                        btnXoaTrang.setEnabled(true);
                        btnSua.setEnabled(false);
                        btnXoa.setEnabled(false);
                    }
                }
            }
        });
    }

    private void populateDetailsPanel(KhachHang kh) {
        txtMa.setText(kh.getMa());
        txtHoTen.setText(kh.getHoTen());
        txtSdt.setText(kh.getSdt());
        txtEmail.setText(kh.getEmail());
        
        if (kh.getNamSinh() != null) {
            txtNamSinh.setDate(kh.getNamSinh()); 
        } else {
            txtNamSinh.setDate(null); 
        }
        
        txtDiaChi.setText(kh.getDiaChi()); 
        
        if (kh.getNgayDangKy() != null) {
            txtNgayDangKy.setDate(kh.getNgayDangKy()); 
        } else {
            txtNgayDangKy.setDate(null); 
        }
        
        txtDiemTichLuy.setText(String.valueOf(kh.getDiemTichLuy()));
        txtHangThanhVien.setText(String.valueOf(kh.getHangThanhVien()));
        txtSoLanMuaHang.setText(String.valueOf(kh.getSoLanMuaHang()));
        txtMaNV.setText(kh.getMaNV());
    }
    private void clearFields() {
        txtMa.setText("");        
        txtDiemTichLuy.setText("");
        txtHangThanhVien.setText("");
        txtSoLanMuaHang.setText("");
        txtHoTen.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        txtMaNV.setText("");
        txtNamSinh.setDate(null);  
        txtNgayDangKy.setDate(null);
        txtDiaChi.setText("");
        
        setFieldsEditable(true);
        txtMa.setEditable(true); 
        tableKhachHang.clearSelection(); 
        txtMa.requestFocus(); 
        btnThem.setEnabled(true);
        btnXoaTrang.setEnabled(true);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (khachHangDAO == null) { 
             JOptionPane.showMessageDialog(this, "Ứng dụng chưa kết nối cơ sở dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
        }
        if (o == btnThem)
        	them();
        else if(o == btnSua)
        	sua();
        else if(o == btnXoa)
        	xoa();
        else if(o == btnXoaTrang)
        	xoatrang();
        else if (o == btnSearch) { 
        	String searchText = txtSearch.getText().trim();
            if (!searchText.isEmpty()) {
                searchCustomers(searchText); 
            } else {
                loadCustomerData();  
            }
        }
    }
    private void searchCustomers(String searchText) {
        try {
            List<KhachHang> filteredCustomers = khachHangDAO.searchByName(searchText); // Tìm kiếm theo tên
            updateTable(filteredCustomers);  
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm khách hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<KhachHang> customers) {
        tableModel.setRowCount(0);  
        for (KhachHang kh : customers) {
            Object[] rowData = {
                kh.getMa(),
                kh.getHoTen(),
                kh.getSdt(),
                kh.getEmail(),
                kh.getNamSinh() != null ? dateFormat.format(kh.getNamSinh()) : "",
                kh.getDiaChi(),
                kh.getNgayDangKy() != null ? dateFormat.format(kh.getNgayDangKy()) : "",
                kh.getDiemTichLuy(),
                kh.getHangThanhVien(),
                kh.getSoLanMuaHang(),
                kh.getMaNV()
            };
            tableModel.addRow(rowData); 
        }
    }

        private boolean validData() {
            // Lấy dữ liệu trực tiếp từ các trường nhập liệu
            String ma = txtMa.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSdt.getText().trim();
            String email = txtEmail.getText().trim();
            // Lưu ý: JDateChooser trả về java.util.Date. Cần kiểm tra null.
            Date namSinhDate = txtNamSinh.getDate();
            Date ngayDangKyDate = txtNgayDangKy.getDate();
            String diaChi = txtDiaChi.getText().trim();
            String diemTichLuyStr = txtDiemTichLuy.getText().trim();
            String hangThanhVienStr = txtHangThanhVien.getText().trim();
            String soLanMuaHangStr = txtSoLanMuaHang.getText().trim();
            String maNV = txtMaNV.getText().trim();


            // Validate required fields 
            if (ma.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã Khách Hàng không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                txtMa.requestFocus(); 
                return false;
            }
            if (hoTen.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Họ tên không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                 txtHoTen.requestFocus(); 
                 return false;
             }
            if (sdt.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                 txtSdt.requestFocus(); 
                 return false;
             }
             if (email.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Email không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                 txtEmail.requestFocus(); 
                 return false;
             }
             if (namSinhDate == null) {
                  JOptionPane.showMessageDialog(this, "Năm sinh không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtNamSinh.requestFocus(); 
                  return false;
              }
             if (diaChi.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtDiaChi.requestFocus(); 
                  return false;
              }
             if (ngayDangKyDate == null) {
                  JOptionPane.showMessageDialog(this, "Ngày đăng ký không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtNgayDangKy.requestFocus(); 
                  return false;
              }
             if (diemTichLuyStr.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "Điểm tích lũy không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtDiemTichLuy.requestFocus(); 
                  return false;
              }
             if (hangThanhVienStr.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "Hạng thành viên không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtHangThanhVien.requestFocus(); 
                  return false;
              }
             if (soLanMuaHangStr.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "Số lần mua hàng không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtSoLanMuaHang.requestFocus();
                  return false;
              }
             if (maNV.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "Mã NV tạo không được để trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                  txtMaNV.requestFocus(); 
                  return false;
              }

            if (!ma.startsWith("KH")) {
                JOptionPane.showMessageDialog(this, "Mã Khách Hàng phải bắt đầu bằng \"KH\".", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
                txtMa.requestFocus(); 
                return false;
            }

    		if (!sdt.matches("^\\d{10,11}$")) {
    			JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (phải là 10-11 chữ số).", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
    			txtSdt.requestFocus(); 
    			return false;
    		}

    		// Validate email format
    		if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
    			JOptionPane.showMessageDialog(this, "Địa chỉ email không hợp lệ.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
    			txtEmail.requestFocus(); // Set focus
    			return false;
    		}

    		// Validate number fields and non-negative values
    		int diemTichLuy, hangThanhVien, soLanMuaHang;
    		try {
    			diemTichLuy = Integer.parseInt(diemTichLuyStr);
                 
    			hangThanhVien = Integer.parseInt(hangThanhVienStr);
    			soLanMuaHang = Integer.parseInt(soLanMuaHangStr);

    			if (diemTichLuy < 0) {
    				JOptionPane.showMessageDialog(this, "Điểm tích lũy không được âm.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
    				txtDiemTichLuy.requestFocus(); 
    				return false;
    			}
                if (hangThanhVien < 0) {
                     JOptionPane.showMessageDialog(this, "Hạng thành viên không được âm.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                     txtHangThanhVien.requestFocus(); 
                     return false;
                }
                if (soLanMuaHang < 0) {
                    JOptionPane.showMessageDialog(this, "Số lần mua hàng không được âm.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                    txtSoLanMuaHang.requestFocus(); 
                    return false;
                }

    		} catch (NumberFormatException e) {
                if (!diemTichLuyStr.matches("-?\\d+")) { 
                     JOptionPane.showMessageDialog(this, "Điểm tích lũy phải là số nguyên.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
                     txtDiemTichLuy.requestFocus();
                } else if (!hangThanhVienStr.matches("-?\\d+")) {
                     JOptionPane.showMessageDialog(this, "Hạng thành viên phải là số nguyên.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
                     txtHangThanhVien.requestFocus();
                } else if (!soLanMuaHangStr.matches("-?\\d+")) {
                     JOptionPane.showMessageDialog(this, "Số lần mua hàng phải là số nguyên.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
                     txtSoLanMuaHang.requestFocus();
                } else {
                     
                     JOptionPane.showMessageDialog(this, "Dữ liệu số không hợp lệ ở một trong các trường Điểm, Hạng, Số lần mua.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
                     
                     txtDiemTichLuy.requestFocus();
                }
    			return false; 
    		}

    		return true;
    	}

    private void them() {
        // Chỉ gọi validData()
        if (!validData()) {
            return; // Nếu dữ liệu không hợp lệ, dừng lại và không làm gì thêm
        }

        // Dữ liệu đã hợp lệ, lấy dữ liệu từ các trường
        String ma = txtMa.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        Date namSinhDate = txtNamSinh.getDate();
        Date ngayDangKyDate = txtNgayDangKy.getDate();
        String diaChi = txtDiaChi.getText().trim();
        String diemTichLuyStr = txtDiemTichLuy.getText().trim();
        String hangThanhVienStr = txtHangThanhVien.getText().trim();
        String soLanMuaHangStr = txtSoLanMuaHang.getText().trim();
        String maNV = txtMaNV.getText().trim();

        java.sql.Date ngayDangKySql = new java.sql.Date(ngayDangKyDate.getTime());
        java.sql.Date namSinhSql = new java.sql.Date(namSinhDate.getTime());

        // Tạo đối tượng KhachHangMoi
        KhachHang khachHangMoi = new KhachHang(
            ma, hoTen, sdt, email, namSinhSql, diaChi,
            ngayDangKySql, Integer.parseInt(diemTichLuyStr), Integer.parseInt(hangThanhVienStr), Integer.parseInt(soLanMuaHangStr), maNV
        );

        try {
            boolean success = khachHangDAO.insert(khachHangMoi);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng mới thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại. Có thể mã khách hàng đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) { // Common state code for integrity constraint violation (duplicate key)
                JOptionPane.showMessageDialog(this, "Lỗi: Mã khách hàng '" + ma + "' đã tồn tại.", "Lỗi Trùng Mã", JOptionPane.ERROR_MESSAGE);
                txtMa.requestFocus(); 
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng vào cơ sở dữ liệu: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void sua() {
        int selectedRow = tableKhachHang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validData()) {
            return; 
        }

        String ma = txtMa.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        Date namSinhDate = txtNamSinh.getDate();
        Date ngayDangKyDate = txtNgayDangKy.getDate();
        String diaChi = txtDiaChi.getText().trim();
        String diemTichLuyStr = txtDiemTichLuy.getText().trim();
        String hangThanhVienStr = txtHangThanhVien.getText().trim();
        String soLanMuaHangStr = txtSoLanMuaHang.getText().trim();
        String maNV = txtMaNV.getText().trim();


        // Chuyển đổi java.util.Date sang java.sql.Date
        java.sql.Date ngayDangKySql = new java.sql.Date(ngayDangKyDate.getTime());
        java.sql.Date namSinhSql = new java.sql.Date(namSinhDate.getTime());

        // Tạo đối tượng KhachHangCapNhat
        KhachHang khachHangCapNhat = new KhachHang(
            ma, hoTen, sdt, email, namSinhSql, diaChi,
            ngayDangKySql, Integer.parseInt(diemTichLuyStr), Integer.parseInt(hangThanhVienStr), Integer.parseInt(soLanMuaHangStr), maNV
        );

        try {
            boolean success = khachHangDAO.update(khachHangCapNhat);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData(); 
                clearFields();      
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Khách hàng với mã '" + ma + "' không còn tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                loadCustomerData(); 
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi SQL khi cập nhật khách hàng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        int selectedRow = tableKhachHang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy mã khách hàng từ dòng được chọn (hoặc từ trường txtMa - đảm bảo khớp)
        String maKHToDelete = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng có mã: " + maKHToDelete + " không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = khachHangDAO.delete(maKHToDelete); // Xóa khách hàng
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadCustomerData(); 
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại. Khách hàng có thể không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   private void xoatrang() {
       clearFields();
   }
}
