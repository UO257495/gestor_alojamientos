package com.nayarasanchez.gestor_alojamientos.dto.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensajeUsuario {

    public enum Tipo {
        OK,
        INFO,
        AVISO,
        ERROR
    }

    private Tipo tipo;
    private String texto;

    public static MensajeUsuario mensajeCorrecto(String texto) {
        return new MensajeUsuario(MensajeUsuario.Tipo.OK, texto);
    }

    public static MensajeUsuario mensajeInfo(String texto) {
        return new MensajeUsuario(MensajeUsuario.Tipo.INFO, texto);
    }

    public static MensajeUsuario mensajeAviso(String texto) {
        return new MensajeUsuario(MensajeUsuario.Tipo.AVISO, texto);
    }

    public static MensajeUsuario mensajeError(String texto) {
        return new MensajeUsuario(MensajeUsuario.Tipo.ERROR, texto);
    }
    
}