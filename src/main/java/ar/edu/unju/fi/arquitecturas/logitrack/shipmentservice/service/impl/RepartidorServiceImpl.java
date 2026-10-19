package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.impl;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoDuplicadoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.exception.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.mapper.RepartidorMapper;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Repartidor;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository.RepartidorRepository;
import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.service.interfaces.RepartidorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación de la capa de servicio para la gestión operativa y dominio relacional
 * de la entidad {@link Repartidor}.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 19-10-2026
 * @see RepartidorService
 * @see RepartidorRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepartidorServiceImpl implements RepartidorService {

    private final RepartidorRepository repartidorRepository;

    @Override
    @Transactional
    public RepartidorResponseDto registrarRepartidor(RepartidorRequestDto requestDto) {
        log.info("Iniciando registro de nuevo repartidor con DNI: {}", requestDto.getDni());

        if (repartidorRepository.existsByDni(requestDto.getDni())) {
            throw new RecursoDuplicadoException("Ya existe un repartidor registrado con el DNI: " + requestDto.getDni());
        }

        if (repartidorRepository.existsByTelefono(requestDto.getTelefono())) {
            throw new RecursoDuplicadoException("Ya existe un repartidor registrado con el teléfono: " + requestDto.getTelefono());
        }

        Repartidor nuevoRepartidor = RepartidorMapper.toEntity(requestDto);
        Repartidor guardado = repartidorRepository.save(nuevoRepartidor);

        log.info("Repartidor registrado exitosamente con ID: {}", guardado.getIdRepartidor());
        return RepartidorMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public RepartidorResponseDto obtenerPorId(UUID idRepartidor) {
        log.info("Buscando repartidor por ID: {}", idRepartidor);

        Repartidor repartidor = repartidorRepository.findById(idRepartidor)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el repartidor con ID: " + idRepartidor));

        return RepartidorMapper.toResponseDto(repartidor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepartidorResponseDto> listarRepartidores(EstadoRepartidor estado) {
        log.info("Consultando lista de repartidores con filtro de estado: {}", estado);

        List<Repartidor> repartidores = (estado != null)
                ? repartidorRepository.findByEstadoRepartidor(estado)
                : repartidorRepository.findAll();

        return repartidores.stream()
                .map(RepartidorMapper::toResponseDto)
                .toList();
    }
}