package working;

import org.codehaus.jackson.map.ObjectMapper;
import session.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by user on 27.10.2015.
 */
public class ChatClient implements Runnable {

    private Socket s;
    private Message m = new Message();
    private boolean listener = true;

    /**
     * ������ ����������� � �������.
     * ��� ����� ����� ������������ ���� � ��� �� �����, ��� ��������� ���������� ��������� ������� � �������� ���������
     * �� ������� �����������, �.�. �� connectionId �� Message
     */
    private void initClient() {
        try {
            s = new Socket("localhost", 3129);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ���� ����� ���������� ����� ����� �������� ������ �������.
     */
    public void connectToServer() {
        // ��� ������� ������� � ���, ��� ����������� ��� �� ��������� � ��� �����������
        m.setConnectionId(0);

        initClient();
        // ��������� ����, ������� ����� �������� ��������� �� �������
        Thread listener = new Thread(this);
        listener.start();
        // ��������� ����, ������� ����� ���������� ��������� �������, �������� �� � ����������
        Thread producer = new Thread(this);
        producer.start();
    }

    @Override
    public void run() {
        if (listener) {
            listener = false;
            System.out.println("i'm new thread-listener");
            try {
                byte buf[] = new byte[64 * 1024];
                int length;
                String mes;
                while (true) {
                    length = s.getInputStream().read(buf);
                    mes = new String(buf, 0, length);
                    //����� ���������� ������ ���������� ���������� ������� connectionId
//                    if (mes.startsWith("connectionAm")) {
                    if (m.getConnectionId() == 0) {
                        String[] pars = mes.split(" ");
                        m.setConnectionId(Integer.parseInt(pars[1]));
                        System.out.println("my id now is " + m.getConnectionId());
                        m.token = pars[2];
                        System.out.println("m.token " + m.token);
                    }
                    System.out.println("message from server: " + mes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("i'm new thread-producer");
            Scanner scanner = new Scanner(System.in);
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString;
            while (true) {
                //��������� ���������
                m.setBody(scanner.nextLine());
                m.setTime(Calendar.getInstance().getTime());
                //����������� ���������-������ � json-������
                try {
                    jsonInString = mapper.writeValueAsString(m);
                    //������ ����������� � ������ ����� � ������� ��� ����� �����
                    s.getOutputStream().write(jsonInString.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
