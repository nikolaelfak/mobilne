package com.example.app1;

public class DataClass {
    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
    private String key;

    private String longi;
    private String lati;

    private String recipeImageUrl;

    private String CarRealAddress;
    private double averageRating;
    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userName;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }

    // Getter i Setter za longi
    public String getLongi() {
        return longi;
    }
    public void setLongi(String longi) {
        this.longi = longi;
    }

    // Getter i Setter za lati
    public String getLati() {
        return lati;
    }
    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getRecipeImageUrl() {
        return recipeImageUrl;
    }

    public void setRecipeImageUrl(String recipeImageUrl) {
        this.recipeImageUrl = recipeImageUrl;
    }

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, String longi, String lati) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.longi = longi;
        this.lati = lati;
    }

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, String longi, String lati, String CarRealAddress) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.longi = longi;
        this.lati = lati;
        this.CarRealAddress = CarRealAddress;
    }

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
    }

    public DataClass(){
    }

    public String getCarRealAddress(){
        return CarRealAddress;
    }
}
