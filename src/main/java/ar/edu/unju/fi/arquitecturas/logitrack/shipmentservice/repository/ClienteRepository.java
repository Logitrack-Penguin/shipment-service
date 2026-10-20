package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.repository;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la gestión de persistencia de la entidad {@link Cliente}.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 18-10-2026
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    /**
     * Busca un cliente por su Documento Nacional de Identidad (DNI).
     *
     * @param dni DNI del cliente.
     * @return Un {@link Optional} que contiene el cliente si existe.
     */
    Optional<Cliente> findByDni(String dni);

    /**
     * Verifica si ya existe un cliente registrado con el DNI proporcionado.
     *
     * @param dni DNI a verificar.
     * @return {@code true} si existe, {@code false} en caso contrario.
     */
    boolean existsByDni(String dni);
    /**
     * Verifica si ya existe un cliente registrado con el correo electrónico proporcionado.
     *
     * @param email Correo electrónico a verificar.
     * @return {@code true} si existe un registro con ese email, {@code false} en caso contrario.
     */
    boolean existsByEmail(String email);
    /**
     * Verifica si ya existe un cliente registrado con el numero de telefono proporcionado.
     *
     * @param telefono numero telefonico a verificar.
     * @return {@code true} si existe un registro con ese telefono, {@code false} en caso contrario.
     */
    boolean existsByTelefono(String telefono);
}