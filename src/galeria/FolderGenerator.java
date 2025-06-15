package galeria;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public class FolderGenerator {
	
	public static void generateDefaultFolders() {
		                    //default folder where program is running
        String basePath = System.getProperty("user.dir") + "/biblioteca_imagenes";
        new File(basePath).mkdirs(); // Crear carpeta si no existe
        //prueba de push 
        System.out.println();
        Random random = new Random();
        generateFolders(basePath, 3, 3, Arrays.asList("fotos", "viajes", "personal", "familia", "mascota", "montaña", "zaragoza", "valencia", "juan"), random);
    } 
	
	private static void generateFolders(String currentPath, int maxFoldersPerLevel, int maxDepth, List<String> folderNames, Random random) {
        if (maxDepth <= 0) return;

        // Número aleatorio DIFERENTE para cada carpeta (entre 1 y maxFoldersPerLevel)
        int numFolders = random.nextInt(maxFoldersPerLevel) + 1;
        
        for (int i = 0; i < numFolders; i++) {
            String folderName = folderNames.get(random.nextInt(folderNames.size()));
            File newFolder = new File(currentPath, folderName);
            newFolder.mkdirs();
            System.out.println("Carpeta creada: " + newFolder.getAbsolutePath());
            try {
            	//Meter un for con random para que no sea fijo el número de imágenes generadas 
				createImages(newFolder.getAbsolutePath() + "/" + newFolder.getName() + (i + 1) + ".jpg", 800, 600);
				System.out.println("Imagen creada en: " + newFolder.getAbsolutePath() + (i + 1) + ".jpg");
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            // Llamada recursiva con depth - 1
            generateFolders(newFolder.getAbsolutePath(), maxFoldersPerLevel, maxDepth - 1,
                          folderNames, random);
        }
    }
	
	public static void createImages(String outputPath, int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Fondo blanco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Dibujar formas aleatorias
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            switch (random.nextInt(3)) {
                case 0 -> g2d.fillRect(random.nextInt(width), random.nextInt(height), 50, 50); // Rectángulo
                case 1 -> g2d.fillOval(random.nextInt(width), random.nextInt(height), 50, 50); // Círculo
                case 2 -> g2d.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height)); // Línea
            }
        }
        
        g2d.dispose();
        ImageIO.write(image, "JPG", new File(outputPath));
    }

}
