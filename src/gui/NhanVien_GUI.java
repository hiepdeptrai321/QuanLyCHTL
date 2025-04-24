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
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

public class NhanVien_GUI extends JPanel implements ActionListener,MouseListener{

	private JButton btnKH;
	private JButton btnHD;
	private JPanel pnlC;

	public NhanVien_GUI() {
		super();
//		======================================================================================== Pannel North
		setLayout(new BorderLayout());
		JPanel pnlN = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblTitle = new JLabel("Chức năng nhân viên");
		Font fTitle = new Font("TimesRoman",Font.BOLD,20);
		lblTitle.setForeground(Color.decode("#FF3366"));
		lblTitle.setFont(fTitle);
		pnlN.add(lblTitle);
		
//		======================================================================================== Pannel West
		Box pnlW = Box.createVerticalBox();
		Dimension btnSize = new Dimension(200, 50);
		
//		button khách hàng
		ImageIcon iconKH = new ImageIcon(getClass().getResource("/148899.png"));
	    Image imgKH = iconKH.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
		btnKH = new JButton("Khách hàng", new ImageIcon(imgKH));
		btnKH.setBorderPainted(false);
		btnKH.setContentAreaFilled(false);
		btnKH.setFocusPainted(false);
		btnKH.setOpaque(true);
		btnKH.setPreferredSize(btnSize);
		btnKH.setMaximumSize(btnSize);
		btnKH.setHorizontalAlignment(SwingConstants.LEFT);
		btnKH.setIconTextGap(10);
	    
//		button Hóa đơn
	    ImageIcon iconSP = new ImageIcon(getClass().getResource("/127907.png"));
	    Image imgSP = iconSP.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
	    btnHD = new JButton("Hóa đơn", new ImageIcon(imgSP));
	    btnHD.setBorderPainted(false);
	    btnHD.setContentAreaFilled(false);
	    btnHD.setFocusPainted(false);
	    btnHD.setOpaque(true);
	    btnHD.setPreferredSize(btnSize);
		btnHD.setMaximumSize(btnSize);
		btnHD.setHorizontalAlignment(SwingConstants.LEFT);
		btnHD.setIconTextGap(10);
	    

		pnlW.add(btnKH);
		pnlW.add(btnHD);
		
		btnKH.addActionListener(this);
		btnHD.addActionListener(this);
		
		btnKH.addMouseListener(this);
		btnHD.addMouseListener(this);
//		======================================================================================== pannel Center
		pnlC = new JPanel(new BorderLayout());
		
//		thêm vào pannel chính
		add(pnlN,BorderLayout.NORTH);
		add(pnlW,BorderLayout.WEST);
		add(pnlC,BorderLayout.CENTER);
		
		pnlC.add(KH());
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

	    if (o == btnKH) {
	    	btnKH.setOpaque(true);
	    	btnKH.setBackground(Color.decode("#FF6699"));
	    } else if (o == btnHD) {
	    	btnHD.setOpaque(true);
	    	btnHD.setBackground(Color.decode("#FF6699"));
	    }
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Object o = e.getSource();

	    if (o == btnKH) {
	    	btnKH.setOpaque(true);
	    	btnKH.setBackground(null);
	    } else if (o == btnHD) {
	    	btnHD.setOpaque(true);
	    	btnHD.setBackground(null);
	    }
	}
//	======================================================================================== actionPerformed
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object o = e.getSource();
		if(o.equals(btnKH)) {
			pnlC.removeAll();
			pnlC.add(KH());
			pnlC.revalidate();
	        pnlC.repaint(); 
		}else if(o.equals(btnHD)) {
			pnlC.removeAll();
			pnlC.add(HD());
			pnlC.revalidate();
	        pnlC.repaint(); 
		}
	}
	
//	======================================================================================== Methods
//	Khách hàng
	private JPanel KH() {
		JPanel pnl = new JPanel();
		pnl.setBackground(Color.PINK);
		
		return pnl;
	}
//	Hóa đơn
	private JPanel HD() {
		JPanel pnl = new JPanel();
		pnl.setBackground(Color.BLUE);
		
		return pnl;
	}
}
	
