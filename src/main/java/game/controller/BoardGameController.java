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

        for(int i=0;i<4;i++){
            col[i]=checkMoveCol(i,RedPos);
        }

        for(int i=0;i<4;i++){
            diagonal[i]=checkMoveDiagonal(RedPos);
        }


        for(var s:row){
            if(s==3){
                Logger.info("RED WINS");
            }
            Logger.info("Sor: "+s);
        }

        for(var s:col){
            if(s==3){
                Logger.info("RED WINS");
            }
            Logger.info("Oszlop: "+s);
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
        int[] rowArray = new int[5];
        for(int i=0;i<PosList.size();i++){
            if (PosList.get(i).x() == row && PosList.get(i).y() < 4) {
                if (PosList.get(2).y() - PosList.get(0).y() != 3) {
                    count++;
                    rowArray[i] = count;
                }
            }
        }


        return count;
    }

    public int checkMoveCol(int col,List<Position> PosList){
        int count = 0;
        int[] colArray = new int[4];
        for(int i=0; i<PosList.size();i++){
            if(PosList.get(i).x()<=4 && PosList.get(i).y() ==col){
                    count++;
                    colArray[i] = count;

            }
        }

        return count;
    }

    public int checkMoveDiagonal(List<Position> PosList){
        int count=0;

        if((PosList.get(1).x()-PosList.get(0).x()<=1 && PosList.get(2).x()-PosList.get(1).x()==1 &&
                PosList.get(3).x()-PosList.get(2).x()==1) && (PosList.get(1).x() != PosList.get(0).x() && PosList.get(1).y() != PosList.get(0).y())){
            count++;
        }
        else if(PosList.get(2).x()-PosList.get(1).x()==1 && PosList.get(3).x()-PosList.get(2).x()==1){
            count++;
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

