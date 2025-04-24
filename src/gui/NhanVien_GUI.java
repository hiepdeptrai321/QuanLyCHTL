package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;

public class NhanVien_GUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMa, txtHoTen, txtSDT, txtEmail, txtDiaChi, txtLuong, txtMaNQL, txtMaTK;
    private JDateChooser dateNamSinh, dateNgayVaoLam, dateCaLam;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTim;
    private JTextField txtTim;

    public NhanVien_GUI() {
        setLayout(new BorderLayout(10, 10));

        // === BẢNG DANH SÁCH (BÊN TRÁI) ===
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        txtTim = new JTextField();
        btnTim = new JButton("Tìm");
        searchPanel.add(txtTim, BorderLayout.CENTER);
        searchPanel.add(btnTim, BorderLayout.EAST);

        String[] columns = {"Mã", "Họ tên", "SĐT", "Email", "Ngày sinh", "Địa chỉ", "Ngày vào làm", "Lương", "Ca", "Mã NQL", "Mã TK"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(600, 0));

        // === FORM CHI TIẾT (BÊN PHẢI) ===
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(11, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết nhân viên"));

        txtMa = new JTextField();
        txtHoTen = new JTextField();
        txtSDT = new JTextField();
        txtEmail = new JTextField();
        txtDiaChi = new JTextField();
        txtLuong = new JTextField();
        txtMaNQL = new JTextField();
        txtMaTK = new JTextField();
        dateNamSinh = new JDateChooser();
        dateNgayVaoLam = new JDateChooser();
        dateCaLam = new JDateChooser();

        formPanel.add(new JLabel("Mã NV:"));
        formPanel.add(txtMa);
        formPanel.add(new JLabel("Họ tên:"));
        formPanel.add(txtHoTen);
        formPanel.add(new JLabel("SĐT:"));
        formPanel.add(txtSDT);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Ngày sinh:"));
        formPanel.add(dateNamSinh);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(txtDiaChi);
        formPanel.add(new JLabel("Ngày vào làm:"));
        formPanel.add(dateNgayVaoLam);
        formPanel.add(new JLabel("Lương:"));
        formPanel.add(txtLuong);
        formPanel.add(new JLabel("Ca làm:"));
        formPanel.add(dateCaLam);
        formPanel.add(new JLabel("Mã NQL:"));
        formPanel.add(txtMaNQL);
        formPanel.add(new JLabel("Mã TK:"));
        formPanel.add(txtMaTK);

        // === NÚT CHỨC NĂNG ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnThem = new JButton("Thêm");
        btnSua = new JButton("Sửa");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // === ADD TO MAIN PANEL ===
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }
}
