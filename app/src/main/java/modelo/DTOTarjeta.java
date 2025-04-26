package modelo;

import java.io.Serializable;

public class DTOTarjeta implements Serializable {

    private int id;

    private String pregunta;

    private String respuesta;

    private int idMazo;

    public DTOTarjeta(int id, String pregunta, String respuesta, int idMazo) {
        this.id = id;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.idMazo = idMazo;
    }

    public DTOTarjeta(String pregunta, String respuesta, int idMazo) {
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.idMazo = idMazo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public int getIdMazo() {
        return idMazo;
    }

    public void setIdMazo(int idMazo) {
        this.idMazo = idMazo;
    }



}
