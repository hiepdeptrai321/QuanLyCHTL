package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import connectDB.ConnectDB;
import dao.SanPham_DAO;
import entity.SanPham;

public class SanPham_GUI extends JPanel implements ActionListener, MouseListener {

    private Connection conn;
    private SanPham_DAO sanPhamDAO; 
    private DefaultTableModel tableModel;
    private JTable tableSanPham; 

    // Details Panel Components (corresponding to SanPham attributes)
    private JTextField txtMaSP, txtTenSP, txtGiaBan, txtGiaGoc, txtMaLoai, txtMaNH, txtMaNCC, txtMaNQL;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;
	private JComboBox comboMaLoai;
	private JComboBox comboMaNH;
	private JComboBox comboMaNCC;
	private JTextField txtSearch;
	private JButton btnSearch;

    // SimpleDateFormat is not needed for SanPham as it has no Date fields
    // private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SanPham_GUI() {
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


        
        ConnectDB.getInstance().connect();
        
        this.conn = ConnectDB.getConnection();
        
        if (this.conn != null) {
             this.sanPhamDAO = new SanPham_DAO(this.conn); 
        } else {
            
            JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu. Ứng dụng sẽ thoát.", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            System.exit(1); 
        }
        setPreferredSize(new Dimension(1200, 700));

        initComponents();
        
        if (this.sanPhamDAO != null) { 
            loadProductData(); 
            addTableMouseListener();
        } else {
             System.err.println("Không tải dữ liệu do lỗi kết nối database.");
             tableSanPham.setEnabled(false); 
            // setFieldsEditable(false); // Vô hiệu hóa các trường nhập liệu
             // Vô hiệu hóa các nút chức năng liên quan đến dữ liệu
             btnThem.setEnabled(false);
             btnSua.setEnabled(false);
             btnXoa.setEnabled(false);
             // btnXoaTrang vẫn có thể giữ lại để xóa trắng các trường đã nhập (nếu có)
        }
    }
    public static void main(String[] args) {
        // Chạy giao diện trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
        	JFrame frame = new JFrame("Quản Lý Sản Phẩm"); 
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SanPham_GUI sanPhamPanel = new SanPham_GUI();

            frame.add(sanPhamPanel); // Thêm panel vào content pane mặc định của frame

            // 4. Đặt các thuộc tính khác cho JFrame và hiển thị
            frame.setSize(1200, 700); 
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true); 
        });
    }

    private void initComponents() {
        // Main Panel with BorderLayout
    	this.setLayout(new BorderLayout()); // Ví dụ dùng BorderLayout
        this.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding cho JPanel chính

        // North Panel (Title)
        JPanel northPanel = new JPanel();
        JLabel lblTitle = new JLabel("QUẢN LÝ SẢN PHẨM"); 
        // Tăng kích thước và kiểu chữ cho tiêu đề
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(50, 100, 150)); 
        northPanel.add(lblTitle);
        this.add(northPanel, BorderLayout.NORTH);

        // Center Panel with JSplitPane
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.9); 
        centerSplitPane.setDividerSize(8); 

        // Left Panel (Table)
        JPanel tablePanel = createTablePanel();
        centerSplitPane.setLeftComponent(tablePanel);

        // Right Panel (Details)
        JPanel detailsPanel = createDetailsPanel();
        JPanel statisticsPanel = createStatisticsPanel(); // Thêm panel thống kê
        JSplitPane rightPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightPanelSplit.setResizeWeight(0.2);
        rightPanelSplit.setDividerSize(8);
        rightPanelSplit.setTopComponent(statisticsPanel);
        rightPanelSplit.setBottomComponent(detailsPanel);

        centerSplitPane.setRightComponent(rightPanelSplit);

        this.add(centerSplitPane, BorderLayout.CENTER);


       // setFieldsEditable(false);
        txtMaSP.setEditable(true); 
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        btnThem.setEnabled(true);
        btnXoaTrang.setEnabled(true);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Thêm padding và TitledBorder
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Danh sách Sản Phẩm"), // Update title
            new EmptyBorder(5, 5, 5, 5)));
        
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Padding cho các components

        JLabel lblSearch = new JLabel("Tìm Sản Phẩm:");
        txtSearch = new JTextField(40); 
        btnSearch = new JButton("Tìm");
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {  
                    String searchText = txtSearch.getText().trim();
                    if (!searchText.isEmpty()) {
                        searchProducts(searchText);  
                    } else {
                        loadProductData();  
                    }
                }
            }
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Update column names for SanPham
        String[] columnNames = {"Mã SP", "Tên SP", "Giá bán(đ)", "Giá gốc(đ)", "Mã loại", "Mã NH", "Mã NCC", "Mã NQL"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };
        tableSanPham = new JTable(tableModel); // Use tableSanPham
        // Tăng chiều cao dòng cho bảng
        tableSanPham.setRowHeight(30);
        // Điều chỉnh font cho header bảng
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tableSanPham.setRowSorter(sorter); // Đổi từ tableKhachHang thành tableSanPham
        
        tableSanPham.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tableSanPham.getColumnModel().getColumnIndexAtX(e.getX());
                if (column != -1) {
                    sorter.toggleSortOrder(column); // Thực hiện sắp xếp theo cột được chọn
                }
            }
        });
        
        tableSanPham.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableSanPham.getTableHeader().setBackground(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tableSanPham); // Wrap tableSanPham
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Thống Kê"),
                new EmptyBorder(5, 5, 5, 5)));

        // Thêm ComboBox với các phương thức truy vấn
        String[] queryOptions = {"Tất cả sản phẩm", "Sản phẩm theo loại", "Sản phẩm theo nhà cung cấp", "Sản phẩm theo nhãn hàng"};
        JComboBox<String> comboQueryOptions = new JComboBox<>(queryOptions);
        comboQueryOptions.setSelectedIndex(0);

        // Label điều kiện
        JLabel lblCondition = new JLabel("Bộ lọc:");

        // ComboBox để chọn điều kiện cụ thể
        JComboBox<String> comboCondition = new JComboBox<>();
        comboCondition.setEnabled(false); // Không cho phép chọn khi chưa chọn "Sản phẩm theo loại", "Sản phẩm theo nhà cung cấp",...

        // Thêm nút Reset
        JButton btnReset = new JButton("Tải lại bảng");

        // Panel cho ComboBox và nút
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(comboQueryOptions);
        topPanel.add(lblCondition);
        topPanel.add(comboCondition);
        topPanel.add(btnReset);

        panel.add(topPanel, BorderLayout.NORTH);

        // Hành động khi chọn phương thức truy vấn
        comboQueryOptions.addActionListener(e -> {
            String selectedOption = (String) comboQueryOptions.getSelectedItem();
            updateQueryConditionComboBox(selectedOption, comboCondition); // Cập nhật dữ liệu cho combo điều kiện
            updateTableDataBasedOnQuery(selectedOption, comboCondition.getSelectedItem());
        });
        comboCondition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) comboQueryOptions.getSelectedItem();
                String selectedCondition = (String) comboCondition.getSelectedItem();  // Get the selected condition
                updateTableDataBasedOnQuery(selectedOption, selectedCondition);  // Pass the selected condition to update the table
            }
        });

        // Hành động khi nhấn nút Reset
        btnReset.addActionListener(e -> {
            loadProductData();  // Tải lại dữ liệu ban đầu vào bảng
        });

        return panel;
    }
    private void updateQueryConditionComboBox(String selectedOption, JComboBox<String> comboCondition) {
        comboCondition.removeAllItems();  // Xóa các mục cũ

        try {
            switch (selectedOption) {
                case "Tất cả sản phẩm":
                    comboCondition.setEnabled(false);
                    break;
                case "Sản phẩm theo loại":
                    comboCondition.setEnabled(true);
                    List<String> maLoaiList = sanPhamDAO.getMaLoaiList();  
                    for (String maLoai : maLoaiList) {
                        comboCondition.addItem(maLoai);
                    }
                    break;
                case "Sản phẩm theo nhà cung cấp":
                    comboCondition.setEnabled(true);
                    List<String> maNCCList = sanPhamDAO.getMaNCCList();  
                    for (String maNCC : maNCCList) {
                        comboCondition.addItem(maNCC);
                    }
                    break;
                case "Sản phẩm theo nhãn hàng":
                    comboCondition.setEnabled(true);
                    List<String> maNHList = sanPhamDAO.getMaNHList(); 
                    for (String maNH : maNHList) {
                        comboCondition.addItem(maNH);
                    }
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu cho ComboBox.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableDataBasedOnQuery(String selectedOption, Object condition) {
        tableModel.setRowCount(0);  // Xóa các dòng hiện tại trong bảng
        try {
            List<SanPham> sanPhamList = null;
            switch (selectedOption) {
                case "Tất cả sản phẩm":
                    sanPhamList = sanPhamDAO.getAll(); // Lấy tất cả sản phẩm
                    break;
                case "Sản phẩm theo loại":
                    sanPhamList = sanPhamDAO.getProductsByCategory((String) condition); // Lọc theo loại
                    break;
                case "Sản phẩm theo nhà cung cấp":
                    sanPhamList = sanPhamDAO.getProductsBySupplier((String) condition); // Lọc theo nhà cung cấp
                    break;
                case "Sản phẩm theo nhãn hàng":
                    sanPhamList = sanPhamDAO.getProductsByBrand((String) condition); // Lọc theo nhãn hàng
                    break;
            }

            // Cập nhật bảng với dữ liệu mới
            for (SanPham sp : sanPhamList) { 
                Object[] rowData = {
                        sp.getMaSP(),
                        sp.getTenSP(),
                        sp.getGiaBan(),
                        sp.getGiaGoc(),
                        sp.getMaLoai(),
                        sp.getMaNH(),
                        sp.getMaNCC(),
                        sp.getMaNQL()
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức cải tiến sử dụng GridBagLayout cho panel chi tiết
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Thông tin chi tiết Sản Phẩm"), // Update title
            new EmptyBorder(5, 5, 5, 5)));

        // Panel chứa các trường nhập liệu dùng GridBagLayout
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding giữa các components
        gbc.anchor = GridBagConstraints.WEST; // Căn lề trái cho components

        // Initialize JComboBox for SanPham attributes
        comboMaLoai = new JComboBox<>();
        comboMaNH = new JComboBox<>();
        comboMaNCC = new JComboBox<>();
        

        // Load combo box data
        loadComboBoxData();
        comboMaLoai.setSelectedIndex(-1); 
        comboMaNH.setSelectedIndex(-1);  
        comboMaNCC.setSelectedIndex(-1); 
        // Khởi tạo Text Fields for SanPham attributes
        txtMaSP = new JTextField(25);
        txtTenSP = new JTextField(25);
        txtGiaBan = new JTextField(25);
        txtGiaGoc = new JTextField(25);
        txtMaNQL = new JTextField(25);


        // No Date fields, so no Tooltips for date format needed


        // --- Thêm các cặp Label-TextField vào fieldsPanel dùng GridBagLayout ---
        int row = 0;
        addField(fieldsPanel, gbc, new JLabel("Mã SP:"), txtMaSP, row++); // Use MaSP
        addField(fieldsPanel, gbc, new JLabel("Tên SP:"), txtTenSP, row++); // Use TenSP
        addField(fieldsPanel, gbc, new JLabel("Giá bán(đ):"), txtGiaBan, row++); // Use GiaBan
        addField(fieldsPanel, gbc, new JLabel("Giá gốc(đ):"), txtGiaGoc, row++); // Use GiaGoc
        addField(fieldsPanel, gbc, new JLabel("Mã loại:"),comboMaLoai, row++); // Use MaLoai
        addField(fieldsPanel, gbc, new JLabel("Mã NH:"), comboMaNH, row++);   // Use MaNH
        addField(fieldsPanel, gbc, new JLabel("Mã NCC:"), comboMaNCC, row++); // Use MaNCC
        addField(fieldsPanel, gbc, new JLabel("Mã NQL:"), txtMaNQL, row++); // Use MaNQL


        // Add some vertical space before buttons
         gbc.gridx = 0;
         gbc.gridy = row;
         gbc.gridwidth = 2; // Span across two columns
         gbc.weighty = 1.0; // Push everything above this up
         gbc.fill = GridBagConstraints.VERTICAL;
         fieldsPanel.add(Box.createVerticalGlue(), gbc); // Use a vertical glue to push fields up


        // Panel chứa các nút dùng FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Tăng khoảng cách giữa các nút

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnXoaTrang = new JButton("Xóa trắng");

        Dimension buttonSize = new Dimension(100, 30); 
        btnSua.setPreferredSize(buttonSize);
        btnXoa.setPreferredSize(buttonSize);
        btnXoaTrang.setPreferredSize(buttonSize);

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnXoaTrang);

        panel.add(fieldsPanel, BorderLayout.CENTER); 
        panel.add(buttonPanel, BorderLayout.SOUTH); 

        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaTrang.addActionListener(this);
        btnSearch.addActionListener(this);


        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, JLabel label, JComponent component, int row) {
        gbc.gridx = 0; // Column 0 for Label
        gbc.gridy = row; // Current row
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; // Label doesn't fill the cell
        panel.add(label, gbc);

        gbc.gridx = 1; // Column 1 for Component
        gbc.weightx = 1.0; // Component takes the remaining space
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill the cell horizontally
        panel.add(component, gbc);
    }


    // Bật/tắt khả năng chỉnh sửa các trường nhập liệu (trừ mã SP)
    private void setFieldsEditable(boolean editable) {
         txtTenSP.setEditable(editable);
         txtGiaBan.setEditable(editable);
         txtGiaGoc.setEditable(editable);
         txtMaLoai.setEditable(editable);
         txtMaNH.setEditable(editable);
         txtMaNCC.setEditable(editable);
         txtMaNQL.setEditable(editable);
         // txtMaSP is handled separately
    }

    private void loadProductData() {
        tableModel.setRowCount(0);
        try {
            if (sanPhamDAO != null) {
                List<SanPham> sanPhamList = sanPhamDAO.getAll(); 
                for (SanPham sp : sanPhamList) { 
                    Object[] rowData = {
                            sp.getMaSP(),
                            sp.getTenSP(),
                            sp.getGiaBan(),
                            sp.getGiaGoc(), 
                            sp.getMaLoai(),
                            sp.getMaNH(),
                            sp.getMaNCC(),
                            sp.getMaNQL()
                    };
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadComboBoxData() {
        try {
            // Load data for 'maLoai' combo box
            List<String> maLoaiList = sanPhamDAO.getMaLoaiList();
            for (String maLoai : maLoaiList) {
                comboMaLoai.addItem(maLoai);
            }

            // Load data for 'maNH' combo box
            List<String> maNHList = sanPhamDAO.getMaNHList();
            for (String maNH : maNHList) {
                comboMaNH.addItem(maNH);
            }

            // Load data for 'maNCC' combo box
            List<String> maNCCList = sanPhamDAO.getMaNCCList();
            for (String maNCC : maNCCList) {
                comboMaNCC.addItem(maNCC);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu cho ComboBox.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTableMouseListener() { 
        tableSanPham.addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableSanPham.rowAtPoint(e.getPoint());

                if (selectedRow != -1) {                  
                    tableSanPham.setRowSelectionInterval(selectedRow, selectedRow);
                    String maSanPham = tableModel.getValueAt(selectedRow, 0).toString();
                    try {
                         if (sanPhamDAO != null) {
                            SanPham selectedSanPham = sanPhamDAO.getById(maSanPham); 
                            if (selectedSanPham != null) {
                                populateDetailsPanel(selectedSanPham); 
                                //setFieldsEditable(true); 
                                txtMaSP.setEditable(false); 
                                btnSua.setEnabled(true);
                                btnXoa.setEnabled(true);
                                btnThem.setEnabled(false); 

                            } else {
                                clearFields();
                               // setFieldsEditable(false); 
                                txtMaSP.setEditable(false);
                                btnSua.setEnabled(false);
                                btnXoa.setEnabled(false);
                                btnThem.setEnabled(false); 
                            }	
                         	}
                         } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(SanPham_GUI.this, "Lỗi khi lấy thông tin chi tiết sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        clearFields();
                       // setFieldsEditable(false);
                        txtMaSP.setEditable(false);
                        btnSua.setEnabled(false);
                        btnXoa.setEnabled(false);
                        btnThem.setEnabled(true);
                    }
                } else {                   
                    tableSanPham.clearSelection();
                    clearFields();                   
                   // setFieldsEditable(true);
                    txtMaSP.setEditable(true);                  
                    btnThem.setEnabled(true);
                    btnXoaTrang.setEnabled(true);
                    btnSua.setEnabled(false);
                    btnXoa.setEnabled(false);
                }
            }
            @Override
             public void mousePressed(MouseEvent e) {}
            @Override
             public void mouseReleased(MouseEvent e) {}
            @Override
             public void mouseEntered(MouseEvent e) {}
            @Override
             public void mouseExited(MouseEvent e) {}
        });
    }
    //dien thong tin
    private void populateDetailsPanel(SanPham sp) { 
        txtMaSP.setText(sp.getMaSP());
        txtTenSP.setText(sp.getTenSP());
        txtGiaBan.setText(String.valueOf(sp.getGiaBan())); 
        txtGiaGoc.setText(String.valueOf(sp.getGiaGoc())); 
        comboMaLoai.setSelectedItem(sp.getMaLoai()); 
        comboMaNH.setSelectedItem(sp.getMaNH());   
        comboMaNCC.setSelectedItem(sp.getMaNCC()); 
        txtMaNQL.setText(sp.getMaNQL());
    }

    private void clearFields() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtGiaBan.setText("");
        txtGiaGoc.setText("");
        comboMaLoai.setSelectedIndex(-1); 
        comboMaNH.setSelectedIndex(-1);    
        comboMaNCC.setSelectedIndex(-1);
        txtMaNQL.setText("");

         
       // setFieldsEditable(true);
        txtMaSP.setEditable(true); 
        tableSanPham.clearSelection(); 
        txtMaSP.requestFocus();       
        btnThem.setEnabled(true);
        btnXoaTrang.setEnabled(true);

        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (sanPhamDAO == null) { 
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
                searchProducts(searchText);  
            } else {
                loadProductData();  
            }
        }
    }
    private void searchProducts(String searchText) {
        try {
            List<SanPham> searchedProducts = sanPhamDAO.searchProducts(searchText);  // Lọc sản phẩm theo từ khóa
            updateTableData(searchedProducts); // Cập nhật bảng với kết quả tìm kiếm
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableData(List<SanPham> sanPhamList) {
        tableModel.setRowCount(0);  
        for (SanPham sp : sanPhamList) {
            Object[] rowData = {
                sp.getMaSP(),
                sp.getTenSP(),
                sp.getGiaBan(),
                sp.getGiaGoc(),
                sp.getMaLoai(),
                sp.getMaNH(),
                sp.getMaNCC(),
                sp.getMaNQL()
            };
            tableModel.addRow(rowData);  // Thêm vào bảng
        }
    }
    private boolean validData() {
        String maSP = txtMaSP.getText().trim();
        String tenSP = txtTenSP.getText().trim();
        String giaBanStr = txtGiaBan.getText().trim();
        String giaGocStr = txtGiaGoc.getText().trim();
        Object selectedMaLoai = comboMaLoai.getSelectedItem();
        Object selectedMaNH = comboMaNH.getSelectedItem();
        Object selectedMaNCC = comboMaNCC.getSelectedItem();
        String maNQL = txtMaNQL.getText().trim(); 

        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Sản Phẩm không được để trống.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus(); 
            return false;
        }
        if (!maSP.matches("SP\\d+")) {
            JOptionPane.showMessageDialog(this, "Mã Sản Phẩm phải bắt đầu bằng 'SP'(VD: SP05)", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            txtMaSP.requestFocus();
            return false;
        }

        if (tenSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtTenSP.requestFocus();
            return false;
        }

        if (giaBanStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá bán không được để trống.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtGiaBan.requestFocus();
            return false;
        }
        try {
            double giaBan = Double.parseDouble(giaBanStr);
            if (giaBan <= 0) {
                JOptionPane.showMessageDialog(this, "Giá bán phải là số dương.", "Lỗi giá trị", JOptionPane.WARNING_MESSAGE);
                txtGiaBan.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là một số hợp lệ (ví dụ: 150000.50).", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            txtGiaBan.requestFocus();
            return false;
        }

        if (giaGocStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giá gốc không được để trống.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtGiaGoc.requestFocus();
            return false;
        }
        try {
            double giaGoc = Double.parseDouble(giaGocStr);
            if (giaGoc <= 0) {
                JOptionPane.showMessageDialog(this, "Giá gốc phải là số dương.", "Lỗi giá trị", JOptionPane.WARNING_MESSAGE);
                txtGiaGoc.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá gốc phải là một số hợp lệ (ví dụ: 100000).", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            txtGiaGoc.requestFocus();
            return false;
        }

        if (selectedMaLoai == null || selectedMaLoai.toString().trim().isEmpty()) { // Kiểm tra cả trường hợp chọn mục rỗng nếu có
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã Loại.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            comboMaLoai.requestFocus();
            return false;
        }
        if (selectedMaNH == null || selectedMaNH.toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã Nhãn Hàng.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            comboMaNH.requestFocus();
            return false;
        }
        if (selectedMaNCC == null || selectedMaNCC.toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Mã Nhà Cung Cấp.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            comboMaNCC.requestFocus();
            return false;
        }

        if (maNQL.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Người Quản Lý không được để trống.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            txtMaNQL.requestFocus();
            return false;
        }
        if (!maNQL.matches("QL\\d+")) {
            JOptionPane.showMessageDialog(this, "Mã Người Quản Lí phải bắt đầu bằng 'QL'", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            txtMaNQL.requestFocus();
            return false;
        }
        
        try {
           double giaBan = Double.parseDouble(giaBanStr);
           double giaGoc = Double.parseDouble(giaGocStr);
           if (giaBan < giaGoc) {
               int choice = JOptionPane.showConfirmDialog(this,
                   "Giá bán đang nhỏ hơn giá gốc. Bạn có chắc chắn muốn tiếp tục?",
                   "Cảnh báo logic", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
               if (choice == JOptionPane.NO_OPTION) {
                   txtGiaBan.requestFocus();
                   return false; 
               }
           }
        } catch (NumberFormatException e) {
           e.printStackTrace();
        }

        return true;
    }

    private void them() {
    	if (!validData()) {
            return; 
        }
        String maSP = txtMaSP.getText().trim();
        String tenSP = txtTenSP.getText().trim();
        double giaBan = Double.parseDouble(txtGiaBan.getText().trim()); // Parse lại an toàn vì đã kiểm tra
        double giaGoc = Double.parseDouble(txtGiaGoc.getText().trim()); // Parse lại an toàn
        String maLoai = comboMaLoai.getSelectedItem().toString();
        String maNH = comboMaNH.getSelectedItem().toString();
        String maNCC = comboMaNCC.getSelectedItem().toString();
        String maNQL = txtMaNQL.getText().trim();

        SanPham sanPhamMoi = new SanPham(maSP, tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL);

        try {
            boolean success = sanPhamDAO.insert(sanPhamMoi); 
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadProductData(); 
                loadComboBoxData();
                clearFields(); 
                 btnThem.setEnabled(true);
                 btnXoaTrang.setEnabled(true);
                 btnSua.setEnabled(false);
                 btnXoa.setEnabled(false);

            } 
            
            else {
                 txtMaSP.setText("");
                 txtMaSP.requestFocus();
                // JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại. Có thể mã sản phẩm đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage(); 
            String sqlState = e.getSQLState(); 

            if (sqlState != null && sqlState.startsWith("23")) {
                if (errorMessage != null && (errorMessage.contains("PRIMARY KEY") || errorMessage.contains("UNIQUE KEY") || errorMessage.contains("duplicate key"))) {
                      JOptionPane.showMessageDialog(this, "Lỗi: Mã Sản Phẩm đã tồn tại hoặc dữ liệu bị trùng lặp.", "Lỗi Ràng Buộc Duy Nhất", JOptionPane.ERROR_MESSAGE);
                 } else {
                     JOptionPane.showMessageDialog(this, "Lỗi: Vi phạm ràng buộc toàn vẹn dữ liệu (SQLState: " + sqlState + "). Chi tiết: " + errorMessage, "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
                 }
            }
            else {
                
                JOptionPane.showMessageDialog(this, "Lỗi SQL khi thêm sản phẩm: " + errorMessage, "Lỗi SQL Khác", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sua() {
        int selectedRow = tableSanPham.getSelectedRow(); 
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validData()) {
            return; 
       }

        String maSP = txtMaSP.getText().trim(); // Lấy từ ô text (đã disable)
        String tenSP = txtTenSP.getText().trim();
        double giaBan = Double.parseDouble(txtGiaBan.getText().trim());
        double giaGoc = Double.parseDouble(txtGiaGoc.getText().trim());
        String maLoai = comboMaLoai.getSelectedItem().toString();
        String maNH = comboMaNH.getSelectedItem().toString();
        String maNCC = comboMaNCC.getSelectedItem().toString();
        String maNQL = txtMaNQL.getText().trim();

       

        SanPham sanPhamCapNhat = new SanPham(maSP, tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL); // Create SanPham object

        try {
            boolean success = sanPhamDAO.update(sanPhamCapNhat); 
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin sản phẩm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadProductData();
                loadComboBoxData();
                clearFields(); 
                 btnThem.setEnabled(true);
                 btnXoaTrang.setEnabled(true);
                 btnSua.setEnabled(false);
                 btnXoa.setEnabled(false);
                 txtMaSP.setEnabled(true);

            } else {
                 JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại. Có thể sản phẩm không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sản phẩm vào cơ sở dữ liệu: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void xoa() {
        int[] selectedRows = tableSanPham.getSelectedRows();  // Lấy các dòng đã chọn
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sản phẩm để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> maSPList = new ArrayList<>();
        for (int row : selectedRows) {
            String maSP = (String) tableSanPham.getValueAt(row, 0);  
            maSPList.add(maSP);
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa các sản phẩm đã chọn?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = sanPhamDAO.delete(maSPList);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadProductData();  
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm: Tồn tại khóa ngoại ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   private void xoatrang() {
       clearFields(); 
       txtMaSP.setEnabled(true);
       btnThem.setEnabled(true);
       btnXoaTrang.setEnabled(true);
       btnSua.setEnabled(false);
       btnXoa.setEnabled(false);
   }
@Override
public void mouseClicked(MouseEvent e) {
	// TODO Auto-generated method stub
	
}
@Override
public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub
	
}
@Override
public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
	
}
@Override
public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	
}
@Override
public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	
}



}
