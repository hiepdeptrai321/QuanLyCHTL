package entity;

import java.util.Date;

public class KhuyenMai {
    private String maKM;
    private String tenKM;
    private float giaTriGiam;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String moTa;
    private String maSP;
    private String maNQL;
    
	public KhuyenMai(String maKM, String tenKM, float giaTriGiam, Date ngayBD, Date ngayKT, String moTa, String maSP,
			String maNQL) {
		super();
		this.maKM = maKM;
		this.tenKM = tenKM;
		this.giaTriGiam = giaTriGiam;
		this.ngayBatDau = ngayBD;
		this.ngayKetThuc = ngayKT;
		this.moTa = moTa;
		this.maSP = maSP;
		this.maNQL = maNQL;
	}

	public KhuyenMai() {
		super();
	}

	public String getMaKM() {
		return maKM;
	}

	public float getGiaTriGiam() {
		return giaTriGiam;
	}

	public Date getNgayBatDau() {
		return ngayBatDau;
	}

	public Date getNgayKetThuc() {
		return ngayKetThuc;
	}

	public String getMoTa() {
		return moTa;
	}

	public String getMaSP() {
		return maSP;
	}

	public String getMaNQL() {
		return maNQL;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public void setGiaTriGiam(float giaTriGiam) {
		this.giaTriGiam = giaTriGiam;
	}

	public void setNgayBatDau(Date ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public void setNgayKetThuc(Date ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public void setMaNQL(String maNQL) {
		this.maNQL = maNQL;
	}
    
	public String getTenKM() {
		return tenKM;
	}

	public void setTenKM(String tenKM) {
		this.tenKM = tenKM;
	}

	@Override
	public String toString() {
		return String.format(
				"KhuyenMai [maKM=%s, tenKM=%s,, giaTriGiam=%s, ngayBatDau=%s, ngayKetThuc=%s, moTa=%s, maSP=%s, maNQL=%s]", maKM, tenKM,
				giaTriGiam, ngayBatDau, ngayKetThuc, moTa, maSP, maNQL);
	}
}

