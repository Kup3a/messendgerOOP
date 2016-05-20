package tools; /**
 * Created by r.kildiev on 02.11.2015.
 */

import authorization.DBUserStore;
import authorization.UserStore;
import chat.Chat;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import session.MessageDBStorage;
import session.MessagesStorage;
import session.Session;
import session.User;

import java.beans.PropertyVetoException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;

public class JDBCWorker {

    ComboPooledDataSource cpds = new ComboPooledDataSource();

    Map<Connection, List<String>> connectionStatements = new HashMap<>();
    Map<String, PreparedStatement> nameStatement = new HashMap<>();

    public JDBCWorker() {
        try {
//            cpds.setDriverClass("org.postgresql.Driver"); //loads the jdbc driver
//            cpds.setJdbcUrl("jdbc:postgresql://178.62.140.149:5432/Kup3a");
//            cpds.setUser("senthil");
//            cpds.setPassword("ubuntu");
            cpds.setDriverClass("org.postgresql.Driver"); //loads the jdbc driver
            cpds.setJdbcUrl("jdbc:postgresql://localhost:5432/Kup3a");
            cpds.setUser("postgres");
            cpds.setPassword("114499");
            cpds.setMaxStatements(20);
            cpds.setMaxStatementsPerConnection(4);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }


    // работа с юзерами, а точнее методы для класса DBUserStore

    public User getUserFromDB(String login) throws SQLException, ClassNotFoundException {
        User u = new User();

//        Class.forName("org.postgresql.Driver");
//        Connection c = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Kup3a",
//                "senthil", "ubuntu");
        Connection c = cpds.getConnection();

        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM USERS WHERE u_login =" + "'" + login + "'";
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            u.setId(rs.getInt("U_ID"));
            u.setLogin(login);
            u.setHashedPassword(rs.getString("HASHED_PASSWORD"));
            u.setSalt(rs.getString("SALT"));
            return u;
        } else {
            return null;
        }
    }

    public void addUserToDB(User u) throws SQLException, ClassNotFoundException {
//        Class.forName("org.postgresql.Driver");
//        Connection c = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Kup3a",
//                "senthil", "ubuntu");

        Connection c = cpds.getConnection();
        Statement stmt = c.createStatement();

        String s = null;
        try {
            s = HashClass.getSaltHP(u.getPassword().toCharArray());
            String[] ss = s.split(" ");
            u.setSalt(ss[0]);
            u.setHashedPassword(ss[1]);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO USERS (U_LOGIN,SALT,HASHED_PASSWORD) "
                + "VALUES (" + "'" + u.getLogin() + "','" + u.getSalt() + "','" + u.getHashedPassword() + "'" + ");";
        stmt.executeUpdate(sql);
        stmt.close();
    }


    // а тут методы для работы с классом DBChatStorage

    public void storeChat(String name) {
        try {
            Connection connection = cpds.getConnection();
            Statement stmt = connection.createStatement();

            String sql = "INSERT INTO CHATS (CH_NAME) "
                    + "VALUES ('" + name + "');";
            stmt.executeUpdate(sql);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Chat getChat(String name) {
        Chat chat = new Chat();
        try {
            Connection connection = cpds.getConnection();

            List<String> currentStatements = connectionStatements.get(connection);
            String stringStatement = "SELECT * FROM CHATS WHERE CH_NAME = ?";
            PreparedStatement preparedStatement;
            if (currentStatements != null && currentStatements.contains(stringStatement)) {
                preparedStatement = nameStatement.get(stringStatement);
                ResultSet rs = preparedStatement.executeQuery();
            } else if (currentStatements != null) {
                preparedStatement = connection.prepareStatement(stringStatement);
                currentStatements.add(stringStatement);
                if (!nameStatement.containsKey(stringStatement)) {
                    nameStatement.put(stringStatement, preparedStatement);
                }
            } else {
                currentStatements = new LinkedList<>();
                currentStatements.add(stringStatement);
                connectionStatements.put(connection, currentStatements);
                preparedStatement = connection.prepareStatement(stringStatement);
                if (!nameStatement.containsKey(stringStatement)) {
                    nameStatement.put(stringStatement, preparedStatement);
                }
            }
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                chat.setChatId(rs.getInt("CH_ID"));
                chat.setChatName(rs.getString("CH_NAME"));
                return chat;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Chat> getChatList(Session session) {
        try {
            Connection connection = cpds.getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM CHATS";
            ResultSet rs = stmt.executeQuery(sql);
            ArrayList<Chat> chats = new ArrayList<>();
            while (rs.next()) {
                Chat chat = new Chat();
                chat.setChatId(rs.getInt("CH_ID"));
                chat.setChatName(rs.getString("CH_NAME"));
                chats.add(chat);
                return chats;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean isUserInChat(User user, Chat chat) {
        boolean answer = false;
        try {
            Connection connection = cpds.getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM USER_CHAT WHERE CH_ID = " + chat.getChatId() + " AND U_ID = " + user.getId();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                answer = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answer;
    }

    public String addUserToChat(Chat chat, User user) {
        if (isUserInChat(user, chat)) {
            return "This user is already exists in this chat.";
        } else {
            try {
                Connection connection = cpds.getConnection();
                Statement stmt = connection.createStatement();
                String sql = "INSERT INTO USER_CHAT (CH_ID, U_ID) "
                        + "VALUES (" + chat.getChatId() + "," + user.getId() + ");";
                stmt.executeUpdate(sql);
                stmt.close();
                return "User is successfully added into " + chat.getChatName() + " chat.";
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "smth went wrong";
    }

    public static void main(String[] argv) throws SQLException, ClassNotFoundException {
        //дальше начинаются мои изыскания
        ArrayList<String> requiredTables = new ArrayList<>();
        requiredTables.add("messages");
        requiredTables.add("users");
        requiredTables.add("chats");
        requiredTables.add("user_chat");
        int flag = 0;
        Class.forName("org.postgresql.Driver");

        //Connection c = DriverManager.getConnection("jdbc:postgresql://178.62.140.149:5432/Kup3a", "senthil", "ubuntu");
        Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Kup3a", "postgres", "114499");

        DatabaseMetaData dbData = c.getMetaData();
        ResultSet rs = dbData.getTables(null, "public", "", null);
        while (rs.next()) {
            String name = rs.getString("TABLE_NAME");
            if (requiredTables.contains(name)) {
                flag++;
            }
        }
        rs.close();

        Statement stmt;
        String sql;

        if (flag != requiredTables.size()) {
            System.out.println("Wrong db");

            stmt = c.createStatement();
            sql = "CREATE TABLE USERS " +
                    "(U_ID            SERIAL PRIMARY KEY     NOT NULL," +
                    " U_LOGIN         TEXT    NOT NULL UNIQUE , " +
                    " U_NICK          TEXT    UNIQUE , " +
                    " SALT        TEXT, " +
                    " HASHED_PASSWORD         TEXT)";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = c.createStatement();
            sql = "CREATE TABLE CHATS " +
                    "(CH_ID            SERIAL PRIMARY KEY     NOT NULL," +
                    " CH_NAME         TEXT    NOT NULL UNIQUE , " +
                    " CH_DATE          TEXT     )";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = c.createStatement();
            sql = "CREATE TABLE MESSAGES " +
                    "(M_ID             SERIAL PRIMARY KEY     NOT NULL," +
                    " M_TEXT         TEXT    NOT NULL, " +
                    " SENDER_ID INT     NOT NULL, " +
                    " CHAT_ID        INT, " +
                    " CHAT_DATE        TIMESTAMP, " +
                    " CONSTRAINT MES_FK_U FOREIGN KEY (SENDER_ID) REFERENCES USERS (U_ID) ON UPDATE NO ACTION ON DELETE NO ACTION," +
                    " CONSTRAINT MES_FK_CH FOREIGN KEY (CHAT_ID) REFERENCES CHATS (CH_ID) ON UPDATE NO ACTION ON DELETE NO ACTION)";
            stmt.executeUpdate(sql);
            stmt.close();

            stmt = c.createStatement();
            sql = "CREATE TABLE USER_CHAT " +
                    "(CH_ID            INT     NOT NULL," +
                    " U_ID             INT     NOT NULL," +
                    " PRIMARY KEY (CH_ID, U_ID)," +
                    " CONSTRAINT U_CH_FK_U FOREIGN KEY (U_ID) REFERENCES USERS (U_ID) ON UPDATE NO ACTION ON DELETE NO ACTION," +
                    " CONSTRAINT U_CH_FK_CH FOREIGN KEY (CH_ID) REFERENCES CHATS (CH_ID) ON UPDATE NO ACTION ON DELETE NO ACTION)";
            stmt.executeUpdate(sql);
            stmt.close();

            c.setAutoCommit(false);

            stmt = c.createStatement();
            sql = "INSERT INTO USERS (U_LOGIN) "
                    + "VALUES ('alex');";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
        } else {
            System.out.println("OK db");
        }

        stmt = c.createStatement();
        rs = stmt.executeQuery("SELECT * FROM MESSAGES;");

        while (rs.next()) {
            String text = rs.getString("m_text");
            System.out.println(" M_TEXT  = " + text);
        }
        rs.close();


//        stmt = c.createStatement();
//        sql = "INSERT INTO CHATS (CH_NAME) "
//                + "VALUES ('myChat');";
//        stmt.executeUpdate(sql);
        //ТЕСТИРОВАНИЕ КЛАССА DBUserStore
        //МЕТОДА isUserExist
        UserStore userStore = new DBUserStore();
        System.out.println("is user exist: " + userStore.isUserExist("alex"));
        //МЕТОДА addUser
        User u = new User();
        u.setLogin("alexey");
        if (!userStore.isUserExist("alexey")) {
            try {
                String s = HashClass.getSaltHP("114499".toCharArray());
                String[] ss = s.split(" ");
                u.setPassword("114499");
                u.setSalt(ss[0]);
                u.setHashedPassword(ss[1]);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            userStore.addUser(u);
        }
        //МЕТОДА getUser
        u = userStore.getUser("alexey");
        System.out.println(u.getHashedPassword());
        //ТЕСТИРОВАНИЕ КЛАССА
        //тестирование метода storeMesage
        MessagesStorage messagesStorage = new MessageDBStorage(2, 2);
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
//        while (!str.equals("exit")) {
//            messagesStorage.storeMesage(str, );
//            str = scanner.nextLine();
//        }
        //тестирование метода
        String[] list = messagesStorage.getAllMessages();
        System.out.println("list.length: " + list.length);
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }

        stmt.close();
        c.close();

    }

}