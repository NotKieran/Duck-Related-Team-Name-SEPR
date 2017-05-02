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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.enums.ResourceType;

public class MarketAdjustableActor extends Table {

	/**
	 * The game's engine
	 */
	private RoboticonQuest game;

	/**
	 * Button decrementing the quantity of things to buy/sell through the widget
	 */
	private final TextButton subButton;

	/**
	 * Button incrementing the quantity of things to buy/sell through the widget
	 */
	private final TextButton addButton;

	/**
	 * Button that attempts to initiate a buying/selling transaction for/using the amount of resources specified
	 * in the widget by the player
	 */
	private final TextButton actButton;

	/**
	 * Label encoding the desired value of resources to be bought or sold through the widget
	 */
	private final Label valueLabel;

	/**
	 * Label providing a visual and descriptive representation of the widget's function
	 */
	private final Label titleLabel;

	/**
	 * Variable holding the quantity of resources that the user currently wants to buy or sell through the widget
	 */
	private int value;

	/**
	 * Variable setting the minimum value that the widget can take
	 */
	private int min;

	/**
	 * Variable setting the maximum value that the widget can take
	 */
	private int max;

	/**
	 * Encapsulates the code to be executed when the widget's button is clicked on
	 */
	private ChangeListener actionEvent;

	/**
	 * Determines whether the widget is to act as an interface for buying or selling
	 */
	private boolean buy;

	/**
	 * Determines the type of resource that the widget will handle transactions for
	 */
	private ResourceType resourceType;

	/**
	 * Get current title string.
	 * @return     The title text.
	 */
	public String getTitle() {
		return titleLabel.getText().toString();
	}

	/**
	 * Set a new title string.
	 * @param title  The new Title.
	 */
	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	/**
	 * Get current adjusted value
	 * @return     Current value.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set a new adjusted value.
	 * @param value       The new value.
	 */
	public void setValue(int value) {
		this.value = value;

		if (value > max) {
			value = max;
		} else if (value < min) {
			value = min;
		}

		updateInterface();
	}

	/**
	 * Get current minimum value.
	 * @return     Currently set minimum value.
	 */
	public int getMin() {
		return min;
	}

	/**
	 * Set a new minimum value.
	 * @param min       The new minimum value.
	 */
	public void setMin(int min) {
		this.min = min;

		if (value < min) {
			value = min;
		}

		updateInterface();
	}

	/**
	 * Get current maximum value.
	 * @return     Currently set maximum value.
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Set a new maximum value.
	 * @param max       The new maximum value.
	 */
	public void setMax(int max) {
		this.max = max;

		if (value > max) {
			value = max;
		}

		updateInterface();
	}

	/**
	 * Set the new action button event.
	 * @param actionEvent        The new action button handle event.
	 */
	public void setActionEvent(ChangeListener actionEvent) {
		this.actionEvent = actionEvent;
	}
	//</editor-fold>

	/**
	 * Constructs an interface to handle the purchase or sale of a particular resource-type
	 * Populated with buttons for incrementing/decrementing a desired quantity of a particular resource to buy/sell,
	 * along with an additional button for initiating the relevant transaction and any necessary labels to indicate
	 * the widget's function and current state
	 *
	 * @param game The game's engine
	 * @param skin The skin-file determining the widget's visual parameters
	 * @param resourceType The type of resource that the widget will handle purchases/sales for
	 * @param buy Determines whether the widget will handle purchasing resources for the player or selling the
	 *            resources that the player owns to the market
	 */
	public MarketAdjustableActor(RoboticonQuest game, Skin skin, ResourceType resourceType, boolean buy) {
		this.game = game;
		//Import the gane's engine to initiate transactions with

		this.value = 1;
		this.min = 1;
		//Set the minimum and starting values of resources that can be bought or sold

		this.buy = buy;
		this.resourceType = resourceType;
		//Set the type of resource that the widget will handle, along with whether it will handle purchasing or selling

		subButton = new TextButton("<", skin);
		addButton = new TextButton(">", skin);
		actButton = new TextButton("", skin);
		valueLabel = new Label("", skin);
		//Initialise features allowing users to select desired purchase/sale quantities and to indicate said quantities

		if (buy) {
			titleLabel = new Label(resourceType.toString() + " - Price: " + game.market.getSellPrice(resourceType), skin);
			//Set the widget's title to indicate the price that the market is selling the given resource-type at

			this.max = game.market.getResource(resourceType);
			//If the widget handles purchases, don't allow the player to buy more than what the market has to offer
		} else {
			titleLabel = new Label(resourceType.toString() + " - Price: " + game.market.getBuyPrice(resourceType), skin);
			//Set the widget's title to indicate the price that the market will pay for a single unit of the given resource-type

			this.max = game.getPlayer().getResource(resourceType);
			//If the widget handles sales, don't allow the player to sell more than what they have
		}

		bindButtonEvents();
		updateInterface();

		/*
		 *   +---------------+
		 *   |   [ title ]   |
		 *   +---------------+
		 *   | < | value | > |
		 *   +---------------+
		 *   |   [ button ]  |
		 *   +---------------+
		 */

		titleLabel.setAlignment(Align.center);
		valueLabel.setWidth(80);
		valueLabel.setAlignment(Align.center);
		subButton.padLeft(5).padRight(5);
		addButton.padLeft(5).padRight(5);
		//Set alignment and padding properties of widget elements

		add(titleLabel).colspan(3).fillX().spaceBottom(10);
		row();
		//Add the widget's title to the internal structure

		add(subButton).align(Align.left);
		add(valueLabel).fillX().width(100);
		add(addButton).align(Align.right);
		row();
		//Add the selection buttons and quantity indication label to the internal structure

		add(actButton).colspan(3).fillX().spaceTop(10).width(150);
		row();
		//Add transaction initiation button to the internal structure
	}

	/**
	 * Binds events to buttons.
	 */
	private void bindButtonEvents() {
		actButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (actionEvent != null)
					actionEvent.changed(event, actor);
			}
		});

		subButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (value > min) {
					value--;
					updateInterface();
					game.resourceMarket.actors().setButtonStates();
				}
			}
		});

		addButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (value < max) {
					value++;
					updateInterface();
					game.resourceMarket.actors().setButtonStates();
				}
			}
		});
	}

	/**
	 * Update the quantity indication label and the purchase initiation button to show the player's desired
	 * purchasing/selling quantity and the amount of money that they would gain/lose from a possible transaction
	 * through the widget
	 */
	private void updateInterface() {
		valueLabel.setText(value + "/" + max);

		if (buy == true) {
			actButton.setText("[PRICE: " + (value * game.market.getSellPrice(resourceType)) + "] Buy");
		} else {
			actButton.setText("[PRICE: " + (value * game.market.getBuyPrice(resourceType)) + "] Sell");
		}
	}

	@Override
	protected void sizeChanged() {

	}


	public void setButtonState (Touchable state) {
		actButton.setTouchable(state);
	}
}
