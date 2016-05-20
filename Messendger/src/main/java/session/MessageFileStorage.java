package session;

import tools.FileWork;

import java.io.FileNotFoundException;
import java.util.Calendar;

/**
 * Created by user on 20.10.2015.
 */
public class MessageFileStorage implements MessagesStorage {
    private FileWork fw = new FileWork();
    //������ ��������� ����������: ������� ����� �� ������ ���������� ���� � ��� �����������
    private String filePath = "D:\\Projects\\IdeaProjects\\technotrackChat\\data\\messages.txt";

    public MessageFileStorage(String userLogin) {
        this.filePath = "D:\\Projects\\IdeaProjects\\technotrackChat\\data\\" + userLogin;
    }

    public MessageFileStorage(){};

    @Override
    public void storeMesage(String mes, Session session) {
        try {
            Calendar ca = Calendar.getInstance();
            fw.writeToFile("[" + ca.getTime() + "] " + mes, filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getAllMessages() {
        String[] messages = new String[0];
        try {
            messages = fw.readFile(filePath).split("\\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public String[] getNMessages(int N) {
        String[] messages = new String[0];
        try {
            messages = fw.readFile(filePath).split("\\n");
            if (messages.length > N) {
                String[] lastMes = new String[N];
                for (int i = messages.length - N, j = 0; i < messages.length; i++, j++) {
                    lastMes[j] = messages[i];
                }
                return lastMes;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
