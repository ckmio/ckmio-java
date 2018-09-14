package com.ckmio.client;

public class FunnelCondition{
    public String op;
    public String field ;
    public Object value ;

    public FunnelCondition(){
        this.op = "";
        this.field = "";
        this.value = "";
    }

    public FunnelCondition(String field, String operator, Object value){
        this.op = operator;
        this.field = field;
        this.value = value;
    }
}