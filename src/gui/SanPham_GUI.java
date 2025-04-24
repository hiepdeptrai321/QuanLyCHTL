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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
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

import connectDB.ConnectDB;
import dao.SanPham_DAO;
import entity.SanPham;

public class SanPham_GUI extends JFrame implements ActionListener, MouseListener {

    private Connection conn;
    private SanPham_DAO sanPhamDAO; 
    private DefaultTableModel tableModel;
    private JTable tableSanPham; 

    // Details Panel Components (corresponding to SanPham attributes)
    private JTextField txtMaSP, txtTenSP, txtGiaBan, txtGiaGoc, txtMaLoai, txtMaNH, txtMaNCC, txtMaNQL;
    private JButton btnThem, btnSua, btnXoa, btnXoaTrang;

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


        setTitle("Quản Lý Sản Phẩm"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null); 

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
            new SanPham_GUI().setVisible(true); 
        });
    }

    private void initComponents() {
        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Thêm padding cho panel chính
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // North Panel (Title)
        JPanel northPanel = new JPanel();
        JLabel lblTitle = new JLabel("QUẢN LÝ SẢN PHẨM"); // Update title label
        // Tăng kích thước và kiểu chữ cho tiêu đề
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(50, 100, 150)); // Màu sắc cho tiêu đề
        northPanel.add(lblTitle);
        mainPanel.add(northPanel, BorderLayout.NORTH);

        // Center Panel with JSplitPane
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.6); // Table takes 60% initially
        centerSplitPane.setDividerSize(8); // Độ dày của đường chia

        // Left Panel (Table)
        JPanel tablePanel = createTablePanel();
        centerSplitPane.setLeftComponent(tablePanel);

        // Right Panel (Details)
        JPanel detailsPanel = createDetailsPanel();
        centerSplitPane.setRightComponent(detailsPanel);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        // Add mainPanel to the frame
        getContentPane().add(mainPanel);

         // Vô hiệu hóa các trường và nút ban đầu khi chưa chọn hoặc bấm xóa trắng
       // setFieldsEditable(false);
        txtMaSP.setEditable(false); // MaSP is usually not editable after creation
        // btnSua và btnXoa nên vô hiệu hóa ban đầu, chỉ bật khi chọn dòng
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        // btnThem và btnXoaTrang có thể bật ban đầu
        btnThem.setEnabled(true);
        btnXoaTrang.setEnabled(true);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Thêm padding và TitledBorder
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("Danh sách Sản Phẩm"), // Update title
            new EmptyBorder(5, 5, 5, 5)));

        // Update column names for SanPham
        String[] columnNames = {"Mã SP", "Tên SP", "Giá bán", "Giá gốc", "Mã loại", "Mã NH", "Mã NCC", "Mã NQL"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells not editable
            }
        };
        tableSanPham = new JTable(tableModel); // Use tableSanPham
        // Tăng chiều cao dòng cho bảng
        tableSanPham.setRowHeight(25);
        // Điều chỉnh font cho header bảng
        tableSanPham.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableSanPham.getTableHeader().setBackground(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(tableSanPham); // Wrap tableSanPham
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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

        // Khởi tạo Text Fields for SanPham attributes
        txtMaSP = new JTextField(25);
        txtTenSP = new JTextField(25);
        txtGiaBan = new JTextField(25);
        txtGiaGoc = new JTextField(25);
        txtMaLoai = new JTextField(25);
        txtMaNH = new JTextField(25);
        txtMaNCC = new JTextField(25);
        txtMaNQL = new JTextField(25);


        // No Date fields, so no Tooltips for date format needed


        // --- Thêm các cặp Label-TextField vào fieldsPanel dùng GridBagLayout ---
        int row = 0;
        addField(fieldsPanel, gbc, new JLabel("Mã SP:"), txtMaSP, row++); // Use MaSP
        addField(fieldsPanel, gbc, new JLabel("Tên SP:"), txtTenSP, row++); // Use TenSP
        addField(fieldsPanel, gbc, new JLabel("Giá bán:"), txtGiaBan, row++); // Use GiaBan
        addField(fieldsPanel, gbc, new JLabel("Giá gốc:"), txtGiaGoc, row++); // Use GiaGoc
        addField(fieldsPanel, gbc, new JLabel("Mã loại:"), txtMaLoai, row++); // Use MaLoai
        addField(fieldsPanel, gbc, new JLabel("Mã NH:"), txtMaNH, row++);   // Use MaNH
        addField(fieldsPanel, gbc, new JLabel("Mã NCC:"), txtMaNCC, row++); // Use MaNCC
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


        return panel;
    }

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


    // Bật/tắt khả năng chỉnh sửa các trường nhập liệu (trừ mã SP)
//    private void setFieldsEditable(boolean editable) {
//         txtTenSP.setEditable(editable);
//         txtGiaBan.setEditable(editable);
//         txtGiaGoc.setEditable(editable);
//         txtMaLoai.setEditable(editable);
//         txtMaNH.setEditable(editable);
//         txtMaNCC.setEditable(editable);
//         txtMaNQL.setEditable(editable);
//         // txtMaSP is handled separately
//    }

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
                                btnThem.setEnabled(true); 
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
        txtMaLoai.setText(sp.getMaLoai());
        txtMaNH.setText(sp.getMaNH());
        txtMaNCC.setText(sp.getMaNCC());
        txtMaNQL.setText(sp.getMaNQL());
    }

    private void clearFields() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtGiaBan.setText("");
        txtGiaGoc.setText("");
        txtMaLoai.setText("");
        txtMaNH.setText("");
        txtMaNCC.setText("");
        txtMaNQL.setText("");

         
       // setFieldsEditable(true);
        txtMaSP.setEditable(true); 
        tableSanPham.clearSelection(); 
        txtMaSP.requestFocus(); 

        // Bật nút Thêm và Xóa trắng        
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
    }

    private void them() {
        String maSP = txtMaSP.getText().trim();
        String tenSP = txtTenSP.getText().trim();
        String giaBanStr = txtGiaBan.getText().trim();
        String giaGocStr = txtGiaGoc.getText().trim();
        String maLoai = txtMaLoai.getText().trim();
        String maNH = txtMaNH.getText().trim();
        String maNCC = txtMaNCC.getText().trim();
        String maNQL = txtMaNQL.getText().trim();

        if (maSP.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Mã Sản Phẩm không được rỗng.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
       }
       if (!maSP.startsWith("SP")) {
           JOptionPane.showMessageDialog(this, "Mã Sản Phẩm phải bắt đầu bằng \"SP\".", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
           return; 
       }

       if (tenSP.isEmpty() || giaBanStr.isEmpty() ||
           giaGocStr.isEmpty() || maLoai.isEmpty() || maNH.isEmpty() ||
           maNCC.isEmpty() || maNQL.isEmpty()) {
           JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin sản phẩm (trừ Mã SP đã kiểm tra).", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
           return;
       }

        double giaBan = 0.0;
        double giaGoc = 0.0;

        try {
            giaBan = Double.parseDouble(giaBanStr);
            giaGoc = Double.parseDouble(giaGocStr);
            //them cac valid...

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Định dạng số không hợp lệ cho Giá bán hoặc Giá gốc.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SanPham sanPhamMoi = new SanPham(maSP, tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL);

        try {
            boolean success = sanPhamDAO.insert(sanPhamMoi); 
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadProductData(); 
                clearFields(); 
                 btnThem.setEnabled(true);
                 btnXoaTrang.setEnabled(true);
                 btnSua.setEnabled(false);
                 btnXoa.setEnabled(false);

            } else {
                 
                 JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại. Có thể mã sản phẩm đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            int errorCode = e.getErrorCode(); 
            String sqlState = e.getSQLState(); 

            if (errorCode == 547) {
                 if (errorMessage != null) {
                    if (errorMessage.contains("FK__SanPham__maNH")) {
                        JOptionPane.showMessageDialog(this, "Lỗi: Mã Nhãn Hàng không tồn tại trong danh mục Nhãn Hàng.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                    } else if (errorMessage.contains("FK__SanPham__maLoai")) {
                        JOptionPane.showMessageDialog(this, "Lỗi: Mã Loại Sản Phẩm không tồn tại trong danh mục Loại Sản Phẩm.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                    } else if (errorMessage.contains("FK__SanPham__maNCC")) { 
                         JOptionPane.showMessageDialog(this, "Lỗi: Mã Nhà Cung Cấp không tồn tại trong danh mục Nhà Cung Cấp.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                    } else if (errorMessage.contains("FK__SanPham__maNQL")) { 
                         JOptionPane.showMessageDialog(this, "Lỗi: Mã Người Quản Lý không tồn tại trong danh mục Người Quản Lý.", "Lỗi Khóa Ngoại", JOptionPane.ERROR_MESSAGE);
                    }                    
                    else {                        
                        JOptionPane.showMessageDialog(this, "Lỗi: Vi phạm ràng buộc trong cơ sở dữ liệu. Chi tiết: " + errorMessage, "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
                    }
                 } else {                   
                      JOptionPane.showMessageDialog(this, "Lỗi: Vi phạm ràng buộc trong cơ sở dữ liệu (Mã lỗi: 547).", "Lỗi Ràng Buộc", JOptionPane.ERROR_MESSAGE);
                 }
            }
            else if (sqlState != null && sqlState.startsWith("23")) {
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

        String maSP = txtMaSP.getText().trim();
         if (maSP.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Không tìm thấy mã sản phẩm để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
             return;
         }
        String tenSP = txtTenSP.getText().trim();
        String giaBanStr = txtGiaBan.getText().trim();
        String giaGocStr = txtGiaGoc.getText().trim();
        String maLoai = txtMaLoai.getText().trim();
        String maNH = txtMaNH.getText().trim();
        String maNCC = txtMaNCC.getText().trim();
        String maNQL = txtMaNQL.getText().trim();

        if (maSP.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Mã Sản Phẩm không được rỗng.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return;
       }
       if (!maSP.startsWith("SP")) {
           JOptionPane.showMessageDialog(this, "Mã Sản Phẩm phải bắt đầu bằng \"SP\".", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
           return; 
       }


       if (tenSP.isEmpty() || giaBanStr.isEmpty() ||
           giaGocStr.isEmpty() || maLoai.isEmpty() || maNH.isEmpty() ||
           maNCC.isEmpty() || maNQL.isEmpty()) {
           JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin sản phẩm (trừ Mã SP đã kiểm tra).", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
           return;
       }


        double giaBan = 0.0;
        double giaGoc = 0.0;

        try {
            giaBan = Double.parseDouble(giaBanStr);
            giaGoc = Double.parseDouble(giaGocStr);

             // TODO: Bổ sung các valid

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Định dạng số không hợp lệ cho Giá bán hoặc Giá gốc.", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SanPham sanPhamCapNhat = new SanPham(maSP, tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL); // Create SanPham object

        try {
            boolean success = sanPhamDAO.update(sanPhamCapNhat); 
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin sản phẩm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadProductData(); 
                clearFields(); 
                 btnThem.setEnabled(true);
                 btnXoaTrang.setEnabled(true);
                 btnSua.setEnabled(false);
                 btnXoa.setEnabled(false);

            } else {
                 JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại. Có thể sản phẩm không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật sản phẩm vào cơ sở dữ liệu: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
        }
    }


   private void xoa() {
       int selectedRow = tableSanPham.getSelectedRow();
       if (selectedRow == -1) {
           JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
           return;
       }

       String maSP = txtMaSP.getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã sản phẩm để xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

       int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sản phẩm có mã " + maSP + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

       if (confirm == JOptionPane.YES_OPTION) {
           try {
               boolean success = sanPhamDAO.delete(maSP); 
               if (success) {
                   JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                   loadProductData(); 
                   clearFields(); 
                    btnThem.setEnabled(true);
                    btnXoaTrang.setEnabled(true);
                    btnSua.setEnabled(false);
                    btnXoa.setEnabled(false);

               } else {
                    JOptionPane.showMessageDialog(this, "Xóa sản phẩm thất bại. Có thể sản phẩm không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
               }
           } catch (SQLException e) {
               e.printStackTrace();
               if (e.getSQLState() != null && e.getSQLState().startsWith("23")) { 
                    JOptionPane.showMessageDialog(this, "Không thể xóa sản phẩm này vì có ràng buộc với dữ liệu khác (ví dụ: chi tiết hóa đơn).", "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
               } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa sản phẩm khỏi cơ sở dữ liệu: " + e.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
               }
           }
       }
   }

   private void xoatrang() {
       clearFields(); 
 
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
