package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de {@link LiberacionEnviosServiceImpl} (LT4-34).
 *
 * @author Elías Maciel Villalva
 * @version 1.0.0
 * @since 10-11-2026
 */
@ExtendWith(MockitoExtension.class)
class LiberacionEnviosServiceImplTest {

    private static final String DNI = "28111222";

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;

    @InjectMocks
    private LiberacionEnviosServiceImpl liberacionEnviosService;

    private static Envio envio(String codigo, EstadoEnvio estado) {
        Envio envio = new Envio();
        envio.setCodigoSeguimiento(codigo);
        envio.setEstadoEnvio(estado);
        envio.setDniRepartidor(DNI);
        return envio;
    }

    @Test
    @DisplayName("Libera los envíos REPARTIDOR_ASIGNADO: vuelven a PENDIENTE_ASIGNACION sin repartidor")
    void liberar_EnviosAsignados_VuelvenAPendiente() {
        Envio e1 = envio("TRK-1", EstadoEnvio.REPARTIDOR_ASIGNADO);
        Envio e2 = envio("TRK-2", EstadoEnvio.REPARTIDOR_ASIGNADO);
        when(envioRepository.findByDniRepartidorAndEstadoEnvio(DNI, EstadoEnvio.REPARTIDOR_ASIGNADO))
                .thenReturn(List.of(e1, e2));

        int liberados = liberacionEnviosService.liberarEnviosDeRepartidor(DNI);

        assertEquals(2, liberados);
        for (Envio e : List.of(e1, e2)) {
            assertEquals(EstadoEnvio.PENDIENTE_ASIGNACION, e.getEstadoEnvio());
            assertNull(e.getDniRepartidor());
            verify(envioRepository).save(e);
        }
    }

    @Test
    @DisplayName("Registra en el historial cada envío liberado")
    void liberar_RegistraHistorial() {
        when(envioRepository.findByDniRepartidorAndEstadoEnvio(DNI, EstadoEnvio.REPARTIDOR_ASIGNADO))
                .thenReturn(List.of(envio("TRK-1", EstadoEnvio.REPARTIDOR_ASIGNADO)));

        liberacionEnviosService.liberarEnviosDeRepartidor(DNI);

        ArgumentCaptor<HistoricoEstadoEnvio> captor = ArgumentCaptor.forClass(HistoricoEstadoEnvio.class);
        verify(historicoEstadoEnvioRepository).save(captor.capture());
        HistoricoEstadoEnvio historico = captor.getValue();
        assertEquals("TRK-1", historico.getCodigoSeguimiento());
        assertEquals(EstadoEnvio.REPARTIDOR_ASIGNADO, historico.getEstadoEnvioAnterior());
        assertEquals(EstadoEnvio.PENDIENTE_ASIGNACION, historico.getEstadoEnvioNuevo());
        assertNotNull(historico.getFechaCambioEstado());
    }

    @Test
    @DisplayName("No toca los envíos EN_RUTA: solo busca los REPARTIDOR_ASIGNADO")
    void liberar_NoTocaEnRuta() {
        when(envioRepository.findByDniRepartidorAndEstadoEnvio(DNI, EstadoEnvio.REPARTIDOR_ASIGNADO))
                .thenReturn(List.of());
        when(envioRepository.countByDniRepartidorAndEstadoEnvio(DNI, EstadoEnvio.EN_RUTA)).thenReturn(1L);

        int liberados = liberacionEnviosService.liberarEnviosDeRepartidor(DNI);

        assertEquals(0, liberados);
        verify(envioRepository, never()).findByDniRepartidorAndEstadoEnvio(DNI, EstadoEnvio.EN_RUTA);
        verify(envioRepository, never()).save(any(Envio.class));
        verifyNoInteractions(historicoEstadoEnvioRepository);
    }

    @Test
    @DisplayName("Es idempotente: procesar el mismo evento dos veces no duplica el historial")
    void liberar_DosVeces_MismoResultado() {
        Envio e1 = envio("TRK-1", EstadoEnvio.REPARTIDOR_ASIGNADO);
        // La primera vez el envío está asignado; la segunda ya no aparece en la consulta.
        when(envioRepository.findByDniRepartidorAndEstadoEnvio(DNI, EstadoEnvio.REPARTIDOR_ASIGNADO))
                .thenReturn(List.of(e1))
                .thenReturn(List.of());

        assertEquals(1, liberacionEnviosService.liberarEnviosDeRepartidor(DNI));
        assertEquals(0, liberacionEnviosService.liberarEnviosDeRepartidor(DNI));

        verify(envioRepository, times(1)).save(any(Envio.class));
        verify(historicoEstadoEnvioRepository, times(1)).save(any(HistoricoEstadoEnvio.class));
    }
}
