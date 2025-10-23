package com.nayarasanchez.gestor_alojamientos.model;

public enum EstadoReserva {
    
    
    PENDIENTE ("PENDIENTE"),
    CONFIRMADA ("CONFIRMADA"),
	RECHAZADA ("RECHAZADA"),
    CANCELADA ("CANCELADA");

    private final String valor;

	private EstadoReserva(final String valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return valor;
	}

    
}
