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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.util.Fonts;

/**
 * Created by Joseph on 15/02/2017.
 */
public class GenerationOverlay extends Overlay {

    private Fonts fonts;

    private Label energyYield;
    private Label oreYield;
    private Label foodYield;

    public GenerationOverlay(Color fillColor, Color lineColor, int lineThickness) {
        super(fillColor, lineColor, 330, 120, lineThickness);

        fonts = new Fonts();
        fonts.montserratRegular.setSize(24);
        fonts.montserratLight.setSize(16);

        energyYield = new Label("", new Label.LabelStyle(fonts.montserratLight.font(), Color.WHITE));
        oreYield = new Label("", new Label.LabelStyle(fonts.montserratLight.font(), Color.WHITE));
        foodYield = new Label("", new Label.LabelStyle(fonts.montserratLight.font(), Color.WHITE));

        table().add(new Label("GENERATED RESOURCES", new Label.LabelStyle(fonts.montserratRegular.font(), Color.WHITE))).padBottom(15).colspan(2);

        table().row();
        table().add(new Label("Energy", new Label.LabelStyle(fonts.montserratLight.font(), Color.WHITE)));
        table().add(energyYield);

        table().row();
        table().add(new Label("Ore", new Label.LabelStyle(fonts.montserratLight.font(), Color.WHITE)));
        table().add(oreYield);

        table().row();
        table().add(new Label("Food", new Label.LabelStyle(fonts.montserratLight.font(), Color.WHITE)));
        table().add(foodYield);
    }

    public void updateYieldLabels(Integer energy, Integer ore, Integer food) {
        energyYield.setText("+" + energy.toString());
        oreYield.setText("+" + ore.toString());
        foodYield.setText("+" + food.toString());
    }
}
