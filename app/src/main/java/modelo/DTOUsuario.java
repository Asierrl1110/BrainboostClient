package modelo;

import java.io.Serializable;

public class DTOUsuario implements Serializable {

    private int id;

    private String nombre;

    private String clave;

    private String nuevaClave;

    public DTOUsuario(int id, String nombre, String clave) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
    }

    public DTOUsuario(String nombre, String clave) {
        this.nombre = nombre;
        this.clave = clave;
    }

    public DTOUsuario(String nombre, String clave, String nuevaClave) {
        this.nombre = nombre;
        this.clave = clave;
        this.nuevaClave = nuevaClave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNuevaClave() {
        return nuevaClave;
    }

    public void setNuevaClave(String nuevaClave) {
        this.nuevaClave = nuevaClave;
    }


}