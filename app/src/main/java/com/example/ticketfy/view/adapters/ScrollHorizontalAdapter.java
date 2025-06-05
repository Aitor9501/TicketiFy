package com.example.ticketfy.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketfy.R;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.view.activities.EventoDetallado;
import com.example.ticketfy.view.activities.InfoArtistaFestival;

import java.util.List;

public class ScrollHorizontalAdapter extends RecyclerView.Adapter<ScrollHorizontalAdapter.ViewHolder> {

    private final List<Artista> listaArtistas;
    private final Context context;

    public ScrollHorizontalAdapter(Context context, List<Artista> listaArtistas) {
        this.context = context;
        this.listaArtistas = listaArtistas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento_horizontal, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artista artista = listaArtistas.get(position);
        holder.nombre.setText(artista.nombre);

        Glide.with(context)
                .load(artista.imagen)
                .placeholder(R.drawable.fondoconcierto)
                .into(holder.imagen);

        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(50f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(position * 100L)
                .start();

        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (artista.nombre.toLowerCase().contains("festival")) {
                intent = new Intent(context, InfoArtistaFestival.class);
            } else {
                intent = new Intent(context, EventoDetallado.class);
            }
            intent.putExtra("idArtista", artista.idArtista);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaArtistas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imgEvento);
            nombre = itemView.findViewById(R.id.txtNombreEvento);
        }
    }
}