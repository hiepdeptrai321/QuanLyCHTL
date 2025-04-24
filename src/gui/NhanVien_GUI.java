package gui;

import java.sql.SQLException;
import java.util.ArrayList;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;

public class NhanVien_GUI {
	
	public static void main(String[] args) throws SQLException {
		try {
			ConnectDB.getInstance().connect();
			NhanVien_DAO ds = new NhanVien_DAO();
			ArrayList<NhanVien> dsnv = (ArrayList<NhanVien>) ds.getAll();
			for (NhanVien x : dsnv) {
				System.out.println(x.toString() + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

