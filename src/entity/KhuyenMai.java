package entity;

import java.sql.Date;

public class KhuyenMai {
    private String maKM;
    private float giaTriGiam;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private String moTa;
    private String maSP;
    private String maNQL;
    
	public KhuyenMai(String maKM, float giaTriGiam, Date ngayBatDau, Date ngayKetThuc, String moTa, String maSP,
			String maNQL) {
		super();
		this.maKM = maKM;
		this.giaTriGiam = giaTriGiam;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
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
    
	@Override
	public String toString() {
		return String.format(
				"KhuyenMai [maKM=%s, giaTriGiam=%s, ngayBatDau=%s, ngayKetThuc=%s, moTa=%s, maSP=%s, maNQL=%s]", maKM,
				giaTriGiam, ngayBatDau, ngayKetThuc, moTa, maSP, maNQL);
	}
}

