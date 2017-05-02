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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.Roboticon;
import io.github.teamfractal.entity.enums.PurchaseStatus;
import io.github.teamfractal.entity.enums.ResourceType;

import java.util.ArrayList;

/**
 * Created by Joseph on 08/02/2017.
 */
public class RoboticonMarketActors extends Table {
    /**
     * Texture to be drawn in the market when the current player selects an uncustomised roboticon
     */
    private static final Texture no_cust_texture;

    /**
     * Texture to be drawn in the market when the current player selects a roboticon customized to generate energy
     */
    private static final Texture energy_texture;

    /**
     * Texture to be drawn in the market when the current player selects a roboticon customized to generate ore
     */
    private static final Texture ore_texture;

    /**
     * Texture to be drawn in the market when the current player selects a roboticon customized to generate food
     */
    private static final Texture food_texture;

    /**
     * Texture to be drawn in the market when the current player owns no unplaced roboticons
     */
    private static final Texture no_robotic_texture;

    /**
     * Import the roboticon textures to be drawn in the interface
     */
    static {
        no_cust_texture = new Texture(Gdx.files.internal("roboticon_images/robot.png"));
        energy_texture = new Texture(Gdx.files.internal("roboticon_images/robot_energy.png"));
        ore_texture = new Texture(Gdx.files.internal("roboticon_images/robot_ore.png"));
        food_texture = new Texture(Gdx.files.internal("roboticon_images/Robot_Food.png"));
        no_robotic_texture = new Texture(Gdx.files.internal("roboticon_images/no_roboticons.png"));
    }

    /**
     * An array containing the unplaced roboticons held by the current player
     */
    public ArrayList<Roboticon> roboticons = new ArrayList<Roboticon>();
    /**
     * The game's engine
     */
    private RoboticonQuest game;
    /**
     * Texture corresponding to the selected roboticon
     */
    private Texture roboticonTexture;

    /**
     * Image object that serves to draw a visual representation of the roboticonTexture object
     */
    private Image roboticonImage;

    /**
     * Variable holding the amount of roboticons that the user would like to purchase
     */
    private Integer roboticonPurchaseAmount;

    /**
     * Label representing the amount of roboticons that the user would like to purchase
     */
    private Label roboticonPurchaseAmountLabel;

    /**
     * Button that decrements roboticonPurchaseAmount
     */
    private TextButton roboticonSubButton;

    /**
     * Button that increments roboticonPurchaseAmount
     */
    private TextButton roboticonAddButton;

    /**
     * Button that, upon being clicked, attempts to process a transaction of roboticons for the current player
     */
    private TextButton roboticonPurchaseButton;

    /**
     * Variable holding the index of the player-owned roboticon that the current player last selected
     */
    private Integer selectedRoboticonIndex;

    /**
     * Label representing the randomly-generated ID-tag of the last roboticon to have been selected
     */
    private Label selectedRoboticonIDLabel;

    /**
     * Button that decrements selectedRoboticonIndex
     */
    private TextButton moveLeftInventoryButton;

    /**
     * Button that increments selectedRoboticonIndex
     */
    private TextButton moveRightInventoryButton;

    /**
     * Drop-down box enabling users to select potential customisations for their roboticons
     */
    private SelectBox<String> customisationDropDown;

    /**
     * Button that, upon being clicked, attempts to process a transaction for upgrading the currently-selected roboticon
     */
    private TextButton customisationPurchaseButton;

    /**
     * Button that closes the market's interface and advances the game upon being clicked
     */
    private TextButton exitButton;

    /**
     * Constructor class that connects the roboticon market to the internal engine and builds its visual interface
     *
     * @param game The game's engine
     */
    public RoboticonMarketActors(RoboticonQuest game) {
        this.game = game;
        //Import the game's engine to perform transactions with

        constructInterface();
        //Construct the market's visual interface
    }

    /**
     * Builds the visual framework that serves as the market's interface
     */
    private void constructInterface() {
        Table purchaseTable = new Table();
        Table selectionTable = new Table();
        Table upgradeTable = new Table();
        //Instantiate sub-frameworks to place appropriate actors into

        this.roboticonImage = new Image();
        //Instantiate image object to indicate the properties of the roboticons that the player selects

        roboticonPurchaseAmount = 1;
        //Set starting purchase amount

        constructLabels();
        constructButtons();

        purchaseTable.add(new Label("PURCHASE ROBOTICONS", new Label.LabelStyle(game.headerFontRegular.font(), Color.WHITE))).colspan(4);
        //Add purchase-region header

        purchaseTable.row();
        purchaseTable.add(roboticonSubButton).width(25);
        purchaseTable.add(roboticonPurchaseAmountLabel).width(50).expandX();
        purchaseTable.add(roboticonAddButton).width(25);
        purchaseTable.add(roboticonPurchaseButton).align(Align.right).width(268).padLeft(14);
        //Add buttons for incrementing and decrementing quantities of roboticons to purchase, along with another for
        //triggering purchases

        add(purchaseTable).padBottom(35);
        row();

        selectionTable.add(new Label("CUSTOMISE ROBOTICONS", new Label.LabelStyle(game.headerFontRegular.font(), Color.WHITE))).colspan(3);
        //Add customisation-region header

        selectionTable.row();
        selectionTable.add(moveLeftInventoryButton).width(50);
        selectionTable.add(roboticonImage).expandX();
        selectionTable.add(moveRightInventoryButton).width(50);
        //Add buttons for selecting roboticons owned by the current player, along with the image object to represent
        //them

        selectionTable.row();
        selectionTable.add(selectedRoboticonIDLabel).colspan(3);
        //Add ID label to distinguish each roboticon that the player owns

        add(selectionTable).padBottom(10);
        row();

        customisationDropDown = new SelectBox<String>(game.skin);
        customisationDropDown.setItems("Energy Generation", "Ore Mining", "Food Farming");
        upgradeTable.add(customisationDropDown).expandX().padRight(14);
        upgradeTable.add(customisationPurchaseButton).width(196);
        //Prepare and add drop-down list to differentiate and select potential roboticon customisations

        add(upgradeTable).padBottom(35);
        row();

        add(exitButton).expandX().width(382);
    }

    /**
     * Constructs critical label objects that may change in appearance as the market is interacted with
     */
    private void constructLabels() {
        roboticonPurchaseAmountLabel = new Label(roboticonPurchaseAmount + "/" +  game.market.getResource(ResourceType.ROBOTICON), new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        roboticonPurchaseAmountLabel.setAlignment(Align.center);
        //Set up roboticonPurchaseAmountLabel to indicate the amount of roboticons that the player wishes to purchase...
        //...and the amount of roboticons that are currently available to purchase from the market

        selectedRoboticonIDLabel = new Label("", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
        selectedRoboticonIDLabel.setAlignment(Align.center);
        //Set up selectedRoboticonIDLabel so that it can be changed later on to differentiate between owned roboticons
    }

    /**
     * Constructs the buttons that will allow users to interact with the market
     */
    private void constructButtons() {
        //Increases number of roboticons to be purchased
        roboticonAddButton = new TextButton("+", game.skin);
        roboticonAddButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addRoboticonFunction();
            }
        });

        //Decreases number of roboticons to be purchased
        roboticonSubButton = new TextButton("-", game.skin);
        roboticonSubButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                subRoboticonFunction();
            }
        });

        //Purchases the specified number of roboticons
        roboticonPurchaseButton = new TextButton("[PRICE: " + (game.market.getSellPrice(ResourceType.ROBOTICON) * roboticonPurchaseAmount) + "] PURCHASE", game.skin);
        roboticonPurchaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                purchaseRoboticonFunction();
            }
        });

        //Goes back through the queue of roboticons owned by the current player
        moveLeftInventoryButton = new TextButton("<", game.skin);
        moveLeftInventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedRoboticonIndex > 0) {
                    selectedRoboticonIndex--;
                    refreshCurrentlySelectedRoboticon();
                }
            }
        });

        //Goes forward through the queue of roboticons owned by the current player
        moveRightInventoryButton = new TextButton(">", game.skin);
        moveRightInventoryButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedRoboticonIndex < roboticons.size() - 1) {
                    selectedRoboticonIndex++;
                    refreshCurrentlySelectedRoboticon();
                }
            }
        });

        //Purchases a customisation and applies it to the currently-selected Roboticon
        customisationPurchaseButton = new TextButton("[PRICE: " + game.market.getSellPrice(ResourceType.CUSTOMISATION) + "] PURCHASE", game.skin);
        customisationPurchaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedRoboticonIndex == -1) {
                    return;
                }
                //Stop the customisation transaction from going ahead if the player hasn't selected a roboticon

                ResourceType resource = null;

                switch(customisationDropDown.getSelectedIndex()) {
                    case (0):
                        resource = ResourceType.ENERGY;
                        break;
                    case (1):
                        resource = ResourceType.ORE;
                        break;
                    case (2):
                        resource = ResourceType.FOOD;
                        break;
                }
                //Equate the current value of the customisationDropDown box to a resource-type...

                purchaseCustomisationFunction(resource, selectedRoboticonIndex);
                //...and upgrade the currently-selected roboticon to improve its ability to generate that resource-type
            }
        });

        //Exits the shop and advances the game upon being clicked
        exitButton = new TextButton("EXIT ROBOTICON SHOP", game.skin);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.nextPhase();
            }
        });
    }

    /**
     * Refreshes the interface of the Roboticon shop whenever it's interacted with
     * Also toggles the player's ability to make their desired purchase, depending on whether or not they hold
     * sufficient resources
     */
    public void refreshRoboticonShop() {
        roboticonPurchaseAmountLabel.setText(roboticonPurchaseAmount + "/" + game.market.getResource(ResourceType.ROBOTICON));
        //Refresh roboticonPurchaseAmountLabel to indicate the desired amount of roboticons to be purchased and the
        //number that the market can currently offer
        roboticonPurchaseButton.setText("[PRICE: " + (game.market.getSellPrice(ResourceType.ROBOTICON) * roboticonPurchaseAmount) + "] PURCHASE");
        //Set the roboticon shop's price-tag (integrated into the local purchase button) to reflect the total cost
        //of the transaction that the player desires to make

        if (game.getPlayer().getMoney() >= game.market.getSellPrice(ResourceType.ROBOTICON) * roboticonPurchaseAmount) {
            roboticonPurchaseButton.setTouchable(Touchable.enabled);
        } else {
            roboticonPurchaseButton.setTouchable(Touchable.disabled);
        }
        //If the player can't afford to make their desired purchase, disable the purchase button to stop them from doing
        //so
	}

    /**
     * Adds a roboticon to the quantity selected
     */
    public void addRoboticonFunction() {
        if (roboticonPurchaseAmount < game.market.getResource(ResourceType.ROBOTICON)) {
            roboticonPurchaseAmount += 1;
            refreshRoboticonShop();
        }
    }

    /**
     * Subtracts a roboticon from the quantity selected
     */
    public void subRoboticonFunction() {
        if (roboticonPurchaseAmount > 1) {
            roboticonPurchaseAmount -= 1;
            refreshRoboticonShop();
        }
    }

    /**
     * Buys the selected customisation and adds it to the player's inventory
     *
     * @param resource The customisation that has been bought by the player
     * @param index The position of the currently selected, non-customised roboticon
     */
    public void purchaseCustomisationFunction(ResourceType resource, int index) {
        if (game.getPlayer().purchaseCustomisationFromMarket(resource, roboticons.get(index), game.market) == PurchaseStatus.Success) {
            widgetUpdate();
            //Refresh the customisation area to reflect the new customisation purchase
            game.gameScreen.getActors().textUpdate();
            //Refresh the text describing the state of the player's inventory to reflect the new customisation purchase

            customisationPurchaseButton.setText("CUSTOMISED");
            customisationPurchaseButton.setTouchable(Touchable.disabled);
            //Stop the player from trying to customise the roboticon that they just customised again
        }
    }

    /**
     * Buys the selected amount of roboticons and places them in the player's inventory
     */
    public void purchaseRoboticonFunction() {
        if (game.getPlayer().purchaseRoboticonsFromMarket(roboticonPurchaseAmount, game.market) == PurchaseStatus.Success) {
            if (game.market.getResource(ResourceType.ROBOTICON) == 0) {
                roboticonPurchaseAmount = 0;
            } else {
                roboticonPurchaseAmount = 1;
            }
            //If the market ran out of roboticons after this purchase, indicate so accordingly

            refreshRoboticonShop();
            //Refresh the market's interface to reflect the new roboticon stock-level and price
            widgetUpdate();
            //Indicate that the player has acquired a new roboticon in the market's customisation region
            game.gameScreen.getActors().textUpdate();
            //Refresh the text describing the state of the player's inventory to reflect the new roboticon purchase
        }
    }

    /**
     * Resets the array of roboticons available to customise in the market (based on that which the player owns)
     */
    public void widgetUpdate() {
        roboticons.clear();
        for (Roboticon r : game.getPlayer().getRoboticons()) {
            if (!r.isInstalled()) {
                roboticons.add(r);
            }
        }
        //Clear and re-acquire the set of uninstalled roboticons owned by the current player

        if (roboticons.size() == 0) {
            selectedRoboticonIndex = -1;
        } else {
            selectedRoboticonIndex = 0;
        }
        //Select the player's first uninstalled roboticon if they own one

        refreshCurrentlySelectedRoboticon();
        //Refresh the appearance of the customisation area based on the properties of the roboticon that was just
        //selected
    }

    /**
     * Sets the appearance of the current roboticon on the market screen. This is based on the customisation of the
     * selected roboticon as well as the ID number
     */
    private void refreshCurrentlySelectedRoboticon() {
        if (selectedRoboticonIndex != -1) {
            ResourceType roboticonType = roboticons.get(selectedRoboticonIndex).getCustomisation();
            //Get the selected roboticon's customisation type...

            switch (roboticonType) {
                case Unknown:
                    roboticonTexture = no_cust_texture;
                    break;
                case ENERGY:
                    roboticonTexture = energy_texture;
                    customisationDropDown.setSelectedIndex(0);
                    break;
                case ORE:
                    roboticonTexture = ore_texture;
                    customisationDropDown.setSelectedIndex(1);
                    break;
                case FOOD:
                    roboticonTexture = food_texture;
                    customisationDropDown.setSelectedIndex(2);
                    break;
                default:
                    break;
            }
            //...and adjust the image in the interface accordingly

            this.selectedRoboticonIDLabel.setText("[" + (selectedRoboticonIndex + 1) + "/" + roboticons.size() + "] ISSUE NUMBER: " + padZero(roboticons.get(selectedRoboticonIndex).getID(), 4));
            //Set the label below the selected roboticon's image to show its ID number along with its index in the
            //array of roboticons owned bt the player
            //APPEARS AS:
            //      [{CURRENT INDEX} / {TOTAL NUMBER OF UNCUSTOMISED ROBOTICONS}] ISSUE NUMBER: {CURRENT ID}

            if (roboticonType == ResourceType.Unknown) {
                customisationPurchaseButton.setText("[PRICE: " + game.market.getSellPrice(ResourceType.CUSTOMISATION) + "] PURCHASE");
                //Set the customisation purchase button to show the current customisation price maintained by the market

                if (game.getPlayer().getMoney() >= game.market.getSellPrice(ResourceType.CUSTOMISATION)) {
                    customisationPurchaseButton.setTouchable(Touchable.enabled);
                } else {
                    customisationPurchaseButton.setTouchable(Touchable.disabled);
                }
                //If the player can't afford to customise their roboticon, disable the button which would allow them
                //to do so

            } else {
                customisationPurchaseButton.setText("CUSTOMISED");
                customisationPurchaseButton.setTouchable(Touchable.disabled);
                //If the player has already customised the current roboticon, prevent them from doing it again
            }
        } else {
            roboticonTexture = no_robotic_texture;
            this.selectedRoboticonIDLabel.setText("ISSUE NUMBER: ####");
            //If the player owns no roboticons, indicate this in the customisation area...

            customisationPurchaseButton.setText("[PRICE: " + game.market.getSellPrice(ResourceType.CUSTOMISATION) + "] PURCHASE");
            customisationPurchaseButton.setTouchable(Touchable.disabled);
            //...and prevent them from trying to perform customisation transactions
        }

        roboticonImage.setDrawable(new TextureRegionDrawable(new TextureRegion(roboticonTexture)));
        //Update the image object in the market to encode the texture pertaining to the currently-selected roboticon
    }
    /**
     * Generates a string of a number followed by a certain amount of zeros
     * @param number The number that the string starts with
     * @param length The number of zeros that the number is followed by
     * @return The string that has been generated
     */

    public String padZero(int number, int length) {
        String s = "" + number;
        while (s.length() < length) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * Getter for the index of the selected Roboticon
     * @return The index of the selected Roboticon
     */
    public int selectedRoboticonIndex() {
        return selectedRoboticonIndex;
    }
}
