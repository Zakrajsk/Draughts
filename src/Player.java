import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Player {

    private List<Token> tokens;

    private boolean first;

    private Color token_color;

    private Field toggled_token=null;

    private String name;

    public Player(boolean first){
        this(first? GUI.PLAYER_ONE_COLOR: GUI.PLAYER_TWO_COLOR, first);

    }

    public Player(Color token_color, boolean first){
        super();
        this.first = first;
        this.tokens = resetTokens(first);
        this.token_color = token_color;
        this.name = first? "Red": "Black";

    }

    private List<Token> resetTokens(boolean first){
        List<Token> all_tokens = new LinkedList<>();

        if (first){
            for (int i = 0; i < 3; i++){
                for (int j = 0; j < 8; j++){
                    if ((i + j) % 2 != 0){
                        all_tokens.add(new Token(i, j, false, false));
                    }
                }
            }
        }
        else{
            for (int i = 5; i < 8; i++){
                for (int j = 0; j < 8; j++){
                    if ((i + j) % 2 != 0) {
                        all_tokens.add(new Token(i, j, true, false));
                    }
                }
            }
        }
        return all_tokens;
    }

    Color getTokenColor() {
        return token_color;
    }

    List<Token> getTokens() {
        return tokens;
    }

    boolean isFirst() {
        return first;
    }

    void setToggled_token(Field toggled_token) {
        this.toggled_token = toggled_token;
    }

    Field getToggled_token() {
        return toggled_token;
    }

    String getName() {
        return name;
    }

    boolean hasLost(){
        return (tokens.size() == 0);
    }

    Token getTokenOnField(Field field){
        for (Token token: tokens) {
            if (field.getRow() == token.getRow() && field.getColumn() == token.getColumn()) {
                return token;
            }
        }
        return null;
    }

    boolean isHisToken(Field field){
        for (Token token: tokens){
            if (field.getRow() == token.getRow() && field.getColumn() == token.getColumn()){
                return true;
            }
        }
        return false;
    }

    void moveToken(Field old_field, Field new_field){
        for (Token token : tokens) {
            if (token.getRow() == old_field.getRow() && token.getColumn() == old_field.getColumn()) {
                token.setRow(new_field.getRow());
                token.setColumn(new_field.getColumn());
                return;
            }
        }
    }

    void removeToken(Field field){
        for (int i = 0; i < tokens.size(); i++){
            if (tokens.get(i).getRow() == field.getRow() && tokens.get(i).getColumn() == field.getColumn()){
                tokens.remove(i);
                break;
            }
        }
    }
}
