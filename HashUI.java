import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;

/**
 * HashUI — Interactive Swing front-end for the Hashing Algorithm OOP Assignment.
 *
 * Wraps the existing AuthenticatedUser / User / Authentication backend without
 * modifying any of the original source files.
 *
 * Features:
 *  - Dark-themed, glassmorphism UI with custom-painted components
 *  - Sign In / Sign Up tabbed interface
 *  - Password show/hide toggle
 *  - Live djb2 hash visualizer (updates on every keystroke)
 *  - Animated status panel with colour-coded feedback
 *  - Animated glowing header with radial orbs
 */
public class HashUI extends JFrame {

    // ─────────────────────────────────────────────
    //  Colour palette
    // ─────────────────────────────────────────────
    private static final Color BG_DARK        = new Color(13,  17,  23);
    private static final Color BG_CARD        = new Color(22,  27,  34);
    private static final Color BG_INPUT       = new Color(30,  38,  48);
    private static final Color ACCENT_BLUE    = new Color(56,  139, 253);
    private static final Color ACCENT_PURPLE  = new Color(139, 92,  246);
    private static final Color ACCENT_GREEN   = new Color(63,  185, 80);
    private static final Color ACCENT_RED     = new Color(248, 81,  73);
    private static final Color ACCENT_ORANGE  = new Color(255, 166, 87);
    private static final Color TEXT_PRIMARY   = new Color(230, 237, 243);
    private static final Color TEXT_MUTED     = new Color(110, 118, 129);
    private static final Color BORDER_COLOR   = new Color(48,  54,  61);

    // ─────────────────────────────────────────────
    //  Fonts  (fallback to SansSerif)
    // ─────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  26);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 12);
    private static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 10);

    // ─────────────────────────────────────────────
    //  Shared UI state
    // ─────────────────────────────────────────────
    private JTabbedPane tabbedPane;

    // Sign-In fields
    private JTextField     siUsernameField;
    private JPasswordField siPasswordField;
    private JLabel         siStatusLabel;
    private JPanel         siStatusPanel;
    private JLabel         siHashLabel;
    private JPanel         siHashBar;

    // Sign-Up fields
    private JTextField     suUsernameField;
    private JPasswordField suPasswordField;
    private JLabel         suStatusLabel;
    private JPanel         suStatusPanel;
    private JLabel         suHashLabel;
    private JPanel         suHashBar;

    // Orb animation timer
    private float orbPhase = 0f;
    private JPanel headerPanel;

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public HashUI() {
        setTitle("HashVault — Secure Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(560, 740);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeaderPanel(), BorderLayout.NORTH);
        add(buildMainPanel(),   BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);

        // Subtle header orb animation (pulse every 50ms)
        Timer animTimer = new Timer(50, e -> {
            orbPhase = (orbPhase + 0.04f) % (2 * (float) Math.PI);
            if (headerPanel != null) headerPanel.repaint();
        });
        animTimer.start();
    }

    // ─────────────────────────────────────────────
    //  Header panel (gradient + animated orbs)
    // ─────────────────────────────────────────────
    private JPanel buildHeaderPanel() {
        headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Deep gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(18, 22, 56),
                    getWidth(), getHeight(), new Color(10, 14, 36)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Animated blue orb (top-right, pulsing)
                float alpha1 = 0.22f + 0.08f * (float) Math.sin(orbPhase);
                int orbR1 = 100 + (int)(10 * Math.sin(orbPhase));
                RadialGradientPaint orb1 = new RadialGradientPaint(
                    new Point2D.Float(getWidth() - 70, 30), orbR1,
                    new float[]{0f, 1f},
                    new Color[]{new Color(56, 139, 253, (int)(alpha1 * 255)), new Color(56, 139, 253, 0)}
                );
                g2.setPaint(orb1);
                g2.fillOval(getWidth() - 70 - orbR1, 30 - orbR1, orbR1 * 2, orbR1 * 2);

                // Animated purple orb (top-left, counter-phase)
                float alpha2 = 0.18f + 0.06f * (float) Math.sin(orbPhase + Math.PI);
                int orbR2 = 80 + (int)(8 * Math.sin(orbPhase + Math.PI));
                RadialGradientPaint orb2 = new RadialGradientPaint(
                    new Point2D.Float(60, 70), orbR2,
                    new float[]{0f, 1f},
                    new Color[]{new Color(139, 92, 246, (int)(alpha2 * 255)), new Color(139, 92, 246, 0)}
                );
                g2.setPaint(orb2);
                g2.fillOval(60 - orbR2, 70 - orbR2, orbR2 * 2, orbR2 * 2);

                // Bottom separator line
                g2.setColor(BORDER_COLOR);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                g2.dispose();
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(560, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 28, 16, 28));

        // Lock icon (drawn with Graphics2D)
        JLabel lockIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shackle arc
                g2.setColor(ACCENT_BLUE);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(7, 3, 22, 20, 0, 180);
                // Lock body
                g2.setColor(ACCENT_BLUE);
                g2.fillRoundRect(3, 18, 30, 22, 7, 7);
                // Keyhole circle
                g2.setColor(BG_DARK);
                g2.fillOval(12, 23, 12, 12);
                // Keyhole slot
                g2.fillRect(15, 31, 6, 7);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(42, 46); }
        };

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);

        JLabel titleLabel = new JLabel("HashVault");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel badge = makeBadge("djb2", ACCENT_PURPLE);
        JLabel badge2 = makeBadge("Salted", ACCENT_GREEN);

        titleRow.add(lockIcon);
        titleRow.add(titleLabel);
        titleRow.add(badge);
        titleRow.add(badge2);

        JLabel subtitle = new JLabel("  Secure password hashing with salt · OOP demonstration");
        subtitle.setFont(FONT_CAPTION);
        subtitle.setForeground(TEXT_MUTED);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);
        textCol.add(titleRow);
        textCol.add(Box.createVerticalStrut(6));
        textCol.add(subtitle);

        headerPanel.add(textCol, BorderLayout.CENTER);
        return headerPanel;
    }

    // ─────────────────────────────────────────────
    //  Main panel (tabs)
    // ─────────────────────────────────────────────
    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_DARK);
        main.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setFont(FONT_LABEL);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setUI(new DarkTabbedPaneUI());

        tabbedPane.addTab("  \uD83D\uDD13  Sign In  ", buildSignInPanel());
        tabbedPane.addTab("  \uD83D\uDCDD  Sign Up  ", buildSignUpPanel());

        // Clear status on tab switch
        tabbedPane.addChangeListener(e -> {
            clearStatus(siStatusPanel, siStatusLabel);
            clearStatus(suStatusPanel, suStatusLabel);
        });

        main.add(tabbedPane, BorderLayout.CENTER);
        return main;
    }

    // ─────────────────────────────────────────────
    //  Sign-In panel
    // ─────────────────────────────────────────────
    private JPanel buildSignInPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(26, 28, 24, 28));

        // Username
        panel.add(makeLabel("Username"));
        panel.add(Box.createVerticalStrut(6));
        siUsernameField = makeTextField("Enter your username");
        panel.add(siUsernameField);
        panel.add(Box.createVerticalStrut(16));

        // Password + show/hide
        panel.add(makeLabel("Password"));
        panel.add(Box.createVerticalStrut(6));
        siPasswordField = makePasswordField("Enter your password");
        JPanel siPwdRow = makePasswordRow(siPasswordField);
        panel.add(siPwdRow);
        panel.add(Box.createVerticalStrut(20));

        // Hash Visualizer
        JPanel[] vizRefs = buildHashVisualizerSection();
        siHashLabel = (JLabel) vizRefs[0].getClientProperty("hashLabel");
        siHashBar   = vizRefs[1];
        panel.add(vizRefs[0]);
        panel.add(Box.createVerticalStrut(20));

        // Status panel
        siStatusPanel = buildStatusPanel();
        siStatusLabel = (JLabel) siStatusPanel.getComponent(0);
        panel.add(siStatusPanel);
        panel.add(Box.createVerticalStrut(16));

        // Sign In button
        JButton signInBtn = makeButton("Sign In", ACCENT_BLUE);
        signInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInBtn.addActionListener(e -> performSignIn());
        panel.add(signInBtn);

        // Wire up live hash visualizer
        siPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateHashViz(true); }
            public void removeUpdate(DocumentEvent e)  { updateHashViz(true); }
            public void changedUpdate(DocumentEvent e) { updateHashViz(true); }
        });

        return panel;
    }

    // ─────────────────────────────────────────────
    //  Sign-Up panel
    // ─────────────────────────────────────────────
    private JPanel buildSignUpPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(26, 28, 24, 28));

        // Username
        panel.add(makeLabel("Username"));
        panel.add(Box.createVerticalStrut(6));
        suUsernameField = makeTextField("Choose a username");
        panel.add(suUsernameField);
        panel.add(Box.createVerticalStrut(16));

        // Password + show/hide
        panel.add(makeLabel("Password"));
        panel.add(Box.createVerticalStrut(6));
        suPasswordField = makePasswordField("Choose a password");
        JPanel suPwdRow = makePasswordRow(suPasswordField);
        panel.add(suPwdRow);
        panel.add(Box.createVerticalStrut(20));

        // Hash Visualizer
        JPanel[] vizRefs = buildHashVisualizerSection();
        suHashLabel = (JLabel) vizRefs[0].getClientProperty("hashLabel");
        suHashBar   = vizRefs[1];
        panel.add(vizRefs[0]);
        panel.add(Box.createVerticalStrut(20));

        // Status panel
        suStatusPanel = buildStatusPanel();
        suStatusLabel = (JLabel) suStatusPanel.getComponent(0);
        panel.add(suStatusPanel);
        panel.add(Box.createVerticalStrut(16));

        // Sign Up button
        JButton signUpBtn = makeButton("Create Account", ACCENT_GREEN);
        signUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpBtn.addActionListener(e -> performSignUp());
        panel.add(signUpBtn);

        // Wire up live hash visualizer
        suPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateHashViz(false); }
            public void removeUpdate(DocumentEvent e)  { updateHashViz(false); }
            public void changedUpdate(DocumentEvent e) { updateHashViz(false); }
        });

        return panel;
    }

    // ─────────────────────────────────────────────
    //  Password row  (field + show/hide eye button)
    // ─────────────────────────────────────────────
    private JPanel makePasswordRow(JPasswordField pf) {
        JPanel row = new JPanel(new BorderLayout(0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Eye toggle button
        JButton eyeBtn = new JButton() {
            private boolean showPwd = false;
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setToolTipText("Show / hide password");
                addActionListener(e -> {
                    showPwd = !showPwd;
                    pf.setEchoChar(showPwd ? (char) 0 : '\u25CF');
                    repaint();
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background pill
                g2.setColor(BG_INPUT);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Eye icon
                int cx = getWidth() / 2, cy = getHeight() / 2;
                g2.setColor(showPwd ? ACCENT_BLUE : TEXT_MUTED);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Outer eye arc
                int[] xPts = new int[]{cx - 10, cx, cx + 10, cx};
                int[] yPts = new int[]{cy, cy - 6, cy, cy + 6};
                g2.drawPolygon(xPts, yPts, 4);
                // Pupil
                g2.fillOval(cx - 3, cy - 3, 6, 6);
                if (!showPwd) {
                    // Slash through eye
                    g2.setColor(TEXT_MUTED);
                    g2.drawLine(cx - 9, cy + 5, cx + 9, cy - 5);
                }
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        eyeBtn.setPreferredSize(new Dimension(44, 44));

        row.add(pf,     BorderLayout.CENTER);
        row.add(eyeBtn, BorderLayout.EAST);
        return row;
    }

    // ─────────────────────────────────────────────
    //  Hash Visualizer section — returns [container, barPanel]
    //  container has client property "hashLabel" → JLabel
    // ─────────────────────────────────────────────
    private JPanel[] buildHashVisualizerSection() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_DARK);
        container.setBorder(new CompoundBorder(
            new LineBorder(new Color(ACCENT_PURPLE.getRed(), ACCENT_PURPLE.getGreen(), ACCENT_PURPLE.getBlue(), 60), 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Section header row
        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelRow.setOpaque(false);

        JLabel sectionLabel = new JLabel("\u26A1 Live djb2 Hash Visualizer");
        sectionLabel.setFont(FONT_LABEL);
        sectionLabel.setForeground(ACCENT_ORANGE);

        JLabel hint = new JLabel("  \u2014 updates as you type");
        hint.setFont(FONT_CAPTION);
        hint.setForeground(TEXT_MUTED);

        labelRow.add(sectionLabel);
        labelRow.add(hint);
        labelRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(labelRow);
        container.add(Box.createVerticalStrut(8));

        // Hash value display
        JLabel hashValueLabel = new JLabel("hash(password)  =  \u2014");
        hashValueLabel.setFont(FONT_MONO);
        hashValueLabel.setForeground(ACCENT_BLUE);
        hashValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(hashValueLabel);
        container.add(Box.createVerticalStrut(6));

        // Bit-count caption
        JLabel bitsLabel = new JLabel("Bits set: \u2014   |   Length: 0 chars");
        bitsLabel.setFont(FONT_SMALL);
        bitsLabel.setForeground(TEXT_MUTED);
        bitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(bitsLabel);
        container.add(Box.createVerticalStrut(8));

        // Entropy bar
        JPanel entropyBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Track
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                // Fill
                Object prop = getClientProperty("fillFraction");
                if (prop != null) {
                    double frac = (double) prop;
                    int w = (int)(frac * getWidth());
                    if (w > 0) {
                        GradientPaint gp = new GradientPaint(0, 0, ACCENT_PURPLE, w, 0, ACCENT_BLUE);
                        g2.setPaint(gp);
                        g2.fillRoundRect(0, 0, w, getHeight(), 6, 6);
                    }
                }
                g2.dispose();
            }
        };
        entropyBar.setOpaque(false);
        entropyBar.setPreferredSize(new Dimension(0, 8));
        entropyBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        entropyBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(entropyBar);

        // Store references via client properties
        container.putClientProperty("hashLabel", hashValueLabel);
        container.putClientProperty("bitsLabel", bitsLabel);

        return new JPanel[]{container, entropyBar};
    }

    // ─────────────────────────────────────────────
    //  Status panel
    // ─────────────────────────────────────────────
    private JPanel buildStatusPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Object colorProp = getClientProperty("bgColor");
                if (colorProp != null) {
                    g2.setColor((Color) colorProp);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                    g2.setColor(((Color) colorProp).brighter());
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
            }
        };
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setVisible(false);

        JLabel label = new JLabel("\u2014");
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        panel.add(label);

        return panel;
    }

    // ─────────────────────────────────────────────
    //  Footer
    // ─────────────────────────────────────────────
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_DARK);
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        leftInfo.setOpaque(false);
        leftInfo.add(makeChip("djb2",         ACCENT_BLUE));
        leftInfo.add(makeChip("Salted",        ACCENT_GREEN));
        leftInfo.add(makeChip("SecureRandom",  ACCENT_PURPLE));
        leftInfo.add(makeChip("File Store",    ACCENT_ORANGE));

        JLabel credit = new JLabel("OOP Assignment  \u00B7  Java Swing UI");
        credit.setFont(FONT_CAPTION);
        credit.setForeground(TEXT_MUTED);

        footer.add(leftInfo, BorderLayout.WEST);
        footer.add(credit,   BorderLayout.EAST);
        return footer;
    }

    // ─────────────────────────────────────────────
    //  djb2 hash — mirrors User.java (for live viz only)
    // ─────────────────────────────────────────────
    private long djb2Hash(String str) {
        long h = 5381;
        for (int i = 0; i < str.length(); i++) {
            h = ((h << 5) + h) + str.charAt(i);
        }
        return h;
    }

    // ─────────────────────────────────────────────
    //  Update live hash visualizer
    // ─────────────────────────────────────────────
    private void updateHashViz(boolean isSignIn) {
        JPasswordField pwdField   = isSignIn ? siPasswordField : suPasswordField;
        JLabel         hashLabel  = isSignIn ? siHashLabel     : suHashLabel;
        JPanel         bar        = isSignIn ? siHashBar       : suHashBar;

        // Retrieve the container to get the bitsLabel
        JPanel vizContainer = (JPanel) bar.getParent();
        JLabel bitsLabel = vizContainer != null
            ? (JLabel) vizContainer.getClientProperty("bitsLabel")
            : null;

        String pwd = new String(pwdField.getPassword());
        if (pwd.isEmpty()) {
            hashLabel.setText("hash(password)  =  \u2014");
            if (bitsLabel != null) bitsLabel.setText("Bits set: \u2014   |   Length: 0 chars");
            bar.putClientProperty("fillFraction", 0.0);
            bar.repaint();
            return;
        }

        long hashVal = djb2Hash(pwd);
        String hexStr = Long.toHexString(hashVal).toUpperCase();
        String displayPwd = pwd.length() > 10 ? pwd.substring(0, 10) + "\u2026" : pwd;
        hashLabel.setText("hash(\"" + displayPwd + "\")  =  " + hashVal + "  (0x" + hexStr + ")");

        int bits = Long.bitCount(Math.abs(hashVal));
        if (bitsLabel != null) {
            bitsLabel.setText("Bits set: " + bits + " / 64   |   Length: " + pwd.length() + " chars");
        }

        double fraction = Math.min(1.0, bits / 50.0);
        bar.putClientProperty("fillFraction", fraction);
        bar.repaint();
    }

    // ─────────────────────────────────────────────
    //  Sign In — delegates to AuthenticatedUser backend
    // ─────────────────────────────────────────────
    private void performSignIn() {
        String username = siUsernameField.getText().trim();
        String password = new String(siPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus(siStatusPanel, siStatusLabel,
                "\u26A0  Please fill in both fields.", ACCENT_ORANGE, new Color(40, 30, 10));
            return;
        }

        String result = captureOutput(() -> {
            try {
                AuthenticatedUser u = new AuthenticatedUser(username, password);
                u.signIn();
            } catch (IOException ex) {
                System.out.println("An error occurred: " + ex.getMessage());
            }
        });

        if (result.contains("successfully")) {
            showStatus(siStatusPanel, siStatusLabel,
                "\u2714  " + result.trim(), ACCENT_GREEN, new Color(10, 30, 15));
        } else {
            showStatus(siStatusPanel, siStatusLabel,
                "\u2716  " + result.trim(), ACCENT_RED, new Color(35, 12, 12));
        }
    }

    // ─────────────────────────────────────────────
    //  Sign Up — delegates to AuthenticatedUser backend
    // ─────────────────────────────────────────────
    private void performSignUp() {
        String username = suUsernameField.getText().trim();
        String password = new String(suPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus(suStatusPanel, suStatusLabel,
                "\u26A0  Please fill in both fields.", ACCENT_ORANGE, new Color(40, 30, 10));
            return;
        }

        String result = captureOutput(() -> {
            try {
                AuthenticatedUser u = new AuthenticatedUser(username, password);
                u.signUp();
            } catch (IOException ex) {
                System.out.println("An error occurred: " + ex.getMessage());
            }
        });

        if (result.contains("successfully") || result.contains("registered")) {
            showStatus(suStatusPanel, suStatusLabel,
                "\u2714  " + result.trim(), ACCENT_GREEN, new Color(10, 30, 15));
        } else {
            showStatus(suStatusPanel, suStatusLabel,
                "\u2716  " + result.trim(), ACCENT_RED, new Color(35, 12, 12));
        }
    }

    // ─────────────────────────────────────────────
    //  Redirect System.out to capture backend messages
    // ─────────────────────────────────────────────
    private String captureOutput(Runnable action) {
        PrintStream oldOut = System.out;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            action.run();
        } finally {
            System.setOut(oldOut);
        }
        return baos.toString().trim();
    }

    // ─────────────────────────────────────────────
    //  Show / clear status panel
    // ─────────────────────────────────────────────
    private void showStatus(JPanel panel, JLabel label, String msg, Color fg, Color bg) {
        label.setText(msg);
        label.setForeground(fg);
        panel.putClientProperty("bgColor", bg);
        panel.setVisible(true);
        panel.repaint();
    }

    private void clearStatus(JPanel panel, JLabel label) {
        if (panel != null) panel.setVisible(false);
    }

    // ─────────────────────────────────────────────
    //  UI Helper factories
    // ─────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_INPUT);
                    Insets ins = getInsets();
                    g2.drawString(placeholder,
                        ins.left + 2,
                        getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        styleInputField(f);
        return f;
    }

    private JPasswordField makePasswordField(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_INPUT);
                    Insets ins = getInsets();
                    g2.drawString(placeholder,
                        ins.left + 2,
                        getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        f.setEchoChar('\u25CF');
        styleInputField(f);
        return f;
    }

    private void styleInputField(JTextField f) {
        f.setFont(FONT_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_INPUT);
        f.setCaretColor(ACCENT_BLUE);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setOpaque(true);

        // Focus highlight effect
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(ACCENT_BLUE, 1, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                f.repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                f.repaint();
            }
        });
    }

    private JButton makeButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            private boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                    public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = pressed ? accent.darker()
                           : hovered ? accent.brighter()
                           : accent;
                GradientPaint gp = new GradientPaint(0, 0, base.brighter(), 0, getHeight(), base);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Subtle gloss
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);
                // Text
                g2.setFont(getFont());
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel makeBadge(String text, Color color) {
        JLabel badge = new JLabel("  " + text + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(FONT_CAPTION);
        badge.setForeground(color);
        badge.setOpaque(false);
        return badge;
    }

    private JLabel makeChip(String text, Color color) {
        JLabel chip = new JLabel("  " + text + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(FONT_CAPTION);
        chip.setForeground(color);
        chip.setOpaque(false);
        return chip;
    }

    // ─────────────────────────────────────────────
    //  Custom dark TabbedPane UI
    // ─────────────────────────────────────────────
    private static class DarkTabbedPaneUI extends BasicTabbedPaneUI {
        @Override
        protected void installDefaults() {
            super.installDefaults();
            // tabAreaBackground removed in JDK 17+; we override paintTabArea instead
            contentBorderInsets = new Insets(0, 0, 0, 0);
        }

        @Override
        protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {
            // Fill the tab area with our dark background before painting tabs
            g.setColor(BG_DARK);
            g.fillRect(0, 0, tabPane.getWidth(), getTabRunHeight(tabPlacement, 0));
            super.paintTabArea(g, tabPlacement, selectedIndex);
        }

        private int getTabRunHeight(int tabPlacement, int run) {
            if (rects == null || rects.length == 0) return 40;
            return rects[0].height + 4;
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (isSelected) {
                g2.setColor(BG_CARD);
            } else {
                g2.setColor(new Color(25, 31, 38));
            }
            g2.fillRoundRect(x, y, w, h, 8, 8);
            if (isSelected) {
                // Blue accent underline on selected tab
                GradientPaint gp = new GradientPaint(x + 4, 0, ACCENT_PURPLE, x + w - 8, 0, ACCENT_BLUE);
                g2.setPaint(gp);
                g2.fillRect(x + 4, y + h - 3, w - 8, 3);
            }
            g2.dispose();
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
            // suppress default border
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // suppress default content border
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                           int tabIndex, Rectangle iconRect, Rectangle textRect,
                                           boolean isSelected) {
            // suppress focus ring
        }
    }

    // ─────────────────────────────────────────────
    //  Entry point
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Global dark defaults
        UIManager.put("Panel.background",           BG_DARK);
        UIManager.put("Label.foreground",           TEXT_PRIMARY);
        UIManager.put("TextField.background",       BG_INPUT);
        UIManager.put("TextField.foreground",       TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",  ACCENT_BLUE);
        UIManager.put("PasswordField.background",   BG_INPUT);
        UIManager.put("PasswordField.foreground",   TEXT_PRIMARY);
        UIManager.put("TabbedPane.background",      BG_DARK);
        UIManager.put("TabbedPane.foreground",      TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",        BG_CARD);

        SwingUtilities.invokeLater(() -> {
            HashUI ui = new HashUI();
            ui.setVisible(true);
        });
    }
}
