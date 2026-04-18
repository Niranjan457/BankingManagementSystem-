package ui;

import database.TransactionDAO;
import model.BankingService;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ATMFrame extends JFrame {

    private String  authenticatedAccount = null; 
    private int     pinAttempts          = 0;
    private static final int MAX_ATTEMPTS = 3;

    private JPanel  mainPanel;
    private CardLayout cardLayout;

    private JTextField  accField;
    private JPasswordField pinField;
    private JTextField  amtField;
    private JTextField  newPinField;
    private JLabel      atmDisplay;

    public ATMFrame() {
        initATM();
    }

    private void initATM() {
        setTitle("SecureBank ATM");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(5, 10, 20),
                        getWidth(), getHeight(), new Color(0, 30, 60)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        outer.setOpaque(false);


        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel atmTitle = UITheme.createLabel("  SecureBank ATM", UITheme.FONT_SUBTITLE, UITheme.ACCENT_GOLD);
        JLabel backBtn  = UITheme.createLabel(" Bank Login", UITheme.FONT_SMALL, UITheme.ACCENT_BLUE);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new LoginFrame().setVisible(true); dispose();
            }
        });
        header.add(atmTitle, BorderLayout.WEST);
        header.add(backBtn,  BorderLayout.EAST);


        atmDisplay = new JLabel("Insert Card / Enter Account No", SwingConstants.CENTER);
        atmDisplay.setFont(UITheme.FONT_MONO);
        atmDisplay.setForeground(UITheme.ACCENT_GREEN);
        atmDisplay.setBackground(new Color(0, 20, 10));
        atmDisplay.setOpaque(true);
        atmDisplay.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.ACCENT_GREEN, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setOpaque(false);
        mainPanel.add(buildPINEntryScreen(),   "PIN_ENTRY");
        mainPanel.add(buildMenuScreen(),       "MENU");
        mainPanel.add(buildWithdrawScreen(),   "WITHDRAW");
        mainPanel.add(buildMiniStmtScreen(),   "MINI_STMT");
        mainPanel.add(buildGeneratePINScreen(),"GEN_PIN");

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        center.add(atmDisplay, BorderLayout.NORTH);
        center.add(mainPanel,  BorderLayout.CENTER);

        outer.add(header, BorderLayout.NORTH);
        outer.add(center, BorderLayout.CENTER);
        setContentPane(outer);
    }

    private JPanel buildPINEntryScreen() {
        JPanel p = atmPanel();

        accField = UITheme.createTextField(16);
        pinField = UITheme.createPasswordField(16);
        pinField.setDocument(new javax.swing.text.PlainDocument() {
            @Override public void insertString(int o, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= 4) super.insertString(o, str, a); 
            }
        });

        JButton loginBtn    = UITheme.successButton("Authenticate ");
        JButton genPinBtn   = UITheme.createButton("Generate PIN", UITheme.ACCENT_BLUE, Color.WHITE);

        loginBtn.addActionListener(e -> handlePINLogin());    
        pinField.addActionListener(e -> handlePINLogin());
        genPinBtn.addActionListener(e -> showScreen("GEN_PIN")); 

        addATMRow(p, "Account Number:", accField);
        addATMRow(p, "4-Digit PIN:",    pinField);
        p.add(Box.createVerticalStrut(14));
        p.add(loginBtn);
        p.add(Box.createVerticalStrut(8));
        p.add(genPinBtn);
        return p;
    }

    private void handlePINLogin() {
        if (pinAttempts >= MAX_ATTEMPTS) {
            setDisplay(" Card blocked. Too many attempts.");
            return;
        }
        String acc = accField.getText().trim();
        String pin = new String(pinField.getPassword()).trim();

        if (acc.isEmpty() || pin.length() != 4) {
            setDisplay("Enter account number and 4-digit PIN.");
            return;
        }
        if (BankingService.validatePIN(acc, pin)) {
            authenticatedAccount = acc;
            pinAttempts = 0;
            setDisplay(" Authenticated: " + acc);
            showScreen("MENU");
        } else {
            pinAttempts++;
            int rem = MAX_ATTEMPTS - pinAttempts;
            setDisplay(" Wrong PIN. " + (rem > 0 ? rem + " attempts left." : "Card blocked!"));
            pinField.setText("");
        }
    }

    private JPanel buildMenuScreen() {
        JPanel p = atmPanel();
        JLabel title = UITheme.createLabel("Select Operation", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(20));

        String[][] menu = {
            {"  Check Balance",    "BAL"},
            {"  Withdraw Cash",    "WITHDRAW"},
            {"  Mini Statement",   "MINI_STMT"},
            {"  Change PIN",       "GEN_PIN"},
            {"  Logout",           "LOGOUT"},
        };

        for (String[] item : menu) {
            JButton btn = UITheme.createButton(item[0], UITheme.BG_INPUT, UITheme.TEXT_PRIMARY);
            btn.setMaximumSize(new Dimension(320, 44));
            btn.setAlignmentX(CENTER_ALIGNMENT);
            final String action = item[1];
            btn.addActionListener(e -> handleMenuAction(action)); 
            p.add(btn);
            p.add(Box.createVerticalStrut(8));
        }
        return p;
    }

    private void handleMenuAction(String action) {
        switch (action) {
            case "BAL":
                double bal = TransactionDAO.getBalance(authenticatedAccount);
                setDisplay("Balance: Rs " + String.format("%.2f", bal));
                JOptionPane.showMessageDialog(this,
                    "Account: " + authenticatedAccount + "\nBalance: Rs " + String.format("%.2f", bal),
                    "Balance Inquiry", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "WITHDRAW": showScreen("WITHDRAW"); break;
            case "MINI_STMT": loadMiniStatement(); showScreen("MINI_STMT"); break;
            case "GEN_PIN":  showScreen("GEN_PIN");  break;
            case "LOGOUT":
                authenticatedAccount = null;
                pinField.setText("");
                accField.setText("");
                setDisplay("Thank you. Goodbye!");
                showScreen("PIN_ENTRY");
                break;
        }
    }

    private JPanel buildWithdrawScreen() {
        JPanel p = atmPanel();
        amtField = UITheme.createTextField(16);

        JPanel quickBtns = new JPanel(new GridLayout(2, 3, 8, 8));
        quickBtns.setOpaque(false);
        int[] amounts = {500, 1000, 2000, 5000, 10000, 20000};
        for (int amt : amounts) {
            JButton qb = UITheme.createButton("Rs" + amt, UITheme.BG_DARK, UITheme.ACCENT_GOLD);
            final int a = amt;
            qb.addActionListener(e -> amtField.setText(String.valueOf(a))); 
            quickBtns.add(qb);
        }

        JButton withdrawBtn = UITheme.dangerButton("Withdraw ");
        JButton backBtn     = UITheme.createButton(" Back", UITheme.BG_INPUT, UITheme.TEXT_MUTED);

        withdrawBtn.addActionListener(e -> {
            try {
                double amt = Double.parseDouble(amtField.getText().trim());
                double newBal = BankingService.withdraw(authenticatedAccount, amt);
                if (newBal >= 0) {
                    setDisplay(" Dispensing Rs" + amt);
                    JOptionPane.showMessageDialog(this,
                        "Rs" + amt + " dispensed.\nRemaining Balance: Rs" + String.format("%.2f", newBal),
                        "Withdrawal Successful", JOptionPane.INFORMATION_MESSAGE);
                    amtField.setText("");
                } else if (newBal == -2) {
                    UITheme.showMessage(this, "FD accounts cannot withdraw.", false);
                } else {
                    setDisplay(" Insufficient balance / min balance rule.");
                    UITheme.showMessage(this, "Withdrawal failed. Check balance.", false);
                }
            } catch (NumberFormatException ex) {
                UITheme.showMessage(this, "Enter a valid amount.", false);
            }
        });

        backBtn.addActionListener(e -> showScreen("MENU"));

        addATMRow(p, "Enter Amount (Rs):", amtField);
        p.add(UITheme.createLabel("Quick Select:", UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        p.add(Box.createVerticalStrut(6));
        quickBtns.setMaximumSize(new Dimension(340, 90));
        p.add(quickBtns);
        p.add(Box.createVerticalStrut(14));
        p.add(withdrawBtn);
        p.add(Box.createVerticalStrut(6));
        p.add(backBtn);
        return p;
    }

    private JTextArea miniStmtArea;

    private JPanel buildMiniStmtScreen() {
        JPanel p = atmPanel();
        JLabel title = UITheme.createLabel("Mini Statement (Last 5)", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        miniStmtArea = new JTextArea(10, 28);
        miniStmtArea.setFont(UITheme.FONT_MONO);
        miniStmtArea.setBackground(new Color(0, 20, 10));
        miniStmtArea.setForeground(UITheme.ACCENT_GREEN);
        miniStmtArea.setEditable(false);
        miniStmtArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(miniStmtArea);
        scroll.setMaximumSize(new Dimension(360, 200));

        JButton backBtn = UITheme.createButton(" Back", UITheme.BG_INPUT, UITheme.TEXT_MUTED);
        backBtn.addActionListener(e -> showScreen("MENU"));

        p.add(title);
        p.add(Box.createVerticalStrut(12));
        p.add(scroll);
        p.add(Box.createVerticalStrut(12));
        p.add(backBtn);
        return p;
    }

    private void loadMiniStatement() {
        List<String[]> txns = BankingService.getHistory(authenticatedAccount, 5);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-10s %-10s%n", "ID", "Type", "Amount"));
        sb.append(.repeat(30)).append("\n");
        if (txns.isEmpty()) {
            sb.append("No transactions found.");
        } else {
            txns.forEach(t -> sb.append(
                String.format("%-6s %-10s ₹%-9s%n", t[0],
                    t[4].length() > 9 ? t[4].substring(0, 9) : t[4], t[3])));
        }
        if (miniStmtArea != null) miniStmtArea.setText(sb.toString());
        setDisplay("Mini Statement loaded.");
    }

    private JPanel buildGeneratePINScreen() {
        JPanel p = atmPanel();
        JTextField genAccField  = UITheme.createTextField(16);
        newPinField             = UITheme.createTextField(16);
        JTextField confirmField = UITheme.createTextField(16);

        JButton saveBtn = UITheme.primaryButton("Save PIN");
        JButton backBtn = UITheme.createButton(" Back", UITheme.BG_INPUT, UITheme.TEXT_MUTED);

        saveBtn.addActionListener(e -> {                     
            String acc     = authenticatedAccount != null ? authenticatedAccount : genAccField.getText().trim();
            String newPin  = newPinField.getText().trim();
            String confirm = confirmField.getText().trim();

            if (acc.isEmpty()) { UITheme.showMessage(this, "Enter account number.", false); return; }
            if (newPin.length() != 4 || !newPin.matches("\\d{4}")) {
                UITheme.showMessage(this, "PIN must be exactly 4 digits.", false); return;
            }
            if (!newPin.equals(confirm)) {
                UITheme.showMessage(this, "PINs do not match.", false); return;
            }
            if (!TransactionDAO.accountExists(acc)) {
                UITheme.showMessage(this, "Account not found.", false); return;
            }
            if (BankingService.generatePIN(acc, newPin)) {
                setDisplay(" PIN set for " + acc);
                UITheme.showMessage(this, "PIN saved successfully!", true);
                newPinField.setText(""); confirmField.setText("");
                showScreen(authenticatedAccount != null ? "MENU" : "PIN_ENTRY");
            } else UITheme.showMessage(this, "Failed to save PIN.", false);
        });

        backBtn.addActionListener(e ->                        
            showScreen(authenticatedAccount != null ? "MENU" : "PIN_ENTRY"));

        if (authenticatedAccount == null) {
            addATMRow(p, "Account No:", genAccField);
        }
        addATMRow(p, "New PIN (4 digits):",    newPinField);
        addATMRow(p, "Confirm PIN:",           confirmField);
        p.add(Box.createVerticalStrut(14));
        p.add(saveBtn);
        p.add(Box.createVerticalStrut(6));
        p.add(backBtn);
        return p;
    }

    private JPanel atmPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        return p;
    }

    private void addATMRow(JPanel p, String label, JComponent field) {
        JLabel lbl = UITheme.createLabel(label, UITheme.FONT_LABEL, UITheme.TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        field.setMaximumSize(new Dimension(340, 40));
        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(field);
        p.add(Box.createVerticalStrut(12));
    }

    private void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    private void setDisplay(String msg) {
        atmDisplay.setText(msg);
    }
}
