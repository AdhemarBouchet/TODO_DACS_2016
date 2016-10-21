package edu.frcu.dacs.todo.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by lkloster on 8/26/2016.
 */
public class ToDoEntry extends SugarRecord implements Serializable {
    @SerializedName("Titulo")
    String titulo;

    @SerializedName("Descripcion")
    String descripcion;

    public ToDoEntry(){
    }

    public ToDoEntry(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {

        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
