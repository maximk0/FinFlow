import java.io.Serializable;

public class Category implements Serializable {
    private final String name;
    private int budget = 0;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getBudget() {
        return budget;
    }
}
