package com.ckmio.client;

import java.util.HashMap;

public class ChatMessagePart{
    public String from;
    public String to;
    public String type; 
    public String content;
    public ChatMessagePart(String from, String to, String type, String content){
        this.from = from;
        this.to = to;
        this.type = type;
        this.content = content;
    }

    public ChatMessagePart(HashMap<String, Object> payload){
        this.from = (String)payload.get("from");
        this.to = (String)payload.get("to");;
        this.type = (String)payload.get("type");
        this.content = (String)payload.get("content");
    }
}