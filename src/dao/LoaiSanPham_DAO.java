package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.LoaiSanPham;

public class LoaiSanPham_DAO {
    private Connection conn;

    public LoaiSanPham_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(LoaiSanPham loai) throws SQLException {
        String sql = "INSERT INTO LoaiSanPham (maLoai, tenLoai) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loai.getMaLoai());
            ps.setString(2, loai.getTenLoai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(LoaiSanPham loai) throws SQLException {
        String sql = "UPDATE LoaiSanPham SET tenLoai=? WHERE maLoai=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loai.getTenLoai());
            ps.setString(2, loai.getMaLoai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maLoai) throws SQLException {
        String sql = "DELETE FROM LoaiSanPham WHERE maLoai=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLoai);
            return ps.executeUpdate() > 0;
        }
    }

    public LoaiSanPham getById(String maLoai) throws SQLException {
        String sql = "SELECT * FROM LoaiSanPham WHERE maLoai=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maLoai);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                LoaiSanPham loai = new LoaiSanPham();
                loai.setMaLoai(rs.getString("maLoai"));
                loai.setTenLoai(rs.getString("tenLoai"));
                return loai;
            }
            return null;
        }
    }

    public List<LoaiSanPham> getAll() throws SQLException {
        List<LoaiSanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM LoaiSanPham";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LoaiSanPham loai = new LoaiSanPham();
                loai.setMaLoai(rs.getString("maLoai"));
                loai.setTenLoai(rs.getString("tenLoai"));
                list.add(loai);
            }
        }
        return list;
    }
}
