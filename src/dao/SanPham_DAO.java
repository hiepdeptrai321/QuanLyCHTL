package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import entity.SanPham;

public class SanPham_DAO {
    private Connection conn;

    public SanPham_DAO(Connection conn) {
        this.conn = conn;
    }
    private String normalizeMaSP(String maSP) {
        if (maSP.length() < 4) {
           
            maSP = maSP.substring(0, 2) + String.format("%02d", Integer.parseInt(maSP.substring(2)));
        }
        return maSP;
    }
    private boolean isProductExists(String maSP) throws SQLException {
        // Bạn có thể không cần dòng normalize này nếu maSP luôn đúng định dạng "SP..."
         maSP = normalizeMaSP(maSP);
        String sql = "{CALL GetSanPhamById(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maSP); // Đặt tham số maSP
            try (ResultSet rs = cs.executeQuery()) {
                // Chỉ cần kiểm tra xem ResultSet có dòng nào không
                // Nếu rs.next() trả về true, nghĩa là có ít nhất một sản phẩm khớp với maSP
                return rs.next();
            }
        }
    }
    public boolean isMaSPExists(String maSP) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SanPham WHERE maSP = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; 
            }
        }
        return false; 
    }

    public boolean insert(SanPham sp) throws SQLException {
    	if (isMaSPExists(sp.getMaSP())) {
            JOptionPane.showMessageDialog(null, "Mã sản phẩm " + sp.getMaSP() + " đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false; 
        }

        String sql = "{CALL InsertSanPham(?, ?, ?, ?, ?, ?, ?, ?)}"; 
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, sp.getMaSP());
            cs.setString(2, sp.getTenSP());
            cs.setDouble(3, sp.getGiaBan());
            cs.setDouble(4, sp.getGiaGoc());
            cs.setString(5, sp.getMaLoai());
            cs.setString(6, sp.getMaNH());
            cs.setString(7, sp.getMaNCC());
            cs.setString(8, sp.getMaNQL());
            return cs.executeUpdate() > 0;
        }
    }

    public boolean update(SanPham sp) throws SQLException {
        String sql = "{CALL UpdateSanPham(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, sp.getMaSP());
            cs.setString(2, sp.getTenSP());
            cs.setDouble(3, sp.getGiaBan());
            cs.setDouble(4, sp.getGiaGoc());
            cs.setString(5, sp.getMaLoai());
            cs.setString(6, sp.getMaNH());
            cs.setString(7, sp.getMaNCC());
            cs.setString(8, sp.getMaNQL());
            return cs.executeUpdate() > 0;
        }
    }

    public boolean delete(List<String> maSPList) throws SQLException {
        StringBuilder maSPs = new StringBuilder();
        for (String maSP : maSPList) {
            if (maSPs.length() > 0) {
                maSPs.append(",");
            }
            maSPs.append(maSP);  
        }

        String sql = "{CALL DeleteNSanPham(?)}";  
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maSPs.toString());  
            return cs.executeUpdate() > 0;  
        }
    }
    

    public SanPham getById(String maSP) throws SQLException {
        String sql = "{CALL GetSanPhamById(?)}";
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maSP);  // Chuyển maSP thành chuỗi
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("maSP"));  // Lấy maSP dưới dạng chuỗi
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
        String sql = "{CALL GetAllSanPham()}";
        try (CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
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
    public List<String> getMaLoaiList() throws SQLException {
        List<String> maLoaiList = new ArrayList<>();
        String sql = "{CALL GetMaLoaiList}"; // Call the stored procedure to get maLoai
        try (CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                maLoaiList.add(rs.getString("maLoai"));
            }
        }
        return maLoaiList;
    }

    public List<String> getMaNHList() throws SQLException {
        List<String> maNHList = new ArrayList<>();
        String sql = "{CALL GetMaNHList}"; // Call the stored procedure to get maNH
        try (CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                maNHList.add(rs.getString("maNH"));
            }
        }
        return maNHList;
    }

    public List<String> getMaNCCList() throws SQLException {
        List<String> maNCCList = new ArrayList<>();
        String sql = "{CALL GetMaNCCList}"; // Call the stored procedure to get maNCC
        try (CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                maNCCList.add(rs.getString("maNCC"));
            }
        }
        return maNCCList;
    }
    //san pham theo ma loai
    public List<SanPham> getProductsByCategory(String maLoai) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        String sql = "{CALL GetProductsByCategory(?)}";  // Gọi stored procedure GetProductsByCategory
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maLoai);  // Gửi tham số maLoai
            ResultSet rs = cs.executeQuery();
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
                sanPhamList.add(sp);
            }
        }
        return sanPhamList;
    }
    
    //san pham theo ncc
    public List<SanPham> getProductsBySupplier(String maNCC) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        String sql = "{CALL GetProductsBySupplier(?)}";  // Gọi stored procedure GetProductsBySupplier
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maNCC);  // Gửi tham số maNCC
            ResultSet rs = cs.executeQuery();
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
                sanPhamList.add(sp);
            }
        }
        return sanPhamList;
    }
    
    //san pham theo nhan hang
    public List<SanPham> getProductsByBrand(String maNH) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        String sql = "{CALL GetProductsByBrand(?)}";  // Gọi stored procedure GetProductsBySupplier
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, maNH);  // Gửi tham số maNCC
            ResultSet rs = cs.executeQuery();
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
                sanPhamList.add(sp);
            }
        }
        return sanPhamList;
    }
    
    public List<SanPham> searchProducts(String keyword) throws SQLException {
        List<SanPham> list = new ArrayList<>();
        String sql = "{CALL SearchProducts(?)}";  
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, keyword);  
            ResultSet rs = cs.executeQuery();  
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

    
}
