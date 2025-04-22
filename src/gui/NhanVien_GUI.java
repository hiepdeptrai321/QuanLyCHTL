package gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import dao.NhanVien_DAO;
import entity.NhanVien;

public class NhanVien_GUI {
	
	public static void main(String[] args) throws SQLException {
		ConnectDB.getInstance().connect();
		NhanVien_DAO ds = new NhanVien_DAO();
		ArrayList<NhanVien> dsnv = new ArrayList<NhanVien>();
		dsnv = (ArrayList<NhanVien>) ds.getAll();
		for(NhanVien x : dsnv) {
			System.out.println(x.toString()+"\n");
		}
	}
}

