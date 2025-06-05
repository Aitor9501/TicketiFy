package com.example.ticketfy.data.db.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"usuarioId", "artistaId"},
foreignKeys = {
@ForeignKey(entity = Usuario.class,
        parentColumns = "idUsuario",
        childColumns = "usuarioId",
        onDelete = ForeignKey.CASCADE),
@ForeignKey(entity = Artista.class,
        parentColumns = "idArtista",
        childColumns = "artistaId",
        onDelete = ForeignKey.CASCADE)
    }
            )
public class Favorito {
    public int usuarioId;
    public int artistaId;

    public Favorito() {
    }

    public Favorito(int usuarioId, int artistaId) {
        this.usuarioId = usuarioId;
        this.artistaId = artistaId;
    }
}


