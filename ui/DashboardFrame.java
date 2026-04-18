package ui;

import database.TransactionDAO;
import model.BankingService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final String userId;
    private final String userName;

    private JPanel contentPanel;

    public DashboardFrame(String userId, String userName) {
        this.userId   = userId;
        this.userName = userName;
        initUI();
    }

    private void initUI() {
        setTitle("SecureBank  Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 660);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);

        root.add(buildSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(UITheme.BG_DARK);

        contentPanel.add(buildHomePanel(),        "HOME");
        contentPanel.add(buildCreateAccPanel(),   "CREATE");
        contentPanel.add(buildDepositPanel(),     "DEPOSIT");
        contentPanel.add(buildWithdrawPanel(),    "WITHDRAW");
        contentPanel.add(buildTransferPanel(),    "TRANSFER");
        contentPanel.add(buildHistoryPanel(),     "HISTORY");
        contentPanel.add(buildAccountsPanel(),    "ACCOUNTS");

        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
        showCard("HOME");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.BG_CARD);
        sidebar.setPreferredSize(new Dimension(220, 660));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 16, 30, 16));

        // Bank logo
        JLabel logo = UITheme.createLabel(" SecureBank", UITheme.FONT_SUBTITLE, UITheme.ACCENT_GOLD);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(6));

        JLabel welcome = UITheme.createLabel("Hello, " + userName.split(" ")[0],
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        welcome.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(welcome);
        sidebar.add(Box.createVerticalStrut(30));

        // Nav buttons using lambdas
        String[][] nav = {
            {"  Home",             "HOME"},
            {"  Create Account",   "CREATE"},
            {"  Deposit",          "DEPOSIT"},
            {"  Withdraw",         "WITHDRAW"},
            {"  Transfer",          "TRANSFER"},
            {"  History",           "HISTORY"},
            {"  My Accounts",       "ACCOUNTS"},
        };

        for (String[] item : nav) {
            JButton btn = createNavButton(item[0]);
            btn.addActionListener(e -> showCard(item[1]));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(6));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout button
        JButton logout = UITheme.dangerButton("  Logout");
        logout.setAlignmentX(CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(200, 40));
        logout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        sidebar.add(logout);
        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(UITheme.TEXT_PRIMARY);
        btn.setBackground(UITheme.BG_DARK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 8));
        return btn;
    }

    private void showCard(String name) {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, name);
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = UITheme.createLabel("Welcome, " + userName + "  ",
                UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 3, 16, 16));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        List<String[]> accounts = TransactionDAO.getUserAccounts(userId);
        double totalBal = accounts.stream()
                .mapToDouble(a -> Double.parseDouble(a[2])).sum();

        addStatCard(cards, "Total Balance",  "Rs " + String.format("%.2f", totalBal), UITheme.ACCENT_GOLD);
        addStatCard(cards, "Accounts",       String.valueOf(accounts.size()),          UITheme.ACCENT_BLUE);
        addStatCard(cards, "User ID",        userId,                                   UITheme.ACCENT_GREEN);
        addStatCard(cards, "Bank",           "SecureBank",                             UITheme.TEXT_MUTED);
        addStatCard(cards, "Status",         " Active",                              UITheme.ACCENT_GREEN);
        addStatCard(cards, "Support",        "1800-SEC-BANK",                          UITheme.TEXT_MUTED);

        p.add(cards, BorderLayout.CENTER);
        return p;
    }

    private void addStatCard(JPanel parent, String label, String value, Color accent) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel lbl = UITheme.createLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel val = UITheme.createLabel(value, UITheme.FONT_SUBTITLE, accent);
        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(val);
        parent.add(card);
    }

    private JPanel buildCreateAccPanel() {
        JPanel p = formPanel("Create New Account");

        String[] types = {"SAVINGS", "CURRENT", "FIXED_DEPOSIT"};
        JComboBox<String> typeBox = new JComboBox<>(types);
        styleCombo(typeBox);
        JTextField depositField = UITheme.createTextField(20);

        JLabel descLabel = UITheme.createLabel(getTypeDesc("SAVINGS"),
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);

        typeBox.addActionListener(e ->
            descLabel.setText(getTypeDesc((String) typeBox.getSelectedItem())));

        JButton createBtn = UITheme.primaryButton("Create Account");
        createBtn.addActionListener(e -> {                    
            try {
                double dep = Double.parseDouble(depositField.getText().trim());
                if (dep < 500) { UITheme.showMessage(p, "Minimum initial deposit is Rs 500.", false); return; }
                String accNo = BankingService.createAccount(userId,
                        (String) typeBox.getSelectedItem(), dep);
                if (accNo != null) {
                    UITheme.showMessage(p, " Account created!\nAccount No: " + accNo, true);
                    depositField.setText("");
                } else UITheme.showMessage(p, "Failed to create account.", false);
            } catch (NumberFormatException ex) {
                UITheme.showMessage(p, "Enter a valid deposit amount.", false);
            }
        });

        addRow(p, "Account Type:", typeBox);
        addRow(p, "Initial Deposit (Rs):", depositField);
        p.add(descLabel);
        p.add(Box.createVerticalStrut(16));
        p.add(createBtn);
        return p;
    }


    private JPanel buildDepositPanel() {
        JPanel p = formPanel("Deposit Money");
        JTextField accField  = UITheme.createTextField(20);
        JTextField amtField  = UITheme.createTextField(20);
        JButton    depBtn    = UITheme.successButton("Deposit ");

        depBtn.addActionListener(e -> {                        
            try {
                String acc = accField.getText().trim();
                double amt = Double.parseDouble(amtField.getText().trim());
                double bal = BankingService.deposit(acc, amt);
                if (bal >= 0) UITheme.showMessage(p,
                        " Deposited Rs" + amt + "\nNew Balance: Rs" + String.format("%.2f", bal), true);
                else UITheme.showMessage(p, "Deposit failed. Check account number.", false);
            } catch (NumberFormatException ex) {
                UITheme.showMessage(p, "Enter a valid amount.", false);
            }
        });

        addRow(p, "Account Number:", accField);
        addRow(p, "Amount (Rs):", amtField);
        p.add(Box.createVerticalStrut(16));
        p.add(depBtn);
        return p;
    }

    private JPanel buildWithdrawPanel() {
        JPanel p = formPanel("Withdraw Money");
        JTextField accField = UITheme.createTextField(20);
        JTextField amtField = UITheme.createTextField(20);
        JButton    wdBtn    = UITheme.dangerButton("Withdraw ");

        wdBtn.addActionListener(e -> {                         
            try {
                String acc = accField.getText().trim();
                double amt = Double.parseDouble(amtField.getText().trim());
                double bal = BankingService.withdraw(acc, amt);
                if (bal == -2) UITheme.showMessage(p, " Fixed Deposit accounts cannot withdraw before maturity.", false);
                else if (bal >= 0) UITheme.showMessage(p,
                        " Withdrawn Rs" + amt + "\nNew Balance: Rs" + String.format("%.2f", bal), true);
                else UITheme.showMessage(p, " Withdrawal failed. Check balance / min balance rule.", false);
            } catch (NumberFormatException ex) {
                UITheme.showMessage(p, "Enter a valid amount.", false);
            }
        });

        addRow(p, "Account Number:", accField);
        addRow(p, "Amount (Rs):", amtField);
        p.add(Box.createVerticalStrut(16));
        p.add(wdBtn);
        return p;
    }

    private JPanel buildTransferPanel() {
        JPanel p = formPanel("Money Transfer");

        JTextField fromField  = UITheme.createTextField(20);
        JTextField toField    = UITheme.createTextField(20);
        JTextField amtField   = UITheme.createTextField(20);
        String[]   txnTypes   = {"Domestic Transfer", "International Transfer"};
        JComboBox<String> typeBox = new JComboBox<>(txnTypes);
        styleCombo(typeBox);

        JLabel feeNote = UITheme.createLabel("", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        typeBox.addActionListener(e ->                         
            feeNote.setText(typeBox.getSelectedIndex() == 1 ?
                "* 2% international transfer fee will be charged." : ""));

        JButton sendBtn = UITheme.primaryButton("Send Money  ");
        sendBtn.addActionListener(e -> {
            try {
                String from = fromField.getText().trim();
                String to   = toField.getText().trim();
                double amt  = Double.parseDouble(amtField.getText().trim());
                String result;
                if (typeBox.getSelectedIndex() == 0) {
                    result = BankingService.transferDomestic(from, to, amt);
                    if ("SUCCESS".equals(result))
                        UITheme.showMessage(p, " Rs" + amt + " transferred to " + to, true);
                    else UITheme.showMessage(p, " " + result, false);
                } else {
                    result = BankingService.transferInternational(from, to, amt);
                    if (result.startsWith("SUCCESS")) {
                        double charged = Double.parseDouble(result.split(":")[1]);
                        UITheme.showMessage(p, "International transfer complete.\nTotal charged: Rs"
                                + String.format("%.2f", charged), true);
                    } else UITheme.showMessage(p, " " + result, false);
                }
            } catch (NumberFormatException ex) {
                UITheme.showMessage(p, "Enter a valid amount.", false);
            }
        });

        addRow(p, "Transfer Type:", typeBox);
        addRow(p, "From Account:", fromField);
        addRow(p, "To Account:",   toField);
        addRow(p, "Amount (Rs):",   amtField);
        p.add(feeNote);
        p.add(Box.createVerticalStrut(16));
        p.add(sendBtn);
        return p;
    }


    private JPanel buildHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = UITheme.createLabel("Transaction History", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.NORTH);

        JTextField accField = UITheme.createTextField(20);
        JButton    loadBtn  = UITheme.primaryButton("Load  ");

        String[] cols = {"ID", "From", "To", "Amount ()", "Type", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(UITheme.BG_CARD);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        loadBtn.addActionListener(e -> {
            model.setRowCount(0); 
            List<String[]> rows = BankingService.getHistory(accField.getText().trim(), 20);
            rows.forEach(model::addRow);                      
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(UITheme.createLabel("Account No:", UITheme.FONT_LABEL, UITheme.TEXT_MUTED));
        top.add(accField);
        top.add(loadBtn);

        p.add(top,   BorderLayout.NORTH);  
        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(top,   BorderLayout.SOUTH);
        p.add(north, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAccountsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = UITheme.createLabel("My Accounts", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);

        String[] cols = {"Account No", "Type", "Balance (Rs)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UITheme.BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Load data
        List<String[]> accounts = TransactionDAO.getUserAccounts(userId);
        accounts.forEach(model::addRow);                       

        JButton refresh = UITheme.primaryButton("↻ Refresh");
        refresh.addActionListener(e -> {                       
            model.setRowCount(0);
            TransactionDAO.getUserAccounts(userId).forEach(model::addRow);
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        p.add(top,    BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel formPanel(String title) {
        JPanel p = new JPanel();
        p.setBackground(UITheme.BG_DARK);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        JLabel lbl = UITheme.createLabel(title, UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(24));
        return p;
    }

    private void addRow(JPanel p, String label, JComponent field) {
        JLabel lbl = UITheme.createLabel(label, UITheme.FONT_LABEL, UITheme.TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        field.setMaximumSize(new Dimension(420, 42));
        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(field);
        p.add(Box.createVerticalStrut(14));
    }

    private void styleTable(JTable table) {
        table.setBackground(UITheme.BG_CARD);
        table.setForeground(UITheme.TEXT_PRIMARY);
        table.setFont(UITheme.FONT_SMALL);
        table.setRowHeight(30);
        table.setGridColor(UITheme.BORDER_COLOR);
        table.setSelectionBackground(UITheme.ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setBackground(UITheme.BG_DARK);
        header.setForeground(UITheme.ACCENT_GOLD);
        header.setFont(UITheme.FONT_LABEL);
    }

    @SuppressWarnings("unchecked")
    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(UITheme.BG_INPUT);
        cb.setForeground(UITheme.TEXT_PRIMARY);
        cb.setFont(UITheme.FONT_BODY);
        cb.setMaximumSize(new Dimension(420, 42));
    }

    private String getTypeDesc(String type) {
        switch (type) {
            case "SAVINGS":       return "4% p.a. interest | Min balance Rs500";
            case "CURRENT":       return "No interest | Overdraft up to Rs10,000";
            case "FIXED_DEPOSIT": return "7.5% p.a. | Locked for 1 year | No early withdrawal";
            default:              return "";
        }
    }
}
