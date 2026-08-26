package dev.rodrigosambade.seats;

import dev.rodrigosambade.net.ConnectivityAndInternetAccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class SeatClient {

    private static final Duration CONNECTIVITY_TIMEOUT = Duration.ofSeconds(2);
    private static final int CONNECT_TIMEOUT_MS = 3_000;

    private SeatClient() {
    }

    public static String reserve(
            String host,
            int port,
            int row,
            int column) throws IOException {
        ConnectivityAndInternetAccess.requireInternetForRemoteHost(
                host,
                CONNECTIVITY_TIMEOUT);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MS);

            try (BufferedReader input = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.UTF_8));
                 BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
                         socket.getOutputStream(),
                         StandardCharsets.UTF_8))) {
                sendReservation(output, row, column);
                return input.readLine();
            }
        }
    }

    private static void sendReservation(
            BufferedWriter output,
            int row,
            int column) throws IOException {
        output.write("RESERVE " + row + " " + column);
        output.newLine();
        output.flush();
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: host port");
        }

        try (BufferedReader console = new BufferedReader(new InputStreamReader(
                System.in,
                StandardCharsets.UTF_8))) {
            int row = readCoordinate(console, "Fila (0-3): ");
            int column = readCoordinate(console, "Asiento (0-3): ");

            String response = reserve(
                    args[0],
                    Integer.parseInt(args[1]),
                    row,
                    column);
            System.out.println(response);
        }
    }

    private static int readCoordinate(BufferedReader console, String prompt)
            throws IOException {
        System.out.print(prompt);
        return Integer.parseInt(console.readLine());
    }
}
