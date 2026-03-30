CREATE DATABASE IF NOT EXISTS uamishop_catalogo;
CREATE DATABASE IF NOT EXISTS uamishop_ventas;
CREATE DATABASE IF NOT EXISTS uamishop_orden;

GRANT ALL PRIVILEGES ON uamishop_catalogo.* TO 'uamishop'@'%';
GRANT ALL PRIVILEGES ON uamishop_ventas.* TO 'uamishop'@'%';
GRANT ALL PRIVILEGES ON uamishop_orden.* TO 'uamishop'@'%';

FLUSH PRIVILEGES;
