package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
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
import javax.swing.JFileChooser;
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
import javax.swing.RowSorter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import connectDB.ConnectDB;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.NhanVien_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.SanPham;
import entity.XuatHoaDonPDF;


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
    private KhachHang khachHangHienTai;
    private ChiTietHoaDon_DAO cthdDAO;
	private List<ChiTietHoaDon> dsChiTietHoaDonTam = new ArrayList<ChiTietHoaDon>();
	private JButton btnXuatHD;

    public HoaDon_GUI() {
        ConnectDB.getInstance().connect();
		hoaDonDAO = new HoaDon_DAO(ConnectDB.getConnection());
		sanPhamDAO = new SanPham_DAO(ConnectDB.getConnection());
		khachHangDAO = new KhachHang_DAO(ConnectDB.getConnection());
		cthdDAO = new ChiTietHoaDon_DAO(ConnectDB.getConnection());
		currentHoaDonList = new ArrayList<>();

        setSize(1200, 700);
        
        setLayout(new BorderLayout());
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0)); 
        add(contentPane,BorderLayout.CENTER);

        // --- Tạo JTabbedPane ---
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER); 
        
        panelTaoHoaDon = createTaoHoaDonTab();
        tabbedPane.addTab("Tạo Hóa đơn mới", null, panelTaoHoaDon, "Tạo một hóa đơn bán hàng mới");

        panelDanhSach = createDanhSachHoaDonTab(); 
        tabbedPane.addTab("Danh sách Hóa đơn", null, panelDanhSach, "Xem danh sách và chi tiết các hóa đơn đã tạo");

        try {
             loadDataToTable(hoaDonDAO.getAll());
        } catch(SQLException e) {
             JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách hóa đơn ban đầu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        }
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
        panelSearchLeft.setBorder(BorderFactory.createTitledBorder("Tìm theo mã hóa đơn"));
        panelSearchAndFilter.add(panelSearchLeft);

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(txtTimKiem.getPreferredSize().width, 30));
        panelSearchLeft.add(txtTimKiem, BorderLayout.CENTER);

        btnTimKiem = new JButton("Tìm");
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        panelSearchLeft.add(btnTimKiem, BorderLayout.EAST);

        // --- Panel Bộ lọc (Bên Phải) ---
        JPanel panelFilterRight = new JPanel(new BorderLayout(5, 5));
        panelFilterRight.setBorder(BorderFactory.createTitledBorder("Lọc và sắp xếp"));
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

        String[] columnNames = {"Mã HĐ", "Ngày lập", "Thành tiền", "Số lượng sản phẩm", "Mã KH", "Tên NV"};
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

        btnChiTiet = new JButton("Xem chi tiết");
        btnChiTiet.setPreferredSize(new Dimension(200, 35));
        btnXuatHD = new JButton("Xuất hóa đơn");
        btnXuatHD.setPreferredSize(new Dimension(200, 35));
        panelSouth.add(btnChiTiet);
        panelSouth.add(btnXuatHD);

        // --- Thêm sự kiện cho các nút trong tab này ---
        btnTimKiem.addActionListener(this);
        btnChiTiet.addActionListener(this);
        btnApDung.addActionListener(this);
        btnHuyLoc.addActionListener(this);
        btnXuatHD.addActionListener(this);

        // Listener cho việc chọn dòng trên bảng 
         tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 btnChiTiet.setEnabled(tableHoaDon.getSelectedRow() != -1);
             }
         });

        return panel; // Trả về panel hoàn chỉnh cho tab danh sách
    }

    // --- Hàm tạo giao diện cho Tab Tạo Hóa đơn mới ---
    @SuppressWarnings({ "serial", "serial", "serial" })
	private JPanel createTaoHoaDonTab() {
        panelTaoHoaDon = new JPanel(new BorderLayout(10, 10)); // Layout chính cho tab
        panelTaoHoaDon.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Khu vực Trái: Nhập SP và Bảng Chi Tiết ---
        JPanel panelLeftPOS = new JPanel(new BorderLayout(0, 10));

        // Panel Nhập liệu Sản phẩm
        JPanel panelInputSP = new JPanel(new BorderLayout(5, 0));
        panelInputSP.setBorder(BorderFactory.createTitledBorder("Nhập mã sản phẩm"));

        txtMaSPEntry = new JTextField();
        txtMaSPEntry.setPreferredSize(new Dimension(150, 30));
        btnThemSP = new JButton("Thêm");
        btnThemSP.setPreferredSize(new Dimension(80, 30));

        panelInputSP.add(txtMaSPEntry, BorderLayout.CENTER);
        panelInputSP.add(btnThemSP, BorderLayout.EAST);

        panelLeftPOS.add(panelInputSP, BorderLayout.NORTH);

        // Bảng chi tiết hóa đơn đang tạo
        modelChiTietTaoHD = new DefaultTableModel(new String[]{"STT", "Mã SP", "Tên sản phẩm", "Số lượng", "Đơn giá", "Giảm giá", "Thành tiền", ""}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 7;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; 
                    case 3: return Integer.class; 
                    case 4: return Double.class;  
                    case 5: return Double.class; 
                    case 6: return Double.class; 
                    case 7: return JButton.class; 
                    default: return String.class;
                }
            }
        };
        tableChiTietTaoHD = new JTable(modelChiTietTaoHD);
        tableChiTietTaoHD.setRowHeight(25);
        tableChiTietTaoHD.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));

        // --- Cấu hình cột Số Lượng dùng JSpinner ---
        TableColumn quantityColumn = tableChiTietTaoHD.getColumnModel().getColumn(3); 
        quantityColumn.setCellEditor(new SpinnerEditor()); 
     

        // --- Cấu hình cột Xóa ---
        TableColumnModel columnModelTaoHD = tableChiTietTaoHD.getColumnModel();
        columnModelTaoHD.getColumn(7).setCellRenderer(new ButtonRenderer()); // Dùng renderer tùy chỉnh
        columnModelTaoHD.getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox())); // Dùng editor tùy chỉnh

        // --- Định dạng các cột khác ---
        setupTaoHDTableColumns(columnModelTaoHD); 

        scrollPaneChiTietTaoHD = new JScrollPane(tableChiTietTaoHD);
        panelLeftPOS.add(scrollPaneChiTietTaoHD, BorderLayout.CENTER);
        
        tableChiTietTaoHD.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Component oppositeComponent = e.getOppositeComponent();
                if (oppositeComponent != null && SwingUtilities.isDescendingFrom(oppositeComponent, tableChiTietTaoHD)) {
                    return;
                }
                if (tableChiTietTaoHD.isEditing()) {
                     if (!tableChiTietTaoHD.getCellEditor().stopCellEditing()) {
                          tableChiTietTaoHD.getCellEditor().cancelCellEditing();
                     }
                }
                tableChiTietTaoHD.clearSelection();
            }
        });


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

        JPanel panelSummaryFields = new JPanel(new GridLayout(0, 2, 10, 8)); 
        panelSummaryFields.setBorder(new EmptyBorder(5, 5, 5, 5)); 
        panelSummaryFields.add(createLabelPOS("Tổng tiền hàng:"));
        lblTongTienHangValuePOS = createValueLabelPOS(SwingConstants.RIGHT);
        panelSummaryFields.add(lblTongTienHangValuePOS);

        panelSummaryFields.add(createLabelPOS("Giảm giá (KM):"));
        lblGiamGiaValuePOS = createValueLabelPOS(SwingConstants.RIGHT); 
        panelSummaryFields.add(lblGiamGiaValuePOS);

        panelSummaryFields.add(createLabelPOS("Thành tiền:"));
        lblThanhTienValuePOS = createValueLabelPOS(SwingConstants.RIGHT, Font.BOLD, Color.RED); 
        panelSummaryFields.add(lblThanhTienValuePOS);

        panelSummaryFields.add(createLabelPOS("Tiền nhận:"));
        txtTienNhanPOS = new JTextField("0");
        txtTienNhanPOS.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtTienNhanPOS.setHorizontalAlignment(JTextField.RIGHT);
        panelSummaryFields.add(txtTienNhanPOS);

        panelSummaryFields.add(createLabelPOS("Tiền thối lại:"));
        lblTienThoiValuePOS = createValueLabelPOS(SwingConstants.RIGHT, Font.BOLD); 
        panelSummaryFields.add(lblTienThoiValuePOS);

        panelSummaryPayment.add(panelSummaryFields, BorderLayout.CENTER);

        // Panel Nút Thanh toán / Hủy
        JPanel panelActionButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        btnThanhToan = new JButton("Thanh toán");
        btnThanhToan.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnThanhToan.setPreferredSize(new Dimension(150, 40));

        btnHuyHoaDon = new JButton("Hủy hóa đơn");
        btnHuyHoaDon.setPreferredSize(new Dimension(150, 40));

        panelActionButtons.add(btnThanhToan);
        panelActionButtons.add(btnHuyHoaDon);
        panelSummaryPayment.add(panelActionButtons, BorderLayout.SOUTH);


        panelRightPOS.add(panelSummaryPayment, BorderLayout.CENTER);


        // --- Sử dụng JSplitPane để chia đôi ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelLeftPOS, panelRightPOS);
        splitPane.setDividerLocation(750); 
        splitPane.setResizeWeight(0.6); 
        panelTaoHoaDon.add(splitPane, BorderLayout.CENTER);

        // --- Thêm sự kiện ---
        addListenersTaoHDTab();
        resetTaoHoaDonTab();

        // --- Listener cho model bảng chi tiết để tự cập nhật tổng tiền ---

        modelChiTietTaoHD.addTableModelListener(new TableModelListener() {
            private boolean updating = false;

            @Override
            public void tableChanged(TableModelEvent e) {
                if (updating) {
                    return;
                }

                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (column == 3) { // Chỉ xử lý khi cột Số Lượng (index 3) thay đổi
                        if (row >= 0 && row < modelChiTietTaoHD.getRowCount()) {
                            updating = true;
                            try {
                                Object quantityObj = modelChiTietTaoHD.getValueAt(row, 3);
                                Object priceObj = modelChiTietTaoHD.getValueAt(row, 4);
                                Object giamGiaObj = modelChiTietTaoHD.getValueAt(row, 5);

                                if (quantityObj instanceof Number && priceObj instanceof Number) {
                                    int quantity = ((Number) quantityObj).intValue();
                                    double unitPrice = ((Number) priceObj).doubleValue();
                                    double giamGia = ((Number) giamGiaObj).doubleValue();

                                    if (quantity < 1) {
                                        quantity = 1;
                                        final int finalQuantity = quantity;
                                        SwingUtilities.invokeLater(() -> {
                                             if(row < modelChiTietTaoHD.getRowCount()){
                                                 modelChiTietTaoHD.setValueAt(finalQuantity, row, 3);
                                             }
                                        });
                                        JOptionPane.showMessageDialog(panelTaoHoaDon, "Số lượng phải ít nhất là 1.", "Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
                                        updating = false;
                                        return;
                                    }

                                    double newLineTotal = quantity * (unitPrice - giamGia);
                                    modelChiTietTaoHD.setValueAt(newLineTotal, row, 6);

                                     if(row < dsChiTietHoaDonTam.size()) {
                                        String maSP_Check = modelChiTietTaoHD.getValueAt(row, 1).toString();
                                        ChiTietHoaDon cthd_tam = dsChiTietHoaDonTam.get(row);
                                        if (cthd_tam.getMaSP().equals(maSP_Check)) {
                                              cthd_tam.setSoLuong(quantity);
                                        } else {
                                             for(ChiTietHoaDon ct : dsChiTietHoaDonTam) {
                                                 if(ct.getMaSP().equals(maSP_Check)) {
                                                     ct.setSoLuong(quantity);
                                                     break;
                                                 }
                                             }
                                        }
                                    }

                                } else {
                                     System.err.println("Kiểu dữ liệu không hợp lệ tại dòng " + row);
                                     // Cần phải có xử lý an toàn hơn ở đây nếu cần thiết
                                     final int finalRow = row; // Cần final để dùng trong lambda
                                      SwingUtilities.invokeLater(() -> {
                                         if(finalRow < modelChiTietTaoHD.getRowCount()) {
                                             modelChiTietTaoHD.setValueAt(0.0, finalRow, 5);
                                         }
                                     });
                                }
                            } catch (Exception ex) {
                                System.err.println("Lỗi khi cập nhật thành tiền tự động: " + ex.getMessage());
                                ex.printStackTrace();
                                 final int finalRow = row;
                                 SwingUtilities.invokeLater(() -> {
                                     if(finalRow < modelChiTietTaoHD.getRowCount()) {
                                         modelChiTietTaoHD.setValueAt(0.0, finalRow, 5);
                                     }
                                 });
                            } finally {
                                 updating = false;
                            }
                        }
                    }
                }

                 if (!updating && e.getType() != TableModelEvent.HEADER_ROW) {
                    updateSummaryPOS();
                 }
            }
        });


        return panelTaoHoaDon;
    }
    static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        final JSpinner spinner;
        public SpinnerEditor() {
            spinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
            spinner.setBorder(null);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected, int row, int column) {
            if (value instanceof Number) {
                spinner.setValue(((Number) value).intValue());
            } else {
                spinner.setValue(1);
            }
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }

     // --- Hàm định dạng các cột trong bảng Tạo Hóa Đơn ---
    @SuppressWarnings("serial")
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


        columnModel.getColumn(0).setPreferredWidth(35); 
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(1).setPreferredWidth(80); 
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        columnModel.getColumn(2).setPreferredWidth(200); 
        columnModel.getColumn(3).setPreferredWidth(70);  
        columnModel.getColumn(3).setCellRenderer(rightRenderer); 
        columnModel.getColumn(4).setPreferredWidth(90); 
        columnModel.getColumn(4).setCellRenderer(currencyRenderer);
        columnModel.getColumn(5).setPreferredWidth(90); 
        columnModel.getColumn(5).setCellRenderer(currencyRenderer);
        columnModel.getColumn(6).setPreferredWidth(120); 
        columnModel.getColumn(6).setCellRenderer(currencyRenderer);
        columnModel.getColumn(7).setPreferredWidth(35);
    }

     // --- Hàm thêm listeners cho các component trong Tab Tạo Hóa Đơn ---
    private void addListenersTaoHDTab() {
         btnThemSP.addActionListener(this);
         txtMaSPEntry.addActionListener(this);

         btnTimKH.addActionListener(this);
         chkKhachLe.addActionListener(this);
         btnLamMoiKhachHang.addActionListener(this);

         btnThanhToan.addActionListener(this);
         btnHuyHoaDon.addActionListener(this);

         txtTienNhanPOS.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateChange(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateChange(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateChange(); }
         });
         txtTienNhanPOS.addActionListener(e -> btnThanhToan.doClick());
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
                     modelChiTietTaoHD.setValueAt((currentQuantity + 1) * donGia, i, 7);
                    found = true;
                    break;
                }
            }

            if (!found) {
                // Chưa có -> Thêm dòng mới
                 int stt = modelChiTietTaoHD.getRowCount() + 1;
                 double donGia = sp.getGiaBan(); 
                 double giamGia = sanPhamDAO.getGiaTriGiamTheoMaSP(sp.getMaSP())/100 * donGia;
                 double thanhTien = 1 * donGia - giamGia; 
                 modelChiTietTaoHD.addRow(new Object[]{
                         stt,
                         sp.getMaSP(),
                         sp.getTenSP(),
                         1,
                         donGia,
                         giamGia,
                         thanhTien,
                         "X" // Text hiển thị cho nút (không quan trọng lắm)
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
          chkKhachLe.setSelected(true); 
          handleKhachLeCheck(); 
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
             Object thanhTienValue = modelChiTietTaoHD.getValueAt(i, 6);
             if (thanhTienValue instanceof Number) {
                 tongTienHang += ((Number) thanhTienValue).doubleValue();
             }
        }

        double giamGia = 0; 
        for (int i = 0; i < modelChiTietTaoHD.getRowCount(); i++) {
             Object giamGiaValue = modelChiTietTaoHD.getValueAt(i, 5);
             Object soLuongValue = modelChiTietTaoHD.getValueAt(i, 3);
             if (giamGiaValue instanceof Number) {
                 giamGia += ((Number) giamGiaValue).doubleValue() * ((Number) soLuongValue).doubleValue();;
             }
        }

        double thanhTien = tongTienHang;
        
        tongTienHang = thanhTien + giamGia;

        lblTongTienHangValuePOS.setText(currencyFormat.format(tongTienHang));
        lblGiamGiaValuePOS.setText(currencyFormat.format(giamGia));
        lblThanhTienValuePOS.setText(currencyFormat.format(thanhTien));

        calculateChange();
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
             String maHDMoi = "HD" + (System.currentTimeMillis()%10000); 
             hoaDonMoi.setMaHD(maHDMoi);
             hoaDonMoi.setNgayLap(new Date());
             
             NhanVien_DAO nvdao = new NhanVien_DAO(ConnectDB.getConnection());
             NhanVien nvhh = new NhanVien();
             try {
				nvhh = nvdao.getById(DangNhap_GUI.MaQLTemp);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             hoaDonMoi.setNv(nvhh);
             hoaDonMoi.setKh(khachHangHienTai); 
             hoaDonMoi.setQuay(1);
             try {
                 hoaDonMoi.setTongTien(currencyFormat.parse(lblTongTienHangValuePOS.getText()).doubleValue());
                 hoaDonMoi.setThanhTien(thanhTien);
                 hoaDonMoi.setTienNhan(tienNhan);
                 hoaDonMoi.setTienThoi(currencyFormat.parse(lblTienThoiValuePOS.getText()).doubleValue());

             } catch (Exception e) {}

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
                  SanPham spTrongBang = null;
                  try { spTrongBang = sanPhamDAO.getById(maSP); } catch (SQLException ex) {}

                  if(spTrongBang != null) {
                       ChiTietHoaDon cthd = new ChiTietHoaDon(soLuong, donGia, hoaDonMoi.getMaHD(), spTrongBang.getMaSP(), null );
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
                  resetTaoHoaDonTab();
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


    // Class để vẽ button trên JTable
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("X");                     
            setForeground(Color.RED);       
            setFont(new Font("Arial", Font.BOLD, 14)); 
            setBorderPainted(false);    
            setFocusPainted(false);  
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.WHITE); 
                setBackground(Color.RED); 
            }
            return this;
        }
    }

    // Class để xử lý sự kiện click button trên JTable
    @SuppressWarnings("serial")
	class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private String label;
		private boolean isPushed;
        private int editingRow;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox) {
            button = new JButton();
            button.addActionListener(this);
            button.setText("X");
            button.setForeground(Color.RED);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
            this.table = table;
            this.editingRow = row;
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
                      modelChiTietTaoHD.removeRow(editingRow);
                      updateSTTTable();
             }
             updateSummaryPOS();
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


    @SuppressWarnings("serial")
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
         }
    }

    private void handleHuyLoc() {
         txtTimKiem.setText("");
         cmbFilterCriteria.setSelectedIndex(0);
         radTangDan.setSelected(true);
         RowSorter<?> sorter = tableHoaDon.getRowSorter();
         if (sorter instanceof TableRowSorter) {
             ((TableRowSorter<?>) sorter).setSortKeys(null);
         }
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
                }
             } catch(SQLException ex) {
                  JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                  ex.printStackTrace();
             }
         } else {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết.");
         }
    }

    private void handleXuatHoaDon() {
        int selectedRow = tableHoaDon.getSelectedRow();
        if (selectedRow != -1) { // Kiểm tra xem có dòng nào được chọn không
            // Chuyển đổi chỉ số dòng trên giao diện sang chỉ số dòng trong model
            // (quan trọng nếu bảng có bật sắp xếp/lọc)
            int modelRow = tableHoaDon.convertRowIndexToModel(selectedRow);
            String maHD = modelHoaDon.getValueAt(modelRow, 0).toString(); // Lấy mã hóa đơn

            try {
                HoaDon hdSelected = hoaDonDAO.getById(maHD); 
                List<ChiTietHoaDon> chiTietHD = cthdDAO.getByMaHD(maHD);
                if (hdSelected != null && chiTietHD != null && !chiTietHD.isEmpty()) {
                    JFileChooser fileChooser = new JFileChooser();
                    //Tạo  thư mục mặc định
                    File defaultDirectory = new File("./DSHoaDonPDF");
                    fileChooser.setCurrentDirectory(defaultDirectory);
                    
                    fileChooser.setDialogTitle("Chọn nơi lưu file PDF hóa đơn");
                    fileChooser.setSelectedFile(new java.io.File("HoaDon_" + maHD + ".pdf"));
                    fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));
                    int userSelection = fileChooser.showSaveDialog(this); 
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                         if (!filePath.toLowerCase().endsWith(".pdf")) {
                            filePath += ".pdf";
                         }
                        XuatHoaDonPDF exporter = new XuatHoaDonPDF();
                        boolean success = exporter.xuatHoaDon(hdSelected, chiTietHD, filePath);
                    } else {
                        System.out.println("Người dùng đã hủy thao tác lưu file."); 
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Không thể lấy đủ thông tin chi tiết cho hóa đơn " + maHD + " để xuất.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi CSDL khi lấy thông tin hóa đơn để xuất PDF: " + ex.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                 ex.printStackTrace(); 
            } catch (Exception ex) {
                  JOptionPane.showMessageDialog(this, "Lỗi không xác định khi chuẩn bị xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                  ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn từ danh sách để xuất file PDF.");
        }
    }
 

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (tabbedPane.getSelectedComponent() == panelDanhSach) {
            if (o.equals(btnTimKiem)) {
                handleTimKiem(); 
            } else if (o.equals(btnApDung)) {
                handleApDungLoc();
            } else if (o.equals(btnHuyLoc)) {
                handleHuyLoc();
            } else if (o.equals(btnChiTiet)) {
                handleXemChiTiet();
            } else if (o.equals(btnXuatHD)) {
                handleXuatHoaDon();
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

}