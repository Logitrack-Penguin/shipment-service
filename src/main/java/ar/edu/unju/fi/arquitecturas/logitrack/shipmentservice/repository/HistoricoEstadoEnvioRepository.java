package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.HistoricoEstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la jerarquía {@link HistoricoEstadoEnvio}.
 * <p>
 * Implementa el Patrón Almacén del Dominio (Domain Store) abstrayendo las consultas relacionales
 * sobre la tabla unificada generada bajo la estrategia SINGLE_TABLE.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see HistoricoEstadoEnvio
 * @see JpaRepository
 */

@Repository
public interface HistoricoEstadoEnvioRepository extends JpaRepository<HistoricoEstadoEnvio, UUID> {

    /**
     * Retorna el histórico en orden cronológico de los cambios.
     *
     * @param codigoSeguimiento Código de seguimiento del envío correspondiente.
     * @return {@link List} Listado de los cambios en orden cronológico.
     */
    List<HistoricoEstadoEnvio> findByCodigoSeguimientoOrderByFechaCambioAsc(String codigoSeguimiento);
}
