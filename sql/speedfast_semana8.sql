CREATE DATABASE IF NOT EXISTS speedfast_db;
USE speedfast_db;

CREATE TABLE IF NOT EXISTS repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA','ENCOMIENDA','EXPRESS'),
    estado ENUM('PENDIENTE','EN_REPARTO','ENTREGADO')
);

CREATE TABLE IF NOT EXISTS entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT,
    id_repartidor INT,
    fecha DATE,
    hora TIME,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
);

INSERT INTO repartidores (nombre) VALUES ('Juan Perez'), ('Maria Soto');
INSERT INTO pedidos (direccion, tipo, estado) VALUES
    ('Av. Central 123', 'COMIDA', 'PENDIENTE'),
    ('Calle Los Aromos 45', 'ENCOMIENDA', 'EN_REPARTO');

CREATE USER IF NOT EXISTS 'speedfast_user'@'localhost' IDENTIFIED BY 'SpeedFast2026!';
GRANT ALL PRIVILEGES ON speedfast_db.* TO 'speedfast_user'@'localhost';
FLUSH PRIVILEGES;

SHOW TABLES;
