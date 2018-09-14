package com.ckmio.client;

public class  ExchangeMessage{
    public String action; 
    public String clt_ref;
    public Object payload;
    public ExchangeMessage(String action, String clt_ref, Object payload){
        this.action = action ;
        this.clt_ref = clt_ref;
        this.payload = payload;
    }

    public void print (){
        System.out.println("From exchange message " + this.action);
    }
}