package com.example.flance20.model;

public class EstablishmentsMain {
    int id;
    String name, address, url_preview_img;
    double lat_for_map, lng_for_map;

    public EstablishmentsMain(int id, String name, String address, String url_preview_img, double lat_for_map, double lng_for_map) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.url_preview_img = url_preview_img;
        this.lat_for_map = lat_for_map;
        this.lng_for_map = lng_for_map;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getUrl_preview_img() {
        return url_preview_img;
    }

    public double getLat_for_map() {
        return lat_for_map;
    }

    public double getLng_for_map() {
        return lng_for_map;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUrl_preview_img(String url_preview_img) { this.url_preview_img = url_preview_img; }

    public void setLat_for_map(double lat_for_map) {
        this.lat_for_map = lat_for_map;
    }

    public void setLng_for_map(double lng_for_map) {
        this.lng_for_map = lng_for_map;
    }
}
