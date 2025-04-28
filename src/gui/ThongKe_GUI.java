package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;
import java.text.NumberFormat;
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Locale;

public class ThongKe_GUI extends JDialog {

    private JTable tableThongKe;
    private DefaultTableModel modelThongKe;
    private JLabel lblTieuDe;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    // Định dạng ngày nếu cần hiển thị Date object từ DAO
    private SimpleDateFormat dialogDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Constructor cho Dialog thống kê.
     * @param owner Frame cha (hoặc Dialog cha).
     * @param tieuChi Chuỗi mô tả tiêu chí thống kê (ví dụ: "Ngày", "Tháng", "Năm").
     * @param data Danh sách dữ liệu thống kê, mỗi phần tử là một Object[] chứa dữ liệu 1 dòng.
     */
    public ThongKe_GUI(Frame owner, String tieuChi, List<Object[]> data) {
        super(owner, "Kết quả Thống kê Doanh thu theo " + tieuChi, true); // true: Dialog dạng modal
        setSize(700, 450); // Kích thước dialog
        setLocationRelativeTo(owner); // Hiển thị giữa cửa sổ cha
        setLayout(new BorderLayout(5, 10)); // Layout chính
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Đóng dialog khi nhấn X
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // --- Tiêu đề ---
        lblTieuDe = new JLabel("BẢNG THỐNG KÊ DOANH THU THEO " + tieuChi.toUpperCase(), JLabel.CENTER);
        lblTieuDe.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTieuDe.setForeground(Color.BLUE);
        add(lblTieuDe, BorderLayout.NORTH);

        // --- Bảng dữ liệu ---
        String[] columnNames = {"Thời gian (" + tieuChi + ")", "Số lượng HĐ", "Tổng Doanh thu", "Tổng SL SP Bán ra"};
        modelThongKe = new DefaultTableModel(columnNames, 0) {
             @Override
            public boolean isCellEditable(int row, int column) { return false; } // Không cho sửa bảng
        };
        tableThongKe = new JTable(modelThongKe);
        tableThongKe.setRowHeight(24);
        tableThongKe.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        tableThongKe.getTableHeader().setBackground(new Color(200, 220, 240)); // Màu header nhẹ

        // Đổ dữ liệu vào bảng
        populateTable(data);

        // Cài đặt Renderer để định dạng số và căn lề
        setupRenderers();

        JScrollPane scrollPane = new JScrollPane(tableThongKe);
        add(scrollPane, BorderLayout.CENTER);

        // --- Nút Đóng ---
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose()); // Gọi dispose() để đóng dialog
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Căn phải nút Đóng
        bottomPanel.add(btnDong);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Hàm đổ dữ liệu List<Object[]> vào model bảng
    private void populateTable(List<Object[]> data) {
        modelThongKe.setRowCount(0); // Xóa dữ liệu cũ
        if (data != null) {
            for (Object[] rowData : data) {
                modelThongKe.addRow(rowData); // Thêm trực tiếp Object[] vào model
            }
        }
    }

    // Hàm cài đặt Renderer cho các cột
    private void setupRenderers() {
         TableColumnModel columnModel = tableThongKe.getColumnModel();
         DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
         centerRenderer.setHorizontalAlignment(JLabel.CENTER);
         DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
         rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

         // Renderer cho cột Thời gian (có thể là Date, String, hoặc Int)
         columnModel.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
             @Override
             public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                 Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                 if (value instanceof java.sql.Date) { // Nếu DAO trả về java.sql.Date cho thống kê ngày
                     setText(dialogDateFormat.format((Date) value));
                 } else {
                     // Giữ nguyên giá trị nếu là String (yyyy-MM) hoặc Int (Năm)
                     setText(value == null ? "" : value.toString());
                 }
                 setHorizontalAlignment(JLabel.CENTER);
                 return c;
             }
         });

         // Renderer cho cột Số lượng HĐ (cột 1)
         columnModel.getColumn(1).setCellRenderer(rightRenderer);

         // Renderer cho cột Tổng Doanh thu (cột 2) - Định dạng tiền tệ
         columnModel.getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
             @Override
             public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                 Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                 if (value instanceof Number) {
                     setText(currencyFormat.format(value)); // Định dạng tiền tệ
                 } else {
                     setText(value == null ? "" : value.toString());
                 }
                 setHorizontalAlignment(JLabel.RIGHT);
                 return c;
             }
         });

         // Renderer cho cột Tổng SL SP (cột 3)
         columnModel.getColumn(3).setCellRenderer(rightRenderer);

         // Set độ rộng cột (tùy chọn)
         columnModel.getColumn(0).setPreferredWidth(150);
         columnModel.getColumn(1).setPreferredWidth(100);
         columnModel.getColumn(2).setPreferredWidth(180);
         columnModel.getColumn(3).setPreferredWidth(120);
     }
}