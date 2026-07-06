import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FloatingChatButton {
    
    private JFrame parentFrame;
    private JWindow floatingWindow;
    private JButton chatButton;
    private ChatGPTUI chatbotUI;
    private String username;
    private boolean isChatOpen = false;
    private Timer pulseTimer;
    private float pulseScale = 1.0f;
    
    private static final int BUTTON_SIZE = 65;
    private static final int OFFSET_RIGHT = 30;
    private static final int OFFSET_BOTTOM = 30;
    
    public FloatingChatButton(JFrame parent, String username) {
        this.parentFrame = parent;
        this.username = username;
        createButton();
        startPulse();
    }
    
    private void createButton() {
        floatingWindow = new JWindow(parentFrame);
        floatingWindow.setSize(BUTTON_SIZE, BUTTON_SIZE);
        floatingWindow.setBackground(new Color(0, 0, 0, 0));
        
        chatButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 150, 255), 
                                                     getWidth(), getHeight(), new Color(100, 50, 200));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 65, 65);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 32));
                FontMetrics fm = g2d.getFontMetrics();
                String icon = isChatOpen ? "✕" : "💬";
                int x = (getWidth() - fm.stringWidth(icon)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(icon, x, y);
            }
        };
        
        chatButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        chatButton.setFocusPainted(false);
        chatButton.setBorderPainted(false);
        chatButton.setContentAreaFilled(false);
        chatButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chatButton.addActionListener(e -> toggle());
        
        floatingWindow.add(chatButton);
        makeDraggable();
        positionButton();
        
        parentFrame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) { positionButton(); }
            public void componentResized(ComponentEvent e) { positionButton(); }
        });
        
        floatingWindow.setVisible(true);
    }
    
    private void startPulse() {
        pulseTimer = new Timer(30, new ActionListener() {
            float dir = 0.02f;
            public void actionPerformed(ActionEvent e) {
                if (!isChatOpen) {
                    pulseScale += dir;
                    if (pulseScale >= 1.1f) dir = -0.02f;
                    if (pulseScale <= 0.95f) dir = 0.02f;
                    
                    int size = (int)(BUTTON_SIZE * pulseScale);
                    floatingWindow.setSize(size, size);
                    positionButton();
                } else if (pulseScale != 1.0f) {
                    pulseScale = 1.0f;
                    floatingWindow.setSize(BUTTON_SIZE, BUTTON_SIZE);
                    positionButton();
                }
            }
        });
        pulseTimer.start();
    }
    
    private void positionButton() {
        Point parentLoc = parentFrame.getLocation();
        Dimension parentSize = parentFrame.getSize();
        
        int x = parentLoc.x + parentSize.width - BUTTON_SIZE - OFFSET_RIGHT;
        int y = parentLoc.y + parentSize.height - BUTTON_SIZE - OFFSET_BOTTOM;
        
        floatingWindow.setLocation(x, y);
        floatingWindow.setAlwaysOnTop(false);
    }
    
    private void makeDraggable() {
        final Point[] dragStart = {null};
        
        chatButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
            public void mouseReleased(MouseEvent e) { dragStart[0] = null; }
        });
        
        chatButton.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null) {
                    Point current = floatingWindow.getLocation();
                    floatingWindow.setLocation(current.x + e.getX() - dragStart[0].x,
                                               current.y + e.getY() - dragStart[0].y);
                }
            }
        });
    }
    
    private void toggle() {
        if (isChatOpen && chatbotUI != null && chatbotUI.isVisible()) {
            chatbotUI.dispose();
            isChatOpen = false;
            chatButton.repaint();
        } else {
            if (chatbotUI == null || !chatbotUI.isVisible()) {
                chatbotUI = new ChatGPTUI(parentFrame, username);
                chatbotUI.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        isChatOpen = false;
                        chatButton.repaint();
                    }
                });
                isChatOpen = true;
                chatButton.repaint();
            }
        }
    }
    
    public void dispose() {
        if (pulseTimer != null) pulseTimer.stop();
        if (floatingWindow != null) floatingWindow.dispose();
        if (chatbotUI != null) chatbotUI.dispose();
    }
}