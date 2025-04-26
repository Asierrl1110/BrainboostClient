package modelo;

import java.io.Serializable;

public class DTOMazo implements Serializable {

    private int id;

    private String nombre;

    private String categoria;

    private int idUsuario;

    public DTOMazo(String nombre, String categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public DTOMazo(int id, String nombre, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public DTOMazo(int id, String nombre, String categoria, int idUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.idUsuario = idUsuario;
    }

    public DTOMazo(String nombre, String categoria, int idUsuario) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.idUsuario = idUsuario;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
