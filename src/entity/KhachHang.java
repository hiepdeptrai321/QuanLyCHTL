package entity;

import java.sql.Date;

public class KhachHang {
    private String ma;
    private Date ngayDangKy;
    private int diemTichLuy;
    private int hangThanhVien;
    private int soLanMuaHang;
    private String hoTen;
    private String sdt;
    private String email;
    private Date namSinh;
    private String maNV;
	public KhachHang(String ma, Date ngayDangKy, int diemTichLuy, int hangThanhVien, int soLanMuaHang, String hoTen,
			String sdt, String email, Date namSinh, String maNV) {
		super();
		this.ma = ma;
		this.ngayDangKy = ngayDangKy;
		this.diemTichLuy = diemTichLuy;
		this.hangThanhVien = hangThanhVien;
		this.soLanMuaHang = soLanMuaHang;
		this.hoTen = hoTen;
		this.sdt = sdt;
		this.email = email;
		this.namSinh = namSinh;
		this.maNV = maNV;
	}
	public KhachHang() {
		super();
	}
	public String getMa() {
		return ma;
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
	public String getHoTen() {
		return hoTen;
	}
	public String getSdt() {
		return sdt;
	}
	public String getEmail() {
		return email;
	}
	public Date getNamSinh() {
		return namSinh;
	}
	public String getMaNV() {
		return maNV;
	}
	public void setMa(String ma) {
		this.ma = ma;
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
	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setNamSinh(Date namSinh) {
		this.namSinh = namSinh;
	}
	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
	@Override
	public String toString() {
		return String.format(
				"KhachHang [ma=%s, ngayDangKy=%s, diemTichLuy=%s, hangThanhVien=%s, soLanMuaHang=%s, hoTen=%s, sdt=%s, email=%s, namSinh=%s, maNV=%s]",
				ma, ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, hoTen, sdt, email, namSinh, maNV);
	}
    
    
}
