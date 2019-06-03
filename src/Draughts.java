public class Draughts {

    private boolean first;

    private boolean end_game;

    private boolean draw_game;

    private Player[] players;

    private GUI GUI;

    public Draughts(){
        super();

        first = true;
        end_game = false;
        draw_game = false;
        players = new Player[] {new Player(true), new Player(false)};

        GUI = new GUI(this);
        GUI.setVisible(true);
    }

    private void play(){
    }

    Player[] getPlayers() {
        return players;
    }

    Player getPlayer(){
        return players[first? 0: 1];
    }

    Player getOtherPlayer(){
        return players[first? 1: 0];
    }

    boolean isEnd_game() {
        return end_game;
    }

    boolean isDraw_game(){
        return draw_game;
    }

    void togglePlayer(){
        first = !first;
    }

    void draw(){
        GUI.repaint();
    }

    void newGame(){
        first = true;
        end_game = false;
        draw_game = false;
        players = new Player[] {new Player(true), new Player(false)};
    }

    void drawGame(){
        draw_game = true;
    }

    void endGame(){
        end_game = true;
    }

    public static void main(String[] args) {
        new Draughts().play();
    }
}