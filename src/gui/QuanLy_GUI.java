package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

public class QuanLy_GUI extends JPanel implements ActionListener,MouseListener{

	private JButton btnNV;
	private JButton btnSP;
	private JButton btnKM;
	private JPanel pnlC;

	public QuanLy_GUI() {
		super();
//		======================================================================================== Pannel North
		setLayout(new BorderLayout());
		JPanel pnlN = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTitle = new JLabel("Quản lý");
		Font fTitle = new Font("TimesRoman",Font.BOLD,20);
		lblTitle.setForeground(Color.decode("#FF3366"));
		lblTitle.setFont(fTitle);
		pnlN.add(lblTitle);
		
//		======================================================================================== Pannel West
		Box pnlW = Box.createVerticalBox();
		Dimension btnSize = new Dimension(200, 50);
		pnlW.setOpaque(true);
		pnlW.setBackground(Color.decode("#FF3366"));
		
//		button quản lý nhân viên
		ImageIcon iconNV = new ImageIcon(getClass().getResource("/148960.png"));
	    Image imgNV = iconNV.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		btnNV = new JButton("Quản lý nhân viên", new ImageIcon(imgNV));
		btnNV.setBackground(Color.decode("#FF3366"));
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
		btnSP.setBackground(Color.decode("#FF3366"));
		btnSP.setBorderPainted(false);
		btnSP.setContentAreaFilled(false);
		btnSP.setFocusPainted(false);
		btnSP.setOpaque(true);
		btnSP.setPreferredSize(btnSize);
		btnSP.setMaximumSize(btnSize);
		btnSP.setHorizontalAlignment(SwingConstants.LEFT);
		btnSP.setIconTextGap(10);
	    
//	    button quản lý khuyến mãi
	    ImageIcon iconKM = new ImageIcon(getClass().getResource("/160256.png"));
	    Image imgKM = iconKM.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		btnKM = new JButton("Quản lý Khuyến mãi", new ImageIcon(imgKM));
		btnKM.setBackground(Color.decode("#FF3366"));
		btnKM.setBorderPainted(false);
		btnKM.setContentAreaFilled(false);
		btnKM.setFocusPainted(false);
		btnKM.setOpaque(true);
		btnKM.setPreferredSize(btnSize);
		btnKM.setMaximumSize(btnSize);
		btnKM.setHorizontalAlignment(SwingConstants.LEFT);
		btnKM.setIconTextGap(10);

		pnlW.add(btnNV);
		pnlW.add(btnSP);
		pnlW.add(btnKM);
		
		btnSP.addActionListener(this);
		btnKM.addActionListener(this);
		btnNV.addActionListener(this);
		
		btnSP.addMouseListener(this);
		btnKM.addMouseListener(this);
		btnNV.addMouseListener(this);
//		======================================================================================== pannel Center
		pnlC = new JPanel(new BorderLayout());
		
//		thêm vào pannel chính
		add(pnlN,BorderLayout.NORTH);
		add(pnlW,BorderLayout.WEST);
		add(pnlC,BorderLayout.CENTER);
		
		pnlC.add(quanLyNV());
	}
//	======================================================================================== mouse listener
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
	    } else if (o == btnSP) {
	    	btnSP.setOpaque(true);
	    	btnSP.setBackground(Color.decode("#FF6699"));
	    } else if(o == btnKM) {
	    	btnKM.setOpaque(true);
	    	btnKM.setBackground(Color.decode("#FF6699"));
	    }
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Object o = e.getSource();

	    if (o == btnNV) {
	        btnNV.setOpaque(true);
	        btnNV.setBackground(Color.decode("#FF3366"));
	    } else if (o == btnSP) {
	    	btnSP.setOpaque(true);
	    	btnSP.setBackground(Color.decode("#FF3366"));
	    } else if(o == btnKM) {
	    	btnKM.setOpaque(true);
	    	btnKM.setBackground(Color.decode("#FF3366"));
	    }
	}
//	======================================================================================== actionPerformed
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o = e.getSource();
		if(o.equals(btnSP)) {
			pnlC.removeAll();
			pnlC.add(quanLySP());
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o.equals(btnKM)) {
			pnlC.removeAll();
			pnlC.add(quanLyKM());
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o.equals(btnNV)) {
			pnlC.removeAll();
			pnlC.add(quanLyNV());
			pnlC.revalidate();
	        pnlC.repaint(); 
		}
	}
	
//	======================================================================================== Methods
//	quản lý sản phẩm
	private JPanel quanLySP() {
		JPanel pnl = new JPanel();
		pnl.setBackground(Color.PINK);
		
		return pnl;
	}
//	quản lý khuyến mãi
	private JPanel quanLyKM() {
		JPanel pnl = new JPanel();
		pnl.setBackground(Color.BLUE);
		
		return pnl;
	}
//	quản lý nhân viên
	private JPanel quanLyNV() {
		JPanel pnl = new JPanel();
		pnl.setBackground(Color.GREEN);
		
		return pnl;
	}
}
	
