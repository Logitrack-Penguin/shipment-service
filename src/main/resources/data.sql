SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE histórico_estado_envíos;
TRUNCATE TABLE envíos;
TRUNCATE TABLE repartidores;
TRUNCATE TABLE clientes;

SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- 1. CLIENTES
-- ==========================================================
INSERT INTO clientes (id, fecha_creación, fecha_ultima_modificacion, apellido, dni, email, nombre, teléfono)
VALUES
    (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), NOW(), NOW(), 'Pérez', '35123456', 'juan.perez@email.com', 'Juan', '3881234567'),
    (UUID_TO_BIN('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), NOW(), NOW(), 'Gómez', '38987654', 'maria.gomez@email.com', 'María', '3887654321'),
    (UUID_TO_BIN('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'), NOW(), NOW(), 'López', '40111222', 'carlos.lopez@email.com', 'Carlos', '3889876543'),
    (UUID_TO_BIN('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'), NOW(), NOW(), 'Martínez', '42333444', 'ana.martinez@email.com', 'Ana', '3884567890');

-- ==========================================================
-- 2. REPARTIDORES
-- ==========================================================
INSERT INTO repartidores (id, fecha_creación, fecha_ultima_modificacion, apellido, dni, estado_repartidor, nombre, teléfono, tipo_vehículo)
VALUES
    (UUID_TO_BIN('111bc999-9c0b-4ef8-bb6d-6bb9bd380111'), NOW(), NOW(), 'Quito', '28111222', 1, 'Esteban', '388111222', 'MOTO'),
    (UUID_TO_BIN('222bc999-9c0b-4ef8-bb6d-6bb9bd380222'), NOW(), NOW(), 'Melano', '30222333', 1, 'Rosa', '388222333', 'AUTO');

-- ==========================================================
-- 3. ENVÍOS
-- ==========================================================
INSERT INTO envíos (
    id, fecha_creación, fecha_ultima_modificacion,
    código_seguimiento, código_recepción,
    alto, ancho, largo, peso, tarifa,
    estado_envío, fecha_estimada_entrega,
    calle_origen, numero_origen, ciudad_origen, codigo_postal_origen, observaciones_origen,
    calle_destino, numero_destino, ciudad_destino, codigo_postal_destino, observaciones_destino,
    cliente_id, repartidor_id
)
VALUES
    (
        UUID_TO_BIN('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380e01'), NOW(), NOW(),
        'TRK-2026-000001', 'REC-001',
        15.0, 20.0, 30.0, 2.50, 4500.00,
        'PENDIENTE', '2026-10-25',
        'Av. Belgrano', '1234', 'San Salvador de Jujuy', '4600', 'Salida desde depósito central',
        'Calle Salta', '456', 'San Salvador de Jujuy', '4600', 'Entrega en planta baja',
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), NULL
    ),
    (
        UUID_TO_BIN('e2eebc99-9c0b-4ef8-bb6d-6bb9bd380e02'), NOW(), NOW(),
        'TRK-2026-000002', 'REC-002',
        10.0, 10.0, 20.0, 1.20, 3200.00,
        'EN_RUTA', '2026-10-22',
        'San Martín', '789', 'San Salvador de Jujuy', '4600', 'Retiro en sucursal',
        'Éxodo', '101', 'Palpalá', '4612', 'Casa con reja blanca',
        UUID_TO_BIN('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), UUID_TO_BIN('111bc999-9c0b-4ef8-bb6d-6bb9bd380111')
    );

-- ==========================================================
-- 4. HISTÓRICO DE ESTADOS DE ENVÍOS (Sin valores NULL)
-- ==========================================================
INSERT INTO histórico_estado_envíos (
    id_historico_estado_envio, código_seguimiento,
    estado_envío_anterior, estado_envío_nuevo, fecha_cambio_estado
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