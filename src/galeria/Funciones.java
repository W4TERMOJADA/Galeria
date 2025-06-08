package galeria;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class Funciones {
	
	public static void generateDefaultFolders() {
		                    //default folder where program is running
        String basePath = System.getProperty("user.dir") + "/biblioteca_imagenes";
        new File(basePath).mkdirs(); // Crear carpeta si no existe
        //prueba de push 
        System.out.println();
        Random random = new Random();
        generateFolders(basePath, 3, 3, Arrays.asList("fotos", "viajes", "personal", "familia", "mascota", "montaña", "zaragoza", "valencia", "juan"), random);
    } 

    private static void generateFolders(String currentPath, int maxFoldersPerLevel, int maxDepth,List<String> folderNames, Random random) {
        if (maxDepth <= 0) return;

        // Número aleatorio DIFERENTE para cada carpeta (entre 1 y maxFoldersPerLevel)
        int numFolders = random.nextInt(maxFoldersPerLevel) + 1;
        
        for (int i = 0; i < numFolders; i++) {
            String folderName = folderNames.get(random.nextInt(folderNames.size()));
            File newFolder = new File(currentPath, folderName);
            newFolder.mkdirs();
            System.out.println("Carpeta creada: " + newFolder.getAbsolutePath());
            try {
				createImages(newFolder.getAbsolutePath() + "/" + newFolder.getName() + (i + 1) + ".jpg", 800, 600);
				System.out.println("Imagen creada en: " + newFolder.getAbsolutePath() + (i + 1) + ".jpg");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error al crear imagen en la carpeta: " + newFolder.getAbsolutePath());
			}
            
            // Llamada recursiva con depth - 1
            generateFolders(newFolder.getAbsolutePath(), maxFoldersPerLevel, maxDepth - 1,
                          folderNames, random);
        }
    }
	
	
	
	public static void deleteFolder(File folder) {
        if (!folder.exists()) return;
        
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFolder(file); // Borra subcarpetas/archivos primero
            }
        }
        folder.delete(); 
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
	
	public static void updateExifMetadata(Path imagePath, Date captureDate, double latitude, double longitude)
            throws ImagingException, IOException {

        File imageFile = imagePath.toFile();
        TiffOutputSet outputSet = new TiffOutputSet(); //almacena los cambios de EXIF que mas tarde se guardarán en el archivo

        // 1. Actualizar fecha de captura (EXIF general)
        TiffOutputDirectory exifDir = outputSet.getOrCreateExifDirectory();
        exifDir.add(
                ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, // Constante correcta
                new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(captureDate)
        );

        // 2. Actualizar coordenadas GPS
        TiffOutputDirectory gpsDir = outputSet.getOrCreateGpsDirectory();
        gpsDir.add(
                GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF,
                latitude >= 0 ? "N" : "S"
        );

        // Conversión a RationalNumber[]
        double absLatitude = Math.abs(latitude);
        int degrees = (int) absLatitude;
        double remaining = absLatitude - degrees;
        double minutes = remaining * 60;
        int minutesInt = (int) minutes;
        double seconds = (minutes - minutesInt) * 60;

        RationalNumber[] latRationals = new RationalNumber[]{
                new RationalNumber(degrees, 1),
                new RationalNumber(minutesInt, 1),
                new RationalNumber((int) (seconds * 100), 100)
        };

        gpsDir.add(
                GpsTagConstants.GPS_TAG_GPS_LATITUDE,
                latRationals
        );

        // 3. Guardar cambios
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            new ExifRewriter().updateExifMetadataLossy(imageFile, fos, outputSet);
        }
    }
	
	
}
