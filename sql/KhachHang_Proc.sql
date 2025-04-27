use QuanLyCHTL
--insert
CREATE PROCEDURE sp_InsertKhachHang
    @p_ma NVARCHAR(50),
    @p_ngayDangKy DATE,
    @p_diemTichLuy INT,
    @p_hangThanhVien INT,
    @p_soLanMuaHang INT,
    @p_hoTen NVARCHAR(50),
    @p_sdt NVARCHAR(50),
    @p_email NVARCHAR(50),
    @p_namSinh DATE,
    @p_diaChi NVARCHAR(255),
    @p_maNV NVARCHAR(50)
AS
BEGIN
    INSERT INTO KhachHang (ma, ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, hoTen, sdt, email, namSinh, diaChi, maNV)
    VALUES (@p_ma, @p_ngayDangKy, @p_diemTichLuy, @p_hangThanhVien, @p_soLanMuaHang, @p_hoTen, @p_sdt, @p_email, @p_namSinh, @p_diaChi, @p_maNV);
END
GO

--update
CREATE PROCEDURE sp_UpdateKhachHang
    @p_ma NVARCHAR(50),
    @p_ngayDangKy DATE,
    @p_diemTichLuy INT,
    @p_hangThanhVien INT,
    @p_soLanMuaHang INT,
    @p_hoTen NVARCHAR(50),
    @p_sdt NVARCHAR(50),
    @p_email NVARCHAR(50),
    @p_namSinh DATE,
    @p_diaChi NVARCHAR(255),
    @p_maNV NVARCHAR(50)
AS
BEGIN
    UPDATE KhachHang
    SET ngayDangKy = @p_ngayDangKy,
        diemTichLuy = @p_diemTichLuy,
        hangThanhVien = @p_hangThanhVien,
        soLanMuaHang = @p_soLanMuaHang,
        hoTen = @p_hoTen,
        sdt = @p_sdt,
        email = @p_email,
        namSinh = @p_namSinh,
        diaChi = @p_diaChi,
        maNV = @p_maNV
    WHERE ma = @p_ma;
END
GO

--delete
CREATE PROCEDURE sp_DeleteKhachHang
    @p_ma NVARCHAR(50)
AS
BEGIN
    DELETE FROM KhachHang WHERE ma = @p_ma;
END
GO

--select
CREATE PROCEDURE sp_GetKhachHangById
    @p_ma NVARCHAR(50)
AS
BEGIN
    SELECT * FROM KhachHang WHERE ma = @p_ma;
END
GO

--select all
CREATE PROCEDURE sp_GetAllKhachHang
AS
BEGIN
    SELECT * FROM KhachHang;
END
GO

--SearchKhachHangByName
CREATE PROCEDURE sp_SearchKhachHangByName
    @searchText NVARCHAR(50)
AS
BEGIN
    SELECT * 
    FROM KhachHang
    WHERE hoTen LIKE '%' + @searchText + '%';
END
GO

CREATE PROCEDURE SearchKhachHang
    @keyword NVARCHAR(100)
AS
BEGIN
    SELECT * 
    FROM KhachHang 
    WHERE hoTen LIKE '%' + @keyword + '%' OR ma LIKE '%' + @keyword + '%';
END
GO

CREATE PROCEDURE SP_GetDistinctMemberRanks
AS
BEGIN
    SELECT DISTINCT
        HangThanhVien
    FROM
        KhachHang
    WHERE HangThanhVien IS NOT NULL AND HangThanhVien != ''; -- Loại bỏ các giá trị rỗng hoặc null nếu cần
END
GO

CREATE PROCEDURE SP_GetDistinctEmployeeIDsCreatingCustomers
AS
BEGIN
    SELECT DISTINCT
        MaNV
    FROM
        KhachHang
    WHERE MaNV IS NOT NULL AND MaNV != ''; -- Loại bỏ các giá trị rỗng hoặc null nếu cần
END
GO

CREATE PROCEDURE SP_GetKhachHangByHangTV
    @HangTV NVARCHAR(50) -- Sử dụng kiểu dữ liệu phù hợp với cột HangThanhVien
AS
BEGIN
    SELECT
        Ma,
        HoTen,
        Sdt,
        Email,
        NamSinh,
        DiaChi,
        NgayDangKy,
        DiemTichLuy,
        HangThanhVien,
        SoLanMuaHang,
        MaNV
    FROM
        KhachHang
    WHERE
        HangThanhVien = @HangTV;
END
GO

CREATE PROCEDURE SP_GetKhachHangByMaNV
    @MaNV NVARCHAR(10) -- Sử dụng kiểu dữ liệu phù hợp với cột MaNV
AS
BEGIN
    SELECT
        Ma,
        HoTen,
        Sdt,
        Email,
        NamSinh,
        DiaChi,
        NgayDangKy,
        DiemTichLuy,
        HangThanhVien,
        SoLanMuaHang,
        MaNV
    FROM
        KhachHang
    WHERE
        MaNV = @MaNV;
END
GO

CREATE PROCEDURE SP_HasKhachHangInvoices
    @Ma NVARCHAR(10) -- Kiểu dữ liệu phải khớp với cột MaKH/maKH trong DB
AS
BEGIN
    SELECT COUNT(*) FROM HoaDon WHERE maKH = @Ma;
END;
GO

ALTER TABLE HoaDon
ADD CONSTRAINT FK_HoaDon_KhachHang
FOREIGN KEY (maKH) REFERENCES KhachHang(ma)
ON DELETE CASCADE;

ALTER TABLE ChiTietHoaDon
ADD CONSTRAINT FK_ChiTietHoaDon_KhachHang
FOREIGN KEY (maHD) REFERENCES HoaDon(maHD)
ON DELETE CASCADE;

ALTER TABLE KhuyenMai
ADD CONSTRAINT FK_KhuyenMai_SanPham
FOREIGN KEY (maSP)
REFERENCES SanPham(maSP)
ON DELETE CASCADE;

ALTER TABLE ChiTietHoaDon
ADD CONSTRAINT FK_ChiTietHoaDon_SanPham
FOREIGN KEY (maSP)
REFERENCES SanPham(maSP)
ON DELETE CASCADE;