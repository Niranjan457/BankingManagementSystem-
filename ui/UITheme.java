package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UITheme {

    public static final Color BG_DARK       = new Color(13, 17, 30);      
    public static final Color BG_CARD       = new Color(22, 28, 48);      
    public static final Color BG_INPUT      = new Color(30, 38, 62);     
    public static final Color ACCENT_GOLD   = new Color(255, 196, 0);     
    public static final Color ACCENT_GREEN  = new Color(0, 200, 130);     
    public static final Color ACCENT_RED    = new Color(255, 80, 80);     
    public static final Color ACCENT_BLUE   = new Color(64, 150, 255);    
    public static final Color TEXT_PRIMARY  = new Color(230, 235, 255);   
    public static final Color TEXT_MUTED    = new Color(140, 150, 180);   
    public static final Color BORDER_COLOR  = new Color(50, 60, 100);     

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO     = new Font("Consolas",  Font.PLAIN, 13);


    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                            getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_LABEL);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton primaryButton(String text) {
        return createButton(text, ACCENT_GOLD, BG_DARK);
    }

    public static JButton successButton(String text) {
        return createButton(text, ACCENT_GREEN, BG_DARK);
    }

    public static JButton dangerButton(String text) {
        return createButton(text, ACCENT_RED, Color.WHITE);
    }

    public static JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_INPUT);
        tf.setCaretColor(ACCENT_GOLD);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_BODY);
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(BG_INPUT);
        pf.setCaretColor(ACCENT_GOLD);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return pf;
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return card;
    }

    public static void showMessage(Component parent, String message, boolean success) {
        JOptionPane.showMessageDialog(parent, message,
            success ? "  Success" : " Error",
            success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    public static void darkBg(JPanel p) {
        p.setBackground(BG_DARK);
    }
}
