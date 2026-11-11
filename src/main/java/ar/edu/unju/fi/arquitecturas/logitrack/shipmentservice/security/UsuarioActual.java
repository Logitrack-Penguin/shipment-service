package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.security;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Usuario que hace el pedido, armado con las cabeceras que agrega el api-gateway después de validar
 * el JWT ({@code X-User-Roles} y {@code X-User-Dni}). El gateway descarta esas cabeceras si las manda
 * el cliente, y shipment-service solo es accesible a través del gateway.
 * <p>
 * Un REPARTIDOR (sin ADMIN) solo puede ver y cambiar los envíos asignados a él. Sin cabeceras (por
 * ejemplo, una llamada interna entre servicios) no se restringe. Mismo criterio que
 * {@code UsuarioActual} de courier-service (LT4-40).
 * </p>
 *
 * @param roles Roles del usuario, sin prefijo {@code ROLE_}.
 * @param dni   DNI del usuario (el de su ficha de repartidor), o {@code null} si el token no lo trae.
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 11-11-2026
 */
public record UsuarioActual(Set<String> roles, String dni) {

    /** Cabecera con los roles separados por coma, por ejemplo {@code ADMIN,REPARTIDOR}. */
    public static final String HEADER_ROLES = "X-User-Roles";

    /** Cabecera con el DNI del usuario. */
    public static final String HEADER_DNI = "X-User-Dni";

    /**
     * Arma el usuario a partir de las cabeceras del gateway.
     *
     * @param rolesHeader Valor de {@code X-User-Roles} (puede ser {@code null}).
     * @param dniHeader   Valor de {@code X-User-Dni} (puede ser {@code null}).
     * @return El usuario; sin roles si no vino la cabecera.
     */
    public static UsuarioActual desde(String rolesHeader, String dniHeader) {
        Set<String> roles = rolesHeader == null ? Set.of() : Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
        String dni = dniHeader == null || dniHeader.isBlank() ? null : dniHeader.trim();
        return new UsuarioActual(roles, dni);
    }

    /**
     * @return {@code true} si es REPARTIDOR y no ADMIN: solo puede acceder a sus propios envíos.
     */
    public boolean esRepartidorRestringido() {
        return roles.contains("REPARTIDOR") && !roles.contains("ADMIN");
    }

    /**
     * @param dniRepartidorDelEnvio DNI del repartidor asignado al envío ({@code null} si no tiene).
     * @return {@code true} si puede ver el envío: siempre, salvo un repartidor restringido con un envío
     *         que no está asignado a él (ni los pendientes de asignación, porque no puede asignárselos).
     */
    public boolean puedeVer(String dniRepartidorDelEnvio) {
        return !esRepartidorRestringido() || (dni != null && dni.equals(dniRepartidorDelEnvio));
    }
}
