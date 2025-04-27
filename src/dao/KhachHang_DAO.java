package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.KhachHang;

public class KhachHang_DAO {
    private Connection conn;

    public KhachHang_DAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insert(KhachHang kh) throws SQLException {
        String sql = "{CALL sp_InsertKhachHang(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, kh.getMa());
            stmt.setDate(2, kh.getNgayDangKy());
            stmt.setInt(3, kh.getDiemTichLuy());
            stmt.setInt(4, kh.getHangThanhVien());
            stmt.setInt(5, kh.getSoLanMuaHang());
            stmt.setString(6, kh.getHoTen());
            stmt.setString(7, kh.getSdt());
            stmt.setString(8, kh.getEmail());
            stmt.setDate(9, kh.getNamSinh());
            stmt.setString(10, kh.getDiaChi());
            stmt.setString(11, kh.getMaNV());

            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }

    public boolean update(KhachHang kh) throws SQLException {
        String sql = "{CALL sp_UpdateKhachHang(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement ps = conn.prepareCall(sql)) {
            ps.setString(1, kh.getMa());
            ps.setDate(2, new java.sql.Date(kh.getNgayDangKy().getTime()));
            ps.setInt(3, kh.getDiemTichLuy());
            ps.setInt(4, kh.getHangThanhVien());
            ps.setInt(5, kh.getSoLanMuaHang());
            ps.setString(6, kh.getHoTen());
            ps.setString(7, kh.getSdt());
            ps.setString(8, kh.getEmail());
            ps.setDate(9, new java.sql.Date(kh.getNamSinh().getTime()));
            ps.setString(10, kh.getDiaChi());
            ps.setString(11, kh.getMaNV());

            return ps.executeUpdate() > 0;
        }
    }

//    public boolean delete(String maKH) throws SQLException {
//        boolean success = false;
//        String sql = "{call SP_DeleteKhachHang(?)}" ; 
//        try (CallableStatement cs = conn.prepareCall(sql)) { 
//            cs.setString(1, maKH);
//            int rowsAffected = cs.executeUpdate(); 
//            success = rowsAffected > 0;
//        }
//        return success;
//    }
    public boolean delete(String maKH) throws SQLException {
        boolean success = false;
        // Bỏ qua kiểm tra hóa đơn và tiến hành xóa khách hàng trực tiếp
        String sql = "{CALL sp_DeleteKhachHang(?)}"; 
        try (CallableStatement cs = conn.prepareCall(sql)) { 
            cs.setString(1, maKH);
            int rowsAffected = cs.executeUpdate(); 
            success = rowsAffected > 0;
        }
        return success;
    }
    //Kiểm tra xem một khách hàng có bất kỳ hóa đơn nào liên quan trong bảng HoaDon hay không
    public boolean hasInvoices(String maKH) throws SQLException {
        String callSql = "{call SP_HasKhachHangInvoices(?)}";
        // Sử dụng try-with-resources để đảm bảo CallableStatement và ResultSet được đóng
        try (CallableStatement cs = conn.prepareCall(callSql)) {
            cs.setString(1, maKH);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1); 
                    return count > 0; 
                }
            }
        }
        throw new SQLException("Lỗi khi gọi Stored Procedure SP_HasKhachHangInvoices cho khách hàng: " + maKH);
    }

    public KhachHang getById(String ma) throws SQLException {
        String sql = "{CALL sp_GetKhachHangById(?)}";
        try (CallableStatement ps = conn.prepareCall(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMa(rs.getString("ma"));
                kh.setNgayDangKy(rs.getDate("ngayDangKy"));
                kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
                kh.setHangThanhVien(rs.getInt("hangThanhVien"));
                kh.setSoLanMuaHang(rs.getInt("soLanMuaHang"));
                kh.setHoTen(rs.getString("hoTen"));
                kh.setSdt(rs.getString("sdt"));
                kh.setEmail(rs.getString("email"));
                kh.setNamSinh(rs.getDate("namSinh"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setMaNV(rs.getString("maNV"));
                return kh;
            }
            return null;
        }
    }

    public List<KhachHang> getAll() throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "{CALL sp_GetAllKhachHang()}";
        try (CallableStatement ps = conn.prepareCall(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMa(rs.getString("ma"));
                kh.setNgayDangKy(rs.getDate("ngayDangKy"));
                kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
                kh.setHangThanhVien(rs.getInt("hangThanhVien"));
                kh.setSoLanMuaHang(rs.getInt("soLanMuaHang"));
                kh.setHoTen(rs.getString("hoTen"));
                kh.setSdt(rs.getString("sdt"));
                kh.setEmail(rs.getString("email"));
                kh.setNamSinh(rs.getDate("namSinh"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setMaNV(rs.getString("maNV"));
                list.add(kh);
            }
        }
        return list;
    }
    public List<KhachHang> searchByName(String keyword) throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        String sql = "{CALL SearchKhachHang(?)}";  
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, keyword);  
            ResultSet rs = cs.executeQuery();  // Thực thi truy vấn và lấy kết quả
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMa(rs.getString("ma"));
                kh.setHoTen(rs.getString("hoTen"));
                kh.setSdt(rs.getString("sdt"));
                kh.setEmail(rs.getString("email"));
                kh.setNamSinh(rs.getDate("namSinh"));
                kh.setDiaChi(rs.getString("diaChi"));
                kh.setNgayDangKy(rs.getDate("ngayDangKy"));
                kh.setDiemTichLuy(rs.getInt("diemTichLuy"));
                kh.setHangThanhVien(rs.getInt("hangThanhVien"));
                kh.setSoLanMuaHang(rs.getInt("soLanMuaHang"));
                kh.setMaNV(rs.getString("maNV"));
                list.add(kh);
            }
        }
        return list;
    }
    public List<String> getDistinctMemberRanks() throws SQLException {
        List<String> memberRanks = new ArrayList<>();
        try (CallableStatement cs = conn.prepareCall("{call SP_GetDistinctMemberRanks()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                String rank = rs.getString(1);
                if (rank != null && !rank.trim().isEmpty()) {
                    memberRanks.add(rank);
                }
            }
        }
        return memberRanks;
    }

    public List<String> getDistinctEmployeeIDs() throws SQLException {
        List<String> employeeIDs = new ArrayList<>();
        try (CallableStatement cs = conn.prepareCall("{call SP_GetDistinctEmployeeIDsCreatingCustomers()}");
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                String empID = rs.getString(1);
                if (empID != null && !empID.trim().isEmpty()) {
                    employeeIDs.add(empID);
                }
            }
        }
        return employeeIDs;
    }

 // Phương thức lấy khách hàng theo hạng thành viên sử dụng SP SP_GetKhachHangByHangTV
    public List<KhachHang> getCustomersByMemberRank(String rank) throws SQLException {
        List<KhachHang> khachHangList = new ArrayList<>();
        try (CallableStatement cs = conn.prepareCall("{call SP_GetKhachHangByHangTV(?)}")) {
            cs.setString(1, rank);  
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = createKhachHangFromResultSet(rs);
                    khachHangList.add(kh);
                }
            }
        }
        return khachHangList;
    }

    public List<KhachHang> getCustomersByEmployee(String maNV) throws SQLException {
        List<KhachHang> khachHangList = new ArrayList<>();
        try (CallableStatement cs = conn.prepareCall("{call SP_GetKhachHangByMaNV(?)}")) {

            // Đặt giá trị cho tham số đầu vào (Mã nhân viên)
            cs.setString(1, maNV); 

            // Thực thi câu truy vấn và lấy kết quả
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    // Tạo đối tượng KhachHang từ kết quả trả về
                    KhachHang kh = createKhachHangFromResultSet(rs);
                    khachHangList.add(kh);
                }
            }
        }
        return khachHangList;
    }

    private KhachHang createKhachHangFromResultSet(ResultSet rs) throws SQLException {
        String maKH = rs.getString("Ma"); 
        String hoTen = rs.getString("HoTen");
        String sdt = rs.getString("Sdt"); 
        String email = rs.getString("Email"); 
        Date namSinh = rs.getDate("NamSinh"); 
        String diaChi = rs.getString("DiaChi"); 
        Date ngayDangKy = rs.getDate("NgayDangKy"); 
        int diemTichLuy = rs.getInt("DiemTichLuy"); 
        int hangThanhVien = rs.getInt("HangThanhVien"); 
        int soLanMuaHang = rs.getInt("SoLanMuaHang"); 
        String maNV = rs.getString("MaNV"); 

        // Tạo đối tượng KhachHang từ các giá trị trên và trả về
        return new KhachHang(maKH, hoTen, sdt, email, namSinh, diaChi, ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, maNV);
    }

}

