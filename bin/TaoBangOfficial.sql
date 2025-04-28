create database QuanLyCHTL
use QuanLyCHTL

--drop database
use master
ALTER DATABASE QuanLyCHTL
SET SINGLE_USER
WITH ROLLBACK IMMEDIATE;
GO
drop database QuanLyCHTL

-- -----------------------------
-- T?O B?NG H? TH?NG C?A H�NG TI?N L?I
-- -----------------------------
-- =============================
-- CREATE TABLE STATEMENTS (Giữ nguyên như bạn cung cấp)
-- =============================
-- Khuyến nghị: Nên dùng NVARCHAR cho các cột chứa tiếng Việt có dấu
-- Ví dụ: tenLoai NVARCHAR(50), tenNCC NVARCHAR(100), diaChi NVARCHAR(255), hoTen NVARCHAR(50), ...

CREATE TABLE LoaiSanPham (
    maLoai NVARCHAR(50) PRIMARY KEY,
    tenLoai NVARCHAR(50) -- Nên là NVARCHAR(50)
);

CREATE TABLE NhanHang (
    maNH NVARCHAR(50) PRIMARY KEY,
    tenNH NVARCHAR(50) -- Nên là NVARCHAR(50)
);

CREATE TABLE NhaCungCap (
    maNCC NVARCHAR(50) PRIMARY KEY,
    tenNCC NVARCHAR(100), -- Nên là NVARCHAR(100)
    diaChi NVARCHAR(255), -- Nên là NVARCHAR(255)
    soDienThoai NVARCHAR(50),
    xepLoai INTEGER
);

CREATE TABLE TaiKhoan (
    maTK NVARCHAR(50) PRIMARY KEY,
    tenDN NVARCHAR(35),
    matKhau NVARCHAR(35),
    vaiTro NVARCHAR(50)
);

CREATE TABLE NguoiQuanLy (
    ma NVARCHAR(50) PRIMARY KEY,
    capBac NVARCHAR(10),
    phuCap FLOAT,
    hoTen NVARCHAR(50), -- Nên là NVARCHAR(50)
    sdt NVARCHAR(12),
    email NVARCHAR(50),
    namSinh DATE,
    diaChi NVARCHAR(255), -- Nên là NVARCHAR(255)
    maTK NVARCHAR(50),
    FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
);

CREATE TABLE SanPham (
    maSP NVARCHAR(50) PRIMARY KEY,
    tenSP NVARCHAR(100), -- Nên là NVARCHAR(100)
    giaBan FLOAT,
    giaGoc FLOAT,
    maNH NVARCHAR(50),
    maLoai NVARCHAR(50),
    maNCC NVARCHAR(50),
    maNQL NVARCHAR(50),
    FOREIGN KEY (maNH) REFERENCES NhanHang(maNH),
    FOREIGN KEY (maLoai) REFERENCES LoaiSanPham(maLoai),
    FOREIGN KEY (maNCC) REFERENCES NhaCungCap(maNCC),
    FOREIGN KEY (maNQL) REFERENCES NguoiQuanLy(ma)
);

CREATE TABLE KhuyenMai (
    maKM NVARCHAR(50) PRIMARY KEY,
	tenKM NVARCHAR(50),
    giaTriGiam FLOAT,
    ngayBatDau DATE,
    ngayKetThuc DATE,
    moTa NVARCHAR(255),
    maSP NVARCHAR(50),
    maNQL NVARCHAR(50),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP),
    FOREIGN KEY (maNQL) REFERENCES NguoiQuanLy(ma)
);

CREATE TABLE NhanVien (
    ma NVARCHAR(50) PRIMARY KEY,
    ngayVaoLam DATE,
    luong FLOAT,
    caLam NVARCHAR(50),
    hoTen NVARCHAR(50), -- Nên là NVARCHAR(50)
    sdt NVARCHAR(12),
    email NVARCHAR(50),
    namSinh DATE,
    diaChi NVARCHAR(255), -- Nên là NVARCHAR(255)
    maNQL NVARCHAR(50),
    maTK NVARCHAR(50),
    FOREIGN KEY (maNQL) REFERENCES NguoiQuanLy(ma),
    FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
);

CREATE TABLE KhachHang (
    ma NVARCHAR(50) PRIMARY KEY,
    ngayDangKy DATETIME,
    diemTichLuy INTEGER,
    hangThanhVien INTEGER,
    soLanMuaHang INTEGER,
    hoTen NVARCHAR(50), -- Nên là NVARCHAR(50)
    sdt NVARCHAR(50),
    email NVARCHAR(50),
    namSinh DATE,
    diaChi NVARCHAR(255), -- Nên là NVARCHAR(255)
    maNV NVARCHAR(50),
    FOREIGN KEY (maNV) REFERENCES NhanVien(ma)
);

CREATE TABLE HoaDon (
    maHD NVARCHAR(50) PRIMARY KEY,
    ngayLap DATETIME,
    quay INTEGER,
    tongTien FLOAT,
    thanhTien FLOAT,
    tienNhan FLOAT,
    tienThoi FLOAT,
    tongSoLuongSP INTEGER,
    maKH NVARCHAR(50),
    maNV NVARCHAR(50),
    FOREIGN KEY (maKH) REFERENCES KhachHang(ma),
    FOREIGN KEY (maNV) REFERENCES NhanVien(ma)
);

CREATE TABLE ChiTietHoaDon (
    soLuong INTEGER,
    donGia FLOAT,
    maHD NVARCHAR(50),
    maSP NVARCHAR(50),
    maKM NVARCHAR(50),
    PRIMARY KEY (maHD, maSP),
    FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP),
    FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
);
GO


CREATE TRIGGER trg_AfterInsertNguoiQuanLy_AddNhanVien
ON dbo.NguoiQuanLy
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.NhanVien (
        ma,
        ngayVaoLam,
        luong,
        caLam,
        hoTen,
        sdt,
        email,
        namSinh,
        diaChi,
        maNQL,
        maTK
    )
    SELECT
        i.ma,
        CONVERT(DATE, GETDATE()), -- Lấy ngày hiện tại làm Ngày vào làm
        0,                        -- Lương mặc định là 0
        NULL,                     -- Ca làm mặc định là NULL
        i.hoTen,
        i.sdt,
        i.email,
        i.namSinh,
        i.diaChi,
        NULL,                     -- Mã người quản lý mặc định là NULL
        i.maTK
    FROM
        inserted i;
END;
GO

CREATE TRIGGER trg_themTaiKhoanSauKhiTaoNhanVien
ON dbo.NhanVien
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.TaiKhoan (maTK,tenDN,matKhau,vaiTro)
    SELECT
		i.ma,
        i.ma,         
        '1',
		CASE 
            WHEN LEFT(i.ma, 2) = 'QL' THEN 'quanly'
            ELSE 'nhanvien'
        END
    FROM inserted i;
END;
GO
-- =============================
--Chèn dữ liệu
-- =============================

-- LoaiSanPham
INSERT INTO LoaiSanPham (maLoai, tenLoai)
VALUES ('LSP01', N'Nước giải khát'),
       ('LSP02', N'Bánh kẹo'),
       ('LSP03', N'Mì ăn liền'),
       ('LSP04', N'Sữa'),
       ('LSP05', N'Gia vị'),
       ('LSP06', N'Đồ hộp'),
       ('LSP07', N'Thực phẩm đông lạnh'),
       ('LSP08', N'Trái cây'),
       ('LSP09', N'Rau củ'),
       ('LSP10', N'Đồ ăn nhanh');

-- NhanHang
INSERT INTO NhanHang (maNH, tenNH)
VALUES ('NH01', 'Pepsi'),
       ('NH02', 'Coca-Cola'),
       ('NH03', 'Vinamilk'),
       ('NH04', 'Oreo'),
       ('NH05', 'Milo'),
       ('NH06', 'Omachi'),
       ('NH07', 'Chinsu'),
       ('NH08', 'Vedan'),
       ('NH09', 'Acecook'),
       ('NH10', 'Nutifood');

-- NhaCungCap
INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, soDienThoai, xepLoai)
VALUES
('NCC01', N'Công ty Nước giải khát VN', N'123 Đường Nước, TP.HCM', '0889285559', 5),
('NCC02', N'Công ty Thực phẩm ABC', N'12 Trần Hưng Đạo, Hà Nội', '0912345678', 4),
('NCC03', N'Công ty Vinamilk', N'1A Nguyễn Huệ, TP.HCM', '0987654321', 5),
('NCC04', N'Công ty Nestle', N'34 Lê Thường Kiệt, Đà Nẵng', '0909123456', 5),
('NCC05', N'Công ty Ajinomoto', N'56 Phạm Văn Đồng, Hà Nội', '0978123456', 4),
('NCC06', N'Công ty Masan', N'789 CMT8, TP.HCM', '0968345678', 5),
('NCC07', N'Công ty Bánh kẹo Kinh Đô', N'234 Nguyễn Trãi, Hà Nội', '0912233445', 4),
('NCC08', N'Công ty Acecook', N'65 Võ Văn Tần, TP.HCM', '0909988776', 5),
('NCC09', N'Công ty NutiFood', N'9 Lý Chính Thắng, Cần Thơ', '0888666555', 3),
('NCC10', N'Công ty Vissan', N'100 Lê Duẩn, TP.HCM', '0833221144', 4);

-- TaiKhoan
INSERT INTO TaiKhoan (maTK,tenDN, matKhau, vaiTro)
VALUES
('QL100', 'hiep', 'hiep123', 'quanly'),
('QL200', 'cong', 'cong123', 'quanly'),
('QL300', 'an', 'an123', 'quanly')





-- NguoiQuanLy
INSERT INTO NguoiQuanLy (ma, capBac, phuCap, hoTen, sdt, email, namSinh, diaChi)
VALUES
('QL03', 'A1', 1000000, N'Đỗ Phú Hiệp', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi'),
('QL04', 'A1', 1000000, N'Hoàng Phước Thành Công', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi'),
('QL05', 'A1', 1000000, N'Đàm Thái An', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi'),
('QL01', 'A1', 1000000, N'Nguyễn Văn An', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi'),
('QL02', 'A2', 1100000, N'Trần Thị Bình', '0912345679', 'binh@gmail.com', '1990-02-02', N'2 Nguyễn Huệ')


-- SanPham
INSERT INTO SanPham (maSP, tenSP, giaBan, giaGoc, maNH, maLoai, maNCC, maNQL)
VALUES
('SP01', N'Pepsi lon 330ml', 10000, 8000, 'NH01', 'LSP01', 'NCC01', 'QL01'),
('SP02', N'Coca lon 330ml', 10500, 8500, 'NH02', 'LSP01', 'NCC02', 'QL01'),
('SP03', N'Sữa tươi Vinamilk 1L', 32000, 28000, 'NH03', 'LSP04', 'NCC03', 'QL02'),
('SP04', N'Bánh Oreo 133g', 15000, 12000, 'NH04', 'LSP02', 'NCC04', 'QL02'),
('SP05', N'Sữa Milo hộp 180ml', 9000, 7000, 'NH05', 'LSP04', 'NCC05', 'QL01'),
('SP06', N'Mì Omachi tôm chua cay', 6000, 4500, 'NH06', 'LSP03', 'NCC06', 'QL01'),
('SP07', N'Nước tương Chinsu', 12000, 10000, 'NH07', 'LSP05', 'NCC06', 'QL02'),
('SP08', N'Bột ngọt Vedan 400g', 25000, 22000, 'NH08', 'LSP05', 'NCC07', 'QL02'),
('SP09', N'Mì Hảo Hảo tôm chua cay', 5000, 4000, 'NH09', 'LSP03', 'NCC08', 'QL02'),
('SP10', N'Sữa NutiFood 180ml', 8500, 6500, 'NH10', 'LSP04', 'NCC09', 'QL02');

-- KhuyenMai
INSERT INTO KhuyenMai (maKM, tenKM, giaTriGiam, ngayBatDau, ngayKetThuc, moTa, maSP, maNQL)
VALUES
('KM01', N'Giảm giá mùa hè', 10, '2025-05-01', '2025-05-10', N'Chương trình giảm giá đặc biệt chào hè.', 'SP01', 'QL01'),
('KM02', N'Ưu đãi hè', 5, '2025-06-01', '2025-06-15', N'Ưu đãi hấp dẫn trong mùa hè.', 'SP02', 'QL01'),
('KM03', N'Giảm sốc', 15, '2025-07-01', '2025-07-10', N'Giảm giá cực sốc trong thời gian ngắn.', 'SP03', 'QL02'),
('KM04', N'Flash Sale', 20, '2025-08-01', '2025-08-05', N'Khuyến mãi chớp nhoáng với mức giảm giá lớn, chỉ diễn ra trong 5 ngày.', 'SP04', 'QL02'),
('KM05', N'Back to School', 8, '2025-09-01', '2025-09-07', N'Ưu đãi đặc biệt mừng mùa tựu trường.', 'SP05', 'QL01'),
('KM06', N'Mừng sinh nhật', 12, '2025-10-01', '2025-10-10', N'Chương trình khuyến mãi đặc biệt mừng sinh nhật.', 'SP06', 'QL02'),
('KM07', N'Sale tháng 11', 7, '2025-11-01', '2025-11-15', N'Đợt giảm giá lớn trong tháng 11.', 'SP07', 'QL02'),
('KM08', N'Xmas Sale', 18, '2025-12-01', '2025-12-25', N'Khuyến mãi đặc biệt mừng Giáng sinh, giảm giá mạnh.', 'SP08', 'QL02'),
('KM09', N'Tết Sale', 9, '2026-01-01', '2026-01-10', N'Giảm giá mừng Tết Nguyên Đán.', 'SP09', 'QL01'),
('KM10', N'Valentine Deal', 6, '2026-02-01', '2026-02-14', N'Ưu đãi ngọt ngào dành cho ngày Lễ Tình Nhân Valentine.', 'SP10', 'QL01');

-- NhanVien
INSERT INTO NhanVien (ma, ngayVaoLam, luong, caLam, hoTen, sdt, email, namSinh, diaChi, maNQL)
VALUES
('NV01', '2023-01-01', 10000000, N'T4 (22:00-06:00)', N'Lê Văn A', '0911000001', 'a@gmail.com', '1995-01-01', N'123 Đường A', 'QL01'),
('NV02', '2023-02-01', 9500000,  N'CN (07:00-11:00)', N'Trần Thị B', '0911000002', 'b@gmail.com', '1996-02-02', N'234 Đường B', 'QL01'),
('NV03', '2023-03-01', 11000000, N'T2 (13:00-17:00)', N'Nguyễn Văn C', '0911000003', 'c@gmail.com', '1997-03-03', N'345 Đường C', 'QL01'),
('NV04', '2023-04-01', 9000000,  N'T3 (17:30-21:30)', N'Lê Thị D', '0911000004', 'd@gmail.com', '1998-04-04', N'456 Đường D', 'QL01'),
('NV05', '2023-05-01', 10500000, N'T4 (22:00-06:00)', N'Phạm Văn E', '0911000005', 'e@gmail.com', '1999-05-05', N'567 Đường E', 'QL01'),
('NV06', '2023-06-01', 9700000,  N'CN (07:00-11:00)', N'Huỳnh Thị F', '0911000006', 'f@gmail.com', '1994-06-06', N'678 Đường F', 'QL01'),
('NV07', '2023-07-01', 10200000, N'T2 (13:00-17:00)', N'Đỗ Văn G', '0911000007', 'g@gmail.com', '1993-07-07', N'789 Đường G', 'QL01'),
('NV08', '2023-08-01', 9800000,  N'T3 (17:30-21:30)', N'Vũ Thị H', '0911000008', 'h@gmail.com', '1992-08-08', N'890 Đường H', 'QL01'),
('NV09', '2023-09-01', 10800000, N'T4 (22:00-06:00)', N'Mai Văn I', '0911000009', 'i@gmail.com', '1991-09-09', N'901 Đường I', 'QL01'),
('NV10', '2023-10-01', 9500000,  N'CN (07:00-11:00)', N'Phan Thị K', '0911000010', 'k@gmail.com', '1990-10-10', N'012 Đường K', 'QL01');

-- KhachHang
INSERT INTO KhachHang (ma, ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, hoTen, sdt, email, namSinh, diaChi, maNV)
VALUES
('KH01', '2024-03-01', 50, 1, 3, N'Ngô Thị Chinh', '0987654321', 'chinh@gmail.com', '2000-10-10', N'789 Nguyễn Trãi', 'NV01'),
('KH02', '2024-03-05', 20, 2, 2, N'Trần Văn Dũng', '0911122233', 'dung@gmail.com', '1998-09-09', N'45 Cách Mạng Tháng 8', 'NV02'),
('KH03', '2024-03-10', 35, 1, 1, N'Lê Thị Hoa', '0911223344', 'hoa.kh@gmail.com', '1997-08-08', N'67 Lê Duẩn', 'NV03'),
('KH04', '2024-03-15', 40, 2, 3, N'Nguyễn Văn Minh', '0911334455', 'minh.kh@gmail.com', '1996-07-07', N'89 Võ Thị Sáu', 'NV04'),
('KH05', '2024-03-20', 15, 1, 2, N'Phạm Thị Lan', '0911445566', 'lan.kh@gmail.com', '1995-06-06', N'123 Lê Lợi', 'NV05'),
('KH06', '2024-03-25', 60, 2, 3, N'Hoàng Văn Nam', '0911556677', 'nam.kh@gmail.com', '1994-05-05', N'234 Trần Hưng Đạo', 'NV01'),
('KH07', '2024-03-30', 55, 1, 2, N'Đặng Thị Yến', '0911667788', 'yen.kh@gmail.com', '1993-04-04', '345 Pasteur', 'NV02'),
('KH08', '2024-04-01', 30, 2, 1, N'Vũ Văn Hùng', '0911778899', 'hung.kh@gmail.com', '1992-03-03', N'456 Nguyễn Trãi', 'NV03'),
('KH09', '2024-04-05', 45, 1, 2, N'Bùi Thị Hòa', '0911889900', 'hoa.bui@gmail.com', '1991-02-02', N'567 Điện Biên Phủ', 'NV04'),
('KH10', '2024-04-10', 25, 2, 3, N'Trịnh Văn Khải', '0911990011', 'khoi.kh@gmail.com', '1990-01-01', N'678 Phạm Văn Đồng', 'NV05');

-- HoaDon
INSERT INTO HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV)
VALUES
('HD01', '2025-04-22', 1, 10000, 9000, 10000, 1000, 9, 'KH01', 'NV01'),
('HD02', '2025-04-22', 2, 10500, 9975, 10000, 25, 7, 'KH02', 'NV02'),
('HD03', '2025-04-22', 1, 32000, 27200, 30000, 2800, 13, 'KH03', 'NV03'),
('HD04', '2025-04-21', 3, 15000, 12000, 15000, 3000, 12, 'KH04', 'NV04'),
('HD05', '2025-04-20', 2, 9000, 8280, 10000, 1720, 10, 'KH05', 'NV05'),
('HD06', '2025-04-20', 1, 6000, 5280, 6000, 720, 50, 'KH06', 'NV01'),
('HD07', '2025-04-19', 2, 12000, 11160, 12000, 840, 11, 'KH07', 'NV02'),
('HD08', '2025-04-19', 1, 25000, 20500, 25000, 4500, 20, 'KH08', 'NV03'),
('HD09', '2025-04-18', 2, 5000, 4550, 5000, 450, 45, 'KH09', 'NV04'),
('HD10', '2025-04-17', 1, 8500, 7990, 8000, 10, 3, 'KH10', 'NV05');

-- ChiTietHoaDon
INSERT INTO ChiTietHoaDon (soLuong, donGia, maHD, maSP, maKM)
VALUES
(1, 10000, 'HD01', 'SP01', 'KM01'),
(1, 10500, 'HD02', 'SP02', 'KM02'),
(1, 32000, 'HD03', 'SP03', 'KM03'),
(1, 15000, 'HD04', 'SP04', 'KM04'),
(1, 9000, 'HD05', 'SP05', 'KM05'),
(1, 6000, 'HD06', 'SP06', 'KM06'),
(1, 12000, 'HD07', 'SP07', 'KM07'),
(1, 25000, 'HD08', 'SP08', 'KM08'),
(1, 5000, 'HD09', 'SP09', 'KM09'),
(1, 8500, 'HD10', 'SP10', 'KM10');

select * from [dbo].[ChiTietHoaDon]
select * from [dbo].[HoaDon]
select * from [dbo].[KhachHang]
select * from [dbo].[KhuyenMai]
select * from [dbo].[LoaiSanPham]
select * from [dbo].[NguoiQuanLy]
select * from [dbo].[NhaCungCap]
select * from [dbo].[NhanHang]
select * from [dbo].[NhanVien]
select * from [dbo].[SanPham]
select * from [dbo].[TaiKhoan] 

