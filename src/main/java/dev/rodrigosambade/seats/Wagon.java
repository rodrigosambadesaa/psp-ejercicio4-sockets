package dev.rodrigosambade.seats;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Wagon {

    public enum Status {
        RESERVED,
        OCCUPIED,
        FULL,
        INVALID
    }

    public record Reservation(Status status, List<String> freeSeats) {
    }

    private final boolean[][] occupied;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public Wagon(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("rows and columns must be positive");
        }
        occupied = new boolean[rows][columns];
    }

    public Reservation reserve(int row, int column) {
        lock.writeLock().lock();
        try {
            if (!isValidSeat(row, column)) {
                return reservation(Status.INVALID);
            }

            if (occupied[row][column]) {
                Status status = isFullUnsafe()
                        ? Status.FULL
                        : Status.OCCUPIED;
                return reservation(status);
            }

            occupied[row][column] = true;
            return reservation(Status.RESERVED);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> freeSeats() {
        lock.readLock().lock();
        try {
            return freeSeatsUnsafe();
        } finally {
            lock.readLock().unlock();
        }
    }

    private Reservation reservation(Status status) {
        return new Reservation(status, freeSeatsUnsafe());
    }

    private boolean isValidSeat(int row, int column) {
        return row >= 0
                && column >= 0
                && row < occupied.length
                && column < occupied[0].length;
    }

    private boolean isFullUnsafe() {
        for (boolean[] row : occupied) {
            for (boolean seatOccupied : row) {
                if (!seatOccupied) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<String> freeSeatsUnsafe() {
        List<String> freeSeats = new ArrayList<>();

        for (int row = 0; row < occupied.length; row++) {
            for (int column = 0; column < occupied[row].length; column++) {
                if (!occupied[row][column]) {
                    freeSeats.add(row + "," + column);
                }
            }
        }

        return List.copyOf(freeSeats);
    }
}
