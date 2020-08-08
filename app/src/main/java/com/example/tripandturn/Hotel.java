package com.example.tripandturn;

public class Hotel {
    private String Name, Type, Address, City, Contact, Email, Website, Description, LastUpdateTime;

    public Hotel() {
    }

    public Hotel(String name, String type, String address, String city, String contact, String email, String website, String description, String lastUpdateTime) {
        Name = name;
        Type = type;
        Address = address;
        City = city;
        Contact = contact;
        Email = email;
        Website = website;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getWebsite() {
        return Website;
    }

    public void setWebsite(String website) {
        Website = website;
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
