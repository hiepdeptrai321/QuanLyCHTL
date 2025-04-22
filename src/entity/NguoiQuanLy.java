package entity;

import java.sql.Date;

public class NguoiQuanLy {
    private String ma;
    private int capBac;
    private double phuCap;
    private String hoTen;
    private String sdt;
    private String email;
    private Date namSinh;
    private String diaChi;
    private String maTK;
    
	public NguoiQuanLy(String ma, int capBac, double phuCap, String hoTen, String sdt, String email, Date namSinh,
			String diaChi, String maTK) {
		super();
		this.ma = ma;
		this.capBac = capBac;
		this.phuCap = phuCap;
		this.hoTen = hoTen;
		this.sdt = sdt;
		this.email = email;
		this.namSinh = namSinh;
		this.diaChi = diaChi;
		this.maTK = maTK;
	}

	public NguoiQuanLy() {
		super();
	}

	public String getMa() {
		return ma;
	}

	public int getCapBac() {
		return capBac;
	}

	public double getPhuCap() {
		return phuCap;
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

	public String getMaTK() {
		return maTK;
	}

	public void setMa(String ma) {
		this.ma = ma;
	}

	public void setCapBac(int capBac) {
		this.capBac = capBac;
	}

	public void setPhuCap(double phuCap) {
		this.phuCap = phuCap;
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

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public void setMaTK(String maTK) {
		this.maTK = maTK;
	}

	@Override
	public String toString() {
		return String.format(
				"NguoiQuanLy [ma=%s, capBac=%s, phuCap=%s, hoTen=%s, sdt=%s, email=%s, namSinh=%s, diaChi=%s, maTK=%s]",
				ma, capBac, phuCap, hoTen, sdt, email, namSinh, diaChi, maTK);
	}
    
    
}
