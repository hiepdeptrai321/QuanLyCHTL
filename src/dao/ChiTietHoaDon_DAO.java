package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.ChiTietHoaDon;

public class ChiTietHoaDon_DAO {
    private Connection conn;

    public ChiTietHoaDon_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(ChiTietHoaDon ct) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maSP, maKM, soLuong, donGia) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getMaHD());
            ps.setString(2, ct.getMaSP());
            ps.setString(3, ct.getMaKM());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getDonGia());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(ChiTietHoaDon ct) throws SQLException {
        String sql = "UPDATE ChiTietHoaDon SET soLuong=?, donGia=?, maKM=? WHERE maHD=? AND maSP=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ct.getSoLuong());
            ps.setDouble(2, ct.getDonGia());
            ps.setString(3, ct.getMaKM());
            ps.setString(4, ct.getMaHD());
            ps.setString(5, ct.getMaSP());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maHD, String maSP) throws SQLException {
        String sql = "DELETE FROM ChiTietHoaDon WHERE maHD=? AND maSP=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        }
    }

    public ChiTietHoaDon getById(String maHD, String maSP) throws SQLException {
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHD=? AND maSP=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ps.setString(2, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHD(rs.getString("maHD"));
                ct.setMaSP(rs.getString("maSP"));
                ct.setMaKM(rs.getString("maKM"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                return ct;
            }
        }
        return null;
    }

    public List<ChiTietHoaDon> getAll() throws SQLException {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHD(rs.getString("maHD"));
                ct.setMaSP(rs.getString("maSP"));
                ct.setMaKM(rs.getString("maKM"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                list.add(ct);
            }
        }
        return list;
    }

    public List<ChiTietHoaDon> getByMaHD(String maHD) throws SQLException {
        List<ChiTietHoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietHoaDon WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setMaHD(rs.getString("maHD"));
                ct.setMaSP(rs.getString("maSP"));
                ct.setMaKM(rs.getString("maKM"));
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                list.add(ct);
            }
        }
        return list;
    }
}

