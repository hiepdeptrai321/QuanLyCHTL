--Store procedure 

--HoaDon
USE QuanLyCHTL;
GO



IF OBJECT_ID('dbo.sp_InsertHoaDon', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_InsertHoaDon;
GO
CREATE PROCEDURE dbo.sp_InsertHoaDon
    @maHD NVARCHAR(50),
    @ngayLap DATETIME2,
    @quay INT,
    @tongTien DECIMAL(18, 2),
    @thanhTien DECIMAL(18, 2),
    @tienNhan DECIMAL(18, 2),
    @tienThoi DECIMAL(18, 2),
    @tongSoLuongSP INT,
    @maKH NVARCHAR(50),
    @maNV NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    INSERT INTO dbo.HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV)
    VALUES (@maHD, @ngayLap, @quay, @tongTien, @thanhTien, @tienNhan, @tienThoi, @tongSoLuongSP, @maKH, @maNV);
END;
GO

IF OBJECT_ID('dbo.sp_UpdateHoaDon', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_UpdateHoaDon;
GO
CREATE PROCEDURE dbo.sp_UpdateHoaDon
    @maHD NVARCHAR(50),
    @ngayLap DATETIME2,
    @quay INT,
    @tongTien DECIMAL(18, 2),
    @thanhTien DECIMAL(18, 2),
    @tienNhan DECIMAL(18, 2),
    @tienThoi DECIMAL(18, 2),
    @tongSoLuongSP INT,
    @maKH NVARCHAR(50),
    @maNV NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.HoaDon
    SET ngayLap = @ngayLap,
        quay = @quay,
        tongTien = @tongTien,
        thanhTien = @thanhTien,
        tienNhan = @tienNhan,
        tienThoi = @tienThoi,
        tongSoLuongSP = @tongSoLuongSP,
        maKH = @maKH,
        maNV = @maNV
    WHERE maHD = @maHD;
END;
GO

IF OBJECT_ID('dbo.sp_DeleteHoaDon', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_DeleteHoaDon;
GO
CREATE PROCEDURE dbo.sp_DeleteHoaDon
    @maHD NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM dbo.HoaDon
    WHERE maHD = @maHD;
END;
GO

IF OBJECT_ID('dbo.sp_GetHoaDonById', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetHoaDonById;
GO
CREATE PROCEDURE dbo.sp_GetHoaDonById
    @maHD NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM dbo.HoaDon
    WHERE maHD = @maHD;
END;
GO

IF OBJECT_ID('dbo.sp_GetAllHoaDon', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_GetAllHoaDon;
GO
CREATE PROCEDURE dbo.sp_GetAllHoaDon
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM dbo.HoaDon
    ORDER BY ngayLap DESC;
END;
GO

IF OBJECT_ID('dbo.sp_SearchHoaDonByMaHD', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_SearchHoaDonByMaHD;
GO
CREATE PROCEDURE dbo.sp_SearchHoaDonByMaHD
    @maHD_pattern NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT *
    FROM dbo.HoaDon
    WHERE maHD LIKE '%' + @maHD_pattern + '%';
END;
GO


IF TYPE_ID(N'dbo.ChiTietHoaDonTableType') IS NULL
BEGIN
    CREATE TYPE dbo.ChiTietHoaDonTableType AS TABLE(
        maSP NVARCHAR(50) NOT NULL,
        soLuong INT NOT NULL,
        donGia DECIMAL(18, 2) NOT NULL,
        maKM NVARCHAR(50) NULL
    )
END
GO
IF OBJECT_ID('dbo.sp_InsertHoaDonWithDetails', 'P') IS NOT NULL
    DROP PROCEDURE dbo.sp_InsertHoaDonWithDetails;
GO
CREATE PROCEDURE dbo.sp_InsertHoaDonWithDetails
    @maHD NVARCHAR(50),
    @ngayLap DATETIME2,
    @quay INT,
    @tongTien DECIMAL(18, 2),
    @thanhTien DECIMAL(18, 2),
    @tienNhan DECIMAL(18, 2),
    @tienThoi DECIMAL(18, 2),
    @tongSoLuongSP INT,
    @maKH NVARCHAR(50),
    @maNV NVARCHAR(50),
    @ChiTietList dbo.ChiTietHoaDonTableType READONLY
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;
    BEGIN TRY
        INSERT INTO dbo.HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV)
        VALUES (@maHD, @ngayLap, @quay, @tongTien, @thanhTien, @tienNhan, @tienThoi, @tongSoLuongSP, @maKH, @maNV);

        INSERT INTO dbo.ChiTietHoaDon (maHD, maSP, soLuong, donGia, maKM)
        SELECT
            @maHD, tvp.maSP, tvp.soLuong, tvp.donGia, tvp.maKM
        FROM
            @ChiTietList AS tvp;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO
--================================================================================
create trigger trg_updateKMvaoCTHD
on [dbo].[ChiTietHoaDon]
after insert
as
	begin
		update [dbo].[ChiTietHoaDon]
		set [maKM] = km.maKM
		from [dbo].[KhuyenMai] km join [dbo].[SanPham] sp on km.maSP = sp.maSP
			join [dbo].[ChiTietHoaDon] cthd on cthd.maSP = sp.maSP
	end
go


create function func_getGiaTriGiam(@maSP nvarchar(50))
returns float
as
	begin
		declare @gtGiam float = 0;
		select top 1  @gtGiam = giaTriGiam
		from [dbo].[KhuyenMai] 
		where [maSP] = @maSP and getdate() >= ngayBatDau and getdate() <= ngayKetThuc
		order by giaTriGiam desc
		return @gtGiam
	end

Go
-- *** 1. Thống kê theo Ngày ***
IF OBJECT_ID('sp_ThongKeDoanhThuTheoNgay', 'P') IS NOT NULL DROP PROCEDURE sp_ThongKeDoanhThuTheoNgay;
GO
CREATE PROCEDURE sp_ThongKeDoanhThuTheoNgay
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        CONVERT(date, ngayLap) AS ThoiGian, -- Chỉ lấy phần ngày, kiểu DATE
        COUNT(maHD) AS SoLuongHoaDon,
        SUM(thanhTien) AS TongDoanhThu,
        SUM(tongSoLuongSP) AS TongSoLuongSPBanRa
    FROM HoaDon -- Thay HoaDon bằng tên bảng hóa đơn của bạn nếu khác
    GROUP BY CONVERT(date, ngayLap)
    ORDER BY ThoiGian DESC;
END
GO

-- *** 2. Thống kê theo Tháng ***
IF OBJECT_ID('sp_ThongKeDoanhThuTheoThang', 'P') IS NOT NULL DROP PROCEDURE sp_ThongKeDoanhThuTheoThang;
GO
CREATE PROCEDURE sp_ThongKeDoanhThuTheoThang
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        FORMAT(ngayLap, 'yyyy-MM') AS ThoiGian, -- Định dạng Năm-Tháng, kiểu chuỗi
        COUNT(maHD) AS SoLuongHoaDon,
        SUM(thanhTien) AS TongDoanhThu,
        SUM(tongSoLuongSP) AS TongSoLuongSPBanRa
    FROM HoaDon -- Thay HoaDon bằng tên bảng hóa đơn của bạn nếu khác
    GROUP BY FORMAT(ngayLap, 'yyyy-MM')
    ORDER BY ThoiGian DESC;
END
GO

-- *** 3. Thống kê theo Năm ***
IF OBJECT_ID('sp_ThongKeDoanhThuTheoNam', 'P') IS NOT NULL DROP PROCEDURE sp_ThongKeDoanhThuTheoNam;
GO
CREATE PROCEDURE sp_ThongKeDoanhThuTheoNam
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        YEAR(ngayLap) AS ThoiGian, -- Chỉ lấy Năm, kiểu INT
        COUNT(maHD) AS SoLuongHoaDon,
        SUM(thanhTien) AS TongDoanhThu,
        SUM(tongSoLuongSP) AS TongSoLuongSPBanRa
    FROM HoaDon -- Thay HoaDon bằng tên bảng hóa đơn của bạn nếu khác
    GROUP BY YEAR(ngayLap)
    ORDER BY ThoiGian DESC;
END
GO