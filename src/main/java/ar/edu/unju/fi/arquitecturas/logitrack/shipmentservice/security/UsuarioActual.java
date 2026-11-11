package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.security;

import ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Envio;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Usuario que hace el pedido, armado con las cabeceras que agrega el api-gateway después de validar
 * el JWT ({@code X-User-Roles} y {@code X-User-Dni}). El gateway descarta esas cabeceras si las manda
 * el cliente, y shipment-service solo es accesible a través del gateway.
 * <p>
 * Un usuario sin ADMIN queda restringido a lo suyo:
 * <ul>
 *     <li>REPARTIDOR: los envíos asignados a él (DNI de su ficha de repartidor).</li>
 *     <li>CLIENTE: sus envíos y su ficha de cliente (DNI de su ficha de cliente).</li>
 * </ul>
 * Sin DNI no accede a nada. Sin cabeceras (por ejemplo, una llamada interna entre servicios) no se
 * restringe. Mismo criterio que {@code UsuarioActual} de courier-service (LT4-40).
 * </p>
 *
 * @param roles Roles del usuario, sin prefijo {@code ROLE_}.
 * @param dni   DNI del usuario (el de su ficha de repartidor o de cliente), o {@code null} si el token
 *              no lo trae.
 * @author Logitrack-Penguin
 * @version 1.1.0
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
     * @return {@code true} si es REPARTIDOR y no ADMIN: solo accede a los envíos asignados a él.
     */
    public boolean esRepartidorRestringido() {
        return roles.contains("REPARTIDOR") && !roles.contains("ADMIN");
    }

    /**
     * @return {@code true} si es CLIENTE y no ADMIN: solo accede a sus envíos y a su ficha.
     */
    public boolean esClienteRestringido() {
        return roles.contains("CLIENTE") && !roles.contains("ADMIN");
    }

    /**
     * @return {@code true} si hay que filtrar lo que ve (REPARTIDOR o CLIENTE sin ADMIN).
     */
    public boolean esRestringido() {
        return esRepartidorRestringido() || esClienteRestringido();
    }

    /**
     * @param envio Envío a mostrar o modificar.
     * @return {@code true} si puede verlo: siempre, salvo un usuario restringido con un envío que no
     *         es suyo. Un repartidor tampoco ve los pendientes de asignación, porque no puede
     *         asignárselos.
     */
    public boolean puedeVer(Envio envio) {
        if (!esRestringido()) {
            return true;
        }
        if (dni == null) {
            return false;
        }
        boolean esSuRepartidor = esRepartidorRestringido() && dni.equals(envio.getDniRepartidor());
        boolean esSuCliente = esClienteRestringido() && envio.getCliente() != null
                && dni.equals(envio.getCliente().getDni());
        return esSuRepartidor || esSuCliente;
    }

    /**
     * @param dniCliente DNI de un cliente.
     * @return {@code true} si puede ver o usar ese cliente: siempre, salvo un CLIENTE restringido con
     *         un DNI que no es el suyo.
     */
    public boolean puedeUsarCliente(String dniCliente) {
        return !esClienteRestringido() || (dni != null && dni.equals(dniCliente));
    }
}
