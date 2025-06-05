package com.example.ticketfy.view.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.room.Room;

import com.example.ticketfy.R;
import com.example.ticketfy.data.db.AppDatabase;
import com.example.ticketfy.data.db.entities.Artista;
import com.example.ticketfy.data.db.entities.Evento;
import com.example.ticketfy.data.db.entities.Ubicacion;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

public class EntradaEvento extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrada_evento);
        configurarCabecera();

        TextView nombre = findViewById(R.id.textViewNombreEvento);
        TextView fecha = findViewById(R.id.textViewFecha);
        TextView lugar = findViewById(R.id.textViewUbicacion);
        TextView codigo = findViewById(R.id.textViewCodigo);
        ImageView qr = findViewById(R.id.imageViewQR);
        Button volver = findViewById(R.id.btnVolver);

        int idEvento = getIntent().getIntExtra("idEvento", -1);
        String nombreEvento = getIntent().getStringExtra("nombreEvento");
        String fechaEvento = getIntent().getStringExtra("fecha");
        String ubicacionEvento = getIntent().getStringExtra("ubicacion");
        String codigoEntrada = getIntent().getStringExtra("codigoEntrada");

        if (nombreEvento == null || nombreEvento.isEmpty()) nombreEvento = "Evento desconocido";
        if (fechaEvento == null || fechaEvento.isEmpty()) fechaEvento = "Fecha desconocida";
        if (ubicacionEvento == null || ubicacionEvento.isEmpty()) ubicacionEvento = "Ubicación desconocida";
        if (codigoEntrada == null || codigoEntrada.isEmpty()) codigoEntrada = "#000000";

        if (idEvento != -1) {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "ticketify-db")
                    .allowMainThreadQueries()
                    .build();

            Evento evento = db.eventoDao().obtenerPorId(idEvento);
            if (evento != null) {
                if (fechaEvento.equals("Fecha desconocida") && evento.fecha != null)
                    fechaEvento = evento.fecha;

                if (nombreEvento.equals("Evento desconocido")) {
                    Artista artista = db.artistaDao().obtenerPorId(evento.idArtista);
                    if (artista != null && artista.nombre != null)
                        nombreEvento = artista.nombre;
                }

                if (ubicacionEvento.equals("Ubicación desconocida")) {
                    Ubicacion ubicacion = db.ubicacionDao().obtenerPorId(evento.idUbicacion);
                    if (ubicacion != null && ubicacion.nombre != null)
                        ubicacionEvento = ubicacion.nombre;
                }
            }
        }

        nombre.setText(nombreEvento);
        fecha.setText("Fecha: " + fechaEvento);
        lugar.setText("Lugar: " + ubicacionEvento);
        codigo.setText("Código: " + codigoEntrada);

        // Generar el código QR
        String contenidoQR = "Entrada|" + nombreEvento + "|" + ubicacionEvento + "|" + codigoEntrada;
        Bitmap qrBitmap = generarCodigoQR(contenidoQR, 400, 400);
        if (qrBitmap != null) {
            qr.setImageBitmap(qrBitmap);
        }

        volver.setOnClickListener(v -> finish());
    }

    private Bitmap generarCodigoQR(String contenido, int ancho, int alto) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, ancho, alto);
            Bitmap bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.RGB_565);

            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
