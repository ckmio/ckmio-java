package com.ckmio.client;
import java.util.HashMap;
import java.io.Console;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
       CkmioClient ckmio = new CkmioClient("community-test-key", "community-test-secret", "Khady", "xxxx");
       ckmio.debug=false;
       ckmio.connect();
       String topicName = "A brand new topic";    
       String streamName = "A brand new Stream";    
       ckmio.subscribeToChat();
       ckmio.subscribeToTopic(topicName);
       ckmio.funnel(streamName, new FunnelCondition[]{new FunnelCondition("age", "greater_than", 40)});
       ckmio.setFunnelHandler((data)-> System.out.println("Funnel Update " + data.content ));
       ckmio.setChatHandler((message)-> System.out.println("Message Update " + message.from));
       ckmio.setTopicHandler((topicUpdate)-> System.out.println("Message Update " + topicUpdate.content));

       ckmio.send("Khady", "Hello");
       ckmio.updateTopic(topicName, "Topic Update");
       ckmio.sendToStream(streamName, new HashMap<String, Object>(){ { put("age",60); put("name", "Bob"); put("gender", "Male");}});
       ckmio.sendToStream(streamName, new HashMap<String, Object>(){ { put("age",20); put("name", "Alice"); put("gender", "Female");}});
       Console console = System.console();
       console.readLine("");
    }
}
