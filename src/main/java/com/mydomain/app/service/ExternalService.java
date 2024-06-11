package com.mydomain.app.service;

import com.mydomain.app.model.Mascota;

public interface ExternalService {

    boolean validarVacunas(Mascota mascota);
    boolean verificarRegistroMunicipal(Mascota mascota);
}
