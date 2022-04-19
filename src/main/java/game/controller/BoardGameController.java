package game.controller;

import game.model.BoardModel;
import game.model.CircleDirection;
import game.model.Position;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Comparator.comparing;

public class BoardGameController {

    private enum Players{
        RED_PLAYER,
        BLUE_PLAYER;

        public Players alter() {
            return switch (this) {
                case BLUE_PLAYER -> RED_PLAYER;
                case RED_PLAYER -> BLUE_PLAYER;
            };
        }
    }


    private enum Select{
        SELECT_FROM,
        SELECT_TO;

        public Select alter(){
            return switch (this){
                case SELECT_FROM -> SELECT_TO;
                case SELECT_TO -> SELECT_FROM;
            };
        }
    }

    private Players players = Players.RED_PLAYER;
    private Select select = Select.SELECT_FROM;

    private List<Position> selectPos = new ArrayList<>();

    private Position selected;

    private BoardModel model = new BoardModel();

    @FXML
    private GridPane board;

    @FXML
    private void initialize(){
        createBoard();
        createCircles();
        setSelectablePositions();
        showSelectablePositions();
    }

    private void createBoard(){
        for(int i = 0; i < board.getRowCount();i++){
            for(int j = 0; j < board.getColumnCount();j++){
                var square = createSquare();
                board.add(square, j,i);
            }
        }
    }

    private StackPane createSquare(){
        var square = new StackPane();
        square.getStyleClass().add("square");
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }


    private void createCircles(){
        for(int i = 0; i < model.getCircleNumber();i++){
            model.positionProperty(i).addListener(this::circlePositionChange);
            var circle = createCircle(Color.valueOf(model.getCircleType(i).name()));
            getCircle(model.getCirclePosition(i)).getChildren().add(circle);
        }
    }

    private Circle createCircle(Color color){
        var circle = new javafx.scene.shape.Circle(50);
        circle.setFill(color);
        return circle;
    }

    @FXML
    private void handleMouseClick(MouseEvent event){
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        var pos = new Position(row,col);

        handleClickOnSquare(pos);

    }

    private void handleClickOnSquare(Position pos){
        switch (select){
            case SELECT_FROM ->{
                if(selectPos.contains(pos) ){
                    selectPosition(pos);
                    alterSelectionPhase();

                }
            }
            case SELECT_TO -> {
                if(selectPos.contains(pos)){

                    var circleNumber  = model.getCircleNumber(selected).getAsInt();
                    var direction = CircleDirection.of(pos.x() - selected.x(), pos.y() - selected.y());
                    model.move(circleNumber, direction);

                    getCircleColor(circleNumber);
                    deselectSelectedPosition();
                    checkWinner();
                    alterSelectionPhase();
                    alterPlayerPhase();

                }
            }
        }
    }

    private String getCircleColor(int circleNumber){
        String color=model.getCircleColor(circleNumber);

        return color;

    }



    private void checkWinner(){
        List<Position> allPos = new ArrayList<>();
        allPos.addAll(model.getCirclePositions());

        List<Position> RedPos = new ArrayList<>();
        List<Position> BluePos = new ArrayList<>();

        for(int i =0; i<allPos.size();i++){

            if(getCircleColor(i) == "RED"){
                RedPos.add(allPos.get(i));
            }
            else if(getCircleColor(i) == "BLUE"){
                BluePos.add(allPos.get(i));
            }
        }

        Collections.sort(RedPos, comparing(Position::x).thenComparing(Position::y));
        Collections.sort(BluePos, comparing(Position::x).thenComparing(Position::y));

        int[] row = new int[5];
        int[] col = new int[4];
        int[] diagonal = new int[4];

        for(int i=0;i<5;i++){
            row[i]=checkMoveRow(i,RedPos);
        }

        for(int i=0;i<3;i++){
            col[i]=checkMoveCol(i,RedPos);
        }

        for(int i=0;i<4;i++){
            diagonal[i]=checkMoveDiagonal(RedPos);
        }


        for(var s:row){
            if(s==3){
                Logger.info("RED WINS");
            }
            //Logger.info("Sor: "+s);
        }

        for(var s:col){
            if(s==6){
                Logger.info("RED WINS");
            }
            //Logger.info("Oszlop: "+s);
        }

        for(var s:diagonal){
            Logger.info("ATLO: "+s);
        }
        diagonalWinCheck(diagonal);

        Logger.info("RED POS: ");
        Logger.info(RedPos);

        Logger.info("BLUE POS: ");
        Logger.info(BluePos);


    }

    public void diagonalWinCheck(int[] diagonalArray){

        if(diagonalArray[0]==1 && diagonalArray[1]==1 && diagonalArray[2]==1){
            Logger.info("RED WIN");
        }
        else if(diagonalArray[1]==1 && diagonalArray[2]==1 && diagonalArray[3]==1){
            Logger.info("RED WIN");
        }

    }

    public int checkMoveRow(int row,List<Position> PosList){
        int count = 0;
        for(int i=0;i<PosList.size();i++){
            if (PosList.get(i).x() == row && PosList.get(i).y() < 4) {
                if (PosList.get(2).y() - PosList.get(0).y() != 3) {
                    count++;
                }
            }
        }


        return count;
    }

    public int checkMoveCol(int col,List<Position> PosList){
        int count = 0;

        for(int j=0;j<PosList.size();j++) {
            if (PosList.get(j).y() == col) {
                for (int i = 1; i < PosList.size() - 1; i++) {
                    if ((PosList.get(i + 1).x() - PosList.get(i).x() == 1) && (PosList.get(i + 1).y() - PosList.get(i).y() == 0)) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public int checkMoveDiagonal(List<Position> PosList){

        int count = 0;

        List<Position> diagonal1 = new ArrayList<>();
        diagonal1.add(new Position(0,0));
        diagonal1.add(new Position(1,1));
        diagonal1.add(new Position(2,2));
        diagonal1.add(new Position(3,3));

        List<Position> diagonal2 = new ArrayList<>();
        diagonal2.add(new Position(0,1));
        diagonal2.add(new Position(1,2));
        diagonal2.add(new Position(2,3));

        List<Position> diagonal3 = new ArrayList<>();
        diagonal3.add(new Position(1,0));
        diagonal3.add(new Position(2,1));
        diagonal3.add(new Position(3,2));
        diagonal3.add(new Position(4,3));

        List<Position> diagonal4 = new ArrayList<>();
        diagonal4.add(new Position(2,0));
        diagonal4.add(new Position(3,1));
        diagonal4.add(new Position(4,2));

        List<Position> diagonal5= new ArrayList<>();
        diagonal5.add(new Position(0,2));
        diagonal5.add(new Position(1,1));
        diagonal5.add(new Position(2,0));

        List<Position> diagonal6 = new ArrayList<>();
        diagonal6.add(new Position(0,3));
        diagonal6.add(new Position(1,2));
        diagonal6.add(new Position(2,1));
        diagonal6.add(new Position(3,0));

        List<Position> diagonal7 = new ArrayList<>();
        diagonal7.add(new Position(1,3));
        diagonal7.add(new Position(2,2));
        diagonal7.add(new Position(3,1));
        diagonal7.add(new Position(4,0));

        List<Position> diagonal8= new ArrayList<>();
        diagonal8.add(new Position(2,3));
        diagonal8.add(new Position(3,2));
        diagonal8.add(new Position(4,1));



        if(containsAny(PosList,diagonal1)==3){
            count = containsAny(PosList,diagonal1);
        }
        else if(containsAny(PosList,diagonal2)==3){
            count = containsAny(PosList,diagonal2);
        }
        else if(containsAny(PosList,diagonal3)==3){
            count = containsAny(PosList,diagonal3);
        }
        else if(containsAny(PosList,diagonal4)==3){
            count = containsAny(PosList,diagonal4);
        }
        else if(containsAny(PosList,diagonal5)==3){
            count = containsAny(PosList,diagonal5);
        }
        else if(containsAny(PosList,diagonal6)==3){
            count = containsAny(PosList,diagonal6);
        }
        else if(containsAny(PosList,diagonal7)==3){
            count = containsAny(PosList,diagonal7);
        }
        else if(containsAny(PosList,diagonal8)==3){
            count = containsAny(PosList,diagonal8);
        }

        return count;
    }

    public int containsAny(List<Position> l1, List<Position> l2) {
        int count = 0;
        for (Position elem : l1) {
            if (l2.contains(elem)) {
                count++;
            }
        }
        return count;
    }

    private void alterPlayerPhase(){
        players = players.alter();
        hideSelectablePositions();
        setSelectablePositions();
        showSelectablePositions();
    }

    private void alterSelectionPhase() {
        select = select.alter();
        hideSelectablePositions();
        setSelectablePositions();
        showSelectablePositions();
    }

    private void selectPosition(Position position) {
        selected = position;
        showSelectedPosition();
    }

    private void showSelectedPosition() {
        var square = getCircle(selected);
        square.getStyleClass().add("selected");
    }

    private void deselectSelectedPosition() {
        hideSelectedPosition();
        selected = null;
    }

    private void hideSelectedPosition() {
        var square = getCircle(selected);
        square.getStyleClass().remove("selected");
    }

    private void setSelectablePositions() {
        selectPos.clear();
        if(players.name()=="RED_PLAYER"){
            switch (select) {
                case SELECT_FROM -> {
                    for(int i = 0;i < model.getCircleNumber();i++){
                        if(getCircleColor(i) == "RED"){
                            selectPos.add(model.getCirclePosition(i));
                        }
                    }

                }
                case SELECT_TO -> {
                    var pieceNumber = model.getCircleNumber(selected).getAsInt();
                    if(getCircleColor(pieceNumber)=="RED"){
                        for (var direction : model.getValidMoves(pieceNumber)) {
                            selectPos.add(selected.moveTo(direction));
                        }
                    }
                }
            }
        }
        else if(players.name()=="BLUE_PLAYER"){
            switch (select) {
                case SELECT_FROM -> {
                    for(int i = 0;i < model.getCircleNumber();i++){
                        if(getCircleColor(i) == "BLUE"){
                            selectPos.add(model.getCirclePosition(i));
                        }
                    }

                }
                case SELECT_TO -> {
                    var pieceNumber = model.getCircleNumber(selected).getAsInt();
                    if(getCircleColor(pieceNumber)=="BLUE"){
                        for (var direction : model.getValidMoves(pieceNumber)) {
                            selectPos.add(selected.moveTo(direction));
                        }
                    }
                }
            }
        }

    }

    private void showSelectablePositions() {
        for (var selectablePosition : selectPos) {
            var square = getCircle(selectablePosition);
            square.getStyleClass().add("selectable");
        }


    }

    private void hideSelectablePositions() {
        for (var selectablePosition : selectPos) {
            var square = getCircle(selectablePosition);
            square.getStyleClass().remove("selectable");
        }
    }

    private StackPane getCircle(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.x() && GridPane.getColumnIndex(child) == position.y()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void circlePositionChange(ObservableValue<? extends Position> observable, Position oldPosition, Position newPosition) {
        StackPane oldSquare = getCircle(oldPosition);
        StackPane newSquare = getCircle(newPosition);
        newSquare.getChildren().addAll(oldSquare.getChildren());
        oldSquare.getChildren().clear();
    }

}

