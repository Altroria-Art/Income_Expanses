package incomeexpenseproject;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class IncomeExpenseGUI extends JFrame {
    private final IncomeExpenseProject.Ledger ledger = new IncomeExpenseProject.Ledger();
    private final JTextArea output = new JTextArea();

    public IncomeExpenseGUI() {
        setTitle("Income & Expense System");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::showMenuLoop);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IncomeExpenseGUI().setVisible(true));
    }

    private void showMenuLoop() {
        String[] options = {"Add Income", "Add Expense", "Show All", "Show Summary", "Exit"};
        while (true) {
            int ch = JOptionPane.showOptionDialog(
                    this, "Choose an action", "Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, options, options[0]);

            if (ch == 0) addIncomeDialog();
            else if (ch == 1) addExpenseDialog();
            else if (ch == 2) showAll();
            else if (ch == 3) showSummary();
            else break; // Exit หรือกดปิด
        }
    }

    private void addIncomeDialog() {
        String amt = JOptionPane.showInputDialog(this, "Amount (+):");
        if (amt == null) return;
        String note = JOptionPane.showInputDialog(this, "Note:");
        try {
            double a = Double.parseDouble(amt.trim());
            if (a < 0) throw new NumberFormatException();
            ledger.add(new IncomeExpenseProject.Income(LocalDate.now(), a, note == null ? "" : note));
            output.append("Income: +" + String.format("%.2f", a) + " [" + (note==null?"":note) + "]\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount (>= 0)");
        }
    }

    private void addExpenseDialog() {
        String amt = JOptionPane.showInputDialog(this, "Amount (+):");
        if (amt == null) return;
        String note = JOptionPane.showInputDialog(this, "Note:");
        try {
            double a = Double.parseDouble(amt.trim());
            if (a < 0) throw new NumberFormatException();
            ledger.add(new IncomeExpenseProject.Expense(LocalDate.now(), a, note == null ? "" : note));
            output.append("Expense: -" + String.format("%.2f", a) + " [" + (note==null?"":note) + "]\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount (>= 0)");
        }
    }

    private void showAll() {
        var items = ledger.getItems();
        if (items.isEmpty()) {
            output.append("(No transactions yet)\n");
            return;
        }
        output.append("\n-- All Transactions --\n");
        for (IncomeExpenseProject.Transaction t : items) {
            output.append(String.format("%s  %-7s %12.2f  %s%n",
                    t.getDate(), t.getType(), t.getSignedAmount(), t.getNote()));
        }
        output.append("\n");
    }

    private void showSummary() {
        output.append("\nSummary:\n");
        output.append("Income = " + String.format("%.2f", ledger.getTotalIncome()) + "\n");
        output.append("Expense = " + String.format("%.2f", ledger.getTotalExpense()) + "\n");
        output.append("Balance = " + String.format("%.2f", ledger.getBalance()) + "\n\n");
    }
}
