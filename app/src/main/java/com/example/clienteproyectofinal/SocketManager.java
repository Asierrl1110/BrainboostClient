package com.example.clienteproyectofinal;

import java.io.IOException;
import java.net.InetSocketAddress;
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
            int timeout = 2000;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), timeout);
            } catch (IOException e) {
                socket = null;
            }
        }
        return socket;
    }

}
