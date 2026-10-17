package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;

/**
 * Clase utilitaria encargada de la transformación bidireccional entre el modelo de persistencia
 * {@link Envio} y los objetos de transferencia de datos {@link EnvioRequestDto} y {@link EnvioResponseDto}.
 * <p>
 * Implementa el patrón Data Mapper para aislar el dominio relacional de las capas externas de transporte (REST/JSON),
 * evitando fugas de abstracción y problemas de recursión infinita en serializaciones bidireccionales.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see Envio
 * @see EnvioRequestDto
 * @see EnvioResponseDto
 */
public class EnvioMapper {

    /**
     * Constructor privado que previene la instanciación de una clase utilitaria.
     */
    EnvioMapper(){
        throw new UnsupportedOperationException("EnvioMapper es una clase utilitaria estática y no debe ser instanciada.");
    }

    /**
     * Transforma un {@link EnvioRequestDto} en una instancia persistible de {@link Envio}
     * inicializada con los valores mandatorios para evitar infracciones de integridad.
     *
     * @param requestDto Contrato de entrada.
     * @return Entidad {@link Envio}
     */
    public static Envio toEntity(EnvioRequestDto requestDto){
        if(requestDto == null) return null;

        return Envio.builder()
                .peso(requestDto.getPeso())
                .dimensiones(requestDto.getDimensiones())
                .direccionOrigen(requestDto.getDireccionOrigen())
                .direccionDestino(requestDto.getDireccionDestino())
                .build();
    }

    /**
     * Convierte una instancia concreta de la jerarquía {@link Envio} en su correspondiente DTO de respuesta.
     *
     * @param envio Instancia de {@link Envio} persistida en base de datos.
     * @return {@link EnvioResponseDto}
     */
    public static EnvioResponseDto toResponseDto(Envio envio){
        if(envio == null) return null;

        return EnvioResponseDto.builder()
                .idEnvio(envio.getIdEnvio())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .peso(envio.getPeso())
                .dimensiones(envio.getDimensiones())
                .tarifa(envio.getTarifa())
                .remitente(String.format(
                        "Nombre: %s, %s - DNI: %s",
                        envio.getCliente().getApellido(),
                        envio.getCliente().getNombre(),
                        envio.getCliente().getDni()
                ))
                .repartidorAsignado(String.format(
                        "Nombre: %s, %s - DNI: %s",
                        envio.getRepartidor().getApellido(),
                        envio.getRepartidor().getNombre(),
                        envio.getRepartidor().getDni()
                ))
                .estadoEnvio(envio.getEstadoEnvio())
                .fechaEstimadaEntrega(envio.getFechaEstimadaEntrega())
                .codigoRecepcion(envio.getCodigoRecepcion())
                .direccionOrigen(envio.getDireccionOrigen())
                .direccionDestino(envio.getDireccionDestino())
                .fechaCreacion(envio.getFechaCreacion())
                .build();
    }
}
