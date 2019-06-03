import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JPanel implements MouseListener {

    private Draughts draughts;
    private boolean can_switch_token;
    private BufferedImage image_yellow_king, image_black_king;

    public Main(Draughts draughts) {
        super();
        this.draughts = draughts;
        this.can_switch_token = true;
        try{
            this.image_yellow_king = ImageIO.read(new File("icons/yellow_king.png"));
            this.image_black_king = ImageIO.read(new File("icons/black_king.png"));
        }catch (IOException e){
            System.out.println("Missing icons");
        }
        addMouseListener(this);

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D graphics = (Graphics2D)g;

        int width = getWidth();
        int height = getHeight();

        graphics.setColor(Color.LIGHT_GRAY);
        int board_size = (width < height? width: height) - GUI.BORDER_WIDTH * 2;
        graphics.fillRect(width / 2 - board_size / 2, height / 2 - board_size / 2, board_size, board_size);

        //draws chess board
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                int x_shift = j * board_size / 8;
                int y_shift = i * board_size / 8;

                if ((i + j) % 2 == 0){
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect((width - board_size) / 2 + x_shift, (height - board_size) / 2 + y_shift, board_size / 8, board_size / 8);
                }
            }
        }

        //draws which player has active turn, indicating it with small circle on players side of the field
        graphics.setColor(draughts.getPlayer().getTokenColor());
        if (draughts.getPlayer().isFirst()){
            graphics.fillOval((width - board_size) / 2 - board_size / 20, (height - board_size) / 2 , board_size / 20, board_size / 20);
        }
        else{
            graphics.fillOval((width - board_size) / 2 - board_size / 20 , (height + board_size) / 2 - board_size / 20, board_size / 20, board_size / 20);
        }

        //calculates all the tokens that can move this round
        ArrayList<Field> possible_tokens = validTokens();
        if (validTokens().size() == 0){
            draughts.endGame();
        }
        //If both players have only one token and that token is king, the game automaticly stops and declares a draw match.
        if (draughts.getPlayer().getTokens().size() == 1 && draughts.getOtherPlayer().getTokens().size() == 1 && draughts.getPlayer().getTokens().get(0).isKing() && draughts.getOtherPlayer().getTokens().get(0).isKing()){
            draughts.drawGame();
        }

        //This part generates all the possible paths for each token, to force the player to choose the one that has most jumps
        ArrayList<ArrayList<Field>> best_paths = new ArrayList<>();
        int best_lenght = 0;
        for (Field field: possible_tokens){
            ArrayList<ArrayList<Field>> start_poz = new ArrayList<>();
            ArrayList<Field> start = new ArrayList<>();
            start.add(field);
            start_poz.add(start);
            ArrayList<ArrayList<Field>> posible_paths = bestPaths(start_poz);
            for (ArrayList<Field> one_path : posible_paths) {
                if (pathDistance(one_path) > best_lenght) {
                    best_lenght = pathDistance(one_path);
                    best_paths = new ArrayList<>();
                    best_paths.add(one_path);
                }
                else if (pathDistance(one_path) == best_lenght){
                    best_paths.add(one_path);
                }
            }
        }

        //Draws circles under all tokens that are viable for player to move on the active round
        for (ArrayList<Field> single_path : best_paths) {
            int x_position = (width - board_size) / 2 + single_path.get(0).getColumn() * board_size / 8;
            int y_position = (height - board_size) / 2 + single_path.get(0).getRow() * board_size / 8;
            graphics.setColor(Color.GREEN);
            graphics.fillOval(x_position, y_position, board_size / 8, board_size / 8);
        }

        //draws the tokens of both players
        for (Player player: draughts.getPlayers()){

            //If players chooses a token, it draws a yellow ring around the chosen token
            if (player.getToggled_token() != null){
                int x_position = (width - board_size) / 2 + player.getToggled_token().getColumn() * board_size / 8;
                int y_position = (height - board_size) / 2 + player.getToggled_token().getRow() * board_size / 8;
                graphics.setColor(new Color(255, 215, 0));
                graphics.fillOval(x_position, y_position, board_size / 8 , board_size / 8);
            }
            graphics.setColor(player.getTokenColor());
            //draws the tokens
            for (Token token: player.getTokens()){
                int x_position = (width - board_size) / 2 + token.getColumn() * board_size / 8;
                int y_position = (height - board_size) / 2 + token.getRow() * board_size / 8;
                graphics.fillOval(x_position + board_size / 160, y_position + board_size / 160, board_size / 8 - board_size / 80, board_size / 8 - board_size / 80);
                if (token.isKing()){
                    graphics.drawImage((player.isFirst()? image_yellow_king: image_black_king).getScaledInstance(board_size / 8 - 16, board_size / 8 - 16, Image.SCALE_SMOOTH), x_position + 8, y_position + 8 , null);
                }
            }

        }
        //This part is when the game is over or draw
        if (draughts.isEnd_game() || draughts.isDraw_game()){
            graphics.setColor(new Color(125, 125, 125, 128));
            graphics.fillRect(0, 0, width, height);

            graphics.setFont(new Font(Font.SANS_SERIF, Font.BOLD, board_size / 20));
            FontMetrics metrics = graphics.getFontMetrics();
            String victory_message;

            if (draughts.isEnd_game()) {
                victory_message = draughts.getOtherPlayer().getName() + " player has won!";
            }
            else{
                victory_message = "Draw Game";
            }
            RoundRectangle2D rect = new RoundRectangle2D.Float(width / 2 - (board_size - 50) / 2, height / 3, board_size - 50, height / 3, 20, 20);
            graphics.setColor(Color.WHITE);
            graphics.fill(rect);
            graphics.setColor(Color.BLACK);
            graphics.draw(rect);

            graphics.drawString(victory_message, (width - metrics.stringWidth(victory_message))/ 2, (height - GUI.BORDER_WIDTH + metrics.getHeight()) / 2);

        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

        //first find on which square is clicked
        int width = getWidth();
        int height = getHeight();
        int board_size = (width < height? width: height) - GUI.BORDER_WIDTH * 2;
        int x = e.getX();
        int y = e.getY();
        int chosen_row = (int)(4 + (y - height / 2.0) / (board_size / 8.0));
        int chosen_column = (int)(4 + (x - width / 2.0) / (board_size / 8.0));
        Field old_field = draughts.getPlayer().getToggled_token();
        Field next_field = new Field(chosen_row, chosen_column);

        ArrayList<Field> possible_tokens = validTokens();

        //calculates all possible paths for all valid tokens (that can move)
        ArrayList<ArrayList<Field>> best_paths = new ArrayList<>();
        int best_lenght = 0;
        for (Field field: possible_tokens){
            ArrayList<ArrayList<Field>> start_poz = new ArrayList<>();
            ArrayList<Field> start = new ArrayList<>();
            start.add(field);
            start_poz.add(start);
            ArrayList<ArrayList<Field>> posible_paths = bestPaths(start_poz);
            for (ArrayList<Field> one_path : posible_paths) {
                if (pathDistance(one_path) > best_lenght) {
                    best_lenght = pathDistance(one_path);
                    best_paths = new ArrayList<>();
                    best_paths.add(one_path);
                }
                else if (pathDistance(one_path) == best_lenght){
                    best_paths.add(one_path);
                }
            }
        }

        possible_tokens = new ArrayList<>();
        for (ArrayList<Field> single_path : best_paths) {
            possible_tokens.add(single_path.get(0));
        }

        if (draughts.getPlayer().getToggled_token() != null){
            //checks if the move is valid and the move is in the best path possible (rules of draughts)
            if (isValidMove(old_field, next_field, best_paths)) {
                draughts.getPlayer().moveToken(old_field, next_field);
                if (next_field.getRow() == 7 && draughts.getPlayer().isFirst() || next_field.getRow() == 0 && !draughts.getPlayer().isFirst()){
                    draughts.getPlayer().getTokenOnField(next_field).setKing(true);
                }
                //checks if the move is normal or if he is attempting to eat enemys token
                switch (moveDistance(old_field, next_field)){
                    case 1:{
                        //normal move
                        draughts.togglePlayer();
                        draughts.getOtherPlayer().setToggled_token(null);
                        break;
                    }
                    case 2:{
                        //eat move
                        Field eaten_field = new Field((old_field.getRow() + next_field.getRow()) / 2, (old_field.getColumn() + next_field.getColumn()) / 2);
                        draughts.getOtherPlayer().removeToken(eaten_field);
                        //if player can eat more with that token the game stays on his turn
                        if (canEatMore(next_field).size() == 0){

                            draughts.togglePlayer();
                            draughts.getOtherPlayer().setToggled_token(null);
                            can_switch_token = true;
                        }
                        else{
                            draughts.getPlayer().setToggled_token(next_field);
                            can_switch_token = false;
                        }
                        break;
                    }
                }
            }
        }
        //Checks if he clicked (toggled) a token that is valid
        if (draughts.getPlayer().isHisToken(next_field) && can_switch_token && is_token_choosable(next_field, possible_tokens)){
            draughts.getPlayer().setToggled_token(next_field);
            draughts.getOtherPlayer().setToggled_token(null);
        }
        //If player runs out of tokens he losses the game
        if (draughts.getPlayer().hasLost()){
            draughts.endGame();
        }
        //repaints the board
        draughts.draw();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    //checks if there is a token on given field
    private boolean isThereToken(Field field){
        return (draughts.getPlayer().isHisToken(field) || draughts.getOtherPlayer().isHisToken(field));
    }

    //checks if the move is passing al the draughts rules
    private boolean isValidMove(Field old_field, Field next_field, ArrayList<ArrayList<Field>> alowed_moves){
        Token token = draughts.getPlayer().getTokenOnField(old_field);
        boolean next_field_in_path = false;
        for (ArrayList<Field> one_move : alowed_moves)
            if (old_field.getRow() == one_move.get(0).getRow() && old_field.getColumn() == one_move.get(0).getColumn() && next_field.getRow() == one_move.get(1).getRow() && next_field.getColumn() == one_move.get(1).getColumn()) {
                next_field_in_path = true;
                break;
            }

        if (!fieldOnBoard(next_field) || !next_field_in_path){
            return false;
        }
        //Depends on which move is currently in progress
        switch (moveDistance(old_field, next_field)){
            case 1:{
                //normal move
                if (token.isKing()){
                    //king move
                    return  !(!isMoveDiagonal(old_field, next_field) || isThereToken(next_field));
                }
                else{
                    //man move
                    return !(!isMoveDiagonal(old_field, next_field) || !isMoveForward(old_field, next_field) || isThereToken(next_field));
                }
            }
            case 2:{
                //eat move
                Field eaten_field = new Field((old_field.getRow() + next_field.getRow()) / 2, (old_field.getColumn() + next_field.getColumn()) / 2);
                if (token.isKing()){
                    //king move
                    return !(!isMoveDiagonal(old_field, next_field) || isThereToken(next_field) || !isThereToken(eaten_field) || draughts.getPlayer().isHisToken(eaten_field));
                }
                else{
                    //man move
                    return !(!isMoveDiagonal(old_field, next_field) || !isMoveForward(old_field, next_field) || isThereToken(next_field) || !isThereToken(eaten_field) || draughts.getPlayer().isHisToken(eaten_field));
                }
            }
            default: return false;

        }
    }

    //checks if the token is one of the valid one (tokens with most possible moves in one turn)
    private boolean is_token_choosable(Field wanted, ArrayList<Field> all_posibilities){
        for (Field singe : all_posibilities){
            if (wanted.getRow() == singe.getRow() && wanted.getColumn() == singe.getColumn()){
                return true;
            }
        }
        return false;
    }

    //checks if the tokens has moved diagonally
    private boolean isMoveDiagonal(Field old_field, Field next_field){
        return (Math.abs(old_field.getColumn() - next_field.getColumn()) == Math.abs(old_field.getRow() - next_field.getRow()));
    }

    //calculates the move distance
    private int moveDistance(Field old_field, Field next_field){
        return (Math.abs(old_field.getRow() - next_field.getRow()));
    }

    //calculates distance of all the jumps in the path
    private int pathDistance(ArrayList<Field> path){
        int full_distance = 0;
        for (int i = 0; i < path.size() - 1; i ++){
            full_distance += moveDistance(path.get(i), path.get(i + 1));
        }
        return full_distance;
    }

    //Checks if the move is going forward (only for man(normal) tokens)
    private boolean isMoveForward(Field old_fieldn, Field next_field){
        if (draughts.getPlayer().isFirst()){
            return (old_fieldn.getRow() - next_field.getRow() < 0);
        }
        else{
            return (old_fieldn.getRow() - next_field.getRow() > 0);
        }

    }

    //checks if the chosen field is still on board
    private boolean fieldOnBoard(Field field){
        return (field.getRow() >= 0 && field.getRow() < 8 && field.getColumn() >= 0 && field.getColumn() < 8);
    }

    //Checks if token on this field can make a normal move this turn
    private ArrayList<Field> canMove(Field field) {
        Token active_token = draughts.getPlayer().getTokenOnField(field);
        ArrayList<Field> all_possible_fields = new ArrayList<>();
        if (active_token.isKing()) {
            //Check all diagonals
            if (!isThereToken(new Field(field.getRow() - 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() - 1, field.getColumn() - 1))){
                all_possible_fields.add(new Field(field.getRow() - 1, field.getColumn() - 1));
            }
            if (!isThereToken(new Field(field.getRow() - 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() - 1, field.getColumn() + 1))){
                all_possible_fields.add(new Field(field.getRow() - 1, field.getColumn() + 1));
            }
            if (!isThereToken(new Field(field.getRow() + 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() + 1, field.getColumn() - 1))){
                all_possible_fields.add(new Field(field.getRow() + 1, field.getColumn() - 1));
            }
            if (!isThereToken(new Field(field.getRow() + 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() + 1, field.getColumn() + 1))){
                all_possible_fields.add(new Field(field.getRow() + 1, field.getColumn() + 1));
            }
            return all_possible_fields;

        } else {
            //check forward diagonals
            if (active_token.isPlayer()) {
                //Black token
                if (!isThereToken(new Field(field.getRow() - 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() - 1, field.getColumn() - 1))){
                    all_possible_fields.add(new Field(field.getRow() - 1, field.getColumn() - 1));
                }
                if (!isThereToken(new Field(field.getRow() - 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() - 1, field.getColumn()+ 1))){
                    all_possible_fields.add(new Field(field.getRow() - 1, field.getColumn() + 1));
                }
                return all_possible_fields;
            } else {
                //White token
                if (!isThereToken(new Field(field.getRow() + 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() + 1, field.getColumn() - 1))){
                    all_possible_fields.add(new Field(field.getRow() + 1, field.getColumn() - 1));
                }
                if (!isThereToken(new Field(field.getRow() + 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() +1, field.getColumn() + 1))){
                    all_possible_fields.add(new Field(field.getRow() + 1, field.getColumn() + 1));
                }
                return all_possible_fields;
            }
        }
    }

    //Checks if token on this field can make a eat move this turn
    private ArrayList<Field> canEatMore(Field field){
        Token active_token = draughts.getPlayer().getTokenOnField(field);
        ArrayList<Field> all_possible_fields = new ArrayList<>();
        if (active_token.isKing()){
            //Check all diagonals
            if (isThereToken(new Field(field.getRow() - 1, field.getColumn() - 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() - 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() - 2, field.getColumn() - 2)) && !isThereToken(new Field(field.getRow() - 2, field.getColumn() - 2))){
                all_possible_fields.add(new Field(field.getRow() - 2, field.getColumn() - 2));
            }
            if (isThereToken(new Field(field.getRow() - 1, field.getColumn() + 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() - 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() - 2, field.getColumn() + 2)) && !isThereToken(new Field(field.getRow() - 2, field.getColumn() + 2))){
                all_possible_fields.add(new Field(field.getRow() - 2, field.getColumn() + 2));
            }
            if (isThereToken(new Field(field.getRow() + 1, field.getColumn() - 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() + 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() + 2, field.getColumn() - 2)) && !isThereToken(new Field(field.getRow() + 2, field.getColumn() - 2))){
                all_possible_fields.add(new Field(field.getRow() + 2, field.getColumn() - 2));
            }
            if (isThereToken(new Field(field.getRow() + 1, field.getColumn() + 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() + 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() +2, field.getColumn() + 2)) && !isThereToken(new Field(field.getRow() + 2, field.getColumn() + 2))){
                all_possible_fields.add(new Field(field.getRow() + 2, field.getColumn() + 2));
            }
            return all_possible_fields;

        }
        else{
            //check forward diagonals
            if (active_token.isPlayer()){
                //Black token
                if (isThereToken(new Field(field.getRow() - 1, field.getColumn() - 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() - 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() - 2, field.getColumn() - 2)) && !isThereToken(new Field(field.getRow() - 2, field.getColumn() - 2))){
                    all_possible_fields.add(new Field(field.getRow() - 2, field.getColumn() - 2));
                }
                if (isThereToken(new Field(field.getRow() - 1, field.getColumn() + 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() - 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() - 2, field.getColumn() + 2)) && !isThereToken(new Field(field.getRow() - 2, field.getColumn() + 2))){
                    all_possible_fields.add(new Field(field.getRow() - 2, field.getColumn() + 2));
                }
                return all_possible_fields;
            }
            else{
                //White token
                if (isThereToken(new Field(field.getRow() + 1, field.getColumn() - 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() + 1, field.getColumn() - 1)) && fieldOnBoard(new Field(field.getRow() + 2, field.getColumn() - 2)) && !isThereToken(new Field(field.getRow() + 2, field.getColumn() - 2))){
                    all_possible_fields.add(new Field(field.getRow() + 2, field.getColumn() - 2));
                }
                if (isThereToken(new Field(field.getRow() + 1, field.getColumn() + 1)) && draughts.getOtherPlayer().isHisToken(new Field(field.getRow() + 1, field.getColumn() + 1)) && fieldOnBoard(new Field(field.getRow() + 2, field.getColumn() + 2)) && !isThereToken(new Field(field.getRow() + 2, field.getColumn() + 2))){
                    all_possible_fields.add(new Field(field.getRow() + 2, field.getColumn() + 2));
                }
                return all_possible_fields;
            }
        }
    }

    //Checks and returns all the valid tokens for possible moves in active round
    private ArrayList<Field> validTokens() {
        ArrayList<Field> valid_tokens = new ArrayList<>();
        for (Token token : draughts.getPlayer().getTokens()) {
            Field tokens_field = new Field(token.getRow(), token.getColumn());
            if ((canMove(tokens_field).size() > 0) || canEatMore(tokens_field).size() > 0) {
                valid_tokens.add(tokens_field);
            }
        }
        return valid_tokens;
    }

    //Checks all possible moves for each token and returns all paths in list of list of paths
    private ArrayList<ArrayList<Field>> bestPaths(ArrayList<ArrayList<Field>> allPaths) {
        boolean can_do_smth = false;
        ArrayList<ArrayList<Field>> new_paths = new ArrayList<>();
        //for first turn when the function only gets tokens field in double arrays
        if (allPaths.get(0).size() == 1) {
            Field current_field = allPaths.get(0).get(0);
            for (Field eat_field : canEatMore(current_field)) {
                ArrayList<Field> one_path = new ArrayList<>();
                one_path.add(current_field);
                one_path.add(eat_field);
                new_paths.add(one_path);
                can_do_smth = true;
            }
            //if the token can eat something there is no need to check if he can just normally move
            if (!can_do_smth) {

                for (Field move_field : canMove(current_field)) {
                    ArrayList<Field> one_path = new ArrayList<>();
                    one_path.add(current_field);
                    one_path.add(move_field);
                    new_paths.add(one_path);
                }
                return new_paths;
            }

        }
        else{
            for (ArrayList<Field> one_path : allPaths){

                Field last_position = one_path.get(one_path.size() - 1);

                draughts.getPlayer().moveToken(one_path.get(0), last_position);

                for (Field eat_field : canEatMore(last_position)){
                    ArrayList<Field> temp_path = new ArrayList<>(one_path);
                    found: { //That we can brake if he starts to spin in circles

                        if (eat_field.getRow() == one_path.get(one_path.size() - 2).getRow() && eat_field.getColumn() == one_path.get(one_path.size() - 2).getColumn()){
                            break found;
                        }
                        for (int i = 0; i < one_path.size() - 2; i++) {
                            //Checks if he already made this move (because its still viable because we don't remove tokens if he eats them
                            if ((one_path.get(i).getRow() == last_position.getRow() && one_path.get(i).getColumn() == last_position.getColumn() && one_path.get(i + 1).getRow() == eat_field.getRow() && one_path.get(i + 1).getColumn() == eat_field.getColumn())) {
                                break found;
                            }
                        }
                        temp_path.add(eat_field);
                        new_paths.add(temp_path);
                        can_do_smth = true;
                        draughts.getPlayer().moveToken(one_path.get(one_path.size() - 2), one_path.get(0));

                    }
                }
                draughts.getPlayer().moveToken(one_path.get(one_path.size() - 1), one_path.get(0));
            }
        }
        //checks if there is need for recursively check next moves (in case he can eat something)
        if (can_do_smth){
            return bestPaths(new_paths);
        }
        return allPaths;
    }

}