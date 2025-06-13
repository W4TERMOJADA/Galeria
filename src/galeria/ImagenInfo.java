package galeria;

import java.util.Date;

public class ImagenInfo {
    private String nombre;
    private String ruta;
    private long tamano; // en bytes
    private int ancho;
    private int alto;
    private Date fechaCaptura;
    // Puedes añadir más campos como latitud, longitud, etc.

    public ImagenInfo(String nombre, String ruta, long tamano, int ancho, int alto, Date fechaCaptura) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.tamano = tamano;
        this.ancho = ancho;
        this.alto = alto;
        this.fechaCaptura = fechaCaptura;
    }

    // --- Getters y Setters para todas las propiedades ---
    // (Opcional pero recomendado para JavaFX)

    public String getNombre() { return nombre; }
    public String getRuta() { return ruta; }
    public long getTamano() { return tamano; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public Date getFechaCaptura() { return fechaCaptura; }
}
