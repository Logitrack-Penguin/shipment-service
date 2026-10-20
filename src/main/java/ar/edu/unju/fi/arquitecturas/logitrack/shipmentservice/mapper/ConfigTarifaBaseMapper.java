package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ConfigTarifaBaseResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ConfigTarifaBaseRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.ConfigTarifaBase;

/**
 * Clase utilitaria encargada de la transformación bidireccional entre el modelo de persistencia
 * {@link ConfigTarifaBase} y los objetos de transferencia de datos {@link ConfigTarifaBaseRequestDto} y {@link ConfigTarifaBaseResponseDto}.
 * <p>
 * Implementa el patrón Data Mapper para aislar el dominio relacional de las capas externas de transporte (REST/JSON),
 * evitando fugas de abstracción y problemas de recursión infinita en serializaciones bidireccionales.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see ConfigTarifaBase
 * @see ConfigTarifaBaseRequestDto
 * @see ConfigTarifaBaseResponseDto
 */
public class ConfigTarifaBaseMapper {

    /**
     * Constructor privado que previene la instanciación de una clase utilitaria.
     */
    private ConfigTarifaBaseMapper(){
        throw new UnsupportedOperationException("ConfigTarifaBaseMapper es una clase utilitaria estática y no debe ser instanciada.");
    }

    /**
     * Transforma un {@link ConfigTarifaBaseRequestDto} en una instancia persistible de {@link ConfigTarifaBase}
     * inicializada con los valores mandatorios para evitar infracciones de integridad.
     *
     * @param requestDto Contrato de entrada.
     * @return Entidad {@link ConfigTarifaBase}
     */
    public static ConfigTarifaBase toEntity (ConfigTarifaBaseRequestDto requestDto){
        if(requestDto == null) return null;

        return ConfigTarifaBase.builder()
                .tarifaBase(requestDto.getTarifaBase())
                .costoPorKg(requestDto.getCostoPorKg())
                .costoPorKm(requestDto.getCostoPorKm())
                .costoPorCmCubo(requestDto.getCostoPorCmCubo())
                .build();
    }

    /**
     * Convierte una instancia concreta de la jerarquía {@link ConfigTarifaBase} en su correspondiente DTO de respuesta.
     *
     * @param configTarifaBase Instancia de {@link ConfigTarifaBase} persistida en base de datos.
     * @return {@link ConfigTarifaBaseResponseDto}
     */
    public static ConfigTarifaBaseResponseDto toResponseDto(ConfigTarifaBase configTarifaBase){
        if(configTarifaBase == null) return null;

        return ConfigTarifaBaseResponseDto.builder()
                .tarifaBase(configTarifaBase.getTarifaBase())
                .costoPorKg(configTarifaBase.getCostoPorKg())
                .costoPorKm(configTarifaBase.getCostoPorKm())
                .costoPorCmCubo(configTarifaBase.getCostoPorCmCubo())
                .build();
    }
}
