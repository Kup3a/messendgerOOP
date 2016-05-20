package authorization;

import session.User;

/**
 * Created by user on 20.10.2015.
 */
public interface UserStore {

    // ���������, ���� �� ������������ � ����� ������
    // ���� ����, ������� true
    boolean isUserExist(String name);

    // �������� ������������ � ���������
    void addUser(User user);

    // �������� ������������ �� ����� � ������
    User getUser(String name);
}
