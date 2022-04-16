package game.model;

public enum CircleDirection implements Direction{

    FEL(0,1),
    LE(0,-1),
    JOBBRA(1,0),
    BALRA(-1,0),
    FEL_BALRA(-1,1),
    FEL_JOBBRA(1,1),
    LE_BALRA(-1,-1),
    LE_JOBBRA(1,-1);

    private final int rowChange;
    private final int colChange;


    CircleDirection(int x, int y) {
        rowChange=x;
        colChange=y;
    }

    @Override
    public int getRowChange() {
        return rowChange;
    }

    @Override
    public int getColChange() {
        return colChange;
    }

    public static CircleDirection of(int x, int y){
        for(var direction: values()){
            if(direction.rowChange==x && direction.colChange==y){
                return direction;
            }
        }
        throw new IllegalArgumentException();
    }

    public static void main(String[] args){
        System.out.println(of(1,0));
    }
}
