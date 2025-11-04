import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AuthServiceTest {
  private final String login = "login";
  private final String password = "password";
  private final String otherLogin = "other login";
  private final String otherPassword = "other password";

  @Test
  void saveUserAndCheckLogin() {
    AuthService auth = new AuthService();
    User u = new User(login, password);
    auth.saveUser(u);

    assertTrue(auth.checkLogin(login));
    assertFalse(auth.checkLogin(otherLogin));
  }

  @Test
  void checkPasswordWorks() {
    AuthService auth = new AuthService();
    auth.saveUser(new User(login, password));

    assertTrue(auth.checkPassword(login, password));
    assertFalse(auth.checkPassword(login, otherPassword));
    assertFalse(auth.checkPassword(otherLogin, password));
  }

  @Test
  void setAndGetCurrentUser() {
    AuthService auth = new AuthService();
    auth.saveUser(new User(login, password));
    auth.saveUser(new User(otherLogin, otherPassword));

    auth.setCurrentUser(login);
    assertNotNull(auth.getCurrentUser());
    assertEquals(login, auth.getCurrentUser().getLogin());

    auth.logout();
    assertNull(auth.getCurrentUser());
  }
}
