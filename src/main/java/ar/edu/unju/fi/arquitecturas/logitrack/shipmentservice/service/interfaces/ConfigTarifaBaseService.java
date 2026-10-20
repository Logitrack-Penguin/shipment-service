package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ConfigTarifaBaseRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ConfigTarifaBaseResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.ConfigTarifaBase;

/**
 * Contrato de operaciones de negocio para la gestión integral de la entidad {@link ConfigTarifaBase} destinada a guardar
 * la configuración global de las tarifas base.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see ConfigTarifaBase
 * @see ConfigTarifaBaseRequestDto
 * @see ConfigTarifaBaseResponseDto
 */
public interface ConfigTarifaBaseService {

    /**
     * Actualiza las tarifas mediante un request data transfer object.
     *
     * @param requestDto Nueva configuración ingresada.
     * @return {@link ConfigTarifaBaseResponseDto} Con la nueva configuración actualizada.
     */
    ConfigTarifaBaseResponseDto actualizarTarifas(ConfigTarifaBaseRequestDto requestDto);

    /**
     * Retorna la configuración de tarifa base vigente en el momento de registrar el envío.
     *
     * @return {@link ConfigTarifaBaseResponseDto} Con la configuración actual.
     */
    ConfigTarifaBaseResponseDto obtenerConfiguracionVigente();
}
