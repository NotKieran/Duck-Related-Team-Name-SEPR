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

package io.github.teamfractal.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.github.teamfractal.screens.AbstractAnimationScreen;

/**
 * Created by Joseph on 15/02/2017.
 */
public class AnimationCustomHeader implements IAnimation {

    /**
     * Measures the length of time over which the animation is active
     */
    private float time;

    /**
     * Measures the length of time over which the animation should play out
     */
    private float length;

    /**
     * The text to be displayed in the animation
     */
    private String text;

    /**
     * The font of the text to be displayed in the animation
     */
    private BitmapFont font;

    /**
     * Used internally to manage repeated playbacks of the animation
     */
    private boolean play;

    /**
     * Builds a textual animation that, when played, fades in from the top; remains stationary for a few seconds,
     * and then fades out to disappear until it's played again
     *
     * @param text The text to be displayed in the animation
     * @param font The font of the text to be displayed in the animation
     * @param length The length of time over which the animation should play out
     */
    public AnimationCustomHeader(String text, BitmapFont font, int length) {
        this.text = text;
        //Import the text to be displayed in the animation

        this.font = font;
        //Import the font of the text to be displayed in the animation

        this.length = length;
        //Set the animation's length

        time = 0;
        play = false;
        //Stop the animation from playing as soon as it's added to a screen
    }

    /**
     * Function that maps time [t] to [t^2] when [t less than 1]...
     * ...[t] to 1 when [1 less than t less than (length - 1)]...
     * ...and [t] to [(length - t)^2] when [(length - 1) less than t less than length]
     *
     * Used to calculate opacity levels for the animation's fade-in and fade-out sequences
     *
     * @return float The alpha (opacity) level of the animation
     */
    private float calculateOpacity() {
        if (time < 1) {
            return (float) Math.pow(time, 2);
        } else if (time < length - 1) {
            return 1;
        } else {
            return (float) Math.pow(length - time, 2);
        }
    }

    /**
     * Signals that the animation can play out from a stopped state when called
     */
    public void play() {
        if (play == false) {
            play = true;
        }
    }

    /**
     * Stops the animation from being played and prepares it for potential re-playback
     */
    public void stop() {
        time = 0;
        play = false;
    }

    /**
     * Sets the text to be displayed in the animation
     *
     * @param text The text to be displayed in the animation
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the length of the animation (in seconds)
     *
     * @param length The length of time over which the animation should play out
     */
    public void setLength(float length) {
        this.length = length;
    }

    /**
     * Executes once per frame to drive the animation forward
     *
     * @param delta The amount of time since this method was last called
     * @param batch The rendering pipeline operated to draw the animation's next frame
     */
    @Override
    public boolean tick(float delta, AbstractAnimationScreen screen, Batch batch) {
        if (play == true) {
            time += delta;
            //Update the amount of time recorded since the animation's beginning

            if (time > length) {
                stop();
                return false;
            }
            //Cut this method short and prevent the animation from being drawn if the amount of time since it was
            //last played back exceeds the length of the animation itself

            batch.begin();
            //Activate a rendering pipeline to draw the animation for the next frame

            font.setColor(1, 1, 1, calculateOpacity());
            GlyphLayout GL = new GlyphLayout(font, text);
            //Set up the font for the animation's text based on the amount of time since it was started

            if (time < 1) {
                font.draw(batch, GL, Gdx.graphics.getWidth() / 2 - GL.width / 2, Gdx.graphics.getHeight() - 5 - (((float) Math.pow(time, 2)) * 30));
            } else {
                font.draw(batch, GL, Gdx.graphics.getWidth() / 2 - GL.width / 2, Gdx.graphics.getHeight() - 35);
            }
            //Draws the animation such that it slides in from the top at the beginning and then freezes in place

            batch.end();
            //Stop the rendering pipeline now that it has drawn the animation for the next frame
        }

        return false;
    }


    @Override
    public void setAnimationFinish(IAnimationFinish callback) {

    }

    @Override
    public void callAnimationFinish() {

    }

    @Override
    public void cancelAnimation() {

    }

}
