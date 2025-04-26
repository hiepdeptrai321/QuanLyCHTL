package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.SanPham;

public class SanPham_DAO {
    private Connection conn;

    public SanPham_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(SanPham sp) throws SQLException {
        String sql = "INSERT INTO SanPham (maSP, tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setDouble(3, sp.getGiaBan());
            ps.setDouble(4, sp.getGiaGoc());
            ps.setString(5, sp.getMaLoai());
            ps.setString(6, sp.getMaNH());
            ps.setString(7, sp.getMaNCC());
            ps.setString(8, sp.getMaNQL());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(SanPham sp) throws SQLException {
        String sql = "UPDATE SanPham SET tenSP=?, giaBan=?, giaGoc=?, maLoai=?, maNH=?, maNCC=?, maNQL=? WHERE maSP=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getGiaBan());
            ps.setDouble(3, sp.getGiaGoc());
            ps.setString(4, sp.getMaLoai());
            ps.setString(5, sp.getMaNH());
            ps.setString(6, sp.getMaNCC());
            ps.setString(7, sp.getMaNQL());
            ps.setString(8, sp.getMaSP());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maSP) throws SQLException {
        String sql = "DELETE FROM SanPham WHERE maSP = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;
        }
    }

    public SanPham getById(String maSP) throws SQLException {
        String sql = "SELECT * FROM SanPham WHERE maSP = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("maSP"));
                sp.setTenSP(rs.getString("tenSP"));
                sp.setGiaBan(rs.getDouble("giaBan"));
                sp.setGiaGoc(rs.getDouble("giaGoc"));
                sp.setMaLoai(rs.getString("maLoai"));
                sp.setMaNH(rs.getString("maNH"));
                sp.setMaNCC(rs.getString("maNCC"));
                sp.setMaNQL(rs.getString("maNQL"));
                return sp;
            }
            return null;
        }
    }

    public List<SanPham> getAll() throws SQLException {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("maSP"));
                sp.setTenSP(rs.getString("tenSP"));
                sp.setGiaBan(rs.getDouble("giaBan"));
                sp.setGiaGoc(rs.getDouble("giaGoc"));
                sp.setMaLoai(rs.getString("maLoai"));
                sp.setMaNH(rs.getString("maNH"));
                sp.setMaNCC(rs.getString("maNCC"));
                sp.setMaNQL(rs.getString("maNQL"));
                list.add(sp);
            }
        }
        return list;
    }
    public double getGiaTriGiamTheoMaSP(String maSP) throws SQLException {
        String sql = "{? = call dbo.func_getGiaTriGiam(?)}";
        double giaTriGiam = 0.0;
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.FLOAT);
            cs.setString(2, maSP);
            cs.execute();
            giaTriGiam = cs.getDouble(1); 

        }
        return giaTriGiam;
    }

}
