package com.example.clienteproyectofinal;

import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

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
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try{
            oos = new ObjectOutputStream(SocketManager.getSocket().getOutputStream());
            ois = new ObjectInputStream(SocketManager.getSocket().getInputStream());
            oos.writeUTF(caso);
            switch (caso){
                case "Registrarse":
                    DTOUsuario nuevoUsuario = new DTOUsuario(nombreUsuario,claveUsuario);
                    oos.writeObject(nuevoUsuario);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;

                case "IniciarSesion":
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
                    DTOMazo nuevoMazo = new DTOMazo(nombreMazo,categoriaMazo,idUsuario);
                    oos.writeObject(nuevoMazo);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;

                case "Mazos":
                    oos.writeInt(idUsuario);
                    oos.flush();
                    List<DTOMazo> mazos = (List<DTOMazo>) ois.readObject();
                    ZonaCompartida.setMazos(mazos);
                    SocketManager.getSocket().close();
                    break;
            }
        }catch (IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
        }
    }
}
