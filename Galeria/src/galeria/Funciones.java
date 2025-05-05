package galeria;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

public class Funciones {
	
	public static void generateDefaultFolders() {
        String basePath = System.getProperty("user.dir") + "/biblioteca_imagenes";
        new File(basePath).mkdirs(); // Crear carpeta si no existe
        
        Random random = new Random();
        generateFolders(basePath, 3, 2, Arrays.asList("fotos", "viajes", "personal"), random);
    }

    private static void generateFolders(String currentPath, int maxFoldersPerLevel, int maxDepth,List<String> folderNames, Random random) {
        if (maxDepth <= 0) return;

        // Número aleatorio DIFERENTE para cada carpeta (entre 1 y maxFoldersPerLevel)
        int numFolders = random.nextInt(maxFoldersPerLevel) + 1;
        
        for (int i = 0; i < numFolders; i++) {
            String folderName = folderNames.get(random.nextInt(folderNames.size()));
            File newFolder = new File(currentPath, folderName);
            newFolder.mkdirs();
            
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
        ImageIO.write(image, "PNG", new File(outputPath));
    }
	
	public static void updateExifMetadata(String imagePath, Date captureDate, 
            double latitude, double longitude) throws ImagingException, IOException {

		File imageFile = new File(imagePath);
		TiffImageMetadata exifData = Imaging.getMetadata(imageFile).getExif();

		TiffOutputSet outputSet = (exifData != null) ? exifData.getOutputSet() : new TiffOutputSet();


		outputSet.getOrCreateExifDirectory().addField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(captureDate));

		// Actualizar GPS usando RationalNumber
		TiffOutputDirectory gpsDir = outputSet.getOrCreateGPSDirectory();
		gpsDir.addField(ExifTagConstants.GPS_TAG_GPS_LATITUDE_REF, 
		latitude >= 0 ? "N" : "S");

		RationalNumber[] latRational = RationalNumber.decimalToRational(latitude);
		gpsDir.addField(ExifTagConstants.GPS_TAG_GPS_LATITUDE, latRational);

		// Guardar cambios
		try (FileOutputStream fos = new FileOutputStream(imageFile)) {
			new ExifRewriter().updateExifMetadataLossless(imageFile, fos, outputSet);
		}
	}
	
	
}
