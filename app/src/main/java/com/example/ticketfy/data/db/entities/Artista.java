package com.example.ticketfy.data.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Artista {
    @PrimaryKey(autoGenerate = true)
    public int idArtista;

    public String nombre;
    public String imagen;
    public String infoArtista;
}