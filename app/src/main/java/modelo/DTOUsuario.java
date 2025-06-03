package modelo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DTOUsuario implements Serializable {

    private int id;

    private String nombreUsuario;

    private String clave;

    private String nuevaClave;

    private String nombre;

    private String apellidos;

    private Date fechaNacimiento;

    private String genero;

    private String rol;

    public DTOUsuario(int id, String nombreUsuario, String clave, String nombre, String apellidos, Date fechaNacimiento, String genero, String rol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.rol = rol;
    }

    public DTOUsuario(int id, String nombreUsuario, String clave) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
    }

    public DTOUsuario(String nombreUsuario, String clave){
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
    }

    public DTOUsuario(String nombreUsuario, String clave, String nombre, String apellidos, String fechaNacimiento, String genero, String rol) {
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.nombre = nombre;
        this.apellidos = apellidos;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            this.fechaNacimiento = sdf.parse(fechaNacimiento);
        } catch (ParseException e) {
            throw new RuntimeException("Error al parsear la fecha: " + fechaNacimiento, e);
        }

        this.genero = genero;
        this.rol = rol;
    }


    public DTOUsuario(String nombreUsuario, String clave, String nuevaClave) {
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.nuevaClave = nuevaClave;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}