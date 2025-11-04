import java.io.Serializable;

public record Transaction(int sum, Category category, TransactionType type)
    implements Serializable {}
