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