package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.KhuyenMai;

public class KhuyenMai_DAO {
    private Connection conn;

    public KhuyenMai_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(KhuyenMai km) throws SQLException {
        String sql = "INSERT INTO KhuyenMai (maKM, giaTriGiam, ngayBatDau, ngayKetThuc, moTa, maSP, maNQL) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, km.getMaKM());
            ps.setFloat(2, km.getGiaTriGiam());
            ps.setDate(3, new java.sql.Date(km.getNgayBatDau().getTime()));
            ps.setDate(4, new java.sql.Date(km.getNgayKetThuc().getTime()));
            ps.setString(5, km.getMoTa());
            ps.setString(6, km.getMaSP());
            ps.setString(7, km.getMaNQL());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(KhuyenMai km) throws SQLException {
        String sql = "UPDATE KhuyenMai SET giaTriGiam=?, ngayBatDau=?, ngayKetThuc=?, moTa=?, maSP=?, maNQL=? WHERE maKM=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, km.getGiaTriGiam());
            ps.setDate(2, new java.sql.Date(km.getNgayBatDau().getTime()));
            ps.setDate(3, new java.sql.Date(km.getNgayKetThuc().getTime()));
            ps.setString(4, km.getMoTa());
            ps.setString(5, km.getMaSP());
            ps.setString(6, km.getMaNQL());
            ps.setString(7, km.getMaKM());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maKM) throws SQLException {
        String sql = "DELETE FROM KhuyenMai WHERE maKM=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;
        }
    }

    public KhuyenMai getById(String maKM) throws SQLException {
        String sql = "SELECT * FROM KhuyenMai WHERE maKM=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setMaKM(rs.getString("maKM"));
                km.setGiaTriGiam(rs.getFloat("giaTriGiam"));
                km.setNgayBatDau(rs.getDate("ngayBatDau"));
                km.setNgayKetThuc(rs.getDate("ngayKetThuc"));
                km.setMoTa(rs.getString("moTa"));
                km.setMaSP(rs.getString("maSP"));
                km.setMaNQL(rs.getString("maNQL"));
                return km;
            }
            return null;
        }
    }

    public List<KhuyenMai> getAll() throws SQLException {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setMaKM(rs.getString("maKM"));
                km.setGiaTriGiam(rs.getFloat("giaTriGiam"));
                km.setNgayBatDau(rs.getDate("ngayBatDau"));
                km.setNgayKetThuc(rs.getDate("ngayKetThuc"));
                km.setMoTa(rs.getString("moTa"));
                km.setMaSP(rs.getString("maSP"));
                km.setMaNQL(rs.getString("maNQL"));
                list.add(km);
            }
        }
        return list;
    }
}
