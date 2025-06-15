package interfaz;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class MetadataEditorDialog extends JDialog {
    private JTextField dateField;
    private JTextField latField;
    private JTextField lonField;
    private JButton saveButton;
    private boolean saved = false;
    private String dateStr;
    private String latStr;
    private String lonStr;

    public MetadataEditorDialog(JFrame parent, String currentDate, String currentLat, String currentLon) {
        super(parent, "Editar Metadatos", true);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Fecha (yyyy:MM:dd HH:mm:ss):"));
        dateField = new JTextField(currentDate);
        add(dateField);

        add(new JLabel("Latitud:"));
        latField = new JTextField(currentLat);
        add(latField);

        add(new JLabel("Longitud:"));
        lonField = new JTextField(currentLon);
        add(lonField);

        saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> {
            dateStr = dateField.getText();
            latStr = latField.getText();
            lonStr = lonField.getText();
            saved = true;
            setVisible(false);
        });
        add(saveButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isSaved() { return saved; }
    public String getDateStr() { return dateStr; }
    public String getLatStr() { return latStr; }
    public String getLonStr() { return lonStr; }
}