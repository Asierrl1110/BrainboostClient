package com.example.clienteproyectofinal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import modelo.DTOMazo;
import modelo.DTOTarjeta;
import modelo.DTOUsuario;

/**
 * Clase que controla las conexiones de la aplicación con el servidor
 */
public class SocketConnection extends Thread {

    private Context context;

    // Variable que almacena que instrucciones quiere realizar el cliente en la conexión con el servidor
    private final String caso;

    // Variable que almacena el id del usuario registrado
    private int idUsuario;

    // Variable que almacena el id del mazo del nuevo mazo importado
    private int idMazo;

    // Variable booleana que almacena si la instrucción se ha podido realizar correctamente
    private boolean instruccionRealizada;

    // Variable que almacena los datos de un usuario
    private DTOUsuario usuario;

    // Variables que almacenan datos de mazos
    private DTOMazo mazo, mazoAntiguo;

    // Variables que almacenan datos de tarjetas
    private DTOTarjeta tarjeta, tarjetaAntigua;

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

    public SocketConnection(String caso, int idUsuario, Context context){
        this.caso=caso;
        this.idUsuario=idUsuario;
        this.context=context;
    }

    public SocketConnection(String caso, DTOUsuario usuario, Context context){
        this.caso=caso;
        this.usuario=usuario;
        this.context=context;
    }

    public SocketConnection(String caso, DTOMazo mazo, Context context){
        this.caso=caso;
        this.mazo=mazo;
        this.context=context;
    }

    public SocketConnection(String caso, DTOMazo mazo, DTOMazo mazoAntiguo, Context context){
        this.caso=caso;
        this.mazo=mazo;
        this.mazoAntiguo=mazoAntiguo;
        this.context=context;
    }

    public SocketConnection(String caso, DTOTarjeta tarjeta, DTOTarjeta tarjetaAntigua, Context context) {
        this.caso = caso;
        this.tarjeta = tarjeta;
        this.tarjetaAntigua = tarjetaAntigua;
        this.context=context;
    }

    public SocketConnection(String caso, DTOTarjeta tarjeta, Context context){
        this.caso=caso;
        this.tarjeta=tarjeta;
        this.context=context;
    }

    public void run(){
        if(hayWifi()){
            // Objetos pra enviar y recibir información con el servidor
            Socket socket = null;
            ObjectOutputStream oos;
            ObjectInputStream ois;
            try{
                socket = SocketManager.getSocket();
                // Comprobamos si hemos obtenido conexión con el servidor o no
                // En caso de que no haya conexión, el socket es nulo
                if(socket != null) {
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    ois = new ObjectInputStream(socket.getInputStream());
                    // Escribimos el caso/instrucción de lo que queremos hacer en la conexión con el servidor
                    oos.writeUTF(caso);
                    oos.flush();
                    // En función de la instrucción realizaremos una serie de instrucciones o no
                    switch (caso){
                        case "Registrarse":
                            oos.writeObject(usuario);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;

                        case "IniciarSesion":
                            oos.writeObject(usuario);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            if(instruccionRealizada){
                                usuario = (DTOUsuario) ois.readObject();
                                ZonaCompartida.setUsuarioRegistrado(usuario);
                            }
                            break;

                        case "BorrarUsuario":
                            oos.writeObject(usuario);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;
                        case "CambiarClave":
                            oos.writeObject(usuario);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;

                        case "AnadirMazo":
                            oos.writeObject(mazo);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;

                        case "ModificarMazo":
                            oos.writeObject(mazo);
                            oos.flush();
                            oos.writeObject(mazoAntiguo);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;

                        case "BorrarMazo":
                            oos.writeObject(mazo);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;

                        case "Mazos":
                            oos.writeInt(idUsuario);
                            oos.flush();
                            List<DTOMazo> mazos = (List<DTOMazo>) ois.readObject();
                            ZonaCompartida.setMazos(mazos);
                            break;
                        case "IdMazo":
                            idMazo = ois.readInt();
                            break;

                        case "AnadirTarjeta":
                            oos.writeObject(tarjeta);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;
                        case "ModificarTarjeta":
                            oos.writeObject(tarjeta);
                            oos.flush();
                            oos.writeObject(tarjetaAntigua);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;
                        case "BorrarTarjeta":
                            oos.writeObject(tarjeta);
                            oos.flush();
                            instruccionRealizada = ois.readBoolean();
                            break;
                        case "TarjetasPorUsuario":
                            oos.writeInt(idUsuario);
                            oos.flush();
                            List<DTOTarjeta> tarjetas = (List<DTOTarjeta>) ois.readObject();
                            ZonaCompartida.setTarjetas(tarjetas);
                            break;
                    }
                }else{
                    funcionalidadesOffline();
                }
            }catch (IOException ioException){
                ioException.printStackTrace();
            }catch (ClassNotFoundException classNotFoundException){
                classNotFoundException.printStackTrace();
            }finally {
                try{
                    if(socket != null){
                        SocketManager.getSocket().close();
                    }
                }catch (IOException exception){
                }
            }
        }else{
            funcionalidadesOffline();
        }
    }

    // Método que contiene las funcionalidades del modo offline
    private void funcionalidadesOffline() {
        // Hay algunas funcionalidades que aun sin conexión se pueden realizar, como iniciar sesion
        // y recuperar los mazos y tarjetas de la base de datos local
        switch (caso){
            case "IniciarSesion":
                DAOUsuario daoUsuario = new DAOUsuario(context);
                instruccionRealizada = daoUsuario.signup(usuario);
                break;
            case "Mazos":
                DAOMazo daoMazo = new DAOMazo(context);
                List<DTOMazo> listaMazos = daoMazo.getMazos(ZonaCompartida.getUsuarioRegistrado().getId());
                ZonaCompartida.setMazos(listaMazos);
                break;
            case "TarjetasPorUsuario":
                DAOTarjeta daoTarjeta = new DAOTarjeta(context);
                List<DTOTarjeta> listaTarjetas = daoTarjeta.getTarjetas(ZonaCompartida.getUsuarioRegistrado().getId());
                ZonaCompartida.setTarjetas(listaTarjetas);
                break;
        }
    }

    /**
     * Método que comprueba si el dispositivo esta conectado a una red wifi o no
     * @return booleano de si el usuario esta conectado a wifi o no
     */
    private boolean hayWifi(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);

        if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
            return true;
        }else{
            return false;
        }
    }
}