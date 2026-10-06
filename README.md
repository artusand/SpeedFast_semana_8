![Duoc UC](https://www.duoc.cl/wp-content/uploads/2022/09/logo-0.png)
# 🧠 Persistiendo datos con objetos y base de datos – Desarrollo Orientado a Objetos II

## 👤 Autor del proyecto
- **Nombre completo:** [Arturo Ignacio Sandoval Gonzalez]
- **Carrera:** Analista programador computacional
- **Sede:** [Concepcion]

---

## 📘 Descripcion general del sistema
Proyecto desarrollado en Java utilizando NetBeans. Sistema de gestion para la empresa SpeedFast que permite administrar repartidores, pedidos y entregas incorporando operaciones CRUD, conexion a MySQL mediante JDBC, patrón DAO e interfaz grafica desarrollada con Swing.

---

## 🧱 Estructura general del proyecto

```plaintext
📁src/

    -ui
        Main.java
        PanelRepartidores.java
        PanelPedidos.java
        PanelEntregas.java
        ItemCombo.java

    -dao
        RepartidorDAO.java
        PedidoDAO.java
        EntregaDAO.java

    -model
        Repartidor.java
        Pedido.java
        Entrega.java

    -conexion
        ConexionDB.java



    /lib/
        mysql-connector-j-26.7.0.jar

    /sql/
        speedfast_semana8.sql


````

---



## ⚙️ Instrucciones de funcionalidad:

1. CRUD de repartidores (guardar, editar, eliminar y listar en JTable).

2. CRUD de pedidos con tipo (COMIDA, ENCOMIENDA, EXPRESS) y estado (PENDIENTE,  EN_REPARTO, ENTREGADO).

3. Filtros del listado de pedidos por estado y por tipo.

4. CRUD de entregas, asociando un pedido y un repartidor mediante JComboBox, con fecha y hora.

5. Listado de entregas filtrado por pedido y por repartidor.

6. Clases DAO con PreparedStatement y ResultSet; cada operación informa si realmente afectó una fila.

7. Validación de entradas (campos vacíos, largo máximo, formato de fecha y hora).

8. Manejo de errores SQL con mensajes claros mediante JOptionPane, incluido el caso de eliminar registros con entregas asociadas.

9. Separación por capas: interfaz (ui), acceso a datos (dao), conexión (conexion) y modelo (model).

---

## ⚙️ Instrucciones de uso:

1. Abrir el proyecto.

2. Ejecutar el script sql/speedfast_semana8.sql en MySQL workbench con el usuario root. Crea la base speedfast_db, las tablas, datos de prueba y el usuario speedfast_user.

3. Verificar en ConexionDB.java que el usuario y la clave coincidan con los del script.

4. Comprobar que el conector lib/mysql-connector-j-26.7.0.jar aparece en Libraries.

5. Ejecutar la clase Main.java.

6. Registrar repartidores y pedidos en sus pestañas, y luego registrar entregas en la pestaña Entregas.

---
## 📘 Tecnologias utilizadas:

1. Java
2. Java Swing
3. Apache NetBeans
4. MySQL y MySQL Connector/J
5. JDBC


---

**Repositorio GitHub:** https://github.com/artusand/SpeedFast_semana_8.git
**Fecha de entrega:** \[05/10/2026]

---

© Duoc UC | Escuela de Informática y Telecomunicaciones | Semana 8 persistiendo datos con objetos y base de datos



