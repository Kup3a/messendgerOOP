package session;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by user on 15.11.2015.
 */
public class MessageDBStorage implements MessagesStorage {
    /*
    Экземпляр этого класса будет создаваться и жить внутри Session, поэтому тут (внутри) надо как-то специфицировать чьи
    сообщения мы будем сохранять. Для этого сделаем конструктор, в который будет передаваться айди пользователя и айди
    чата.
     */
    private int chat_id;
    private int user_id;

    public MessageDBStorage(int chat_id, int user_id) {
        this.chat_id = chat_id;
        this.user_id = user_id;
    }

    public MessageDBStorage() {
    }



    @Override
    public void storeMesage(String mes, Session session) {
        try {
            Class.forName("org.postgresql.Driver");
//            Connection c = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Kup3a", "senthil", "ubuntu");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Kup3a", "postgres", "114499");
            Statement stmt = c.createStatement();

            String sql = "INSERT INTO MESSAGES (M_TEXT, SENDER_ID, CHAT_ID) "
                    + "VALUES (" + "'" + mes + "'," + user_id + "," + session.getCurrentChat().getChatId() + ");";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String[] getAllMessages() {
        String[] result = new String[0];
        try {
            Class.forName("org.postgresql.Driver");
//            Connection c = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Kup3a", "senthil", "ubuntu");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Kup3a", "postgres", "114499");
            Statement stmt = c.createStatement();


            String sql = "SELECT * FROM MESSAGES WHERE SENDER_ID = " + user_id + " AND CHAT_ID = " + chat_id;
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<String> list = new ArrayList<String>();
            while (rs.next()) {
                list.add(rs.getString("M_TEXT"));
            }
            result = list.toArray(result);
            return result;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    @Override
    public String[] getNMessages(int N) {
        return new String[0];
    }
}
