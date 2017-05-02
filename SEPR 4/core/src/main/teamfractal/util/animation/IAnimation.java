/**
 * @author DRTN
 * Team Website with download:
 * https://nicopinedo.github.io/SEPR4/
 *
 * This Class contains either modifications or is entirely new in Assessment 4
 **/

package teamfractal.util.animation;

import com.badlogic.gdx.graphics.g2d.Batch;
import teamfractal.util.screens.AbstractAnimationScreen;
import teamfractal.util.screens.AbstractAnimationScreen;

public interface IAnimation {
    /**
     * Draw animation on screen.
     *
     * @param delta     Time change since last call.
     * @param screen    The screen to draw on.
     * @param batch     The Batch for drawing stuff.
     * @return          return <code>true</code> if the animation has completed.
     */
    boolean tick(float delta, AbstractAnimationScreen screen, Batch batch);

    /**
     * Set the animation finish callback.
     * @param callback   The callback function.
     */
    void setAnimationFinish(IAnimationFinish callback);
    void callAnimationFinish();
    void cancelAnimation();
	AnimationType getType();

    enum AnimationType {
    	Tile,
	    Overlay
    }
}
