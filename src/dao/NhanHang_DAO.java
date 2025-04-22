package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.NhanHang;

public class NhanHang_DAO {
    private Connection conn;

    public NhanHang_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(NhanHang nh) throws SQLException {
        String sql = "INSERT INTO NhanHang (maNH, tenNH) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nh.getMaNH());
            ps.setString(2, nh.getTenNH());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(NhanHang nh) throws SQLException {
        String sql = "UPDATE NhanHang SET tenNH=? WHERE maNH=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nh.getTenNH());
            ps.setString(2, nh.getMaNH());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maNH) throws SQLException {
        String sql = "DELETE FROM NhanHang WHERE maNH=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNH);
            return ps.executeUpdate() > 0;
        }
    }

    public NhanHang getById(String maNH) throws SQLException {
        String sql = "SELECT * FROM NhanHang WHERE maNH=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNH);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NhanHang nh = new NhanHang();
                nh.setMaNH(rs.getString("maNH"));
                nh.setTenNH(rs.getString("tenNH"));
                return nh;
            }
            return null;
        }
    }

    public List<NhanHang> getAll() throws SQLException {
        List<NhanHang> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanHang";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanHang nh = new NhanHang();
                nh.setMaNH(rs.getString("maNH"));
                nh.setTenNH(rs.getString("tenNH"));
                list.add(nh);
            }
        }
        return list;
    }
}
