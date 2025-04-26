package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NhanVien_GUI extends JPanel implements ActionListener{
    private JTable table;
    private DefaultTableModel model;
    private JDateChooser dateNamSinh;
    private JDateChooser dateNgayVaoLam;
    private JDateChooser dateCaLam;
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

        String[] columns = {"Mã", "Họ tên", "SĐT", "Email", "Ngày sinh", "Địa chỉ", "Ngày vào làm", "Lương", "Ca", "Mã NQL", "Mã TK"};
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
        pnlR.add(createFormRow("Ca:", dateCaLam = new JDateChooser()));
        pnlR.add(createFormRow("Mã TK:", txtMaTK = new JTextField()));
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
        

        pnlR.add(buttonPanel);

//      ================================================================================= thêm pannel
        add(pnlL, BorderLayout.WEST);
        add(pnlR, BorderLayout.CENTER);
        LoadData();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o = e.getSource();
		if(o.equals(btnThem)) {
			
		}
	}
	
//  ================================================================================= methods
//	tạo form cho dòng nhập
	private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(100, 20));
        component.setPreferredSize(new Dimension(300, 25));
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
    		String luongfommat = fmt.format(x.getLuong());
    		String[] s = {x.getMa(),x.getHoTen(),x.getSdt(),x.getEmail(),String.valueOf(x.getNamSinh()),String.valueOf(x.getDiaChi()),String.valueOf(x.getNgayVaoLam()),luongfommat,String.valueOf(x.getCaLam()),x.getMaNQL(),x.getMaTK()};
    		model.addRow(s);
    	}
    }
    
//  kiểm tra data
    private void ValidData() {
    	if (txtMaNV.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền mã nhân viên");
    	} else if (txtHoTen.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền họ tên");
    	} else if (txtSDT.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền số điện thoại");
    	} else if (txtEmail.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền email");
    	} else if (dateNamSinh.getDate() == null) {
    	    JOptionPane.showMessageDialog(this, "Phải chọn năm sinh");
    	} else if (txtDiaChi.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền địa chỉ");
    	} else if (dateNgayVaoLam.getDate() == null) {
    	    JOptionPane.showMessageDialog(this, "Phải chọn ngày vào làm");
    	} else if (txtLuong.getText().trim().isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Phải điền lương");
    	} 
//    	else if (cboCaLam.getSelectedItem() == null || cboCaLam.getSelectedItem().toString().trim().isEmpty()) {
//    	    JOptionPane.showMessageDialog(this, "Phải chọn ca làm");
//    	} 
    }
}