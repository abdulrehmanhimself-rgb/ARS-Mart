import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatGPTUI extends JDialog {
    
    private RealAIChatbot chatbot;
    private String username;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JPanel headerPanel;
    private JScrollPane scrollPane;
    private SimpleDateFormat timeFormat;
    private Point mouseClickPoint;
    private JPanel thinkingPanel;
    private JLabel statusLabel;
    private Color headerColor = new Color(0, 120, 215);
    private javax.swing.Timer colorTimer;
    private float hue = 0;
    
    public ChatGPTUI(JFrame parent, String username) {
        super(parent, "ARS Mart AI Assistant", false);
        this.username = username;
        this.chatbot = new RealAIChatbot(username);
        this.timeFormat = new SimpleDateFormat("hh:mm a");
        
        setupUI();
        startRainbowEffect();
        showWelcomeMessage();
        
        setVisible(true);
    }
    
    private void setupUI() {
        setSize(500, 700);
        setLocationRelativeTo(getParent());
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 18, 22));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
        
        // Header
        headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(18, 18, 22));
        chatArea.setForeground(Color.WHITE);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatArea.setMargin(new Insets(15, 15, 15, 15));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(18, 18, 22));
        scrollPane.getViewport().setBackground(new Color(18, 18, 22));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Thinking panel
        thinkingPanel = createThinkingPanel();
        mainPanel.add(thinkingPanel, BorderLayout.SOUTH);
        thinkingPanel.setVisible(false);
        
        // Input panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Fade in
        fadeIn();
        
        // Make draggable
        makeDraggable();
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(headerColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setPreferredSize(new Dimension(500, 70));
        panel.setOpaque(false);
        
        // Left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        leftPanel.setOpaque(false);
        
        JLabel avatar = new JLabel("🛍️");
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        
        JLabel title = new JLabel("ARS Mart Assistant");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("● Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(new Color(100, 255, 100));
        
        textPanel.add(title);
        textPanel.add(statusLabel);
        
        leftPanel.add(avatar);
        leftPanel.add(textPanel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 18));
        rightPanel.setOpaque(false);
        
        JButton clearBtn = createControlButton("🗑️");
        clearBtn.addActionListener(e -> clearChat());
        
        JButton minBtn = createControlButton("−");
        minBtn.addActionListener(e -> setVisible(false));
        
        JButton closeBtn = createControlButton("✕");
        closeBtn.addActionListener(e -> dispose());
        
        rightPanel.add(clearBtn);
        rightPanel.add(minBtn);
        rightPanel.add(closeBtn);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createControlButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(35, 35));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 255, 255, 30));
                btn.setOpaque(true);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(null);
                btn.setOpaque(false);
            }
        });
        
        return btn;
    }
    
    private JPanel createThinkingPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(28, 28, 32));
        
        JLabel icon = new JLabel("🤖");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JLabel text = new JLabel("AI is thinking");
        text.setForeground(new Color(150, 150, 160));
        text.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        
        JLabel d1 = new JLabel(".");
        JLabel d2 = new JLabel(".");
        JLabel d3 = new JLabel(".");
        d1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        d2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        d3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        d1.setForeground(new Color(0, 120, 215));
        d2.setForeground(new Color(0, 120, 215));
        d3.setForeground(new Color(0, 120, 215));
        
        javax.swing.Timer timer = new javax.swing.Timer(400, new ActionListener() {
            int step = 0;
            public void actionPerformed(ActionEvent e) {
                step = (step + 1) % 4;
                d1.setVisible(step >= 1);
                d2.setVisible(step >= 2);
                d3.setVisible(step >= 3);
            }
        });
        timer.start();
        
        panel.add(icon);
        panel.add(text);
        panel.add(d1);
        panel.add(d2);
        panel.add(d3);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(25, 25, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 15, 15, 15));
        
        // Quick buttons
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        quickPanel.setOpaque(false);
        
        String[][] quick = {
            {"🛍️", "How to order?"},
            {"🚚", "Delivery time"},
            {"💳", "Payment methods"},
            {"🔄", "Return policy"}
        };
        
        for (String[] q : quick) {
            JButton btn = new JButton(q[0] + " " + q[1]);
            btn.setBackground(new Color(45, 45, 52));
            btn.setForeground(new Color(200, 200, 210));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                inputField.setText(q[1]);
                sendMessage();
            });
            quickPanel.add(btn);
        }
        
        panel.add(quickPanel, BorderLayout.NORTH);
        
        // Input field
        JPanel inputContainer = new JPanel(new BorderLayout(10, 0));
        inputContainer.setOpaque(false);
        
        inputField = new JTextField();
        inputField.setBackground(new Color(45, 45, 52));
        inputField.setForeground(Color.WHITE);
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setCaretColor(new Color(0, 120, 215));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        inputField.addActionListener(e -> sendMessage());
        
        sendButton = new JButton("Send →");
        sendButton.setBackground(new Color(0, 120, 215));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());
        
        inputContainer.add(inputField, BorderLayout.CENTER);
        inputContainer.add(sendButton, BorderLayout.EAST);
        
        panel.add(inputContainer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void startRainbowEffect() {
        colorTimer = new javax.swing.Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hue = (hue + 0.005f) % 1.0f;
                headerColor = Color.getHSBColor(hue, 0.8f, 0.7f);
                headerPanel.repaint();
            }
        });
        colorTimer.start();
    }
    
    private void showWelcomeMessage() {
        String welcome = "✨ **Welcome to ARS Mart!** ✨\n\n" +
                        "Hi **" + username + "**! I'm your AI shopping assistant.\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "💡 **You can ask me:**\n\n" +
                        "• \"How to place an order?\"\n" +
                        "• \"What's the delivery time?\"\n" +
                        "• \"What payment methods do you accept?\"\n" +
                        "• \"What's your return policy?\"\n" +
                        "• \"Show me products\"\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "How can I help you today? 🛍️";
        
        appendMessage("ARS Mart AI", welcome);
    }
    
    private void sendMessage() {
        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;
        
        appendMessage(username, msg);
        inputField.setText("");
        
        showThinking(true);
        sendButton.setEnabled(false);
        
        new Thread(() -> {
            try {
                Thread.sleep(500);
                String response = chatbot.getResponse(msg);
                
                SwingUtilities.invokeLater(() -> {
                    showThinking(false);
                    appendMessage("ARS Mart AI", response);
                    scrollToBottom();
                    sendButton.setEnabled(true);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showThinking(false);
                    appendMessage("ARS Mart AI", "Sorry, I encountered an error. Please try again.");
                    sendButton.setEnabled(true);
                });
            }
        }).start();
        
        inputField.requestFocus();
    }
    
    private void showThinking(boolean show) {
        SwingUtilities.invokeLater(() -> {
            thinkingPanel.setVisible(show);
            statusLabel.setText(show ? "● Thinking..." : "● Ready");
            statusLabel.setForeground(show ? new Color(255, 200, 50) : new Color(100, 255, 100));
            scrollToBottom();
        });
    }
    
    private void appendMessage(String sender, String message) {
        String time = timeFormat.format(new Date());
        String formatted;
        
        if (sender.equals(username)) {
            formatted = String.format("[%s] 👤 %s:\n%s\n\n", time, sender, message);
        } else {
            formatted = String.format("[%s] 🤖 %s:\n%s\n\n", time, sender, message);
        }
        
        SwingUtilities.invokeLater(() -> {
            chatArea.append(formatted);
            scrollToBottom();
        });
    }
    
    private void clearChat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all messages?", "Clear Chat", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            chatArea.setText("");
            showWelcomeMessage();
        }
    }
    
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    private void fadeIn() {
        setOpacity(0f);
        javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() {
            float opacity = 0;
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    ((javax.swing.Timer)e.getSource()).stop();
                }
                setOpacity(opacity);
            }
        });
        timer.start();
    }
    
    private void makeDraggable() {
        headerPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseClickPoint = e.getPoint();
            }
        });
        headerPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point current = getLocation();
                setLocation(current.x + e.getX() - mouseClickPoint.x,
                           current.y + e.getY() - mouseClickPoint.y);
            }
        });
    }
    
    @Override
    public void dispose() {
        if (colorTimer != null) colorTimer.stop();
        super.dispose();
    }
}