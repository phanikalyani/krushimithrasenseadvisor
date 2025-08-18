package com.krushi.dto;
public class AuthResponse {
    public String token;
    public String status;
    public AuthResponse(){}
    public AuthResponse(String token, String status){ this.token = token; this.status = status;}
}
