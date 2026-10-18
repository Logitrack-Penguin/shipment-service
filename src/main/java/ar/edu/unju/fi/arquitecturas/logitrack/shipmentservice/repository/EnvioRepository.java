package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la jerarquía {@link Envio}.
 * <p>
 * Implementa el Patrón Almacén del Dominio (Domain Store) abstrayendo las consultas relacionales
 * sobre la tabla unificada generada bajo la estrategia SINGLE_TABLE.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 17-10-2026
 * @see Envio
 * @see JpaRepository
 */

@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    /**
     * Búsqueda unívoca de un envío por su código de seguimiento.
     * @param codigoSeguimiento Código de seguimiento del envío a buscar.
     * @return {@link Optional} con la instancia del envío en caso de que exista.
     */
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
}
