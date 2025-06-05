package com.example.ticketfy.data.db;

import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ticketfy.data.db.dao.ArtistaDao;
import com.example.ticketfy.data.db.dao.EntradaDao;
import com.example.ticketfy.data.db.dao.EventoDao;
import com.example.ticketfy.data.db.dao.FavoritosDao;
import com.example.ticketfy.data.db.dao.UbicacionDao;
//import com.example.ticketfy.data.db.dao.UsuarioDao;
//import com.example.ticketfy.data.db.dao.EntradaDao;

import com.example.ticketfy.data.db.dao.UsuarioDao;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.data.db.entities.Favorito;
import com.example.ticketfy.data.db.entities.Ubicacion;
import com.example.ticketfy.data.db.entities.Usuario;
import com.example.ticketfy.data.db.entities.Entrada;

@Database(entities = {
        Artista.class,
        Evento.class,
        Ubicacion.class,
        Usuario.class,
        Entrada.class,
        Favorito.class
}, version = 9 )
public abstract class AppDatabase extends RoomDatabase {
    public abstract ArtistaDao artistaDao();
    public abstract EventoDao eventoDao();
    public abstract UbicacionDao ubicacionDao();
    public abstract UsuarioDao usuarioDao();
    public abstract FavoritosDao favoritoDao();
    public abstract EntradaDao entradaDao();

}