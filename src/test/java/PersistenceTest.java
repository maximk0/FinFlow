import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PersistenceTest {

  private static final String CATEGORY_FOOD = "Еда";
  private static final String CATEGORY_SALARY = "Зарплата";

  private static final String USER_LOGIN = "login";
  private static final String USER_PASSWORD = "password";

  private static final int BUDGET_FOOD = 1000;
  private static final int EXPENSE_FOOD = 250;
  private static final int INCOME_SALARY = 5000;

  private static final int EXPECTED_REMAINING_BUDGET = 750;
  private static final int EXPECTED_TOTAL_INCOMES = 5000;
  private static final int EXPECTED_INCOME_BY_CATEGORY = 5000;

  private static final String FILE_WALLET = "wallet.bin";
  private static final String FILE_USER = "user.bin";

  @Test
  void walletSerializeDeserialize(@TempDir Path dir) throws Exception {
    Wallet w = new Wallet();
    w.addCategory(CATEGORY_FOOD);
    w.saveCategoryBudget(CATEGORY_FOOD, BUDGET_FOOD);
    w.addExpenseTransaction(EXPENSE_FOOD, CATEGORY_FOOD);

    Path file = dir.resolve(FILE_WALLET);

    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(w);
    }

    Wallet loaded;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      loaded = (Wallet) ois.readObject();
    }

    assertNotNull(loaded);
    assertTrue(loaded.hasCategory(CATEGORY_FOOD));
    assertEquals(BUDGET_FOOD, loaded.getBudgetByCategory(CATEGORY_FOOD));
    assertEquals(EXPENSE_FOOD, loaded.getExpensesByCategory(CATEGORY_FOOD));
    assertEquals(EXPECTED_REMAINING_BUDGET, loaded.getRemainingBudget(CATEGORY_FOOD));
  }

  @Test
  void userSerializeDeserializeWithWallet(@TempDir Path dir) throws Exception {
    User u = new User(USER_LOGIN, USER_PASSWORD);
    Wallet w = u.getWallet();
    w.addCategory(CATEGORY_SALARY);
    w.addIncomeTransaction(INCOME_SALARY, CATEGORY_SALARY);

    Path file = dir.resolve(FILE_USER);

    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(u);
    }

    User loaded;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      loaded = (User) ois.readObject();
    }

    assertNotNull(loaded);
    assertEquals(USER_LOGIN, loaded.getLogin());
    assertEquals(USER_PASSWORD, loaded.getPassword());
    assertEquals(EXPECTED_TOTAL_INCOMES, loaded.getWallet().getTotalIncomes());
    assertEquals(
        EXPECTED_INCOME_BY_CATEGORY, loaded.getWallet().getIncomesByCategory(CATEGORY_SALARY));
  }
}
