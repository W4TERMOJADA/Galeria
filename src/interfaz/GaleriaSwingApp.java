package interfaz;

import galeria.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.List;

public class GaleriaSwingApp extends JFrame {

    private JTable tableView;
    private ImagenTableModel tableModel;
    private JLabel imageViewLabel;
    private List<ImagenInfo> coleccionCompleta;

    public GaleriaSwingApp() {
        setTitle("Biblioteca de Imágenes (Swing)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        // --- Menús (3.3) ---
        JMenuBar menuBar = crearMenus();
        setJMenuBar(menuBar);

        // --- Panel Principal dividido ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7); // Dar más espacio inicial a la tabla

        // --- TableView (JTable) (3.1) ---
        tableModel = new ImagenTableModel();
        tableView = new JTable(tableModel);
        tableView.setAutoCreateRowSorter(true); // Habilita la ordenación por columnas
        JScrollPane scrollPane = new JScrollPane(tableView);
        splitPane.setLeftComponent(scrollPane);

        // --- ImageView (JLabel) (3.2) ---
        imageViewLabel = new JLabel("Seleccione una imagen", SwingConstants.CENTER);
        imageViewLabel.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageViewLabel);
        splitPane.setRightComponent(imageScrollPane);
        
        add(splitPane, BorderLayout.CENTER);

        // --- Lógica de selección ---
        tableView.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableView.getSelectedRow();
                if (selectedRow != -1) {
                    // Convertir el índice de la vista al del modelo (por si está ordenado)
                    int modelRow = tableView.convertRowIndexToModel(selectedRow);
                    ImagenInfo info = tableModel.getImageAt(modelRow);
                    actualizarImagen(info.getRuta());
                }
            }
        });
    }

    private JMenuBar crearMenus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        JMenu herramientasMenu = new JMenu("Herramientas");

        JMenuItem generarItem = new JMenuItem("Generar Biblioteca por Defecto");
        generarItem.addActionListener(e -> {
            // Se ejecuta en un hilo separado para no bloquear la GUI
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Funciones.generateDefaultFolders();
                    return null;
                }
                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(GaleriaSwingApp.this, 
                        "Biblioteca generada con éxito.", "Proceso Terminado", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }.execute();
        });

        JMenuItem analizarItem = new JMenuItem("Analizar Biblioteca");
        analizarItem.addActionListener(this::analizarAction);
        
        archivoMenu.add(generarItem);
        herramientasMenu.add(analizarItem);
        menuBar.add(archivoMenu);
        menuBar.add(herramientasMenu);
        return menuBar;
    }

    private void analizarAction(ActionEvent e) {
        String path = System.getProperty("user.dir") + "/biblioteca_imagenes";
        coleccionCompleta = Funciones.analizarImagenes(path); // Usa la función del Ejercicio 2
        tableModel.setImages(coleccionCompleta); // Actualiza el modelo de la tabla
        JOptionPane.showMessageDialog(this, 
            "Análisis completado. " + coleccionCompleta.size() + " imágenes encontradas.", 
            "Análisis", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void actualizarImagen(String rutaImagen) {
        try {
            BufferedImage img = ImageIO.read(new File(rutaImagen));
            // Escalar la imagen para que quepa en el JLabel sin perder la proporción
            int labelWidth = imageViewLabel.getParent().getWidth(); // Ancho del contenedor
            int labelHeight = imageViewLabel.getParent().getHeight(); // Alto del contenedor
            
            Image scaledImg = img.getScaledInstance(labelWidth, -1, Image.SCALE_SMOOTH); // Escalar por ancho
            if (scaledImg.getHeight(null) > labelHeight && labelHeight > 0) {
                 scaledImg = img.getScaledInstance(-1, labelHeight, Image.SCALE_SMOOTH); // Escalar por alto si es necesario
            }
            
            imageViewLabel.setIcon(new ImageIcon(scaledImg));
            imageViewLabel.setText(null); // Quitar el texto
        } catch (Exception ex) {
            imageViewLabel.setIcon(null);
            imageViewLabel.setText("No se pudo cargar la imagen.");
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // Es una buena práctica ejecutar las aplicaciones Swing en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            GaleriaSwingApp app = new GaleriaSwingApp();
            app.setLocationRelativeTo(null); // Centrar en pantalla
            app.setVisible(true);
        });
    }
}