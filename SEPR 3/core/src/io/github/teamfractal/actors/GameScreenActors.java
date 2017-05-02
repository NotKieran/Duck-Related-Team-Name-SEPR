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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import io.github.teamfractal.RoboticonQuest;
import io.github.teamfractal.entity.LandPlot;
import io.github.teamfractal.entity.Player;
import io.github.teamfractal.entity.Roboticon;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.screens.AbstractAnimationScreen;
import io.github.teamfractal.screens.GameScreen;

public class GameScreenActors {
	private final Stage stage;
	private RoboticonQuest game;
	private GameScreen screen;

	private Table phaseInfo;
	private Label playerLabel;
	private Label phaseLabel;
	private Label phaseDescriptionLabel;

	private Table playerStats;
	private Label playerOreLabel;
	private Label playerEnergyLabel;
	private Label playerFoodLabel;
	private Label playerMoneyLabel;

	private Table plotStatsTable;
	private Label plotOreLabel;
	private Label plotEnergyLabel;
	private Label plotFoodLabel;

	private TextButton buyLandPlotBtn;
	private TextButton installRoboticonBtn;
	private TextButton installRoboticonBtnCancel;
	private SelectBox<String> installRoboticonSelect;
	private TextButton nextButton;
	private boolean listUpdated;
    private Table installRoboticonTable;

	/**
	 * Initialise the main game screen components.
	 * @param game         The game manager {@link RoboticonQuest}
	 * @param screen       Current screen to display on.
	 */
	public GameScreenActors(final RoboticonQuest game, GameScreen screen) {
		this.game = game;
		this.screen = screen;
		this.stage = screen.getStage();
	}

	/**
	 * Setup buttons
	 */
	public void constructElements() {

		// Create UI components
		nextButton = new TextButton("Next Phase", game.skin);
		buyLandPlotBtn = new TextButton("Buy Plot", game.skin);
		createRoboticonInstallMenu();

		// Create player stats table
		playerStats = new Table();
		playerStats.align(Align.left);
		playerOreLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		playerFoodLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		playerEnergyLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		playerMoneyLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		playerStats.add(new Label("Ore", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		playerStats.add(playerOreLabel).width(50);
		playerStats.row();
		playerStats.add(new Label("Food", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		playerStats.add(playerFoodLabel).width(50);
		playerStats.row();
		playerStats.add(new Label("Energy", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		playerStats.add(playerEnergyLabel).width(50);
		playerStats.row();
		playerStats.add(new Label("Money", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		playerStats.add(playerMoneyLabel).width(50);

		// Create phase info table;
		phaseInfo = new Table();
		phaseInfo.align(Align.right);
		playerLabel = new Label("PLAYER 1", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE));
		phaseLabel = new Label("PHASE 1", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		phaseDescriptionLabel = new Label("Claim a Tile", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		playerLabel.setAlignment(Align.right);
		phaseLabel.setAlignment(Align.right);
		phaseDescriptionLabel.setAlignment(Align.right);
		phaseInfo.add(playerLabel).width(300);
		phaseInfo.row();
		phaseInfo.add(phaseLabel).width(300);
		phaseInfo.row();
		phaseInfo.add(phaseDescriptionLabel).width(300);

		// Create plot stats table
		plotStatsTable = new Table();
		plotStatsTable.align(Align.left);
		plotStatsTable.setVisible(false);
		plotOreLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		plotFoodLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		plotEnergyLabel = new Label("0", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		plotStatsTable.add(new Label("Ore", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		plotStatsTable.add(plotOreLabel).width(50);
		plotStatsTable.row();
		plotStatsTable.add(new Label("Food", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		plotStatsTable.add(plotFoodLabel).width(50);
		plotStatsTable.row();
		plotStatsTable.add(new Label("Energy", new Label.LabelStyle(game.smallFontRegular.font(), Color.WHITE))).width(70);
		plotStatsTable.add(plotEnergyLabel).width(50);

		// Adjust properties.
		listUpdated = false;
		hideInstallRoboticon();
		buyLandPlotBtn.setVisible(false);
		buyLandPlotBtn.pad(2, 10, 2, 10);
		installRoboticonSelect.setSelected(null);

		// Bind events
		bindEvents();

		// Add to the stage for rendering.
		stage.addActor(nextButton);
		stage.addActor(buyLandPlotBtn);
		stage.addActor(installRoboticonTable);
		stage.addActor(phaseInfo);
		stage.addActor(plotStatsTable);
		stage.addActor(playerStats);

		// Update UI positions.
		AbstractAnimationScreen.Size size = screen.getScreenSize();
		//resizeScreen(size.Width, size.Height);

		playerStats.setPosition(8, Gdx.graphics.getHeight() - 50);
		phaseInfo.setPosition(Gdx.graphics.getWidth() - 8, Gdx.graphics.getHeight() - 39);
		nextButton.setPosition(size.Width - nextButton.getWidth() - 10, 10);
	}

	/**
	 * Create the roboticon installation menu.
	 */
	private void createRoboticonInstallMenu() {
		installRoboticonTable = new Table();
		Table t = installRoboticonTable;

		installRoboticonSelect = new SelectBox<String>(game.skin);
		installRoboticonSelect.setItems(game.getPlayer().getRoboticonAmountList());

		Label installRoboticonLabel = new Label("Install Roboticon", new Label.LabelStyle(game.smallFontLight.font(), Color.WHITE));
		installRoboticonBtn = new TextButton("Confirm", game.skin);
		installRoboticonBtnCancel = new TextButton("Cancel", game.skin);

		t.add(installRoboticonLabel).colspan(2);
		t.row();
		t.add(installRoboticonSelect).colspan(2);
		t.row();
		t.add(installRoboticonBtn);
		t.add(installRoboticonBtnCancel);
		t.row();
	}


	/**
	 * Bind all button events.
	 */
	private void bindEvents() {
		buyLandPlotBtn.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				buyLandPlotFunction();
			}
		});

		nextButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				nextButtonFunction();
			}
		});

		installRoboticonBtn.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				installRoboticonFunction();
			}
		});

		installRoboticonBtnCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.stop();
				hideInstallRoboticon();
			}
		});
	}

	/**
	 * Tile click callback event.
	 *
	 * @param plot The landplot tileClicked.
	 * @param x    Current mouse x position
	 * @param y    Current mouse y position
	 */
	public void tileClicked(LandPlot plot, float x, float y) {
		Player player = game.getPlayer();

		switch (game.getPhase()) {
			// Phase 1:
			// Purchase LandPlot.
			case 1:
				buyLandPlotBtn.setPosition(x + 10, y);
				if (game.canPurchaseLandThisTurn()
						&& !plot.hasOwner()) {
					buyLandPlotBtn.setDisabled(false);
				} else {
					buyLandPlotBtn.setDisabled(true);
				}
				showPlotStats(plot, x + 10, y - 35);

				buyLandPlotBtn.setVisible(true);
				break;

			// Phase 3:
			// Install Roboticon 
			case 3:
				if (player == plot.getOwner()) {
					installRoboticonTable.setPosition(x, y, Align.center);
					updateRoboticonList();
					installRoboticonTable.setVisible(true);
				} else {
					hideInstallRoboticon();
				}
		}


	}

	/**
	 * Update the dropdown list of roboticon available.
	 */
	private void updateRoboticonList() {
		installRoboticonSelect.setItems(game.getPlayer().getRoboticonAmountList());
	}

	/**
	 * Get the "Buy Land" button.
	 * @return "Buy Land" button.
	 */
	public TextButton getBuyLandPlotBtn() {
		return buyLandPlotBtn;
	}

	/**
	 * Updates the UI display.
	 */
	public void textUpdate() {
		playerLabel.setText("PLAYER " + (game.getPlayerInt() + 1));
		phaseLabel.setText("PHASE " + String.valueOf(game.getPhase()));

		switch (game.getPhase()) {
			case (1):
				phaseDescriptionLabel.setText("Claim a Tile");
				break;
			case (2):
				phaseDescriptionLabel.setText("Buy and Upgrade Roboticons");
				break;
			case (3):
				phaseDescriptionLabel.setText("Deploy Roboticons");
				break;
			case (4):
				phaseDescriptionLabel.setText("Generate Resources");
				break;
			case (5):
				phaseDescriptionLabel.setText("Buy and Sell Resources");
				break;
		}

		playerOreLabel.setText(String.valueOf(game.getPlayer().getOre()));
		playerEnergyLabel.setText(String.valueOf(game.getPlayer().getEnergy()));
		playerFoodLabel.setText(String.valueOf(game.getPlayer().getFood()));
		playerMoneyLabel.setText(String.valueOf(game.getPlayer().getMoney()));
	}


	/**
	 * Show plot information about current selected stats.
	 * @param plot           The land plot to show info.
	 * @param x              The <i>x</i> position to display the information.
	 * @param y              The <i>y</i> position to display the information.
	 */
	private void showPlotStats(LandPlot plot, float x, float y) {
		plotOreLabel.setText(String.valueOf(plot.getResource(ResourceType.ORE)));
		plotEnergyLabel.setText(String.valueOf(plot.getResource(ResourceType.ENERGY)));
		plotFoodLabel.setText(String.valueOf(plot.getResource(ResourceType.FOOD)));

		plotStatsTable.setPosition(x, y);
		plotStatsTable.setVisible(true);
	}


	/**
	 * Hide "Buy Land" button and plot information.
	 */
	public void hideBuyLand() {
		buyLandPlotBtn.setVisible(false);
		plotStatsTable.setVisible(false);
	}

	/**
	 * Hide install roboticon dialog.
	 */
	public void hideInstallRoboticon() {
		installRoboticonTable.setVisible(false);
	}

	/**
	 * Check if install roboticon dialog is visible.
	 * @return   <code>true</code> if is visible, <code>false</code> if not visible.
	 */
	public boolean installRoboticonVisible() {
		return installRoboticonTable.isVisible();
	}

	/**
	 * Purchases the landplot that the player has clicked on if it is not already owned.
	 */
	public void buyLandPlotFunction(){
		hideBuyLand();
		if (buyLandPlotBtn.isDisabled()) {
			return ;
		}
		LandPlot selectedPlot = screen.getSelectedPlot();

		if (selectedPlot.hasOwner()) {
			return;
		}

		Player player = game.getPlayer();
		if (player.purchaseLandPlot(selectedPlot)) {
			TiledMapTileLayer.Cell playerTile = selectedPlot.getPlayerTile();
			playerTile.setTile(screen.getPlayerTile(player));
			textUpdate();

			nextButton.setVisible(true);
		}
	}
	/**
	 * The function that advances the phase of the game when the next button is clicked
	 */
	public void nextButtonFunction(){
		if (nextButton.isDisabled()) {
			return ;
		}
        if (game.canPurchaseLandThisTurn() != false) {
            return;
        }
        buyLandPlotBtn.setVisible(false);
        plotStatsTable.setVisible(false);
        hideInstallRoboticon();
        game.nextPhase();
        installRoboticonSelect.setItems(game.getPlayer().getRoboticonAmountList());
        textUpdate();
    }
	/**
	 * Presents the user with a list of roboticons that they can install on the land plot that they have clicked on. Once they have selected
	 * a roboticon to install, a function is called that will install the roboticon.
	 */
	private void installRoboticonFunction(){
		if (installRoboticonBtn.isDisabled()) {
			return ;
		}
		if (!listUpdated) { //prevents updating selection list from updating change listener
			LandPlot selectedPlot = screen.getSelectedPlot();
			if (selectedPlot.getOwner() == game.getPlayer() && !selectedPlot.hasRoboticon()) {
				Roboticon roboticon = null;
                ResourceType type;
                type = ResourceType.Unknown;
                int selection = installRoboticonSelect.getSelectedIndex();

				Array<Roboticon> roboticons = game.getPlayer().getRoboticons();
				switch (selection) {
					case 0:
						type = ResourceType.ORE;
						break;
					case 1:
						type = ResourceType.ENERGY;
						break;
					case 2:
						type = ResourceType.FOOD;
						break;
					default:
						type = ResourceType.Unknown;
						break;
				}

				for (Roboticon r : roboticons) {
					if (!r.isInstalled() && r.getCustomisation() == type) {
						roboticon = r;
						break;
					}
				}

				if (roboticon != null) {
					installRoboticonFunction(selectedPlot,roboticon);
					textUpdate();
				}

				hideInstallRoboticon();
				updateRoboticonList();

			} else listUpdated = false;
		}
	}
	/**
	 * Installs the specified roboticon on the specified land plot.
	 * @param selectedPlot The plot that has been selected
	 * @param roboticon The roboticon that is to be installed
	 */
	public void installRoboticonFunction(LandPlot selectedPlot, Roboticon roboticon){
        selectedPlot.installRoboticon(roboticon);
        TiledMapTileLayer.Cell roboticonTile = selectedPlot.getRoboticonTile();
        roboticonTile.setTile(screen.getResourcePlayerTile(selectedPlot.getOwner(),roboticon.getCustomisation()));
        selectedPlot.setHasRoboticon(true);
    }

    public void switchNextButton() {
		nextButton.setVisible(!nextButton.isVisible());
	}

	public void setNextButtonVisibility(boolean visible) {
		nextButton.setVisible(visible);
	}
}
