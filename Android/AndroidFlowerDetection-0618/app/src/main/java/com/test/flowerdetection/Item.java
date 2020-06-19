package com.test.flowerdetection;

public class Item {
    private String name;
    private byte[] image;
    private String date;

    public Item(String name, byte[] image, String date) {
        this.name = name;
        this.image = image;
        this.date = date;
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
}

