package com.example.ticketfy.auxiliares;

public class EventoApi {
    public String nombreEvento;
    public String imagenUrl;
    public String nombreUbicacion;
    public String fecha;
    public String urlCompra;
    public boolean esNacional;

    public EventoApi(String nombreEvento, String imagenUrl, String nombreUbicacion, String fecha, String urlCompra, boolean esNacional) {
        this.nombreEvento = nombreEvento;
        this.imagenUrl = imagenUrl;
        this.nombreUbicacion = nombreUbicacion;
        this.fecha = fecha;
        this.urlCompra = urlCompra;
        this.esNacional = esNacional;
    }

    public EventoApi() {
    }
}