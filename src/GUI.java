import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    public static final long serialVersionUID = 1L;

    static final int BORDER_WIDTH = 20;

    static final Color PLAYER_ONE_COLOR = Color.RED;

    static final Color PLAYER_TWO_COLOR = Color.BLACK;

    private Draughts draughts;

    public GUI(Draughts draughts){
        super();

        this.draughts = draughts;

        setTitle("Draughts");
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new Main(draughts), BorderLayout.CENTER);

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);

        JMenu menu = new JMenu("Game");
        bar.add(menu);

        JMenuItem item = new JMenuItem("New Game");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                draughts.newGame();
                draughts.draw();
            }
        });
        menu.add(item);

        item = new JMenuItem("Next Player");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                draughts.togglePlayer();
                draughts.draw();
            }
        });
        menu.add(item);


    }
}
