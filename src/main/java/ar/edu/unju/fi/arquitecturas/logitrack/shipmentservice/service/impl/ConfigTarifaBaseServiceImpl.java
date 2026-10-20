package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.ConfigTarifaBaseRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.ConfigTarifaBaseResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper.ConfigTarifaBaseMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.ConfigTarifaBase;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.ConfigTarifaBaseRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.ConfigTarifaBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación transaccional del servicio de aplicación {@link ConfigTarifaBase}.
 * <p>
 * Centraliza la orquestación de operaciones de negocio para la gestión de envíos,
 * garantizando integridad transaccional ACID, inmutabilidad de componentes y auditoría vía logs.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see ConfigTarifaBaseService
 * @see ConfigTarifaBaseRepository
 * @see ConfigTarifaBaseMapper
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConfigTarifaBaseServiceImpl implements ConfigTarifaBaseService {
    private final ConfigTarifaBaseRepository configTarifaBaseRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ConfigTarifaBaseResponseDto actualizarTarifas(ConfigTarifaBaseRequestDto requestDto) {
        log.debug("Iniciando actualización de tarifas.");
        ConfigTarifaBase configTarifaBaseExistente = obtenerEntityConfiguracionVigente();

        configTarifaBaseExistente.setTarifaBase(requestDto.getTarifaBase());
        configTarifaBaseExistente.setCostoPorKg(requestDto.getCostoPorKg());
        configTarifaBaseExistente.setCostoPorKm(requestDto.getCostoPorKm());
        configTarifaBaseExistente.setCostoPorCmCubo(requestDto.getCostoPorCmCubo());

        ConfigTarifaBase configTarifaBaseActualizada = configTarifaBaseRepository.save(configTarifaBaseExistente);
        log.info("Actualización exitosa.");
        return ConfigTarifaBaseMapper.toResponseDto(configTarifaBaseActualizada);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ConfigTarifaBaseResponseDto obtenerConfiguracionVigente() {
        ConfigTarifaBase configTarifaBase = obtenerEntityConfiguracionVigente();
        return ConfigTarifaBaseMapper.toResponseDto(configTarifaBase);
    }

    protected ConfigTarifaBase obtenerEntityConfiguracionVigente(){
        log.debug("Ejecutando consulta de configuración de tarifa base vigente.");
        return configTarifaBaseRepository.findAll().getFirst();
    }
}
