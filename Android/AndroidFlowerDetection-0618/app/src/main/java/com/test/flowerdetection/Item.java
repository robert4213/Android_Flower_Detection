package com.test.flowerdetection;

public class Item {
    private String name;
    private byte[] image;
    private String date;
    private double latitude;
    private double longitude;
    private String city;
    private String boxes;
    private String type_list;
    private String key;

    public Item(String name, byte[] image, String date, String boxes, String type_list, double latitude, double longitude, String city,String key) {
        this.name = name;
        this.image = image;
        this.boxes = boxes;
        this.type_list = type_list;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoxes() {return boxes;}

    public void setBoxes(String boxes) {this.boxes = boxes;}

    public String getType_list() {return type_list;}

    public void setType_list(String type_list) {this.type_list = type_list;}

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

    public String getKey() {return key;}

    public void setKey() {this.key = key;}

}

