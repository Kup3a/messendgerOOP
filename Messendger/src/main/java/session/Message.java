package session;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Date;

/**
 * Created by user on 30.10.2015.
 */
public class Message {
    private String body;
    private Date time;
    private int connectionId;
    private static ObjectMapper mapper = new ObjectMapper();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public Message(){
    }

    public static Message makeFromJson(String jsonString) {
        Message m = null;
        try {
            m = mapper.readValue(jsonString, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return m;
    }

    public String toJsonString () {
        String result = null;
        try {
            result = mapper.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
