package com.example.tripandturn.DB;

public class User {
    public String firstName, middleName, lastName, gender, dateOfBirth, address, city, pinCode, contactNo, email,Login_Id,Profile_Picture,Social,Registration_Date,Last_Update_Time;

    public User() {
    }

    public User(String firstName, String middleName, String lastName, String gender, String dateOfBirth, String address, String city, String pinCode, String contactNo, String email, String login_Id, String profile_Picture, String social, String registration_Date, String last_Update_Time) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.pinCode = pinCode;
        this.contactNo = contactNo;
        this.email = email;
        Login_Id = login_Id;
        Profile_Picture = profile_Picture;
        Social = social;
        Registration_Date = registration_Date;
        Last_Update_Time = last_Update_Time;
    }
}
