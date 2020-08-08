package com.example.tripandturn;

public class Package {
    private String Name, Type, Cost, Place, Persons, Days, Nights, Description, LastUpdateTime;

    public Package() {
    }

    public Package(String name, String type, String cost, String place, String persons, String days, String nights, String description, String lastUpdateTime) {
        Name = name;
        Type = type;
        Cost = cost;
        Place = place;
        Persons = persons;
        Days = days;
        Nights = nights;
        Description = description;
        LastUpdateTime = lastUpdateTime;
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

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getPlace() {
        return Place;
    }

    public void setPlace(String place) {
        Place = place;
    }

    public String getPersons() {
        return Persons;
    }

    public void setPersons(String persons) {
        Persons = persons;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }

    public String getNights() {
        return Nights;
    }

    public void setNights(String nights) {
        Nights = nights;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }
}
