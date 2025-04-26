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
        String sql = "{call sp_InsertTaiKhoan(?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, tk.getMaTK());
            cs.setString(2, tk.getTenDN());
            cs.setString(3, tk.getMatKhau());
            cs.setString(4, tk.getVaiTro());
            return cs.executeUpdate() > 0;
        }
    }

    public boolean update(TaiKhoan tk) throws SQLException {
        String sql = "{call sp_UpdateTaiKhoan(?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, tk.getMaTK());
            cs.setString(2, tk.getTenDN());
            cs.setString(3, tk.getMatKhau());
            cs.setString(4, tk.getVaiTro());
            return cs.executeUpdate() > 0;
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
