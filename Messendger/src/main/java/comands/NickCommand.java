package comands;

import session.Session;
import session.User;

/**
 * Created by user on 20.10.2015.
 */
public class NickCommand implements Command {
    @Override
    public String execute(Session session, String[] args) {
        if (session.getSessionUser() == null) {
            return "you are not authorized";
        } else if (args.length == 2){
            User user = session.getSessionUser();
            user.setNick(args[1]);
            return "nick is adeed";
        } else {
            return "This command requires 1 arg";
        }
    }
}
