package com.example.ticketfy.data.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int idUsuario;

    public String nombreUsuario;
    public String email;
    public String contrasena;
    public String imagen;
}