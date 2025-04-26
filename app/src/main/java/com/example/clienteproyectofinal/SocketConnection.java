package com.example.clienteproyectofinal;

import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import modelo.DTOMazo;
import modelo.DTOTarjeta;
import modelo.DTOUsuario;

public class SocketConnection extends Thread {

    private String caso, nombreMazo, categoriaMazo;

    private int idUsuario, idMazo;

    private DTOUsuario usuario;

    private DTOMazo mazo;

    private DTOTarjeta tarjeta;

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

    public boolean isInstruccionRealizada() {
        return instruccionRealizada;
    }

    public SocketConnection(String caso){
        this.caso=caso;
    }

    public SocketConnection(String caso, DTOUsuario usuario){
        this.caso=caso;
        this.usuario=usuario;
    }

    public SocketConnection(String caso, DTOMazo mazo){
        this.caso=caso;
        this.mazo=mazo;
    }

    public SocketConnection(String caso, DTOTarjeta tarjeta){
        this.caso=caso;
        this.tarjeta=tarjeta;
    }

    public void run(){
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try{
            oos = new ObjectOutputStream(SocketManager.getSocket().getOutputStream());
            ois = new ObjectInputStream(SocketManager.getSocket().getInputStream());
            oos.writeUTF(caso);
            oos.flush();
            switch (caso){
                case "Registrarse":
                    oos.writeObject(usuario);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;

                case "IniciarSesion":
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

                case "BorrarMazo":
                    oos.writeObject(mazo);
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
                case "AnadirTarjeta":
                    oos.writeObject(tarjeta);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;
            }
        }catch (IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
        }
    }
}
