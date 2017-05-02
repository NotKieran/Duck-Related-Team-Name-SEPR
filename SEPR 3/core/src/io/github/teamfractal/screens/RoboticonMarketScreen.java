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
import io.github.teamfractal.actors.RoboticonMarketActors;

/**
 * Created by Joseph on 15/02/2017.
 */
public class RoboticonMarketScreen extends Overlay {

    /**
     * The actors that will populate the roboticon market
     */
    private RoboticonMarketActors actors;

    /**
     * Constructs the roboticon market's interface in the space of an overlay using the actors defined and instantiated
     * in the RoboticonMarketActors class
     *
     * @param game The game's engine
     */
    public RoboticonMarketScreen(RoboticonQuest game) {
        super(Color.GRAY, Color.WHITE, 400, 400, 0, 45, 3);
        //Construct the interface's space

        actors = new RoboticonMarketActors(game);
        table().add(actors);
        //Add the market's actors to that space
    }

    /**
     * Returns the RoboticonMarketActors table that forms the core of this interface
     *
     * @return RoboticonMarketActors The market screen's actors
     */
    public RoboticonMarketActors actors() {
        return actors;
    }
}
