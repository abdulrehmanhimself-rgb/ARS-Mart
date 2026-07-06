import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * Central helper to bind ENTER to a Swing JButton programmatically.
 */
public final class SwingEnterKeyBinder {
    private SwingEnterKeyBinder() {}

    public static void bindEnterToButton(JRootPane rootPane, JButton button) {
        if (rootPane == null || button == null) return;

        // Use InputMap/ActionMap so ENTER works regardless of current focused component.
        String actionKey = "enterKeyFor:" + button.getText();

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(javax.swing.KeyStroke.getKeyStroke("ENTER"), actionKey);

        rootPane.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (button.isEnabled()) {
                    button.doClick();
                }
            }
        });
    }
}

