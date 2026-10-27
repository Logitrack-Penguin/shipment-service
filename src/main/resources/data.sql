SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE historico_estado_envios;
TRUNCATE TABLE envios;
TRUNCATE TABLE clientes;
TRUNCATE TABLE configuracion_tarifa_base;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 1. CONFIGURACIÓN DE TARIFA BASE
-- ==========================================================
INSERT INTO configuracion_tarifa_base (
    id_config_tarifa_base, fecha_creacion, fecha_ultima_modificacion,
    tarifa_base, costo_por_kg, costo_por_km, costo_por_cm_cubo
)
VALUES
    (
        UUID_TO_BIN('11111111-9c0b-4ef8-bb6d-6bb9bd380f01'), NOW(), NOW(),
        2500.00, 150.50, 45.00, 5.25
    );

-- ==========================================================
-- 2. CLIENTES
-- ==========================================================
INSERT INTO clientes (id, fecha_creacion, fecha_ultima_modificacion, apellido, dni, email, nombre, telefono)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), NOW(), NOW(), 'Pérez', '35123456', 'juan.perez@email.com', 'Juan', '3881234567'),
    (UUID_TO_BIN('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), NOW(), NOW(), 'Gómez', '38987654', 'maria.gomez@email.com', 'María', '3887654321'),
    (UUID_TO_BIN('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), NOW(), NOW(), 'López', '40111222', 'carlos.lopez@email.com', 'Carlos', '3889876543'),
    (UUID_TO_BIN('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), NOW(), NOW(), 'Martínez', '42333444', 'ana.martinez@email.com', 'Ana', '3884567890');


-- ==========================================================
-- 3. ENVÍOS
-- ==========================================================
INSERT INTO envios (
    id, fecha_creacion, fecha_ultima_modificacion,
    codigo_seguimiento, codigo_recepcion,
    alto, ancho, largo, peso, tarifa,
    estado_envio, fecha_estimada_entrega,
    calle_origen, numero_origen, ciudad_origen, codigo_postal_origen, observaciones_origen,
    calle_destino, numero_destino, ciudad_destino, codigo_postal_destino, observaciones_destino, dni_repartidor,
    cliente_id
)
VALUES
    (
        UUID_TO_BIN('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380e01'), NOW(), NOW(),
        'TRK-2026-000001', 'REC-001',
        15.0, 20.0, 30.0, 2.50, 4500.00,
        'PENDIENTE', '2026-10-25',
        'Av. Belgrano', '1234', 'San Salvador de Jujuy', '4600', 'Salida desde depósito central',
        'Calle Salta', '456', 'San Salvador de Jujuy', '4600', 'Entrega en planta baja', null,
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
    ),
    (
        UUID_TO_BIN('e2eebc99-9c0b-4ef8-bb6d-6bb9bd380e02'), NOW(), NOW(),
        'TRK-2026-000002', 'REC-002',
        10.0, 10.0, 20.0, 1.20, 3200.00,
        'EN_RUTA', '2026-10-22',
        'San Martín', '789', 'San Salvador de Jujuy', '4600', 'Retiro en sucursal',
        'Éxodo', '101', 'Palpalá', '4612', 'Casa con reja blanca', null,
        UUID_TO_BIN('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22')
    );

-- ==========================================================
-- 4. HISTÓRICO DE ESTADOS DE ENVÍOS
-- ==========================================================
INSERT INTO historico_estado_envios (
    id_historico_estado_envio, codigo_seguimiento,
    estado_envio_anterior, estado_envio_nuevo, fecha_cambio_estado
)
VALUES
    (
        UUID_TO_BIN('333bc999-9c0b-4ef8-bb6d-6bb9bd380333'), 'TRK-2026-000001',
        'PENDIENTE', 'PENDIENTE', NOW()
    ),
    (
        UUID_TO_BIN('444bc999-9c0b-4ef8-bb6d-6bb9bd380444'), 'TRK-2026-000002',
        'PENDIENTE', 'EN_RUTA', NOW()
    );