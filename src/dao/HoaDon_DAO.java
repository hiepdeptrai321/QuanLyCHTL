package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
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

    private HoaDon mapResultSetToHoaDon(ResultSet rs) throws SQLException {
         HoaDon hd = new HoaDon();
         hd.setMaHD(rs.getString("maHD"));
         Timestamp tsNgayLap = rs.getTimestamp("ngayLap");
         if (tsNgayLap != null) {
             hd.setNgayLap(new java.util.Date(tsNgayLap.getTime()));
         } else {
             hd.setNgayLap(null);
         }
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

    public boolean insert(HoaDon hd) throws SQLException {
        String sql = "{call dbo.sp_InsertHoaDon(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, hd.getMaHD());
            cs.setTimestamp(2, new Timestamp(hd.getNgayLap().getTime()));
            cs.setInt(3, hd.getQuay());
            cs.setBigDecimal(4, BigDecimal.valueOf(hd.getTongTien()));
            cs.setBigDecimal(5, BigDecimal.valueOf(hd.getThanhTien()));
            cs.setBigDecimal(6, BigDecimal.valueOf(hd.getTienNhan()));
            cs.setBigDecimal(7, BigDecimal.valueOf(hd.getTienThoi()));
            cs.setInt(8, hd.getTongSoLuongSP());

            if (hd.getKh() != null) cs.setString(9, hd.getKh().getMa());
            else cs.setNull(9, Types.NVARCHAR);

            if (hd.getNv() != null) cs.setString(10, hd.getNv().getMa());
            else cs.setNull(10, Types.NVARCHAR);

            return cs.executeUpdate() > 0;
        }
    }

    public boolean update(HoaDon hd) throws SQLException {
        String sql = "{call dbo.sp_UpdateHoaDon(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, hd.getMaHD());
            cs.setTimestamp(2, new Timestamp(hd.getNgayLap().getTime()));
            cs.setInt(3, hd.getQuay());
            cs.setBigDecimal(4, BigDecimal.valueOf(hd.getTongTien()));
            cs.setBigDecimal(5, BigDecimal.valueOf(hd.getThanhTien()));
            cs.setBigDecimal(6, BigDecimal.valueOf(hd.getTienNhan()));
            cs.setBigDecimal(7, BigDecimal.valueOf(hd.getTienThoi()));
            cs.setInt(8, hd.getTongSoLuongSP());

            if (hd.getKh() != null) cs.setString(9, hd.getKh().getMa());
            else cs.setNull(9, Types.NVARCHAR);

            if (hd.getNv() != null) cs.setString(10, hd.getNv().getMa());
            else cs.setNull(10, Types.NVARCHAR);

            return cs.executeUpdate() > 0;
        }
    }

    public boolean delete(String maHD) throws SQLException {
        String sql = "{call dbo.sp_DeleteHoaDon(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maHD);
            return cs.executeUpdate() > 0;
        }
    }

    public HoaDon getById(String maHD) throws SQLException {
        String sql = "{call dbo.sp_GetHoaDonById(?)}";
        HoaDon hd = null;
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maHD);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    hd = mapResultSetToHoaDon(rs);
                }
            }
        }
        return hd;
    }

    public List<HoaDon> getAll() throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "{call dbo.sp_GetAllHoaDon()}";
        try (CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToHoaDon(rs));
            }
        }
        return list;
    }

    public List<HoaDon> getListByIDRegex(String regex) throws SQLException {
        List<HoaDon> list = new ArrayList<>();
        String sql = "{call dbo.sp_SearchHoaDonByMaHD(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, regex);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToHoaDon(rs));
                }
            }
        }
        return list;
    }

    public boolean insertWithDetails(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) throws SQLException {
        if (hoaDon == null || chiTietList == null || chiTietList.isEmpty()) {
            return false;
        }

        CallableStatement cs = null;
        boolean success = false;
        String storedProc = "{call dbo.sp_InsertHoaDonWithDetails(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try {
            SQLServerDataTable chiTietData = new SQLServerDataTable();
            chiTietData.addColumnMetadata("maSP", Types.NVARCHAR);
            chiTietData.addColumnMetadata("soLuong", Types.INTEGER);
            chiTietData.addColumnMetadata("donGia", Types.DECIMAL);
            chiTietData.addColumnMetadata("maKM", Types.NVARCHAR);

            for (ChiTietHoaDon cthd : chiTietList) {
                chiTietData.addRow(
                        cthd.getMaSP(),
                        cthd.getSoLuong(),
                        BigDecimal.valueOf(cthd.getDonGia()),
                        cthd.getMaKM()
                );
            }

            cs = conn.prepareCall(storedProc);

            cs.setString(1, hoaDon.getMaHD());
            cs.setTimestamp(2, new Timestamp(hoaDon.getNgayLap().getTime()));
            cs.setInt(3, hoaDon.getQuay());
            cs.setBigDecimal(4, BigDecimal.valueOf(hoaDon.getTongTien()));
            cs.setBigDecimal(5, BigDecimal.valueOf(hoaDon.getThanhTien()));
            cs.setBigDecimal(6, BigDecimal.valueOf(hoaDon.getTienNhan()));
            cs.setBigDecimal(7, BigDecimal.valueOf(hoaDon.getTienThoi()));
            cs.setInt(8, hoaDon.getTongSoLuongSP());

            if (hoaDon.getKh() != null) cs.setString(9, hoaDon.getKh().getMa());
            else cs.setNull(9, Types.NVARCHAR);

            if (hoaDon.getNv() != null) cs.setString(10, hoaDon.getNv().getMa());
            else cs.setNull(10, Types.NVARCHAR);

            if (cs instanceof SQLServerCallableStatement) {
                SQLServerCallableStatement sqlCs = (SQLServerCallableStatement) cs;
                sqlCs.setStructured(11, "dbo.ChiTietHoaDonTableType", chiTietData);
            } else {
                throw new SQLException("CallableStatement không phải là SQLServerCallableStatement, không thể set TVP.");
            }

            cs.executeUpdate();
            success = true;

        } finally {
            if (cs != null) {
                try { cs.close(); } catch (SQLException e) { }
            }
        }
        return success;
    }
}