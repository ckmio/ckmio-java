package com.ckmio.client;

import java.util.Dictionary;
import java.util.HashMap;

public class  Response{
    public String status; 
    public String message;
    public String service;
    public String clt_ref;
    public HashMap<String, Object> payload;

    public Response(){
        this.status = "" ;
        this.message = "";
        this.service = "";
        this.clt_ref = "";
        this.payload = new HashMap<String, Object>();
    }

    public Response(String status, String message, String service, String clt_ref, HashMap<String, Object> payload){
        this.status = status ;
        this.message = message;
        this.service = service;
        this.clt_ref = clt_ref;
        this.payload = payload;
    }

    public void print (){
        System.out.println("From exchange message " + this.service);
    }
}