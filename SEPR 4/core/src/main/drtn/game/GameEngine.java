/**
 * @author DRTN
 * Team Website with download:
 * https://nicopinedo.github.io/SEPR4/
 *
 * This Class contains either modifications or is entirely new in Assessment 4
 **/

package drtn.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import drtn.game.effects.PlayerEffectSource;
import drtn.game.effects.PlotEffect;
import drtn.game.effects.PlotEffectSource;
import drtn.game.entity.*;
import drtn.game.enums.ResourceType;
import drtn.game.exceptions.InvalidResourceTypeException;
import drtn.game.screens.GameScreen;
import drtn.game.screens.MiniGameScreen;
import drtn.game.util.Drawer;

import java.util.*;


// Changed in Assessment 3: Added so no more than one GameEngine can be instantiated at any one time.
public class GameEngine {
    private static GameEngine _instance;
    /**
     * Stores data pertaining to the game's active players
     * For more information, check the "Player" class
     */
    private static Player[] players;
    /**
     * Stores current game-state, enabling transitions between screens and external QOL drawing functions
     */
    private Game game;

    /**
     * The game's engine only ever runs while the main in-game interface is showing, so it was designed to manipulate
     * elements (both visual and logical in nature) on that screen
     * It therefore requires access to the public methods in the GameScreen class, so instantiation in this class
     * is a necessity
     */
    private GameScreen gameScreen;
    /**
     * Holds the numeric getID of the player who's currently active in the game
     */
    private int currentPlayerID;
    /**
     * Holds the number of the phase that the game is currently in
     * Varies between 1 and 5
     */
    private int phase;
    /**
     * Defines whether or not a tile has been acquired in the current phase of the game
     */
    private boolean tileAcquired;
    /**
     * Object providing QOL drawing functions to simplify visual construction and rendering tasks
     */
    private Drawer drawer;
    /**
     * Holds all of the data and the functions of the game's market
     * Also comes bundled with a visual interface which can be rendered on to the game's screen
     */
    private Market market;
    /**
     * Array holding the tiles to be laid over the map
     * Note that the tiles' visuals are encoded by the image declared and stored in the GameScreen class (and not here)
     */
    public Tile[] tiles;
    /**
     * Holds the data pertaining to the currently-selected tile
     */
    private Tile selectedTile;
    /**
     * Variable dictating whether the game is running or paused at any given moment
     */
    private State state;
    /**
     * Holds data for the "Catch the Chancellor" mini-game
     */
    private Chancellor chancellor;
    /**
     * An integer signifying the ID of the next roboticon to be created
     *
     */
    private int roboticonIDCounter = 0;
    // Added in Assessment 3: Added to keep track of trades, colleges and random events.
    // ---------------------------------------------------------------------------------
    /**
     * An array storing the currently pending trades
     */
    private Array<Trade> trades;
    /**
     * An array storing all the playable colleges
     */
	private College[] colleges;

    /**
     * Defines all of the random PlotEffects that can occur during the game
     */
    private PlotEffectSource plotEffectSource;

    /**
     * Defines all of the random PlayerEffects that can occur during the game
     */
    private PlayerEffectSource playerEffectSource;

    /**
     * Constructs the game's engine. Imports the game's state (for direct renderer access) and the data held by the
     * GameScreen which this engine directly controls; then goes on to set up player-data for the game's players,
     * tile-data for the on-screen map and the in-game market for in-play manipulation. In the cases of the latter
     * two tasks, the engine directly interacts with the GameScreen object imported to it (as a parameter of this
     * constructor) so that it can draw the visual interfaces for the game's market and tiles directly to the game's
     * primary interface.
     *
     * @param game Variable storing the game's state
     * @param gameScreen The object encoding the in-game interface which is to be controlled by this engine
     */
    public GameEngine(Game game, GameScreen gameScreen) {
        _instance = this;

        this.game = game;
        //Import current game-state to access the game's renderer

        this.gameScreen = gameScreen;
        //Bind the engine to the main in-game interface
        //Required to alter the visuals and logic of the interface directly through this engine

        drawer = new Drawer(this.game);
        //Import QOL drawing function

        tiles = new Tile[16];
        //Initialise data for all 16 tiles on the screen
        //This instantiation does NOT automatically place the tiles on the game's main interface

        for (int i = 0; i < 16; i++) {
            final int fi = i;
            final GameScreen gs = gameScreen;

            tiles[i] = new Tile(this.game, i + 1, 5, 5, 5, null, new Runnable() {
                @Override
                public void run() {
                    gs.selectTile(tiles[fi], true);
                    selectedTile = tiles[fi];
                }
            });
        }
        //Configure all 16 tiles with independent yields and landmark data
        //Also assign listeners to them so that they can detect mouse clicks


        //Instantiates the game's market and hands it direct renderer access

        state = State.RUN;
        //Mark the game's current play-state as "running" (IE: not paused)

        // Added in Assessment 3: Stores each college in the college array.
        // ----------------------------------------------------------------
        colleges = new College[9];
        colleges[0] = new College(0, "Goodricke");
        colleges[1] = new College(1, "Derwent");
        colleges[2] = new College(2, "Langwith");
        colleges[3] = new College(3, "Alcuin");
        colleges[4] = new College(4, "Constantine");
        colleges[5] = new College(5, "Halifax");
        colleges[6] = new College(6, "James");
        colleges[7] = new College(7, "Vanbrugh");
        colleges[8] = new College(8, "Wentworth");
        // ----------------------------------------------------------------

        this.chancellor = new Chancellor(tiles);

        phase = 0;
        currentPlayerID = 0;
        trades = new Array<Trade>();
        try {
            setupEffects();
        } catch (InvalidResourceTypeException e) {
            e.printStackTrace();
        }

    }
    // ---------------------------------------------------------------------------------

    /**
     * Getter for the instance of the GameEngine
     * @return The GameEngine object
     */
    public static GameEngine getInstance() {
        return _instance;
    }

    /**
     * Setter fo the currently selected tile
     * @param tile The tile that has been selected
     */
    public void updateSelectedTileObject(Tile tile) {selectedTile = tile;}

    /**
     * Sets the currently selected tile to null
     */
    public void deselectTile() {
        selectedTile = null;

        gameScreen.selectedTileInfoTable.hideTileInfo();
    }

    /**
     * Advances the game's progress upon call
     * Acts as a state machine of sorts, moving the game from one phase to another depending on what phase it is
     * currently at when this method is called. If player 1 is the current player in any particular phase, then the
     * phase number remains and control is handed off to the other player: otherwise, control returns to player 1 and
     * the game advances to the next state, implementing any state-specific features as it goes.
     *
     * PHASE 1: Acquisition of Tiles
     * PHASE 2: Acquisition of Roboticons
     * PHASE 3: Placement of Roboticons
     * PHASE 4: Production of Resources by Roboticons
     * PHASE 5: Market Trading
     */

    // Changed in Assessment 3: Refactored nextPhase() from giant if-else statement to switch statement.
    public void nextPhase() {
        gameScreen.phaseInfoTable.timer.stop();

        nextPlayer();

        deselectTile();
        resetAuctionInterface();

        System.out.println("Player " + currentPlayerID + " | Phase " + phase);

        switch (phase) {
            case 1:
                tileAcquired = false;
                //Reset the tile-acquisition flag to allow for one more to be obtained

                drawer.toggleButton(gameScreen.endTurnButton(), false, Color.GRAY);
                //Stop the game from advancing until a tile has been claimed

                market.produceRoboticon();

                closeMarketInterface();
                //Close the market if phase 1 is beginning straight off the back of phase 5

                gameScreen.marketInterfaceTable.toggleAuctionAccess(true);
                break;

            case 2:
                gameScreen.phaseInfoTable.timer.setTime(0, 30);
                gameScreen.phaseInfoTable.timer.start();
                //Impose a time-limit on phase 2

                openRoboticonMarketInterface();
                //Allow for roboticons to be purchased

                drawer.toggleButton(gameScreen.endTurnButton(), true, Color.BLACK);
                break;

            case 3:
                gameScreen.phaseInfoTable.timer.setTime(0, 45);
                gameScreen.phaseInfoTable.timer.start();
                //Impose a time limit on phase 3

                closeMarketInterface();
            
                beginChancellorMode();
                //Setup the "Capture the Chancellor" minigame
                break;

            case 4:
                gameScreen.phaseInfoTable.timer.setTime(0, 0);
                gameScreen.phaseInfoTable.timer.stop();

                this.stopChancellorMode();
                //Stop the Chancellor's shenanigans

                produceResource();
            
                clearEffects();
                setEffects();

                gameScreen.playerInfoTable.showPlayerInventory(currentPlayer());
                break;

            case 5:
                openResourceMarketInterface();

                gameScreen.marketInterfaceTable.toggleAuctionAccess(false);
            
                if(checkGameEnd()){
                    System.out.println("Someone win");
                    gameScreen.showPlayerWin(getWinner());
                }
                break;
        }



        gameScreen.phaseInfoTable.updateLabels(phase);

        //If the upgrade overlay is open, close it when the next phase begins
        if (gameScreen.getUpgradeOverlayVisible()) {
            gameScreen.closeUpgradeOverlay();
        }

        if (isCurrentlyAiPlayer()) {
            AiPlayer aiPlayer = (AiPlayer)currentPlayer();
            aiPlayer.performPhase(this, gameScreen);
        } else {
            testTrade();
        }
    }

    /**
     * Produces resources for the current players according to the tiles that they own
     */
    private void produceResource() {
        for (Tile tile : currentPlayer().getTileList()) {
            tile.produce();
        }
    }

    /**
     * Sets the current player to be that which isn't active whenever this is called
     * Updates the in-game interface to reflect the statistics and the identity of the player now controlling it
     */
    private void nextPlayer() {
        currentPlayerID ++;
        if (currentPlayerID >= players.length) {
            currentPlayerID = 0;

            phase ++;
            if (phase >= 6) {
                phase = 1;
            }
            System.out.print("Move to phase " + phase + ", ");
        }
        System.out.println("Change to player " + currentPlayerID);

        // Find and draw the icon representing the "new" player's associated college
        if (!isCurrentlyAiPlayer()){
	        // Display the "new" player's inventory on-screen
            gameScreen.playerInfoTable.showPlayerInfo(currentPlayer());

            gameScreen.marketInterfaceTable.refreshPlayers(players, currentPlayer());
        }
    }

    /**
     * Pauses the game and opens the pause-menu (which is just a sub-stage in the GameScreen class)
     * Specifically pauses the game's timer and marks the engine's internal play-state to [State.PAUSE]
     */
    public void pauseGame() {
        gameScreen.phaseInfoTable.timer.stop();
        //Stop the game's timer

        state = State.PAUSE;
        //Mark that the game has been paused
    }

    /**
     * Resumes the game and re-opens the primary in-game inteface
     * Specifically increments the in-game timer by 1 second, restarts it and marks the engine's internal play-state
     * to [State.PAUSE]
     * Note that the timer is incremented by 1 second to circumvent a bug that causes it to lose 1 second whenever
     * it's restarted
     */
    public void resumeGame() {
        state = State.RUN;
        //Mark that the game is now running again

        if (gameScreen.phaseInfoTable.timer.minutes() > 0 || gameScreen.phaseInfoTable.timer.seconds() > 0) {
            gameScreen.phaseInfoTable.timer.increment();
            gameScreen.phaseInfoTable.timer.start();
        }
        //Restart the game's timer from where it left off
        //The timer needs to be incremented by 1 second before being restarted because, for a reason that I can't
        //quite identify, restarting the timer automatically takes a second off of it straight away
    }

    /**
     * Claims the last tile to have been selected on the main GameScreen for the active player
     * This grants them the ability to plant a Roboticon on it and yield resources from it for themselves
     * Specifically registers the selected tile under the object holding the active player's data, re-colours its
     * border for owner identification purposes and moves the game on to the next player/phase
     */
    public void claimTile() {
        if (phase == 1 && !selectedTile.isOwned()) {
            players[currentPlayerID].assignTile(selectedTile);
            //Assign selected tile to current player

            selectedTile.setOwner(players[currentPlayerID]);
            //Set the owner of the currently selected tile to be the current player

            tileAcquired = true;
            //Mark that a tile has been acquired on this turn

            switch (players[currentPlayerID].getCollege().getID()) {
                case 0:
                    //DERWENT
                    selectedTile.setTileBorderColor(Color.BLUE);
                    break;
                case 1:
                    //LANGWITH
                    selectedTile.setTileBorderColor(Color.CHARTREUSE);
                    break;
                case 2:
                    //VANBURGH
                    selectedTile.setTileBorderColor(Color.TEAL);
                    break;
                case 3:
                    //JAMES
                    selectedTile.setTileBorderColor(Color.CYAN);
                    break;
                case 4:
                    //WENTWORTH
                    selectedTile.setTileBorderColor(Color.MAROON);
                    break;
                case 5:
                    //HALIFAX
                    selectedTile.setTileBorderColor(Color.YELLOW);
                    break;
                case 6:
                    //ALCUIN
                    selectedTile.setTileBorderColor(Color.RED);
                    break;
                case 7:
                    //GOODRICKE
                    selectedTile.setTileBorderColor(Color.GREEN);
                    break;
                case 8:
                    //CONSTANTINE
                    selectedTile.setTileBorderColor(Color.PINK);
                    break;
            }
            //Set the colour of the tile's new border based on the college of the player who claimed it

            nextPhase(); // at ClaimTile
            //Advance the game
        }
    }

    /**
     * Deploys a Roboticon on the last tile to have been selected
     * Draws a Roboticon from the active player's Roboticon count and assigns it to the tile in question
     */
    public void deployRoboticon() {
        if (phase == 3) {
            if (!selectedTile.hasRoboticon()) {
                if (players[currentPlayerID].getRoboticonInventory() > 0) {
                    Roboticon Roboticon = new Roboticon(roboticonIDCounter, players[currentPlayerID], selectedTile);
                    selectedTile.assignRoboticon(Roboticon);
                    roboticonIDCounter += 1;
                    players[currentPlayerID].decreaseRoboticonInventory();
                    gameScreen.playerInfoTable.showPlayerInventory(currentPlayer());
                }
            }
        }
    }

    /**
     * Begins "Catch the Chancellor" mini-game
     */
    public void beginChancellorMode(){
        chancellor.activate();
        chancellor.updatePlayer(players[currentPlayerID]);
    }

    /**
     * Stops the "Catch the Chancellor" mini-game
     */
    public void stopChancellorMode(){ chancellor.deactivate(); }

    /**
     * Return's the game's current play-state, which can either be [State.RUN] or [State.PAUSE]
     * This is not to be confused with the game-state (which is directly linked to the renderer)
     *
     * @return State The game's current play-state
     */
    public State state() {
        return state;
    }

    /**
     * Getter for the chancellor object of the game
     * @return The chancellor object
     */

    public Chancellor chancellor() {
        return chancellor;
    }
    /**
     * Return's the game's phase as a number between (or possibly one of) 1 and 5
     *
     * @return int The game's current phase
     */
    public int phase() {
        return phase;
    }

    /**
     * Returns all of the data pertaining to the array of players managed by the game's engine
     * Unless the game's architecture changes radically, this should only ever return two Player objects
     *
     * @return Player[] An array of all Player objects (encapsulating player-data) managed by the engine
     */
    public Player[] players() { return players; }

    /**
     * Returns the data pertaining to the player who is active at the time when this is called
     *
     * @return Player The current user's Player object, encoding all of their data
     */
    public Player currentPlayer() { return players[currentPlayerID]; }

    /**
     * Returns the ID of the player who is active at the time when this is called
     *
     * @return int The current player's ID
     */
    public int currentPlayerID() {
        return currentPlayerID;
    }

    /**
     * Collectively returns every Tile managed by the engine in array
     *
     * @return Tile[] An array of all Tile objects (encapsulating tile-data) managed by the engine
     */
    public Tile[] tiles() {
        return tiles;
    }

    /**
     * Returns the data pertaining to the last Tile that was selected by a player
     *
     * @return Tile The last Tile to have been selected
     */
    public Tile selectedTile() {
        return selectedTile;
    }

    /**
     * Returns all of the data pertaining to the game's market, which is declared and managed by the engine
     *
     * @return Market The game's market
     */
    public Market market() {
        return market;
    }

    /**
     * Returns a value that's true if all tiles have been claimed, and false otherwise
     *
     * @return Boolean Determines if the game has ended or not
     */
    private boolean checkGameEnd(){
        for(Tile tile : tiles){
            if (tile.getOwner() == null){
                return false;
            }
        }

        return true;
    }

    /**
     * Updates the data pertaining to the game's current player
     * This is used by the Market class to process item transactions
     *
     * @param currentPlayer The new Player object to represent the active player with
     */
    public void updateCurrentPlayer(Player currentPlayer) {
        players[currentPlayerID] = currentPlayer;

        gameScreen.playerInfoTable.showPlayerInventory(currentPlayer());
        //Refresh the on-screen inventory labels to reflect the new object's possessions
    }

    /**
     * Function for upgrading a particular level of the roboticon stored on the last tile to have been selected
     * @param resource The type of resource which the roboticon will gather more of {0: ore | 1: energy | 2: food}
     */
    public void upgradeRoboticon(int resource) {
         if (selectedTile().getRoboticonStored().getLevel()[resource] < selectedTile().getRoboticonStored().getMaxLevel()) {
            switch (resource) {
                case (0):
                    currentPlayer().setResource(ResourceType.MONEY, currentPlayer().getResource(ResourceType.MONEY) - selectedTile.getRoboticonStored().getOreUpgradeCost());
                    break;
                case (1):
                    currentPlayer().setResource(ResourceType.MONEY, currentPlayer().getResource(ResourceType.MONEY) - selectedTile.getRoboticonStored().getEnergyUpgradeCost());
                    break;
                case (2):
                    currentPlayer().setResource(ResourceType.MONEY, currentPlayer().getResource(ResourceType.MONEY) - selectedTile.getRoboticonStored().getFoodUpgradeCost());
                    break;
            }

            selectedTile().getRoboticonStored().upgrade(resource);
        }

    }

    // Added in Assessment 3 from here down to EOF.

    /**
     * Getter for the current phase
     * @return The current phase
     */
    public int getPhase() {
        return phase;
    }

    /**
     * Calculates who won the game
     * @return The ID of the player who won the game
     */
    public int getWinner(){
        List<Player> playersList = Arrays.asList(players);
        Collections.sort(playersList, new Comparator<Player>() {
            @Override
            public int compare(Player a, Player b) {
                return b.calculateScore() - a.calculateScore();
            }
        });
        return playersList.get(0).getPlayerID();
    }

    /**
     * Returns true if the current player is AI
     * @return True if the current player is an AI player, false otherwise
     */
    public boolean isCurrentlyAiPlayer() {
        return currentPlayer().isAi();
    }


    /**
     * Adds a trade to the system
     * @param trade The trade to be added
     */
    public void addTrade(Trade trade){
    	trades.add(trade);
    }

    /**
     * Getter for the latest trade offer that has been sent
     * @return The trade that has been committed
     */
    public Trade getCurrentPendingTrade() {
        Iterator<Trade> it = trades.iterator();

        while (it.hasNext()) {
            Trade trade = it.next();
            if (trade.getTargetPlayer() == currentPlayer()) {
                it.remove();
                return trade;
            }
        }

        return null;
    }

    /**
     * Creates and initialises the players for the game
     * @param AIAmount The amount of AI players that are playing
     * @param playerAmount The amount fo human players that are playing
     */
    public void initialisePlayers(int AIAmount, int playerAmount){
    	int length = AIAmount + playerAmount;
    	
    	players = new Player[length];
    	for(int i = 0; i < playerAmount; i++){
    		Player player = new Player(i);
    		players[i] = player;
    		College college = colleges[i];
    		college.assignPlayer(player); //assigns a college to the player based on their playerID
    		player.assignCollege(college);
    	}
    	for(int i = playerAmount; i < length; i++){
    		Player player = new AiPlayer(i);
    		players[i] = player;
    		College college = colleges[i];
    		college.assignPlayer(player);
    		player.assignCollege(college);
    	}

    	currentPlayerID = length - 1;

        market = new Market();

        this.chancellor = new Chancellor(tiles);
    }

    /**
     * Tests whether there is a trade currently pending
     */
    public void testTrade(){
        Trade trade = getCurrentPendingTrade();
        if (trade == null) return ;
        gameScreen.activeTrade(trade);
    }

    /**
     * Closes the trade overlay
     */
    public void closeTrade(){
        gameScreen.closeTradeOverlay();
    }

    /**
     * Sets the current screen to the minigame screen
     */
    public void miniGame() {
        game.setScreen(new MiniGameScreen());
    }

    /**
     * Getter for the current game screen
     * @return The current game screen
     */
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    /**
     * Sets the current game screen to the main game screen
     */
    public void backToGame(){
        game.setScreen(getGameScreen());

    }

    /**
     * Creates and initialises all of the effects
     * @throws InvalidResourceTypeException
     */
    private void setupEffects() throws InvalidResourceTypeException {
        plotEffectSource = new PlotEffectSource(this);
        playerEffectSource = new PlayerEffectSource(this);

    }
    /**
     * Randomly applies the effects
     */
    private void setEffects() {
        Random RNGesus = new Random();
        int plotEffectIndex = RNGesus.nextInt(plotEffectSource.size);
        int playerEffectIndex = RNGesus.nextInt(playerEffectSource.size);

        if (RNGesus.nextInt(2) == 0) {
            plotEffectSource.get(plotEffectIndex).executeRunnable();
            if (!(isCurrentlyAiPlayer())) {
                gameScreen.showEventMessage(plotEffectSource.get(plotEffectIndex).getDescription());
            }
        } else {
            playerEffectSource.get(playerEffectIndex).executeRunnable();
            if (!(isCurrentlyAiPlayer())) {
                gameScreen.showEventMessage(playerEffectSource.get(playerEffectIndex).getDescription());
            }
        }
    }
    /**
     * Clears all imposed PlotEffects
     */
    private void clearEffects() {
        for (PlotEffect PE : plotEffectSource) {
            PE.revertAll();
        }
    }

    /**
     * Sets the functions of all the buttons within the market
     */
    public void setMarketButtonFunctions() {
        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.ORE, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.buy(ResourceType.ORE, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ORE, true, "-" + market.getOreBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ORE, false, "+" + market.getOreSellPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.ORE, market.getOreStock());

                    refreshMarketButtonAvailability();

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.ORE);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                    resetAuctionInterface();
                }
            }
        });

        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.ENERGY, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.buy(ResourceType.ENERGY, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ENERGY, true, "-" + market.getEnergyBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ENERGY, false, "+" + market.getEnergySellPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.ENERGY, market.getEnergyStock());

                    refreshMarketButtonAvailability();

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.ENERGY);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                    resetAuctionInterface();
                }
            }
        });

        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.FOOD, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.buy(ResourceType.FOOD, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.FOOD, true, "-" + market.getFoodBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.FOOD, false, "+" + market.getFoodSellPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.FOOD, market.getFoodStock());

                    refreshMarketButtonAvailability();

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.FOOD);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                    resetAuctionInterface();
                }
            }
        });

        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.ROBOTICON, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.buy(ResourceType.ROBOTICON, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ROBOTICON, true, "-" + market.getRoboticonBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.ROBOTICON, market.getRoboticonStock());

                    if (currentPlayer().getResource(ResourceType.MONEY) < market.getRoboticonBuyPrice() || market.getRoboticonStock() == 0) {
                        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ROBOTICON, true, false, Color.RED);
                    }

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.ROBOTICON);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);
                }
            }
        });

        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.ORE, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.sell(ResourceType.ORE, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ORE, true, "-" + market.getOreBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ORE, false, "+" + market.getOreSellPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.ORE, market.getOreStock());

                    refreshMarketButtonAvailability();

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.ORE);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                    resetAuctionInterface();
                }
            }
        });

        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.ENERGY, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.sell(ResourceType.ENERGY, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ENERGY, true, "-" + market.getEnergyBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ENERGY, false, "+" + market.getEnergySellPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.ENERGY, market.getEnergyStock());

                    refreshMarketButtonAvailability();

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.ENERGY);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                    resetAuctionInterface();
                }
            }
        });

        gameScreen.marketInterfaceTable.setMarketButtonFunction(ResourceType.FOOD, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (market.sell(ResourceType.FOOD, 1, currentPlayer())) {
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.FOOD, true, "-" + market.getFoodBuyPrice());
                    gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.FOOD, false, "+" + market.getFoodSellPrice());
                    gameScreen.marketInterfaceTable.setMarketStockText(ResourceType.FOOD, market.getFoodStock());

                    refreshMarketButtonAvailability();

                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.FOOD);
                    gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                    resetAuctionInterface();
                }
            }
        });
    }

    /**
     * Updates the appearance of the buttons within the market
     */
    public void refreshMarketButtonAvailability() {
        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getOreBuyPrice() && market.getOreStock() > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, true, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getEnergyBuyPrice() && market.getEnergyStock() > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, true, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getFoodBuyPrice() && market.getFoodStock() > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, true, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.ORE) > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, false, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, false, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.ENERGY) > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, false, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, false, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.FOOD) > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, false, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, false, false, Color.RED);
        }
    }

    /**
     * Initialises and opens the market interface
     */
    public void openResourceMarketInterface() {
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ORE, true, "-" + market.getOreBuyPrice());
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ORE, false, "+" + market.getOreSellPrice());
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ENERGY, true, "-" + market.getEnergyBuyPrice());
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ENERGY, false, "+" + market.getEnergySellPrice());
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.FOOD, true, "-" + market.getFoodBuyPrice());
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.FOOD, false, "+" + market.getFoodSellPrice());

        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getOreBuyPrice() && market.getOreStock() > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, true, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getEnergyBuyPrice() && market.getEnergyStock() > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, true, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getFoodBuyPrice()  && market.getFoodStock() > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, true, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.ORE) > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, false, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, false, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.ENERGY) > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, false, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, false, false, Color.RED);
        }

        if (currentPlayer().getResource(ResourceType.FOOD) > 0) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, false, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, false, false, Color.RED);
        }
    }

    /**
     * Initialises and opens the roboticon market interface
     */
    public void openRoboticonMarketInterface() {
        gameScreen.marketInterfaceTable.setMarketButtonText(ResourceType.ROBOTICON, true, "-" + market.getRoboticonBuyPrice());

        if (currentPlayer().getResource(ResourceType.MONEY) >= market.getRoboticonBuyPrice()) {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ROBOTICON, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ROBOTICON, true, false, Color.RED);
        }
    }

    /**
     * Closes the market interface
     */
    public void closeMarketInterface() {
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, true, false, Color.GRAY);
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ORE, false, false, Color.GRAY);
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, true, false, Color.GRAY);
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ENERGY, false, false, Color.GRAY);
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, true, false, Color.GRAY);
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.FOOD, false, false, Color.GRAY);
        gameScreen.marketInterfaceTable.toggleMarketButton(ResourceType.ROBOTICON, true, false, Color.GRAY);
    }

    /**
     * Resets the appearance of the auction interface
     */
    public void resetAuctionInterface() {
        gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.ORE, 0);
        gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.ENERGY, 0);
        gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.FOOD, 0);
        gameScreen.marketInterfaceTable.setTradePrice(0);

        if (currentPlayer().getResource(ResourceType.ORE) > 0) {
            gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, true, false, Color.RED);
        }
        gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, false, false, Color.RED);

        if (currentPlayer().getResource(ResourceType.ENERGY) > 0) {
            gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, true, false, Color.RED);
        }
        gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, false, false, Color.RED);

        if (currentPlayer().getResource(ResourceType.FOOD) > 0) {
            gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, true, true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, true, false, Color.RED);
        }
        gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, false, false, Color.RED);

        refreshAuctionPriceButtonAvailability();

        gameScreen.marketInterfaceTable.toggleAuctionConfirmationButton(false, Color.RED);
    }

    /**
     * Sets the functions of the buttons within the market interface
     */
    public void setAuctionButtonFunctions() {
        gameScreen.marketInterfaceTable.setAuctionQuantityButtonFunction(ResourceType.ORE, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.ORE, gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ORE) + 1);

                if (gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ORE) < currentPlayer().getResource(ResourceType.ORE)) {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, true, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, true, false, Color.RED);
                }

                gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, false, true, Color.GREEN);

                refreshAuctionConfirmationButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionQuantityButtonFunction(ResourceType.ENERGY, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.ENERGY, gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ENERGY) + 1);

                if (gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ENERGY) < currentPlayer().getResource(ResourceType.ENERGY)) {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, true, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, true, false, Color.RED);
                }

                gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, false, true, Color.GREEN);

                refreshAuctionConfirmationButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionQuantityButtonFunction(ResourceType.FOOD, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.FOOD, gameScreen.marketInterfaceTable.tradeAmount(ResourceType.FOOD) + 1);

                if (gameScreen.marketInterfaceTable.tradeAmount(ResourceType.FOOD) < currentPlayer().getResource(ResourceType.FOOD)) {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, true, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, true, false, Color.RED);
                }

                gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, false, true, Color.GREEN);

                refreshAuctionConfirmationButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionQuantityButtonFunction(ResourceType.ORE, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.ORE, gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ORE) - 1);

                if (gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ORE) > 0) {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, false, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, false, false, Color.RED);
                }

                gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ORE, true, true, Color.GREEN);

                refreshAuctionConfirmationButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionQuantityButtonFunction(ResourceType.ENERGY, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.ENERGY, gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ENERGY) - 1);

                if (gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ENERGY) > 0) {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, false, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, false, false, Color.RED);
                }

                gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.ENERGY, true, true, Color.GREEN);

                refreshAuctionConfirmationButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionQuantityButtonFunction(ResourceType.FOOD, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradeAmount(ResourceType.FOOD, gameScreen.marketInterfaceTable.tradeAmount(ResourceType.FOOD) - 1);

                if (gameScreen.marketInterfaceTable.tradeAmount(ResourceType.FOOD) > 0) {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, false, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, false, false, Color.RED);
                }

                gameScreen.marketInterfaceTable.toggleAuctionQuantityButton(ResourceType.FOOD, true, true, Color.GREEN);

                refreshAuctionConfirmationButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionPriceButtonFunction(1, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradePrice(gameScreen.marketInterfaceTable.tradePrice() + 1);

                refreshAuctionPriceButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionPriceButtonFunction(2, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradePrice(gameScreen.marketInterfaceTable.tradePrice() + 10);

                refreshAuctionPriceButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionPriceButtonFunction(3, true, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradePrice(gameScreen.marketInterfaceTable.tradePrice() + 100);

                refreshAuctionPriceButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionPriceButtonFunction(1, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradePrice(gameScreen.marketInterfaceTable.tradePrice() - 1);

                refreshAuctionPriceButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionPriceButtonFunction(2, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradePrice(gameScreen.marketInterfaceTable.tradePrice() - 10);

                refreshAuctionPriceButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionPriceButtonFunction(3, false, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.marketInterfaceTable.setTradePrice(gameScreen.marketInterfaceTable.tradePrice() - 100);

                refreshAuctionPriceButtonAvailability();
            }
        });

        gameScreen.marketInterfaceTable.setAuctionConfirmationButtonFunction(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean offerSent = false;

                for (Trade tradeInQueue : trades) {
                    if (tradeInQueue.getTargetPlayer() == gameScreen.marketInterfaceTable.selectedPlayer()) {
                        offerSent = true;

                        gameScreen.marketInterfaceTable.toggleAuctionConfirmationButton(false, Color.RED);
                        gameScreen.marketInterfaceTable.setAuctionConfirmationButtonText("Offer Already Sent");
                    }
                }

                if (offerSent == false) {
                    Trade trade = new Trade(gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ORE),
                            gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ENERGY),
                            gameScreen.marketInterfaceTable.tradeAmount(ResourceType.FOOD),
                            gameScreen.marketInterfaceTable.tradePrice(), currentPlayer(),
                            gameScreen.marketInterfaceTable.selectedPlayer());

                    trades.add(trade);

                    gameScreen.marketInterfaceTable.toggleAuctionConfirmationButton(false, Color.GREEN);
                    gameScreen.marketInterfaceTable.setAuctionConfirmationButtonText("Offer Sent Successfully!");
                }

                Timer timer = new Timer();
                timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        resetAuctionInterface();
                        gameScreen.marketInterfaceTable.setAuctionConfirmationButtonText("Send Offer to This Player");
                    }
                }, 3);
                timer.start();
            }
        });
    }

    /**
     * Sets the button functions of the upgrade overlay
     */
    public void setUpgradeOverlayButtonFunctions() {
        gameScreen.upgradeOverlay.setUpgradeButtonFunction(ResourceType.ORE, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                upgradeRoboticon(0);

                gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                gameScreen.upgradeOverlay.setRoboticonLevelLabelText(ResourceType.ORE, selectedTile.getRoboticonStored().getLevel()[0]);
                updateUpgradeOptions();
            }
        });

        gameScreen.upgradeOverlay.setUpgradeButtonFunction(ResourceType.ENERGY, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                upgradeRoboticon(1);

                gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                gameScreen.upgradeOverlay.setRoboticonLevelLabelText(ResourceType.ENERGY, selectedTile.getRoboticonStored().getLevel()[1]);
                updateUpgradeOptions();
            }
        });

        gameScreen.upgradeOverlay.setUpgradeButtonFunction(ResourceType.FOOD, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                upgradeRoboticon(2);

                gameScreen.playerInfoTable.updateResource(currentPlayer(), ResourceType.MONEY);

                gameScreen.upgradeOverlay.setRoboticonLevelLabelText(ResourceType.FOOD, selectedTile.getRoboticonStored().getLevel()[2]);
                updateUpgradeOptions();
            }
        });

        gameScreen.upgradeOverlay.setCloseUpgradeOverlayButtonFunction(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.closeUpgradeOverlay();
            }
        });
    }

    /**
     * Refreshes the values within the upgrade overlay
     */
    public void refreshUpgradeOverlay() {
        try {
            gameScreen.upgradeOverlay.setYieldLabelText(ResourceType.ORE, selectedTile.getResource(ResourceType.ORE));
            gameScreen.upgradeOverlay.setYieldLabelText(ResourceType.ENERGY, selectedTile.getResource(ResourceType.ENERGY));
            gameScreen.upgradeOverlay.setYieldLabelText(ResourceType.FOOD, selectedTile.getResource(ResourceType.FOOD));
        } catch (InvalidResourceTypeException e) {
            //Do nothing: this should never, ever happen
        }

        if (selectedTile.hasRoboticon()) {
            gameScreen.upgradeOverlay.setRoboticonLevelLabelText(ResourceType.ORE, selectedTile.getRoboticonStored().getLevel()[0]);
            gameScreen.upgradeOverlay.setRoboticonLevelLabelText(ResourceType.ENERGY, selectedTile.getRoboticonStored().getLevel()[1]);
            gameScreen.upgradeOverlay.setRoboticonLevelLabelText(ResourceType.FOOD, selectedTile.getRoboticonStored().getLevel()[2]);
        }

        updateUpgradeOptions();
    }

    /**
     * Updates the options available to the current player on the roboticon upgrade screen based on their money count
     */
    private void updateUpgradeOptions() {
        if (selectedTile().getRoboticonStored().getLevel()[0] < 3) {
            gameScreen.upgradeOverlay.setUpgradeButtonLabelText(ResourceType.ORE, "-" + selectedTile.getRoboticonStored().getOreUpgradeCost());
            if (currentPlayer().getResource(ResourceType.MONEY) >= selectedTile.getRoboticonStored().getOreUpgradeCost()) {
                gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.ORE, true, Color.GREEN);
            } else {
                gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.ORE, false, Color.RED);
            }
            //Conditionally enable ore upgrade button
        } else {
            gameScreen.upgradeOverlay.setUpgradeButtonLabelText(ResourceType.ORE, "MAX");
            gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.ORE, false, Color.RED);
        }

        if (selectedTile().getRoboticonStored().getLevel()[1] < 3) {
            gameScreen.upgradeOverlay.setUpgradeButtonLabelText(ResourceType.ENERGY, "-" + selectedTile.getRoboticonStored().getEnergyUpgradeCost());
            if (currentPlayer().getResource(ResourceType.MONEY) >= selectedTile.getRoboticonStored().getEnergyUpgradeCost()) {
                gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.ENERGY, true, Color.GREEN);
            } else {
                gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.ENERGY, false, Color.RED);
            }
            //Conditionally enable energy upgrade button
        } else {
            gameScreen.upgradeOverlay.setUpgradeButtonLabelText(ResourceType.ENERGY, "MAX");
            gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.ENERGY, false, Color.RED);
        }

        if (selectedTile().getRoboticonStored().getLevel()[2] < 3) {
            gameScreen.upgradeOverlay.setUpgradeButtonLabelText(ResourceType.FOOD, "-" + selectedTile.getRoboticonStored().getFoodUpgradeCost());
            if (currentPlayer().getResource(ResourceType.MONEY) >= selectedTile.getRoboticonStored().getFoodUpgradeCost()) {
                gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.FOOD, true, Color.GREEN);
            } else {
                gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.FOOD, false, Color.RED);
            }
            //Conditionally enable food upgrade button
        } else {
            gameScreen.upgradeOverlay.setUpgradeButtonLabelText(ResourceType.FOOD, "MAX");
            gameScreen.upgradeOverlay.toggleUpgradeButton(ResourceType.FOOD, false, Color.RED);
        }
    }

    /**
     * Updates the appearance of the auction table
     */
    public void refreshAuctionPriceButtonAvailability() {
        if (gameScreen.marketInterfaceTable.tradePrice() < gameScreen.marketInterfaceTable.selectedPlayer().getResource(ResourceType.MONEY)) {
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(1, true, true, Color.GREEN);

            if (gameScreen.marketInterfaceTable.tradePrice() + 10 <= gameScreen.marketInterfaceTable.selectedPlayer().getResource(ResourceType.MONEY)) {
                gameScreen.marketInterfaceTable.toggleAuctionPriceButton(2, true, true, Color.GREEN);

                if (gameScreen.marketInterfaceTable.tradePrice() + 100 <= gameScreen.marketInterfaceTable.selectedPlayer().getResource(ResourceType.MONEY)) {
                    gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, true, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, true, false, Color.RED);
                }
            } else {
                gameScreen.marketInterfaceTable.toggleAuctionPriceButton(2, true, false, Color.RED);
                gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, true, false, Color.RED);
            }
        } else {
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(1, true, false, Color.RED);
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(2, true, false, Color.RED);
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, true, false, Color.RED);
        }

        if (gameScreen.marketInterfaceTable.tradePrice() > 0) {
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(1, false, true, Color.GREEN);

            if (gameScreen.marketInterfaceTable.tradePrice() - 10 >= 0) {
                gameScreen.marketInterfaceTable.toggleAuctionPriceButton(2, false, true, Color.GREEN);

                if (gameScreen.marketInterfaceTable.tradePrice() - 100 >= 0) {
                    gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, false, true, Color.GREEN);
                } else {
                    gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, false, false, Color.RED);
                }
            } else {
                gameScreen.marketInterfaceTable.toggleAuctionPriceButton(2, false, false, Color.RED);
                gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, false, false, Color.RED);
            }
        } else {
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(1, false, false, Color.RED);
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(2, false, false, Color.RED);
            gameScreen.marketInterfaceTable.toggleAuctionPriceButton(3, false, false, Color.RED);
        }

        refreshAuctionConfirmationButtonAvailability();
    }

    /**
     * Updates the appearance of the auction table
     */
    public void refreshAuctionConfirmationButtonAvailability() {
        if ((gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ORE) > 0
                || gameScreen.marketInterfaceTable.tradeAmount(ResourceType.ENERGY) > 0
                || gameScreen.marketInterfaceTable.tradeAmount(ResourceType.FOOD) > 0)
                && gameScreen.marketInterfaceTable.tradePrice() > 0) {
            gameScreen.marketInterfaceTable.toggleAuctionConfirmationButton(true, Color.GREEN);
        } else {
            gameScreen.marketInterfaceTable.toggleAuctionConfirmationButton(false, Color.RED);
        }
    }

    /**
     * Removes a specific trade from the array  of pending trades
     * @param trade The trade to be removed
     */
    public void removeTrade(Trade trade) {
        trades.removeValue(trade, true);
    }

    /**
     * Encodes possible play-states
     * These are not to be confused with the game-state (which is directly linked to the renderer)
     */
    public enum State {
        RUN,
        PAUSE
    }
}