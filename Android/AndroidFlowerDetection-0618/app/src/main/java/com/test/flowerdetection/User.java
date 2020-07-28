package com.test.flowerdetection;

public class User {
    private String id;
    private String email;
    private String name;

    public User(String email,String id) {
       this.id = id;
        this.email = email;
      //  this.gender = gender;
     //  this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
