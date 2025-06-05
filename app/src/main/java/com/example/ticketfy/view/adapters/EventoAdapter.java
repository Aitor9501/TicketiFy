package com.example.ticketfy.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.data.db.entities.Ubicacion;
import com.example.ticketfy.view.activities.DetalleConcierto;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<Evento> eventos;
    private final AppDatabase db;
    private final Context context;

    public EventoAdapter(Context context, List<Evento> eventos, AppDatabase db) {
        this.context = context;
        this.eventos = eventos;
        this.db = db;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.evento_item, parent, false);
        return new EventoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {

        Evento evento = eventos.get(position);

        Artista artista = db.artistaDao().obtenerPorId(evento.idArtista);
        String nombreArtista;
        String imagenArtista;

        if (artista != null) {
            nombreArtista = artista.nombre;
            if (artista.imagen != null && !artista.imagen.isEmpty()) {
                imagenArtista = artista.imagen;
                RequestOptions options = new RequestOptions().transform(new RoundedCorners(40));
                Glide.with(context)
                        .load(artista.imagen)
                        .apply(options)
                        .into(holder.imgEvento);
            } else {
                imagenArtista = "";
                holder.imgEvento.setImageResource(R.drawable.placeholder);
            }
        } else {
            nombreArtista = "Artista desconocido";
            imagenArtista = "";
            holder.imgEvento.setImageResource(R.drawable.placeholder);
        }

        holder.textNombre.setText(nombreArtista);

        Ubicacion ubicacion = db.ubicacionDao().obtenerPorId(evento.idUbicacion);
        String nombreUbicacion = (ubicacion != null && ubicacion.nombre != null) ? ubicacion.nombre : "Ubicación desconocida";
        holder.textUbicacion.setText(nombreUbicacion);

        // Ir directamente a DetalleConcierto con imagen, nombre, ubicación
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleConcierto.class);
            intent.putExtra("imagenEvento", imagenArtista);
            intent.putExtra("nombreArtista", nombreArtista);
            intent.putExtra("ubicacion", nombreUbicacion);
            context.startActivity(intent);
        });
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.imagen_menu_animacion);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvento;
        TextView textNombre, textUbicacion;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEvento = itemView.findViewById(R.id.imageEvento);
            textNombre = itemView.findViewById(R.id.textNombre);
        }
    }

    public void setEventos(List<Evento> nuevosEventos) {
        this.eventos.clear();
        this.eventos.addAll(nuevosEventos);
        notifyDataSetChanged();
    }
}
