package com.example.clienteproyectofinal;

import java.util.List;

import modelo.DTOMazo;
import modelo.DTOUsuario;

public class ZonaCompartida {

    private static DTOUsuario usuarioRegistrado;

    private static List<DTOMazo> mazos;

    public static void setUsuarioRegistrado(DTOUsuario usuarioRegistrado) {
        ZonaCompartida.usuarioRegistrado = usuarioRegistrado;
    }

    public static DTOUsuario getUsuarioRegistrado() {
        return usuarioRegistrado;
    }
}
