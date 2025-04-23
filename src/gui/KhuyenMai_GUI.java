package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB; // Assuming connectDB package
import dao.KhuyenMai_DAO; // Assuming dao package
import entity.KhuyenMai; // Assuming entity package

public class KhuyenMai_GUI extends JPanel implements ActionListener {

    private JPanel contentPane;
    private JTextField txtTimKiem;
    private JTable tableKhuyenMai;
    private DefaultTableModel modelKhuyenMai;
    private JTextField txtMaKM;
    private JTextField txtTenKM;
    private JTextField txtMucGiamGia;
    private JDateChooser dateChooserNgayBatDau;
    private JDateChooser dateChooserNgayKetThuc;
    private JTextArea txtMoTa;
    private JTextArea txtMaNQL; // Field for MaNQL
    private JButton btnTimKiem;
    private JButton btnThem;
    private JButton btnXoa;
    private JButton btnSua;
    // private JButton btnLuu; // Removed Save button
    private JButton btnLamMoi;
    private JTextField txtSanPhamApDung;
    private KhuyenMai_DAO khuyenMaiDAO; // Changed variable name convention

    public KhuyenMai_GUI() {
        ConnectDB.getInstance().connect();
		khuyenMaiDAO = new KhuyenMai_DAO(ConnectDB.getConnection());

        setSize(1100, 700);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
       add(contentPane);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.45);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(5);
        contentPane.add(splitPane, BorderLayout.CENTER);

        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BorderLayout(0, 5));
        panelLeft.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        splitPane.setLeftComponent(panelLeft);

        JPanel panelSearch = new JPanel();
        panelSearch.setLayout(new BorderLayout(5, 0));
        panelLeft.add(panelSearch, BorderLayout.NORTH);

        txtTimKiem = new JTextField();
        panelSearch.add(txtTimKiem, BorderLayout.CENTER);
        txtTimKiem.setColumns(10);

        btnTimKiem = new JButton("Tìm");
        panelSearch.add(btnTimKiem, BorderLayout.EAST);

        JScrollPane scrollPaneTable = new JScrollPane();
        panelLeft.add(scrollPaneTable, BorderLayout.CENTER);

        tableKhuyenMai = new JTable();
        String[] columnNames = {"Mã KM", "Tên KM", "Giá trị giảm", "Sản phẩm áp dụng"};
        modelKhuyenMai = new DefaultTableModel( new Object[][]{}, columnNames) {
             @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
        tableKhuyenMai.setModel(modelKhuyenMai);

        tableKhuyenMai.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableKhuyenMai.getColumnModel().getColumn(1).setPreferredWidth(180);
        tableKhuyenMai.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableKhuyenMai.getColumnModel().getColumn(3).setPreferredWidth(150);

        tableKhuyenMai.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableKhuyenMai.setRowHeight(25);
        tableKhuyenMai.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
        scrollPaneTable.setViewportView(tableKhuyenMai);

        // Removed panelLeftButtons that previously held btnThem

        JPanel panelRight = new JPanel();
        panelRight.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết khuyến mãi"));
        panelRight.setLayout(new GridBagLayout());
        splitPane.setRightComponent(panelRight);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Detail Fields ---
        // Row 0: MaKM
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        panelRight.add(new JLabel("Mã KM:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtMaKM = new JTextField();
        txtMaKM.setEditable(false); // MaKM is editable only when adding new
        panelRight.add(txtMaKM, gbc);

        // Row 1: TenKM
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panelRight.add(new JLabel("Tên KM:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtTenKM = new JTextField();
        panelRight.add(txtTenKM, gbc);

        // Row 2: MucGiamGia
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panelRight.add(new JLabel("Mức giảm giá (%):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtMucGiamGia = new JTextField();
        panelRight.add(txtMucGiamGia, gbc);

        // Row 3: NgayBatDau
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panelRight.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        dateChooserNgayBatDau = new JDateChooser();
        dateChooserNgayBatDau.setDateFormatString("yyyy-MM-dd");
        dateChooserNgayBatDau.setPreferredSize(new Dimension(120, 25)); // Adjusted size
        panelRight.add(dateChooserNgayBatDau, gbc);

        // Row 4: NgayKetThuc
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panelRight.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        dateChooserNgayKetThuc = new JDateChooser();
        dateChooserNgayKetThuc.setDateFormatString("yyyy-MM-dd");
        dateChooserNgayKetThuc.setPreferredSize(new Dimension(120, 25)); // Adjusted size
        panelRight.add(dateChooserNgayKetThuc, gbc);

        // Row 5: SanPhamApDung
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panelRight.add(new JLabel("Sản phẩm áp dụng:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtSanPhamApDung = new JTextField();
        panelRight.add(txtSanPhamApDung, gbc);

        // Row 6: MoTa
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelRight.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true); // Enable line wrapping
        txtMoTa.setWrapStyleWord(true); // Wrap by word
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        panelRight.add(scrollMoTa, gbc);

        // Row 8: MaNQL
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0; gbc.weightx = 0; // Reset weights
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panelRight.add(new JLabel("Mã NQL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtMaNQL = new JTextArea(1, 20); // Using JTextArea, consider JTextField if always short
        JScrollPane scrollMaNQL = new JScrollPane(txtMaNQL); // Keep scroll pane
        panelRight.add(scrollMaNQL, gbc);


        // Row 9: Buttons Panel (Moved btnThem here, removed btnLuu)
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 4;
        gbc.gridheight = 1; gbc.weighty = 0; gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelRightButtons = new JPanel();
        panelRightButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnThem = new JButton("Thêm"); // Moved from left panel
        panelRightButtons.add(btnThem);

        btnSua = new JButton("Sửa");
        panelRightButtons.add(btnSua);

        btnXoa = new JButton("Xóa");
        panelRightButtons.add(btnXoa);

        // btnLuu removed

        btnLamMoi = new JButton("Làm mới");
        panelRightButtons.add(btnLamMoi);

        panelRight.add(panelRightButtons, gbc);

        // --- Add Action Listeners ---
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnTimKiem.addActionListener(this);
        btnLamMoi.addActionListener(this);

        tableKhuyenMai.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableKhuyenMai.getSelectedRow() != -1) {
                hienThiThongTinChiTiet(tableKhuyenMai.getSelectedRow());
            } else if (tableKhuyenMai.getSelectedRow() == -1) {
                 lamMoiFormChiTiet();
            }
        });

        loadDataTable();
        lamMoiFormChiTiet(); // Set initial state (form ready for add)
        
        try {
            // Use Nimbus for a modern look, or System Look and Feel
           for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
               if ("Nimbus".equals(info.getName())) {
                   UIManager.setLookAndFeel(info.getClassName());
                   break;
               }
           }
           // If Nimbus not available, fallback to System L&F
           // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
       } catch (Exception e) {
            // If L&F setting fails, it might default to Metal, which is okay
           e.printStackTrace();
       }

        setVisible(true);
    }

    private void loadDataTable() {
        modelKhuyenMai.setRowCount(0);
         try {
             if (khuyenMaiDAO == null) {
                 System.err.println("KhuyenMai_DAO is not initialized!"); // Debug check
                 // Add sample data as fallback?
                 modelKhuyenMai.addRow(new Object[]{"KMERR", "Lỗi DAO", "0%", ""});
                 return;
             }
             List<KhuyenMai> list = khuyenMaiDAO.getAll();
             if (list != null) {
                  for (KhuyenMai km : list) {
                      modelKhuyenMai.addRow(new Object[]{
                          km.getMaKM(),
                          km.getTenKM(),
                          String.format("%.0f%%", km.getGiaTriGiam()),
                          km.getMaSP() // Display MaSP from entity
                      });
                  }
              } else {
                   System.err.println("DAO returned null list."); // Debug check
              }
         } catch (SQLException e) {
             JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi không xác định khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
         }
    }

    private void hienThiThongTinChiTiet(int row) {
        if (row < 0 || row >= modelKhuyenMai.getRowCount()) {
            lamMoiFormChiTiet(); // Clear form if row is invalid
            return;
        }

        String maKM = modelKhuyenMai.getValueAt(row, 0).toString();
        KhuyenMai kmChiTiet = null;

        try {
            if (khuyenMaiDAO == null) throw new SQLException("DAO not initialized");
            kmChiTiet = khuyenMaiDAO.getById(maKM);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy chi tiết khuyến mãi: " + e.getMessage());
            e.printStackTrace(); return;
        }

        if (kmChiTiet == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết cho mã: " + maKM);
            lamMoiFormChiTiet(); return;
        }

        txtMaKM.setText(kmChiTiet.getMaKM());
        txtTenKM.setText(kmChiTiet.getTenKM());
        txtMucGiamGia.setText(String.format("%.0f", kmChiTiet.getGiaTriGiam()));
        dateChooserNgayBatDau.setDate(kmChiTiet.getNgayBatDau());
        dateChooserNgayKetThuc.setDate(kmChiTiet.getNgayKetThuc());
        txtSanPhamApDung.setText(kmChiTiet.getMaSP());
        txtMoTa.setText(kmChiTiet.getMoTa());
        txtMaNQL.setText(kmChiTiet.getMaNQL());

        setChiTietEditable(true);
        txtMaKM.setEditable(false); // MaKM not editable when viewing/editing
        btnThem.setEnabled(false); // Disable Add when viewing/editing
        btnSua.setEnabled(true);
        btnXoa.setEnabled(true);
    }

    private void lamMoiFormChiTiet() {
        tableKhuyenMai.clearSelection();
        txtMaKM.setText("");
        txtTenKM.setText("");
        txtMucGiamGia.setText("");
        dateChooserNgayBatDau.setDate(null);
        dateChooserNgayKetThuc.setDate(null);
        txtSanPhamApDung.setText("");
        txtMoTa.setText("");
        txtMaNQL.setText("");
        txtMaKM.setEditable(true); // Allow editing MaKM for new entry
        txtTenKM.requestFocus();

        setChiTietEditable(true);

        btnThem.setEnabled(true); // Enable Add when form is clear
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        // btnLuu removed
    }

    private void setChiTietEditable(boolean editable) {
        // txtMaKM editable status is handled separately
        txtTenKM.setEditable(editable);
        txtMucGiamGia.setEditable(editable);
        dateChooserNgayBatDau.setEnabled(editable);
        dateChooserNgayKetThuc.setEnabled(editable);
        txtSanPhamApDung.setEditable(editable);
        txtMoTa.setEditable(editable);
        txtMaNQL.setEditable(editable);
    }
    
    public boolean validData() {
        String maKM = txtMaKM.getText().trim();
        String tenKM = txtTenKM.getText().trim();
        String mucGiamStr = txtMucGiamGia.getText().trim();
        Date ngayBD = dateChooserNgayBatDau.getDate();
        Date ngayKT = dateChooserNgayKetThuc.getDate();
        String spApDung = txtSanPhamApDung.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String maNQL = txtMaNQL.getText().trim();
        if (maKM.isEmpty() || tenKM.isEmpty() || mucGiamStr.isEmpty() || ngayBD == null || ngayKT == null || maNQL.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc (Mã, Tên, Mức giảm, Ngày BĐ/KT, Mã NQL)."); return false;
        }
        float mucGiam;
        try {
            mucGiam = Float.parseFloat(mucGiamStr);
            if (mucGiam < 0 || mucGiam > 100) throw new NumberFormatException("Giá trị giảm phải từ 0 đến 100");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Mức giảm giá không hợp lệ (0-100)."); txtMucGiamGia.requestFocus(); return false;
        }
        if (ngayKT.before(ngayBD)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau hoặc bằng ngày bắt đầu."); return false;
        }
        return true;
    }
    private KhuyenMai createKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        String tenKM = txtTenKM.getText().trim();
        Float mucGiamStr = Float.parseFloat(txtMucGiamGia.getText().trim());
        Date ngayBD = dateChooserNgayBatDau.getDate();
        Date ngayKT = dateChooserNgayKetThuc.getDate();
        String spApDung = txtSanPhamApDung.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String maNQL = txtMaNQL.getText().trim();
        KhuyenMai km = new KhuyenMai(maKM, tenKM, mucGiamStr, ngayBD, ngayKT, moTa, spApDung, maNQL);
        return km;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
        	if(!validData()) return;
	         KhuyenMai km = createKhuyenMai();
	         boolean success = false;
	         try {
	             if (khuyenMaiDAO == null) throw new SQLException("DAO not initialized");
	              // Check duplicate MaKM via DAO is better
	             if (khuyenMaiDAO.getById(km.getMaKM()) != null) {
	                  JOptionPane.showMessageDialog(this, "Lỗi: Mã khuyến mãi '" + km.getMaKM() + "' đã tồn tại!");
	                  txtMaKM.requestFocus(); return;
	             }
	             success = khuyenMaiDAO.insert(km);
	             if (success) {
	                 modelKhuyenMai.addRow(new Object[]{
	                     km.getMaKM(), km.getTenKM(), String.format("%.0f%%", km.getGiaTriGiam()), km.getMaSP()
	                 });
	                 JOptionPane.showMessageDialog(this, "Thêm khuyến mãi mới thành công!");
	                 lamMoiFormChiTiet();
	             } else {
	                 JOptionPane.showMessageDialog(this, "Lỗi khi thêm khuyến mãi vào CSDL!");
	             }
	         } catch (SQLException ex) {
	             JOptionPane.showMessageDialog(this, "Lỗi SQL khi thêm: " + ex.getMessage()); ex.printStackTrace();
	         } catch (Exception ex) {
	             JOptionPane.showMessageDialog(this, "Lỗi không xác định khi thêm: " + ex.getMessage()); ex.printStackTrace();
	         }

        } else if (o.equals(btnSua)) {
            int selectedRow = tableKhuyenMai.getSelectedRow();
            if (selectedRow != -1) {
            	if(!validData()) return;
   	         	KhuyenMai km = createKhuyenMai();
                boolean success = false;
                try {
                    if (khuyenMaiDAO == null) throw new SQLException("DAO not initialized");
                    success = khuyenMaiDAO.update(km);
                    if (success) {
                        modelKhuyenMai.setValueAt(km.getTenKM(), selectedRow, 1);
                        modelKhuyenMai.setValueAt(String.format("%.0f%%", km.getGiaTriGiam()), selectedRow, 2);
                        modelKhuyenMai.setValueAt(km.getMaSP(), selectedRow, 3);
                        JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!");
                        lamMoiFormChiTiet();
                        tableKhuyenMai.clearSelection();
                        setChiTietEditable(false);
                        btnSua.setEnabled(false);
                        btnXoa.setEnabled(false);
                        btnThem.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khuyến mãi!");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi SQL khi cập nhật: " + ex.getMessage()); ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi không xác định khi cập nhật: " + ex.getMessage()); ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để sửa!");
            }

        } else if (o.equals(btnXoa)) {
             int selectedRow = tableKhuyenMai.getSelectedRow();
             if (selectedRow != -1) {
                 int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa khuyến mãi '" + modelKhuyenMai.getValueAt(selectedRow, 1) + "' không?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                 if (confirm == JOptionPane.YES_OPTION) {
                     String maKMCanXoa = modelKhuyenMai.getValueAt(selectedRow, 0).toString();
                     boolean success = false;
                     try {
                         if (khuyenMaiDAO == null) throw new SQLException("DAO not initialized");
                         success = khuyenMaiDAO.delete(maKMCanXoa);
                     } catch (SQLException ex) {
                         JOptionPane.showMessageDialog(this, "Lỗi SQL khi xóa: " + ex.getMessage()); ex.printStackTrace();
                     } catch (Exception ex) {
                         JOptionPane.showMessageDialog(this, "Lỗi không xác định khi xóa: " + ex.getMessage()); ex.printStackTrace();
                     }
                     if(success) {
                          modelKhuyenMai.removeRow(selectedRow);
                          lamMoiFormChiTiet();
                          JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!");
                     } else {
                          JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thất bại!");
                     }
                 }
            } else {
                 JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để xóa!");
            }

        } else if (o.equals(btnTimKiem)) {
            String regex = txtTimKiem.getText().trim();
            if(regex.isBlank()) return;
            List<KhuyenMai> list = new ArrayList<KhuyenMai>();
			try {
				list.addAll(khuyenMaiDAO.getListByIDRegex(regex));
				list.addAll(khuyenMaiDAO.getListByNameRegex(regex));
	            if (!list.isEmpty()) {
	            	modelKhuyenMai.setRowCount(0);
	                 for (KhuyenMai km : list) {
	                     modelKhuyenMai.addRow(new Object[]{
	                         km.getMaKM(),
	                         km.getTenKM(),
	                         String.format("%.0f%%", km.getGiaTriGiam()),
	                         km.getMaSP() // Display MaSP from entity
	                     });
	                 }
	             } else {
	                 JOptionPane.showMessageDialog(this, "Không có khuyến mãi chứa từ khóa '" + regex + "'");
	             }
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        } else if (o.equals(btnLamMoi)) {
             lamMoiFormChiTiet();
             loadDataTable(); // Reload all data
        }
    }

//    public static void main(String[] args) {
//        // Set Look and Feel (optional but recommended)
//
//        EventQueue.invokeLater(() -> {
//            try {
//                KhuyenMai_GUI frame = new KhuyenMai_GUI();
//                frame.setVisible(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//                JOptionPane.showMessageDialog(null, "Không thể khởi chạy ứng dụng: " + e.getMessage(), "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//    }
}