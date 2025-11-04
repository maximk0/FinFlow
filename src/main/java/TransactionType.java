public enum TransactionType {
    INCOME,
    EXPENSE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
