/**
 * @author DRTN
 * Team Website with download:
 * https://misterseph.github.io/DuckRelatedFractalProject/
 *
 * This Class contains either modifications or is entirely new in Assessment 3
 *
 * If you are in any doubt a complete changelog can be found here:
 * https://github.com/NotKieran/DRTN-Fractal/compare/Fractal_Initial...development
 *
 * And a more concise report can be found in our Change3 document.
 **/

package io.github.teamfractal.screens;

import com.badlogic.gdx.graphics.Color;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.actors.ResourceMarketActors;

import java.util.Random;

/**
 * Created by Joseph on 17/02/2017.
 */
public class ResourceMarketScreen extends Overlay {

    /**
     * The game's engine
     */
    private RoboticonQuest game;

    /**
     * The actors that will populate the market
     */
    private ResourceMarketActors actors;

    /**
     * Tracks the total amount of money won by the current player while taking gambles during the current turn
     */
    private int gambleMoneyWonCounter;

    /**
     * Tracks the total amount of money lost by the current player while taking gambles during the current turn
     */
    private int gambleMoneyLostCounter;

    /**
     * Tracks the difference between the numbers of wins and losses the current player experiences while gambling in
     * the current turn
     */
    private int gambleWinLossCounter;

    /**
     * Constructs the market's interface in the space of an overlay using the actors defined and instantiated in the
     * ResourceMarketActors class
     *
     * @param game The game's engine
     */
    public ResourceMarketScreen(RoboticonQuest game) {
        super(Color.GRAY, Color.WHITE, 520, 395, 0, 45, 3);
        //Construct the interface's space

        this.game = game;
        //Import the game's engine to facilitate gambling

        actors = new ResourceMarketActors(game);
        table().add(actors);
        //Add the market's actors to that space

        gambleStatisticsReset();
        //Reset the market's gambling statistics in preparation for the first player's arrival at the market
    }

    /**
     * Returns the ResourceMarketActors table that forms the core of this interface
     *
     * @return ResourceMarketActors The market screen's actors
     */
    public ResourceMarketActors actors() {
        return actors;
    }

    /**
     * Attempts to process a gamble using the amount of money entered into the market's interface by the current player
     * Will only gamble non-empty, valid amounts of money that the current player can actually afford to lose
     */
    public void gamble() {
        int playerRoll;
        int AIRoll;

        if (actors.gambleFieldValue().isEmpty()) {
            actors.setGambleStatusLabel("NO VALUE\nGIVEN", Color.RED);
            //Check to see if the player has actually provided an amount of money to gamble
        } else if (Integer.parseInt(actors.gambleFieldValue()) < 1) {
            actors.setGambleStatusLabel("INVALID VALUE\nGIVEN", Color.RED);
            //Check to see if the player has tried to gamble with no money at all
        } else if (Integer.parseInt(actors.gambleFieldValue()) > game.getPlayer().getMoney()) {
            actors.setGambleStatusLabel("CANNOT AFFORD\nGAMBLE", Color.RED);
            //Check to see if the player can afford to make their specified gamble
        } else {
            Random RNGesus = new Random();
            Random RNGudas = new Random();

            playerRoll = RNGesus.nextInt(6) + 1;
            AIRoll = RNGudas.nextInt(6) + 1;
            //Roll two die

            if (playerRoll == AIRoll) {
                actors.setGambleStatusLabel("YOU\nDREW", Color.YELLOW);
                //Do nothing if both dice produce equal values
            } else if (playerRoll > AIRoll) {
                actors.setGambleStatusLabel("YOU\nWON", Color.GREEN);
                game.getPlayer().setMoney(game.getPlayer().getMoney() + Integer.parseInt(actors.gambleFieldValue()));
                gambleMoneyWonCounter += Integer.parseInt(actors.gambleFieldValue());
                gambleWinLossCounter += 1;
                //If the player's dice produces a higher value, add the amount of money that they gambled on to their
                //current stack of funds
            } else {
                actors.setGambleStatusLabel("YOU\nLOST", Color.RED);
                game.getPlayer().setMoney(game.getPlayer().getMoney() - Integer.parseInt(actors.gambleFieldValue()));
                gambleMoneyLostCounter += Integer.parseInt(actors.gambleFieldValue());
                gambleWinLossCounter -= 1;
                //If the AI's dice produces a higher value, subtract the amount of money that they gambled from their
                //current stack of funds
            }

            actors.setGambleRollLabels(playerRoll, AIRoll);
            actors.setGambleStatisticsLabels(gambleMoneyWonCounter, gambleMoneyLostCounter, gambleWinLossCounter);
            //Update the market interface to reflect the outcomes of the player's latest gamble
        }
    }

    /**
     * Resets the market's gambling statistics and updates the market's interface to reflect this reset
     */
    public void gambleStatisticsReset() {
        gambleMoneyWonCounter = 0;
        gambleMoneyLostCounter = 0;
        gambleWinLossCounter = 0;

        actors.setGambleStatusLabel( "\n ", Color.WHITE);
        actors.setGambleRollLabels("-", "-");
        actors.setGambleStatisticsLabels(0, 0, 0);
    }
}
