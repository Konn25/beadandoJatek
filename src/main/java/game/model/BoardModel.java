package game.model;

import javafx.beans.property.ObjectProperty;

import java.util.*;

public class BoardModel {

    public static int TABLA_MERET_X = 5;
    public static int TABLA_MERET_Y = 4;

    private final Circle[] circles;

    public BoardModel() {
        this(new Circle(CircleType.KEK,new Position(0,0)));
    }

    public BoardModel(Circle... circles) {
        checkCircle(circles);
        this.circles =circles.clone();
    }

    private void checkCircle(Circle[] circles){
        var circlePos = new HashSet<Position>();
        for(var circle: circles){
            if(! isOnBoard(circle.getPosition()) || circlePos.contains(circle.getPosition())){
                throw new IllegalArgumentException();
            }
            circlePos.add(circle.getPosition());
        }
    }

    public int getCirclesNumber(){
        return circles.length;
    }

    public CircleType getCircleType(int circleNumber){
        return circles[circleNumber].getType();
    }

    public Position getCirclePosition(int circleNumber){
        return circles[circleNumber].getPosition();
    }

    public ObjectProperty<Position> positionProperty(int circleNumber) {
        return circles[circleNumber].positionProperty();
    }

    public boolean isValidMove(int pieceNumber, CircleDirection direction) {
        if (pieceNumber < 0 || pieceNumber >= circles.length) {
            throw new IllegalArgumentException();
        }
        Position newPosition = circles[pieceNumber].getPosition().moveTo(direction);
        if (! isOnBoard(newPosition)) {
            return false;
        }
        for (var piece : circles) {
            if (piece.getPosition().equals(newPosition)) {
                return false;
            }
        }
        return true;
    }

    public Set<CircleDirection> getValidMoves(int pieceNumber) {
        EnumSet<CircleDirection> validMoves = EnumSet.noneOf(CircleDirection.class);
        for (var direction : CircleDirection.values()) {
            if (isValidMove(pieceNumber, direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    public void move(int pieceNumber, CircleDirection direction) {
        circles[pieceNumber].moveTo(direction);
    }

    public static boolean isOnBoard(Position position) {
        return 0 <= position.x() && position.x() < TABLA_MERET_X
                && 0 <= position.y() && position.y() < TABLA_MERET_Y;
    }

    public List<Position> getPiecePositions() {
        List<Position> positions = new ArrayList<>(circles.length);
        for (var piece : circles) {
            positions.add(piece.getPosition());
        }
        return positions;
    }

    public OptionalInt getPieceNumber(Position position) {
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (var piece : circles) {
            joiner.add(piece.toString());
        }
        return joiner.toString();
    }

    public static void main(String[] args) {
        BoardModel model = new BoardModel();
        System.out.println(model);
    }

}
