package com.nayarasanchez.gestor_alojamientos.model;

public enum EstadoPago {
    
    PENDIENTE("PENDIENTE"),
    PAGADO("PAGADO");

    private final String valor;

    private EstadoPago(final String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}

