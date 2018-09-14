package com.ckmio.client;

public class AuthMessagePart{
    public String plan_key;
    public String plan_secret;
    public String user; 
    public String password;
    public AuthMessagePart(String plan_key, String plan_secret, String user, String password){
        this.plan_key = plan_key;
        this.plan_secret = plan_secret;
        this.user = user;
        this.password = password;
    }
}