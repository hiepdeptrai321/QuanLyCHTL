package entity;

import java.sql.Date;

public class NguoiQuanLy extends NhanVien{
    private String capBac;
    private double phuCap;
    private String maTK;
	public NguoiQuanLy(String ma, String hoTen, String sdt, String email, Date namSinh, String diaChi, String capBac,
			double phuCap, String maTK) {
		super(ma, hoTen, sdt, email, namSinh, diaChi, namSinh, phuCap, namSinh, maTK, maTK);
		this.capBac = capBac;
		this.phuCap = phuCap;
		this.maTK = maTK;
	}
	public NguoiQuanLy() {
		super();
	}
	public String getCapBac() {
		return capBac;
	}
	public double getPhuCap() {
		return phuCap;
	}
	public String getMaTK() {
		return maTK;
	}
	public void setCapBac(String capBac) {
		this.capBac = capBac;
	}
	public void setPhuCap(double phuCap) {
		this.phuCap = phuCap;
	}
	public void setMaTK(String maTK) {
		this.maTK = maTK;
	}
	@Override
	public String toString() {
		return String.format(
				"NguoiQuanLy [capBac=%s, phuCap=%s, maTK=%s, getMa()=%s, getHoTen()=%s, getSdt()=%s, getEmail()=%s, getNamSinh()=%s, getDiaChi()=%s, toString()=%s, getClass()=%s, hashCode()=%s]",
				capBac, phuCap, maTK, getMa(), getHoTen(), getSdt(), getEmail(), getNamSinh(), getDiaChi(),
				super.toString(), getClass(), hashCode());
	}
    
    
}
