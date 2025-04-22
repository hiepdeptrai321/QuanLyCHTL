package entity;

public class NhanHang {
    private String maNH;
    private String tenNH;
    
	public NhanHang(String maNH, String tenNH) {
		super();
		this.maNH = maNH;
		this.tenNH = tenNH;
	}
	
	public NhanHang() {
		super();
	}
	
	public String getMaNH() {
		return maNH;
	}
	public String getTenNH() {
		return tenNH;
	}
	public void setMaNH(String maNH) {
		this.maNH = maNH;
	}
	public void setTenNH(String tenNH) {
		this.tenNH = tenNH;
	}

	@Override
	public String toString() {
		return String.format("NhanHang [maNH=%s, tenNH=%s]", maNH, tenNH);
	}
    
	
}

