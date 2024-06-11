package com.mydomain.app.service;

import com.mydomain.app.model.Mascota;

public class ExternalServiceImpl implements ExternalService{

    @Override
    public boolean validarVacunas(Mascota mascota) {
        return !mascota.getNombre().contains("sin vacuna");
    }

    @Override
    public boolean verificarRegistroMunicipal(Mascota mascota) {
        return !mascota.getNombre().contains("no registrado");
    }
}
