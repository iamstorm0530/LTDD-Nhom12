package com.example.appshopbanhang;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id; // ID đơn hàng
    private String tenKh;
    private String diaChi;
    private String sdt;
    private float tongTien;
    private String ngayDatHang;
    private String trangthai;
    private List<ChiTietDonHang> chiTietList; // Danh sách chi tiết đơn hàng

    public Order(int id, String tenKh, String diaChi, String sdt, float tongTien, String ngayDatHang,String trangthai) {
        this.id = id;
        this.tenKh = tenKh;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.tongTien = tongTien;
        this.ngayDatHang = ngayDatHang;
        this.trangthai=trangthai;
        this.chiTietList = new ArrayList<>(); // Khởi tạo danh sách
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getTenKh() {
        return tenKh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getSdt() {
        return sdt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTenKh(String tenKh) {
        this.tenKh = tenKh;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setTongTien(float tongTien) {
        this.tongTien = tongTien;
    }

    public void setNgayDatHang(String ngayDatHang) {
        this.ngayDatHang = ngayDatHang;
    }

    public String getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(String trangthai) {
        this.trangthai = trangthai;
    }

    public float getTongTien() {
        return tongTien;
    }

    public String getNgayDatHang() {
        return ngayDatHang;
    }

    public List<ChiTietDonHang> getChiTietList() {
        return chiTietList; // Getter cho danh sách chi tiết
    }

    public void setChiTietList(List<ChiTietDonHang> chiTietList) {
        this.chiTietList = chiTietList; // Setter cho danh sách chi tiết
    }
}