package net.cjsah.slimefinder.data;

public record Position(int x, int z) {
    public static final Position ZERO = new Position(0, 0);

    public String toString() {
        return this.x + "," + this.z;
    }

}
