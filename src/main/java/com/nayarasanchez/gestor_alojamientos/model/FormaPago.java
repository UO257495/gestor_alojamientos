package com.nayarasanchez.gestor_alojamientos.model;

public enum FormaPago {
    
    ALOJAMIENTO("ALOJAMIENTO"),
    TRANSFERENCIA("TRANSFERENCIA");

    private final String valor;

    private FormaPago(final String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return valor;
    }
}