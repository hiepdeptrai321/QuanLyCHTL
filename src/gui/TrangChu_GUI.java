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
	private JButton btnMA;
	private JButton btnThoat;
	private JPanel pnlC;
	
	public TrangChu_GUI() {
		super("Cửa hàng tiện lợi");
		setSize(1600,800);
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
		JPanel pnlN_R = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JLabel dateLabel = new JLabel();
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd / MMMM / yyyy");
		Timer timer = new Timer(1000, e -> {
			String dateText = sdf.format(new Date());
			dateLabel.setText(dateText);
	    });
	    timer.start();
	    pnlN_R.add(dateLabel);
	    
//	    thêm màu cho vùng North
	    pnlN_L.setBackground(Color.decode("#FF3366"));
	    pnlN_R.setBackground(Color.decode("#FF3366"));
	    
	    pnlN.add(pnlN_L);
	    pnlN.add(pnlN_R);
	    
//	    ======================================================================================== Pannel West
	    Box pnlW = Box.createVerticalBox();
	    pnlW.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
	    
//	    nút vào giao diện nhân viên
	    ImageIcon iconNV = new ImageIcon(getClass().getResource("/130154.png"));
	    Image imgNV = iconNV.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    btnNV = new JButton(new ImageIcon(imgNV));
	    btnNV.setBorderPainted(false);
	    btnNV.setContentAreaFilled(false);
	    btnNV.setFocusPainted(false);
	    btnNV.setOpaque(false);
	    
//	    nút vào giao diện quản lý
	    ImageIcon iconMA = new ImageIcon(getClass().getResource("/131440.png"));
	    Image imgMA = iconMA.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    btnMA = new JButton(new ImageIcon(imgMA));
	    btnMA.setBorderPainted(false);
	    btnMA.setContentAreaFilled(false);
	    btnMA.setFocusPainted(false);
	    btnMA.setOpaque(false);
	    
//	    nút thoát
	    ImageIcon iconThoat = new ImageIcon(getClass().getResource("/142005.png"));
	    Image imgThoat = iconThoat.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    btnThoat = new JButton(new ImageIcon(imgThoat));
	    btnThoat.setBorderPainted(false);
	    btnThoat.setContentAreaFilled(false);
	    btnThoat.setFocusPainted(false);
	    btnThoat.setOpaque(false);
	    
//	    add listener
	    btnNV.addMouseListener(this);
	    btnMA.addMouseListener(this);
	    btnThoat.addMouseListener(this);
	    
	    btnNV.addActionListener(this);
	    btnMA.addActionListener(this);
	    btnThoat.addActionListener(this);
	    
	    pnlW.add(btnNV);
	    pnlW.add(btnMA);
	    pnlW.add(btnThoat);
	    
//	    ======================================================================================== Pannel Center
	    pnlC = new JPanel(new BorderLayout());
	    
//	    ======================================================================================== Các thành phần phụ   
//		thêm các pannel vào pannel lớn
		pnlAll.add(pnlN,BorderLayout.NORTH);
		pnlAll.add(pnlW,BorderLayout.WEST);
		pnlAll.add(pnlC, BorderLayout.CENTER);
		pnlC.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 0));
		
//		thêm pannel vào jframe
		add(pnlAll);
		pnlC.add(new NhanVien_GUI(), BorderLayout.CENTER);
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	    Object o = e.getSource();

	    if (o == btnNV) {
	        btnNV.setOpaque(true);
	        btnNV.setBackground(Color.decode("#FF6699"));
	    } else if (o == btnMA) {
	        btnMA.setOpaque(true);
	        btnMA.setBackground(Color.decode("#FF6699"));
	    } else if(o == btnThoat) {
	    	btnThoat.setOpaque(true);
	    	btnThoat.setBackground(Color.decode("#FF6699"));
	    }
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Object o = e.getSource();

	    if (o == btnNV) {
	        btnNV.setOpaque(false);
	        btnNV.setBackground(null);
	    } else if (o == btnMA) {
	        btnMA.setOpaque(false);
	        btnMA.setBackground(null);
	    } else if(o == btnThoat) {
	    	btnThoat.setOpaque(false);
	    	btnThoat.setBackground(null);
	    }
	}
//	======================================================================================== action Performed
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o = e.getSource();
		
		if(o == btnMA) {
			pnlC.removeAll();
			pnlC.add(new QuanLy_GUI(), BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnNV) {
			pnlC.removeAll();
			pnlC.add(new NhanVien_GUI(), BorderLayout.CENTER);
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o == btnThoat) {
			
		}
	}
}
