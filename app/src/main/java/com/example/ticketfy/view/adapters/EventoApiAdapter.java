package com.example.ticketfy.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.ticketfy.auxiliares.EventoApi;
import com.example.ticketfy.view.activities.DetalleConcierto;

import java.util.List;

public class EventoApiAdapter extends RecyclerView.Adapter<EventoApiAdapter.ViewHolder> {

    private final Context context;
    private final List<EventoApi> eventosApi;

    public EventoApiAdapter(Context context, List<EventoApi> eventosApi) {
        this.context = context;
        this.eventosApi = eventosApi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.evento_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventoApi evento = eventosApi.get(position);
        if (evento == null) return;

        // Mostrar nombre del evento
        holder.textNombre.setText(evento.nombreEvento);
        holder.textUbicacion.setText(evento.nombreUbicacion);
        holder.textFecha.setText(evento.fecha);

        // Cargar imagen con Glide
        RequestOptions options = new RequestOptions()
                .transform(new RoundedCorners(30))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder);

        Glide.with(context)
                .load(evento.imagenUrl)
                .apply(options)
                .into(holder.imageEvento);

        // Acción al pulsar en el item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleConcierto.class);
            intent.putExtra("imagenEvento", evento.imagenUrl);
            intent.putExtra("nombreArtista", evento.nombreEvento);
            intent.putExtra("ubicacion", evento.nombreUbicacion);
            intent.putExtra("fecha", evento.fecha);

            String urlCompra = evento.urlCompra != null ? evento.urlCompra : "";
            intent.putExtra("urlCompra", urlCompra);
            context.startActivity(intent);
            Log.d("URL_EVENTO", "URL de compra: " + urlCompra);
        });

        // Animación
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.imagen_menu_animacion);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return eventosApi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEvento;
        TextView textNombre, textUbicacion, textFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEvento = itemView.findViewById(R.id.imageEvento);
            textNombre = itemView.findViewById(R.id.textNombre);
            textUbicacion = itemView.findViewById(R.id.textUbicacion);
            textFecha = itemView.findViewById(R.id.textFecha);
        }
    }
}
