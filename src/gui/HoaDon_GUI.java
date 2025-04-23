package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import connectDB.ConnectDB;
import dao.HoaDon_DAO;
import entity.HoaDon;
import entity.KhuyenMai;

import javax.swing.table.DefaultTableCellRenderer;

public class HoaDon_GUI extends JFrame implements ActionListener {

    private JPanel contentPane;
    private JTextField txtTimKiem;
    private JTable tableHoaDon;
    private DefaultTableModel modelHoaDon;
    private JButton btnTimKiem;
    private JButton btnChiTiet;
    private HoaDon_DAO hoaDonDAO;

    public HoaDon_GUI() {
        ConnectDB.getInstance().connect();
		hoaDonDAO = new HoaDon_DAO(ConnectDB.getConnection());

        setDefaultCloseOperation(EXIT_ON_CLOSE); // Hoặc DISPOSE_ON_CLOSE nếu là cửa sổ phụ
        setSize(1100, 700); // Kích thước có thể điều chỉnh
        setLocationRelativeTo(null); // Căn giữa màn hình

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding lớn hơn chút
        contentPane.setLayout(new BorderLayout(0, 10)); // Khoảng cách dọc 10px
        setContentPane(contentPane);


        // --- Panel Center: Chứa Tìm kiếm và Bảng ---
        JPanel panelCenter = new JPanel(new BorderLayout(0, 5)); // Khoảng cách dọc 5px
        contentPane.add(panelCenter, BorderLayout.CENTER);

        // --- Panel Tìm kiếm (Phía trên Panel Center) ---
        JPanel panelSearch = new JPanel();
        panelSearch.setLayout(new BorderLayout(5, 0)); // Khoảng cách ngang 5px
        panelCenter.add(panelSearch, BorderLayout.NORTH);

        txtTimKiem = new JTextField();
        txtTimKiem.setPreferredSize(new Dimension(txtTimKiem.getPreferredSize().width, 30)); // Tăng chiều cao ô tìm kiếm
        panelSearch.add(txtTimKiem, BorderLayout.CENTER);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setPreferredSize(new Dimension(100, 30)); // Set kích thước nút Tìm
        panelSearch.add(btnTimKiem, BorderLayout.EAST);

        // --- ScrollPane chứa Table (Giữa Panel Center) ---
        JScrollPane scrollPaneTable = new JScrollPane();
        panelCenter.add(scrollPaneTable, BorderLayout.CENTER);

        tableHoaDon = new JTable();
        tableHoaDon.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ cho chọn 1 dòng
        tableHoaDon.setRowHeight(25); // Chiều cao dòng
        tableHoaDon.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13)); // Header font
        tableHoaDon.getTableHeader().setOpaque(false); // Tắt màu nền mặc định header
        tableHoaDon.getTableHeader().setBackground(new Color(173, 216, 230)); // Màu header Light Blue

        String[] columnNames = {"Mã HĐ", "Ngày Lập", "Thành Tiền", "Tổng SL SP", "Mã KH", "Mã NV"};
        modelHoaDon = new DefaultTableModel(new Object[][]{}, columnNames) {
            // Ngăn chỉnh sửa trực tiếp trên bảng
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
             // Định dạng kiểu dữ liệu cho cột (ví dụ: số)
             @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 3) { // Cột Thành Tiền, Tổng SL SP
                    return Number.class;
                }
                return Object.class; // Mặc định là Object hoặc String
            }
        };
        tableHoaDon.setModel(modelHoaDon);

        // --- Định dạng cột (Căn lề, Độ rộng) ---
        TableColumnModel columnModel = tableHoaDon.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(100); 
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(5).setPreferredWidth(80); 

        // Căn lề phải cho cột số
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        columnModel.getColumn(2).setCellRenderer(rightRenderer); 
        columnModel.getColumn(3).setCellRenderer(rightRenderer); 

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        columnModel.getColumn(0).setCellRenderer(centerRenderer); 
        columnModel.getColumn(1).setCellRenderer(centerRenderer); 
        columnModel.getColumn(4).setCellRenderer(centerRenderer); 
        columnModel.getColumn(5).setCellRenderer(centerRenderer); 

        scrollPaneTable.setViewportView(tableHoaDon);

        // --- Panel South: Chứa nút chức năng ---
        JPanel panelSouth = new JPanel();
        // Căn phải cho nút Chi tiết
        panelSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        contentPane.add(panelSouth, BorderLayout.SOUTH);

        btnChiTiet = new JButton("Hiển thị chi tiết");
        btnChiTiet.setPreferredSize(new Dimension(150, 35)); // Kích thước nút Chi tiết
        panelSouth.add(btnChiTiet);


        // --- Thêm sự kiện ---
        btnTimKiem.addActionListener(this);
        btnChiTiet.addActionListener(this);

        // Load dữ liệu ban đầu (ví dụ)
        loadDataHoaDon(); // Gọi hàm tải dữ liệu

        // Ban đầu không chọn dòng nào thì nút Chi tiết bị mờ
        btnChiTiet.setEnabled(false);

        // Thêm listener để bật/tắt nút Chi tiết khi chọn/bỏ chọn dòng
         tableHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Chỉ xử lý khi việc chọn đã kết thúc
                 btnChiTiet.setEnabled(tableHoaDon.getSelectedRow() != -1);
            }
        });

    }

    // Hàm tải dữ liệu mẫu hoặc từ DAO
    private void loadDataHoaDon() {
        modelHoaDon.setRowCount(0); // Xóa dữ liệu cũ
         try {
             List<HoaDon> list = hoaDonDAO.getAll(); // Giả sử có hàm getAll()
             if (list != null) {
                 for (HoaDon hd : list) {
                     modelHoaDon.addRow(new Object[]{
                         hd.getMaHD(),
                         hd.getNgayLap(),
                         hd.getThanhTien(),
                         hd.getTongSoLuongSP(),
                         hd.getKh().getMa(),
                         hd.getNv().getMa()
                     });
                 }
             }
         } catch (SQLException e) {
              JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
         }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnTimKiem)) {
            String regex = txtTimKiem.getText().trim();
            if(regex.isBlank()) return;
            List<HoaDon> list = new ArrayList<HoaDon>();
			try {
				list.addAll(hoaDonDAO.getListByIDRegex(regex));
	            if (!list.isEmpty()) {
	            	modelHoaDon.setRowCount(0);
	                 for (HoaDon hd : list) {
	                     modelHoaDon.addRow(new Object[]{
                             hd.getMaHD(),
                             hd.getNgayLap(),
                             hd.getThanhTien(),
                             hd.getTongSoLuongSP(),
                             hd.getKh().getMa(),
                             hd.getNv().getMa()
	                     });
	                 }
	             } else {
	                 JOptionPane.showMessageDialog(this, "Không có hóa đơn chứa có mã chứa '" + regex + "'");
	             }
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

        } else if (o.equals(btnChiTiet)) {
            int selectedRow = tableHoaDon.getSelectedRow();
            if (selectedRow != -1) {
                // Lấy mã hóa đơn từ dòng được chọn
                String maHD = modelHoaDon.getValueAt(selectedRow, 0).toString();
                // Hiển thị cửa sổ/dialog chi tiết hóa đơn
                // new ChiTietHoaDon_Dialog(this, maHD).setVisible(true); // Ví dụ gọi Dialog chi tiết
                 JOptionPane.showMessageDialog(this, "Hiển thị chi tiết cho hóa đơn: " + maHD + " (chưa được hiện thực)");
            } else {
                 // Mặc dù nút đã bị disable, thêm thông báo phòng trường hợp lỗi logic
                 JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết.");
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
           e.printStackTrace();
       }

        EventQueue.invokeLater(() -> {
            try {
                HoaDon_GUI frame = new HoaDon_GUI();
                frame.setVisible(true); // Hiển thị frame
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Không thể khởi chạy giao diện Hóa đơn: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
