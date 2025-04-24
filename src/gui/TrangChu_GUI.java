package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrangChu_GUI extends JFrame implements MouseListener,ActionListener{
	private JButton btnNV;
	private JButton btnThoat;
	private JPanel pnlC;
	private JPanel pnlRight;
	private JButton btnSP;
	private JButton btnKM;
	private JButton selectedButton = null;
	private JButton btnKH;
	private JButton btnHD;

	
	public TrangChu_GUI() {
		super("Cửa hàng tiện lợi");
		setSize(1400,800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		JPanel pnlAll = new JPanel(new BorderLayout());
		
//		======================================================================================== Pannel North
		Box pnlN = Box.createHorizontalBox();
		
//		trái
		JPanel pnlN_L = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTitle = new JLabel("Cửa hàng tiện lợi");
		Font fTitle = new Font("TimesRoman",Font.BOLD,20);
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setFont(fTitle);
		pnlN_L.add(lblTitle);
		
//		phải
		JPanel pnlN_R = new JPanel();
		pnlN_R.setLayout(new BoxLayout(pnlN_R, BoxLayout.Y_AXIS));
		pnlN_R.setBackground(Color.decode("#FF3366"));

		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		wrapper.setOpaque(false);
		JLabel dateLabel = new JLabel();
		dateLabel.setForeground(Color.WHITE);
		dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
		wrapper.add(dateLabel);

		pnlN_R.add(Box.createVerticalGlue());
		pnlN_R.add(wrapper);
		pnlN_R.add(Box.createVerticalGlue());

		// Timer cập nhật thời gian
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd / MM / yyyy - HH:mm:ss");
		Timer timer = new Timer(1000, e -> {
		    String dateText = sdf.format(new Date());
		    dateLabel.setText(dateText);
		});
		timer.start();
	    
//	    thêm màu cho vùng North
	    pnlN_L.setBackground(Color.decode("#FF3366"));
	    pnlN_R.setBackground(Color.decode("#FF3366"));
	    
	    pnlN.add(pnlN_L);
	    pnlN.add(pnlN_R);
	    
//	    ======================================================================================== Pannel West
	    Box pnlW = Box.createVerticalBox();
	    pnlW.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
	    Dimension btnSize = new Dimension(200, 50);
	    
//		button quản lý nhân viên
	    ImageIcon iconNV = new ImageIcon(getClass().getResource("/148960.png"));
	    Image imgNV = iconNV.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    btnNV = new JButton("Quản lý nhân viên", new ImageIcon(imgNV));
	    btnNV.setBorderPainted(false);
	    btnNV.setContentAreaFilled(false);
	    btnNV.setFocusPainted(false);
	    btnNV.setOpaque(true);
	    btnNV.setPreferredSize(btnSize);
    	btnNV.setMaximumSize(btnSize);
    	btnNV.setHorizontalAlignment(SwingConstants.LEFT);
    	btnNV.setIconTextGap(10);
    
//		button quản lý sản phẩm
    	ImageIcon iconSP = new ImageIcon(getClass().getResource("/139413.png"));
    	Image imgSP = iconSP.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
    	btnSP = new JButton("Quản lý sản phẩm", new ImageIcon(imgSP));
    	btnSP.setBorderPainted(false);
    	btnSP.setContentAreaFilled(false);
		btnSP.setFocusPainted(false);
		btnSP.setOpaque(true);
		btnSP.setPreferredSize(btnSize);
		btnSP.setMaximumSize(btnSize);
		btnSP.setHorizontalAlignment(SwingConstants.LEFT);
		btnSP.setIconTextGap(10);
    
//    	button quản lý khuyến mãi
		ImageIcon iconKM = new ImageIcon(getClass().getResource("/160256.png"));
		Image imgKM = iconKM.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		btnKM = new JButton("Quản lý Khuyến mãi", new ImageIcon(imgKM));
		btnKM.setBorderPainted(false);
		btnKM.setContentAreaFilled(false);
		btnKM.setFocusPainted(false);
		btnKM.setOpaque(true);
		btnKM.setPreferredSize(btnSize);
		btnKM.setMaximumSize(btnSize);
		btnKM.setHorizontalAlignment(SwingConstants.LEFT);
		btnKM.setIconTextGap(10);
		
//		button khách hàng
		ImageIcon iconKH = new ImageIcon(getClass().getResource("/148899.png"));
		Image imgKH = iconKH.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		btnKH = new JButton("Quản lý khách hàng", new ImageIcon(imgKH));
		btnKH.setBorderPainted(false);
		btnKH.setContentAreaFilled(false);
		btnKH.setFocusPainted(false);
		btnKH.setOpaque(true);
		btnKH.setPreferredSize(btnSize);
		btnKH.setMaximumSize(btnSize);
		btnKH.setHorizontalAlignment(SwingConstants.LEFT);
		btnKH.setIconTextGap(10);
    
//		button Hóa đơn
		ImageIcon iconHD = new ImageIcon(getClass().getResource("/127907.png"));
		Image imgHD = iconHD.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		btnHD = new JButton("Hóa đơn", new ImageIcon(imgHD));
		btnHD.setBorderPainted(false);
		btnHD.setContentAreaFilled(false);
		btnHD.setFocusPainted(false);
		btnHD.setOpaque(true);
		btnHD.setPreferredSize(btnSize);
		btnHD.setMaximumSize(btnSize);
		btnHD.setHorizontalAlignment(SwingConstants.LEFT);
		btnHD.setIconTextGap(10);
	    
//	    nút thoát
	    ImageIcon iconThoat = new ImageIcon(getClass().getResource("/142005.png"));
	    Image imgThoat = iconThoat.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    btnThoat = new JButton(new ImageIcon(imgThoat));
	    btnThoat.setBorderPainted(false);
	    btnThoat.setContentAreaFilled(false);
	    btnThoat.setFocusPainted(false);
	    btnThoat.setOpaque(true);
	    
//	    add listener
	    btnNV.addMouseListener(this);
	    btnSP.addMouseListener(this);
	    btnKM.addMouseListener(this);
	    btnKH.addMouseListener(this);
	    btnHD.addMouseListener(this);
	    btnThoat.addMouseListener(this);
	    
	    btnNV.addActionListener(this);
	    btnSP.addActionListener(this);
	    btnKM.addActionListener(this);
	    btnKH.addActionListener(this);
	    btnHD.addActionListener(this);
	    btnThoat.addActionListener(this);
	    
	    pnlW.add(btnHD);
	    pnlW.add(btnKH);
	    pnlW.add(btnNV);
	    pnlW.add(btnSP);
	    pnlW.add(btnKM);
	    pnlW.add(Box.createVerticalGlue());
	    pnlW.add(btnThoat);
	    
//	    ======================================================================================== Pannel Center
	    pnlC = new JPanel(new BorderLayout());

	    // tạo panel bên phải
	    pnlRight = new JPanel();
	    pnlRight.setPreferredSize(new Dimension(300, 0));
	    pnlRight.setBackground(Color.LIGHT_GRAY);
	    pnlRight.setVisible(false);
	    
	    pnlC.add(new HoaDon_GUI());
	    pnlC.add(pnlRight, BorderLayout.EAST);
	    btnHD.setBackground(Color.decode("#FF6699"));
	    btnHD.setOpaque(true);
	    selectedButton = btnHD;
	    
	    
//	    ======================================================================================== Các thành phần phụ   
//		thêm các pannel vào pannel lớn
		pnlAll.add(pnlN,BorderLayout.NORTH);
		pnlAll.add(pnlW,BorderLayout.WEST);
		pnlAll.add(pnlC, BorderLayout.CENTER);
		
//		thêm pannel vào jframe
		add(pnlAll);
//		pnlC.add(new NhanVien_GUI(), BorderLayout.CENTER);
		setVisible(true);
	}
	
//	======================================================================================== mouse clicked
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
	    Object o = e.getSource();

	    if (o instanceof JButton && !o.equals(btnThoat)) {
	        if (selectedButton != null) {
	            selectedButton.setBackground(null);
	            selectedButton.setOpaque(false);
	        }

	        selectedButton = (JButton) o;
	        selectedButton.setOpaque(true);
	        selectedButton.setBackground(Color.decode("#FF6699"));
	    }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    Object o = e.getSource();

	    if (o == btnNV) {
	        btnNV.setOpaque(true);
	        btnNV.setBackground(Color.decode("#FF6699"));
	    } else if (o == btnSP) {
	        btnSP.setOpaque(true);
	        btnSP.setBackground(Color.decode("#FF6699"));
	    }else if (o == btnKM) {
	        btnKM.setOpaque(true);
	        btnKM.setBackground(Color.decode("#FF6699"));
	    }
	    else if (o == btnKH) {
	    	btnKH.setOpaque(true);
	    	btnKH.setBackground(Color.decode("#FF6699"));
	    }
	    else if (o == btnHD) {
	    	btnHD.setOpaque(true);
	    	btnHD.setBackground(Color.decode("#FF6699"));
	    }
	}


	@Override
	public void mouseExited(MouseEvent e) {
	    Object o = e.getSource();

	    if (o instanceof JButton && o != selectedButton) {
	        JButton btn = (JButton) o;
	        btn.setOpaque(false);
	        btn.setBackground(null);
	    }
	}
//	======================================================================================== action Performed
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o = e.getSource();
		
		if(o == btnNV) {
			pnlC.removeAll();
			pnlC.add(new NhanVien_GUI(),BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnKM) {
			pnlC.removeAll();
			pnlC.add(new KhuyenMai_GUI(),BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnSP) {
			pnlC.removeAll();
//			pnlC.add(new HoaDon_GUI(),BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnKH) {
			pnlC.removeAll();
//			pnlC.add(new HoaDon_GUI(),BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnHD) {
			pnlC.removeAll();
			pnlC.add(new HoaDon_GUI(),BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnThoat) {
			DangNhap_GUI dangNhap = new DangNhap_GUI();
			dangNhap.setVisible(true);
			this.dispose();
			pnlC.revalidate();
	        pnlC.repaint(); 
		}
	}
	public static void main(String[] args) {
		new TrangChu_GUI();
	}
}
