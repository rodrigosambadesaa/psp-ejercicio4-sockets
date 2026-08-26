package dev.rodrigosambade.seats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SeatServer implements AutoCloseable {

    private static final int ROWS = 4;
    private static final int COLUMNS = 4;
    private static final int MAX_CLIENTS = ROWS * COLUMNS;
    private static final int DEFAULT_PORT = 5_000;

    private final Wagon wagon = new Wagon(ROWS, COLUMNS);
    private final ServerSocket serverSocket;
    private final ExecutorService workers = Executors.newFixedThreadPool(MAX_CLIENTS);

    private volatile boolean running = true;

    public SeatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public int port() {
        return serverSocket.getLocalPort();
    }

    public void serve() throws IOException {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                workers.submit(() -> handle(socket));
            } catch (SocketException exception) {
                if (running) {
                    throw exception;
                }
            }
        }
    }

    private void handle(Socket socket) {
        try (socket;
             BufferedReader input = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(),
                     StandardCharsets.UTF_8));
             BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
                     socket.getOutputStream(),
                     StandardCharsets.UTF_8))) {
            processCommands(input, output);
        } catch (IOException ignored) {
            // A failed client must not stop the server.
        }
    }

    private void processCommands(BufferedReader input, BufferedWriter output)
            throws IOException {
        String line;
        while ((line = input.readLine()) != null) {
            if (line.equals("QUIT")) {
                return;
            }
            processCommand(line, output);
        }
    }

    private void processCommand(String line, BufferedWriter output) throws IOException {
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3 || !parts[0].equals("RESERVE")) {
            send(output, "ERROR expected RESERVE row col");
            return;
        }

        try {
            int row = Integer.parseInt(parts[1]);
            int column = Integer.parseInt(parts[2]);
            Wagon.Reservation reservation = wagon.reserve(row, column);
            sendReservation(output, reservation);
        } catch (NumberFormatException exception) {
            send(output, "INVALID|");
        }
    }

    private static void sendReservation(
            BufferedWriter output,
            Wagon.Reservation reservation) throws IOException {
        String freeSeats = String.join(";", reservation.freeSeats());
        send(output, reservation.status() + "|" + freeSeats);
    }

    private static void send(BufferedWriter output, String message) throws IOException {
        output.write(message);
        output.newLine();
        output.flush();
    }

    @Override
    public void close() throws IOException {
        running = false;
        serverSocket.close();
        workers.shutdownNow();
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : DEFAULT_PORT;

        try (SeatServer server = new SeatServer(port)) {
            server.serve();
        }
    }
}
