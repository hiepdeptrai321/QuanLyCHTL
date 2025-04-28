package entity;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.PdfEncodings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;

import entity.ChiTietHoaDon; // Đảm bảo import đúng package
import entity.HoaDon;
import entity.SanPham;
import dao.SanPham_DAO;   // Đảm bảo import đúng package
import connectDB.ConnectDB; // Đảm bảo import đúng package

/**
 * Lớp tiện ích để xuất thông tin hóa đơn ra file PDF.
 */
public class XuatHoaDonPDF {
    public static final String FONT_PATH = "C:/Windows/Fonts/arial.ttf";
    public boolean xuatHoaDon(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList, String filePath) {

        // 1. Kiểm tra đầu vào
        if (hoaDon == null || filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Lỗi: Dữ liệu hóa đơn hoặc đường dẫn file không hợp lệ.");
            JOptionPane.showMessageDialog(null, "Dữ liệu hóa đơn hoặc đường dẫn file không hợp lệ.", "Lỗi đầu vào", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Đảm bảo file path kết thúc bằng .pdf (có thể tùy chọn)
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        // Khởi tạo DAO để lấy tên sản phẩm (cần có kết nối DB hoạt động)
        SanPham_DAO spDao = null;
        try {
            // Giả định ConnectDB đã được gọi connect() trước đó
            spDao = new SanPham_DAO(ConnectDB.getConnection());
            if (ConnectDB.getConnection() == null) {
                 throw new SQLException("Chưa kết nối CSDL.");
            }
        } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Lỗi kết nối CSDL để lấy tên SP: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             return false;
        }

        // 2. Sử dụng try-with-resources để đảm bảo tài nguyên được đóng đúng cách
        try (FileOutputStream fos = new FileOutputStream(filePath);
             PdfWriter writer = new PdfWriter(fos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) // Khổ A4 mặc định
        {
            // 3. Load Font hỗ trợ Tiếng Việt (Rất quan trọng!)
            PdfFont font = null;
            try {
                 // Sử dụng font từ file .ttf, nhúng vào PDF
                 font = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                 document.setFont(font); // Đặt font mặc định cho tài liệu
            } catch (IOException e) {
                 System.err.println("Không thể tải font: " + FONT_PATH + ". Sử dụng font mặc định (có thể lỗi tiếng Việt). " + e.getMessage());
                 JOptionPane.showMessageDialog(null, "Lỗi font: Không thể tải font tiếng Việt tại\n" + FONT_PATH + "\nVui lòng kiểm tra đường dẫn font.\nPDF có thể bị lỗi hiển thị tiếng Việt.", "Lỗi Font", JOptionPane.WARNING_MESSAGE);
            }

            Paragraph storeName = new Paragraph("CỬA HÀNG TIỆN LỢI CIRCLE ELEVEN")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14).setBold();
            document.add(storeName);
            document.add(new Paragraph("Địa chỉ: Số 23 Nguyễn Văn Bảo, Quận Gò Vấp, TP.HCM").setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            document.add(new Paragraph("Điện thoại: 0896426913").setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            document.add(new Paragraph("--------------------------------------------------").setTextAlignment(TextAlignment.CENTER));

            // --- Tiêu đề hóa đơn ---
            document.add(new Paragraph("HÓA ĐƠN BÁN HÀNG")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18).setBold());
            document.add(new Paragraph(" "));

            // --- Thông tin chung của hóa đơn ---
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Mã HĐ: " + hoaDon.getMaHD()).setFontSize(11));
            document.add(new Paragraph("Ngày lập: " + (hoaDon.getNgayLap() != null ? sdf.format(hoaDon.getNgayLap()) : "N/A")).setFontSize(11));

            String tenKH = "Khách lẻ";
            String sdtKH = "";
            if (hoaDon.getKh() != null) {
                tenKH = hoaDon.getKh().getHoTen();
                sdtKH = " (SĐT: " + (hoaDon.getKh().getSdt() != null ? hoaDon.getKh().getSdt() : "N/A") + ")";
            }
            document.add(new Paragraph("Khách hàng: " + tenKH + sdtKH).setFontSize(11));

            if (hoaDon.getNv() != null) {
                 document.add(new Paragraph("Nhân viên: " + hoaDon.getNv().getHoTen() + " (" + hoaDon.getNv().getMa() + ")").setFontSize(11));
            }
             document.add(new Paragraph("Quầy số: " + hoaDon.getQuay()).setFontSize(11));
            document.add(new Paragraph(" "));

            // --- Bảng chi tiết sản phẩm ---
            float[] columnWidths = {1, 5, 1.5f, 2.5f, 3f}; // STT, Tên SP, SL, Đơn giá, Thành tiền
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100)); // Bảng chiếm toàn bộ chiều rộng

            // Header của bảng
            table.addHeaderCell(createHeaderCell("STT"));
            table.addHeaderCell(createHeaderCell("Tên sản phẩm"));
            table.addHeaderCell(createHeaderCell("SL"));
            table.addHeaderCell(createHeaderCell("Đơn giá"));
            table.addHeaderCell(createHeaderCell("Thành tiền"));

            // Dữ liệu các dòng chi tiết
            int stt = 1;
            Locale localeVN = new Locale("vi", "VN");
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
            double tongTienHangThucTe = 0; // Tính lại tổng tiền từ chi tiết

            if (chiTietList != null) { // Kiểm tra null cho danh sách chi tiết
                for (ChiTietHoaDon ct : chiTietList) {
                    SanPham sp = null;
                    String tenSP = "Lỗi lấy tên SP";
                    try {
                        sp = spDao.getById(ct.getMaSP());
                        if (sp != null) tenSP = sp.getTenSP();
                    } catch (SQLException ignored) {
                        System.err.println("Không tìm thấy SP với mã: " + ct.getMaSP());
                    }

                    double thanhTienDong = ct.getSoLuong() * ct.getDonGia();
                    tongTienHangThucTe += thanhTienDong;

                    table.addCell(new Cell().add(new Paragraph(String.valueOf(stt++))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    table.addCell(new Cell().add(new Paragraph(tenSP)).setFontSize(10));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(ct.getSoLuong()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    table.addCell(new Cell().add(new Paragraph(currencyFormatter.format(ct.getDonGia()))).setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
                    table.addCell(new Cell().add(new Paragraph(currencyFormatter.format(thanhTienDong))).setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
                }
            }
            document.add(table);
            document.add(new Paragraph(" "));

            // --- Phần tổng kết ---
            // Sử dụng table để căn lề phải dễ hơn
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{6, 4})); // 2 cột tỉ lệ 6:4
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            // Có thể tính toán lại tổng tiền hàng từ chi tiết để đảm bảo chính xác
             summaryTable.addCell(createSummaryCell("Tổng tiền hàng:", TextAlignment.RIGHT));
             summaryTable.addCell(createSummaryCell(currencyFormatter.format(tongTienHangThucTe), TextAlignment.RIGHT)); // Dùng tổng tính lại

             // Thêm giảm giá nếu có
             double giamGia = tongTienHangThucTe - hoaDon.getThanhTien(); // Tính giảm giá (nếu có)
             if (giamGia > 0.1) { // Chỉ hiển thị nếu có giảm giá đáng kể
                   summaryTable.addCell(createSummaryCell("Giảm giá:", TextAlignment.RIGHT));
                   summaryTable.addCell(createSummaryCell(currencyFormatter.format(giamGia), TextAlignment.RIGHT));
             }

             summaryTable.addCell(createSummaryCell("Thành tiền:", TextAlignment.RIGHT, true)); // Bold
             summaryTable.addCell(createSummaryCell(currencyFormatter.format(hoaDon.getThanhTien()), TextAlignment.RIGHT, true)); // Bold

             summaryTable.addCell(createSummaryCell("Tiền khách đưa:", TextAlignment.RIGHT));
             summaryTable.addCell(createSummaryCell(currencyFormatter.format(hoaDon.getTienNhan()), TextAlignment.RIGHT));

             summaryTable.addCell(createSummaryCell("Tiền thối lại:", TextAlignment.RIGHT));
             summaryTable.addCell(createSummaryCell(currencyFormatter.format(hoaDon.getTienThoi()), TextAlignment.RIGHT));

            document.add(summaryTable);
            document.add(new Paragraph(" "));

            // --- Footer ---
            document.add(new Paragraph("Cảm ơn quý khách và hẹn gặp lại!")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(11).setItalic());

            // 5. Đóng tài liệu (try-with-resources sẽ tự đóng document, pdf, writer, fos)
            // document.close(); không cần thiết ở đây

            // 6. Thông báo thành công
            JOptionPane.showMessageDialog(null, "Xuất hóa đơn thành file PDF thành công!\nĐã lưu tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Tùy chọn: Tự động mở file PDF
            try {
                 if (java.awt.Desktop.isDesktopSupported()) {
                      java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                 }
            } catch (IOException ioe) {
                 System.err.println("Không thể tự động mở file PDF: " + ioe.getMessage());
            }

            return true; // Trả về true khi thành công

        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Lỗi: Không thể tạo hoặc ghi file tại đường dẫn:\n" + filePath + "\nVui lòng kiểm tra quyền ghi hoặc đường dẫn.", "Lỗi File", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) { // Bắt lỗi liên quan đến font hoặc ghi file
            JOptionPane.showMessageDialog(null, "Lỗi IO khi tạo PDF: " + e.getMessage(), "Lỗi I/O", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) { // Bắt các lỗi khác (runtime,...)
            JOptionPane.showMessageDialog(null, "Lỗi không xác định khi xuất PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        return false; // Trả về false nếu có lỗi xảy ra
    }

    // Hàm tiện ích tạo ô Header cho bảng
    private Cell createHeaderCell(String content) {
        return new Cell().add(new Paragraph(content).setBold().setFontSize(10)).setTextAlignment(TextAlignment.CENTER);
    }

     // Hàm tiện ích tạo ô cho bảng tổng kết
    private Cell createSummaryCell(String content, TextAlignment alignment) {
        return createSummaryCell(content, alignment, false);
    }

    private Cell createSummaryCell(String content, TextAlignment alignment, boolean isBold) {
         Paragraph p = new Paragraph(content).setFontSize(11).setTextAlignment(alignment);
         if (isBold) { p.setBold(); }
         // Tạo ô không có đường viền
         return new Cell().add(p).setBorder(null);
    }

}