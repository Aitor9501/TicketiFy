package com.example.ticketfy.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.entities.Entrada;

import java.util.List;

public class EntradaAdapter extends RecyclerView.Adapter<EntradaAdapter.EntradaViewHolder> {

    public interface OnEntradaClickListener {
        void onEntradaClick(Entrada entrada);
    }

    private final List<Entrada> listaEntradas;
    private final OnEntradaClickListener listener;

    public EntradaAdapter(List<Entrada> listaEntradas, OnEntradaClickListener listener) {
        this.listaEntradas = listaEntradas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntradaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrada_item, parent, false);
        return new EntradaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EntradaViewHolder holder, int position) {
        Entrada entrada = listaEntradas.get(position);
        holder.bind(entrada, listener);
    }

    @Override
    public int getItemCount() {
        return listaEntradas.size();
    }

    static class EntradaViewHolder extends RecyclerView.ViewHolder {

        TextView nombreEvento;
        TextView fechaEvento;
        TextView ubicacionEvento;
        TextView codigoEntrada;

        public EntradaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreEvento = itemView.findViewById(R.id.textNombreEvento);
            fechaEvento = itemView.findViewById(R.id.textFecha);
            ubicacionEvento = itemView.findViewById(R.id.textUbicacion);
            codigoEntrada = itemView.findViewById(R.id.textCodigo);
        }

        public void bind(Entrada entrada, OnEntradaClickListener listener) {
            nombreEvento.setText(entrada.nombreEvento);
            fechaEvento.setText("Fecha: " + entrada.fecha);

            if (entrada.ubicacion != null && !entrada.ubicacion.isEmpty()) {
                ubicacionEvento.setText("Ubicación: " + entrada.ubicacion);
            } else {
                ubicacionEvento.setText("Ubicación: desconocida");
            }

            if (entrada.codigoEntrada != null && !entrada.codigoEntrada.isEmpty()) {
                codigoEntrada.setText("Código: #" + entrada.codigoEntrada);
            } else {
                codigoEntrada.setText("Código: #000000");
            }

            itemView.setOnClickListener(v -> listener.onEntradaClick(entrada));
        }
    }
}
