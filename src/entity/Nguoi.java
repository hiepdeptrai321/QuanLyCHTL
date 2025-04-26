package entity;

import java.util.Date;

public class Nguoi {
    private String ma;
	private String hoTen;
    private String sdt;
    private String email;
    private Date namSinh;
    private String diaChi;
	public Nguoi(String ma, String hoTen, String sdt, String email, Date namSinh, String diaChi) {
		super();
		this.ma = ma;
		this.hoTen = hoTen;
		this.sdt = sdt;
		this.email = email;
		this.namSinh = namSinh;
		this.diaChi = diaChi;
	}
	public Nguoi() {
		super();
	}
	public String getMa() {
		return ma;
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
	public String getDiaChi() {
		return diaChi;
	}
	public void setMa(String ma) {
		this.ma = ma;
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
	public void setNamSinh(Date date) {
		this.namSinh = date;
	}
	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}
	@Override
	public String toString() {
		return String.format("Nguoi [ma=%s, hoTen=%s, sdt=%s, email=%s, namSinh=%s, diaChi=%s]", ma, hoTen, sdt, email,
				namSinh, diaChi);
	}
    
}
