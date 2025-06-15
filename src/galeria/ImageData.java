package galeria;

import java.util.Date;

class ImageData {
    private String path;
    private String name;
    private long size;
    private int width;
    private int height;
    private Date captureDate;
    private String latitude;
    private String longitude;

    public ImageData(String path, String name, long size, int width, int height, Date captureDate, String latitude, String longitude) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
        this.captureDate = captureDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPath() { return path; }
    public String getName() { return name; }
    public long getSize() { return size; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Date getCaptureDate() { return captureDate; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
}