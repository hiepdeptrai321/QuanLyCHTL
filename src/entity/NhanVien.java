package entity;

import java.sql.Date;

public class NhanVien extends Nguoi{
    private Date ngayVaoLam;
    private double luong;
    private String caLam;
    private String maNQL;
    private String maTK;
	public NhanVien(String ma, String hoTen, String sdt, String email, Date namSinh, String diaChi, Date ngayVaoLam,
			double luong, String caLam, String maNQL, String maTK) {
		super(ma, hoTen, sdt, email, namSinh, diaChi);
		this.ngayVaoLam = ngayVaoLam;
		this.luong = luong;
		this.caLam = caLam;
		this.maNQL = maNQL;
		this.maTK = maTK;
	}
	public NhanVien() {
		super();
	}
	public Date getNgayVaoLam() {
		return ngayVaoLam;
	}
	public double getLuong() {
		return luong;
	}
	public String getCaLam() {
		return caLam;
	}
	public String getMaNQL() {
		return maNQL;
	}
	public String getMaTK() {
		return maTK;
	}
	public void setNgayVaoLam(Date ngayVaoLam) {
		this.ngayVaoLam = ngayVaoLam;
	}
	public void setLuong(double luong) {
		this.luong = luong;
	}
	public void setCaLam(String caLam) {
		this.caLam = caLam;
	}
	public void setMaNQL(String maNQL) {
		this.maNQL = maNQL;
	}
	public void setMaTK(String maTK) {
		this.maTK = maTK;
	}
	@Override
	public String toString() {
		return String.format(
				"NhanVien [ngayVaoLam=%s, luong=%s, caLam=%s, maNQL=%s, maTK=%s, getMa()=%s, getHoTen()=%s, getSdt()=%s, getEmail()=%s, getNamSinh()=%s, getDiaChi()=%s, toString()=%s, getClass()=%s, hashCode()=%s]",
				ngayVaoLam, luong, caLam, maNQL, maTK, getMa(), getHoTen(), getSdt(), getEmail(), getNamSinh(),
				getDiaChi(), super.toString(), getClass(), hashCode());
	}
    
	
    
}

