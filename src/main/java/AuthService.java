import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final Map<String, User> users = new HashMap<>();

    private User currentUser;

    public boolean checkLogin(String login) {
        return users.containsKey(login);
    }

    public boolean checkPassword(String login, String password) {
        User user = users.get(login);
        if (user == null) return false;
        return user.getPassword().equals(password);
    }

    public void saveUser(User user) {
        users.put(user.getLogin(), user);
    }

    public void logout() {
        currentUser = null;
    }

    public void setCurrentUser(String login) {
        currentUser = users.get(login);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
