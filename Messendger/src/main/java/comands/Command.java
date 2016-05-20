package comands;

import session.Session;

/**
 * Created by user on 20.10.2015.
 */
public interface Command {


    /**
     * ����� ����� ���������� ���������, ��������� ��� ����� �������
     * ��������� ���������� ��������������� � ����� ������� Result
     *
     * � �������� ������ ������� void
     */
    String execute(Session session, String[] args);
}
