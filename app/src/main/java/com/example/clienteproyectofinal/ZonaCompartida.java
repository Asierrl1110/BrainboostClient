package com.example.clienteproyectofinal;

import java.util.ArrayList;
import java.util.List;

import modelo.DTOMazo;
import modelo.DTOUsuario;

public class ZonaCompartida {

    private static DTOUsuario usuarioRegistrado;

    private static List<DTOMazo> mazos = new ArrayList<DTOMazo>();

    public static void setUsuarioRegistrado(DTOUsuario usuarioRegistrado) {
        ZonaCompartida.usuarioRegistrado = usuarioRegistrado;
    }

    public static void setMazos(List<DTOMazo> mazos){
        ZonaCompartida.mazos = mazos;
    }

    public static DTOUsuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }

    public static List<DTOMazo> getMazos(){
        return mazos;
    }
}
