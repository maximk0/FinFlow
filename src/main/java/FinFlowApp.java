import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class FinFlowApp {
  private static final String CMD_REGISTER = "register";
  private static final String CMD_LOGIN = "login";
  private static final String CMD_HELP = "help";
  private static final String CMD_EXIT = "exit";
  private static final String CMD_ADD_CAT = "addcat";
  private static final String CMD_ADD_INC = "addinc";
  private static final String CMD_ADD_EXP = "addexp";
  private static final String CMD_SET_BUDGET = "setbudget";
  private static final String CMD_STATS = "stats";
  private static final String CMD_STATS_CAT = "stats cat";
  private static final String CMD_STATS_TO_FILE = "stats file";
  private static final String CMD_STATS_CATS = "stats cats";
  private static final String CMD_LOGOUT = "logout";

  private final Scanner scanner;
  private final AuthService authService;
  private final FileService fileService;

  private boolean running = true;

  public FinFlowApp() {
    authService = new AuthService();
    scanner = new Scanner(System.in);
    fileService = new FileService();
  }

  public void run() {
    printBanner();
    printGuestMenu();
    while (running) {
      System.out.print("> ");
      String cmd = readLine();
      if (authService.getCurrentUser() == null) handleGuestCommand(cmd.toLowerCase());
      else handleUserCommand(cmd.toLowerCase());
      System.out.println();
    }
  }

  private void printBanner() {
    System.out.println("=======================================================");
    System.out.println("FinFlow — приложение для управления личными финансами");
    String VERSION = "0.1 (demo UI)";
    System.out.println("Версия: " + VERSION);
    System.out.println("=======================================================");
    System.out.println();
  }

  private void printGuestMenu() {
    System.out.println("[Гость] Доступные команды:");
    System.out.println("  " + CMD_REGISTER + "        — регистрация нового пользователя");
    System.out.println("  " + CMD_LOGIN + "           — вход по логину и паролю");
    System.out.println("  " + CMD_HELP + "            — показать подсказку");
    System.out.println("  " + CMD_EXIT + "            — выйти из приложения");
    System.out.println();
  }

  private void printUserMenu() {
    System.out.println("[" + authService.getCurrentUser() + "] Доступные команды:");
    System.out.println("  " + CMD_ADD_CAT + "          — добавить категорию расходов/доходов");
    System.out.println("  " + CMD_SET_BUDGET + "       — установить/изменить бюджет по категории");
    System.out.println("  " + CMD_ADD_INC + "          — добавить доход");
    System.out.println("  " + CMD_ADD_EXP + "          — добавить расход");
    System.out.println("  " + CMD_STATS + "            — итоги по доходам/расходам и бюджетам");
    System.out.println("  " + CMD_STATS_CAT + "        — итоги по категориям (доходы/расходы)");
    System.out.println("  " + CMD_STATS_CATS + "       — итоги по выбранным категориям");
    System.out.println("  " + CMD_STATS_TO_FILE + "    — сохранить отчёт в файл");
    System.out.println("  " + CMD_LOGOUT + "           — выйти из аккаунта");
    System.out.println("  " + CMD_HELP + "             — показать подсказку");
    System.out.println("  " + CMD_EXIT + "             — выйти из приложения");
    System.out.println();
  }

  private void handleGuestCommand(String cmd) {
    switch (cmd.toLowerCase()) {
      case CMD_REGISTER -> doRegister();
      case CMD_LOGIN -> doLogin();
      case CMD_HELP -> helpGuest();
      case CMD_EXIT -> doExit();
      default -> System.out.println("Неизвестная команда. Введите '" + CMD_HELP + "' для справки.");
    }
  }

  private void handleUserCommand(String cmd) {
    switch (cmd) {
      case CMD_ADD_CAT -> doAddCategory();
      case CMD_ADD_INC -> doAddIncome();
      case CMD_ADD_EXP -> doAddExpense();
      case CMD_SET_BUDGET -> doSetBudget();
      case CMD_STATS -> doTotalStats();
      case CMD_STATS_CAT -> doCategoryStats();
      case CMD_STATS_CATS -> doStatsSelected();
      case CMD_STATS_TO_FILE -> doStatsToFile();
      case CMD_LOGOUT -> doLogout();
      case CMD_HELP -> printUserMenu();
      case CMD_EXIT -> doExit();
      default -> System.out.println("Неизвестная команда. Введите '" + CMD_HELP + "' для справки.");
    }
  }

  private String readLine() {
    String line = scanner.nextLine();
    return line == null ? "" : line.trim();
  }

  private int parseNumber() {
    Integer number;
    do {
      String line = readLine();
      try {
        number = Integer.parseInt(line);
      } catch (NumberFormatException e) {
        System.out.println("Введите только число, не используя другие символы: ");
        number = null;
      }
    } while (number == null);
    return number > 0 ? number : -1;
  }

  private void doRegister() {
    if (authService.getCurrentUser() != null) {
      System.out.println(
          "Чтобы зарегистрировать нового пользователя необходимо выполнить команду 'logout'");
      return;
    }

    String login;
    do {
      System.out.print("Введите логин (или 'back' для выхода в меню): ");
      login = readLine();

      if (needBack(login)) return;

      if (authService.checkLogin(login)) {
        System.out.println("Пользователь с таким логином уже существует!");
        System.out.println("Введите другой логин (или 'back' для выхода в меню):");
      }
    } while (authService.checkLogin(login));

    System.out.print("Введите пароль: ");
    String password = readLine();
    User user = new User(login, password);
    authService.saveUser(user);
    System.out.println(
        "Пользователь "
            + user
            + " зарегистрирован. Используйте команду login, чтобы авторизоваться.");
  }

  private void doLogin() {
    String login;

    do {
      System.out.print("Введите логин (или 'back' для выхода в меню): ");
      login = readLine();

      if (needBack(login)) return;

      if (!authService.checkLogin(login))
        System.out.println("Пользователь с таким логином не найден!");
    } while (!authService.checkLogin(login));

    String password;
    do {
      System.out.print("Введите пароль (или 'back' для выхода в меню): ");
      password = readLine();

      if (needBack(password)) return;
      if (!authService.checkPassword(login, password)) System.out.println("Пароль введён неверно!");

    } while (!authService.checkPassword(login, password));

    authService.setCurrentUser(login);

    fileService.loadWallet(login, wallet());

    System.out.println("Добро пожаловать, " + authService.getCurrentUser().getLogin() + "!");
  }

  private Boolean needBack(String input) {
    Boolean hasBack = input.equalsIgnoreCase("back");
    if (hasBack) System.out.println("Возврат в главное меню.");
    return hasBack;
  }

  private void doLogout() {
    fileService.saveCurrentWallet(authService.getCurrentUser());
    System.out.println("Вы вышли из аккаунта " + authService.getCurrentUser() + ".");
    authService.logout();
  }

  private void doAddCategory() {
    System.out.print("Название категории: ");
    String categoryName = getCategoryFromUser(false);

    if (categoryName == null) return;

    authService.getCurrentUser().getWallet().addCategory(categoryName);
    System.out.println("Категория " + categoryName + " добавлена.");
  }

  private void doAddIncome() {
    System.out.print("Категория: ");
    String categoryName = getCategoryFromUser(true);

    if (categoryName == null) return;

    System.out.print("Сумма: ");
    int sum = getPositiveNumberFromUser();

    wallet().addIncomeTransaction(sum, categoryName);

    checkCategoryBudget(categoryName);
    checkTotalBudget();

    System.out.println("Доход записан.");
  }

  private void doAddExpense() {
    System.out.print("Категория: ");
    String categoryName = getCategoryFromUser(true);

    if (categoryName == null) return;

    System.out.print("Сумма: ");
    int sum = getPositiveNumberFromUser();

    wallet().addExpenseTransaction(sum, categoryName);

    checkCategoryBudget(categoryName);
    checkTotalBudget();

    System.out.println("Расход записан.");
  }

  private void doSetBudget() {
    System.out.print("Категория: ");
    String categoryName = getCategoryFromUser(true);
    System.out.print("Лимит в месяц: ");
    int budget = getPositiveNumberFromUser();

    wallet().saveCategoryBudget(categoryName, budget);
    System.out.printf("Бюджет по категории '%s' установлен: %s%n", categoryName, budget);
  }

  private void doTotalStats() {
    Wallet wallet = wallet();
    int totalIncomes = wallet.getTotalIncomes();
    int totalExpenses = wallet.getTotalExpenses();
    System.out.println("Общий доход: " + totalIncomes);
    System.out.println("Общие расходы: " + totalExpenses);
    System.out.println("Баланс: " + (totalIncomes - totalExpenses));
    System.out.println();
  }

  private void doCategoryStats() {
    Wallet wallet = wallet();
    System.out.println("Доходы по категориям:");
    Set<String> allCategories = wallet.getAllCategories();
    allCategories.forEach(
        category ->
            System.out.println("    " + category + ": " + wallet.getIncomesByCategory(category)));
    System.out.println();

    System.out.println("Расходы по категориям:");
    allCategories.forEach(
        category ->
            System.out.println("    " + category + ": " + wallet.getExpensesByCategory(category)));
    System.out.println();

    System.out.println("Бюджет по категориям (расходы):");
    allCategories.forEach(
        category -> {
          int budget = wallet.getBudgetByCategory(category);
          int remainingBudget = wallet.getRemainingBudget(category);
          System.out.printf(
              "    %s: %d. Оставшийся бюджет: %d%n", category, budget, remainingBudget);
          checkCategoryBudget(category);
        });
  }

  private void doStatsSelected() {
    String userCategories;
    do {
      System.out.print("Введите категории через запятую (например: Еда,Такси): ");
      userCategories = readLine();
      if (userCategories.isBlank()) {
        System.out.println(
            " Поле не может быть пустым. Попробуйте снова (или введите 'back' для выхода в меню).");
      }
    } while (userCategories.isBlank());

    String[] parts = userCategories.split(",");
    List<String> categories = new ArrayList<>();
    for (String category : parts) {
      String categoryName = category.trim();
      if (!categoryName.isEmpty() && !wallet().hasCategory(category)) categories.add(categoryName);
      else System.out.println("Категория " + categoryName + " не найдена.");
    }

    if (categories.isEmpty()) {
      System.out.println(
          " Категории не указаны или указаны не существующие категории. Команда отменена.");
      return;
    }

    System.out.println("Итоги по выбранным категориям:");
    int totalIncome = 0;
    int totalExpense = 0;

    for (String category : categories) {
      int income = wallet().getIncomesByCategory(category);
      int expense = wallet().getExpensesByCategory(category);
      int budget = wallet().getBudgetByCategory(category);
      int remaining = wallet().getRemainingBudget(category);

      totalIncome += income;
      totalExpense += expense;

      System.out.printf(
          "  • %s:%n"
              + "      Доходы: %d%n"
              + "      Расходы: %d%n"
              + "      Бюджет: %d%n"
              + "      Остаток бюджета: %d%n",
          category, income, expense, budget, remaining);

      if (remaining < 0)
        System.out.printf(
            "!!! Внимание, бюджет по категории %s превышен на %d !!!", category, -remaining);
    }

    System.out.println("---------------------------------");
    System.out.printf("Суммарные доходы по выбранным категориям: %d%n", totalIncome);
    System.out.printf("Суммарные расходы по выбранным категориям: %d%n", totalExpense);
    System.out.printf("Баланс по выбранным категориям: %d%n", (totalIncome - totalExpense));

    checkTotalBudget();
  }

  private void doStatsToFile() {
    Path reportsPath = Paths.get("reports");
    try {
      Files.createDirectories(reportsPath);
    } catch (IOException e) {
      System.out.println("Не удалось создать каталог для отчётов: " + reportsPath.toAbsolutePath());
      return;
    }

    String login = authService.getCurrentUser().getLogin();
    String date = LocalDate.now().toString();
    Path file = reportsPath.resolve(login + "_report_" + date + ".csv");

    try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
        PrintWriter out = new PrintWriter(bw)) {

      out.print('\uFEFF');

      out.println("Категория,Доходы,Расходы,Бюджет,Остаток");

      int totalIncomes = 0;
      int totalExpenses = 0;
      for (String c : wallet().getAllCategories()) {
        int inc = wallet().getIncomesByCategory(c);
        int exp = wallet().getExpensesByCategory(c);
        int budget = wallet().getBudgetByCategory(c);
        int remaining = wallet().getRemainingBudget(c);

        totalIncomes += inc;
        totalExpenses += exp;

        out.printf("%s,%d,%d,%d,%d%n", csv(c), inc, exp, budget, remaining);
      }

      out.println();

      out.println("Показатель,Значение");
      out.printf("Общий доход,%d%n", totalIncomes);
      out.printf("Общие расходы,%d%n", totalExpenses);
      out.printf("Баланс,%d%n", (totalIncomes - totalExpenses));

    } catch (IOException e) {
      System.out.println("Ошибка сохранения отчёта: " + e.getMessage());
      return;
    }

    System.out.println("Отчёт (CSV) сохранён: " + file.toAbsolutePath());
  }

  private String csv(String s) {
    if (s == null) return "\"\"";
    String t = s.replace("\"", "\"\"");
    return "\"" + t + "\"";
  }

  private void doExit() {
    fileService.saveCurrentWallet(authService.getCurrentUser());
    System.out.println("До встречи!");
    running = false;
  }

  private void helpGuest() {
    printGuestMenu();
  }

  private Wallet wallet() {
    return authService.getCurrentUser().getWallet();
  }

  public int getPositiveNumberFromUser() {
    int sum;
    do {
      sum = parseNumber();

      if (sum <= 0) {
        System.out.println("Число не может быть отрицательным.");
        System.out.println("Введите положительное число: ");
      }
    } while (sum <= 0);
    return sum;
  }

  private String getCategoryFromUser(boolean mustExist) {
    String categoryName;
    do {
      Wallet wallet = wallet();
      categoryName = readLine();

      if (needBack(categoryName)) return null;

      if (categoryName.equalsIgnoreCase(CMD_ADD_CAT)) {
        doAddCategory();
        categoryName = wallet.getLastCategory();
        continue;
      }

      if (categoryName.isBlank()) {
        System.out.println("Название не может быть пустым.");
        System.out.println("Введите название категории (или 'back' для выхода в меню): ");
        continue;
      }

      if (!wallet.hasCategory(categoryName) && mustExist) {
        System.out.println("Такой категории не существует.");
        System.out.println(
            "Введите 'back' для выхода в меню или 'addcat', чтобы добавить категорию .");
        categoryName = "";
      }
    } while (categoryName.isBlank());

    return categoryName;
  }

  private void checkCategoryBudget(String categoryName) {
    Wallet wallet = wallet();
    int budget = wallet.getBudgetByCategory(categoryName);
    int remainingBudget = wallet.getRemainingBudget(categoryName);
    if (budget > 0 && remainingBudget < 0) {
      System.out.printf(
          "!!! Внимание, бюджет по категории %s превышен на %d !!!",
          categoryName, -remainingBudget);
      System.out.println("Чтобы скорректировать, используйте команду 'setbudget'");
    }
  }

  private void checkTotalBudget() {
    Wallet wallet = wallet();
    int totalIncomes = wallet.getTotalIncomes();
    int totalExpenses = wallet.getTotalExpenses();
    if (totalExpenses > totalIncomes) {
      System.out.println(
          "!!! Внимание, расходы ("
              + totalExpenses
              + ") превысили доходы("
              + totalIncomes
              + ") на ("
              + (totalExpenses - totalIncomes)
              + ")!!!");
    }
  }
}
