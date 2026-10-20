package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.ConfigTarifaBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la jerarquía {@link ConfigTarifaBase}.
 * <p>
 * Implementa el Patrón Almacén del Dominio (Domain Store) abstrayendo las consultas relacionales
 * sobre la tabla unificada generada bajo la estrategia SINGLE_TABLE.
 * </p>
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 20-10-2026
 * @see ConfigTarifaBase
 * @see JpaRepository
 */
@Repository
public interface ConfigTarifaBaseRepository extends JpaRepository<ConfigTarifaBase, UUID> {
}
