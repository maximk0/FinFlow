import java.io.Serializable;

public class User implements Serializable {
  private final String login;
  private final String password;
  private final Wallet wallet = new Wallet();

  User(String login, String password) {
    this.login = login;
    this.password = password;
  }

  public Wallet getWallet() {
    return wallet;
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
