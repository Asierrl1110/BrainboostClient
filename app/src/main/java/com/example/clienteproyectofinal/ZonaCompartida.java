package com.example.clienteproyectofinal;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import modelo.DTOMazo;
import modelo.DTOTarjeta;
import modelo.DTOUsuario;

/**
 * Clase que almacena los datos que se van a utilizar en toda la aplicación o en varias zonas de la aplicación
 */
public class ZonaCompartida {

    private static boolean isOnline = false;
    // Variable que almacena los usuarios del usuario que esta con la sesión iniciada
    private static DTOUsuario usuarioRegistrado;

    // Variable que almacena una lista de los mazos del usuario registrado
    private static List<DTOMazo> mazos = new ArrayList<DTOMazo>();

    // Variable que almacena una lista de las tarjetas del usuario
    private static List<DTOTarjeta> tarjetas = new ArrayList<DTOTarjeta>();

    // Lista que almacena las tarjetas de una sesion de estudio
    private static List<DTOTarjeta> tarjetasEstudio = new ArrayList<DTOTarjeta>();

    // Variable que almacena una lista de las activities que se han ejecutado
    private static List<Activity> activities = new ArrayList<Activity>();

    public static void setUsuarioRegistrado(DTOUsuario usuarioRegistrado) {
        ZonaCompartida.usuarioRegistrado = usuarioRegistrado;
    }

    public static void setMazos(List<DTOMazo> mazos){
        ZonaCompartida.mazos = mazos;
    }

    public static void setTarjetas(List<DTOTarjeta> tarjetas){
        ZonaCompartida.tarjetas = tarjetas;
    }

    public static void setTarjetasEstudio(List<DTOTarjeta> tarjetas){
        ZonaCompartida.tarjetasEstudio = tarjetas;
    }

    public static void setIsOnline(boolean isOnline){
        ZonaCompartida.isOnline = isOnline;
    }

    public static DTOUsuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }

    public static List<DTOMazo> getMazos(){
        return mazos;
    }

    public static List<DTOTarjeta> getTarjetas(){
        return tarjetas;
    }

     public static List<DTOTarjeta> getTarjetasEstudio(){
        return tarjetasEstudio;
    }

    public static boolean isIsOnline(){
        return isOnline;
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void eliminarActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * Método que finaliza todas las activities que se han abierto excepto la primera que se ejecuto
     * (LoginActivity), provocando un cierre de sesión
     */
    public static void cerrarSesion() {
        if (activities.size() <= 1) return;

        // Cierra todas menos la primera
        for (int i = 1; i < activities.size(); i++) {
            activities.get(i).finish();
        }

        // Limpia la lista desde la segunda posición
        activities.subList(1, activities.size()).clear();
    }
}
