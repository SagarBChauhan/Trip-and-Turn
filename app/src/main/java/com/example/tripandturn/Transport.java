package com.example.tripandturn;

public class Transport {
    String CompanyName,Name,Model,Type,Cost,lastUpdateTime;

    public Transport() {
    }

    public Transport(String companyName, String name, String model, String type, String cost, String lastUpdateTime) {
        CompanyName = companyName;
        Name = name;
        Model = model;
        Type = type;
        Cost = cost;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
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

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
