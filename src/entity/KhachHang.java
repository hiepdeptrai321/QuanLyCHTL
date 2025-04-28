package entity;

import java.sql.Date;

public class KhachHang extends Nguoi{
    private Date ngayDangKy;
    private int diemTichLuy;
    private int hangThanhVien;
    private int soLanMuaHang;
    private String maNV;

	public KhachHang(String ma, String hoTen, String sdt, String email, Date namSinh, String diaChi, Date ngayDangKy,
			int diemTichLuy, int hangThanhVien, int soLanMuaHang, String maNV) {
		super(ma, hoTen, sdt, email, namSinh, diaChi);
		this.ngayDangKy = ngayDangKy;
		this.diemTichLuy = diemTichLuy;
		this.hangThanhVien = hangThanhVien;
		this.soLanMuaHang = soLanMuaHang;

		this.maNV = maNV;
	}
	
	public KhachHang() {
		super();
	}

	public Date getNgayDangKy() {
		return ngayDangKy;
	}
	public int getDiemTichLuy() {
		return diemTichLuy;
	}
	public int getHangThanhVien() {
		return hangThanhVien;
	}
	public int getSoLanMuaHang() {
		return soLanMuaHang;
	}
	public String getMaNV() {
		return maNV;
	}
	public void setNgayDangKy(Date ngayDangKy) {
		this.ngayDangKy = ngayDangKy;
	}
	public void setDiemTichLuy(int diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}
	public void setHangThanhVien(int hangThanhVien) {
		this.hangThanhVien = hangThanhVien;
	}
	public void setSoLanMuaHang(int soLanMuaHang) {
		this.soLanMuaHang = soLanMuaHang;
	}
	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

	@Override
	public String toString() {
		return String.format(
				"KhachHang [ngayDangKy=%s, diemTichLuy=%s, hangThanhVien=%s, soLanMuaHang=%s, maNV=%s, getMa()=%s, getHoTen()=%s, getSdt()=%s, getEmail()=%s, getNamSinh()=%s, getDiaChi()=%s, toString()=%s, getClass()=%s, hashCode()=%s]",
				ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, maNV, getMa(), getHoTen(), getSdt(), getEmail(),
				getNamSinh(), getDiaChi(), super.toString(), getClass(), hashCode());
	}
	
    
}
