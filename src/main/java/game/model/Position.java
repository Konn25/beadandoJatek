package game.model;

import java.util.Objects;

public final class Position {
    private final int x;
    private final int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Position) obj;
        return this.x == that.x &&
                this.y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


    public String toStringPos() {
        return "Position[" +
                "x=" + x + ", " +
                "y=" + y + ']';
    }

    public Position moveTo(Direction direction) {
        return new Position(x + direction.getRowChange(), y + direction.getColChange());
    }

    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

}
