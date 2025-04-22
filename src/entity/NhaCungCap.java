package entity;

public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String diaChi;
    private String soDienThoai;
    private int xepLoai;
	public NhaCungCap(String maNCC, String tenNCC, String diaChi, String soDienThoai, int xepLoai) {
		super();
		this.maNCC = maNCC;
		this.tenNCC = tenNCC;
		this.diaChi = diaChi;
		this.soDienThoai = soDienThoai;
		this.xepLoai = xepLoai;
	}
	public NhaCungCap() {
		super();
	}
	public String getMaNCC() {
		return maNCC;
	}
	public String getTenNCC() {
		return tenNCC;
	}
	public String getDiaChi() {
		return diaChi;
	}
	public String getSoDienThoai() {
		return soDienThoai;
	}
	public int getXepLoai() {
		return xepLoai;
	}
	public void setMaNCC(String maNCC) {
		this.maNCC = maNCC;
	}
	public void setTenNCC(String tenNCC) {
		this.tenNCC = tenNCC;
	}
	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}
	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	public void setXepLoai(int xepLoai) {
		this.xepLoai = xepLoai;
	}
	@Override
	public String toString() {
		return String.format("NhaCungCap [maNCC=%s, tenNCC=%s, diaChi=%s, soDienThoai=%s, xepLoai=%s]", maNCC, tenNCC,
				diaChi, soDienThoai, xepLoai);
	}
    
    
}
