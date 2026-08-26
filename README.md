# PSP · Reserva concurrente de asientos por sockets

Reimplementación del `Ejercicio4` de sockets: un servidor comparte un vagón 4×4 entre varios clientes y procesa reservas de forma atómica. Se añaden validación de coordenadas, un protocolo explícito, cierre correcto de clientes y concurrencia limitada.

Para conexiones remotas se usa la comprobación de conectividad adaptada del Gist MIT de Rodrigo Sambade: https://gist.github.com/rodrigosambadesaa/6f8e895de21c76b946bf70a1deffe05b. Las conexiones loopback funcionan sin Internet.

```bash
mvn verify
java -cp target/classes dev.rodrigosambade.seats.SeatServer 5000
java -cp target/classes dev.rodrigosambade.seats.SeatClient localhost 5000
```
