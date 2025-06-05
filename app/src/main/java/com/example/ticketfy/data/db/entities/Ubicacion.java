package com.example.ticketfy.data.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Ubicacion {
    @PrimaryKey(autoGenerate = true)
    public int idUbicacion;

    public String nombre;
    public String lugar;
    public String tipo;
    public String imagen; // Si no usas imagen, puedes dejarlo como String o quitarlo
}