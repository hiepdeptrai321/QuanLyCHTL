package gui;

import javax.swing.*;

import connectDB.ConnectDB;
import dao.TaiKhoan_DAO;
import entity.TaiKhoan;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class DangNhap_GUI extends JFrame implements ActionListener {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JLabel lblError;
	private TaiKhoan_DAO tk;
	

    public DangNhap_GUI() {
        super("Đăng nhập hệ thống");
        ConnectDB.getInstance().connect();
        tk = new TaiKhoan_DAO(ConnectDB.getInstance().getConnection());
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================================ Panel trái - hình ảnh
        ImageIcon icon = new ImageIcon(getClass().getResource("/cuaHang.jpg"));
        Image img = icon.getImage().getScaledInstance(600, 600, Image.SCALE_SMOOTH);
        JLabel lblImage = new JLabel(new ImageIcon(img));

        // ================================ Panel phải - form đăng nhập
        Box pnlForm = Box.createVerticalBox();
        pnlForm.setPreferredSize(new Dimension(400, 600));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(180, 40, 180, 30)); // padding

        // Panel nhập tên đăng nhập
        JLabel lblUserLabel = new JLabel("Tên đăng nhập:");
        txtUser = new JTextField(25);
        JPanel pnl1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl1.add(lblUserLabel);
        pnl1.add(txtUser);

        // Panel nhập mật khẩu
        JLabel lblPassLabel = new JLabel("Mật khẩu:");
        txtPass = new JPasswordField(25);
        JPanel pnl2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl2.add(lblPassLabel);
        pnl2.add(txtPass);

        // Nút đăng nhập
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(this);
        btnLogin.setFocusPainted(false);

        // Label hiển thị lỗi
        lblError = new JLabel("");
        lblError.setForeground(Color.RED);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm các thành phần vào form
        pnlForm.add(Box.createVerticalGlue());
        pnlForm.add(pnl1);
        pnlForm.add(pnl2);
        pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(btnLogin);
        pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(lblError);
        pnlForm.add(Box.createVerticalGlue());

        // Thêm vào giao diện chính
        add(lblImage, BorderLayout.WEST);
        add(pnlForm, BorderLayout.CENTER);
        
        try {
            // Use Nimbus for a modern look, or System Look and Feel
           for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
               if ("Nimbus".equals(info.getName())) {
                   UIManager.setLookAndFeel(info.getClassName());
//                   UIManager.setFocusPainted(false);
                   break;
               }
           }
           // If Nimbus not available, fallback to System L&F
           // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
       } catch (Exception e) {
            // If L&F setting fails, it might default to Metal, which is okay
           e.printStackTrace();
       }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	String user = txtUser.getText();
    	String pass = new String(txtPass.getPassword());
    	String passTemp = new String();
    	if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
        }
    	
    	try {
    		TaiKhoan tkTemp = tk.getByTK(txtUser.getText());
    		passTemp =tkTemp.getMatKhau();
		} catch (SQLException e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
        

        if (passTemp.equals(pass)==false) {
            lblError.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
        } else {
            lblError.setText("");
            new TrangChu_GUI();
        }
    }

    public static void main(String[] args) {
        new DangNhap_GUI().setVisible(true);
    }
}
