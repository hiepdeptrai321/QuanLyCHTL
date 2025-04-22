package entity;

public class LoaiSanPham {
    private String maLoai;
    private String tenLoai;
    
	public LoaiSanPham(String maLoai, String tenLoai) {
		super();
		this.maLoai = maLoai;
		this.tenLoai = tenLoai;
	}

	public LoaiSanPham() {
		super();
	}

	public String getMaLoai() {
		return maLoai;
	}

	public String getTenLoai() {
		return tenLoai;
	}

	public void setMaLoai(String maLoai) {
		this.maLoai = maLoai;
	}

	public void setTenLoai(String tenLoai) {
		this.tenLoai = tenLoai;
	}
    
	@Override
	public String toString() {
		return String.format("LoaiSanPham [maLoai=%s, tenLoai=%s]", maLoai, tenLoai);
	}
}

