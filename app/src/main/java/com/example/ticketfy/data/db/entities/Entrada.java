package com.example.ticketfy.data.db.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity  // elimina foreignKeys
public class Entrada {

    @PrimaryKey(autoGenerate = true)
    public int idEntrada;

    public Integer idEvento;  // ahora permite null

    public double precio;
    public String nombreEvento;
    public String fecha;
    public String ubicacion;
    public String codigoEntrada;
    public String emailUsuario;

    public Entrada(Integer idEvento, double precio, String nombreEvento, String fecha, String ubicacion, String codigoEntrada, String emailUsuario) {
        this.idEvento = idEvento;
        this.precio = precio;
        this.nombreEvento = nombreEvento;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.codigoEntrada = codigoEntrada;
        this.emailUsuario = emailUsuario;
    }
}