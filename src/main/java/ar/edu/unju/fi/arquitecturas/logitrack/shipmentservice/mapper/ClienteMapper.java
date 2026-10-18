package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente;

import java.util.ArrayList;

/**
 * Clase utilitaria encargada de la transformación bidireccional entre el modelo de persistencia
 * {@link Cliente} y los objetos de transferencia de datos {@link ClienteRequestDto} y {@link ClienteResponseDto}.
 * <p>
 * Implementa el patrón Data Mapper para aislar el dominio relacional de las capas externas de transporte (REST/JSON),
 * evitando fugas de abstracción y problemas de recursión infinita en serializaciones bidireccionales.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see Cliente
 * @see ClienteRequestDto
 * @see ClienteResponseDto
 */

public class ClienteMapper {

    /**
     * Constructor privado que previene la instanciación de una clase utilitaria.
     */
    private ClienteMapper(){
        throw new UnsupportedOperationException("ClienteMapper es una clase utilitaria estática y no debe ser instanciada.");
    }

    /**
     * Transforma un {@link ClienteRequestDto} en una instancia persistible de {@link Cliente}
     * inicializada con los valores mandatorios para evitar infracciones de integridad.
     *
     * @param requestDto Contrato de entrada.
     * @return Entidad {@link Cliente}
     */
    public static Cliente toEntity(ClienteRequestDto requestDto){
        if (requestDto == null) return null;

        return Cliente.builder()
                .dni(requestDto.getDni().trim())
                .nombre(requestDto.getNombre().trim())
                .apellido(requestDto.getApellido().trim())
                .email(requestDto.getEmail().trim())
                .telefono(requestDto.getTelefono().trim())
                .envios(new ArrayList<>())
                .build();
    }

    /**
     * Convierte una instancia concreta de la jerarquía {@link Cliente} en su correspondiente DTO de respuesta.
     *
     * @param cliente Instancia de {@link Cliente} persistida en base de datos.
     * @return {@link ClienteResponseDto}
     */
    public static ClienteResponseDto toResponseDto(Cliente cliente){
        if(cliente == null) return null;

        return ClienteResponseDto.builder()
                .idCliente(cliente.getIdCliente())
                .dni(cliente.getDni())
                .nombreCompleto(String.format("%s, %s", cliente.getApellido(), cliente.getNombre()))
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fechaCreacion(cliente.getFechaCreacion())
                .build();
    }
}
