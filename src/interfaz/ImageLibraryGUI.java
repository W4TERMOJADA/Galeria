package interfaz;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class ImageLibraryGUI extends JFrame {
    private static final String ROOT_PATH = "Images";
    private JTree folderTree;
    private JTable imageTable;
    private JLabel imageView;
    private DefaultTableModel tableModel;
    private List<ImageData> currentImages;

    public ImageLibraryGUI() {
        setTitle("Biblioteca de Imágenes");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        File rootDir = new File(ROOT_PATH);
        if (!rootDir.exists()) {
            FolderGenerator.createFolderStructure(ROOT_PATH, 3, 4);
            populateFoldersWithImages(rootDir);
        }

        DefaultMutableTreeNode rootNode = buildTree(new File(ROOT_PATH));
        folderTree = new JTree(new DefaultTreeModel(rootNode));
        folderTree.addTreeSelectionListener(this::folderSelected);
        JScrollPane treeScrollPane = new JScrollPane(folderTree);

        String[] columns = {"Nombre", "Tamaño (bytes)", "Ancho", "Alto", "Fecha de Captura", "Latitud", "Longitud"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        imageTable = new JTable(tableModel);
        imageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = imageTable.getSelectedRow();
                if (row >= 0) displayImage(currentImages.get(row).getPath());
            }
        });
        imageTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = imageTable.columnAtPoint(e.getPoint());
                sortTable(col);
            }
        });
        JScrollPane tableScrollPane = new JScrollPane(imageTable);

        imageView = new JLabel();
        imageView.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageView);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, imageScrollPane);
        rightSplitPane.setDividerLocation(300);
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, rightSplitPane);
        mainSplitPane.setDividerLocation(200);
        add(mainSplitPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem createCollectionItem = new JMenuItem("Crear Nueva Colección");
        createCollectionItem.addActionListener(e -> createNewCollection());
        JMenuItem addFolderItem = new JMenuItem("Añadir Carpeta");
        addFolderItem.addActionListener(e -> addNewFolder());
        JMenuItem addImageItem = new JMenuItem("Añadir Imagen");
        addImageItem.addActionListener(e -> addNewImage());
        JMenuItem analyzeItem = new JMenuItem("Analizar Carpeta");
        analyzeItem.addActionListener(e -> analyzeFolder());
        fileMenu.add(createCollectionItem);
        fileMenu.add(addFolderItem);
        fileMenu.add(addImageItem);
        fileMenu.add(analyzeItem);

        JMenu editMenu = new JMenu("Editar");
        JMenuItem editMetadataItem = new JMenuItem("Editar Metadatos");
        editMetadataItem.addActionListener(e -> editMetadata());
        editMenu.add(editMetadataItem);

        JMenu filterMenu = new JMenu("Filtrar");
        JMenuItem widthFilterItem = new JMenuItem("Filtrar por Ancho (>= 500px)");
        widthFilterItem.addActionListener(e -> applyWidthFilter());
        JMenuItem dateFilterItem = new JMenuItem("Filtrar por Fecha");
        dateFilterItem.addActionListener(e -> applyDateFilter());
        filterMenu.add(widthFilterItem);
        filterMenu.add(dateFilterItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(filterMenu);
        setJMenuBar(menuBar);

        folderTree.setSelectionRow(0);
    }

    private DefaultMutableTreeNode buildTree(File dir) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getName());
        node.setUserObject(dir);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(buildTree(file));
                }
            }
        }
        return node;
    }

    private void folderSelected(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (node == null) return;
        File folder = (File) node.getUserObject();
        currentImages = ImageAnalyzer.analyzeFolder(folder);
        updateTable(currentImages);
        imageView.setIcon(null);
    }

    private void updateTable(List<ImageData> images) {
        tableModel.setRowCount(0);
        for (ImageData img : images) {
            tableModel.addRow(new Object[]{
                img.getName(),
                img.getSize(),
                img.getWidth(),
                img.getHeight(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(img.getCaptureDate()),
                img.getLatitude(),
                img.getLongitude()
            });
        }
    }

    private void displayImage(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaledImg = icon.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
            imageView.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortTable(int column) {
        if (currentImages == null) return;
        switch (column) {
            case 0: currentImages.sort(Comparator.comparing(ImageData::getName)); break;
            case 1: currentImages.sort(Comparator.comparingLong(ImageData::getSize)); break;
            case 2: currentImages = ImageSorter.sortByWidth(currentImages, true); break;
            case 3: currentImages.sort(Comparator.comparingInt(ImageData::getHeight)); break;
            case 4: currentImages = ImageSorter.sortByDate(currentImages, true); break;
        }
        updateTable(currentImages);
    }

    private void createNewCollection() {
        File rootDir = new File(ROOT_PATH);
        if (rootDir.exists()) {
            deleteDirectory(rootDir);
        }
        FolderGenerator.createFolderStructure(ROOT_PATH, 3, 4);
        populateFoldersWithImages(rootDir);
        DefaultMutableTreeNode rootNode = buildTree(rootDir);
        folderTree.setModel(new DefaultTreeModel(rootNode));
        folderTree.setSelectionRow(0);
    }

    private void addNewFolder() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una carpeta primero");
            return;
        }
        File selectedFolder = (File) selectedNode.getUserObject();
        String folderName = JOptionPane.showInputDialog(this, "Nombre de la nueva carpeta:");
        if (folderName != null && !folderName.isEmpty()) {
            File newFolder = new File(selectedFolder, folderName);
            if (newFolder.mkdir()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFolder.getName());
                newNode.setUserObject(newFolder);
                selectedNode.add(newNode);
                ((DefaultTreeModel) folderTree.getModel()).nodeStructureChanged(selectedNode);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la carpeta");
            }
        }
    }

    private void addNewImage() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una carpeta primero");
            return;
        }
        File selectedFolder = (File) selectedNode.getUserObject();
        try {
            String imagePath = new File(selectedFolder, "new_image_" + System.currentTimeMillis() + ".jpg").getAbsolutePath();
            ImageCreator.createImage(imagePath, 300, 300, "jpg");
            MetadataEditor.setRandomMetadata(new File(imagePath));
            analyzeFolder();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al crear la imagen");
        }
    }

    private void analyzeFolder() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) folderTree.getLastSelectedPathComponent();
        if (node == null) return;
        File folder = (File) node.getUserObject();
        currentImages = ImageAnalyzer.analyzeFolder(folder);
        updateTable(currentImages);
    }

    private void applyWidthFilter() {
        if (currentImages == null) return;
        currentImages = ImageFilter.filterByWidth(currentImages, 500);
        updateTable(currentImages);
    }

    private void applyDateFilter() {
        if (currentImages == null) return;
        String dateStr = JOptionPane.showInputDialog(this, "Ingrese la fecha (yyyy-MM-dd):");
        if (dateStr != null) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                currentImages = ImageFilter.filterByDate(currentImages, date);
                updateTable(currentImages);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido");
            }
        }
    }

    private void editMetadata() {
        int selectedRow = imageTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una imagen primero");
            return;
        }
        ImageData selectedImage = currentImages.get(selectedRow);
        String currentDate = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(selectedImage.getCaptureDate());
        String currentLat = selectedImage.getLatitude();
        String currentLon = selectedImage.getLongitude();

        MetadataEditorDialog dialog = new MetadataEditorDialog(this, currentDate, currentLat, currentLon);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            try {
                MetadataEditor.updateMetadata(new File(selectedImage.getPath()), dialog.getDateStr(), dialog.getLatStr(), dialog.getLonStr());
                analyzeFolder();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al actualizar metadatos");
            }
        }
    }

    private void populateFoldersWithImages(File rootDir) {
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    try {
                        ImageCreator.populateFolderWithImages(file, 3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    populateFoldersWithImages(file);
                }
            }
        }
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }
}
