public class Field {

    private int row, column;

    Field(int row, int column){
        this.row = row;
        this.column = column;
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return this.row + " " + this.column;
    }
}