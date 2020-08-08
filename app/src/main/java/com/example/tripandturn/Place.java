package com.example.tripandturn;

public class Place {
    private String Name, Type, City, Description;

    public Place() {
    }

    public Place(String name, String type, String city, String description) {
        Name = name;
        Type = type;
        City = city;
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
