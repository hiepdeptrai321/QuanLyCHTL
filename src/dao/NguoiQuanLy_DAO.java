package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.NguoiQuanLy;

public class NguoiQuanLy_DAO {
    private Connection conn;

    public NguoiQuanLy_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(NguoiQuanLy nql) throws SQLException {
        String sql = "INSERT INTO NguoiQuanLy (ma, capBac, phuCap, hoTen, sdt, email, namSinh, diaChi, maTK) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nql.getMa());
            ps.setInt(2, nql.getCapBac());
            ps.setDouble(3, nql.getPhuCap());
            ps.setString(4, nql.getHoTen());
            ps.setString(5, nql.getSdt());
            ps.setString(6, nql.getEmail());
            ps.setDate(7, new java.sql.Date(nql.getNamSinh().getTime()));
            ps.setString(8, nql.getDiaChi());
            ps.setString(9, nql.getMaTK());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(NguoiQuanLy nql) throws SQLException {
        String sql = "UPDATE NguoiQuanLy SET capBac=?, phuCap=?, hoTen=?, sdt=?, email=?, namSinh=?, diaChi=?, maTK=? WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nql.getCapBac());
            ps.setDouble(2, nql.getPhuCap());
            ps.setString(3, nql.getHoTen());
            ps.setString(4, nql.getSdt());
            ps.setString(5, nql.getEmail());
            ps.setDate(6, new java.sql.Date(nql.getNamSinh().getTime()));
            ps.setString(7, nql.getDiaChi());
            ps.setString(8, nql.getMaTK());
            ps.setString(9, nql.getMa());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String ma) throws SQLException {
        String sql = "DELETE FROM NguoiQuanLy WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        }
    }

    public NguoiQuanLy getById(String ma) throws SQLException {
        String sql = "SELECT * FROM NguoiQuanLy WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NguoiQuanLy nql = new NguoiQuanLy();
                nql.setMa(rs.getString("ma"));
                nql.setCapBac(rs.getInt("capBac"));
                nql.setPhuCap(rs.getDouble("phuCap"));
                nql.setHoTen(rs.getString("hoTen"));
                nql.setSdt(rs.getString("sdt"));
                nql.setEmail(rs.getString("email"));
                nql.setNamSinh(rs.getDate("namSinh"));
                nql.setDiaChi(rs.getString("diaChi"));
                nql.setMaTK(rs.getString("maTK"));
                return nql;
            }
            return null;
        }
    }

    public List<NguoiQuanLy> getAll() throws SQLException {
        List<NguoiQuanLy> list = new ArrayList<>();
        String sql = "SELECT * FROM NguoiQuanLy";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NguoiQuanLy nql = new NguoiQuanLy();
                nql.setMa(rs.getString("ma"));
                nql.setCapBac(rs.getInt("capBac"));
                nql.setPhuCap(rs.getDouble("phuCap"));
                nql.setHoTen(rs.getString("hoTen"));
                nql.setSdt(rs.getString("sdt"));
                nql.setEmail(rs.getString("email"));
                nql.setNamSinh(rs.getDate("namSinh"));
                nql.setDiaChi(rs.getString("diaChi"));
                nql.setMaTK(rs.getString("maTK"));
                list.add(nql);
            }
        }
        return list;
    }
}
