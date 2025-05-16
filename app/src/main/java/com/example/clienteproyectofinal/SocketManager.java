package com.example.clienteproyectofinal;

import java.io.IOException;
import java.net.Socket;

/**
 * Clase que gestiona el socket para realizar la conexión con el servidor
 */
public class SocketManager {

    public static Socket socket;

    public static Socket getSocket(){
        if(socket == null || socket.isClosed()){
            String ip = "10.0.2.2";
            int port = 2000;
            try {
                socket = new Socket(ip,port);
                ZonaCompartida.setIsOnline(true);
            } catch (IOException e) {
                ZonaCompartida.setIsOnline(false);
                throw new RuntimeException(e);
            }
        }
        return socket;
    }

    public static boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
