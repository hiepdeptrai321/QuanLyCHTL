package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.TaiKhoan;

public class TaiKhoan_DAO {
    private Connection conn;

    public TaiKhoan_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO TaiKhoan (maTK, tenDN, matKhau, vaiTro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getMaTK());
            ps.setString(2, tk.getTenDN());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getVaiTro());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(TaiKhoan tk) throws SQLException {
        String sql = "UPDATE TaiKhoan SET tenDN=?, matKhau=?, vaiTro=? WHERE maTK=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDN());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getVaiTro());
            ps.setString(4, tk.getMaTK());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maTK) throws SQLException {
        String sql = "DELETE FROM TaiKhoan WHERE maTK=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTK);
            return ps.executeUpdate() > 0;
        }
    }

    public TaiKhoan getByTK(String tenDN) throws SQLException {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDN=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDN);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setMaTK(rs.getString("maTK"));
                tk.setTenDN(rs.getString("tenDN"));
                tk.setMatKhau(rs.getString("matKhau"));
                tk.setVaiTro(rs.getString("vaiTro"));
                return tk;
            }
            return null;
        }
    }

    public List<TaiKhoan> getAll() throws SQLException {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setMaTK(rs.getString("maTK"));
                tk.setTenDN(rs.getString("tenDN"));
                tk.setMatKhau(rs.getString("matKhau"));
                tk.setVaiTro(rs.getString("vaiTro"));
                list.add(tk);
            }
        }
        return list;
    }
}
