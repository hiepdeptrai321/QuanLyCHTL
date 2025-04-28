create database QuanLyCHTL
go
use QuanLyCHTL
go
--drop database
--use master
--ALTER DATABASE QuanLyCHTL
--SET SINGLE_USER
--WITH ROLLBACK IMMEDIATE;
--GO
--drop database QuanLyCHTL

-- -----------------------------
-- Tạo bảng
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
            WHEN LEFT(i.ma, 3) = 'NQL' THEN 'quanly'
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
VALUES	
('LSP01', N'Nước giải khát'),
('LSP02', N'Bánh kẹo'),
('LSP03', N'Mì ăn liền'),
('LSP04', N'Sữa'),
('LSP05', N'Gia vị'),
('LSP06', N'Đồ hộp'),
('LSP07', N'Thực phẩm đông lạnh'),
('LSP08', N'Trái cây'),
('LSP09', N'Rau củ'),
('LSP10', N'Đồ ăn nhanh'),
('LSP11', N'Thực phẩm khô'),
('LSP12', N'Đồ dùng gia đình'),
('LSP13', N'Thực phẩm tươi sống'),
('LSP14', N'Nước uống có cồn'),
('LSP15', N'Thực phẩm chức năng'),
('LSP16', N'Đồ ăn vặt'),
('LSP17', N'Thực phẩm hữu cơ'),
('LSP18', N'Đồ dùng cá nhân'),
('LSP19', N'Bánh mì, bánh ngọt'),
('LSP20', N'Gia cầm, thịt heo, thịt bò');
-- NhanHang
INSERT INTO NhanHang (maNH, tenNH)
VALUES 
('NH01', N'Coca-Cola'),
('NH02', N'Pepsi'),
('NH03', N'Orion'),
('NH04', N'Kinh Đô'),
('NH05', N'Hảo Hảo'),
('NH06', N'Omachi'),
('NH07', N'Vinamilk'),
('NH08', N'TH True Milk'),
('NH09', N'Nam Dương'),
('NH10', N'Aji-ngon'),
('NH11', N'Vissan'),
('NH12', N'Bình Tây'),
('NH13', N'CP Foods'),
('NH14', N'Satrafoods'),
('NH15', N'Dalat Farm'),
('NH16', N'Vinfruits'),
('NH17', N'Rau Củ Sạch Đà Lạt'),
('NH18', N'Organica'),
('NH19', N'Lotteria'),
('NH20', N'KFC'),
('NH21', N'Tân Tân'),
('NH22', N'Vifon'),
('NH23', N'Lock&Lock'),
('NH24', N'Supor'),
('NH25', N'Vissan Fresh'),
('NH26', N'CP Fresh'),
('NH27', N'Heineken'),
('NH28', N'Tiger'),
('NH29', N'Herbalife'),
('NH30', N'Nutrilite'),
('NH31', N'Poca'),
('NH32', N'Lay''s'),
('NH33', N'Siêu thị Organica'),
('NH34', N'Thực phẩm 5K'),
('NH35', N'Unilever'),
('NH36', N'P&G'),
('NH37', N'BreadTalk'),
('NH38', N'ABC Bakery'),
('NH39', N'Vissan Meat'),
('NH40', N'CP Meat');


INSERT INTO NhaCungCap (maNCC, tenNCC, diaChi, soDienThoai, xepLoai)
VALUES
('NCC01', N'Coca-Cola Vietnam', N'Quận 7, TP.HCM', '0909123456', 1),
('NCC02', N'PepsiCo Vietnam', N'Quận Thủ Đức, TP.HCM', '0909876543', 1),
('NCC03', N'Orion Food Vina', N'Bình Dương', '0912345678', 2),
('NCC04', N'Kinh Đô Mondelez', N'Tân Phú, TP.HCM', '0911223344', 1),
('NCC05', N'Acecook Vietnam', N'Vĩnh Lộc, Bình Chánh, TP.HCM', '0922334455', 1),
('NCC06', N'Vinamilk', N'Bình Thạnh, TP.HCM', '0933445566', 1),
('NCC07', N'TH True Milk', N'Nghệ An', '0944556677', 2),
('NCC08', N'CP Vietnam', N'Đồng Nai', '0955667788', 1),
('NCC09', N'Satrafoods', N'Quận 1, TP.HCM', '0966778899', 2),
('NCC10', N'Vissan', N'Quận 7, TP.HCM', '0977889900', 1),
('NCC11', N'Heineken Vietnam', N'Quận 12, TP.HCM', '0988990011', 1),
('NCC12', N'Unilever Vietnam', N'Quận 7, TP.HCM', '0908001122', 1),
('NCC13', N'P&G Vietnam', N'Quận 10, TP.HCM', '0911002233', 1),
('NCC14', N'Organica', N'Quận 2, TP.HCM', '0922003344', 2),
('NCC15', N'BreadTalk Vietnam', N'Quận 3, TP.HCM', '0933004455', 2);

-- NguoiQuanLy
INSERT INTO NguoiQuanLy (ma, capBac, phuCap, hoTen, sdt, email, namSinh, diaChi)
VALUES
('NQL01', 'A1', 1000000, N'Đỗ Phú Hiệp', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi'),
('NQL02', 'A1', 1000000, N'Hoàng Phước Thành Công', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi'),
('NQL03', 'A1', 1000000, N'Đàm Thái An', '0912345678', 'an@gmail.com', '2005-01-01', N'1 Lê Lợi')




-- SanPham
INSERT INTO SanPham (maSP, tenSP, giaGoc, giaBan, maNH, maLoai, maNCC, maNQL)
VALUES
('SP01', N'Coca-Cola lon 330ml', 9500, 11000, 'NH01', 'LSP01', 'NCC01', 'NQL01'),
('SP02', N'Pepsi chai 390ml', 9000, 10000, 'NH02', 'LSP01', 'NCC02', 'NQL01'),
('SP03', N'Bánh Chocopie hộp 12 cái', 42000, 48000, 'NH03', 'LSP02', 'NCC03', 'NQL01'),
('SP04', N'Bánh bông lan Kinh Đô', 25000, 30000, 'NH04', 'LSP02', 'NCC04', 'NQL01'),
('SP05', N'Mì Hảo Hảo tôm chua cay', 3500, 4000, 'NH05', 'LSP03', 'NCC05', 'NQL02'),
('SP06', N'Mì Omachi sườn hầm', 5500, 6000, 'NH06', 'LSP03', 'NCC05', 'NQL02'),
('SP07', N'Sữa tươi Vinamilk 1L', 29000, 32000, 'NH07', 'LSP04', 'NCC06', 'NQL02'),
('SP08', N'Sữa tươi TH True Milk 1L', 31000, 35000, 'NH08', 'LSP04', 'NCC07', 'NQL02'),
('SP09', N'Nước tương Nam Dương 500ml', 16000, 18000, 'NH09', 'LSP05', 'NCC05', 'NQL03'),
('SP10', N'Hạt nêm Aji-ngon 400g', 36000, 40000, 'NH10', 'LSP05', 'NCC05', 'NQL03'),
('SP11', N'Pate Vissan hộp 170g', 21000, 24000, 'NH11', 'LSP06', 'NCC10', 'NQL03'),
('SP12', N'Cá hộp Bình Tây 155g', 22000, 25000, 'NH12', 'LSP06', 'NCC10', 'NQL03'),
('SP13', N'Gà rán CP Foods gói 500g', 87000, 95000, 'NH13', 'LSP07', 'NCC08', 'NQL01'),
('SP14', N'Xúc xích Satrafoods 500g', 69000, 75000, 'NH14', 'LSP07', 'NCC09', 'NQL01'),
('SP15', N'Cam sành Dalat Farm', 40000, 45000, 'NH15', 'LSP08', 'NCC14', 'NQL01'),
('SP16', N'Táo Mỹ Vinfruits', 65000, 70000, 'NH16', 'LSP08', 'NCC14', 'NQL01'),
('SP17', N'Rau cải xanh Đà Lạt', 22000, 25000, 'NH17', 'LSP09', 'NCC14', 'NQL01'),
('SP18', N'Cà rốt Organica', 30000, 33000, 'NH18', 'LSP09', 'NCC14', 'NQL01'),
('SP19', N'Burger gà Lotteria', 45000, 50000, 'NH19', 'LSP10', 'NCC09', 'NQL01'),
('SP20', N'Gà rán KFC phần 2 miếng', 68000, 75000, 'NH20', 'LSP10', 'NCC09', 'NQL02'),
('SP21', N'Hạt điều Tân Tân hộp 250g', 110000, 120000, 'NH21', 'LSP11', 'NCC03', 'NQL02'),
('SP22', N'Phở ăn liền Vifon gói', 6000, 7000, 'NH22', 'LSP11', 'NCC05', 'NQL02'),
('SP23', N'Bình giữ nhiệt Lock&Lock 500ml', 270000, 300000, 'NH23', 'LSP12', 'NCC12', 'NQL02'),
('SP24', N'Nồi chiên không dầu Supor 3.5L', 1200000, 1350000, 'NH24', 'LSP12', 'NCC12', 'NQL02'),
('SP25', N'Thịt heo Vissan Fresh 500g', 85000, 90000, 'NH25', 'LSP13', 'NCC10', 'NQL02'),
('SP26', N'Thịt gà CP Fresh 500g', 90000, 95000, 'NH26', 'LSP13', 'NCC08', 'NQL02'),
('SP27', N'Bia Heineken lon 330ml', 18000, 20000, 'NH27', 'LSP14', 'NCC11', 'NQL02'),
('SP28', N'Bia Tiger lon 330ml', 17000, 19000, 'NH28', 'LSP14', 'NCC11', 'NQL02'),
('SP29', N'Thực phẩm chức năng Herbalife F1', 850000, 900000, 'NH29', 'LSP15', 'NCC12', 'NQL02'),
('SP30', N'Thực phẩm chức năng Nutrilite Vitamin C', 650000, 700000, 'NH30', 'LSP15', 'NCC13', 'NQL02'),
('SP31', N'Coca-Cola Zero lon 330ml', 10000, 11500, 'NH01', 'LSP01', 'NCC01', 'NQL02'),
('SP32', N'Pepsi Black chai 390ml', 9500, 10500, 'NH02', 'LSP01', 'NCC02', 'NQL01'),
('SP33', N'Bánh Custas kem trứng', 38000, 43000, 'NH03', 'LSP02', 'NCC03', 'NQL01'),
('SP34', N'Bánh quy Cosy vị bơ', 45000, 50000, 'NH04', 'LSP02', 'NCC04', 'NQL01'),
('SP35', N'Mì Modern vị bò hầm', 4000, 4500, 'NH05', 'LSP03', 'NCC05', 'NQL02'),
('SP36', N'Mì Omachi xốt spaghetti', 7000, 7500, 'NH06', 'LSP03', 'NCC05', 'NQL02'),
('SP37', N'Sữa Vinamilk ADM 180ml', 7000, 8000, 'NH07', 'LSP04', 'NCC06', 'NQL02'),
('SP38', N'Sữa TH True Milk Socola 180ml', 8000, 9000, 'NH08', 'LSP04', 'NCC07', 'NQL02'),
('SP39', N'Nước mắm Nam Ngư 500ml', 20000, 23000, 'NH09', 'LSP05', 'NCC05', 'NQL03'),
('SP40', N'Bột ngọt Ajinomoto 400g', 36000, 40000, 'NH10', 'LSP05', 'NCC05', 'NQL03'),
('SP41', N'Cá mòi hộp Vissan 155g', 21000, 25000, 'NH01', 'LSP06', 'NCC10', 'NQL03'),
('SP42', N'Pate Gan heo Vissan 170g', 23000, 26000, 'NH02', 'LSP06', 'NCC10', 'NQL03'),
('SP43', N'Cá hộp sốt cà Bình Tây', 22000, 25000, 'NH03', 'LSP06', 'NCC10', 'NQL03'),
('SP44', N'Xúc xích CP Foods gói 400g', 70000, 77000, 'NH04', 'LSP07', 'NCC08', 'NQL02'),
('SP45', N'Xúc xích Satrafoods heo 300g', 55000, 60000, 'NH05', 'LSP07', 'NCC09', 'NQL02'),
('SP46', N'Nho đỏ Mỹ không hạt', 95000, 105000, 'NH06', 'LSP08', 'NCC14', 'NQL02'),
('SP47', N'Dưa lưới nội địa', 70000, 80000, 'NH07', 'LSP08', 'NCC14', 'NQL02'),
('SP48', N'Xà lách Mỹ Đà Lạt', 35000, 38000, 'NH08', 'LSP09', 'NCC14', 'NQL03'),
('SP49', N'Củ cải trắng Organica', 27000, 30000, 'NH09', 'LSP09', 'NCC14', 'NQL03'),
('SP50', N'Gà viên Lotteria', 40000, 45000, 'NH01', 'LSP10', 'NCC09', 'NQL03'),
('SP51', N'Khoai tây chiên KFC size vừa', 30000, 35000, 'NH02', 'LSP10', 'NCC09', 'NQL03'),
('SP52', N'Hạt điều rang muối Tân Tân 250g', 120000, 130000, 'NH03', 'LSP11', 'NCC03', 'NQL03'),
('SP53', N'Mì Vifon chay nấm đông cô', 7000, 7500, 'NH04', 'LSP11', 'NCC05', 'NQL03'),
('SP54', N'Bình Lock&Lock dung tích 1L', 290000, 320000, 'NH05', 'LSP12', 'NCC12', 'NQL03'),
('SP55', N'Nồi chiên không dầu Lock&Lock 5.5L', 1800000, 2000000, 'NH06', 'LSP12', 'NCC12', 'NQL02'),
('SP56', N'Thịt ba rọi CP Fresh 500g', 95000, 100000, 'NH07', 'LSP13', 'NCC08', 'NQL02'),
('SP57', N'Thịt vai heo Vissan 500g', 88000, 95000, 'NH08', 'LSP13', 'NCC10', 'NQL02'),
('SP58', N'Bia Heineken bạc lon 330ml', 18500, 20000, 'NH09', 'LSP14', 'NCC11', 'NQL02'),
('SP59', N'Bia Tiger Crystal lon 330ml', 18000, 20000, 'NH01', 'LSP14', 'NCC11', 'NQL02'),
('SP60', N'Sữa Herbalife F2 vitamin', 850000, 900000, 'NH02', 'LSP15', 'NCC12', 'NQL02'),
('SP61', N'Thực phẩm chức năng Nutrilite Omega-3', 680000, 750000, 'NH03', 'LSP15', 'NCC13', 'NQL02'),
('SP62', N'Nước tăng lực Redbull chai 250ml', 12000, 14000, 'NH04', 'LSP01', 'NCC02', 'NQL01'),
('SP63', N'Nước tăng lực Sting dâu 330ml', 11000, 13000, 'NH05', 'LSP01', 'NCC02', 'NQL01'),
('SP64', N'Bánh Oreo vị chocolate', 24000, 28000, 'NH06', 'LSP02', 'NCC03', 'NQL01'),
('SP65', N'Bánh AFC rau cải', 23000, 27000, 'NH07', 'LSP02', 'NCC04', 'NQL01'),
('SP66', N'Mì Kokomi 90 bò cay', 4500, 5000, 'NH08', 'LSP03', 'NCC05', 'NQL02'),
('SP67', N'Mì Reeva tôm chua cay', 5500, 6000, 'NH09', 'LSP03', 'NCC05', 'NQL02'),
('SP68', N'Sữa chua uống Probi dâu 170ml', 7000, 8000, 'NH01', 'LSP04', 'NCC06', 'NQL02'),
('SP69', N'Sữa chua TH True Yogurt 100ml', 6000, 7000, 'NH02', 'LSP04', 'NCC07', 'NQL02'),
('SP70', N'Đường tinh luyện Biên Hòa 1kg', 23000, 27000, 'NH03', 'LSP05', 'NCC05', 'NQL03'),
('SP71', N'Muối i-ốt Bảo Ngọc 500g', 6000, 7000, 'NH04', 'LSP05', 'NCC05', 'NQL03'),
('SP72', N'Cá cơm khô Bình Tây 250g', 70000, 75000, 'NH05', 'LSP06', 'NCC10', 'NQL03'),
('SP73', N'Cá thu sốt cà hộp 155g', 23000, 25000, 'NH06', 'LSP06', 'NCC10', 'NQL03'),
('SP74', N'Gà xào cay CP Foods 400g', 85000, 90000, 'NH07', 'LSP07', 'NCC08', 'NQL01'),
('SP75', N'Thịt bò viên Satrafoods 500g', 110000, 120000, 'NH08', 'LSP07', 'NCC09', 'NQL01'),
('SP76', N'Chôm chôm ruột đỏ', 50000, 60000, 'NH09', 'LSP08', 'NCC14', 'NQL01'),
('SP77', N'Ổi xanh Organica', 40000, 45000, 'NH01', 'LSP08', 'NCC14', 'NQL01'),
('SP78', N'Rau muống Đà Lạt', 25000, 28000, 'NH02', 'LSP09', 'NCC14', 'NQL01'),
('SP79', N'Cải thìa Organica', 26000, 29000, 'NH03', 'LSP09', 'NCC14', 'NQL01'),
('SP80', N'Combo Burger + Khoai KFC', 90000, 100000, 'NH04', 'LSP10', 'NCC09', 'NQL01');


-- KhuyenMai
INSERT INTO KhuyenMai (maKM, tenKM, giaTriGiam, ngayBatDau, ngayKetThuc, moTa, maSP, maNQL)
VALUES
('KM01', N'Giảm giá mùa hè', 10, '2025-05-01', '2025-05-10', N'Chương trình giảm giá đặc biệt chào hè.', 'SP01', 'NQL01'),
('KM02', N'Ưu đãi hè', 5, '2025-06-01', '2025-06-15', N'Ưu đãi hấp dẫn trong mùa hè.', 'SP02', 'NQL03'),
('KM03', N'Giảm sốc', 15, '2025-07-01', '2025-07-10', N'Giảm giá cực sốc trong thời gian ngắn.', 'SP03', 'NQL01'),
('KM04', N'Flash Sale', 20, '2025-08-01', '2025-08-05', N'Khuyến mãi chớp nhoáng với mức giảm giá lớn, chỉ diễn ra trong 5 ngày.', 'SP04', 'NQL03'),
('KM05', N'Back to School', 8, '2025-09-01', '2025-09-07', N'Ưu đãi đặc biệt mừng mùa tựu trường.', 'SP05', 'NQL01'),
('KM06', N'Mừng sinh nhật', 12, '2025-10-01', '2025-10-10', N'Chương trình khuyến mãi đặc biệt mừng sinh nhật.', 'SP06', 'NQL02'),
('KM07', N'Sale tháng 11', 7, '2025-11-01', '2025-11-15', N'Đợt giảm giá lớn trong tháng 11.', 'SP07', 'NQL02'),
('KM08', N'Xmas Sale', 18, '2025-12-01', '2025-12-25', N'Khuyến mãi đặc biệt mừng Giáng sinh, giảm giá mạnh.', 'SP08', 'NQL01'),
('KM09', N'Tết Sale', 9, '2025-01-01', '2025-01-10', N'Giảm giá mừng Tết Nguyên Đán.', 'SP09', 'NQL02'),
('KM10', N'Valentine Deal', 6, '2025-02-01', '2025-02-14', N'Ưu đãi ngọt ngào dành cho ngày Lễ Tình Nhân Valentine.', 'SP10', 'NQL02');

-- NhanVien
INSERT INTO NhanVien (ma, ngayVaoLam, luong, caLam, hoTen, sdt, email, namSinh, diaChi, maNQL)
VALUES
('NV01', '2023-01-01', 10000000, N'T4 (22:00-06:00)', N'Lê Văn Minh', '0911000001', 'a@gmail.com', '1995-01-01', N'123 Đường A', 'NQL01'),
('NV02', '2023-02-01', 9500000,  N'CN (07:00-11:00)', N'Trần Thị Bình', '0911000002', 'b@gmail.com', '1996-02-02', N'234 Đường B', 'NQL01'),
('NV03', '2023-03-01', 11000000, N'T2 (13:00-17:00)', N'Nguyễn Văn Công', '0911000003', 'c@gmail.com', '1997-03-03', N'345 Đường C', 'NQL01'),
('NV04', '2023-04-01', 9000000,  N'T3 (17:30-21:30)', N'Lê Thị Dinh', '0911000004', 'd@gmail.com', '1998-04-04', N'456 Đường D', 'NQL01'),
('NV05', '2023-05-01', 10500000, N'T4 (22:00-06:00)', N'Phạm Văn An', '0911000005', 'e@gmail.com', '1999-05-05', N'567 Đường E', 'NQL01'),
('NV06', '2023-06-01', 9700000,  N'CN (07:00-11:00)', N'Huỳnh Thị Minh', '0911000006', 'f@gmail.com', '1994-06-06', N'678 Đường F', 'NQL01'),
('NV07', '2023-07-01', 10200000, N'T2 (13:00-17:00)', N'Đỗ Văn Lợi', '0911000007', 'g@gmail.com', '1993-07-07', N'789 Đường G', 'NQL01'),
('NV08', '2023-08-01', 9800000,  N'T3 (17:30-21:30)', N'Vũ Thị Hoa', '0911000008', 'h@gmail.com', '1992-08-08', N'890 Đường H', 'NQL01'),
('NV09', '2023-09-01', 10800000, N'T4 (22:00-06:00)', N'Mai Văn Trinh', '0911000009', 'i@gmail.com', '1991-09-09', N'901 Đường I', 'NQL01'),
('NV10', '2023-10-01', 9500000,  N'CN (07:00-11:00)', N'Phan Thị An', '0911000010', 'k@gmail.com', '1990-10-10', N'012 Đường K', 'NQL01');

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
('HD01', '2025-04-22 08:30:00', 1, 10000, 9000, 10000, 1000, 9, 'KH01', 'NV01'),
('HD02', '2025-04-22 09:45:00', 2, 10500, 9975, 10000, 25, 7, 'KH02', 'NV02'),
('HD03', '2025-04-22 10:15:00', 1, 32000, 27200, 30000, 2800, 13, 'KH03', 'NV03'),
('HD04', '2025-04-21 14:20:00', 3, 15000, 12000, 15000, 3000, 12, 'KH04', 'NV04'),
('HD05', '2025-04-20 16:50:00', 2, 9000, 8280, 10000, 1720, 10, 'KH05', 'NV05'),
('HD06', '2025-04-20 18:00:00', 1, 6000, 5280, 6000, 720, 50, 'KH06', 'NV01'),
('HD07', '2025-04-19 07:30:00', 2, 12000, 11160, 12000, 840, 11, 'KH07', 'NV02'),
('HD08', '2025-04-19 09:00:00', 1, 25000, 20500, 25000, 4500, 20, 'KH08', 'NV03'),
('HD09', '2025-04-18 11:40:00', 2, 5000, 4550, 5000, 450, 45, 'KH09', 'NV04'),
('HD10', '2025-04-17 13:25:00', 1, 8500, 7990, 8000, 10, 3, 'KH10', 'NV05');

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

