package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.NhanVien;

public class HoaDon_DAO {
    private Connection conn;
    private KhachHang_DAO khachHangDAO; 
    private NhanVien_DAO nhanvienDAO;
    public HoaDon_DAO(Connection conn) {
        this.conn = conn;
        this.khachHangDAO = new KhachHang_DAO(conn); 
        this.nhanvienDAO = new NhanVien_DAO(conn); 
    }

    public boolean insert(HoaDon hd) throws SQLException {
        String sql = "INSERT INTO HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setDate(2, new Date(hd.getNgayLap().getTime())); 
            ps.setInt(3, hd.getQuay());
            ps.setDouble(4, hd.getTongTien());
            ps.setDouble(5, hd.getThanhTien());
            ps.setDouble(6, hd.getTienNhan());
            ps.setDouble(7, hd.getTienThoi());
            ps.setInt(8, hd.getTongSoLuongSP());

            if (hd.getKh() != null) {
                ps.setString(9, hd.getKh().getMa()); 
                ps.setNull(9, Types.NVARCHAR); 
            }

            if (hd.getNv() != null) {
                ps.setString(10, hd.getNv().getMa()); 
                ps.setNull(10, Types.NVARCHAR); 
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(HoaDon hd) throws SQLException {
        String sql = "UPDATE HoaDon SET ngayLap=?, quay=?, tongTien=?, thanhTien=?, tienNhan=?, tienThoi=?, tongSoLuongSP=?, maKH=?, maNV=? WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(hd.getNgayLap().getTime()));
            ps.setInt(2, hd.getQuay());
            ps.setDouble(3, hd.getTongTien());
            ps.setDouble(4, hd.getThanhTien());
            ps.setDouble(5, hd.getTienNhan());
            ps.setDouble(6, hd.getTienThoi());
            ps.setInt(7, hd.getTongSoLuongSP());

            if (hd.getKh() != null) {
                ps.setString(8, hd.getKh().getMa()); 
            } else {
                ps.setNull(8, Types.NVARCHAR);
            }
            if (hd.getNv() != null) {
                ps.setString(9, hd.getNv().getMa()); 
                ps.setNull(9, Types.NVARCHAR); 
            }
            ps.setString(10, hd.getMaHD());
            return ps.executeUpdate() > 0;
        }
    }


    public boolean delete(String maHD) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            return ps.executeUpdate() > 0;
        }
    }

    public HoaDon getById(String maHD) throws SQLException {
        String sql = "SELECT * FROM HoaDon WHERE maHD=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setNgayLap(rs.getDate("ngayLap"));
                hd.setQuay(rs.getInt("quay"));
                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setThanhTien(rs.getDouble("thanhTien"));
                hd.setTienNhan(rs.getDouble("tienNhan"));
                hd.setTienThoi(rs.getDouble("tienThoi"));
                hd.setTongSoLuongSP(rs.getInt("tongSoLuongSP"));
                
                String maNV = rs.getString("maNV");
                if (maNV != null && !rs.wasNull()) {
                    NhanVien nv = nhanvienDAO.getById(maNV); 
                    hd.setNv(nv);
                } else {
                    hd.setNv(null);
                }

                String maKH = rs.getString("maKH");
                if (maKH != null && !rs.wasNull()) {
                    KhachHang kh = khachHangDAO.getById(maKH); 
                    hd.setKh(kh);
                } else {
                    hd.setKh(null);
                }
                return hd;
            }
        }
        return null;
    }


    public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setNgayLap(rs.getDate("ngayLap")); 
                hd.setQuay(rs.getInt("quay"));
                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setThanhTien(rs.getDouble("thanhTien"));
                hd.setTienNhan(rs.getDouble("tienNhan"));
                hd.setTienThoi(rs.getDouble("tienThoi"));
                hd.setTongSoLuongSP(rs.getInt("tongSoLuongSP"));
                
                String maNV = rs.getString("maNV");
                if (maNV != null && !rs.wasNull()) {
                    NhanVien nv = nhanvienDAO.getById(maNV); 
                    hd.setNv(nv);
                } else {
                    hd.setNv(null);
                }
                
                String maKH = rs.getString("maKH");
                 if (maKH != null && !rs.wasNull()) {
                    KhachHang kh = khachHangDAO.getById(maKH);
                    hd.setKh(kh);
                } else {
                     hd.setKh(null);
                }
                list.add(hd);
            }
        }
        return list;
    }
    
    public List<HoaDon> getListByIDRegex(String regex) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT * FROM HoaDon where maHD like '%"+ regex +"%'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHD(rs.getString("maHD"));
                hd.setNgayLap(rs.getDate("ngayLap")); 
                hd.setQuay(rs.getInt("quay"));
                hd.setTongTien(rs.getDouble("tongTien"));
                hd.setThanhTien(rs.getDouble("thanhTien"));
                hd.setTienNhan(rs.getDouble("tienNhan"));
                hd.setTienThoi(rs.getDouble("tienThoi"));
                hd.setTongSoLuongSP(rs.getInt("tongSoLuongSP"));
                
                String maNV = rs.getString("maNV");
                if (maNV != null && !rs.wasNull()) {
                    NhanVien nv = nhanvienDAO.getById(maNV); 
                    hd.setNv(nv);
                } else {
                    hd.setNv(null);
                }
                
                String maKH = rs.getString("maKH");
                 if (maKH != null && !rs.wasNull()) {
                    KhachHang kh = khachHangDAO.getById(maKH);
                    hd.setKh(kh);
                } else {
                     hd.setKh(null);
                }
                list.add(hd);
            }
        }
        return list;
    }
    public boolean insertWithDetails(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) throws SQLException {
        // 1. Kiểm tra đầu vào cơ bản
        if (hoaDon == null || chiTietList == null || chiTietList.isEmpty()) {
            System.err.println("Hóa đơn hoặc danh sách chi tiết không hợp lệ.");
            return false;
        }

        // 2. Định nghĩa câu lệnh SQL
        // Câu lệnh SQL cho HoaDon (giống hoặc tương tự hàm insert đơn lẻ)
        String sqlHoaDon = "INSERT INTO HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // Câu lệnh SQL cho ChiTietHoaDon - ***QUAN TRỌNG: Chỉnh sửa tên cột và thứ tự cho đúng với CSDL của bạn***
        String sqlChiTiet = "INSERT INTO ChiTietHoaDon (maHD, maSP, soLuong, donGia) VALUES (?, ?, ?, ?)";

        PreparedStatement psHoaDon = null;
        PreparedStatement psChiTiet = null;
        boolean success = false;

        try {
            // 3. Bắt đầu Transaction: Tắt auto-commit
            conn.setAutoCommit(false);

            // 4. Thêm Hóa Đơn (Header)
            psHoaDon = conn.prepareStatement(sqlHoaDon);
            psHoaDon.setString(1, hoaDon.getMaHD());
            psHoaDon.setTimestamp(2, new Timestamp(hoaDon.getNgayLap().getTime())); // Lưu cả giờ phút giây
            psHoaDon.setInt(3, hoaDon.getQuay());
            psHoaDon.setDouble(4, hoaDon.getTongTien());    // Tổng tiền hàng gốc (trước KM)
            psHoaDon.setDouble(5, hoaDon.getThanhTien());   // Thành tiền (sau KM)
            psHoaDon.setDouble(6, hoaDon.getTienNhan());    // Tiền khách đưa
            psHoaDon.setDouble(7, hoaDon.getTienThoi());    // Tiền thối
            psHoaDon.setInt(8, hoaDon.getTongSoLuongSP()); // Tổng số lượng SP trong HĐ

            // Xử lý khóa ngoại KhachHang (có thể null)
            if (hoaDon.getKh() != null) {
                psHoaDon.setString(9, hoaDon.getKh().getMa()); // Lấy mã KH
            } else {
                psHoaDon.setNull(9, Types.NVARCHAR); // Hoặc Types.VARCHAR tùy CSDL
            }
            // Xử lý khóa ngoại NhanVien (có thể null)
            if (hoaDon.getNv() != null) {
                 psHoaDon.setString(10, hoaDon.getNv().getMa()); // Lấy mã NV
            } else {
                 psHoaDon.setNull(10, Types.NVARCHAR); // Hoặc Types.VARCHAR
            }

            int hoaDonAffectedRows = psHoaDon.executeUpdate();
            if (hoaDonAffectedRows == 0) {
                // Nếu không thêm được Hóa đơn -> rollback và báo lỗi
                conn.rollback(); // Hoàn tác transaction
                System.err.println("Thêm Hóa Đơn thất bại, không có dòng nào bị ảnh hưởng.");
                return false; // Hoặc ném Exception
            }

            // 5. Thêm Chi Tiết Hóa Đơn (Details) - Sử dụng Batch Update cho hiệu quả
            psChiTiet = conn.prepareStatement(sqlChiTiet);
            for (ChiTietHoaDon cthd : chiTietList) {
                // *** Đảm bảo thứ tự tham số (?) khớp với câu lệnh sqlChiTiet ***
                psChiTiet.setString(1, hoaDon.getMaHD()); // Khóa ngoại liên kết đến Hóa đơn vừa tạo
                psChiTiet.setString(2, cthd.getMaSP()); // Khóa ngoại đến SanPham
                psChiTiet.setInt(3, cthd.getSoLuong());
                psChiTiet.setDouble(4, cthd.getDonGia()); // Đơn giá tại thời điểm bán

                psChiTiet.addBatch(); // Thêm câu lệnh vào batch
            }

            // Thực thi tất cả các lệnh trong batch
            int[] chiTietAffectedRows = psChiTiet.executeBatch();

            // Kiểm tra kết quả batch (tùy chọn, nhưng nên làm)
            for (int result : chiTietAffectedRows) {
                 if (result == PreparedStatement.EXECUTE_FAILED) {
                     conn.rollback(); // Hoàn tác nếu có lỗi trong batch
                     System.err.println("Thêm Chi Tiết Hóa Đơn thất bại trong batch.");
                     return false; // Hoặc ném Exception
                 }
                 if (result == PreparedStatement.SUCCESS_NO_INFO) {
                     // Có thể thành công nhưng driver không cung cấp số dòng ảnh hưởng
                     // Thường vẫn ổn, nhưng cần lưu ý
                 }
            }


            // 6. Nếu tất cả thành công -> Commit Transaction
            conn.commit();
            success = true;
            System.out.println("Thêm hóa đơn và chi tiết thành công (Transaction committed).");

        } catch (SQLException e) {
            // 7. Nếu có lỗi ở bất kỳ đâu -> Rollback Transaction
            System.err.println("Transaction bị lỗi, đang thực hiện rollback...");
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction đã được rollback.");
                } catch (SQLException excep) {
                    System.err.println("Rollback thất bại: " + excep.getMessage());
                    // Ném lỗi nghiêm trọng hơn nếu rollback cũng thất bại
                    throw new SQLException("Lỗi trong transaction VÀ rollback cũng thất bại.", excep);
                }
            }
             // Ném lại lỗi gốc để lớp gọi có thể xử lý
            throw e;

        } finally {
            // 8. Dọn dẹp tài nguyên và trả lại trạng thái auto-commit
            if (psHoaDon != null) {
                try {
                    psHoaDon.close();
                } catch (SQLException e) { /* Bỏ qua lỗi khi đóng */ }
            }
            if (psChiTiet != null) {
                try {
                    psChiTiet.close();
                } catch (SQLException e) { /* Bỏ qua lỗi khi đóng */ }
            }
            if (conn != null) {
                try {
                    // Luôn trả lại trạng thái auto-commit=true cho connection
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Không thể reset auto-commit: " + e.getMessage());
                }
            }
        }

        return success; // Trả về true nếu commit thành công
    }
}

