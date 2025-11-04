import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileService {
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  private Path dataDir() {
    return Paths.get("data");
  }

  private Path walletFile(String login) {
    return dataDir().resolve(login + ".wallet.json");
  }

  private void ensureDataDir() {
    try {
      Files.createDirectories(dataDir());
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось создать каталог данных: " + dataDir(), e);
    }
  }

  public void saveCurrentWallet(User user) {
    if (user == null) {
      System.out.println("Данные кошелька не были сохранены, так как пользователь не авторизован.");
      return;
    }

    ensureDataDir();
    Path file = walletFile(user.getLogin());
    try {
      String json = gson.toJson(user.getWallet());
      Files.writeString(
          file,
          json,
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
      System.out.println("Данные кошелька сохранены в " + file);
    } catch (IOException e) {
      System.out.println("Ошибка сохранения кошелька: " + e.getMessage());
    }
  }

  public void loadWallet(String login, Wallet wallet) {
    Path file = walletFile(login);
    if (!Files.exists(file)) return;

    try {
      String json = Files.readString(file, StandardCharsets.UTF_8);
      Wallet loaded = gson.fromJson(json, Wallet.class);
      wallet.copyFrom(loaded);
      System.out.println("Данные кошелька загружены из " + file.getFileName());
    } catch (IOException e) {
      System.out.println("Ошибка загрузки кошелька: " + e.getMessage());
    }
  }
}
