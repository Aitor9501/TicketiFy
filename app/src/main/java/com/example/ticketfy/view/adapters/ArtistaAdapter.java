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
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.view.activities.InfoArtistaFestival;

import java.util.List;

public class ArtistaAdapter extends RecyclerView.Adapter<ArtistaAdapter.ViewHolder> {
    private final Context context;
    private final List<Artista> artistas;

    public ArtistaAdapter(Context context, List<Artista> artistas) {
        this.context = context;
        this.artistas = artistas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.evento_item, parent, false); // Puedes usar el mismo layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Artista artista = artistas.get(position);
        holder.textNombre.setText(artista.nombre);
        //holder.textUbicacion.setVisibility(View.GONE); // Ocultamos la ubicación

        if (artista.imagen != null && !artista.imagen.isEmpty()) {
            RequestOptions options = new RequestOptions().transform(new RoundedCorners(40));
            Glide.with(context)
                    .load(artista.imagen)
                    .apply(options)
                    .into(holder.imgEvento);
        } else {
            holder.imgEvento.setImageResource(R.drawable.placeholder);
        }

        // ✅ Click en la imagen (o todo el ítem) lleva a InfoArtistaFestival
        holder.imgEvento.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoArtistaFestival.class);
            intent.putExtra("idArtista", artista.idArtista); // 👈 importante
            context.startActivity(intent);
        });

        // También puedes permitir que se abra si se pulsa todo el itemView:
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoArtistaFestival.class);
            intent.putExtra("idArtista", artista.idArtista); // 👈 Para acceder a la info del artista
            context.startActivity(intent);
        });
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.imagen_menu_animacion);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return artistas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvento;
        TextView textNombre, textUbicacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEvento = itemView.findViewById(R.id.imageEvento);
            textNombre = itemView.findViewById(R.id.textNombre);
        }
    }
}
