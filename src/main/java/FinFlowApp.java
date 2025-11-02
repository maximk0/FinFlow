import java.util.*;

public class FinFlowApp {

  private final Scanner scanner;
  private final AuthService authService;

  private boolean running = true;

  public FinFlowApp() {
    authService = new AuthService();
    scanner = new Scanner(System.in);
  }

  public void run() {
    printBanner();
    while (running) {
      showMenu();
      System.out.println();
    }
  }

  private void showMenu() {
    if (authService.getCurrentUser() == null) {
      printGuestMenu();
      System.out.print("> ");
      String cmd = readCmd();
      handleGuestCommand(cmd);
    } else {
      printUserMenu(authService.getCurrentUser());
      System.out.print("> ");
      String cmd = readCmd();
      handleUserCommand(cmd);
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
    System.out.println("  register        — регистрация нового пользователя");
    System.out.println("  login           — вход по логину и паролю");
    System.out.println("  help            — показать подсказку");
    System.out.println("  exit            — выйти из приложения");
    System.out.println();
  }

  private void printUserMenu(User user) {
    System.out.println("[" + user + "] Доступные команды:");
    System.out.println("  addcat          — добавить категорию расходов/доходов");
    System.out.println("  addinc          — добавить доход");
    System.out.println("  addexp          — добавить расход");
    System.out.println("  setbudget       — установить бюджет по категории");
    System.out.println("  stats           — итоги по доходам/расходам и бюджетам");
    System.out.println("  stats cat       — итоги по категориям (доходы/расходы)");
    System.out.println("  stats cats A,B  — итоги по выбранным категориям");
    System.out.println("  wallets         — показать инфо по кошельку");
    System.out.println("  transfer        — перевести между пользователями (доп.задание)");
    System.out.println("  save            — сохранить данные в файл");
    System.out.println("  logout          — выйти из аккаунта");
    System.out.println("  help            — показать подсказку");
    System.out.println("  exit            — выйти из приложения");
    System.out.println();
  }

  private String readCmd() {
    String line = scanner.nextLine();
    return line == null ? "" : line.trim();
  }

  private void handleGuestCommand(String cmd) {
    switch (cmd.toLowerCase()) {
      case "register" -> doRegister();
      case "login" -> doLogin();
      case "help" -> helpGuest();
      case "exit" -> doExit();
      default -> System.out.println("Неизвестная команда. Введите 'help' для справки.");
    }
  }

  private void handleUserCommand(String cmd) {
    switch (cmd.toLowerCase()) {
      case "addcat" -> doAddCategory();
      case "addinc" -> doAddIncome();
      case "addexp" -> doAddExpense();
      case "setbudget" -> doSetBudget();
      case "stats" -> doStats("");
      case "stats cat" -> doStats("cat");
      default -> {
        if (cmd.toLowerCase().startsWith("stats cats")) {
          String tail = cmd.substring("stats cats".length()).trim();
          doStatsSelected(tail);
        } else {
          switch (cmd.toLowerCase()) {
            case "wallets" -> doWallet();
            case "transfer" -> doTransfer();
            case "save" -> doSave();
            case "logout" -> doLogout();
            case "help" -> helpUser();
            case "exit" -> doExit();
            default -> System.out.println("Неизвестная команда. Введите 'help' для справки.");
          }
        }
      }
    }
  }

  private void doRegister() {
    String login;
    do {
      System.out.print("Введите логин (или 'back' для выхода в меню): ");
      login = readCmd();

      if (needBack(login)) return;

      if (authService.checkLogin(login)) {
        System.out.println("Пользователь с таким логином уже существует!");
        System.out.println("Введите другой логин (или 'back' для выхода в меню):");
      }
    } while (authService.checkLogin(login));

    System.out.print("Введите пароль: ");
    String password = readCmd();
    User user = new User(login, password);
    authService.saveUser(user);
    System.out.println("✅ Пользователь " + user + " зарегистрирован. Выполните login.");
  }

  private void doLogin() {
    String login;

    do {
      System.out.print("Введите логин (или 'back' для выхода в меню): ");
      login = readCmd();

      if (needBack(login)) return;

      if (!authService.checkLogin(login))
        System.out.println("Пользователь с таким логином не найден!");
    } while (!authService.checkLogin(login));

    String password;
    do {
      System.out.print("Введите пароль (или 'back' для выхода в меню): ");
      password = readCmd();

      if (needBack(password)) return;
      if (!authService.checkPassword(login, password)) System.out.println("Пароль введён неверно!");

    } while (!authService.checkPassword(login, password));

    authService.setCurrentUser(login);
    System.out.println("Добро пожаловать, " + authService.getCurrentUser().getLogin() + "!");
  }

  private Boolean needBack(String input) {
    Boolean hasBack = input.equalsIgnoreCase("back");
    if (hasBack) System.out.println("Возврат в главное меню.");
    return hasBack;
  }

  private void doLogout() {
    System.out.println("Вы вышли из аккаунта " + authService.getCurrentUser() + ".");
    authService.logout();
  }

  private void doAddCategory() {
    System.out.print("Название категории: ");
    String name = readCmd();
    System.out.print("Тип (inc/exp): ");
    String type = readCmd();
    // позже: сохранить категорию в кошельке пользователя
    System.out.printf(
        "✅ Категория '%s' (%s) добавлена.%n",
        name, type.equalsIgnoreCase("inc") ? "доходы" : "расходы");
  }

  private void doAddIncome() {
    System.out.print("Категория: ");
    String cat = readCmd();
    System.out.print("Сумма: ");
    String amt = readCmd();
    System.out.print("Комментарий (опционально): ");
    String note = readCmd();
    // позже: валидация суммы и запись транзакции
    System.out.println("✅ Доход записан.");
  }

  private void doAddExpense() {
    System.out.print("Категория: ");
    String cat = readCmd();
    System.out.print("Сумма: ");
    String amt = readCmd();
    System.out.print("Комментарий (опционально): ");
    String note = readCmd();
    // позже: валидация суммы, проверка и уведомление о превышении бюджета
    System.out.println("✅ Расход записан.");
  }

  private void doSetBudget() {
    System.out.print("Категория (расходы): ");
    String cat = readCmd();
    System.out.print("Лимит в месяц: ");
    String lim = readCmd();
    // позже: сохранить бюджет, проверить факт превышения
    System.out.printf("✅ Бюджет по '%s' установлен: %s%n", cat, lim);
    // пример предупреждения:
    // System.out.println("⚠️ Превышение! Текущие траты: 3000.00, лимит: 2500.00, перерасход:
    // 500.00");
  }

  private void doStats(String mode) {
    if (mode.isEmpty()) {
      System.out.println("Общий доход: 63000.00");
      System.out.println("Общие расходы: 8300.00");
      System.out.println("Баланс: 54700.00");
      System.out.println();
      System.out.println("Доходы по категориям:");
      System.out.println("  Бонус: 3000.00");
      System.out.println("  Зарплата: 60000.00");
      System.out.println();
      System.out.println("Бюджет по категориям (расходы):");
      System.out.println("  Коммунальные услуги: 2500.00, Оставшийся бюджет: -500.00");
      System.out.println("  Еда: 4000.00, Оставшийся бюджет: 3200.00");
      System.out.println("  Развлечения: 3000.00, Оставшийся бюджет: 0.00");
    } else if (mode.equals("cat")) {
      System.out.println("Расходы по категориям:");
      System.out.println("  Еда: 800.00");
      System.out.println("  Развлечения: 3000.00");
      System.out.println("  Коммунальные услуги: 3000.00");
      System.out.println("  Такси: 1500.00");
    }
  }

  private void doStatsSelected(String csv) {
    if (csv.isEmpty()) {
      System.out.println("Укажите категории через запятую: пример 'stats cats Еда,Такси'");
      return;
    }
    System.out.println("Итоги по выбранным категориям: " + csv);
    // позже: парсинг, суммирование, обработка «категория не найдена»
  }

  private void doWallet() {
    System.out.println("Кошелёк: баланс 54700.00, операций: 9, бюджетных категорий: 3");
    // позже: реальное состояние
  }

  private void doTransfer() {
    System.out.print("Кому (логин): ");
    String to = readCmd();
    System.out.print("Сумма: ");
    String amt = readCmd();
    System.out.print("Комментарий (опционально): ");
    String note = readCmd();
    // позже: проверка существования получателя, запись расхода у отправителя и дохода у получателя
    System.out.printf("✅ Перевод пользователю %s на сумму %s создан (демо).%n", to, amt);
  }

  private void doSave() {
    // позже: сериализация данных текущего пользователя в файл user.finflow.json
    System.out.println("✅ Данные сохранены (user1.finflow.json).");
  }

  private void doExit() {
    System.out.println("До встречи!");
    running = false;
  }

  private void helpGuest() {
    printGuestMenu();
  }

  private void helpUser() {
    printUserMenu(authService.getCurrentUser());
  }
}
