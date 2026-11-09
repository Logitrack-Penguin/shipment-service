package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.HistoricoEstadoEnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.HistoricoEstadoEnvioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link HistoricoEnvioServiceImpl}.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 09-11-2026
 */
@ExtendWith(MockitoExtension.class)
class HistoricoEnvioServiceImplTest {

    private static final String CODIGO_SEGUIMIENTO = "TRK-12345678";

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private HistoricoEstadoEnvioRepository historicoEstadoEnvioRepository;

    @InjectMocks
    private HistoricoEnvioServiceImpl historicoEnvioService;

    @Test
    @DisplayName("Devuelve el historial del envío mapeado y en el orden del repositorio")
    void obtenerHistorial_Exitoso() {
        UUID idEnvio = UUID.randomUUID();
        Envio envio = Envio.builder().idEnvio(idEnvio).codigoSeguimiento(CODIGO_SEGUIMIENTO).build();
        LocalDateTime t1 = LocalDateTime.of(2026, 11, 9, 9, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 11, 9, 11, 0);
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.of(envio));
        when(historicoEstadoEnvioRepository.findByCodigoSeguimientoOrderByFechaCambioEstadoAsc(CODIGO_SEGUIMIENTO))
                .thenReturn(List.of(
                        HistoricoEstadoEnvio.builder().codigoSeguimiento(CODIGO_SEGUIMIENTO)
                                .estadoEnvioAnterior(EstadoEnvio.REPARTIDOR_ASIGNADO)
                                .estadoEnvioNuevo(EstadoEnvio.EN_RUTA).fechaCambioEstado(t1).build(),
                        HistoricoEstadoEnvio.builder().codigoSeguimiento(CODIGO_SEGUIMIENTO)
                                .estadoEnvioAnterior(EstadoEnvio.EN_RUTA)
                                .estadoEnvioNuevo(EstadoEnvio.ENTREGADO).fechaCambioEstado(t2).build()));

        List<HistoricoEstadoEnvioResponseDto> historial = historicoEnvioService.obtenerHistorial(idEnvio);

        assertEquals(2, historial.size());
        assertEquals(EstadoEnvio.REPARTIDOR_ASIGNADO, historial.get(0).getEstadoEnvioAnterior());
        assertEquals(EstadoEnvio.EN_RUTA, historial.get(0).getEstadoEnvioNuevo());
        assertEquals(t1, historial.get(0).getFechaCambioEstado());
        assertEquals(EstadoEnvio.ENTREGADO, historial.get(1).getEstadoEnvioNuevo());
    }

    @Test
    @DisplayName("Lanza RecursoNoEncontradoException si el envío no existe")
    void obtenerHistorial_LanzaExcepcion_SiNoExiste() {
        UUID idEnvio = UUID.randomUUID();
        when(envioRepository.findById(idEnvio)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> historicoEnvioService.obtenerHistorial(idEnvio));
        verify(historicoEstadoEnvioRepository, never()).findByCodigoSeguimientoOrderByFechaCambioEstadoAsc(anyString());
    }
}
