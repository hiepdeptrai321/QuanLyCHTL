package entity;

import java.sql.Date;

public class HoaDon {
    private String maHD;
    private Date ngayLap;
    private int quay;
    private double tongTien;
    private double thanhTien;
    private double tienNhan;
    private double tienThoi;
    private int tongSoLuongSP;
    private KhachHang kh;
    private NhanVien nv;
    
	public HoaDon(String maHD, Date ngayLap, int quay, double tongTien, double thanhTien, double tienNhan,
			double tienThoi, int tongSoLuongSP, KhachHang kh, NhanVien nv) {
		super();
		this.maHD = maHD;
		this.ngayLap = ngayLap;
		this.quay = quay;
		this.tongTien = tongTien;
		this.thanhTien = thanhTien;
		this.tienNhan = tienNhan;
		this.tienThoi = tienThoi;
		this.tongSoLuongSP = tongSoLuongSP;
		this.kh = kh;
		this.nv = nv;
	}

	public HoaDon() {
		super();
	}

	public String getMaHD() {
		return maHD;
	}

	public Date getNgayLap() {
		return ngayLap;
	}

	public int getQuay() {
		return quay;
	}

	public double getTongTien() {
		return tongTien;
	}

	public double getThanhTien() {
		return thanhTien;
	}

	public double getTienNhan() {
		return tienNhan;
	}

	public double getTienThoi() {
		return tienThoi;
	}

	public int getTongSoLuongSP() {
		return tongSoLuongSP;
	}

	public KhachHang getKh() {
		return kh;
	}

	public NhanVien getNv() {
		return nv;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public void setNgayLap(Date ngayLap) {
		this.ngayLap = ngayLap;
	}

	public void setQuay(int quay) {
		this.quay = quay;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public void setThanhTien(double thanhTien) {
		this.thanhTien = thanhTien;
	}

	public void setTienNhan(double tienNhan) {
		this.tienNhan = tienNhan;
	}

	public void setTienThoi(double tienThoi) {
		this.tienThoi = tienThoi;
	}

	public void setTongSoLuongSP(int tongSoLuongSP) {
		this.tongSoLuongSP = tongSoLuongSP;
	}

	public void setKh(KhachHang kh) {
		this.kh = kh;
	}

	public void setNv(NhanVien nv) {
		this.nv = nv;
	}

	@Override
	public String toString() {
		return String.format(
				"HoaDon [maHD=%s, ngayLap=%s, quay=%s, tongTien=%s, thanhTien=%s, tienNhan=%s, tienThoi=%s, tongSoLuongSP=%s, maKH=%s, maNV=%s]",
				maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, kh.getMa(), nv.getMa());
	}
    
    
}

