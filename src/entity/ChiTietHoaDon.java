package entity;

public class ChiTietHoaDon {
    private int soLuong;
    private double donGia;
    private String maHD;
    private String maSP;
    private String maKM;
    
    public ChiTietHoaDon(int soLuong, double donGia, String maHD, String maSP, String maKM) {
		super();
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.maHD = maHD;
		this.maSP = maSP;
		this.maKM = maKM;
	}

	public ChiTietHoaDon() {
		super();
	}

	public int getSoLuong() {
		return soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public String getMaHD() {
		return maHD;
	}

	public String getMaSP() {
		return maSP;
	}

	public String getMaKM() {
		return maKM;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	@Override
	public String toString() {
		return String.format("ChiTietHoaDon [soLuong=%s, donGia=%s, maHD=%s, maSP=%s, maKM=%s]", soLuong, donGia, maHD,
				maSP, maKM);
	}
	
	
}

