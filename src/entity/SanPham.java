package entity;

public class SanPham {
    private String maSP;
    private String tenSP;
    private double giaBan;
    private double giaGoc;
    private String maLoai;
    private String maNH;
    private String maNCC;
    private String maNQL;
    
	public SanPham(String maSP, String tenSP, double giaBan, double giaGoc, String maLoai, String maNH, String maNCC,
			String maNQL) {
		super();
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.giaBan = giaBan;
		this.giaGoc = giaGoc;
		this.maLoai = maLoai;
		this.maNH = maNH;
		this.maNCC = maNCC;
		this.maNQL = maNQL;
	}

	public SanPham() {
		super();
	}

	public String getMaSP() {
		return maSP;
	}

	public String getTenSP() {
		return tenSP;
	}

	public double getGiaBan() {
		return giaBan;
	}

	public double getGiaGoc() {
		return giaGoc;
	}

	public String getMaLoai() {
		return maLoai;
	}

	public String getMaNH() {
		return maNH;
	}

	public String getMaNCC() {
		return maNCC;
	}

	public String getMaNQL() {
		return maNQL;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}

	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
	}

	public void setGiaGoc(double giaGoc) {
		this.giaGoc = giaGoc;
	}

	public void setMaLoai(String maLoai) {
		this.maLoai = maLoai;
	}

	public void setMaNH(String maNH) {
		this.maNH = maNH;
	}

	public void setMaNCC(String maNCC) {
		this.maNCC = maNCC;
	}

	public void setMaNQL(String maNQL) {
		this.maNQL = maNQL;
	}

	@Override
	public String toString() {
		return String.format(
				"SanPham [maSP=%s, tenSP=%s, giaBan=%s, giaGoc=%s, maLoai=%s, maNH=%s, maNCC=%s, maNQL=%s]", maSP,
				tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL);
	}
    
	
}

