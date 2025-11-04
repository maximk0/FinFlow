import java.io.Serializable;
import java.util.*;

public class Wallet implements Serializable {
  private final List<Transaction> transactions = new ArrayList<>();
  private final Map<String, Category> categories = new LinkedHashMap<>();

  public Set<String> getAllCategories() {
    return categories.keySet();
  }

  public String getLastCategory() {
    if (categories.isEmpty()) {
      return null;
    }

    String lastKey = null;
    for (String key : categories.keySet()) {
      lastKey = key;
    }
    return lastKey;
  }

  public void addCategory(String categoryName) {
    categories.put(categoryName, new Category(categoryName));
  }

  public void addExpenseTransaction(int sum, String categoryName) {
    addTransaction(sum, categoryName, TransactionType.EXPENSE);
  }

  public void addIncomeTransaction(int sum, String categoryName) {
    addTransaction(sum, categoryName, TransactionType.INCOME);
  }

  public void saveCategoryBudget(String categoryName, int budget) {
    getCategory(categoryName).setBudget(budget);
  }

  public int getTotalExpenses() {
    return transactions.stream()
        .filter(transaction -> transaction.type() == TransactionType.EXPENSE)
        .mapToInt(Transaction::sum)
        .sum();
  }

  public int getTotalIncomes() {
    return transactions.stream()
        .filter(transaction -> transaction.type() == TransactionType.INCOME)
        .mapToInt(Transaction::sum)
        .sum();
  }

  public int getExpensesByCategory(String categoryName) {
    return transactions.stream()
        .filter(transaction -> transaction.type() == TransactionType.EXPENSE)
        .filter(transaction -> transaction.category().getName().equals(categoryName))
        .mapToInt(Transaction::sum)
        .sum();
  }

  public int getIncomesByCategory(String categoryName) {
    return transactions.stream()
        .filter(transaction -> transaction.type() == TransactionType.INCOME)
        .filter(transaction -> transaction.category().getName().equals(categoryName))
        .mapToInt(Transaction::sum)
        .sum();
  }

  public boolean hasCategory(String categoryName) {
    return getCategory(categoryName) != null;
  }

  public int getBudgetByCategory(String categoryName) {
    return categories.get(categoryName).getBudget();
  }

  public int getRemainingBudget(String categoryName) {
    Category category = categories.get(categoryName);

    int spent = getExpensesByCategory(categoryName);
    return category.getBudget() - spent;
  }

  public void copyFrom(Wallet other) {
    this.transactions.clear();
    this.transactions.addAll(other.transactions);
    this.categories.clear();
    this.categories.putAll(other.categories);
  }

  private void addTransaction(int sum, String categoryName, TransactionType type) {
    transactions.add(new Transaction(sum, getCategory(categoryName), type));
  }

  private Category getCategory(String categoryName) {
    return categories.get(categoryName);
  }
}
