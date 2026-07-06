import javax.swing.*;
import java.awt.*;

public class DashboardUtils {
    public static JLabel centeredTitle(String text, int width) {
        JLabel title = new JLabel(text, SwingConstants.CENTER);
        title.setBounds(0, 20, width, 50);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.TITLE_FONT);
        return title;
    }

    public static void styleRedBackButton(JButton backButton) {
        backButton.setBackground(new Color(200, 50, 50));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(UITheme.BUTTON_FONT);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
    }

    public static JPanel themedPanel(int w, int h) {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(w, h));
        return panel;
    }
}

