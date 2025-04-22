package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.NhaCungCap;

public class NhaCungCap_DAO {
    private Connection conn;

    public NhaCungCap_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(NhaCungCap ncc) throws SQLException {
        String sql = "INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, soDienThoai, xepLoai) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getMaNCC());
            ps.setString(2, ncc.getTenNCC());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getSoDienThoai());
            ps.setInt(5, ncc.getXepLoai());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(NhaCungCap ncc) throws SQLException {
        String sql = "UPDATE NhaCungCap SET tenNCC=?, diaChi=?, soDienThoai=?, xepLoai=? WHERE maNCC=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNCC());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSoDienThoai());
            ps.setInt(4, ncc.getXepLoai());
            ps.setString(5, ncc.getMaNCC());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maNCC) throws SQLException {
        String sql = "DELETE FROM NhaCungCap WHERE maNCC=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;
        }
    }

    public NhaCungCap getById(String maNCC) throws SQLException {
        String sql = "SELECT * FROM NhaCungCap WHERE maNCC=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("maNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setDiaChi(rs.getString("diaChi"));
                ncc.setSoDienThoai(rs.getString("soDienThoai"));
                ncc.setXepLoai(rs.getInt("xepLoai"));
                return ncc;
            }
            return null;
        }
    }

    public List<NhaCungCap> getAll() throws SQLException {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM NhaCungCap";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("maNCC"));
                ncc.setTenNCC(rs.getString("tenNCC"));
                ncc.setDiaChi(rs.getString("diaChi"));
                ncc.setSoDienThoai(rs.getString("soDienThoai"));
                ncc.setXepLoai(rs.getInt("xepLoai"));
                list.add(ncc);
            }
        }
        return list;
    }
}
