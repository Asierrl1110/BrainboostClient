package com.example.clienteproyectofinal;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import modelo.DTOMazo;
import modelo.DTOTarjeta;
import modelo.DTOUsuario;

/**
 * Clase que controla las conexiones de la aplicación con el servidor
 */
public class SocketConnection extends Thread {

    private Context context = null;

    // Vairiable que almaena que instrucciones quiere realizar el cliente en la conexión con el servidor
    private final String caso;

    private int idUsuario;

    private int idMazo;

    private boolean instruccionRealizada;

    private DTOUsuario usuario;

    private DTOMazo mazo, mazoAntiguo;

    private DTOTarjeta tarjeta, tarjetaAntigua;

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdMazo(int idMazo){
        this.idMazo = idMazo;
    }

    public int getIdMazo(){
        return idMazo;
    }

    public boolean isInstruccionRealizada() {
        return instruccionRealizada;
    }

    public SocketConnection(String caso, Context context){
        this.caso=caso;
        this.context=context;
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

    public SocketConnection(String caso, DTOTarjeta tarjeta, DTOTarjeta tarjetaAntigua) {
        this.caso = caso;
        this.tarjeta = tarjeta;
        this.tarjetaAntigua = tarjetaAntigua;
    }

    public SocketConnection(String caso, DTOTarjeta tarjeta){
        this.caso=caso;
        this.tarjeta=tarjeta;
    }

    public void run(){
        if(!caso.equals("Conexion")){
            // Objetos pra enviar y recibir información con el servidor
            ObjectOutputStream oos;
            ObjectInputStream ois;
            try{
                oos = new ObjectOutputStream(SocketManager.getSocket().getOutputStream());
                ois = new ObjectInputStream(SocketManager.getSocket().getInputStream());
                // Escribimos el caso/instrucción de lo que queremos hacer en la conexión con el servidor
                oos.writeUTF(caso);
                oos.flush();
                // En función de la instrucción realizaremos una serie de instrucciones o no
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
                            usuario = (DTOUsuario) ois.readObject();
                            ZonaCompartida.setUsuarioRegistrado(usuario);
                        }
                        SocketManager.getSocket().close();
                        break;

                    case "BorrarUsuario":
                        oos.writeObject(usuario);
                        oos.flush();
                        instruccionRealizada = ois.readBoolean();
                        SocketManager.getSocket().close();
                        break;
                    case "CambiarClave":
                        oos.writeObject(usuario);
                        oos.flush();
                        instruccionRealizada = ois.readBoolean();
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
                    case "IdMazo":
                        // oos.writeObject(mazo);
                        // oos.flush();
                        idMazo = ois.readInt();
                        SocketManager.getSocket().close();
                        break;

                    case "AnadirTarjeta":
                        oos.writeObject(tarjeta);
                        oos.flush();
                        instruccionRealizada = ois.readBoolean();
                        SocketManager.getSocket().close();
                        break;
                    case "ModificarTarjeta":
                        oos.writeObject(tarjeta);
                        oos.flush();
                        oos.writeObject(tarjetaAntigua);
                        oos.flush();
                        instruccionRealizada = ois.readBoolean();
                        SocketManager.getSocket().close();
                        break;
                    case "BorrarTarjeta":
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
            }catch (IOException ioException){
                ioException.printStackTrace();
            }catch (ClassNotFoundException classNotFoundException){
                classNotFoundException.printStackTrace();
            }
        }else{
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("192.168.1.90", 2000), 3000);
                System.out.println("Servidor disponible.");
                ZonaCompartida.setIsOnline(true);
            } catch (IOException e) {
                System.out.println("No se pudo conectar.");
                ZonaCompartida.setIsOnline(false);
            }

        }
    }
}
