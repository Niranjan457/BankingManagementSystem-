package ui;

import database.TransactionDAO;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField     userIdField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("SecureBank – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, UITheme.BG_DARK,
                        getWidth(), getHeight(), new Color(10, 25, 60)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel logo = UITheme.createLabel("🏦 SecureBank", UITheme.FONT_TITLE, UITheme.ACCENT_GOLD);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sub  = UITheme.createLabel("Banking Management System",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(6));
        header.add(sub);
        header.add(Box.createVerticalStrut(30));

        JPanel card = UITheme.createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;

        JLabel loginTitle = UITheme.createLabel("Welcome Back", UITheme.FONT_SUBTITLE, UITheme.TEXT_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 16, 0);
        card.add(loginTitle, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(6, 0, 2, 0);
        card.add(UITheme.createLabel("User ID", UITheme.FONT_LABEL, UITheme.TEXT_MUTED), gbc);
        userIdField = UITheme.createTextField(20);
        userIdField.setPreferredSize(new Dimension(320, 40));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 10, 0);
        card.add(userIdField, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(6, 0, 2, 0);
        card.add(UITheme.createLabel("Password", UITheme.FONT_LABEL, UITheme.TEXT_MUTED), gbc);
        passwordField = UITheme.createPasswordField(20);
        passwordField.setPreferredSize(new Dimension(320, 40));
        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 16, 0);
        card.add(passwordField, gbc);

        statusLabel = UITheme.createLabel("", UITheme.FONT_SMALL, UITheme.ACCENT_RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 8, 0);
        card.add(statusLabel, gbc);

        JButton loginBtn = UITheme.primaryButton("LOGIN  ");
        loginBtn.setPreferredSize(new Dimension(320, 45));
        loginBtn.addActionListener(e -> handleLogin());          
        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(loginBtn, gbc);

        passwordField.addActionListener(e -> handleLogin());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        JLabel atmLabel = UITheme.createLabel("ATM Access  ", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JButton atmBtn  = UITheme.createButton(" Go", UITheme.ACCENT_BLUE, Color.WHITE);
        atmBtn.setFont(UITheme.FONT_SMALL);

        atmBtn.addActionListener(e -> {
            new ATMFrame().setVisible(true);
            dispose();
        });
        footer.add(atmLabel);
        footer.add(atmBtn);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(header);
        center.add(card);
        center.add(Box.createVerticalStrut(20));
        center.add(footer);

        mainPanel.add(center, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void handleLogin() {
        String userId   = userIdField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (userId.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both User ID and Password.");
            return;
        }

        String userName = TransactionDAO.validateUser(userId, password);
        if (userName != null) {
            new DashboardFrame(userId, userName).setVisible(true);
            dispose(); 
        } else {
            statusLabel.setText(" Invalid User ID or Password.");
            passwordField.setText("");
        }
    }
}
