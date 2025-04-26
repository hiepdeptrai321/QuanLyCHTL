package gui;

import javax.swing.*;

import connectDB.ConnectDB;
import dao.NguoiQuanLy_DAO;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
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
	private NhanVien_DAO nv;
	private NguoiQuanLy_DAO ql;
	public static NhanVien nhanVienHienHanh;
	

    public DangNhap_GUI() {
        super("Đăng nhập hệ thống");
        ConnectDB.getInstance().connect();
        tk = new TaiKhoan_DAO(ConnectDB.getConnection());
        ql = new NguoiQuanLy_DAO(ConnectDB.getConnection());
		nv = new NhanVien_DAO(ConnectDB.getConnection());
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
           for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
               if ("Nimbus".equals(info.getName())) {
                   UIManager.setLookAndFeel(info.getClassName());
                   break;
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	String user = txtUser.getText();
    	String pass = new String(txtPass.getPassword());
    	String passTemp = new String();
    	TaiKhoan tkTemp = new TaiKhoan();
    	if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }
    	
    	try {
    		tkTemp = tk.getByTK(txtUser.getText());
    		passTemp = tkTemp.getMatKhau();
		} catch (SQLException e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
        

        if (passTemp.equals(pass)==false) {
            lblError.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
        } else {
            lblError.setText("");
            
            if(tkTemp.getVaiTro().compareTo("quanly")==0) {
            	try {
            		nhanVienHienHanh = ql.getByTK(tkTemp.getMaTK());
            	} catch (SQLException e1) {
	            	// TODO Auto-generated catch block
	            	e1.printStackTrace();
            	}
            }
        	else {
            	try {
            		nhanVienHienHanh = nv.getByTK(tkTemp.getMaTK());
            	} catch (SQLException e1) {
	            	// TODO Auto-generated catch block
	            	e1.printStackTrace();
            	}
        	}
			
            System.out.println(nhanVienHienHanh.getHoTen());
            new TrangChu_GUI();
            
            this.setVisible(false);
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
            System.err.println("Không tìm thấy hoặc không thể áp dụng Nimbus Look and Feel. Sử dụng giao diện mặc định.");
        }
        new DangNhap_GUI().setVisible(true);
    }
}
