package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.KhachHang;

public class KhachHang_DAO {
    private Connection conn;

    public KhachHang_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(KhachHang kh) throws SQLException {
        String sql = "INSERT INTO KhachHang (ma, ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, hoTen, sdt, email, namSinh, maNV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getMa());
            ps.setDate(2, new java.sql.Date(kh.getNgayDangKy().getTime()));
            ps.setInt(3, kh.getDiemTichLuy());
            ps.setInt(4, kh.getHangThanhVien());
            ps.setInt(5, kh.getSoLanMuaHang());
            ps.setString(6, kh.getHoTen());
            ps.setString(7, kh.getSdt());
            ps.setString(8, kh.getEmail());
            ps.setDate(9, new java.sql.Date(kh.getNamSinh().getTime()));
            ps.setString(10, kh.getMaNV());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(KhachHang kh) throws SQLException {
        String sql = "UPDATE KhachHang SET ngayDangKy=?, diemTichLuy=?, hangThanhVien=?, soLanMuaHang=?, hoTen=?, sdt=?, email=?, namSinh=?, maNV=? WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(kh.getNgayDangKy().getTime()));
            ps.setInt(2, kh.getDiemTichLuy());
            ps.setInt(3, kh.getHangThanhVien());
            ps.setInt(4, kh.getSoLanMuaHang());
            ps.setString(5, kh.getHoTen());
            ps.setString(6, kh.getSdt());
            ps.setString(7, kh.getEmail());
            ps.setDate(8, new java.sql.Date(kh.getNamSinh().getTime()));
            ps.setString(9, kh.getMaNV());
            ps.setString(10, kh.getMa());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String ma) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        }
    }

    public KhachHang getById(String ma) throws SQLException {
        String sql = "SELECT * FROM KhachHang WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMa(rs.getString("ma"));
                kh.setNgayDangKy(rs.getDate("ngayDangKy"));
                kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
                kh.setHangThanhVien(rs.getInt("hangThanhVien"));
                kh.setSoLanMuaHang(rs.getInt("soLanMuaHang"));
                kh.setHoTen(rs.getString("hoTen"));
                kh.setSdt(rs.getString("sdt"));
                kh.setEmail(rs.getString("email"));
                kh.setNamSinh(rs.getDate("namSinh"));
                kh.setMaNV(rs.getString("maNV"));
                return kh;
            }
            return null;
        }
    }

    public List<KhachHang> getAll() throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMa(rs.getString("ma"));
                kh.setNgayDangKy(rs.getDate("ngayDangKy"));
                kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
                kh.setHangThanhVien(rs.getInt("hangThanhVien"));
                kh.setSoLanMuaHang(rs.getInt("soLanMuaHang"));
                kh.setHoTen(rs.getString("hoTen"));
                kh.setSdt(rs.getString("sdt"));
                kh.setEmail(rs.getString("email"));
                kh.setNamSinh(rs.getDate("namSinh"));
                kh.setMaNV(rs.getString("maNV"));
                list.add(kh);
            }
        }
        return list;
    }
}

