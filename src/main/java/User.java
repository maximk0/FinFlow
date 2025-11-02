public class User {
  private final String login;
  private final String password;

  User(String login, String password) {
    this.login = login;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public String getLogin() {
    return login;
  }

  @Override
  public String toString() {
    return login;
  }
}
