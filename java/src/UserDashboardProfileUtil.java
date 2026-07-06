import javax.swing.*;
import java.awt.*;

// Helper extracted only to keep UserDashboard simple.
// Not strictly required by build, but safe.
public class UserDashboardProfileUtil {
    public static void showProfile(JFrame parent, String username) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.BACKGROUND);

        JLabel title = new JLabel("USER PROFILE", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.TITLE_FONT);
        p.add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(new Color(20, 20, 24));
        area.setForeground(Color.WHITE);

        // We don't have a dedicated backend command for user details in the provided C++.
        // So we show currently known info.
        area.setText(
                "Username: " + username + "\n\n" +
                "(Profile details can be extended once backend endpoints are added.)\n"
        );

        p.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton back = new JButton("Back");
        back.setBackground(new Color(200, 50, 50));
        back.setForeground(Color.WHITE);
        back.setFont(UITheme.BUTTON_FONT);
        back.setFocusPainted(false);
        back.setBorderPainted(false);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        bottom.setBackground(UITheme.BACKGROUND);
        bottom.add(back);
        p.add(bottom, BorderLayout.SOUTH);

        JDialog d = new JDialog(parent, "Profile", true);
        d.setSize(680, 520);
        d.setLocationRelativeTo(parent);
        d.setContentPane(p);
        d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        d.setVisible(true);


        back.addActionListener(e -> {
            d.dispose();
            // No-op: parent dashboard remains visible behind the modal dialog.
        });
    }
}

