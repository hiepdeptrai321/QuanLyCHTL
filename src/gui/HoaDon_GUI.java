package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane; // Import JTabbedPane
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import connectDB.ConnectDB;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.SanPham;


public class HoaDon_GUI extends JPanel implements ActionListener {

    private JPanel contentPane; 
    private JTabbedPane tabbedPane; 
    private JPanel panelDanhSach; 
    private JTextField txtTimKiem;
    private JTable tableHoaDon;
    private DefaultTableModel modelHoaDon;
    private JButton btnTimKiem;
    private JButton btnChiTiet;
    private JComboBox<String> cmbFilterCriteria;
    private JRadioButton radTangDan;
    private JRadioButton radGiamDan;
    private ButtonGroup bgSortOrder;
    private JButton btnApDung;
    private JButton btnHuyLoc;
    private HoaDon_DAO hoaDonDAO;
    private List<HoaDon> currentHoaDonList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    // Components cho Tab Tạo Hóa đơn mới
    private JPanel panelTaoHoaDon;
    private JTextField txtMaSPEntry;
    private JButton btnThemSP;
    private JTable tableChiTietTaoHD;
    private DefaultTableModel modelChiTietTaoHD;
    private JScrollPane scrollPaneChiTietTaoHD;

    private JTextField txtMaKHSearch;
    private JButton btnTimKH;
    private JCheckBox chkKhachLe;
    private JLabel lblTenKHDisplay;
    private JLabel lblMaKHDisplay; 

    private JLabel lblTongTienHangValuePOS;
    private JLabel lblGiamGiaValuePOS;
    private JLabel lblThanhTienValuePOS;
    private JTextField txtTienNhanPOS;
    private JLabel lblTienThoiValuePOS;

    private JButton btnThanhToan;
    private JButton btnHuyHoaDon;
    private JButton btnLamMoiKhachHang;
    
    private SanPham_DAO sanPhamDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien nhanVienHienTai;
    private KhachHang khachHangHienTai;
	private List<ChiTietHoaDon> dsChiTietHoaDonTam = new ArrayList<ChiTietHoaDon>();

    public HoaDon_GUI() {
        ConnectDB.getInstance().connect();
		hoaDonDAO = new HoaDon_DAO(ConnectDB.getConnection());
		sanPhamDAO = new SanPham_DAO(ConnectDB.getConnection());
		khachHangDAO = new KhachHang_DAO(ConnectDB.getConnection());
		currentHoaDonList = new ArrayList<>();

        setSize(1200, 700);
        
        setLayout(new BorderLayout());
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // Giảm border chút
        contentPane.setLayout(new BorderLayout(0, 0)); // JFrame dùng BorderLayout
        add(contentPane,BorderLayout.CENTER);

        // --- Tạo JTabbedPane ---
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER); // Thêm TabbedPane vào ContentPane

        // --- Tạo Tab 1: Danh sách Hóa đơn ---
        panelDanhSach = createDanhSachHoaDonTab(); // Gọi hàm tạo giao diện cho tab này
        tabbedPane.addTab("Danh sách Hóa đơn", null, panelDanhSach, "Xem danh sách và chi tiết các hóa đơn đã tạo");

        // --- Tạo Tab 2: Tạo Hóa đơn mới ---
        panelTaoHoaDon = createTaoHoaDonTab(); // Gọi hàm tạo giao diện cho tab này
        tabbedPane.addTab("Tạo Hóa đơn mới", null, panelTaoHoaDon, "Tạo một hóa đơn bán hàng mới");

        // Load dữ liệu ban đầu cho tab danh sách
        try {
             loadDataToTable(hoaDonDAO.getAll());
        } catch(SQLException e) {
             JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách hóa đơn ban đầu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
        // Ban đầu không chọn dòng nào thì nút Chi tiết bị mờ
        btnChiTiet.setEnabled(false);
    }

    // --- Hàm tạo giao diện cho Tab Danh sách Hóa đơn ---
    private JPanel createDanhSachHoaDonTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // Panel chính cho tab này
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding cho tab

        // --- Panel Center: Chứa Tìm kiếm/Lọc và Bảng ---
        JPanel panelCenter = new JPanel(new BorderLayout(0, 10));
        panel.add(panelCenter, BorderLayout.CENTER); // Thêm vào panel của tab

        // --- Panel Tìm kiếm và Lọc ---
        JPanel panelSearchAndFilter = new JPanel(new GridLayout(1, 2, 15, 0));
        panelCenter.add(panelSearchAndFilter, BorderLayout.NORTH);

        // --- Panel Tìm kiếm (Bên Trái) ---
        JPanel panelSearchLeft = new JPanel(new BorderLayout(5, 0));
        panelSearchLeft.setBorder(BorderFactory.createTitledBorder("Tìm kiếm theo Mã Hóa Đơn"));
        panelSearchAndFilter.add(panelSearchLeft);

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(txtTimKiem.getPreferredSize().width, 30));
        panelSearchLeft.add(txtTimKiem, BorderLayout.CENTER);

        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        panelSearchLeft.add(btnTimKiem, BorderLayout.EAST);

        // --- Panel Bộ lọc (Bên Phải) ---
        JPanel panelFilterRight = new JPanel(new BorderLayout(5, 5));
        panelFilterRight.setBorder(BorderFactory.createTitledBorder("Lọc và Sắp xếp"));
        panelSearchAndFilter.add(panelFilterRight);

        JPanel panelFilterControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFilterRight.add(panelFilterControls, BorderLayout.CENTER);

        JLabel lblLocTheo = new JLabel("Lọc theo:");
        panelFilterControls.add(lblLocTheo);

        String[] filterOptions = {"Ngày lập", "Thành tiền", "Tổng số lượng"};
        cmbFilterCriteria = new JComboBox<>(filterOptions);
        cmbFilterCriteria.setPreferredSize(new Dimension(150, 25));
        panelFilterControls.add(cmbFilterCriteria);

        radTangDan = new JRadioButton("Tăng dần", true);
        radGiamDan = new JRadioButton("Giảm dần");
        bgSortOrder = new ButtonGroup();
        bgSortOrder.add(radTangDan);
        bgSortOrder.add(radGiamDan);
        panelFilterControls.add(radTangDan);
        panelFilterControls.add(radGiamDan);

        JPanel panelFilterActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelFilterRight.add(panelFilterActions, BorderLayout.SOUTH);

        btnApDung = new JButton("Áp dụng");
        btnApDung.setPreferredSize(new Dimension(100, 30));
        panelFilterActions.add(btnApDung);

        btnHuyLoc = new JButton("Hủy lọc");
        btnHuyLoc.setPreferredSize(new Dimension(100, 30));
        panelFilterActions.add(btnHuyLoc);

        // --- ScrollPane chứa Table ---
        JScrollPane scrollPaneTable = new JScrollPane();
        panelCenter.add(scrollPaneTable, BorderLayout.CENTER);

        tableHoaDon = new JTable();
        tableHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHoaDon.setRowHeight(25);
        tableHoaDon.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        tableHoaDon.getTableHeader().setOpaque(false);
        tableHoaDon.getTableHeader().setBackground(new Color(173, 216, 230));

        String[] columnNames = {"Mã HĐ", "Ngày Lập", "Thành Tiền", "Tổng SL SP", "Mã KH", "Tên NV"};
        modelHoaDon = new DefaultTableModel(new Object[][]{}, columnNames) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false;
             }
              @Override
             public Class<?> getColumnClass(int columnIndex) {
                 switch (columnIndex) {
                     case 0: return String.class;
                     case 1: return java.util.Date.class; // Giữ Date để sort
                     case 2: return Double.class;
                     case 3: return Integer.class;
                     case 4: return String.class;
                     case 5: return String.class;
                     default: return Object.class;
                 }
             }
        };
        tableHoaDon.setModel(modelHoaDon);
        setupTableRenderersAndWidths(); // Áp dụng renderer và width
        scrollPaneTable.setViewportView(tableHoaDon);

        // --- Panel South: Chứa nút chức năng ---
        JPanel panelSouth = new JPanel();
        panelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        panel.add(panelSouth, BorderLayout.SOUTH); // Thêm vào panel của tab

        btnChiTiet = new JButton("Xem Chi Tiết Hóa Đơn");
        btnChiTiet.setPreferredSize(new Dimension(200, 35));
        panelSouth.add(btnChiTiet);

        // --- Thêm sự kiện cho các nút trong tab này ---
        btnTimKiem.addActionListener(this);
        btnChiTiet.addActionListener(this);
        btnApDung.addActionListener(this);
        btnHuyLoc.addActionListener(this);

        // Listener cho việc chọn dòng trên bảng 
         tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 btnChiTiet.setEnabled(tableHoaDon.getSelectedRow() != -1);
             }
         });

        return panel; // Trả về panel hoàn chỉnh cho tab danh sách
    }

    // --- Hàm tạo giao diện cho Tab Tạo Hóa đơn mới (Placeholder) ---
    // --- Hàm tạo giao diện cho Tab Tạo Hóa đơn mới ---
    private JPanel createTaoHoaDonTab() {
        panelTaoHoaDon = new JPanel(new BorderLayout(10, 10)); // Layout chính cho tab
        panelTaoHoaDon.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Khu vực Trái: Nhập SP và Bảng Chi Tiết ---
        JPanel panelLeftPOS = new JPanel(new BorderLayout(0, 10));

        // Panel Nhập liệu Sản phẩm
        JPanel panelInputSP = new JPanel(new BorderLayout(5, 0));
        panelInputSP.setBorder(BorderFactory.createTitledBorder("Nhập mã sản phẩm"));

        JLabel lblMaSPEntry = new JLabel("Mã SP:");
        txtMaSPEntry = new JTextField();
        txtMaSPEntry.setPreferredSize(new Dimension(150, 30));
        btnThemSP = new JButton("Thêm");
        btnThemSP.setPreferredSize(new Dimension(80, 30));

        panelInputSP.add(lblMaSPEntry, BorderLayout.WEST);
        panelInputSP.add(txtMaSPEntry, BorderLayout.CENTER);
        panelInputSP.add(btnThemSP, BorderLayout.EAST);

        panelLeftPOS.add(panelInputSP, BorderLayout.NORTH);

        // Bảng chi tiết hóa đơn đang tạo
        modelChiTietTaoHD = new DefaultTableModel(new String[]{"STT", "Mã SP", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền", "Xóa"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép sửa cột "Số Lượng" (index 3) và cột "Xóa" (index 6)
                return column == 3 || column == 6;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; // STT
                    case 3: return Integer.class; // Số Lượng
                    case 4: return Double.class;  // Đơn Giá
                    case 5: return Double.class;  // Thành Tiền
                    case 6: return JButton.class; // Cột nút Xóa
                    default: return String.class;
                }
            }
        };
        tableChiTietTaoHD = new JTable(modelChiTietTaoHD);
        tableChiTietTaoHD.setRowHeight(25);
        tableChiTietTaoHD.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));

        // --- Cấu hình cột Số Lượng dùng JSpinner ---
        // (Cách này trực quan hơn là sửa trực tiếp) - Bỏ qua nếu muốn sửa trực tiếp
         TableColumn quantityColumn = tableChiTietTaoHD.getColumnModel().getColumn(3);
         JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1)); // Min 1, Max 1000
//         quantityColumn.setCellEditor(new DefaultCellEditor(quantitySpinner));

        // --- Cấu hình cột Xóa ---
        TableColumnModel columnModelTaoHD = tableChiTietTaoHD.getColumnModel();
        columnModelTaoHD.getColumn(6).setCellRenderer(new ButtonRenderer()); // Dùng renderer tùy chỉnh
        columnModelTaoHD.getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox())); // Dùng editor tùy chỉnh

        // --- Định dạng các cột khác ---
        setupTaoHDTableColumns(columnModelTaoHD); // Gọi hàm định dạng cột

        scrollPaneChiTietTaoHD = new JScrollPane(tableChiTietTaoHD);
        panelLeftPOS.add(scrollPaneChiTietTaoHD, BorderLayout.CENTER);


        // --- Khu vực Phải: Thông tin KH, Tổng kết, Thanh toán ---
        JPanel panelRightPOS = new JPanel(new BorderLayout(0, 10));

        // Panel Thông tin Khách hàng
        JPanel panelCustomerActions = new JPanel(new BorderLayout(5, 5));
        panelCustomerActions.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        JPanel panelKhachHangInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblMaKHSearch = new JLabel("Mã KH/SĐT:");
        txtMaKHSearch = new JTextField(15);
        btnTimKH = new JButton("Tìm KH");
        chkKhachLe = new JCheckBox("Khách lẻ", true); // Mặc định là khách lẻ

        panelKhachHangInput.add(lblMaKHSearch);
        panelKhachHangInput.add(txtMaKHSearch);
        panelKhachHangInput.add(btnTimKH);
        panelKhachHangInput.add(chkKhachLe);


        JPanel panelKhachHangDisplay = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblMaKHText;
		panelKhachHangDisplay.add(lblMaKHText = new JLabel("Mã KH:"),gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        panelKhachHangDisplay.add(lblMaKHDisplay = new JLabel("Khách lẻ"),gbc); // Ban đầu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel lblTenKHText;
		panelKhachHangDisplay.add(lblTenKHText = new JLabel("Tên KH:"),gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        panelKhachHangDisplay.add(lblTenKHDisplay = new JLabel(""),gbc); // Ban đầu
        btnLamMoiKhachHang = new JButton("Bỏ chọn"); // Nút để quay lại khách lẻ
        panelKhachHangDisplay.add(btnLamMoiKhachHang);


        panelCustomerActions.add(panelKhachHangInput, BorderLayout.NORTH);
        panelCustomerActions.add(panelKhachHangDisplay, BorderLayout.CENTER);

        panelRightPOS.add(panelCustomerActions, BorderLayout.NORTH);

        // Panel Tổng kết và Thanh toán
        JPanel panelSummaryPayment = new JPanel(new BorderLayout(0, 10));
        panelSummaryPayment.setBorder(BorderFactory.createTitledBorder("Thanh toán"));

        JPanel panelSummaryFields = new JPanel(new GridLayout(0, 2, 10, 8)); // Grid 2 cột
        panelSummaryFields.setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding

        panelSummaryFields.add(createLabelPOS("Tổng tiền hàng:"));
        lblTongTienHangValuePOS = createValueLabelPOS(SwingConstants.RIGHT);
        panelSummaryFields.add(lblTongTienHangValuePOS);

        panelSummaryFields.add(createLabelPOS("Giảm giá (KM):"));
        lblGiamGiaValuePOS = createValueLabelPOS(SwingConstants.RIGHT); // Sẽ cập nhật sau
        panelSummaryFields.add(lblGiamGiaValuePOS);

        panelSummaryFields.add(createLabelPOS("Thành tiền:"));
        lblThanhTienValuePOS = createValueLabelPOS(SwingConstants.RIGHT, Font.BOLD, Color.RED); // In đậm, màu đỏ
        panelSummaryFields.add(lblThanhTienValuePOS);

        panelSummaryFields.add(createLabelPOS("Tiền nhận:"));
        txtTienNhanPOS = new JTextField("0");
        txtTienNhanPOS.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtTienNhanPOS.setHorizontalAlignment(JTextField.RIGHT);
        panelSummaryFields.add(txtTienNhanPOS);

        panelSummaryFields.add(createLabelPOS("Tiền thối lại:"));
        lblTienThoiValuePOS = createValueLabelPOS(SwingConstants.RIGHT, Font.BOLD); // In đậm
        panelSummaryFields.add(lblTienThoiValuePOS);

        panelSummaryPayment.add(panelSummaryFields, BorderLayout.CENTER);

        // Panel Nút Thanh toán / Hủy
        JPanel panelActionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnThanhToan.setPreferredSize(new Dimension(150, 40));

        btnHuyHoaDon = new JButton("Hủy Hóa Đơn");
        btnHuyHoaDon.setPreferredSize(new Dimension(150, 40));

        panelActionButtons.add(btnThanhToan);
        panelActionButtons.add(btnHuyHoaDon);
        panelSummaryPayment.add(panelActionButtons, BorderLayout.SOUTH);


        panelRightPOS.add(panelSummaryPayment, BorderLayout.CENTER);


        // --- Sử dụng JSplitPane để chia đôi ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeftPOS, panelRightPOS);
        splitPane.setDividerLocation(700); // Vị trí thanh chia, điều chỉnh cho phù hợp
        splitPane.setResizeWeight(0.6); // Tỷ lệ phân chia khi resize cửa sổ
        panelTaoHoaDon.add(splitPane, BorderLayout.CENTER);

        // --- Thêm sự kiện ---
        addListenersTaoHDTab();
        resetTaoHoaDonTab(); // Đặt trạng thái ban đầu


        // --- Listener cho model bảng chi tiết để tự cập nhật tổng tiền ---
        modelChiTietTaoHD.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Khi dữ liệu bảng thay đổi (thêm, xóa, sửa số lượng) -> cập nhật tổng tiền
                if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE) {
                    updateSummaryPOS();
                }
            }
        });


        return panelTaoHoaDon;
    }

     // --- Hàm định dạng các cột trong bảng Tạo Hóa Đơn ---
    private void setupTaoHDTableColumns(TableColumnModel columnModel) {
         // Renderer căn phải cho số/tiền
         DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
         rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
         // Renderer căn giữa
         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
         centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Renderer tiền tệ
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
              @Override
              protected void setValue(Object value) {
                  if (value instanceof Number) {
                      setText(currencyFormat.format(value));
                  } else { super.setValue(value); }
                  setHorizontalAlignment(JLabel.RIGHT);
              }
         };


        columnModel.getColumn(0).setPreferredWidth(30); // STT
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(1).setPreferredWidth(80); // Mã SP
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setPreferredWidth(250); // Tên SP
        columnModel.getColumn(3).setPreferredWidth(70);  // Số Lượng
        columnModel.getColumn(3).setCellRenderer(centerRenderer); // Căn giữa số lượng
        columnModel.getColumn(4).setPreferredWidth(100); // Đơn Giá
        columnModel.getColumn(4).setCellRenderer(currencyRenderer);
        columnModel.getColumn(5).setPreferredWidth(110); // Thành Tiền
        columnModel.getColumn(5).setCellRenderer(currencyRenderer);
        columnModel.getColumn(6).setPreferredWidth(50);  // Nút Xóa
        // Renderer và Editor đã set ở trên
    }

     // --- Hàm thêm listeners cho các component trong Tab Tạo Hóa Đơn ---
    private void addListenersTaoHDTab() {
         btnThemSP.addActionListener(this);
         txtMaSPEntry.addActionListener(this); // Cho phép nhấn Enter để thêm SP

         btnTimKH.addActionListener(this);
         chkKhachLe.addActionListener(this);
         btnLamMoiKhachHang.addActionListener(this);

         btnThanhToan.addActionListener(this);
         btnHuyHoaDon.addActionListener(this);

         // Listener cho ô nhập tiền nhận để tính tiền thối
         txtTienNhanPOS.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateChange(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateChange(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateChange(); }
         });
         txtTienNhanPOS.addActionListener(e -> btnThanhToan.doClick()); // Nhấn Enter ở ô Tiền nhận = Thanh toán
    }


    // --- Các hàm xử lý sự kiện cho Tab Tạo Hóa Đơn ---

    private void handleThemSanPham() {
        String maSP = txtMaSPEntry.getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(panelTaoHoaDon, "Vui lòng nhập mã sản phẩm!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtMaSPEntry.requestFocus();
            return;
        }

        try {
            SanPham sp = sanPhamDAO.getById(maSP); // Tìm SP theo mã
            if (sp == null) {
                JOptionPane.showMessageDialog(panelTaoHoaDon, "Không tìm thấy sản phẩm với mã: " + maSP, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                txtMaSPEntry.selectAll();
                txtMaSPEntry.requestFocus();
                return;
            }

            // Kiểm tra xem SP đã có trong bảng chưa
            boolean found = false;
            for (int i = 0; i < modelChiTietTaoHD.getRowCount(); i++) {
                if (modelChiTietTaoHD.getValueAt(i, 1).equals(sp.getMaSP())) {
                    // Đã có -> Tăng số lượng
                    int currentQuantity = (int) modelChiTietTaoHD.getValueAt(i, 3);
                    modelChiTietTaoHD.setValueAt(currentQuantity + 1, i, 3); // Tăng số lượng
                    // Cập nhật thành tiền cho dòng đó (TableModelListener sẽ tự động gọi updateSummary)
                     double donGia = (double) modelChiTietTaoHD.getValueAt(i, 4);
                     modelChiTietTaoHD.setValueAt((currentQuantity + 1) * donGia, i, 5);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Chưa có -> Thêm dòng mới
                 int stt = modelChiTietTaoHD.getRowCount() + 1;
                 double donGia = sp.getGiaBan(); // Lấy giá bán từ sản phẩm
                 double thanhTien = 1 * donGia; // Số lượng ban đầu là 1
                 modelChiTietTaoHD.addRow(new Object[]{
                         stt,
                         sp.getMaSP(),
                         sp.getTenSP(),
                         1, // Số lượng ban đầu
                         donGia,
                         thanhTien,
                         "Xóa" // Text hiển thị cho nút (không quan trọng lắm)
                 });
                 // Thêm vào danh sách tạm
                 ChiTietHoaDon cthd = new ChiTietHoaDon(0, donGia, null, sp.getMaSP(), null);
                 dsChiTietHoaDonTam.add(cthd);
            }

            // Xóa ô nhập liệu và focus lại để quét tiếp
            txtMaSPEntry.setText("");
            txtMaSPEntry.requestFocus();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panelTaoHoaDon, "Lỗi khi tìm kiếm sản phẩm: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Hàm cập nhật STT sau khi xóa dòng
    private void updateSTTTable() {
        for (int i = 0; i < modelChiTietTaoHD.getRowCount(); i++) {
            modelChiTietTaoHD.setValueAt(i + 1, i, 0); // Cập nhật cột STT (index 0)
        }
    }


     private void handleTimKhachHang() {
         String keyword = txtMaKHSearch.getText().trim();
         if (keyword.isEmpty()) {
             JOptionPane.showMessageDialog(panelTaoHoaDon, "Vui lòng nhập Mã KH để tìm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
             return;
         }
         try {

             KhachHang results = khachHangDAO.getById(keyword);

             if (results == null) {
                 JOptionPane.showMessageDialog(panelTaoHoaDon, "Không tìm thấy khách hàng phù hợp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                 resetKhachHangInfo();
             } else {
                 khachHangHienTai = results;
                 displayKhachHangInfo();
                 chkKhachLe.setSelected(false); // Bỏ chọn khách lẻ
             }
         } catch (SQLException e) {
             JOptionPane.showMessageDialog(panelTaoHoaDon, "Lỗi khi tìm khách hàng: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         }
     }

     private void handleKhachLeCheck() {
         if (chkKhachLe.isSelected()) {
             resetKhachHangInfo();
             txtMaKHSearch.setEnabled(false);
             btnTimKH.setEnabled(false);
         } else {
             txtMaKHSearch.setEnabled(true);
             btnTimKH.setEnabled(true);
         }
     }

     private void handleLamMoiKhachHang() {
          resetKhachHangInfo();
          chkKhachLe.setSelected(true); // Chọn lại khách lẻ
          handleKhachLeCheck(); // Cập nhật trạng thái nút/ô nhập
     }

     // Hiển thị thông tin KH đã chọn
     private void displayKhachHangInfo() {
         if (khachHangHienTai != null) {
             lblMaKHDisplay.setText(khachHangHienTai.getMa());
             lblTenKHDisplay.setText(khachHangHienTai.getHoTen());
             txtMaKHSearch.setText(khachHangHienTai.getMa()); // Hiển thị mã lên ô tìm kiếm
         } else {
             resetKhachHangInfo();
         }
     }

     // Reset về trạng thái khách lẻ
     private void resetKhachHangInfo() {
         khachHangHienTai = null;
         lblMaKHDisplay.setText("Khách lẻ");
         lblTenKHDisplay.setText("");
         txtMaKHSearch.setText("");
          // Không cần disable ở đây, handleKhachLeCheck sẽ làm
     }

    // Cập nhật các ô tổng kết
    private void updateSummaryPOS() {
        double tongTienHang = 0;
        for (int i = 0; i < modelChiTietTaoHD.getRowCount(); i++) {
            // Lấy giá trị Thành tiền từ cột 5
             Object thanhTienValue = modelChiTietTaoHD.getValueAt(i, 5);
             if (thanhTienValue instanceof Number) {
                 tongTienHang += ((Number) thanhTienValue).doubleValue();
             }
        }

        // TODO: Xử lý giảm giá khuyến mãi (phức tạp hơn, cần logic chọn KM)
        double giamGia = 0; // Tạm thời chưa có giảm giá
        double thanhTien = tongTienHang - giamGia;

        lblTongTienHangValuePOS.setText(currencyFormat.format(tongTienHang));
        lblGiamGiaValuePOS.setText(currencyFormat.format(giamGia));
        lblThanhTienValuePOS.setText(currencyFormat.format(thanhTien));

        calculateChange(); // Tính lại tiền thối khi tổng tiền thay đổi
    }

    // Tính tiền thối lại
    private void calculateChange() {
        try {
            double thanhTien = currencyFormat.parse(lblThanhTienValuePOS.getText()).doubleValue();
            double tienNhan = Double.parseDouble(txtTienNhanPOS.getText().trim().replace(",", "")); // Cho phép nhập dấu phẩy
            double tienThoi = tienNhan - thanhTien;
            if (tienThoi < 0) {
                 lblTienThoiValuePOS.setText(currencyFormat.format(0)); // Không thối tiền âm
                 lblTienThoiValuePOS.setForeground(Color.RED); // Báo hiệu không đủ tiền
            } else {
                lblTienThoiValuePOS.setText(currencyFormat.format(tienThoi));
                lblTienThoiValuePOS.setForeground(Color.BLUE); // Màu bình thường
            }
        } catch (Exception e) {
            lblTienThoiValuePOS.setText(currencyFormat.format(0)); // Nếu lỗi parse thì tiền thối là 0
             lblTienThoiValuePOS.setForeground(Color.RED);
        }
    }

    private void handleThanhToan() {
         // 1. Kiểm tra điều kiện thanh toán
         if (modelChiTietTaoHD.getRowCount() == 0) {
             JOptionPane.showMessageDialog(panelTaoHoaDon, "Hóa đơn chưa có sản phẩm nào!", "Không thể thanh toán", JOptionPane.WARNING_MESSAGE);
             return;
         }
         double thanhTien;
         double tienNhan;
         try {
              thanhTien = currencyFormat.parse(lblThanhTienValuePOS.getText()).doubleValue();
              tienNhan = Double.parseDouble(txtTienNhanPOS.getText().trim().replace(",", ""));
              if (tienNhan < thanhTien) {
                   JOptionPane.showMessageDialog(panelTaoHoaDon, "Tiền nhận không đủ để thanh toán!", "Thiếu tiền", JOptionPane.WARNING_MESSAGE);
                   txtTienNhanPOS.requestFocus();
                   return;
              }
         } catch (Exception e) {
              JOptionPane.showMessageDialog(panelTaoHoaDon, "Tiền nhận không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
              txtTienNhanPOS.requestFocus();
              return;
         }

         // 2. Xác nhận thanh toán
         int confirm = JOptionPane.showConfirmDialog(panelTaoHoaDon,
                 "Xác nhận thanh toán hóa đơn này?\nThành tiền: " + lblThanhTienValuePOS.getText(),
                 "Xác nhận thanh toán",
                 JOptionPane.YES_NO_OPTION);

         if (confirm == JOptionPane.YES_OPTION) {
             // 3. Tạo đối tượng HoaDon
             HoaDon hoaDonMoi = new HoaDon();
             // TODO: Tạo mã hóa đơn mới (ví dụ: HD + timestamp hoặc theo quy tắc khác)
             String maHDMoi = "HD" + System.currentTimeMillis(); // Ví dụ mã tạm
             hoaDonMoi.setMaHD(maHDMoi);
             hoaDonMoi.setNgayLap(new Date()); // Ngày giờ hiện tại
             hoaDonMoi.setNv(nhanVienHienTai); // Nhân viên đang đăng nhập
             hoaDonMoi.setKh(khachHangHienTai); // Khách hàng đang chọn (có thể null)
             hoaDonMoi.setQuay(1); // Ví dụ quầy số 1
             // Lấy các giá trị tiền tệ đã tính
             try {
                 hoaDonMoi.setTongTien(currencyFormat.parse(lblTongTienHangValuePOS.getText()).doubleValue());
                 // TODO: Xử lý khuyến mãi để lấy tổng tiền gốc và thành tiền đúng
                 hoaDonMoi.setThanhTien(thanhTien);
                 hoaDonMoi.setTienNhan(tienNhan);
                 hoaDonMoi.setTienThoi(currencyFormat.parse(lblTienThoiValuePOS.getText()).doubleValue());

             } catch (Exception e) { /* Bỏ qua lỗi parse ở đây vì đã kiểm tra trước */ }

             // Tính tổng số lượng SP
              int tongSoLuong = 0;
              for(int i=0; i < modelChiTietTaoHD.getRowCount(); i++) {
                  tongSoLuong += (int) modelChiTietTaoHD.getValueAt(i, 3);
              }
              hoaDonMoi.setTongSoLuongSP(tongSoLuong);


             // 4. Tạo danh sách ChiTietHoaDon hoàn chỉnh (gán Hóa đơn vào)
             List<ChiTietHoaDon> dsChiTietFinal = new ArrayList<>();
             for (int i = 0; i < modelChiTietTaoHD.getRowCount(); i++) {
                  String maSP = modelChiTietTaoHD.getValueAt(i, 1).toString();
                  int soLuong = (int) modelChiTietTaoHD.getValueAt(i, 3);
                  double donGia = (double) modelChiTietTaoHD.getValueAt(i, 4);
                  // Tìm lại đối tượng SanPham (hoặc lấy từ dsChiTietHoaDonTam nếu đã lưu)
                   SanPham spTrongBang = null;
                   try { spTrongBang = sanPhamDAO.getById(maSP); } catch (SQLException ex) {} // Bỏ qua lỗi nếu cần

                  if(spTrongBang != null) {
                       ChiTietHoaDon cthd = new ChiTietHoaDon(soLuong, donGia, hoaDonMoi.getMaHD(), spTrongBang.getMaSP(), null);
                       dsChiTietFinal.add(cthd);
                  }
             }


             // 5. Lưu vào cơ sở dữ liệu (Cần transaction)
             boolean success = false;
             try {
                  success = hoaDonDAO.insertWithDetails(hoaDonMoi, dsChiTietFinal); // Cần tạo hàm này trong DAO
             } catch (SQLException e) {
                  JOptionPane.showMessageDialog(panelTaoHoaDon, "Lỗi khi lưu hóa đơn: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                  e.printStackTrace();
             }

             // 6. Xử lý kết quả
             if (success) {
                  JOptionPane.showMessageDialog(panelTaoHoaDon, "Thanh toán và lưu hóa đơn thành công!\nMã HĐ: " + maHDMoi, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                  // TODO: Có thể thêm chức năng in hóa đơn ở đây
                  resetTaoHoaDonTab(); // Reset lại tab để tạo hóa đơn mới
             } else {
                  JOptionPane.showMessageDialog(panelTaoHoaDon, "Lưu hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
             }
         }
    }

    private void handleHuyHoaDon() {
         if (modelChiTietTaoHD.getRowCount() > 0) {
              int confirm = JOptionPane.showConfirmDialog(panelTaoHoaDon,
                      "Bạn có chắc muốn hủy hóa đơn đang tạo?",
                      "Xác nhận hủy",
                      JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
              if (confirm == JOptionPane.YES_OPTION) {
                   resetTaoHoaDonTab();
              }
         } else {
              resetTaoHoaDonTab(); // Nếu chưa có gì thì reset luôn
         }
    }

     // Reset tab tạo hóa đơn về trạng thái ban đầu
    private void resetTaoHoaDonTab() {
         txtMaSPEntry.setText("");
         modelChiTietTaoHD.setRowCount(0); // Xóa bảng chi tiết
         dsChiTietHoaDonTam.clear(); // Xóa danh sách tạm
         resetKhachHangInfo(); // Reset thông tin khách hàng
         chkKhachLe.setSelected(true); // Mặc định khách lẻ
         handleKhachLeCheck(); // Cập nhật trạng thái nút/ô nhập KH
         lblTongTienHangValuePOS.setText(currencyFormat.format(0));
         lblGiamGiaValuePOS.setText(currencyFormat.format(0));
         lblThanhTienValuePOS.setText(currencyFormat.format(0));
         txtTienNhanPOS.setText("0");
         lblTienThoiValuePOS.setText(currencyFormat.format(0));
         txtMaSPEntry.requestFocus(); // Focus vào ô nhập mã SP
    }


    // --- Lớp nội danh (inner class) cho Renderer và Editor của nút Xóa ---

    // Class để vẽ button trên JTable
     class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setText("Xóa"); // Hoặc dùng icon
            // setPreferredSize(new Dimension(40, 20)); // Kích thước nút nhỏ
        }
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                               
                               boolean isSelected, boolean hasFocus, int row, int column) {
            // Đổi màu nền khi được chọn (tùy chọn)
            // if (isSelected) {
            //     setForeground(table.getSelectionForeground());
            //     setBackground(table.getSelectionBackground());
            // } else {
            //     setForeground(table.getForeground());
            //     setBackground(UIManager.getColor("Button.background"));
            // }
            return this;
        }
    }

    // Class để xử lý sự kiện click button trên JTable
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int editingRow;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox) { // Truyền vào gì cũng được, ko dùng
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(this);
            button.setText("Xóa");
             // button.setPreferredSize(new Dimension(40, 20));
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
            this.table = table;
            this.editingRow = row;
            // label = (value == null) ? "" : value.toString();
            // button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label; // Giá trị trả về không quan trọng lắm trong trường hợp này
        }

         @Override
        public boolean stopCellEditing() {
             isPushed = false;
             return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
             super.fireEditingStopped();
        }


        @Override
        public void actionPerformed(ActionEvent e) {
             // Ngừng chỉnh sửa ô
             fireEditingStopped();
             // Xử lý xóa dòng
             if (editingRow >= 0 && editingRow < modelChiTietTaoHD.getRowCount()) {
                 // Xác nhận xóa (tùy chọn)
                 // int confirm = JOptionPane.showConfirmDialog(panelTaoHoaDon, "Xóa sản phẩm này khỏi hóa đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                 // if (confirm == JOptionPane.YES_OPTION) {
                      modelChiTietTaoHD.removeRow(editingRow);
                      // Cập nhật lại STT sau khi xóa
                      updateSTTTable();
                 // }
             }
        }
    }

     // Hàm tiện ích tạo JLabel cho phần POS
    private JLabel createLabelPOS(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.PLAIN, 14)); // Font lớn hơn chút
        return label;
    }
    // Hàm tiện ích tạo JLabel giá trị cho phần POS
    private JLabel createValueLabelPOS(int alignment) {
       return createValueLabelPOS(alignment, Font.PLAIN, Color.BLUE);
    }
    private JLabel createValueLabelPOS(int alignment, int fontStyle) {
       return createValueLabelPOS(alignment, fontStyle, Color.BLUE);
    }
     private JLabel createValueLabelPOS(int alignment, int fontStyle, Color color) {
         JLabel label = new JLabel(currencyFormat.format(0)); // Khởi tạo với giá trị 0
         label.setFont(new Font("Tahoma", fontStyle, 14));
         label.setForeground(color);
         label.setHorizontalAlignment(alignment);
         return label;
     }


    private void setupTableRenderersAndWidths() {
         TableColumnModel columnModel = tableHoaDon.getColumnModel();
         DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
         rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
         centerRenderer.setHorizontalAlignment(JLabel.CENTER);

         DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
              @Override
              protected void setValue(Object value) {
                  if (value instanceof Number) {
                      setText(currencyFormat.format(value));
                  } else { super.setValue(value); }
                  setHorizontalAlignment(JLabel.RIGHT);
              }
         };
         DefaultTableCellRenderer dateTimeRenderer = new DefaultTableCellRenderer() {
              @Override
              protected void setValue(Object value) {
                  if (value instanceof java.util.Date) {
                       setText(dateFormat.format((java.util.Date) value));
                  } else { super.setValue(value); }
                   setHorizontalAlignment(JLabel.CENTER);
              }
         };

         columnModel.getColumn(0).setPreferredWidth(100);
         columnModel.getColumn(0).setCellRenderer(centerRenderer);
         columnModel.getColumn(1).setPreferredWidth(150);
         columnModel.getColumn(1).setCellRenderer(dateTimeRenderer); // Quan trọng: Dùng renderer
         columnModel.getColumn(2).setPreferredWidth(150);
         columnModel.getColumn(2).setCellRenderer(currencyRenderer); // Quan trọng: Dùng renderer
         columnModel.getColumn(3).setPreferredWidth(100);
         columnModel.getColumn(3).setCellRenderer(rightRenderer);
         columnModel.getColumn(4).setPreferredWidth(100);
         columnModel.getColumn(4).setCellRenderer(centerRenderer);
         columnModel.getColumn(5).setPreferredWidth(150);
         columnModel.getColumn(5).setCellRenderer(centerRenderer);
         tableHoaDon.setAutoCreateRowSorter(true);
    }

    private void loadDataToTable(List<HoaDon> list) {
         modelHoaDon.setRowCount(0);
         if (list == null || list.isEmpty()) {
              currentHoaDonList = new ArrayList<>();
             return;
         }
          currentHoaDonList = new ArrayList<>(list);

         for (HoaDon hd : currentHoaDonList) {
             String tenKH = (hd.getKh() != null) ? hd.getKh().getMa() : "Khách lẻ";
              String tenNV = (hd.getNv() != null) ? hd.getNv().getHoTen() : "N/A";

             modelHoaDon.addRow(new Object[]{
                     hd.getMaHD(),
                     hd.getNgayLap(), // Truyền Date gốc
                     hd.getThanhTien(), // Truyền số gốc
                     hd.getTongSoLuongSP(), // Truyền số gốc
                     tenKH,
                     tenNV
             });
         }
    }

    private void handleTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        List<HoaDon> resultList = new ArrayList<>();
        try {
            if (keyword.isEmpty()) {
                resultList = hoaDonDAO.getAll();
            } else {
                HoaDon hd = hoaDonDAO.getById(keyword);
                if (hd != null) {
                    resultList.add(hd);
                }
            }

            if (!resultList.isEmpty() || keyword.isEmpty()) {
                 loadDataToTable(resultList);
            } else {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào có mã: " + keyword);
                 loadDataToTable(new ArrayList<>());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void handleApDungLoc() {
        if (currentHoaDonList == null || currentHoaDonList.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Không có dữ liệu hóa đơn để lọc/sắp xếp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
             return;
         }
         int selectedFilterIndex = cmbFilterCriteria.getSelectedIndex();
         boolean tangDan = radTangDan.isSelected();
         Comparator<HoaDon> comparator = null;
         switch (selectedFilterIndex) {
             case 0: comparator = Comparator.comparing(HoaDon::getNgayLap); break;
             case 1: comparator = Comparator.comparingDouble(HoaDon::getThanhTien); break;
             case 2: comparator = Comparator.comparingInt(HoaDon::getTongSoLuongSP); break;
         }
         if (comparator != null) {
             if (!tangDan) { comparator = comparator.reversed(); }
             currentHoaDonList.sort(comparator);
             loadDataToTable(currentHoaDonList);
             JOptionPane.showMessageDialog(this, "Đã sắp xếp danh sách hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
         }
    }

    private void handleHuyLoc() {
         txtTimKiem.setText("");
         cmbFilterCriteria.setSelectedIndex(0);
         radTangDan.setSelected(true);
         try {
             loadDataToTable(hoaDonDAO.getAll());
         } catch (SQLException e) {
              JOptionPane.showMessageDialog(this, "Lỗi khi tải lại danh sách hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
         }
    }

    private void handleXemChiTiet() {
         int selectedRow = tableHoaDon.getSelectedRow();
         if (selectedRow != -1) {
             int modelRow = tableHoaDon.convertRowIndexToModel(selectedRow);
             String maHD = modelHoaDon.getValueAt(modelRow, 0).toString();
             try {
                HoaDon hdSelected = hoaDonDAO.getById(maHD);
                if (hdSelected != null) {
                     // --- Mở Dialog Chi tiết Hóa đơn ---
                     ChiTietHoaDon_GUI dialog = new ChiTietHoaDon_GUI(this, hdSelected); 
                     dialog.setVisible(true);
                     JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho hóa đơn: " + maHD, "Lỗi", JOptionPane.WARNING_MESSAGE);
                }
             } catch(SQLException ex) {
                  JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                  ex.printStackTrace();
             }
         } else {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết.");
         }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (tabbedPane.getSelectedComponent() == panelDanhSach) {
            if (o.equals(btnTimKiem)) {
                handleTimKiem(); // Cần try-catch nếu hàm này throws SQLException
            } else if (o.equals(btnApDung)) {
                handleApDungLoc();
            } else if (o.equals(btnHuyLoc)) {
                handleHuyLoc();
            } else if (o.equals(btnChiTiet)) {
                handleXemChiTiet();
            }
        }
        else if (tabbedPane.getSelectedComponent() == panelTaoHoaDon) {
             if (o.equals(btnThemSP) || o.equals(txtMaSPEntry)) {
                 handleThemSanPham();
             } else if (o.equals(btnTimKH)) {
                  handleTimKhachHang();
             } else if (o.equals(chkKhachLe)) {
                  handleKhachLeCheck();
             } else if (o.equals(btnLamMoiKhachHang)) {
                  handleLamMoiKhachHang();
             } else if (o.equals(btnThanhToan)) {
                  handleThanhToan();
             } else if (o.equals(btnHuyHoaDon)) {
                  handleHuyHoaDon();
             }
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
           System.err.println("Nimbus Look and Feel not found. Using default.");
        }

        EventQueue.invokeLater(() -> {
            try {
                HoaDon_GUI frame = new HoaDon_GUI();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Không thể khởi chạy giao diện Hóa đơn: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}