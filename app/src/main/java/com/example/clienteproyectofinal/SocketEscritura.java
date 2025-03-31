package com.example.clienteproyectofinal;

import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import modelo.DTOUsuario;

public class SocketEscritura extends Thread {

    private String caso;

    private boolean instruccionRealizada;

    private EditText nombre, password;

    public SocketEscritura(String caso){
        this.caso=caso;
    }

    public void setPassword(EditText password) {
        this.password = password;
    }

    public void setNombre(EditText nombre){
        this.nombre = nombre;
    }

    public boolean isInstruccionRealizada() {
        return instruccionRealizada;
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
                    DTOUsuario nuevoUsuario = new DTOUsuario(nombre.getText().toString(),password.getText().toString());
                    oos.writeObject(nuevoUsuario);
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
