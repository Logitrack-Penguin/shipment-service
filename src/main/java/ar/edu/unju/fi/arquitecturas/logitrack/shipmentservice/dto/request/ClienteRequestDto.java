package ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Objeto de Transferencia de Datos correspondiente a los clientes para la recepción y validación de la información a
 * la hora de registrar un nuevo cliente o actualizar uno ya existente.
 *
 * @author Logitrack-Penguin
 * @version 1.0.0
 * @since 16-10-2026
 * @see ar.edu.unju.fi.arquitecturas.logitrack.shipmentservice.model.Cliente
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDto {

    /**
     * Documento Nacional de Identidad del cliente. Este mismo es obligatorio y solo puede contener 8 caracteres
     * numéricos.
     */
    @NotBlank(message = "El DNI es obligatorio.")
    @Pattern(regexp = "\\d{8}", message = "El DNI solo puede contener números.")
    private String dni;

    /**
     * Nombre de pila del cliente. Es un campo obligatorio y bloquea cualquier nombre con números o caracteres especiales.
     */
    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 30, message = "El nombre no puede superar los 30 caracteres.")
    @Pattern(regexp = "[\\p{L} ]+", message = "El nombre solo puede contener letras.")
    private String nombre;

    /**
     * Apellido del cliente. Tiene las mismas limitaciones que el nombre de pila.
     */
    @NotBlank(message = "El apellido es obligatorio.")
    @Pattern(regexp = "[\\p{L} ]+", message = "El apellido solo puede contener letras.")
    @Pattern(regexp = "[\\p{L} ]+", message = "El apellido solo puede contener letras.")
    private String apellido;

    /**
     * Correo Electrónico de contacto del cliente validado.
     * @see Email
     */
    @Email
    private String email;

    /**
     * Número telefónico de contacto del cliente obligatorio y validado para que solo admita 10 caracteres numéricos.
     */
    @NotBlank(message = "El teléfono es obligatorio.")
    @Pattern(regexp = "\\d{10}")
    private String telefono;
}
