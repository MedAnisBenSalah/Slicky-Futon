package com.ormisiclapps.slickyfuton.game.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.TimeUtils;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.nodes.level.LevelNode;
import com.ormisiclapps.slickyfuton.game.nodes.save.SaveNode;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.screens.GameScreen;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.game.world.Lightening;
import com.ormisiclapps.slickyfuton.game.world.Terrain;
import com.ormisiclapps.slickyfuton.utility.Configuration;

/**
 * Created by OrMisicL on 6/1/2016.
 */
public class GameLogic
{
    private GameIntelligence gameIntelligence;
    private GameWorld gameWorld;
    private Lightening lightening;
    private Player player;
    private Terrain terrain;
    private long score;
    private long lastScore;
    private boolean newRecord;
    private float distanceTraveled;
    private boolean initialized;
    private boolean caveMode;
    private long caveModeStartTime;
    private float lastPlayerPosition;
    private boolean firstGame;
    private GameMode gameMode;
    private int coinsInThisGame;
    private boolean endless;
    private LevelNode currentLevelNode;
    private boolean lethalGround;
    private boolean levelCompleted;
    private int levelCompletion;

    private static GameLogic instance;

    private static final int CAVE_MODE_DURATION = 20000;

    public GameLogic()
    {
        // Reset score
        score = 0;
        lastScore = 0;
        levelCompletion = 0;
        // Reset flags
        initialized = false;
        caveMode = false;
        newRecord = false;
        firstGame = true;
        endless = false;
        levelCompleted = false;
        // Reset instances
        currentLevelNode = null;
        // Set to the default game mode
        gameMode = GameMode.GAME_MODE_FLYING;
        // Set instance
        instance = this;
    }

    public void setup()
    {
        // Create instances
        gameIntelligence = new GameIntelligence();
        gameWorld = new GameWorld();
        terrain = new Terrain();
        player = new Player();
        lightening = new Lightening(gameWorld.getWorld());
        // Create the player
        player.create();
        // Initialize game intelligence
        gameIntelligence.initialize();
        // Create the saved data if it doesn't exist
        if(Core.getInstance().getStatsSaver().savedData == null)
            Core.getInstance().getStatsSaver().savedData = new SaveNode();
    }

    public void initialize()
    {
        // If we're already initialized then don't initialize
        if(initialized)
            return;

        // Set to the default game mode
        gameMode = GameMode.GAME_MODE_FLYING;
        // Setup the player
        player.setup();
        // Initialize the terrain
        terrain.initialize();
        // Initialize game intelligence
        gameIntelligence.initialize();
        // Reset camera's interpolation
        Camera.getInstance().resetInterpolatingToSide();
        // Disable the cave mode
        toggleCaveMode(false);
        // Reset score
        score = 0;
        levelCompletion = 0;
        // Reset distance traveled
        distanceTraveled = 0;
        // Reset last player position
        lastPlayerPosition = 0f;
        // Reset coins in this game
        coinsInThisGame = 0;
        // Reset flags
        newRecord = false;
        levelCompleted = false;
        // Mark as initialized
        initialized = true;
    }

    public void startGame()
    {
        // Increase the games played count
        Core.getInstance().getStatsSaver().savedData.gamesPlayed++;
        // Increase the level's attempts
        if(!endless)
            Core.getInstance().getStatsSaver().savedData.levelAttempts[currentLevelNode.id - 1]++;

        // Reset the first game flag
        firstGame = false;
        // Set the camera's position to be in front of the player
        Camera.getInstance().setPosition(new Vector2(70f, Configuration.TERRAIN_ROOF_POSITION / 2f));
        // Interpolate to player
        Camera.getInstance().resetInterpolatingToSide();
        Camera.getInstance().resetRightSidedFollow();
        Camera.getInstance().interpolateToEntity(Player.getInstance(), 30f);
    }

    public void process(boolean updatePlayer)
    {
        // Ensure we're initialized
        if(!initialized)
            return;

        // Finish cave mode
        if(caveMode && TimeUtils.millis() - caveModeStartTime >= CAVE_MODE_DURATION)
            // Toggle off the cave mode
            toggleCaveMode(false);

        // Process the game intelligence
        gameIntelligence.process();
        // Process the terrain
        terrain.process();
        // Process the player
        if(updatePlayer)
            player.process();

        // Start the player if necessary
        if(!player.isStarted() && player.getPosition().x >= Configuration.START_POSITION)
            // Start the player
            GameScreen.getInstance().onPlayerStart();
        // Check the best score
        if(score > Core.getInstance().getStatsSaver().savedData.bestScore)
        {
            // Set the new best score
            Core.getInstance().getStatsSaver().savedData.bestScore = score;
            // Set as new record
            newRecord = true;
        }
        // Get the traveled distance
        if(player.isStarted())
        {
            // Calculate the new traveled distance
            float newDistance = player.getPosition().x - lastPlayerPosition;
            // Set the traveled distance
            distanceTraveled += newDistance;
            // Check for the longest distance
            if(distanceTraveled > Core.getInstance().getStatsSaver().savedData.longestDistance)
                Core.getInstance().getStatsSaver().savedData.longestDistance = distanceTraveled;

            // Set the last player position
            lastPlayerPosition = player.getPosition().x;
            // Do we need to toggle spikes ?
            if(!terrain.isLevelMode() && !lethalGround && gameMode == GameMode.GAME_MODE_FLYING && score >= Configuration.LETHAL_GROUND_SCORE)
                toggleLethalGround(true);
        }
        else
            // Set the last player position
            lastPlayerPosition = player.getPosition().x;
    }

    public void updateTerrain()
    {
        // Find if we need to switch to cave mode
        if(!caveMode && gameIntelligence.isCaveMode())
            // Toggle cave mode
            toggleCaveMode(true);
    }

    public void destroy()
    {
        // Destroy all game instances
        player.destroy();
        terrain.destroy();
        lightening.destroy();
        // One more process before destroying
        gameWorld.postProcess();
        // Destroy the game world
        gameWorld.destroy();
    }

    public void addScore()
    {
        // Increase the score
        score++;
    }

    public void onLostGame()
    {
        // Save the score
        lastScore = score;
        // Reset the score
        score = 0;
        // Notify the game lost for the game screen
        GameScreen.getInstance().onLostGame();
        // Reset the terrain
        terrain.reset();
        // Reset initialized flag
        initialized = false;
    }

    public void onLevelCompleted()
    {
        // Set level completed flag
        levelCompleted = true;
        // Set level completion
        levelCompletion = 100;
        // Add the reward
        coinsInThisGame += currentLevelNode.reward;
        Core.getInstance().getStatsSaver().savedData.eatablesCollected += currentLevelNode.reward;
        // Set the game completed save
        Core.getInstance().getStatsSaver().savedData.levelCompleted[currentLevelNode.id - 1] = true;
        // Notify the level completion for the game screen
        GameScreen.getInstance().onLevelCompleted();
        // Reset the terrain
        terrain.reset();
        // Reset initialized flag
        initialized = false;
    }

    public void eatObject(Body object)
    {
        // Notify the eaten object to the terrain
        terrain.eatObject(object);
        // Notify the object on the game screen
        Core.getInstance().getScreensManager().getGameScreen().eatObject(object.getPosition());
        // Increase eatables
        Core.getInstance().getStatsSaver().savedData.eatablesCollected++;
        // Add coins for this game
        coinsInThisGame++;
    }

    public void toggleCaveMode(boolean toggle)
    {

        // Toggle the cave lightening
        lightening.toggleCaveLight(toggle);
        // Toggle the cave mode for the player
        player.toggleCaveMode(toggle);
        // Toggle cave mode for the rest of the objects
        terrain.toggleCaveMode(toggle);
        // Set the cave mode flag
        caveMode = toggle;
        // Set the starting time if we're toggling on
        if(toggle)
            caveModeStartTime = TimeUtils.millis();
    }

    public void calculateLevelCompletion()
    {
        // Are we in level mode ?
        if(!endless)
        {
            // Calculate the level completion
            float completion = Core.getInstance().getCurrentMusic().getPosition() / 210f * 100f;
            levelCompletion = (int)completion;
        }
    }

    public GameMode getGameMode() { return gameMode; }

    public void setGameMode(GameMode gameMode)
    {
        // Set the new game mode
        this.gameMode = gameMode;
    }

    public void toggleLethalGround(boolean toggle)
    {
        // Set the lethal ground flag
        lethalGround = toggle;
    }

    public static GameLogic getInstance()
    {
        return instance;
    }

    public Player getPlayer() { return player; }
    public Terrain getTerrain() { return terrain; }
    public long getScore() { return score; }
    public long getLastScore() { return lastScore; }
    public boolean isFirstGame() { return firstGame; }
    public boolean isNewRecord() { return newRecord; }
    public int getCoinsInThisGame() { return coinsInThisGame; }

    public boolean isEndless() {
        return endless;
    }

    public void setEndless(boolean endless) {
        this.endless = endless;
    }

    public LevelNode getCurrentLevelNode() {
        return currentLevelNode;
    }

    public void setCurrentLevelNode(LevelNode currentLevelNode) {
        this.currentLevelNode = currentLevelNode;
    }

    public boolean isLethalGround() {
        return lethalGround;
    }

    public boolean isLevelCompleted() { return levelCompleted; }

    public int getLevelCompletion() { return levelCompletion; }
}
