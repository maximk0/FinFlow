import java.util.HashSet;
import java.util.Set;

public class Wallet {
  Set<String> categories = new HashSet<>();

  //    Map<String, Double>

  public void addCategory(String category) {
    categories.add(category);
  }
}
