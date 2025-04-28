package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {
    private Connection conn;


	public NhanVien_DAO(Connection conn) {
		this.conn = conn;
	}

	public boolean insert(NhanVien nv) throws SQLException {
		if (KiemMaDaTonTaiChua(nv.getMa())) {
			JOptionPane.showMessageDialog(null, "Mã nhân viên đã tồn tại!");
	        return false;
	    }
	    String sql = "{call sp_InsertNhanVien(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	    try (CallableStatement cs = conn.prepareCall(sql)) {
	        cs.setString(1, nv.getMa());
	        cs.setDate(2, new java.sql.Date(nv.getNgayVaoLam().getTime()));
	        cs.setDouble(3, nv.getLuong());
	        cs.setString(4, nv.getCaLam());
	        cs.setString(5, nv.getHoTen());
	        cs.setString(6, nv.getSdt());
	        cs.setString(7, nv.getEmail());
	        cs.setDate(8, new java.sql.Date(nv.getNamSinh().getTime()));
	        cs.setString(9, nv.getDiaChi());
	        cs.setString(10, nv.getMaNQL());
	        cs.setString(11, nv.getMaTK());
	        return cs.executeUpdate() > 0;
	    }
	}
	
	public boolean KiemMaDaTonTaiChua(String ma) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM NhanVien WHERE ma = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, ma);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) > 0;
	            }
	        }
	    }
	    return false;
	}

	public boolean update(NhanVien nv) throws SQLException {
	    String sql = "{call sp_UpdateNhanVien(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	    try (CallableStatement cs = conn.prepareCall(sql)) {
	        cs.setDate(1, new java.sql.Date(nv.getNgayVaoLam().getTime()));
	        cs.setDouble(2, nv.getLuong());
	        cs.setString(3, nv.getCaLam());
	        cs.setString(4, nv.getHoTen());
	        cs.setString(5, nv.getSdt());
	        cs.setString(6, nv.getEmail());
	        cs.setDate(7, new java.sql.Date(nv.getNamSinh().getTime()));
	        cs.setString(8, nv.getDiaChi());
	        cs.setString(9, nv.getMaNQL());
	        cs.setString(10, nv.getMaTK());
	        cs.setString(11, nv.getMa());
	        return cs.executeUpdate() > 0;
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
        String sql = "{call sp_GetNhanVienById(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, ma);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMa(rs.getString("ma"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setLuong(rs.getDouble("luong"));
                nv.setCaLam(rs.getString("caLam"));
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
    
    public NhanVien getTKbangMaTK(String maTK) throws SQLException {
        String sql = "{call sp_GetNhanVienByTK(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maTK);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMa(rs.getString("ma"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setLuong(rs.getDouble("luong"));
                nv.setCaLam(rs.getString("caLam"));
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
                nv.setCaLam(rs.getString("caLam"));
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
    
    public List<NhanVien> findNhanVien(String keyword) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "{call sp_FindNhanVien(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, keyword);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMa(rs.getString("ma"));
                nv.setHoTen(rs.getString("hoTen"));
                nv.setSdt(rs.getString("sdt"));
                nv.setEmail(rs.getString("email"));
                nv.setNamSinh(rs.getDate("namSinh"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setNgayVaoLam(rs.getDate("ngayVaoLam"));
                nv.setLuong(rs.getDouble("luong"));
                nv.setCaLam(rs.getString("caLam"));
                nv.setMaNQL(rs.getString("maNQL"));
                list.add(nv);
            }
        }
        return list;
    }
}
