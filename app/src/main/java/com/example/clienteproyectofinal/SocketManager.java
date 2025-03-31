package com.example.clienteproyectofinal;

import java.io.IOException;
import java.net.Socket;

public class SocketManager {

    public static Socket socket;

    public static Socket getSocket(){
        if(socket == null || socket.isClosed()){
            String ip = "10.0.2.2";
            int port = 2000;
            try {
                socket = new Socket(ip,port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return socket;
    }
}
