package com.test.flowerdetection;

public class Item {
    private String name;
    private byte[] image;
    private String date;
    private double latitude;
    private double longitude;
    private String city;

    public Item(String name, byte[] image, String date, double latitude, double longitude, String city) {
        this.name = name;
        this.image = image;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getLatitude() {return latitude;}

    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}

    public void setLongitude(double longitude) {this.longitude = longitude;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}
}

