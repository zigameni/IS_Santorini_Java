package etf.santorini.sg160664d.gui;
import etf.santorini.sg160664d.board.Alliance;
import etf.santorini.sg160664d.gui.Table.PlayerType;
import etf.santorini.sg160664d.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * GameSetup extends the JDialog class.
 */
public class GameSetup extends JDialog {

    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private JSpinner searchDepthSpinner; //needed when we select the depth for the minimax

    private boolean cancelClicked;
    private boolean okayClicked;
    private boolean windowClosedWithX;
    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";

    GameSetup(final JFrame frame,
              final boolean modal){
        super(frame, modal);
        this.cancelClicked = false;
        this.okayClicked = true;
        this.windowClosedWithX = false;

        final JPanel myPanel = new JPanel(new GridLayout(0,1));
        final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
        final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);

        whiteHumanButton.setActionCommand(HUMAN_TEXT);
        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerButton);
        blackHumanButton.setSelected(true);

        this.getContentPane().add(myPanel);
        myPanel.add(new JLabel("White"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerButton);
        myPanel.add(new JLabel("Black"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerButton);

        myPanel.add(new JLabel("Search"));
        this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(2, 0, Integer.MAX_VALUE, 1));

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("Ok");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whitePlayerType = whiteComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                blackPlayerType = blackComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
                windowClosedWithX = false;
                cancelClicked = false;
                okayClicked = true;
                GameSetup.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                cancelClicked = true;
                windowClosedWithX = false;
                okayClicked = false;
                GameSetup.this.setVisible(false);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowClosedWithX = true;
                cancelClicked = false;
                okayClicked = false;
                super.windowClosing(e);
            }
        });
        myPanel.add(cancelButton);
        myPanel.add(okButton);
        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }//!- GameSetup_constructor

    void promptUser(){
        setVisible(true);
        repaint();
    }

    boolean isAIPlayer(final Player player){
        if(!(player == null)){
            if(player.getAlliance() == Alliance.WHITE){
                return getWhitePlayerType() == PlayerType.COMPUTER;
            }
            return getBlackPlayerType() == PlayerType.COMPUTER;
        }
        return false;
    }

    PlayerType getWhitePlayerType(){ return this.whitePlayerType; }
    PlayerType getBlackPlayerType(){ return this.blackPlayerType;}

    private JSpinner addLabeledSpinner(final Container c,
                                       final String label,
                                       final SpinnerModel model) {
        final JLabel l = new JLabel(label);
        c.add(l);
        final JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
    return spinner;
    }//!- addLabelSpinner

    /**
     * Getters
     */
    int getSearchDepth(){
        return (Integer)this.searchDepthSpinner.getValue();
    }
    public boolean getWindowClosedWithX(){return this.windowClosedWithX;}
    public boolean getOkayClicked(){return this.okayClicked;}
    public boolean getCancelClicked(){return this.cancelClicked;}
}//!- GameSetup
