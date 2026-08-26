# Enunciado / material de partida — Ejercicio 4 de sockets

Fuente: `Ejercicio4.zip` del tema **Sockets** de Programación de Servicios y Procesos.

El ejercicio consiste en implementar por sockets un sistema de reserva concurrente de asientos. El servidor mantiene el estado compartido de un vagón de **4 × 4 plazas** y debe atender a varios clientes.

Cada cliente consulta/selecciona una plaza y solicita su reserva. El servidor debe validar las coordenadas y garantizar que la comprobación y la reserva se realizan de forma atómica, de modo que dos clientes no puedan obtener simultáneamente la misma plaza.

La versión modernizada incorpora además un protocolo explícito, validación de entrada, cierre correcto de conexiones y un límite de concurrencia.

No existe un volcado de base de datos asociado: el estado del ejercicio reside en memoria en el servidor.

> Drive conserva la solución/proyecto en ZIP y no un PDF independiente del enunciado; este documento describe el objetivo verificable del material recuperado.
