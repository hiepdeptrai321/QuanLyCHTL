package entity;

public class TaiKhoan {
    private String maTK;
    private String tenDN;
    private String matKhau;
    private String vaiTro;
    
	public TaiKhoan(String maTK, String tenDN, String matKhau, String vaiTro) {
		super();
		this.maTK = maTK;
		this.tenDN = tenDN;
		this.matKhau = matKhau;
		this.vaiTro = vaiTro;
	}
	
	public TaiKhoan() {
		super();
	}
	
	public String getMaTK() {
		return maTK;
	}
	public String getTenDN() {
		return tenDN;
	}
	public String getMatKhau() {
		return matKhau;
	}
	public String getVaiTro() {
		return vaiTro;
	}
	public void setMaTK(String maTK) {
		this.maTK = maTK;
	}
	public void setTenDN(String tenDN) {
		this.tenDN = tenDN;
	}
	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}
	public void setVaiTro(String vaiTro) {
		this.vaiTro = vaiTro;
	}

	@Override
	public String toString() {
		return String.format("TaiKhoan [maTK=%s, tenDN=%s, matKhau=%s, vaiTro=%s]", maTK, tenDN, matKhau, vaiTro);
	}
    
    
}

