import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WalletTest {

  private Wallet wallet;

  private static final String CATEGORY_FOOD = "Еда";
  private static final String CATEGORY_ENTERTAINMENT = "Развлечения";
  private static final String CATEGORY_UTILITIES = "Коммунальные услуги";
  private static final String CATEGORY_TAXI = "Такси";
  private static final String CATEGORY_SALARY = "Зарплата";
  private static final String CATEGORY_BONUS = "Бонус";
  private static final String CATEGORY_NEW = "Новая";
  private static final String CATEGORY_X = "X";

  private static final int BUDGET_FOOD = 4000;
  private static final int BUDGET_ENTERTAINMENT = 3000;
  private static final int BUDGET_UTILITIES = 2500;
  private static final int BUDGET_X = 10;
  private static final int BUDGET_FOOD_UPDATED = 5000;

  private static final int EXPENSE_FOOD_1 = 300;
  private static final int EXPENSE_FOOD_2 = 500;
  private static final int EXPENSE_ENTERTAINMENT = 3000;
  private static final int EXPENSE_UTILITIES = 3000;
  private static final int EXPENSE_TAXI = 1500;
  private static final int EXPENSE_X = 5;

  private static final int INCOME_SALARY_1 = 20000;
  private static final int INCOME_SALARY_2 = 40000;
  private static final int INCOME_BONUS = 3000;

  private static final int EXPECTED_TOTAL_INCOMES = 63000;
  private static final int EXPECTED_TOTAL_EXPENSES = 8300;
  private static final int EXPECTED_INCOME_SALARY = 60000;
  private static final int EXPECTED_INCOME_BONUS = 3000;
  private static final int EXPECTED_EXPENSE_FOOD = 800;
  private static final int EXPECTED_EXPENSE_ENTERTAINMENT = 3000;
  private static final int EXPECTED_EXPENSE_UTILITIES = 3000;
  private static final int EXPECTED_EXPENSE_TAXI = 1500;
  private static final int EXPECTED_REMAINING_BUDGET_FOOD = 3200;
  private static final int EXPECTED_REMAINING_BUDGET_ENTERTAINMENT = 0;
  private static final int EXPECTED_REMAINING_BUDGET_UTILITIES = -500;
  private static final int EXPECTED_REMAINING_BUDGET_X = 5;

  @BeforeEach
  void setUp() {
    wallet = new Wallet();
    wallet.addCategory(CATEGORY_FOOD);
    wallet.addCategory(CATEGORY_ENTERTAINMENT);
    wallet.addCategory(CATEGORY_UTILITIES);
    wallet.addCategory(CATEGORY_TAXI);

    wallet.saveCategoryBudget(CATEGORY_FOOD, BUDGET_FOOD);
    wallet.saveCategoryBudget(CATEGORY_ENTERTAINMENT, BUDGET_ENTERTAINMENT);
    wallet.saveCategoryBudget(CATEGORY_UTILITIES, BUDGET_UTILITIES);

    // Расходы
    wallet.addExpenseTransaction(EXPENSE_FOOD_1, CATEGORY_FOOD);
    wallet.addExpenseTransaction(EXPENSE_FOOD_2, CATEGORY_FOOD);
    wallet.addExpenseTransaction(EXPENSE_ENTERTAINMENT, CATEGORY_ENTERTAINMENT);
    wallet.addExpenseTransaction(EXPENSE_UTILITIES, CATEGORY_UTILITIES);
    wallet.addExpenseTransaction(EXPENSE_TAXI, CATEGORY_TAXI);

    // Доходы
    wallet.addCategory(CATEGORY_SALARY);
    wallet.addCategory(CATEGORY_BONUS);
    wallet.addIncomeTransaction(INCOME_SALARY_1, CATEGORY_SALARY);
    wallet.addIncomeTransaction(INCOME_SALARY_2, CATEGORY_SALARY);
    wallet.addIncomeTransaction(INCOME_BONUS, CATEGORY_BONUS);
  }

  @Test
  void addCategoryAndHasCategory() {
    Wallet w = new Wallet();
    assertFalse(w.hasCategory(CATEGORY_FOOD));
    w.addCategory(CATEGORY_FOOD);
    assertTrue(w.hasCategory(CATEGORY_FOOD));
  }

  @Test
  void totalIncomesAreCounted() {
    assertEquals(EXPECTED_TOTAL_INCOMES, wallet.getTotalIncomes());
  }

  @Test
  void totalExpensesAreCounted() {
    assertEquals(EXPECTED_TOTAL_EXPENSES, wallet.getTotalExpenses());
  }

  @Test
  void incomesByCategory() {
    assertEquals(EXPECTED_INCOME_SALARY, wallet.getIncomesByCategory(CATEGORY_SALARY));
    assertEquals(EXPECTED_INCOME_BONUS, wallet.getIncomesByCategory(CATEGORY_BONUS));
    assertEquals(0, wallet.getIncomesByCategory(CATEGORY_FOOD));
  }

  @Test
  void expensesByCategory() {
    assertEquals(EXPECTED_EXPENSE_FOOD, wallet.getExpensesByCategory(CATEGORY_FOOD));
    assertEquals(
        EXPECTED_EXPENSE_ENTERTAINMENT, wallet.getExpensesByCategory(CATEGORY_ENTERTAINMENT));
    assertEquals(EXPECTED_EXPENSE_UTILITIES, wallet.getExpensesByCategory(CATEGORY_UTILITIES));
    assertEquals(EXPECTED_EXPENSE_TAXI, wallet.getExpensesByCategory(CATEGORY_TAXI));
  }

  @Test
  void getBudgetByCategory() {
    assertEquals(BUDGET_FOOD, wallet.getBudgetByCategory(CATEGORY_FOOD));
    assertEquals(BUDGET_ENTERTAINMENT, wallet.getBudgetByCategory(CATEGORY_ENTERTAINMENT));
    assertEquals(BUDGET_UTILITIES, wallet.getBudgetByCategory(CATEGORY_UTILITIES));
  }

  @Test
  void getRemainingBudgetPositive() {
    assertEquals(EXPECTED_REMAINING_BUDGET_FOOD, wallet.getRemainingBudget(CATEGORY_FOOD));
  }

  @Test
  void getRemainingBudgetZeroOrNegative() {
    assertEquals(
        EXPECTED_REMAINING_BUDGET_ENTERTAINMENT, wallet.getRemainingBudget(CATEGORY_ENTERTAINMENT));
    assertEquals(
        EXPECTED_REMAINING_BUDGET_UTILITIES, wallet.getRemainingBudget(CATEGORY_UTILITIES));
  }

  @Test
  void categoriesOrderIsPreserved() {
    Set<String> cats = wallet.getAllCategories();
    Iterator<String> it = cats.iterator();
    assertEquals(CATEGORY_FOOD, it.next());
    assertEquals(CATEGORY_ENTERTAINMENT, it.next());
    assertEquals(CATEGORY_UTILITIES, it.next());
    assertEquals(CATEGORY_TAXI, it.next());
    assertEquals(CATEGORY_SALARY, it.next());
    assertEquals(CATEGORY_BONUS, it.next());
  }

  @Test
  void lastCategoryIsCorrect() {
    assertEquals(CATEGORY_BONUS, wallet.getLastCategory());
    wallet.addCategory(CATEGORY_NEW);
    assertEquals(CATEGORY_NEW, wallet.getLastCategory());
  }

  @Test
  void saveCategoryBudgetUpdatesLimit() {
    wallet.saveCategoryBudget(CATEGORY_FOOD, BUDGET_FOOD_UPDATED);
    assertEquals(BUDGET_FOOD_UPDATED, wallet.getBudgetByCategory(CATEGORY_FOOD));
    assertEquals(
        BUDGET_FOOD_UPDATED - EXPECTED_EXPENSE_FOOD, wallet.getRemainingBudget(CATEGORY_FOOD));
  }

  @Test
  void copyFromCopiesState() {
    Wallet w2 = new Wallet();
    w2.addCategory(CATEGORY_X);
    w2.saveCategoryBudget(CATEGORY_X, BUDGET_X);
    w2.addExpenseTransaction(EXPENSE_X, CATEGORY_X);

    Wallet target = new Wallet();
    target.copyFrom(w2);

    assertTrue(target.hasCategory(CATEGORY_X));
    assertEquals(BUDGET_X, target.getBudgetByCategory(CATEGORY_X));
    assertEquals(EXPENSE_X, target.getExpensesByCategory(CATEGORY_X));
    assertEquals(EXPECTED_REMAINING_BUDGET_X, target.getRemainingBudget(CATEGORY_X));
  }
}
