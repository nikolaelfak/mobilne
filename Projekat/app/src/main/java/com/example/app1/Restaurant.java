package com.example.app1;

public class Restaurant {
    private String restaurantId;
    private String name;
    private String location;
    private String description;
    private float rating;

    // Konstruktori, getteri i setteri za atribute

    public Restaurant() {
        // Default konstruktor potreban za Firebase
    }

    public Restaurant(String name, String location, String description, float rating) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.rating = rating;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
