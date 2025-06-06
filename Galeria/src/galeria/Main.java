package galeria;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public static void main(String[] args) {
        String folderPath = System.getProperty("user.dir") + "/biblioteca_imagenes";
        new File(folderPath).mkdirs(); // Crear carpeta si no existe

        try {       	
        	Funciones.generateDefaultFolders();  
        } catch (Exception e) {
            System.err.println("Error al crear imágenes: " + e.getMessage());
        }
    }

}
