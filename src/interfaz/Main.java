package interfaz;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageLibraryGUI gui = new ImageLibraryGUI();
            gui.setVisible(true);
        });
    }
}
