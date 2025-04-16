package com.example.clienteproyectofinal;

import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import modelo.DTOMazo;
import modelo.DTOUsuario;

public class SocketConnection extends Thread {

    private String caso, nombreUsuario, claveUsuario, nombreMazo, categoriaMazo;

    private int idUsuario, idMazo;

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    private boolean instruccionRealizada;

    public void setNombreMazo(String nombreMazo) {
        this.nombreMazo = nombreMazo;
    }

    public void setCategoriaMazo(String categoriaMazo) {
        this.categoriaMazo = categoriaMazo;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public boolean isInstruccionRealizada() {
        return instruccionRealizada;
    }

    public SocketConnection(String caso){
        this.caso=caso;
    }

    public void run(){
        DataOutputStream dos;
        DataInputStream dis;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try{
            dos = new DataOutputStream(SocketManager.getSocket().getOutputStream());
            dis = new DataInputStream(SocketManager.getSocket().getInputStream());
            oos = new ObjectOutputStream(SocketManager.getSocket().getOutputStream());
            ois = new ObjectInputStream(SocketManager.getSocket().getInputStream());
            switch (caso){
                case "Registrarse":
                    dos.writeUTF("Registrarse");
                    DTOUsuario nuevoUsuario = new DTOUsuario(nombreUsuario,claveUsuario);
                    oos.writeObject(nuevoUsuario);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;

                case "IniciarSesion":
                    dos.writeUTF("IniciarSesion");
                    DTOUsuario usuario = new DTOUsuario(nombreUsuario,claveUsuario);
                    oos.writeObject(usuario);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    if(instruccionRealizada){
                        usuario.setId(ois.readInt());
                        ZonaCompartida.setUsuarioRegistrado(usuario);
                    }
                    SocketManager.getSocket().close();
                    break;

                case "AnadirMazo":
                    dos.writeUTF("AnadirMazo");
                    DTOMazo nuevoMazo = new DTOMazo(nombreMazo,categoriaMazo,idUsuario);
                    oos.writeObject(nuevoMazo);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
