use QuanLyCHTL
--insert
Go
CREATE PROCEDURE InsertSanPham
    @maSP VARCHAR(50),
    @tenSP VARCHAR(255),    
    @giaBan DECIMAL(18,2),
    @giaGoc DECIMAL(18,2),
    @maLoai VARCHAR(50),
    @maNH VARCHAR(50),
    @maNCC VARCHAR(50),
    @maNQL VARCHAR(50)
AS
BEGIN
    INSERT INTO SanPham (maSP, tenSP, giaBan, giaGoc, maLoai, maNH, maNCC, maNQL)
    VALUES (@maSP, @tenSP, @giaBan, @giaGoc, @maLoai, @maNH, @maNCC, @maNQL)
END
GO

--update
CREATE PROCEDURE UpdateSanPham
    @maSP VARCHAR(50),	    
    @tenSP VARCHAR(255),
    @giaBan DECIMAL(18,2),
    @giaGoc DECIMAL(18,2),
    @maLoai VARCHAR(50),
    @maNH VARCHAR(50),
    @maNCC VARCHAR(50),
    @maNQL VARCHAR(50)
AS
BEGIN
    UPDATE SanPham
    SET tenSP = @tenSP, giaBan = @giaBan, giaGoc = @giaGoc, maLoai = @maLoai,
        maNH = @maNH, maNCC = @maNCC, maNQL = @maNQL
    WHERE maSP = @maSP
END
GO

--delete
CREATE PROCEDURE DeleteSanPham
    @maSP VARCHAR(50)
AS
BEGIN
    DELETE FROM SanPham WHERE maSP = @maSP
END
GO

--GetSanPhamById
CREATE PROCEDURE GetSanPhamById
    @maSP VARCHAR(50)
AS
BEGIN
    SELECT * FROM SanPham WHERE maSP = @maSP
END
GO

--gett all
CREATE PROCEDURE GetAllSanPham
AS
BEGIN
    SELECT * FROM SanPham
END
GO

CREATE PROCEDURE GetMaLoaiList
AS
BEGIN			  
    SELECT DISTINCT maLoai FROM SanPham;
END
GO

CREATE PROCEDURE GetMaNHList
AS
BEGIN
    SELECT DISTINCT maNH FROM SanPham;
END
GO

CREATE PROCEDURE GetMaNCCList
AS
BEGIN
    SELECT DISTINCT maNCC FROM SanPham;
END
GO

--sanpham theo maloai
CREATE PROCEDURE GetProductsByCategory
    @maLoai NVARCHAR(50)
AS
BEGIN
    SELECT * 
    FROM SanPham 
    WHERE maLoai = @maLoai;
END
GO

--sanpham theo ncc
CREATE PROCEDURE GetProductsBySupplier
    @maNCC NVARCHAR(50)
AS
BEGIN
    SELECT * 
    FROM SanPham 
    WHERE maNCC = @maNCC;
END
GO

--sanpham theo nhanhang
CREATE PROCEDURE GetProductsByBrand
    @maNH NVARCHAR(50)
AS
BEGIN
    SELECT * 
    FROM SanPham 
    WHERE maNH = @maNH;
END
GO

--timkiem
CREATE PROCEDURE SearchProducts
    @keyword NVARCHAR(100)
AS
BEGIN
    SELECT * 
    FROM SanPham 
    WHERE maSP LIKE '%' + @keyword + '%' OR tenSP LIKE '%' + @keyword + '%';
END
GO

CREATE PROCEDURE DeleteNSanPham
    @maSPs NVARCHAR(MAX)  
AS
BEGIN
    DELETE FROM SanPham
    WHERE maSP IN (SELECT value FROM STRING_SPLIT(@maSPs, ','));  
END;
GO
