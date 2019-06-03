public class Token {
    private int row, column;
    private boolean king, player;

    Token(int row, int column, boolean player, boolean king){
        this.row = row;
        this.column = column;
        this.king = king;
        this.player = player;
    }

    int getColumn() {
        return column;
    }

    int getRow() {
        return row;
    }

    boolean isPlayer() {
        return player;
    }

    boolean isKing() {
        return king;
    }

    @Override
    public String toString() {
        return row + " " + column + " " + king;

    }

    void setColumn(int column) {
        this.column = column;
    }

    void setRow(int row) {
        this.row = row;
    }

    void setKing(boolean king) {
        this.king = king;
    }
}