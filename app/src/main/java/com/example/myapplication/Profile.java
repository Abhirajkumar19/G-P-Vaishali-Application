package com.example.myapplication;

import android.graphics.Bitmap;

public class Profile {
    private Bitmap image;
    private String name;
    private String branch;
    private String rollNo;
    private String regNo;
    private String email;

    // Constructors
    public Profile() {}

    public Profile(Bitmap image, String name, String branch, String rollNo, String regNo, String email) {
        this.image = image;
        this.name = name;
        this.branch = branch;
        this.rollNo = rollNo;
        this.regNo = regNo;
        this.email = email;
    }

    // Getters and Setters
    public Bitmap getImage() { return image; }
    public void setImage(Bitmap image) { this.image = image; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
