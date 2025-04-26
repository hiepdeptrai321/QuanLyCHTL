package gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;

public class NhanVien_GUI extends JPanel {
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
    private JTextField txtMaNQL;
    private JTextField txtMaTK;

    public NhanVien_GUI() {
        setLayout(new BorderLayout(10, 10));

        // ============================ Panel Trái ============================
        JPanel pnlL = new JPanel(new BorderLayout(5, 5));
        pnlL.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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

        // ============================ Panel Phải ============================
        Box pnlR = Box.createVerticalBox();
        pnlR.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createTitledBorder("Thông tin chi tiết nhân viên"),
        	    BorderFactory.createEmptyBorder(10, 10, 200, 10)
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
        pnlR.add(createFormRow("Mã NQL:", txtMaNQL = new JTextField()));
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

        // === ADD TO MAIN PANEL ===
        add(pnlL, BorderLayout.WEST);
        add(pnlR, BorderLayout.CENTER);
    }

    private JPanel createFormRow(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout());
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(100, 20));
        component.setPreferredSize(new Dimension(300, 25));
        panel.add(lbl);
        panel.add(component);
        return panel;
    }
}