package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame; // Import Frame để làm parent
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog; // Import JDialog
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants; // Để căn lề label
import javax.swing.WindowConstants; // Để set default close operation
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import connectDB.ConnectDB; // Cần để lấy connection cho DAO
import dao.ChiTietHoaDon_DAO; // Cần DAO này
import dao.SanPham_DAO;
// import dao.SanPham_DAO; // Có thể cần nếu ChiTietHoaDon không lưu đủ thông tin SP
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.SanPham; // Có thể cần

public class ChiTietHoaDon_GUI extends JDialog implements ActionListener {

    private HoaDon hoaDon;
    private ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private SanPham_DAO sanPhamDAO;

    // Components
    private JLabel lblMaHDValue, lblNgayLapValue, lblNhanVienValue, lblKhachHangValue, lblQuayValue;
    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    private JLabel lblTongTienHangValue, lblGiamGiaValue, lblThanhTienValue, lblTienNhanValue, lblTienThoiValue;
    private JButton btnDong, btnInHoaDon;

    // Formatters
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    /**
     * Constructor của Dialog chi tiết hóa đơn.
     * @param parent Frame cha (ví dụ: HoaDon_GUI)
     * @param hd     Đối tượng HoaDon cần hiển thị chi tiết.
     */
    public ChiTietHoaDon_GUI(Frame parent, HoaDon hd) {
        super(parent, true); // Gọi constructor JDialog, true để set modal
        this.hoaDon = hd;

        // Khởi tạo DAO (cần connection)
        try {
            // Lấy connection từ ConnectDB đã được khởi tạo ở Frame cha
            chiTietHoaDonDAO = new ChiTietHoaDon_DAO(ConnectDB.getConnection());
            sanPhamDAO = new SanPham_DAO(ConnectDB.getConnection());
        } catch (Exception e) {
            // Xử lý lỗi nếu không lấy được connection hoặc khởi tạo DAO thất bại
            JOptionPane.showMessageDialog(parent, "Lỗi khởi tạo dữ liệu chi tiết hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Có thể đóng dialog ngay nếu lỗi nghiêm trọng
             dispose(); // Đóng dialog nếu không thể khởi tạo DAO
             return; // Ngăn không chạy tiếp
        }


        setTitle("Chi Tiết Hóa Đơn - " + hoaDon.getMaHD());
        setSize(800, 650); // Kích thước dialog
        setLocationRelativeTo(parent); // Hiển thị dialog giữa frame cha
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // Đóng dialog khi nhấn nút X
        setLayout(new BorderLayout(10, 10)); // Layout chính cho dialog
        getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding

        // --- Panel Thông tin chung (Header) ---
        JPanel panelInfo = createInfoPanel();
        add(panelInfo, BorderLayout.NORTH);

        // --- Panel Bảng Chi Tiết Sản Phẩm ---
        JScrollPane scrollPaneDetails = createDetailsTablePanel();
        add(scrollPaneDetails, BorderLayout.CENTER);

        // --- Panel Tổng kết và Nút chức năng ---
        JPanel panelBottom = createBottomPanel();
        add(panelBottom, BorderLayout.SOUTH);

        // --- Load dữ liệu lên giao diện ---
        loadData();

        // Gán sự kiện cho các nút
        btnDong.addActionListener(this);
        btnInHoaDon.addActionListener(this); // Sẽ xử lý sau
        btnInHoaDon.setEnabled(false); // Tạm thời vô hiệu hóa nút In
    }

    // --- Hàm tạo Panel Thông tin chung ---
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 5)); // Grid layout cho thông tin
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin chung"));

        // Cặp Label-Value cho từng thông tin
        panel.add(createLabel("Mã HĐ:"));
        lblMaHDValue = createValueLabel();
        panel.add(lblMaHDValue);

        panel.add(createLabel("Ngày lập:"));
        lblNgayLapValue = createValueLabel();
        panel.add(lblNgayLapValue);

        panel.add(createLabel("Nhân viên:"));
        lblNhanVienValue = createValueLabel();
        panel.add(lblNhanVienValue);

        panel.add(createLabel("Khách hàng:"));
        lblKhachHangValue = createValueLabel();
        panel.add(lblKhachHangValue);

        panel.add(createLabel("Quầy số:"));
        lblQuayValue = createValueLabel();
        panel.add(lblQuayValue);

        // Thêm label trống để giữ layout nếu cần
        panel.add(new JLabel()); // Placeholder
        panel.add(new JLabel()); // Placeholder
        panel.add(new JLabel()); // Placeholder


        return panel;
    }

    // --- Hàm tạo Bảng Chi Tiết Sản Phẩm ---
    private JScrollPane createDetailsTablePanel() {
        tableChiTiet = new JTable();
        tableChiTiet.setRowHeight(24);
        tableChiTiet.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tableChiTiet.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
        tableChiTiet.getTableHeader().setOpaque(false);
        tableChiTiet.getTableHeader().setBackground(new Color(173, 216, 230));

        String[] columnNames = {"STT", "Mã SP", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        modelChiTiet = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
             // Định nghĩa kiểu dữ liệu cho cột để sort/render đúng
             @Override
             public Class<?> getColumnClass(int columnIndex) {
                 switch (columnIndex) {
                     case 0: return Integer.class; // STT
                     case 1: return String.class;  // Mã SP
                     case 2: return String.class;  // Tên SP
                     case 3: return Integer.class; // Số Lượng
                     case 4: return Double.class;  // Đơn Giá
                     case 5: return Double.class;  // Thành Tiền
                     default: return Object.class;
                 }
             }
        };
        tableChiTiet.setModel(modelChiTiet);

        // --- Định dạng cột cho bảng chi tiết ---
        setupDetailTableRenderersAndWidths();

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết sản phẩm"));
        return scrollPane;
    }

     // --- Hàm định dạng cột cho bảng chi tiết ---
    private void setupDetailTableRenderersAndWidths() {
         TableColumnModel columnModel = tableChiTiet.getColumnModel();

         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
         centerRenderer.setHorizontalAlignment(JLabel.CENTER);

         DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
         rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

         // Renderer đặc biệt cho tiền tệ (dùng lại từ GUI chính)
         DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
              @Override
              protected void setValue(Object value) {
                  if (value instanceof Number) {
                      setText(currencyFormat.format(value));
                  } else { super.setValue(value); }
                  setHorizontalAlignment(JLabel.RIGHT);
              }
         };

         columnModel.getColumn(0).setPreferredWidth(40); // STT
         columnModel.getColumn(0).setCellRenderer(centerRenderer);
         columnModel.getColumn(1).setPreferredWidth(100); // Mã SP
         columnModel.getColumn(1).setCellRenderer(centerRenderer);
         columnModel.getColumn(2).setPreferredWidth(250); // Tên Sản Phẩm
         columnModel.getColumn(3).setPreferredWidth(80);  // Số Lượng
         columnModel.getColumn(3).setCellRenderer(centerRenderer);
         columnModel.getColumn(4).setPreferredWidth(120); // Đơn Giá
         columnModel.getColumn(4).setCellRenderer(currencyRenderer);
         columnModel.getColumn(5).setPreferredWidth(120); // Thành Tiền
         columnModel.getColumn(5).setCellRenderer(currencyRenderer);

         // Không cần sort tự động cho bảng này
         // tableChiTiet.setAutoCreateRowSorter(true);
    }

    // --- Hàm tạo Panel Tổng kết và Nút ---
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));

        // Panel tổng kết
        JPanel panelSummary = new JPanel(new GridLayout(0, 2, 10, 5)); // 2 cột
        panelSummary.setBorder(BorderFactory.createTitledBorder("Tổng kết"));

        panelSummary.add(createLabel("Tổng tiền hàng:"));
        lblTongTienHangValue = createValueLabel();
        lblTongTienHangValue.setHorizontalAlignment(SwingConstants.RIGHT);
        panelSummary.add(lblTongTienHangValue);

        panelSummary.add(createLabel("Giảm giá (KM):"));
        lblGiamGiaValue = createValueLabel();
        lblGiamGiaValue.setHorizontalAlignment(SwingConstants.RIGHT);
        panelSummary.add(lblGiamGiaValue);

        panelSummary.add(createLabel("Thành tiền:"));
        lblThanhTienValue = createValueLabel(Font.BOLD); // In đậm thành tiền
        lblThanhTienValue.setHorizontalAlignment(SwingConstants.RIGHT);
        panelSummary.add(lblThanhTienValue);

        panelSummary.add(createLabel("Tiền khách đưa:"));
        lblTienNhanValue = createValueLabel();
        lblTienNhanValue.setHorizontalAlignment(SwingConstants.RIGHT);
        panelSummary.add(lblTienNhanValue);

        panelSummary.add(createLabel("Tiền thối lại:"));
        lblTienThoiValue = createValueLabel();
        lblTienThoiValue.setHorizontalAlignment(SwingConstants.RIGHT);
        panelSummary.add(lblTienThoiValue);

        panel.add(panelSummary, BorderLayout.CENTER);

        // Panel nút chức năng
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnInHoaDon = new JButton("In Hóa Đơn");
        btnInHoaDon.setPreferredSize(new Dimension(120, 30));
        btnDong = new JButton("Đóng");
        btnDong.setPreferredSize(new Dimension(100, 30));

        panelActions.add(btnInHoaDon);
        panelActions.add(btnDong);
        panel.add(panelActions, BorderLayout.SOUTH);

        return panel;
    }

    // --- Hàm tiện ích tạo JLabel cho tiêu đề thông tin ---
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.PLAIN, 13));
        return label;
    }

    // --- Hàm tiện ích tạo JLabel cho giá trị thông tin ---
    private JLabel createValueLabel() {
       return createValueLabel(Font.PLAIN); // Mặc định font thường
    }
     private JLabel createValueLabel(int fontStyle) {
         JLabel label = new JLabel(" "); // Khởi tạo với khoảng trắng để giữ chỗ
         label.setFont(new Font("Tahoma", fontStyle, 13));
         label.setForeground(Color.BLUE); // Màu chữ giá trị cho dễ nhìn
         return label;
     }


    // --- Hàm load dữ liệu từ HoaDon và ChiTietHoaDon ---
    private void loadData() {
        if (hoaDon == null) {
            return; // Không có gì để hiển thị
        }

        // Load thông tin chung
        lblMaHDValue.setText(hoaDon.getMaHD());
        lblNgayLapValue.setText(dateFormat.format(hoaDon.getNgayLap()));
        lblNhanVienValue.setText(hoaDon.getNv() != null ? hoaDon.getNv().getHoTen() + " (" + hoaDon.getNv().getMa() + ")" : "N/A");
        lblKhachHangValue.setText(hoaDon.getKh() != null ? hoaDon.getKh().getHoTen() + " (" + hoaDon.getKh().getMa() + ")" : "Khách lẻ");
        lblQuayValue.setText(String.valueOf(hoaDon.getQuay()));

        // Load chi tiết hóa đơn từ DAO
        modelChiTiet.setRowCount(0); // Xóa bảng cũ
        try {
            List<ChiTietHoaDon> listCTHD = chiTietHoaDonDAO.getByMaHD(hoaDon.getMaHD());
            int stt = 1;
            double tongTienHang = 0; // Tính lại tổng tiền hàng từ chi tiết
            for (ChiTietHoaDon cthd : listCTHD) {
                // Giả sử ChiTietHoaDon có đối tượng SanPham hoặc ít nhất là tenSP, donGia lúc bán
                 SanPham sp = sanPhamDAO.getById(cthd.getMaSP());
                 String tenSP = (sp != null) ? sp.getTenSP() : "N/A";
                 double donGia = cthd.getDonGia(); // Lấy đơn giá lúc bán từ CTHD
                 double thanhTienDong = cthd.getSoLuong() * donGia;
                 tongTienHang += thanhTienDong;

                modelChiTiet.addRow(new Object[]{
                        stt++,
                        cthd.getMaSP(),
                        tenSP,
                        cthd.getSoLuong(),
                        donGia, // Đơn giá lúc bán
                        thanhTienDong
                });
            }

            // Load thông tin tổng kết
            lblTongTienHangValue.setText(currencyFormat.format(tongTienHang)); // Hiển thị tổng tính lại
            // Tính giảm giá (nếu có) = tổng tiền hàng tính lại - thành tiền trong Hóa đơn
            double giamGia = tongTienHang - hoaDon.getThanhTien();
            lblGiamGiaValue.setText(currencyFormat.format(giamGia > 0 ? giamGia : 0)); // Hiển thị giảm giá
            lblThanhTienValue.setText(currencyFormat.format(hoaDon.getThanhTien()));
            lblTienNhanValue.setText(currencyFormat.format(hoaDon.getTienNhan()));
            lblTienThoiValue.setText(currencyFormat.format(hoaDon.getTienThoi()));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnDong)) {
            dispose(); // Đóng dialog
        } else if (o.equals(btnInHoaDon)) {
            // TODO: Thêm logic in hóa đơn
            JOptionPane.showMessageDialog(this, "Chức năng In Hóa Đơn chưa được cài đặt.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
