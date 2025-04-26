CREATE PROCEDURE sp_InsertNhanVien
    @ma NVARCHAR(10),
    @ngayVaoLam DATE,
    @luong FLOAT,
    @caLam NVARCHAR(20),
    @hoTen NVARCHAR(100),
    @SDT NVARCHAR(20),
    @email NVARCHAR(100),
    @namSinh DATE,
    @diaChi NVARCHAR(255),
    @maNQL NVARCHAR(10),
    @maTK NVARCHAR(10)
AS
BEGIN
    INSERT INTO NhanVien (ma, ngayVaoLam, luong, caLam, hoTen, SDT, email, namSinh, diaChi, maNQL, maTK)
    VALUES (@ma, @ngayVaoLam, @luong, @caLam, @hoTen, @SDT, @email, @namSinh, @diaChi, @maNQL, @maTK)
END
GO
CREATE PROCEDURE sp_UpdateNhanVien
    @ngayVaoLam DATE,
    @luong FLOAT,
    @caLam NVARCHAR(20),
    @hoTen NVARCHAR(100),
    @SDT NVARCHAR(20),
    @email NVARCHAR(100),
    @namSinh DATE,
    @diaChi NVARCHAR(200),
    @maNQL NVARCHAR(10),
    @maTK NVARCHAR(10),
    @ma NVARCHAR(10)
AS
BEGIN
    UPDATE NhanVien
    SET ngayVaoLam = @ngayVaoLam,
        luong = @luong,
        caLam = @caLam,
        hoTen = @hoTen,
        SDT = @SDT,
        email = @email,
        namSinh = @namSinh,
        diaChi = @diaChi,
        maNQL = @maNQL,
        maTK = @maTK
    WHERE ma = @ma
END
GO

CREATE PROCEDURE sp_GetNhanVienById
    @ma NVARCHAR(10)
AS
BEGIN
    SELECT * FROM NhanVien WHERE ma = @ma
END
GO

CREATE PROCEDURE sp_GetNhanVienByTK
    @maTK NVARCHAR(10)
AS
BEGIN
    SELECT * FROM NhanVien WHERE maTK = @maTK
END
GO

CREATE PROCEDURE sp_FindNhanVien
    @keyword NVARCHAR(100)
AS
BEGIN
    SELECT * 
    FROM NhanVien
    WHERE ma LIKE '%' + @keyword + '%'
       OR hoTen LIKE '%' + @keyword + '%'
END

--TaiKhoan
CREATE PROCEDURE sp_InsertTaiKhoan
    @maTK NVARCHAR(50),
    @tenDN NVARCHAR(50),
    @matKhau NVARCHAR(50),
    @vaiTro NVARCHAR(50)
AS
BEGIN
    INSERT INTO TaiKhoan (maTK, tenDN, matKhau, vaiTro)
    VALUES (@maTK, @tenDN, @matKhau, @vaiTro)
END

CREATE PROCEDURE sp_UpdateTaiKhoan
    @maTK NVARCHAR(50),
    @tenDN NVARCHAR(50),
    @matKhau NVARCHAR(50),
    @vaiTro NVARCHAR(50)
AS
BEGIN
    UPDATE TaiKhoan
    SET tenDN = @tenDN,
        matKhau = @matKhau,
        vaiTro = @vaiTro
    WHERE maTK = @maTK
END