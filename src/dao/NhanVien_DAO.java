package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {
    private Connection conn;


	public NhanVien_DAO(Connection conn) {
		this.conn = conn;
	}

    public boolean insert(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (ma, ngayVaoLam, luong, caLam, hoTen, SDT, email, namSinh, diaChi, maNQL, maTK) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMa());
            ps.setDate(2, new java.sql.Date(nv.getNgayVaoLam().getTime()));
            ps.setDouble(3, nv.getLuong());
            ps.setDate(4, new java.sql.Date(nv.getCaLam().getTime()));
            ps.setString(5, nv.getHoTen());
            ps.setString(6, nv.getSdt());
            ps.setString(7, nv.getEmail());
            ps.setDate(8, new java.sql.Date(nv.getNamSinh().getTime()));
            ps.setString(9, nv.getDiaChi());
            ps.setString(10, nv.getMaNQL());
            ps.setString(11, nv.getMaTK());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET ngayVaoLam=?, luong=?, caLam=?, hoTen=?, SDT=?, email=?, namSinh=?, diaChi=?, maNQL=?, maTK=? WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(nv.getNgayVaoLam().getTime()));
            ps.setDouble(2, nv.getLuong());
            ps.setDate(3, new java.sql.Date(nv.getCaLam().getTime()));
            ps.setString(4, nv.getHoTen());
            ps.setString(5, nv.getSdt());
            ps.setString(6, nv.getEmail());
            ps.setDate(7, new java.sql.Date(nv.getNamSinh().getTime()));
            ps.setString(8, nv.getDiaChi());
            ps.setString(9, nv.getMaNQL());
            ps.setString(10, nv.getMaTK());
            ps.setString(11, nv.getMa());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String ma) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        }
    }

    public NhanVien getById(String ma) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE ma=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMa(rs.getString("ma"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setLuong(rs.getDouble("luong"));
                nv.setCaLam(rs.getDate("caLam"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("email"));
                nv.setNamSinh(rs.getDate("namSinh"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setMaNQL(rs.getString("maNQL"));
                nv.setMaTK(rs.getString("maTK"));
                return nv;
            }
            return null;
        }
    }
    public NhanVien getByTK(String maTK) throws SQLException {
        String sql = "SELECT * FROM NhanVien WHERE maTK=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTK);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMa(rs.getString("ma"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setLuong(rs.getDouble("luong"));
                nv.setCaLam(rs.getDate("caLam"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("email"));
                nv.setNamSinh(rs.getDate("namSinh"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setMaNQL(rs.getString("maNQL"));
                nv.setMaTK(rs.getString("maTK"));
                return nv;
            }
            return null;
        }
    }

    public List<NhanVien> getAll() throws SQLException {
    	ConnectDB.getInstance();
        conn = ConnectDB.getConnection();
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMa(rs.getString("ma"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setLuong(rs.getDouble("luong"));
                nv.setCaLam(rs.getDate("caLam"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("email"));
                nv.setNamSinh(rs.getDate("namSinh"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setMaNQL(rs.getString("maNQL"));
                nv.setMaTK(rs.getString("maTK"));
                list.add(nv);
            }
        }
        return list;
    }
}
