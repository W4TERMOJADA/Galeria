package interfaz;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class ImagenTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Nombre", "Ruta", "Ancho", "Alto", "Fecha"};
    private List<ImagenInfo> imageList;

    public ImagenTableModel() {
        this.imageList = new ArrayList<>();
    }
    
    public void setImages(List<ImagenInfo> imageList) {
        this.imageList = imageList;
        // Notifica a la tabla que los datos han cambiado para que se repinte
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return imageList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ImagenInfo imageInfo = imageList.get(rowIndex);
        switch (columnIndex) {
            case 0: return imageInfo.getNombre();
            case 1: return imageInfo.getRuta();
            case 2: return imageInfo.getAncho();
            case 3: return imageInfo.getAlto();
            case 4: return imageInfo.getFechaCaptura();
            default: return null;
        }
    }
    
    // Método para obtener el objeto ImagenInfo de una fila específica
    public ImagenInfo getImageAt(int rowIndex) {
        return imageList.get(rowIndex);
    }
}
