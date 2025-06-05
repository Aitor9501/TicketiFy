package com.example.ticketfy.data.db.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Artista.class,
                parentColumns = "idArtista",
                childColumns = "idArtista",
                onDelete = CASCADE),
        @ForeignKey(entity = Ubicacion.class,
                parentColumns = "idUbicacion",
                childColumns = "idUbicacion",
                onDelete = CASCADE)
})
public class Evento {
    @PrimaryKey(autoGenerate = true)
    public int idEvento;

    public int idArtista;    // FK a Artista
    public int idUbicacion;  // FK a Ubicacion

    public String categoria;
    public String tipoEvento;
    public boolean esNacional;
    public String fecha;

}