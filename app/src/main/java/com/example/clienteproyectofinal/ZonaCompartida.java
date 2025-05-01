package com.example.clienteproyectofinal;

import java.util.ArrayList;
import java.util.List;

import modelo.DTOMazo;
import modelo.DTOTarjeta;
import modelo.DTOUsuario;

public class ZonaCompartida {

    private static DTOUsuario usuarioRegistrado;

    private static List<DTOMazo> mazos = new ArrayList<DTOMazo>();

    private static List<DTOTarjeta> tarjetas = new ArrayList<DTOTarjeta>();

    public static void setUsuarioRegistrado(DTOUsuario usuarioRegistrado) {
        ZonaCompartida.usuarioRegistrado = usuarioRegistrado;
    }

    public static void setMazos(List<DTOMazo> mazos){
        ZonaCompartida.mazos = mazos;
    }

    public static void setTarjetas(List<DTOTarjeta> tarjetas){
        ZonaCompartida.tarjetas = tarjetas;
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
}
