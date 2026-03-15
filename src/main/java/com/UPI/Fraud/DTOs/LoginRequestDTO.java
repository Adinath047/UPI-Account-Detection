package com.UPI.Fraud.DTOs;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
    private String deviceHash;
    private String browser;
    private String os;
    private String platform;
    private String screenResolution;
    private String timezone;
    private String ipAddress;
    private String country;
    private String city;
}