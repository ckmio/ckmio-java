package com.ckmio.client;

import java.util.HashMap;

public class FunnelMessagePart{
    public String stream; 
    public Object content;
    public FunnelCondition[] when;

    public FunnelMessagePart(String stream, Object content){
        this.stream = stream;
        this.content = content;
        this.when = new FunnelCondition[]{};
    }
    public FunnelMessagePart(String stream, FunnelCondition[] when){
        this.stream = stream;
        this.when = when;
        this.content ="";
    }

    public FunnelMessagePart(HashMap<String, Object> payload){
        this.stream = (String)payload.get("stream");
        this.content = payload.get("content");
        this.when = new FunnelCondition[]{};       
    }
}