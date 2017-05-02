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

package io.github.teamfractal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.enums.PurchaseStatus;
import io.github.teamfractal.entity.enums.ResourceType;

/**
 * Created by Joseph on 17/02/2017.
 */
public class ResourceMarketActors extends Table {

    /**
     * The game's engine
     */
    private RoboticonQuest game;

    /**
     * Provides the spatial framework for the buying/selling widgets presented in the market
     */
    private Table marketTable;

    /**
     * Allows players to select amounts of ore to buy and to buy them
     */
    private final MarketAdjustableActor oreBuyAdjustable;

    /**
     * Allows players to select amounts of ore to sell and to sell them
     */
    private final MarketAdjustableActor oreSellAdjustable;

    /**
     * Allows players to select amounts of energy to buy and to buy them
     */
    private final MarketAdjustableActor energyBuyAdjustable;

    /**
     * Allows players to select amounts of energy to sell and to sell them
     */
    private final MarketAdjustableActor energySellAdjustable;

    /**
     * Allows players to select amounts of food to buy and to buy them
     */
    private final MarketAdjustableActor foodBuyAdjustable;

    /**
     * Allows players to select amounts of food to sell and to sell them
     */
    private final MarketAdjustableActor foodSellAdjustable;

    /**
     * Textbox allowing players to input amounts of money to gamble with
     */
    private final TextField gambleField;

    /**
     * Label indicating the result of the last attempted gamble
     */
    private Label gambleStatusLabel;

    /**
     * Label indicating the number that the player rolled in the last completed gamble
     */
    private Label gamblePlayerRoll;

    /**
     * Label indicating the number that the game's AI rolled in the last completed gamble
     */
    private Label gambleRNGesusRoll;

    /**
     * Label indicating the amount of money that the player won from gambling in the current turn
     */
    private Label gambleMoneyWonLabel;

    /**
     * Label indicating the amount of money that the player lost from gambling in the current turn
     */
    private Label gambleMoneyLostLabel;

    /**
     * Label indicating the difference in wins and losses experienced while gambling in the current turn
     */
    private Label gambleWinLossLabel;

    /**
     * Constructor class that connects the resource market to the internal engine and builds its visual interface
     *
     * @param game The game's engine
     */
    public ResourceMarketActors(final RoboticonQuest game) {
        final int spacing = 20;
        //Defines the amount of space separating each of the three columns (BUY/SELL/GAMBLE) in the market's interface

        this.game = game;
        //Import the game's engine to process attempted transactions

        marketTable = new Table();
        Table gambleTable = new Table();
        //Instantiate the spatial frameworks for the purchasing and gambling areas of the market's interface

        oreBuyAdjustable = createAdjustable(ResourceType.ORE, true);
        oreSellAdjustable = createAdjustable(ResourceType.ORE, false);
        energyBuyAdjustable = createAdjustable(ResourceType.ENERGY, true);
        energySellAdjustable = createAdjustable(ResourceType.ENERGY, false);
        foodBuyAdjustable = createAdjustable(ResourceType.FOOD, true);
        foodSellAdjustable = createAdjustable(ResourceType.FOOD, false);
        //Create the widgets that will allow players to specify amounts of resources to buy and sell, and to perform
        //those transactions

        marketTable.add(new Label("BUY", new Label.LabelStyle(game.headerFontRegular.font(), Color.WHITE))).padRight(spacing).padBottom(5);
        marketTable.add(new Label("SELL", new Label.LabelStyle(game.headerFontRegular.font(), Color.WHITE))).padBottom(5);
        rowWithHeight(10);
        //Row headers

        marketTable.add(oreBuyAdjustable).padRight(spacing);
        marketTable.add(oreSellAdjustable);
        rowWithHeight(10);
        //Add buying/selling widgets for ore

        marketTable.add(energyBuyAdjustable).padRight(spacing);
        marketTable.add(energySellAdjustable);
        rowWithHeight(10);
        //Add buying/selling widgets for energy

        marketTable.add(foodBuyAdjustable).padRight(spacing);
        marketTable.add(foodSellAdjustable);
        rowWithHeight(20);
        //Add buying/selling widgets for food

        add(marketTable).padRight(spacing);
        //Add buying/selling region to the market's interface

        gambleTable.add(new Label("PUB", new Label.LabelStyle(game.headerFontRegular.font(), Color.WHITE))).padBottom(10).colspan(2);
        gambleTable.row();
        //Row header for gambling area

        gambleField = new TextField("", game.skin);
        gambleField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        gambleTable.add(gambleField).padBottom(3).colspan(2);
        gambleTable.row();
        //Add textbox to allow input of monetary amounts
        //Set this textbox up to accept numerical inputs only

        TextButton gambleButton = new TextButton("Gamble Money", game.skin);
        gambleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resourceMarket.gamble();
                widgetUpdate();
                game.gameScreen.getActors().textUpdate();
            }
        });
        gambleButton.setWidth(150);
        gambleTable.add(gambleButton).width(150).padBottom(10).colspan(2);
        gambleTable.row();
        //Prepare and add a button to initiate/attempt gambles

        gambleStatusLabel = new Label(" \n ", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE));
        gambleStatusLabel.setAlignment(Align.center);
        gambleTable.add(gambleStatusLabel).padBottom(10).colspan(2);
        gambleTable.row();
        //Add a label to indicate the outcomes of attempted gambles

        gamblePlayerRoll = new Label("-", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        gambleRNGesusRoll = new Label("-", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        gambleTable.add(new Label("Your Roll", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).left().width(110);
        gambleTable.add(gamblePlayerRoll).expandX().left();
        gambleTable.row();
        gambleTable.add(new Label("AI's Roll", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).left().width(110).padBottom(10);
        gambleTable.add(gambleRNGesusRoll).expandX().left().padBottom(10);
        gambleTable.row();
        //Add labels to report on the rolls that the player and the game's AI make when gambling

        gambleMoneyWonLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        gambleMoneyLostLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        gambleWinLossLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        gambleTable.add(new Label("Money Won", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).left().width(110);
        gambleTable.add(gambleMoneyWonLabel).expandX().left();
        gambleTable.row();
        gambleTable.add(new Label("Money Lost", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).left().width(110);
        gambleTable.add(gambleMoneyLostLabel).expandX().left();
        gambleTable.row();
        gambleTable.add(new Label("W/L", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).left().width(110);
        gambleTable.add(gambleWinLossLabel).expandX().left();
        //Add labels to count the player's monetary wins/losses and the difference in their wins and losses while
        //gambling on the current turn

        add(gambleTable).top();
        //Add gambling region to the market's interface

        row();

        TextButton exitButton = new TextButton("EXIT MARKET", game.skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.nextPhase();
            }
        });
        add(exitButton).colspan(2).expandX().width(490);
        //Add a button to exit the market and move the game on at the bottom of the market's interface

        widgetUpdate();
        //Ensure that the market's stock-counters and prices fall in line with the resources owned by the player
        //and the market, along with the general state of the game
    }

    /**
     * Generate an adjustable actor for sell/buy.
     *
     * @param resource The resource type.
     * @param buy  <code>true</code> if is for buying,
     *             or <code>false</code> if is for selling
     * @return The adjustable actor generated.
     */
    private MarketAdjustableActor createAdjustable(final ResourceType resource, final boolean buy) {
        final MarketAdjustableActor adjustableActor = new MarketAdjustableActor(game, game.skin, resource, buy);
        //Prepare a widget to assist players in buying or selling a particular resource

        adjustableActor.setActionEvent(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (buy) {
                    // Buy from market
                    if (game.getPlayer().purchaseResourceFromMarket(adjustableActor.getValue(), game.market, resource) == PurchaseStatus.Success) {
                        game.gameScreen.getActors().textUpdate();
                        widgetUpdate();
                    }
                } else {
                    // Sell to market
                    if (adjustableActor.getValue() <= game.getPlayer().getResource(resource)) {
                        game.getPlayer().sellResourceToMarket(adjustableActor.getValue(), game.market, resource);
                        game.gameScreen.getActors().textUpdate();
                        widgetUpdate();
                    }
                }
                //Set the widget's button up to perform the necessary buying/selling transaction based on the
                //parameters given to the function


            }
        });
        return adjustableActor;
    }

    /**
     * Get price in string format
     *
     * @param resource The resource type.
     * @param buy  <code>true</code> if is for buying,
     *             or <code>false</code> if is for selling
     * @return The formatted string for the resource.
     */
    private String getPriceString(ResourceType resource, boolean buy) {
        // getBuyPrice: market buy-in price (user sell price)
        // getSellPrice: market sell price (user buy price)
        return resource.toString() + ": "
                + (buy
                ? game.market.getSellPrice(resource)
                : game.market.getBuyPrice(resource))
                + " Gold";
    }

    /**
     * Sync. information with the adjustable.
     *
     * @param adjustableActor The adjustable to manipulate with.
     * @param resource        The resource type.
     * @param buy  <code>true</code> if is for buying,
     *             or <code>false</code> if is for selling
     */
    private void updateAdjustable(MarketAdjustableActor adjustableActor, ResourceType resource,
                                  boolean buy) {
        if (buy) {
            adjustableActor.setMax(game.market.getResource(resource));
        } else {
            adjustableActor.setMax(game.getPlayer().getResource(resource));
        }
        //Set the buying/selling cap for the widget based on the amounts of resources owned either by the market
        //(in the case of a purchasing widget) or the player (in the case of a selling widget)

        adjustableActor.setTitle(getPriceString(resource, buy));
        //Update the label indicating the current buying/selling price for the resource that the widget concerns
    }

    /**
     * Updates all of the market's buying/selling widgets based on current buying/selling prices and stocks
     */
    public void widgetUpdate() {
        updateAdjustable(oreBuyAdjustable, ResourceType.ORE, true);
        if (game.market.getResource(ResourceType.ORE) == 0) {
            oreBuyAdjustable.setValue(0);
        } else {
            oreBuyAdjustable.setValue(1);
        }
        //Show market's ore stock and ore purchasing price

        updateAdjustable(oreSellAdjustable, ResourceType.ORE, false);
        if (game.getPlayer().getResource(ResourceType.ORE) == 0) {
            oreSellAdjustable.setValue(0);
        } else {
            oreSellAdjustable.setValue(1);
        }
        //Show player's ore stock and ore selling price

        updateAdjustable(energyBuyAdjustable, ResourceType.ENERGY, true);
        if (game.market.getResource(ResourceType.ENERGY) == 0) {
            energyBuyAdjustable.setValue(0);
        } else {
            energyBuyAdjustable.setValue(1);
        }
        //Show market's energy stock and energy purchasing price

        updateAdjustable(energySellAdjustable, ResourceType.ENERGY, false);
        if (game.getPlayer().getResource(ResourceType.ENERGY) == 0) {
            energySellAdjustable.setValue(0);
        } else {
            energySellAdjustable.setValue(1);
        }
        //Show player's energy stock and energy selling price

        updateAdjustable(foodBuyAdjustable, ResourceType.FOOD, true);
        if (game.market.getResource(ResourceType.FOOD) == 0) {
            foodBuyAdjustable.setValue(0);
        } else {
            foodBuyAdjustable.setValue(1);
        }
        //Show market's food stock and food purchasing price

        updateAdjustable(foodSellAdjustable, ResourceType.FOOD, false);
        if (game.getPlayer().getResource(ResourceType.FOOD) == 0) {
            foodSellAdjustable.setValue(0);
        } else {
            foodSellAdjustable.setValue(1);
        }
        //Show player's food stock and food selling price

        setButtonStates();
        //Enable and disable resources' buying/selling buttons based on whether or not resources are in stock,
        //owned by the player or if the player can afford to make certain purchases
    }

    /**
     * Add an empty row to current table.
     * @param height  The y for that empty row.
     */
    private void rowWithHeight(int height) {
        marketTable.row();
        marketTable.add().spaceTop(height);
        marketTable.row();
    }

    public void setButtonStates() {
        if (game.market.getResource(ResourceType.ORE) == 0 || (oreBuyAdjustable.getValue() * game.market.getSellPrice(ResourceType.ORE) > game.getPlayer().getMoney())) {
            oreBuyAdjustable.setButtonState(Touchable.disabled);
        } else {
            oreBuyAdjustable.setButtonState(Touchable.enabled);
        }
        //Enable ore purchasing if ore is in stock and the player can afford it

        if (game.getPlayer().getResource(ResourceType.ORE) > 0) {
            oreSellAdjustable.setButtonState(Touchable.enabled);
        } else {
            oreSellAdjustable.setButtonState(Touchable.disabled);
        }
        //Enable ore selling if the player has ore to sell

        if (game.market.getResource(ResourceType.ENERGY) == 0 || (energyBuyAdjustable.getValue() * game.market.getSellPrice(ResourceType.ENERGY) > game.getPlayer().getMoney())) {
            energyBuyAdjustable.setButtonState(Touchable.disabled);
        } else {
            energyBuyAdjustable.setButtonState(Touchable.enabled);
        }
        //Enable energy purchasing if energy is in stock and the player can afford it

        if (game.getPlayer().getResource(ResourceType.ENERGY) > 0) {
            energySellAdjustable.setButtonState(Touchable.enabled);
        } else {
            energySellAdjustable.setButtonState(Touchable.disabled);
        }
        //Enable energy selling if the player has energy to sell

        if (game.market.getResource(ResourceType.FOOD) == 0 || (foodBuyAdjustable.getValue() * game.market.getSellPrice(ResourceType.FOOD) > game.getPlayer().getMoney())) {
            foodBuyAdjustable.setButtonState(Touchable.disabled);
        } else {
            foodBuyAdjustable.setButtonState(Touchable.enabled);
        }
        //Enable food purchasing if food is in stock and the player can afford it

        if (game.getPlayer().getResource(ResourceType.FOOD) > 0) {
            foodSellAdjustable.setButtonState(Touchable.enabled);
        } else {
            foodSellAdjustable.setButtonState(Touchable.disabled);
        }
        //Enable food selling if the player has food to sell
    }

    /**
     * Returns the value entered into the gambleField textbox as a String
     *
     * @return String The value entered into the gambleField textbox
     */
    public String gambleFieldValue() {
        return gambleField.getText();
    }

    /**
     * Sets the status text displayed in the market's gambling region (and the colour of that text)
     *
     * @param status The result of the last attempted gamble
     * @param color The new colour of the gamble's status label
     */
    public void setGambleStatusLabel(String status, Color color) {
        gambleStatusLabel.setText(status);
        gambleStatusLabel.setColor(color);
    }

    /**
     * Sets the labels indicating the values returned by the two dice rolled in the last attempted gamble
     *
     * @param playerRoll The value returned by the player's die in the last attempted gamble
     * @param AIroll The value returned by the game's die in the last attempted gamble
     */
    public void setGambleRollLabels(int playerRoll, int AIroll) {
        gamblePlayerRoll.setText(String.valueOf(playerRoll));
        gambleRNGesusRoll.setText(String.valueOf(AIroll));
    }

    /**
     * Sets the labels indicating the values returned by the two dice rolled in the last attempted gamble
     *
     * Overloaded method that sets the aforementioned labels' representations directly using the parameters given
     *
     * @param playerRoll The string encoding the value returned by the player's die in the last attempted gamble
     * @param AIroll The string encoding the value returned by the game's die in the last attempted gamble
     */
    public void setGambleRollLabels(String playerRoll, String AIroll) {
        gamblePlayerRoll.setText(playerRoll);
        gambleRNGesusRoll.setText(AIroll);
    }

    /**
     * Sets the labels indicating the statistics covering the gambles attempted by the player in the current turn
     *
     * @param moneyWon The total amount of money won by the player from gambles in the current turn
     * @param moneyLost The total amount of money lost by the player from gambles in the current turn
     * @param winLoss The difference in wins and losses experienced while gambling in the current turn
     */
    public void setGambleStatisticsLabels(int moneyWon, int moneyLost, int winLoss) {
        gambleMoneyWonLabel.setText(String.valueOf(moneyWon));
        gambleMoneyLostLabel.setText(String.valueOf(moneyLost));
        gambleWinLossLabel.setText(String.valueOf(winLoss));
    }
}
