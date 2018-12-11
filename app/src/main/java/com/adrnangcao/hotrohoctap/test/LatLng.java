package com.adrnangcao.hotrohoctap.test;

public class LatLng {
    public int id;
    public String name;

    public LatLng(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public LatLng(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
