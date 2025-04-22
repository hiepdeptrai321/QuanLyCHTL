create database QLNV
go
use QLNV
go
CREATE TABLE PhongBan(
   maPhong  VARCHAR (10) primary key,
   tenPhong NVARCHAR (30) NOT NULL,     
);
CREATE TABLE NhanVien(
   maNV  NVARCHAR (30) primary key,
   hoTen NVARCHAR (50)  NULL,
   tuoi int NULL,
   maPhong  VARCHAR (10) NULL,  
   tienLuong float,
   Constraint F_LP_HN Foreign key(maPhong) references PhongBan(maPhong),
);
INSERT PhongBan([maPhong], [tenPhong]) VALUES ('PHONG_TC', N'Phòng tổ chức')
INSERT PhongBan([maPhong], [tenPhong]) VALUES ('PHONG_KT', N'Phòng kỹ thuật')
INSERT PhongBan([maPhong], [tenPhong]) VALUES ('PHONG_NS', N'Phòng nhân sự')

INSERT NhanVien([maNV], [hoTen],[tuoi],[maPhong],[tienLuong]) VALUES ('2101623', N'Phạm Văn Lắm', 30, N'PHONG_TC',900)
INSERT NhanVien([maNV], [hoTen],[tuoi],[maPhong],[tienLuong]) VALUES ('2101436', N'Nguyễn Trường Sinh',28, N'PHONG_KT',450)
INSERT NhanVien([maNV], [hoTen],[tuoi],[maPhong],[tienLuong]) VALUES ('2101657', N'Lý cần Đạt ', 43, N'PHONG_NS',600)

 