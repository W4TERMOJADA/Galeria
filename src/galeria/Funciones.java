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

//imports imagenes 

import org.apache.commons.imaging.ImageInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Funciones {
	
	public static void deleteFolder(File folder) {
        if (!folder.exists()) return;
        
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFolder(file);
            }
        }
        folder.delete(); 
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
        //hacer lo mismo con longitud

        // 3. Guardar cambios
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            new ExifRewriter().updateExifMetadataLossy(imageFile, fos, outputSet);
        }
    }
	
	
	
	
	
	
	
	//------ANALIZAR IMAGENES------
	
	
	
	
	
	
	
	// Dentro de la clase Funciones.java o una nueva clase Analizador.java

	public static List<ImagenInfo> analizarImagenes(String rutaBase) {
	    List<ImagenInfo> coleccion = new ArrayList<>();
	    File carpetaBase = new File(rutaBase);
	    
	    if (carpetaBase.exists() && carpetaBase.isDirectory()) {
	        recorrerYAnalizar(carpetaBase, coleccion);
	    } else {
	        System.err.println("La ruta proporcionada no es un directorio válido.");
	    }
	    return coleccion;
	}

	private static void recorrerYAnalizar(File directorio, List<ImagenInfo> coleccion) {
	    File[] archivos = directorio.listFiles();
	    if (archivos == null) return;

	    for (File archivo : archivos) {
	        if (archivo.isDirectory()) {
	            recorrerYAnalizar(archivo, coleccion); // Llamada recursiva
	        } else if (esArchivoDeImagen(archivo.getName())) {
	            try {
	                ImageInfo info = Imaging.getImageInfo(archivo);
	                // Usamos Apache Commons Imaging para leer metadatos básicos
	                // Para metadatos EXIF como la fecha, necesitarías un análisis más profundo
	                // (Similar a como lo haces en updateExifMetadata pero para lectura)
	                
	                Date fecha = new Date(archivo.lastModified()); // Fecha de modificación como fallback
	                ImagenInfo imagen = new ImagenInfo(
	                    archivo.getName(),
	                    archivo.getAbsolutePath(),
	                    archivo.length(),
	                    info.getWidth(),
	                    info.getHeight(),
	                    fecha 
	                );
	                coleccion.add(imagen);
	            } catch (Exception e) {
	                System.err.println("No se pudo analizar el archivo: " + archivo.getName());
	            }
	        }
	    }
	}

	private static boolean esArchivoDeImagen(String nombre) {
	    String lowerName = nombre.toLowerCase(); //haré todo jpg por comodidad exif
	    return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png");
	}
	
	
	
	
	//------ANALIZAR IMAGENES------
	
	
	// Dentro de la clase que contiene la colección de imágenes (p.ej. la clase principal de la GUI)


	private List<ImagenInfo> coleccionCompleta;
	private List<ImagenInfo> coleccionMostrada; // La que se ve en la tabla

	// ... inicializar coleccionCompleta llamando a analizarImagenes(...)

	public void ordenarPorFecha(boolean ascendente) {
	    if (ascendente) {
	        coleccionMostrada.sort(Comparator.comparing(ImagenInfo::getFechaCaptura));
	    } else {
	        coleccionMostrada.sort(Comparator.comparing(ImagenInfo::getFechaCaptura).reversed());
	    }
	    // Después de ordenar, hay que refrescar la TableView en la GUI
	}

	public void ordenarPorAncho(boolean ascendente) {
	    if (ascendente) {
	        coleccionMostrada.sort(Comparator.comparingInt(ImagenInfo::getAncho));
	    } else {
	        coleccionMostrada.sort(Comparator.comparingInt(ImagenInfo::getAncho).reversed());
	    }
	     // Refrescar la TableView
	}

	public void filtrarPorAnchoMinimo(int anchoMinimo) {
	    coleccionMostrada = coleccionCompleta.stream()
	        .filter(img -> img.getAncho() >= anchoMinimo)
	        .collect(Collectors.toList());
	    // Refrescar la TableView
	}

	public void resetearFiltros() {
	    coleccionMostrada = new ArrayList<>(coleccionCompleta);
	    // Refrescar la TableView
	}
	
}
