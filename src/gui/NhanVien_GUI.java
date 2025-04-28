package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NhanVien_GUI extends JPanel implements ActionListener,MouseListener{
    private JTable table;
    private DefaultTableModel model;
    private JDateChooser dateNamSinh;
    private JDateChooser dateNgayVaoLam;
    private JButton btnThem;
    private JButton btnSua;
    private JButton btnXoa;
    private JButton btnLamMoi;
    private JButton btnTim;
    private JTextField txtTim;
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtLuong;
    private JTextField txtMaTK;
    private JComboBox<String> cbxCaLam;
    private NhanVien_DAO nhanVien_DAO = null;
    
    public NhanVien_GUI() {
    	ConnectDB.getInstance().connect();
    	nhanVien_DAO =  new NhanVien_DAO(ConnectDB.getConnection());
        setLayout(new BorderLayout(10, 10));

// 		================================================================================= Panel Trái
        JPanel pnlL = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        txtTim = new JTextField();
        btnTim = new JButton("Tìm");
        searchPanel.add(txtTim, BorderLayout.CENTER);
        searchPanel.add(btnTim, BorderLayout.EAST);

        String[] columns = {"Mã", "Họ tên", "SĐT", "Email", "Ngày sinh", "Địa chỉ", "Ngày vào làm", "Lương", "Ca Làm", "Mã NQL"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        pnlL.add(searchPanel, BorderLayout.NORTH);
        pnlL.add(scrollPane, BorderLayout.CENTER);
        pnlL.setPreferredSize(new Dimension(900, 0));

// 		================================================================================= Panel Phải
        Box pnlR = Box.createVerticalBox();
        pnlR.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createTitledBorder("Thông tin chi tiết nhân viên"),
        	    BorderFactory.createEmptyBorder(10, 5, 200, 5)
        ));

        // Form thông tin chi tiết
        pnlR.add(createFormRow("Mã NV:", txtMaNV = new JTextField()));
        pnlR.add(createFormRow("Họ tên:", txtHoTen = new JTextField()));
        pnlR.add(createFormRow("SĐT:", txtSDT = new JTextField()));
        pnlR.add(createFormRow("Email:", txtEmail = new JTextField()));
        pnlR.add(createFormRow("Ngày sinh:", dateNamSinh = new JDateChooser()));
        pnlR.add(createFormRow("Địa chỉ:", txtDiaChi = new JTextField()));
        pnlR.add(createFormRow("Ngày vào làm:", dateNgayVaoLam = new JDateChooser()));
        pnlR.add(createFormRow("Lương:", txtLuong = new JTextField()));
        
        String[] khungGio = {"00:00-08:00", "08:00h-16:00", "16:00-00:00"};
        
        pnlR.add(createFormRow("Ca:", cbxCaLam = new JComboBox<String>(khungGio)));
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        
//      thêm action cho nút
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);
        btnTim.addActionListener(this);
        table.addMouseListener(this);
        txtTim.addActionListener(this);

        pnlR.add(buttonPanel);

//      ================================================================================= thêm pannel
        add(pnlL, BorderLayout.WEST);
        add(pnlR, BorderLayout.CENTER);
        LoadData();
    }
//  ================================================================================= các action
//  action Performed
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o = e.getSource();
		if(o.equals(btnThem)) {
			if(ValidData()) {
				NhanVien x = taoNhanVien();
				try {
					Locale localeVN = new Locale("vi", "VN");
			        NumberFormat fmt = NumberFormat.getCurrencyInstance(localeVN);
			        String luongFommat = fmt.format(x.getLuong());
			        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		            String namSinhFormatted = sdf.format(x.getNamSinh());
		            String ngayVaoLamFormatted = sdf.format(x.getNgayVaoLam());
					if(nhanVien_DAO.insert(x)) {
						String[] s = {x.getMa(),x.getHoTen(),x.getSdt(),x.getEmail(),namSinhFormatted,String.valueOf(x.getDiaChi()),ngayVaoLamFormatted,luongFommat,String.valueOf(x.getCaLam()),x.getMaNQL()};
			    		model.addRow(s);
					}
				} catch (SQLException e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
		}else if (o.equals(btnSua)) {
	        int row = table.getSelectedRow();
	        if(txtMaNV.getText().equalsIgnoreCase(model.getColumnName(row))) {
	        	JOptionPane.showMessageDialog(this,"Không được sửa mã nhân viên");
	        	return;
	        }
	        if (row != -1) {
	            if (ValidData()) {
	                try {
	                	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	                    NhanVien nv = taoNhanVien();
	                    if (nhanVien_DAO.update(nv)) {
	                        Locale localeVN = new Locale("vi", "VN");
	                        NumberFormat fmt = NumberFormat.getCurrencyInstance(localeVN);
	                        String luongFormat = fmt.format(nv.getLuong());
	                        String namSinhFormatted = sdf.format(nv.getNamSinh());
	                        String ngayVaoLamFormatted = sdf.format(nv.getNgayVaoLam());
	                        
	                        model.setValueAt(nv.getHoTen(), row, 1);
	                        model.setValueAt(nv.getSdt(), row, 2);
	                        model.setValueAt(nv.getEmail(), row, 3);
	                        model.setValueAt(namSinhFormatted, row, 4);
	                        model.setValueAt(nv.getDiaChi(), row, 5);
	                        model.setValueAt(ngayVaoLamFormatted, row, 6);
	                        model.setValueAt(luongFormat, row, 7);
	                        model.setValueAt(nv.getCaLam(), row, 8);
	                        model.setValueAt(nv.getMaNQL(), row, 9);

	                        JOptionPane.showMessageDialog(this, "Sửa thành công!");
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!");
	        }
		}else if (o.equals(btnXoa)) {
	        int row = table.getSelectedRow();
	        if (row != -1) {
	            String maNV = model.getValueAt(row, 0).toString();
	            int confirm = JOptionPane.showConfirmDialog(this, 
	                "Bạn có chắc chắn muốn xóa nhân viên này?", 
	                "Xác nhận", 
	                JOptionPane.YES_NO_OPTION);
	            if (confirm == JOptionPane.YES_OPTION) {
	                try {
	                    if (nhanVien_DAO.delete(maNV)) {
	                        model.removeRow(row);
	                        JOptionPane.showMessageDialog(this, "Xóa thành công!");
	                    }
	                } catch (SQLException ex) {
	                    ex.printStackTrace();
	                }
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
	        }
	    }else if (o.equals(btnTim) || o.equals(txtTim)) {
	        String keyword = txtTim.getText().trim();
	        if (!keyword.isEmpty()) {
	            try {
	                List<NhanVien> dsNV = nhanVien_DAO.findNhanVien(keyword);
	                model.setRowCount(0);
	                
	                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	                Locale localeVN = new Locale("vi", "VN");
	                NumberFormat fmt = NumberFormat.getCurrencyInstance(localeVN);
	                
	                for (NhanVien x : dsNV) {
	                    if (!x.getMa().matches("^QL.*")) {
	                        String[] s = {
	                            x.getMa(),
	                            x.getHoTen(),
	                            x.getSdt(),
	                            x.getEmail(),
	                            sdf.format(x.getNamSinh()),
	                            x.getDiaChi(),
	                            sdf.format(x.getNgayVaoLam()),
	                            fmt.format(x.getLuong()),
	                            x.getCaLam(),
	                            x.getMaNQL()
	                        };
	                        model.addRow(s);
	                    }
	                }
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        } else {
	            LoadData();
	        }
	    }else if (o.equals(btnLamMoi)) {
	        lamMoiForm();
	    }

	}
	
//	mouse listener
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int row = table.getSelectedRow();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if (row != -1) {
            txtMaNV.setText(model.getValueAt(row, 0).toString());
            txtHoTen.setText(model.getValueAt(row, 1).toString());
            txtSDT.setText(model.getValueAt(row, 2).toString());
            txtEmail.setText(model.getValueAt(row, 3).toString());
            try {
            	dateNamSinh.setDate(sdf.parse(model.getValueAt(row, 4).toString()));
                dateNgayVaoLam.setDate(sdf.parse(model.getValueAt(row, 6).toString()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            txtDiaChi.setText(model.getValueAt(row, 5).toString());
            txtLuong.setText(model.getValueAt(row, 7).toString().replaceAll("[^\\d]", ""));
            cbxCaLam.setSelectedItem(model.getValueAt(row, 8).toString());
        }
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
//  ================================================================================= methods
//	tạo form cho dòng nhập
	private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(100, 20));
        component.setPreferredSize(new Dimension(300, 30));
        panel.add(lbl);
        panel.add(component);
        return panel;
    }
    
//	thêm dữ liệu vào bảng
    private void LoadData() {
    	List<NhanVien> dsNV = new ArrayList<NhanVien>();
    	try {
    		dsNV = nhanVien_DAO.getAll();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	Locale localeVN = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(localeVN);
    	for(NhanVien x:dsNV) {
    		if(!x.getMa().matches("^NQL.*")) {
    			String luongfommat = fmt.format(x.getLuong());
        		String[] s = {x.getMa(),x.getHoTen(),x.getSdt(),x.getEmail(),String.valueOf(x.getNamSinh()),String.valueOf(x.getDiaChi()),String.valueOf(x.getNgayVaoLam()),luongfommat,String.valueOf(x.getCaLam()),x.getMaNQL()};
        		model.addRow(s);
    		}
    	}
    }
    
//  kiểm tra data
    private boolean ValidData() {
    	if (txtMaNV.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền mã nhân viên");return false;
    	} 
    	else if (txtHoTen.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền họ tên");return false;
    	} 
    	else if (txtSDT.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền số điện thoại");return false;
    	} 
    	else if (txtEmail.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền email");return false;
    	} 
    	else if (dateNamSinh.getDate() == null) {
    	    JOptionPane.showMessageDialog(this, "Phải chọn năm sinh");return false;
    	} 
    	else if (txtDiaChi.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền địa chỉ");return false;
    	}
    	else if (txtLuong.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền lương");return false;
    	} 
    	else if (cbxCaLam.getSelectedItem() == null || cbxCaLam.getSelectedItem().toString().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải chọn ca làm");return false;
    	} 
    	else if(!txtMaNV.getText().trim().matches("NV\\d{2}")) {
    		JOptionPane.showMessageDialog(this, "Mã nhân viên phải có định dạng (NVXX)");return false;
    	} 
    	else if(!txtHoTen.getText().trim().matches("[A-ZÀ-Ỹ][a-zà-ỹ]*( [A-ZÀ-Ỹ][a-zà-ỹ]*)*")) {
    		JOptionPane.showMessageDialog(this, "Chữ cái đầu của tên phải viết hoa");return false;
    	}
    	else if(!txtSDT.getText().trim().matches("^(0|\\+84)(3[2-9]|5[6|8|9]|7[0|6-9]|8[1-5]|9[0-9])[0-9]{7}$")) {
    		JOptionPane.showMessageDialog(this, "Số điện thoại không tồn tại");return false;
    	}
    	else if(!txtEmail.getText().trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
    		JOptionPane.showMessageDialog(this, "Email không đúng định dạng");return false;
    	}
    	else if(!txtLuong.getText().trim().matches("\\d+")) {
    		JOptionPane.showMessageDialog(this, "Lương phải là số");return false;
    	}
    	return true;
    }
    
    private NhanVien taoNhanVien() {
    	NhanVien nv = new NhanVien();
    	nv.setMa(txtMaNV.getText().trim());
    	nv.setHoTen(txtHoTen.getText().trim());
    	nv.setEmail(txtEmail.getText().trim());
    	nv.setSdt(txtSDT.getText().trim());
    	nv.setNamSinh(dateNamSinh.getDate());
    	nv.setDiaChi(txtDiaChi.getText().trim());
    	nv.setNgayVaoLam(dateNgayVaoLam.getDate());
    	nv.setLuong(Double.parseDouble(txtLuong.getText()));
    	nv.setCaLam(cbxCaLam.getSelectedItem().toString());
    	nv.setMaNQL(DangNhap_GUI.MaQLTemp);
    	return nv;
    }
    
    private void lamMoiForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtLuong.setText("");
        txtTim.setText("");
        
        dateNamSinh.setDate(null);
        dateNgayVaoLam.setDate(null);
        
        cbxCaLam.setSelectedIndex(0);
        
        table.clearSelection();
    }
}

