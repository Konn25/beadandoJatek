package game.model;

import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

import java.util.*;

public class BoardModel {

    public static int TABLA_MERET_X = 5;
    public static int TABLA_MERET_Y = 4;

    private final Circle[] circles;

    public BoardModel() {
        this(new Circle(CircleType.BLUE,new Position(0,0)),
                new Circle(CircleType.RED,new Position(0,1)),
                new Circle(CircleType.BLUE,new Position(0,2)),
                new Circle(CircleType.RED,new Position(0,3)),

                new Circle(CircleType.RED,new Position(4,0)),
                new Circle(CircleType.BLUE,new Position(4,1)),
                new Circle(CircleType.RED,new Position(4,2)),
                new Circle(CircleType.BLUE,new Position(4,3))
                );
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

    public int getCircleNumber(){
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

    public boolean isValidMove(int circleNumber, CircleDirection direction) {
        if (circleNumber < 0 || circleNumber >= circles.length) {
            throw new IllegalArgumentException();
        }
        Position newPosition = circles[circleNumber].getPosition().moveTo(direction);
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

    public void move(int circleNumber, CircleDirection direction) {
        circles[circleNumber].moveTo(direction);
    }

    public static boolean isOnBoard(Position position) {
        return 0 <= position.x() && position.x() < TABLA_MERET_X
                && 0 <= position.y() && position.y() < TABLA_MERET_Y;
    }

    public List<Position> getCirclePositions() {
        List<Position> positions = new ArrayList<>(circles.length);
        for (var piece : circles) {
            positions.add(piece.getPosition());
        }
        return positions;
    }

    public OptionalInt getCircleNumber(Position position) {
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].getPosition().equals(position)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public String getCircleColor(int circlesNumber){
        String color = circles[circlesNumber].getType().name();

        return color;
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
