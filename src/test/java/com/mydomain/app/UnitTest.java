package com.mydomain.app;

import com.mydomain.app.model.Mascota;
import com.mydomain.app.model.Propietario;
import com.mydomain.app.repository.MascotaRepository;
import com.mydomain.app.service.ExternalService;
import com.mydomain.app.service.MascotaService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitTest {

    @InjectMocks
    MascotaService mascotaService;

    @Mock
    MascotaRepository mascotaRepository;

    @Mock
    ExternalService externalService;

    @AfterEach
    void afterEach(){
        clearInvocations(mascotaRepository, externalService);
        reset(mascotaRepository, externalService);
    }

    @Test
    @DisplayName("Registrar que mascota no sea Nula")
    void testRegistrarMascotaConNombreNullLanzaException(){
        Mascota mascota = new Mascota();
        mascota.setNombre(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mascotaService.registrarMascota(mascota));
        assertEquals("El nombre de la mascota no puede estar vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("Registrar que mascota no este Vacia")
    void testRegistrarMascotaConNombreVacioLanzaException(){
        Mascota mascota = new Mascota();
        mascota.setNombre("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mascotaService.registrarMascota(mascota));
        assertEquals("El nombre de la mascota no puede estar vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("El nombre de la mascota esperado debe coincidir con el actual")
    void testValidarNombreMascotaConNombreActual(){
        Mascota mascota = new Mascota();
        mascota.setNombre("Guardian");

        assertEquals("Guardian",mascota.getNombre());
    }

    @Test
    @DisplayName("Registrar que nombre de propietario no sea nulo")
    void testRegistrarPropietarioConNullLanzaException(){
        Mascota mascota = new Mascota();
        mascota.setNombre("Guardian");
        mascota.setPropietario(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mascotaService.registrarMascota(mascota));
        assertEquals("La mascota debe tener un propietario.", exception.getMessage());
    }

    @Test
    @DisplayName("Registrar que teléfono del propietario no sea nulo")
    void testRegistrarTelefonoPropietarioConNullLanzaException(){
        Mascota mascota = new Mascota();
        Propietario propietario = new Propietario("Sebastian", "Chileno", null);

        mascota.setNombre("Guardian");
        mascota.setPropietario(propietario);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> mascotaService.registrarMascota(mascota));
        assertEquals("El propietario debe tener un teléfono registrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Validar si la mascota tienen sus vacunas")
    void testValidarVacunas() {
        Mascota mascota = new Mascota();
        Propietario propietario = new Propietario("Sebastian", "Santiago", "5454545454");

        mascota.setNombre("Guardian");
        mascota.setPropietario(propietario);

        when(externalService.validarVacunas(any(Mascota.class))).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                mascotaService.registrarMascota(mascota));
        assertEquals("La mascota no tiene todas las vacunas al día.", exception.getMessage());
    }

    @Test
    @DisplayName("Validar si la mascota tienen registro en el municipio")
    void testValidarRegistroMunicipal() {
        Mascota mascota = new Mascota();
        Propietario propietario = new Propietario("Sebastian", "Santiago", "5454545454");

        mascota.setNombre("Guardian");
        mascota.setPropietario(propietario);

        when(externalService.validarVacunas(any(Mascota.class))).thenReturn(true);
        //El retorno lo cambie a false para que me pida la excepcion por no tener registro municipal
        when(externalService.verificarRegistroMunicipal(any(Mascota.class))).thenReturn(false);


        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                mascotaService.registrarMascota(mascota));
        assertEquals("La mascota no está registrada en el municipio.", exception.getMessage());
    }

    @Test
    @DisplayName("Validar que la mascota no esté registrada previamente")
    void testQueMascotaNoEsteRegistrada() {
        Mascota mascota = new Mascota();
        Propietario propietario = new Propietario("Sebastian", "Santiago", "5454545454");

        mascota.setNombre("Guardian");
        mascota.setId(1);
        mascota.setPropietario(propietario);

        when(externalService.validarVacunas(any(Mascota.class))).thenReturn(true);
        when(externalService.verificarRegistroMunicipal(any(Mascota.class))).thenReturn(true);
        when(mascotaRepository.findById(any(Integer.class))).thenReturn(Optional.of(mascota));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                mascotaService.registrarMascota(mascota));
        assertEquals("Esta mascota ya está registrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Confirmar que el nombre del propietario sea el mismo que se proporcionó")
    void testValidarNombrePropietarioConNombreActual(){
        Mascota mascota = new Mascota();
        Propietario propietario = new Propietario("Sebastian", "Santi", "9898989898");

        mascota.setNombre("Guardian");
        mascota.setPropietario(propietario);

        assertSame("Sebastian", propietario.getNombre());
    }

    @Test
    @DisplayName("Guardar la mascota satisfactoriamente")
    void testGuardarMascota() {
        Mascota mascota = new Mascota();
        Propietario propietario = new Propietario("Sebastian", "Santiago", "5454545454");

        mascota.setNombre("Guardian");
        mascota.setId(1);
        mascota.setPropietario(propietario);

        when(externalService.validarVacunas(any(Mascota.class))).thenReturn(true);
        when(externalService.verificarRegistroMunicipal(any(Mascota.class))).thenReturn(true);
        when(mascotaRepository.findById(any(Integer.class))).thenReturn(Optional.empty());
        when(mascotaRepository.guardar(any(Mascota.class))).thenReturn(mascota);

        Mascota registrada = mascotaService.registrarMascota(mascota);

        assertNotNull(registrada);
        assertEquals("Guardian", registrada.getNombre());
        assertEquals(1, registrada.getId());
        assertEquals(propietario, registrada.getPropietario());

        ArgumentCaptor<Mascota> captura = ArgumentCaptor.forClass(Mascota.class);
        verify(mascotaRepository, times(1)).guardar(captura.capture());
    }

    @Test
    @DisplayName("Eliminar mascota por ID")
    void testEliminarMascotaPorId() {
        Mascota mascota = new Mascota();
        mascota.setId(2);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mascotaService.eliminarMascotaPorId(2);
        });
        assertEquals("No se puede eliminar: No se encontró mascota con el ID proporcionado.", exception.getMessage());
    }
}
