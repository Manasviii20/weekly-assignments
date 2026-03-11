class ParkingSpot {
    String licensePlate;
    long entryTime;
    boolean occupied;
}

class ParkingLot {
    private ParkingSpot[] spots;
    private int size;

    public ParkingLot(int capacity) {
        spots = new ParkingSpot[capacity];
        size = capacity;
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % size;
    }

    public int parkVehicle(String plate) {
        int idx = hash(plate);
        int probes = 0;
        while (spots[idx] != null && spots[idx].occupied) {
            idx = (idx + 1) % size;
            probes++;
        }
        spots[idx] = new ParkingSpot();
        spots[idx].licensePlate = plate;
        spots[idx].entryTime = System.currentTimeMillis();
        spots[idx].occupied = true;
        System.out.println("Assigned spot #" + idx + " (" + probes + " probes)");
        return idx;
    }

    public void exitVehicle(String plate) {
        int idx = hash(plate);
        while (spots[idx] != null) {
            if (spots[idx].occupied && spots[idx].licensePlate.equals(plate)) {
                long duration = System.currentTimeMillis() - spots[idx].entryTime;
                spots[idx].occupied = false;
                System.out.println("Spot #" + idx + " freed, Duration: " + duration/1000 + "s");
                return;
            }
            idx = (idx + 1) % size;
        }
    }
}