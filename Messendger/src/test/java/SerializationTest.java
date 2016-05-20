import org.junit.Test;
import session.Message;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by user on 15.12.2015.
 */
public class SerializationTest {
    @Test
    public void evaluateBeforeAfter() {
        Message message = new Message();
        message.setTime(Calendar.getInstance().getTime());
        message.setBody("hello, mipt!");
        message.setConnectionId(317);
        String jsonString = message.toJsonString();
        Message newMes = Message.makeFromJson(jsonString);
        assertEquals(newMes.getBody(), message.getBody());
        assertEquals(newMes.getConnectionId(), message.getConnectionId());
        assertEquals(newMes.getTime(), message.getTime());
    }
}
