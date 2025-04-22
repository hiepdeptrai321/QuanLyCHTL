create database QuanLyCHTL
use QuanLyCHTL
drop database QuanLyCHTL

-- -----------------------------
-- T?O B?NG H? TH?NG C?A HÀNG TI?N L?I
-- -----------------------------

CREATE TABLE LoaiSanPham (
    maLoai VARCHAR(50) PRIMARY KEY,
    tenLoai VARCHAR(50)
)

CREATE TABLE NhanHang (
    maNH VARCHAR(50) PRIMARY KEY,
    tenNH VARCHAR(50)
)

CREATE TABLE NhaCungCap (
    maNCC VARCHAR(50) PRIMARY KEY,
    tenNCC VARCHAR(100),
    diaChi VARCHAR(255),
    soDienThoai VARCHAR(50),
    xepLoai INTEGER
)

CREATE TABLE TaiKhoan (
    maTK VARCHAR(50) PRIMARY KEY,
    tenDN VARCHAR(35),
    matKhau VARCHAR(35),
    vaiTro VARCHAR(50)
)

CREATE TABLE NguoiQuanLy (
    ma VARCHAR(50) PRIMARY KEY,
    capBac VARCHAR(10),
    phuCap FLOAT,
    hoTen VARCHAR(50),
    sdt VARCHAR(12),
    email VARCHAR(50),
    namSinh DATE,
    diaChi VARCHAR(255),
    maTK VARCHAR(50)
    FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
)

CREATE TABLE SanPham (
    maSP VARCHAR(50) PRIMARY KEY,
    tenSP VARCHAR(100),
    giaBan FLOAT,
    giaGoc FLOAT,
    maNH VARCHAR(50),
    maLoai VARCHAR(50),
    maNCC VARCHAR(50),
    maNQL VARCHAR(50),
    FOREIGN KEY (maNH) REFERENCES NhanHang(maNH),
    FOREIGN KEY (maLoai) REFERENCES LoaiSanPham(maLoai),
    FOREIGN KEY (maNCC) REFERENCES NhaCungCap(maNCC),
    FOREIGN KEY (maNQL) REFERENCES NguoiQuanLy(ma)
)

CREATE TABLE KhuyenMai (
    maKM VARCHAR(50) PRIMARY KEY,
    giaTriGiam FLOAT,
    ngayBatDau DATE,
    ngayKetThuc DATE,
    moTa VARCHAR(255),
    maSP VARCHAR(50),
    maNQL VARCHAR(50),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP),
    FOREIGN KEY (maNQL) REFERENCES NguoiQuanLy(ma)
)

CREATE TABLE NhanVien (
    ma VARCHAR(50) PRIMARY KEY,
    ngayVaoLam DATE,
    luong FLOAT,
    caLam DATE,
    hoTen VARCHAR(50),
    sdt VARCHAR(12),
    email VARCHAR(50),
    namSinh DATE,
    diaChi VARCHAR(255),
    maNQL VARCHAR(50),
    maTK VARCHAR(50),
    FOREIGN KEY (maNQL) REFERENCES NguoiQuanLy(ma),
    FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
)

CREATE TABLE KhachHang (
    ma VARCHAR(50) PRIMARY KEY,
    ngayDangKy DATE,
    diemTichLuy INTEGER,
    hangThanhVien INTEGER,
    soLanMuaHang INTEGER,
    hoTen VARCHAR(50),
    sdt VARCHAR(50),
    email VARCHAR(50),
    namSinh DATE,
    diaChi VARCHAR(255),
    maNV VARCHAR(50),
    FOREIGN KEY (maNV) REFERENCES NhanVien(ma)
)

CREATE TABLE HoaDon (
    maHD VARCHAR(50) PRIMARY KEY,
    ngayLap DATE,
    quay INTEGER,
    tongTien FLOAT,
    thanhTien FLOAT,
    tienNhan FLOAT,
    tienThoi FLOAT,
    tongSoLuongSP INTEGER,
    maKH VARCHAR(50),
    maNV VARCHAR(50),
    FOREIGN KEY (maKH) REFERENCES KhachHang(ma),
    FOREIGN KEY (maNV) REFERENCES NhanVien(ma)
)

CREATE TABLE ChiTietHoaDon (
    soLuong INTEGER,
    donGia FLOAT,
    maHD VARCHAR(50),
    maSP VARCHAR(50),
    maKM VARCHAR(50),
    PRIMARY KEY (maHD, maSP),
    FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    FOREIGN KEY (maSP) REFERENCES SanPham(maSP),
    FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
)

-- -----------------------------
-- D? LI?U M?U
-- -----------------------------
--LoaiSanPham
INSERT INTO LoaiSanPham 
VALUES ('LSP01', 'N??c gi?i khát'),
('LSP02', 'Bánh k?o'),
('LSP03', 'Mì ?n li?n'),
('LSP04', 'S?a'),
('LSP05', 'Gia v?'),
('LSP06', '?? h?p'),
('LSP07', 'Th?c ph?m ?ông l?nh'),
('LSP08', 'Trái cây'),
('LSP09', 'Rau c?'),
('LSP10', '?? ?n nhanh')

--NhanHang
INSERT INTO NhanHang 
VALUES ('NH01', 'Pepsi'),
('NH02', 'Coca-Cola'),
('NH03', 'Vinamilk'),
('NH04', 'Oreo'),
('NH05', 'Milo'),
('NH06', 'Omachi'),
('NH07', 'Chinsu'),
('NH08', 'Vedan'),
('NH09', 'Acecook'),
('NH10', 'Nutifood')

--NhaCungCap
INSERT INTO NhaCungCap 
VALUES 
('NCC01', 'Công ty N??c gi?i khát VN', '123 ???ng N??c, TP.HCM', '0889285559', 5),
('NCC02', 'Công ty Th?c ph?m ABC', '12 Tr?n H?ng ??o, Hà N?i', '0912345678', 4),
('NCC03', 'Công ty Vinamilk', '1A Nguy?n Hu?, TP.HCM', '0987654321', 5),
('NCC04', 'Công ty Nestle', '34 Lý Th??ng Ki?t, ?à N?ng', '0909123456', 5),
('NCC05', 'Công ty Ajinomoto', '56 Ph?m V?n ??ng, Hà N?i', '0978123456', 4),
('NCC06', 'Công ty Masan', '789 CMT8, TP.HCM', '0968345678', 5),
('NCC07', 'Công ty Bánh k?o Kinh ?ô', '234 Nguy?n Trãi, Hà N?i', '0912233445', 4),
('NCC08', 'Công ty Acecook', '65 Võ V?n T?n, TP.HCM', '0909988776', 5),
('NCC09', 'Công ty NutiFood', '9 Lý Chính Th?ng, C?n Th?', '0888666555', 3),
('NCC10', 'Công ty Vissan', '100 Lê Du?n, TP.HCM', '0833221144', 4)


--TaiKhoan
INSERT INTO TaiKhoan VALUES 
('TK01', 'admin', 'admin123', 'admin'),
('TK02', 'qly2', 'ql456', 'quanly'),
('TK03', 'qly1', 'ql123', 'quanly'),
('TK04', 'nhanvien02', 'nv456', 'nhanvien'),
('TK05', 'nhanvien01', 'nv123', 'nhanvien'),
('TK06', 'nhanvien03', 'nv631', 'nhanvien'),
('TK07', 'nhanvien04', 'nv333', 'nhanvien'),
('TK08', 'nhanvien05', 'nv369', 'nhanvien')



-- NguoiQuanLy 
INSERT INTO NguoiQuanLy (ma, capBac, phuCap, hoTen, sdt, email, namSinh, diaChi, maTK) VALUES
('QL01', 'A1', 1000000, 'Nguy?n V?n An', '0912345678', 'an@gmail.com', '2005-01-01', '1 Lê L?i', 'TK02'),
('QL02', 'A2', 1100000, 'Tr?n Th? Bình', '0912345679', 'binh@gmail.com', '1990-02-02', '2 Nguy?n Hu?', 'TK03')


-- SanPham 
INSERT INTO SanPham (maSP, tenSP, giaBan, giaGoc, maNH, maLoai, maNCC, maNQL) VALUES
('SP01', 'Pepsi lon 330ml', 10000, 8000, 'NH01', 'LSP01', 'NCC01', 'QL01'),
('SP02', 'Coca lon 330ml', 10500, 8500, 'NH02', 'LSP01', 'NCC02', 'QL01'), 
('SP03', 'S?a t??i Vinamilk 1L', 32000, 28000, 'NH03', 'LSP04', 'NCC03', 'QL02'),
('SP04', 'Bánh Oreo 133g', 15000, 12000, 'NH04', 'LSP02', 'NCC04', 'QL02'),
('SP05', 'S?a Milo h?p 180ml', 9000, 7000, 'NH05', 'LSP04', 'NCC05', 'QL01'),
('SP06', 'Mì Omachi tôm chua cay', 6000, 4500, 'NH06', 'LSP03', 'NCC06', 'QL01'),
('SP07', 'N??c t??ng Chinsu', 12000, 10000, 'NH07', 'LSP05', 'NCC06', 'QL02'),
('SP08', 'B?t ng?t Vedan 400g', 25000, 22000, 'NH08', 'LSP05', 'NCC07', 'QL02'), 
('SP09', 'Mì H?o H?o tôm chua cay', 5000, 4000, 'NH09', 'LSP03', 'NCC08', 'QL02'),
('SP10', 'S?a NutiFood 180ml', 8500, 6500, 'NH10', 'LSP04', 'NCC09', 'QL02')

-- KhuyenMai 
INSERT INTO KhuyenMai (maKM, giaTriGiam, ngayBatDau, ngayKetThuc, moTa, maSP, maNQL) VALUES
('KM01', 10, '2025-05-01', '2025-05-10', 'Gi?m giá mùa hè', 'SP01', 'QL01'),
('KM02', 5, '2025-06-01', '2025-06-15', '?u ?ãi hè', 'SP02', 'QL01'),
('KM03', 15, '2025-07-01', '2025-07-10', 'Gi?m s?c', 'SP03', 'QL02'),
('KM04', 20, '2025-08-01', '2025-08-05', 'Flash Sale', 'SP04', 'QL02'),
('KM05', 8, '2025-09-01', '2025-09-07', 'Back to School', 'SP05', 'QL01'),
('KM06', 12, '2025-10-01', '2025-10-10', 'M?ng sinh nh?t', 'SP06', 'QL02'),
('KM07', 7, '2025-11-01', '2025-11-15', 'Sale tháng 11', 'SP07', 'QL02'),
('KM08', 18, '2025-12-01', '2025-12-25', 'Xmas Sale', 'SP08', 'QL02'),
('KM09', 9, '2026-01-01', '2026-01-10', 'T?t Sale', 'SP09', 'QL01'),
('KM10', 6, '2026-02-01', '2026-02-14', 'Valentine Deal', 'SP10', 'QL01')

-- NhanVien 
INSERT INTO NhanVien (ma, ngayVaoLam, luong, caLam, hoTen, sdt, email, namSinh, diaChi, maNQL, maTK) VALUES
('NV01', '2024-01-01', 6000000, '2024-04-01', 'Tr?n V?n B?o', '0909876543', 'bao@gmail.com', '1995-06-01', '456 Lê Lai', 'QL01', 'TK04'),
('NV02', '2024-02-01', 6500000, '2024-05-01', 'Lê Th? Mai', '0909123456', 'mai@gmail.com', '1994-07-02', '789 Tr?n Quang Kh?i', 'QL01', 'TK05'),
('NV03', '2024-03-01', 6200000, '2024-06-01', 'Nguy?n V?n Hòa', '0909234567', 'hoa@gmail.com', '1993-08-03', '12 Nguy?n Th? Minh Khai', 'QL01', 'TK06'),
('NV04', '2024-04-01', 6400000, '2024-07-01', 'Ph?m Th? Tuy?t', '0909345678', 'tuyet@gmail.com', '1992-09-04', '34 Pasteur', 'QL02', 'TK07'),
('NV05', '2024-05-01', 6300000, '2024-08-01', 'Hoàng V?n Khánh', '0909456789', 'khanh@gmail.com', '1991-10-05', '56 Nguy?n Trãi', 'QL02', 'TK08')

-- KhachHang 
INSERT INTO KhachHang (ma, ngayDangKy, diemTichLuy, hangThanhVien, soLanMuaHang, hoTen, sdt, email, namSinh, diaChi, maNV) VALUES
('KH01', '2024-03-01', 50, 1, 3, 'Ngô Th? Chinh', '0987654321', 'chinh@gmail.com', '2000-10-10', '789 Nguy?n Trãi', 'NV01'),
('KH02', '2024-03-05', 20, 2, 2, 'Tr?n V?n D?ng', '0911122233', 'dung@gmail.com', '1998-09-09', '45 Cách M?ng Tháng 8', 'NV02'),
('KH03', '2024-03-10', 35, 1, 1, 'Lê Th? Hoa', '0911223344', 'hoa.kh@gmail.com', '1997-08-08', '67 Lê Du?n', 'NV03'),
('KH04', '2024-03-15', 40, 2, 3, 'Nguy?n V?n Minh', '0911334455', 'minh.kh@gmail.com', '1996-07-07', '89 Võ Th? Sáu', 'NV04'),
('KH05', '2024-03-20', 15, 1, 2, 'Ph?m Th? Lan', '0911445566', 'lan.kh@gmail.com', '1995-06-06', '123 Lê L?i', 'NV05'),
('KH06', '2024-03-25', 60, 2, 3, 'Hoàng V?n Nam', '0911556677', 'nam.kh@gmail.com', '1994-05-05', '234 Tr?n H?ng ??o', 'NV01'),
('KH07', '2024-03-30', 55, 1, 2, '??ng Th? Y?n', '0911667788', 'yen.kh@gmail.com', '1993-04-04', '345 Pasteur', 'NV02'),
('KH08', '2024-04-01', 30, 2, 1, 'V? V?n H?ng', '0911778899', 'hung.kh@gmail.com', '1992-03-03', '456 Nguy?n Trãi', 'NV03'),
('KH09', '2024-04-05', 45, 1, 2, 'Bùi Th? Hòa', '0911889900', 'hoa.bui@gmail.com', '1991-02-02', '567 ?i?n Biên Ph?', 'NV04'), 
('KH10', '2024-04-10', 25, 2, 3, 'Tr?nh V?n Khôi', '0911990011', 'khoi.kh@gmail.com', '1990-01-01', '678 Ph?m V?n ??ng', 'NV05')

-- HoaDon (Khóa ngo?i maKH và maNV ?ã h?p l?)
INSERT INTO HoaDon (maHD, ngayLap, quay, tongTien, thanhTien, tienNhan, tienThoi, tongSoLuongSP, maKH, maNV) VALUES
('HD01', '2025-04-22', 1, 10000, 9000, 10000, 1000, 1, 'KH01', 'NV01'),
('HD02', '2025-04-22', 2, 10500, 9975, 10000, 25, 1, 'KH02', 'NV02'), 
('HD03', '2025-04-22', 1, 32000, 27200, 30000, 2800, 1, 'KH03', 'NV03'),
('HD04', '2025-04-21', 3, 15000, 12000, 15000, 3000, 1, 'KH04', 'NV04'), 
('HD05', '2025-04-20', 2, 9000, 8280, 10000, 1720, 1, 'KH05', 'NV05'),  
('HD06', '2025-04-20', 1, 6000, 5280, 6000, 720, 1, 'KH06', 'NV01'),   
('HD07', '2025-04-19', 2, 12000, 11160, 12000, 840, 1, 'KH07', 'NV02'), 
('HD08', '2025-04-19', 1, 25000, 20500, 25000, 4500, 1, 'KH08', 'NV03'), 
('HD09', '2025-04-18', 2, 5000, 4550, 5000, 450, 1, 'KH09', 'NV04'),   
('HD10', '2025-04-17', 1, 8500, 7990, 8000, 10, 1, 'KH10', 'NV05')   


-- ChiTietHoaDon 
INSERT INTO ChiTietHoaDon (soLuong, donGia, maHD, maSP, maKM) VALUES
(1, 10000, 'HD01', 'SP01', 'KM01'),
(1, 10500, 'HD02', 'SP02', 'KM02'), 
(1, 32000, 'HD03', 'SP03', 'KM03'), 
(1, 15000, 'HD04', 'SP04', 'KM04'), 
(1, 9000, 'HD05', 'SP05', 'KM05'),  
(1, 6000, 'HD06', 'SP06', 'KM06'),  
(1, 12000, 'HD07', 'SP07', 'KM07'), 
(1, 25000, 'HD08', 'SP08', 'KM08'),
(1, 5000, 'HD09', 'SP09', 'KM09'),  
(1, 8500, 'HD10', 'SP10', 'KM10')


