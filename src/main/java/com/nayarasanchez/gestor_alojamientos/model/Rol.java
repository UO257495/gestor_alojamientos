package com.nayarasanchez.gestor_alojamientos.model;

public enum Rol {
    
    ADMIN("ADMIN"),
    PROPIETARIO("PROPIETARIO"),
    CLIENTE("CLIENTE");

    private final String valor;

	private Rol(final String valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return valor;
	}

}
