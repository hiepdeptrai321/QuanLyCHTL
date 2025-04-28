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
	public static String MaQLTemp;
	public static NhanVien nhanVienHienHanh;
	public static boolean quanLyCheck = false;

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
        JPanel pnlAll = new JPanel() {
            private ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/Vie Viet Nam GIF by Lucky Kat Studios.gif"));

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Vẽ hình động nền
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        pnlAll.setLayout(new BorderLayout());
        
        pnlAll.setBorder(BorderFactory.createEmptyBorder(100, 0, 50, 0));
        Box pnlForm = Box.createVerticalBox();
        pnlForm.setPreferredSize(new Dimension(400, 600));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(0, 40, 100, 30));

     // Panel hình ảnh	
        ImageIcon iconLogo = new ImageIcon(getClass().getResource("/logo.png"));
        Image imgLogo = iconLogo.getImage().getScaledInstance(230, 130, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(imgLogo));
        
        // Panel nhập tên đăng nhập
        JLabel lblUserLabel = new JLabel("Tên đăng nhập:");
        txtUser = new JTextField(25);
        JPanel pnl1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl1.add(lblUserLabel);
        pnl1.add(txtUser);
        pnl1.setOpaque(false);

        // Panel nhập mật khẩu
        JLabel lblPassLabel = new JLabel("Mật khẩu:");
        txtPass = new JPasswordField(25);
        JPanel pnl2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnl2.add(lblPassLabel);
        pnl2.add(txtPass);
        txtPass.addActionListener(this); 
        pnl2.setOpaque(false);

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
        pnlForm.add(pnl1);
        pnlForm.add(pnl2);
        pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(btnLogin);
        pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(lblError);
        pnlAll.add(lblLogo,BorderLayout.NORTH);
        pnlAll.add(pnlForm, BorderLayout.CENTER);
        pnlAll.setOpaque(false);
       
        
        // Thêm vào giao diện chính
        add(lblImage, BorderLayout.WEST);
        add(pnlAll, BorderLayout.CENTER);
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
    	boolean userTemp = false;
    	TaiKhoan tkTemp = new TaiKhoan();

    	if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }
    	
    	try {

            tkTemp = tk.getByTK(user);
            if (tkTemp != null) {
                passTemp = tkTemp.getMatKhau();
                userTemp = true;
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }

        if (!userTemp) {
            lblError.setText("Sai tên đăng nhâp");
        }else if(!passTemp.equals(pass)) {
        	lblError.setText("Sai mật khẩu");
        }else {
            lblError.setText("");
            
            if(tkTemp.getVaiTro().compareTo("quanly")==0) {
            	quanLyCheck =true;
            	try {
            		MaQLTemp = tkTemp.getMaTK();
            		nhanVienHienHanh = ql.getByTK(tkTemp.getMaTK());
            	} catch (SQLException e1) {
	            	// TODO Auto-generated catch block
	            	e1.printStackTrace();
            	}
            }
        	else {
            	try {
            		MaQLTemp = tkTemp.getMaTK();
            		nhanVienHienHanh = nv.getTKbangMaTK(tkTemp.getMaTK());
            	} catch (SQLException e1) {
	            	// TODO Auto-generated catch block
	            	e1.printStackTrace();
            	}
        	}
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
