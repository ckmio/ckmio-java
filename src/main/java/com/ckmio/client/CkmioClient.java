package com.ckmio.client;

import java.net.*;
import java.io.*;
import java.io.OutputStream;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ckmio.client.ChatMessagePart;
import com.ckmio.client.ExchangeMessage;
import com.ckmio.client.Response;
import com.ckmio.client.TopicMessagePart;
import java.util.HashMap;

public class CkmioClient
{
    public final static String Auth = "10";
    public final static String Chat = "22";
    public final static String Topic = "21";
    public final static String Funnel = "27";
    public final static String SendChatMessage = "send-chat-message";
    public final static String Notification = "notification";
    public final static String Notify = "notify";
    public final static String Add = "Add";
    public final static String Authenticate = "authenticate";
    public final static String Subscribe = "subscribe";
    public final static String Address = "dev.ckmio.com";
    public static final int PORT = 7023;
    private byte[] buffer = new byte[2048];
    public String planKey ;
    public String planSecret ;
    public String userName ;
    public String password ;
    private Socket connection;
    private OutputStream outputStream;
    public Boolean debug;

    private Handler<ChatMessagePart> chatHandler;
    private Handler<FunnelMessagePart> funnelHandler;
    private Handler<TopicMessagePart> topicHandler;

    public CkmioClient(String planKey, String planSecret)
    {
        this.planKey = planKey;
        this.planSecret = planSecret;
        this.userName = ""; 
        this.password = "";
    }
    public CkmioClient(String planKey, String planSecret, String userName, String password)
    {
        this.planKey = planKey;
        this.planSecret = planSecret;
        this.userName = userName; 
        this.password = password;
    }

    public void start()
    {
        connect();
    }
    public void stop()
    {
        
    }
    public void connect()
    {
        try {
            Socket socket = new Socket("dev.ckmio.com", PORT);       
            this.connection = socket; 
            this.outputStream = connection.getOutputStream();
            authenticate();
            CkmioClient currentClient = this;
            Thread  thread = (new Thread(){
                public void run(){
                    currentClient.startReceiverThread();;
            }});
            thread.start();
        } catch (Exception ex) {
            System.out.println("Server not found: " + ex.getMessage());
        }   
    }

    public void setChatHandler(Handler<ChatMessagePart> chatHandler){
        this.chatHandler  = chatHandler;
    }
    public void setTopicHandler(Handler<TopicMessagePart> topicHandler){
        this.topicHandler  = topicHandler;
    }
    public void setFunnelHandler(Handler<FunnelMessagePart> funnelHandler){
        this.funnelHandler  = funnelHandler;
    }

    public void startReceiverThread(){
        int lengthToRead = 0;
        int read;
        byte[] temp = new byte[4];
        ObjectMapper mapper = new ObjectMapper();
        try{
            InputStream input = connection.getInputStream();
            while(true){
                read = input.read(temp, 0, 4);
                lengthToRead = lengthToReadFromBytes(temp);
                read = input.read(buffer, 0, lengthToRead);
                String json = new String(buffer, 0, read);
                if(this.debug) System.out.println(json);
                handleResponse(mapper.readValue(json, Response.class));
            }
        }
        catch(Exception e ){
            System.out.println(e.getMessage());
        }
    }

    public static int lengthToReadFromBytes(byte[] input){
        return ((input[2] < 0)? 256 + input[2]:input[2]) * 256 +
        ((input[3] < 0)? 256 + input[3]:input[3]);
              
    }

    private void handleResponse(Response response){
        if(response == null)
        {
            System.out.println("Response message from is null");
            return;
        }
        if(!response.status.equals(Notification)){
            if(this.debug) System.out.format("Internal service message %s : %s\n", response.service, response.message);
            return;
        }
        switch(response.service){
            case Chat:
                if(chatHandler!=null) chatHandler.handle(new ChatMessagePart(response.payload));break;
            case Topic: 
                if(topicHandler!=null) topicHandler.handle(new TopicMessagePart(response.payload));break;
            case Funnel:
                if(funnelHandler!=null) funnelHandler.handle(new FunnelMessagePart(response.payload));break;
            default:
                System.out.println("Invalid response message from " + response.service);
        }
    }

    public void send(String to, String message)
    {
        formatAndSendMessage(this.connection, Chat, 
        new ExchangeMessage(SendChatMessage,
            UUID.randomUUID().toString(),
            new ChatMessagePart(this.userName, to, "chat-message", message))
        ); 
    }

    public void sendToStream(String stream, Object content)
    {
        formatAndSendMessage(this.connection, Funnel, 
        new ExchangeMessage(Add,
            UUID.randomUUID().toString(),
            new FunnelMessagePart(stream, content))
        ); 
    }

    public void updateTopic(String name, String content)
    {
       formatAndSendMessage(this.connection, Topic, 
        new ExchangeMessage(Notify,
            UUID.randomUUID().toString(),
            new TopicMessagePart(name, content))
        ); 
    }
    
    public void subscribeToTopic(String name)
    {
       formatAndSendMessage(this.connection, Topic, 
        new ExchangeMessage(Subscribe,
            UUID.randomUUID().toString(),
            new TopicMessagePart(name, ""))
        ); 
    }

    public void funnel(String stream, FunnelCondition[] conditions)
    {
       formatAndSendMessage(this.connection, Funnel, 
        new ExchangeMessage(Subscribe,
            UUID.randomUUID().toString(),
            new FunnelMessagePart(stream, conditions))
        ); 
    }

    public void subscribeToChat()
    {
       formatAndSendMessage(this.connection, Chat, 
        new ExchangeMessage(Subscribe,
            UUID.randomUUID().toString(),
            new ChatMessagePart("", "","",""))
        ); 
    }

    private void authenticate()
    {
        formatAndSendMessage(this.connection, Auth, 
            new ExchangeMessage (
                Authenticate,
                UUID.randomUUID().toString(),
                new AuthMessagePart(planKey, planSecret, userName, password)
            )
        );
    }
    private void formatAndSendMessage(Socket client, String service, ExchangeMessage exchangeMessage) 
    {
        try{
        ObjectMapper objectMapper = new ObjectMapper(); 
        String messageContent = objectMapper.writeValueAsString(exchangeMessage);
        String toBeSent = service + ":" + messageContent;
        byte[] bytes = toBeSent.getBytes();
        this.outputStream.write(lengthOnForBytes(bytes.length));
        this.outputStream.write(bytes);
        }
        catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    public static byte[] lengthOnForBytes(int length){
        return new byte[]{
            (byte)((length & 0xff000000) >> 24),
            (byte)((length & 0x00ff0000) >> 16),
            (byte)((length & 0x0000ff00) >> 8),
            (byte)((length & 0x000000ff))
        };
    }
}