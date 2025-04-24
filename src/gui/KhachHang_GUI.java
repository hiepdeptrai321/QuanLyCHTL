package gui;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import connectDB.ConnectDB;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date; // Using java.util.Date for JTextField parsing/formatting simplicity

public class KhachHang_GUI extends JFrame implements ActionListener {

    private Connection conn;
    private KhachHang_DAO khachHangDAO;
    private DefaultTableModel tableModel;
    private JTable tableKhachHang;

    // Details Panel Components
    private JTextField txtMa, txtNgayDangKy, txtDiemTichLuy, txtHangThanhVien, txtSoLanMuaHang,
                       txtHoTen, txtSdt, txtEmail, txtNamSinh, txtMaNV;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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


        setTitle("Quản Lý Khách Hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null); 

        initComponents();
        // Chỉ tải dữ liệu nếu DAO đã được khởi tạo thành công
        if (this.khachHangDAO != null) {
            loadCustomerData();
            addTableSelectionListener();
        } else {
             // Nếu DAO không được khởi tạo (do lỗi kết nối), có thể hiển thị thông báo khác hoặc ẩn bảng
             System.err.println("Không tải dữ liệu do lỗi kết nối database.");
             tableKhachHang.setEnabled(false); // Vô hiệu hóa bảng
             setFieldsEditable(false); // Vô hiệu hóa các trường nhập liệu
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
            new KhachHang_GUI().setVisible(true);
        });
    }

    private void initComponents() {
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Thêm padding cho panel chính
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // North Panel (Title)
        JPanel northPanel = new JPanel();
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        // Tăng kích thước và kiểu chữ cho tiêu đề
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(50, 100, 150)); // Màu sắc cho tiêu đề
        northPanel.add(lblTitle);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        // Center Panel with JSplitPane
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.8); 
        centerSplitPane.setDividerSize(8); 

        // Left Panel (Table)
        JPanel tablePanel = createTablePanel();
        centerSplitPane.setLeftComponent(tablePanel);

        // Right Panel (Details)
        JPanel detailsPanel = createDetailsPanel();
        centerSplitPane.setRightComponent(detailsPanel);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER) ;

        // Add mainPanel to the frame
        getContentPane().add(mainPanel);

        setFieldsEditable(false);
        txtMa.setEditable(false);

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

        String[] columnNames = {"Mã", "Ngày ĐK", "Điểm TL", "Hạng TV", "Số lần mua", "Họ tên", "SĐT", "Email", "Năm sinh", "Mã NV"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };
        tableKhachHang = new JTable(tableModel);
        // Tăng chiều cao dòng cho bảng
        tableKhachHang.setRowHeight(25);
        // Điều chỉnh font cho header bảng
        tableKhachHang.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableKhachHang.getTableHeader().setBackground(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Thông tin chi tiết Khách Hàng"),
            new EmptyBorder(5, 5, 5, 5)));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding giữa các components
        gbc.anchor = GridBagConstraints.WEST; // Căn lề trái cho components

        // Khởi tạo Text Fields
        txtMa = new JTextField(25); // Tăng kích thước hiển thị
        txtNgayDangKy = new JTextField(25);
        txtDiemTichLuy = new JTextField(25);
        txtHangThanhVien = new JTextField(25);
        txtSoLanMuaHang = new JTextField(25);
        txtHoTen = new JTextField(25);
        txtSdt = new JTextField(25);
        txtEmail = new JTextField(25);
        txtNamSinh = new JTextField(25);
        txtMaNV = new JTextField(25);

        // Thêm Tooltip cho các trường ngày tháng
//        txtNgayDangKy.setToolTipText("Định dạng: yyyy-MM-dd");
//        txtNamSinh.setToolTipText("Định dạng: yyyy-MM-dd");

        int row = 0;
        addField(fieldsPanel, gbc, new JLabel("Mã:"), txtMa, row++);
        addField(fieldsPanel, gbc, new JLabel("Ngày đăng ký:"), txtNgayDangKy, row++);
        addField(fieldsPanel, gbc, new JLabel("Điểm tích lũy:"), txtDiemTichLuy, row++);
        addField(fieldsPanel, gbc, new JLabel("Hạng thành viên:"), txtHangThanhVien, row++);
        addField(fieldsPanel, gbc, new JLabel("Số lần mua hàng:"), txtSoLanMuaHang, row++);
        addField(fieldsPanel, gbc, new JLabel("Họ tên:"), txtHoTen, row++);
        addField(fieldsPanel, gbc, new JLabel("SĐT:"), txtSdt, row++);
        addField(fieldsPanel, gbc, new JLabel("Email:"), txtEmail, row++);
        addField(fieldsPanel, gbc, new JLabel("Năm sinh:"), txtNamSinh, row++);
        addField(fieldsPanel, gbc, new JLabel("Mã NV:"), txtMaNV, row++);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // Tăng khoảng cách giữa các nút

        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnXoaTrang = new JButton("Xóa trắng");

        Dimension buttonSize = new Dimension(100, 30); 
        btnThem.setPreferredSize(buttonSize);
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


        return panel;
    }

     // Helper method to add a label and a text field using GridBagLayout
    private void addField(JPanel panel, GridBagConstraints gbc, JLabel label, JTextField textField, int row) {
        gbc.gridx = 0; // Cột 0 cho Label
        gbc.gridy = row; // Dòng hiện tại
        gbc.weightx = 0; // Label không co giãn chiều ngang
        gbc.fill = GridBagConstraints.NONE; // Không điền đầy ô
        panel.add(label, gbc);

        gbc.gridx = 1; // Cột 1 cho TextField
        gbc.weightx = 1.0; // TextField chiếm hết không gian còn lại theo chiều ngang
        gbc.fill = GridBagConstraints.HORIZONTAL; // Điền đầy ô theo chiều ngang
        panel.add(textField, gbc);
    }


    private void setFieldsEditable(boolean editable) {
         txtNgayDangKy.setEditable(editable);
         txtDiemTichLuy.setEditable(editable);
         txtHangThanhVien.setEditable(editable);
         txtSoLanMuaHang.setEditable(editable);
         txtHoTen.setEditable(editable);
         txtSdt.setEditable(editable);
         txtEmail.setEditable(editable);
         txtNamSinh.setEditable(editable);
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
                            kh.getNgayDangKy() != null ? dateFormat.format(kh.getNgayDangKy()) : "",
                            kh.getDiemTichLuy(),
                            kh.getHangThanhVien(),
                            kh.getSoLanMuaHang(),
                            kh.getHoTen(),
                            kh.getSdt(),
                            kh.getEmail(),
                            kh.getNamSinh() != null ? dateFormat.format(kh.getNamSinh()) : "",
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
                                    populateDetailsPanel(selectedKhachHang);
                                    setFieldsEditable(true); 
                                    txtMa.setEditable(false); 
                                    btnSua.setEnabled(true);
                                    btnXoa.setEnabled(true);                                  
                                    btnThem.setEnabled(false);

                                } else {
                                    clearFields();
                                    setFieldsEditable(false); 
                                    txtMa.setEditable(false);
                                     // Tắt nút Sửa và Xóa
                                    btnSua.setEnabled(false);
                                    btnXoa.setEnabled(false);
                                    // Bật lại nút Thêm
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
        txtNgayDangKy.setText(kh.getNgayDangKy() != null ? dateFormat.format(new Date(kh.getNgayDangKy().getTime())) : "");
        txtDiemTichLuy.setText(String.valueOf(kh.getDiemTichLuy()));
        txtHangThanhVien.setText(String.valueOf(kh.getHangThanhVien()));
        txtSoLanMuaHang.setText(String.valueOf(kh.getSoLanMuaHang()));
        txtHoTen.setText(kh.getHoTen());
        txtSdt.setText(kh.getSdt());
        txtEmail.setText(kh.getEmail());
        txtNamSinh.setText(kh.getNamSinh() != null ? dateFormat.format(new Date(kh.getNamSinh().getTime())) : "");
        txtMaNV.setText(kh.getMaNV());
    }
    private void clearFields() {
        txtMa.setText("");
        txtNgayDangKy.setText("");
        txtDiemTichLuy.setText("");
        txtHangThanhVien.setText("");
        txtSoLanMuaHang.setText("");
        txtHoTen.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        txtNamSinh.setText("");
        txtMaNV.setText("");

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
    }

    private void them() {
        String ma = txtMa.getText().trim();
        String ngayDangKyStr = txtNgayDangKy.getText().trim();
        String diemTichLuyStr = txtDiemTichLuy.getText().trim();
        String hangThanhVienStr = txtHangThanhVien.getText().trim();
        String soLanMuaHangStr = txtSoLanMuaHang.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        String namSinhStr = txtNamSinh.getText().trim();
        String maNV = txtMaNV.getText().trim();
    
        if (ma.isEmpty() || ngayDangKyStr.isEmpty() || diemTichLuyStr.isEmpty() ||
            hangThanhVienStr.isEmpty() || soLanMuaHangStr.isEmpty() || hoTen.isEmpty() ||
            sdt.isEmpty() || email.isEmpty() || namSinhStr.isEmpty() || maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        Date ngayDangKySql = null;
        int diemTichLuy = 0;
        int hangThanhVien = 0;
        int soLanMuaHang = 0;
        Date namSinhSql = null;
    
        try {
            java.util.Date ngayDKUtil = dateFormat.parse(ngayDangKyStr);
            ngayDangKySql = new java.sql.Date(ngayDKUtil.getTime());

            java.util.Date namSinhUtil = dateFormat.parse(namSinhStr);
            namSinhSql = new java.sql.Date(namSinhUtil.getTime());

            diemTichLuy = Integer.parseInt(diemTichLuyStr);
            hangThanhVien = Integer.parseInt(hangThanhVienStr);
            soLanMuaHang = Integer.parseInt(soLanMuaHangStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Sai định dạng ngày (yyyy-MM-dd)", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm, hạng hoặc số lần phải là số", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        KhachHang khachHangMoi = new KhachHang(ma, ngayDangKySql, diemTichLuy, hangThanhVien, soLanMuaHang, hoTen, sdt, email, namSinhSql, maNV);
    
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
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng vào cơ sở dữ liệu: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sua() {
        int selectedRow = tableKhachHang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã khách hàng để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String ngayDangKyStr = txtNgayDangKy.getText().trim();
        String diemTichLuyStr = txtDiemTichLuy.getText().trim();
        String hangThanhVienStr = txtHangThanhVien.getText().trim();
        String soLanMuaHangStr = txtSoLanMuaHang.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        String namSinhStr = txtNamSinh.getText().trim();
        String maNV = txtMaNV.getText().trim();

        if (ngayDangKyStr.isEmpty() || diemTichLuyStr.isEmpty() ||
            hangThanhVienStr.isEmpty() || soLanMuaHangStr.isEmpty() || hoTen.isEmpty() ||
            sdt.isEmpty() || email.isEmpty() || namSinhStr.isEmpty() || maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date ngayDangKySql = null;
        Date namSinhSql = null;
        int diemTichLuy = 0, hangThanhVien = 0, soLanMuaHang = 0;

        try {
            java.util.Date ngayDKUtil = dateFormat.parse(ngayDangKyStr);
            ngayDangKySql = new java.sql.Date(ngayDKUtil.getTime());

            java.util.Date namSinhUtil = dateFormat.parse(namSinhStr);
            namSinhSql = new java.sql.Date(namSinhUtil.getTime());

            diemTichLuy = Integer.parseInt(diemTichLuyStr);
            hangThanhVien = Integer.parseInt(hangThanhVienStr);
            soLanMuaHang = Integer.parseInt(soLanMuaHangStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Sai định dạng ngày (yyyy-MM-dd)", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm, hạng hoặc số lần phải là số", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        KhachHang khachHangCapNhat = new KhachHang(
            ma, ngayDangKySql, diemTichLuy, hangThanhVien, soLanMuaHang,
            hoTen, sdt, email, namSinhSql, maNV
        );

        try {
            boolean success = khachHangDAO.update(khachHangCapNhat);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadCustomerData(); 
                clearFields();     
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Khách hàng không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi SQL khi cập nhật: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

   private void xoa() {
       int selectedRow = tableKhachHang.getSelectedRow();
       if (selectedRow == -1) {
           JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
           return;
       }
       String ma = txtMa.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã khách hàng để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

       int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng có mã " + ma + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

       if (confirm == JOptionPane.YES_OPTION) {
           try {
               boolean success = khachHangDAO.delete(ma);
               if (success) {
                   JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                   loadCustomerData();
                   clearFields(); 
               } else {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại. Có thể khách hàng không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
               }
           } catch (SQLException e) {
               e.printStackTrace();
               // Cần xử lý trường hợp ràng buộc khóa ngoại (Foreign Key Constraint)
               if (e.getSQLState().startsWith("23")) { 
                    JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng này vì có ràng buộc với dữ liệu khác (ví dụ: hóa đơn).", "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
               } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng khỏi cơ sở dữ liệu: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
               }
           }
       }
   }

   private void xoatrang() {
       clearFields();
        setFieldsEditable(true); 
        txtMa.setEditable(true); 
        tableKhachHang.clearSelection(); 
        txtMa.requestFocus(); 
   }
}
