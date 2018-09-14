package com.ckmio.client;
import java.util.HashMap;

public class TopicMessagePart{
    public String name; 
    public String content;
    public TopicMessagePart(String name, String content){
        this.name = name;
        this.content = content;
    }
    public TopicMessagePart(HashMap<String, Object> payload){
        this.name = (String)payload.get("name");
        this.content = (String)payload.get("content");
    }
}