package com.example.clienteproyectofinal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import modelo.DTOMazo;
import modelo.DTOTarjeta;
import modelo.DTOUsuario;

public class SocketConnection extends Thread {

    private String caso;

    private int idUsuario;

    private boolean instruccionRealizada;

    private DTOUsuario usuario;

    private DTOMazo mazo, mazoAntiguo;

    private DTOTarjeta tarjeta;

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public SocketConnection(String caso, DTOMazo mazo, DTOMazo mazoAntiguo){
        this.caso=caso;
        this.mazo=mazo;
        this.mazoAntiguo=mazoAntiguo;
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
                    oos.writeObject(mazo);
                    oos.flush();
                    instruccionRealizada = ois.readBoolean();
                    SocketManager.getSocket().close();
                    break;

                case "ModificarMazo":
                    oos.writeObject(mazo);
                    oos.flush();
                    oos.writeObject(mazoAntiguo);
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
                case "TarjetasPorUsuario":
                    oos.writeInt(idUsuario);
                    oos.flush();
                    List<DTOTarjeta> tarjetas = (List<DTOTarjeta>) ois.readObject();
                    ZonaCompartida.setTarjetas(tarjetas);
                    SocketManager.getSocket().close();
                    break;
            }
        }catch (IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
        }
    }
}
