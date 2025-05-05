package galeria;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	
	/**
	 * Generar 3 imagenes en una carpeta de prueba.
	 *
	 */
	public static void main(String[] args) {
        String folderPath = System.getProperty("user.dir") + "/biblioteca_imagenes";
        new File(folderPath).mkdirs(); // Crear carpeta si no existe

        try {
            for (int i = 1; i <= 3; i++) {
                String imagePath = folderPath + "/imagen_" + i + ".png";
                Funciones.createImages(imagePath, 800, 600);
                System.out.println("Imagen generada: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Error al crear imágenes: " + e.getMessage());
        }
    }

}
