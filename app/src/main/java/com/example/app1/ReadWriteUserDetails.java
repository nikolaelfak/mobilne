package com.example.app1;

public class ReadWriteUserDetails {
    public String dob,fullName ,gender, mobile;
    public int points;

    public ReadWriteUserDetails(){}

    public ReadWriteUserDetails(String textDob, String textFullName, String textGender, String textMobile){
        this.dob = textDob;
        this.fullName = textFullName;
        this.gender = textGender;
        this.mobile = textMobile;
        this.points = 0;
    }

    void setPoints(int points){
        this.points = points;
    }
}
