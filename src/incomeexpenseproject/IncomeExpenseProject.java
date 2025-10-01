package incomeexpenseproject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IncomeExpenseProject {
    private static final Scanner sc = new Scanner(System.in);
    private static final Ledger ledger = new Ledger();

    public static void main(String[] args) {
        while (true) {
            printMenu();
            int choice = readInt("Choose menu: ");
            switch (choice) {
                case 1 -> addIncome();
                case 2 -> addExpense();
                case 3 -> listAll();
                case 4 -> showSummary();
                case 0 -> {
                    System.out.println("Exit program");
                    return;
                }
                default -> System.out.println("Invalid menu");
            }
            pauseEnter();
        }
    }

    private static void printMenu() {
        System.out.println("\n======= Income & Expense System =======");
        System.out.println("1) Add Income");
        System.out.println("2) Add Expense");
        System.out.println("3) Show All Transactions");
        System.out.println("4) Show Summary");
        System.out.println("0) Exit");
    }

    private static void addIncome() {
        System.out.println("\n-- Add Income --");
        LocalDate date = LocalDate.now();
        double amount = readDouble("Amount (+): ");
        String note = readLine("Note: ");
        ledger.add(new Income(date, amount, note));
    }

    private static void addExpense() {
        System.out.println("\n-- Add Expense --");
        LocalDate date = LocalDate.now();
        double amount = readDouble("Amount (+): ");
        String note = readLine("Note: ");
        ledger.add(new Expense(date, amount, note));
    }

    private static void listAll() {
        System.out.println("\n-- All Transactions --");
        var items = ledger.getItems();
        if (items.isEmpty()) {
            System.out.println("(No transactions yet)");
            return;
        }
        System.out.printf("%-12s %-8s %12s  %s%n", "Date", "Type", "Amount", "Note");
        for (Transaction t : items) {
            System.out.printf("%-12s %-8s %12.2f  %s%n",
                    t.getDate(), t.getType(), t.getSignedAmount(), t.getNote());
        }
    }

    private static void showSummary() {
        System.out.println("\n-- Summary --");
        System.out.println("Total Income : " + ledger.getTotalIncome());
        System.out.println("Total Expense: " + ledger.getTotalExpense());
        System.out.println("Balance      : " + ledger.getBalance());
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double d = Double.parseDouble(sc.nextLine().trim());
                if (d >= 0) return d;
            } catch (NumberFormatException ignored) {}
            System.out.println("Please enter a valid amount (>= 0)");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private static void pauseEnter() {
        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
    }

    static abstract class Transaction {
        private final LocalDate date;
        private final double amount;
        private final String note;

        public Transaction(LocalDate date, double amount, String note) {
            if (amount < 0) {
                throw new IllegalArgumentException("amount must be >= 0");
            }
            this.date = (date != null) ? date : LocalDate.now();
            this.amount = amount;
            this.note = (note == null) ? "" : note.trim();
        }

        public LocalDate getDate() { return date; }
        public double getAmount() { return amount; }
        public String getNote() { return note; }

        public abstract double getSignedAmount();
        public abstract String getType();
    }

    static class Income extends Transaction {
        public Income(LocalDate date, double amount, String note) {
            super(date, amount, note);
        }
        @Override public double getSignedAmount() { return getAmount(); }
        @Override public String getType() { return "INCOME"; }
    }

    static class Expense extends Transaction {
        public Expense(LocalDate date, double amount, String note) {
            super(date, amount, note);
        }
        @Override public double getSignedAmount() { return -getAmount(); }
        @Override public String getType() { return "EXPENSE"; }
    }

    static class Ledger {
        private final List<Transaction> items = new ArrayList<>();

        public void add(Transaction t) { items.add(t); }
        public List<Transaction> getItems() { return List.copyOf(items); }

        public double getTotalIncome() {
            return items.stream()
                    .filter(t -> t instanceof Income)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        public double getTotalExpense() {
            return items.stream()
                    .filter(t -> t instanceof Expense)
                    .mapToDouble(Transaction::getAmount)
                    .sum();
        }

        public double getBalance() {
            return items.stream()
                    .mapToDouble(Transaction::getSignedAmount)
                    .sum();
        }
    }
}
