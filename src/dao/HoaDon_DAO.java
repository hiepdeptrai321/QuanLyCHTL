package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.NhanVien;

public class HoaDon_DAO {
    private Connection conn;
    private KhachHang_DAO khachHangDAO; 
    private NhanVien_DAO nhanvienDAO;
    public HoaDon_DAO(Connection conn) {
        this.conn = conn;
        this.khachHangDAO = new KhachHang_DAO(conn); 
        this.nhanvienDAO = new NhanVien_DAO(conn); 
    }

    public boolean insert(HoaDon hd) throws SQLException {
        String sql = "INSERT INTO HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setDate(2, new Date(hd.getNgayLap().getTime())); 
            ps.setInt(3, hd.getQuay());
            ps.setDouble(4, hd.getTongTien());
            ps.setDouble(5, hd.getThanhTien());
            ps.setDouble(6, hd.getTienNhan());
            ps.setDouble(7, hd.getTienThoi());
            ps.setInt(8, hd.getTongSoLuongSP());

            if (hd.getKh() != null) {
                ps.setString(9, hd.getKh().getMa()); 
                ps.setNull(9, Types.NVARCHAR); 
            }

            if (hd.getNv() != null) {
                ps.setString(10, hd.getNv().getMa()); 
                ps.setNull(10, Types.NVARCHAR); 
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(HoaDon hd) throws SQLException {
        String sql = "UPDATE HoaDon SET ngayLap=?, quay=?, tongTien=?, thanhTien=?, tienNhan=?, tienThoi=?, tongSoLuongSP=?, maKH=?, maNV=? WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(hd.getNgayLap().getTime()));
            ps.setInt(2, hd.getQuay());
            ps.setDouble(3, hd.getTongTien());
            ps.setDouble(4, hd.getThanhTien());
            ps.setDouble(5, hd.getTienNhan());
            ps.setDouble(6, hd.getTienThoi());
            ps.setInt(7, hd.getTongSoLuongSP());

            if (hd.getKh() != null) {
                ps.setString(8, hd.getKh().getMa()); 
            } else {
                ps.setNull(8, Types.NVARCHAR);
            }
            if (hd.getNv() != null) {
                ps.setString(9, hd.getNv().getMa()); 
                ps.setNull(9, Types.NVARCHAR); 
            }
            ps.setString(10, hd.getMaHD());
            return ps.executeUpdate() > 0;
        }
    }


    public boolean delete(String maHD) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        }
    }

    public HoaDon getById(String maHD) throws SQLException {
        String sql = "SELECT * FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setQuay(rs.getInt("quay"));
                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setThanhTien(rs.getDouble("thanhTien"));
                hd.setTienNhan(rs.getDouble("tienNhan"));
                hd.setTienThoi(rs.getDouble("tienThoi"));
                hd.setTongSoLuongSP(rs.getInt("tongSoLuongSP"));
                
                String maNV = rs.getString("maNV");
                if (maNV != null && !rs.wasNull()) {
                    NhanVien nv = nhanvienDAO.getById(maNV); 
                    hd.setNv(nv);
                } else {
                    hd.setNv(null);
                }

                String maKH = rs.getString("maKH");
                if (maKH != null && !rs.wasNull()) {
                    KhachHang kh = khachHangDAO.getById(maKH); 
                    hd.setKh(kh);
                } else {
                    hd.setKh(null);
                }
                return hd;
            }
        }
        return null;
    }


    public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setNgayLap(rs.getDate("ngayLap")); 
                hd.setQuay(rs.getInt("quay"));
                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setThanhTien(rs.getDouble("thanhTien"));
                hd.setTienNhan(rs.getDouble("tienNhan"));
                hd.setTienThoi(rs.getDouble("tienThoi"));
                hd.setTongSoLuongSP(rs.getInt("tongSoLuongSP"));
                
                String maNV = rs.getString("maNV");
                if (maNV != null && !rs.wasNull()) {
                    NhanVien nv = nhanvienDAO.getById(maNV); 
                    hd.setNv(nv);
                } else {
                    hd.setNv(null);
                }
                
                String maKH = rs.getString("maKH");
                 if (maKH != null && !rs.wasNull()) {
                    KhachHang kh = khachHangDAO.getById(maKH);
                    hd.setKh(kh);
                } else {
                     hd.setKh(null);
                }
                list.add(hd);
            }
        }
        return list;
    }
    
    public List<HoaDon> getListByIDRegex(String regex) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon where maHD like '%"+ regex +"%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setNgayLap(rs.getDate("ngayLap")); 
                hd.setQuay(rs.getInt("quay"));
                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setThanhTien(rs.getDouble("thanhTien"));
                hd.setTienNhan(rs.getDouble("tienNhan"));
                hd.setTienThoi(rs.getDouble("tienThoi"));
                hd.setTongSoLuongSP(rs.getInt("tongSoLuongSP"));
                
                String maNV = rs.getString("maNV");
                if (maNV != null && !rs.wasNull()) {
                    NhanVien nv = nhanvienDAO.getById(maNV); 
                    hd.setNv(nv);
                } else {
                    hd.setNv(null);
                }
                
                String maKH = rs.getString("maKH");
                 if (maKH != null && !rs.wasNull()) {
                    KhachHang kh = khachHangDAO.getById(maKH);
                    hd.setKh(kh);
                } else {
                     hd.setKh(null);
                }
                list.add(hd);
            }
        }
        return list;
    }
    
}

