package com.example.tripandturn.DB;

public class Login {
    public String Id,Username, Password, Type,Status, LastUpdateTime;

    public Login(){}

    public Login(String id, String username, String password, String type, String status, String lastUpdateTime) {
        Id = id;
        Username = username;
        Password = password;
        Type = type;
        Status = status;
        LastUpdateTime = lastUpdateTime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }
}
