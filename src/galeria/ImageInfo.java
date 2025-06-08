package galeria;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;

public class ImageInfo {
	private String nombreArchivo;
	private String path;
	private int width;
	private int height;
	private long tamano;
	private Date captureDate;
	private double latitude;
	private double longitude;
	
	public ImageInfo(String path, Date captureDate, int width, int height,
            double latitude, double longitude) {
		this.path = path;
		this.captureDate = captureDate; //maybe meter un random que haga que la decha de captura sea aleatoria
		this.width = width;
		this.height = height;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String getPath() { return path; }
    public Date getCaptureDate() { return captureDate; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
	
	
	
}
