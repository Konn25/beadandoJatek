package game.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Circle {

    private final CircleType type;
    private final ObjectProperty<Position> position = new SimpleObjectProperty<>();


    public Circle(CircleType type, Position position) {
        this.type = type;
        this.position.set(position);
    }

    public CircleType getType(){
        return type;
    }

    public Position getPosition(){
        return position.get();
    }

    public void moveTo(Direction direction){
        Position newPos = position.get().moveTo(direction);
        position.set(newPos);
    }

    public ObjectProperty<Position> positionProperty(){
        return position;
    }

    public String toString(){
        return type.toString() + position.get().toStringPos();
    }

    public static void main(String[] args){
        Circle circle = new Circle(CircleType.KEK,new Position(0,0));
        circle.positionProperty().addListener((observableValue, oldPos, newPos) ->{
            System.out.printf("%s -> %s\n", oldPos.toStringPos(), newPos.toStringPos());
        } );

        System.out.println(circle);
        circle.moveTo(CircleDirection.LE);
        System.out.println(circle);
    }



}
