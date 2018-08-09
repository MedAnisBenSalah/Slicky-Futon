package com.ormisiclapps.slickyfuton.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.ormisiclapps.slickyfuton.audio.GameMusic;
import com.ormisiclapps.slickyfuton.audio.GameSound;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.game.world.Lightening;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIText;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by OrMisicL on 6/1/2016.
 */
public class GameScreen implements Screen
{
    private GameLogic gameLogic;
    private Camera camera;
    private GameMusic[] gameMusics;
    private GameMusic currentMusic;
    private GameSound dyingSound;
    private boolean gameStarted;
    private UIText scoreText, eatableText, startingMessageText, pauseText, levelText;
    private UIButton pauseButton, resumeButton;
    private Vector2 tmpVector;
    private Vector2 tmpVector2;
    private Vector2 pausedTextPosition;
    private TextureRegion headphonesTexture;
    private Color tmpColor;
    private Map<Vector2, Long> eatableTexts;
    private String startingMessage;
    private Vector2 tutorialPosition, tutorialSize;
    private boolean paused;
    private float playButtonInitialSize;
    private boolean playButtonIncreaseSize;
    private boolean resuming;
    private int seconds;
    private long secondTick;
    private long lastReadyTime;
    private int lastGameMusic;
    private boolean deadPlayer;

    private static GameScreen instance;

    private static final float AUDIO_VOLUME = 1f;
    private static final float TEXT_TIME = 300f;
    private static final float PAUSE_FADE = 0.7f;
    private static final int GAME_MUSICS = 4;
    private static final float FADE_ALPHA = 0.6f;

    public GameScreen()
    {
        // Reset instances
        gameLogic = null;
        camera = null;
        gameMusics = null;
        dyingSound = null;
        eatableTexts = null;
        startingMessageText = null;
        pauseButton = null;
        resumeButton = null;
        pausedTextPosition = null;
        levelText = null;
        headphonesTexture = null;
        // Reset flags
        gameStarted = false;
        paused = false;
        resuming = false;
        playButtonIncreaseSize = false;
        deadPlayer = false;
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        tutorialPosition = new Vector2();
        tutorialSize = new Vector2();
        // Create tmp color
        tmpColor = new Color(1f, 1f, 1f, 1f);
        // Reset strings
        startingMessage = "";
        // Reset floats
        playButtonInitialSize = 0f;
        // Reset values
        lastGameMusic = -1;
        // Save the instance
        instance = this;
    }

    @Override
    public void initialize()
    {
        // Create the game logic instance
        camera = new Camera();
        gameLogic = new GameLogic();
        // Create the game musics arrays
        gameMusics = new GameMusic[GAME_MUSICS];
        // Create every game music
        for(int i = 0; i < GAME_MUSICS; i++)
            gameMusics[i] = new GameMusic("GameMusic" + (i + 1));

        // Create the dying sound instance
        dyingSound = new GameSound("Dying");
        // Create the eatable texts array
        eatableTexts = new HashMap<Vector2, Long>();
        // Create the score text
        int size = Core.getInstance().getGraphicsManager().HEIGHT / 17;
        scoreText = new UIText(size, Color.WHITE, size / 12f, new Color(1f, 1f, 1f, 0.2f));
        // Create the eatable text
        size = Core.getInstance().getGraphicsManager().HEIGHT / 30;
        eatableText = new UIText(size, Color.WHITE, 0, null);
        // Create the starting message text
        size = Core.getInstance().getGraphicsManager().HEIGHT / 25;
        startingMessageText = new UIText(size, Color.WHITE, size / 9f, new Color(1f, 1f, 1f, 0.2f));
        // Create the pause text
        size = Core.getInstance().getGraphicsManager().HEIGHT / 10;
        pauseText = new UIText(size, Color.WHITE, 0, null);
        // Create the level text
        levelText = new UIText(size, Color.WHITE, size / 15f, new Color(0.2f, 0.4f, 0.6f, 0.7f));
        // Set it as not fade effected
        pauseText.setFadeEffected(false);
        // Create the pause button
        pauseButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/PauseButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set its parameters
        float pauseButtonSize = Core.getInstance().getGraphicsManager().HEIGHT / 16f;
        Vector2 buttonSize = new Vector2(pauseButtonSize, pauseButtonSize);
        pauseButton.setSize(buttonSize);
        pauseButton.setPosition(new Vector2(Core.getInstance().getGraphicsManager().WIDTH - buttonSize.x * 2f,
                Core.getInstance().getGraphicsManager().HEIGHT - buttonSize.y * 1.5f));

        // Create the resume button
        resumeButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/PlayButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set their positions
        float playButtonSize = Core.getInstance().getGraphicsManager().HEIGHT / 5f;
        resumeButton.setSize(new Vector2(playButtonSize, playButtonSize));
        resumeButton.setPosition(new Vector2(GameMath.getCenteredPosition(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR,
                new Vector2(playButtonSize, playButtonSize))));


        // Set UI elements to be not fade effected
        resumeButton.setFadeEffected(false);
        startingMessageText.setFadeEffected(false);
        // Save the player button initial's size
        playButtonInitialSize = playButtonSize;
        // Add elements to the main UI
        UIMain.getInstance().addWidget(scoreText);
        UIMain.getInstance().addWidget(eatableText);
        UIMain.getInstance().addWidget(startingMessageText);
        UIMain.getInstance().addWidget(pauseText);
        UIMain.getInstance().addWidget(levelText);
        UIMain.getInstance().addWidget(pauseButton);
        UIMain.getInstance().addWidget(resumeButton);
        // Get the paused text size
        Vector2 pausedTextSize = pauseText.getSize("PAUSED");
        pausedTextPosition = new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2 - pausedTextSize.x / 2,
                Core.getInstance().getGraphicsManager().HEIGHT * 0.75f - pausedTextSize.y / 2);

        // Setup the tutorial size
        tutorialSize.set(Core.getInstance().getGraphicsManager().WIDTH / 6f, Core.getInstance().getGraphicsManager().WIDTH / 6f);
        // Get the headphones texture
        headphonesTexture = Core.getInstance().getResourcesManager().getResource("UI/Headphones", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Setup the game logic
        gameLogic.setup();
    }

    @Override
    public void activate()
    {
        // Activate UI elements
        scoreText.toggle(false);
        eatableText.toggle(true);
        startingMessageText.toggle(true);
        pauseButton.toggle(false);
        resumeButton.toggle(false);
        pauseText.toggle(true);
        levelText.toggle(true);
        // Get a random starting message
        startingMessage = gameLogic.isEndless() ? GameIntelligence.getInstance().getStartingMessage() : gameLogic.getCurrentLevelNode().message;
        // Initialize the game logic
        gameLogic.initialize();
        // Set the camera free
        Camera.getInstance().setPosition(new Vector2(Player.NOT_STARTED_POSITION, Configuration.TERRAIN_ROOF_POSITION / 2f));
        Camera.getInstance().setFree();
        // Disable player lights
        Player.getInstance().setLightState(false);
        // Reset the game started flag
        gameStarted = false;
        // Reset dead flag
        deadPlayer = false;
        // Hide the ad banner
        Core.getInstance().getOSUtility().hideBannerAd();
        // Set the tutorial position
        if(!GameLogic.getInstance().isEndless())
            tutorialPosition.set(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR).sub(tutorialSize.x / 2f, tutorialSize.y / 2f);
    }

    @Override
    public void update()
    {
        // Are we still transiting ?
        if(Core.getInstance().getScreensManager().isTransitioning())
        {
            // Are we fading out ?
            if(Core.getInstance().getScreensManager().getTransitionScreen() instanceof GameScreen &&
                    Core.getInstance().getGraphicsManager().getScreenEffects().isTransitionFadeOut())
            {
                // Get the transition color
                Color color = Core.getInstance().getGraphicsManager().getScreenEffects().getTransitionColor();
                // If the alpha reached the fading's value then halt the transition
                if(color.a <= FADE_ALPHA)
                {
                    // Halt the transition
                    Core.getInstance().getGraphicsManager().getScreenEffects().haltTransition();
                    // Fade the screen
                    Core.getInstance().getGraphicsManager().getScreenEffects().fadeIn(Color.BLACK, FADE_ALPHA);
                }

            }
        }
        // Check for the pause button click
        if(pauseButton.isClicked())
            pause();

        // Don't update if we're paused
        if(paused)
        {
            // Are we resuming ?
            if(resuming)
            {
                // Should we change the second tick
                if(TimeUtils.millis() - secondTick >= 1000)
                {
                    // Decrease the seconds
                    seconds--;
                    // Reset the second tick
                    secondTick = TimeUtils.millis();
                }
                // Should we resume ?
                if(seconds <= 0)
                {
                    // Reset resuming flag
                    resuming = false;
                    // Reset paused flag
                    paused = false;
                    // Resume the game music
                    currentMusic.resume();
                    // Show UI elements
                    pauseButton.toggle(true);
                    if(gameLogic.isEndless())
                        scoreText.toggle(true);

                    levelText.toggle(true);
                }
                else
                {
                    // Get the text size
                    Vector2 size = pauseText.getSize(Integer.toString(seconds));
                    // Draw the seconds
                    pauseText.drawText(Integer.toString(seconds), tmpVector.set(
                            Core.getInstance().getGraphicsManager().WIDTH / 2 - size.x / 2, Core.getInstance().getGraphicsManager().HEIGHT / 2 - size.y / 2));
                }
            }
            else
            {
                // Animate the play button
                tmpVector.set(resumeButton.getSize()).add(playButtonIncreaseSize ? MainMenuScreen.PLAY_BUTTON_SIZE_INCREASE : -MainMenuScreen.PLAY_BUTTON_SIZE_INCREASE,
                        playButtonIncreaseSize ? MainMenuScreen.PLAY_BUTTON_SIZE_INCREASE : -MainMenuScreen.PLAY_BUTTON_SIZE_INCREASE);

                // Check if we've reached the increasing limit
                if(playButtonIncreaseSize && tmpVector.x >= playButtonInitialSize * 1.15f)
                {
                    // Invert the increasing flag
                    playButtonIncreaseSize = false;
                    // Fix its position
                    tmpVector.set(playButtonInitialSize * 1.15f, playButtonInitialSize * 1.15f);
                }
                else if (!playButtonIncreaseSize && tmpVector.x <= playButtonInitialSize * 0.85f)
                {
                    // Invert the increasing flag
                    playButtonIncreaseSize = true;
                    // Fix its position
                    tmpVector.set(playButtonInitialSize * 0.85f, playButtonInitialSize * 0.85f);
                }
                // Set the button's size
                resumeButton.setSize(tmpVector);
                // Fix its position to be centered
                resumeButton.setPosition(GameMath.getCenteredPosition(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR,
                        tmpVector));

                // Draw the paused text
                pauseText.drawText("PAUSED", pausedTextPosition);
                // Check for the resume button click
                if (resumeButton.isClicked())
                    resume();
            }
            return;
        }
        /*if(!Core.getInstance().isDebugging)
        {
            // Update the physics world
            gameLogic.getGameWorld().process();
            // Post process the world
            gameLogic.getGameWorld().postProcess();
        }*/
        // Update the physics world
        GameWorld.getInstance().process();
        // Post process the world
        GameWorld.getInstance().postProcess();
        // Update the camera
        camera.update();
        // Update the game logic
        gameLogic.process(gameStarted);
        // Process the not started game
        if(!gameStarted && !deadPlayer)
        {
            if(Core.getInstance().getInputHandler().isScreenTouched() && !Core.getInstance().getScreensManager().isTransitioning() &&
                    !Core.getInstance().getInputHandler().isUnreportedTouch())
            {
                // Start the game
                gameStarted = true;
                // Start the game
                gameLogic.startGame();
                // Get the game music
                if(GameLogic.getInstance().isEndless())
                {
                    // Find a random game music
                    int musicId = MathUtils.random(GAME_MUSICS - 1);
                    while(musicId == lastGameMusic)
                        musicId = MathUtils.random(GAME_MUSICS - 1);

                    currentMusic = gameMusics[musicId];
                    // Save the last music id
                    lastGameMusic = musicId;
                }
                else
                    currentMusic = new GameMusic(GameLogic.getInstance().getCurrentLevelNode().musicFile);

                // Play it
                // NOTE: FUCK YOU LIBGDX ! FUCK YOU !!!!
                currentMusic.setPosition(0f);
                currentMusic.play(false, AUDIO_VOLUME);
                // Set it as the game's current music
                Core.getInstance().setCurrentMusic(currentMusic);
                // Set the player's initial position
                gameLogic.getPlayer().teleport(new Vector2(0f, gameLogic.getPlayer().getPosition().y));
                // Toggle player lights accordingly
                Player.getInstance().setLightState(Core.getInstance().getStatsSaver().savedData.lightState);
                // Update the game logic
                gameLogic.process(true);
                // Fade the screen out
                Core.getInstance().getGraphicsManager().getScreenEffects().fadeOut();
            }
            else
            {
                // Update camera
                tmpVector.set(Camera.getInstance().getPosition()).add(0.25f, 0f);
                Camera.getInstance().setPosition(tmpVector);
                // Restore camera's position
                /*if(gameLogic.getPlayer().getPosition().x >= Player.NOT_STARTED_POSITION / 6f * 5f)
                    gameLogic.getPlayer().teleport(tmpVector.set(Player.NOT_STARTED_POSITION, gameLogic.getPlayer().getPosition().y));*/

                // Fix player position
                gameLogic.getPlayer().setPosition(tmpVector.set(Player.NOT_STARTED_POSITION, Configuration.TERRAIN_ROOF_POSITION / 2f));
                // Restore camera's position
                if(Camera.getInstance().getPosition().x >= Player.NOT_STARTED_POSITION / 4f)
                    Camera.getInstance().setPosition(tmpVector.set(Player.NOT_STARTED_POSITION, Configuration.TERRAIN_ROOF_POSITION / 2f));

                // Get the text size
                Vector2 size = startingMessageText.getSize(startingMessage);
                // Calculate the text position
                tmpVector.set(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR.x, Core.getInstance().getGraphicsManager().HEIGHT / 5 * 4).sub
                        (size.x / 2, size.y * 1.75f);

                // Draw the text
                startingMessageText.drawText(startingMessage, tmpVector);
            }
        }
        else
        {
            // Did the player start yet ?
            if(!Player.getInstance().isStarted())
            {
                // Draw the ready text
                Vector2 size = pauseText.getSize("HOLD !");
                tmpVector.set(GameMath.getCenteredPosition(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR, size));
                pauseText.drawText("HOLD !", tmpVector);
                // Update the last ready time
                lastReadyTime = TimeUtils.millis();
            }
            else if(TimeUtils.millis() - lastReadyTime < 1000)
            {
                // Draw the go text
                Vector2 size = pauseText.getSize("GO !");
                tmpVector.set(GameMath.getCenteredPosition(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR, size));
                pauseText.drawText("GO !", tmpVector);

            }
            // Get the current player's position
            tmpVector2.set(Camera.getInstance().worldToScreen(Player.getInstance().getPosition()));
            // Loop through the eatable texts
            Iterator<Vector2> iterator = eatableTexts.keySet().iterator();
            while(iterator.hasNext())
            {
                // Get the position
                Vector2 position = iterator.next();
                // Get the started time
                Long startedTime = eatableTexts.get(position);
                // Calculate the texts alpha
                float alpha = (TEXT_TIME - (float)(TimeUtils.millis() - startedTime)) / TEXT_TIME;
                // Check if we need to remove it
                if(alpha <= 0)
                {
                    // Remove the position
                    iterator.remove();
                    continue;
                }
                // Set the text's alpha
                tmpColor.a = alpha;
                eatableText.setColor(tmpColor);
                // Draw the text
                tmpVector.set(Camera.getInstance().worldToScreen(position));
                tmpVector.x = tmpVector2.x;
                tmpVector.y += (float)(TimeUtils.millis() - startedTime) / 10f;
                eatableText.drawText("+1", tmpVector);
            }
            if(GameLogic.getInstance().isEndless())
            {
                // Compute the score text size
                Vector2 size = scoreText.getSize(Long.toString(gameLogic.getScore()));
                // Draw the text
                scoreText.drawText(Long.toString(gameLogic.getScore()), tmpVector.set(Core.getInstance().getGraphicsManager().WIDTH / 2 - size.x / 2,
                        Core.getInstance().getGraphicsManager().HEIGHT / 8 * 7 + size.y / 2));
            }
            // Change the music if its over
            if(GameLogic.getInstance().isEndless() && !currentMusic.isPlaying() && !Player.getInstance().isDying() &&
                    !Core.getInstance().getScreensManager().isTransitioning() && !deadPlayer)
            {
                // Find a random game music
                int musicId = MathUtils.random(GAME_MUSICS - 1);
                while(musicId == lastGameMusic)
                    musicId = MathUtils.random(GAME_MUSICS - 1);

                currentMusic = gameMusics[musicId];
                // Save the last music id
                lastGameMusic = musicId;
                // Play it
                currentMusic.play(false, AUDIO_VOLUME);
                // Set it as the game's current music
                Core.getInstance().setCurrentMusic(currentMusic);
            }
        }
    }

    @Override
    public void render()
    {
        // Render the terrain
        gameLogic.getTerrain().render();
        // Render the player
        if(!Core.getInstance().getScreensManager().isTransitioning() && gameStarted)
            gameLogic.getPlayer().render();

    }

    @Override
    public void postFadeRender()
    {
        // Draw the headphones texture
        if(!GameLogic.getInstance().isEndless() && !gameStarted)
            Core.getInstance().getGraphicsManager().drawTextureRegion(headphonesTexture, tutorialPosition, tutorialSize, 270f);
    }

    public void onPlayerStart()
    {
        // Start the player
        Player.getInstance().start();
        // Show game HUD
        pauseButton.toggle(true);
        if(gameLogic.isEndless())
            scoreText.toggle(true);
    }

    @Override
    public void deactivate()
    {
        // Hide UI elements
        scoreText.toggle(false);
        eatableText.toggle(false);
        startingMessageText.toggle(false);
        pauseButton.toggle(false);
        resumeButton.toggle(false);
        pauseText.toggle(false);
        levelText.toggle(false);
        // Set the game not started
        gameStarted = false;
        // Restore player's position
        Player.getInstance().setPosition(new Vector2(Player.NOT_STARTED_POSITION, Configuration.TERRAIN_ROOF_POSITION / 2f));
        // Free the camera
        Camera.getInstance().setFree();
    }

    public void dyingSound()
    {
        // Calculate the level completion
        gameLogic.calculateLevelCompletion();
        // Fade the music away
        Core.getInstance().fadeMusic();
        // Play the dying sound
        dyingSound.play();
    }

    @Override
    public void dispose()
    {
        // Destroy the game logic
        gameLogic.destroy();
        // Dispose the game musics
        for(GameMusic music : gameMusics)
            music.dispose();

        // Dispose of the game sounds
        dyingSound.dispose();
    }

    public boolean isPausePressed()
    {
        pauseButton.process();
        return pauseButton.isClicking() || pauseButton.isClicked();
    }

    public void onLostGame()
    {
        // Set the dead flag
        deadPlayer = true;
        // Set death screen
        Core.getInstance().getScreensManager().setDeathScreen();
    }

    public void onLevelCompleted()
    {
        // Set death screen
        Core.getInstance().getScreensManager().setDeathScreen();
        // Fade the music away
        Core.getInstance().fadeMusic();
    }

    public void eatObject(Vector2 position)
    {
        // Add text to the eatable position
        eatableTexts.put(new Vector2(position), TimeUtils.millis());
    }

    public void pause()
    {
        // We only pause if the game has started and we're not already paused
        if(paused || !gameStarted || !Player.getInstance().isStarted() || Player.getInstance().isDying())
            return;

        // Hide UI elements
        scoreText.toggle(false);
        eatableText.toggle(false);
        startingMessageText.toggle(false);
        pauseButton.toggle(false);
        resumeButton.toggle(false);
        pauseText.toggle(false);
        levelText.toggle(false);
        // Pause the game music
        currentMusic.pause();
        // Set the paused flag
        paused = true;
        // Reset resuming flag
        resuming = false;
        // Show the resume button
        resumeButton.toggle(true);
        // Show the pause text
        pauseText.toggle(true);
        // Toggle world lights off
        Lightening.getInstance().toggleLights(false);
        // Fade in the screen
        Core.getInstance().getGraphicsManager().getScreenEffects().fadeIn(Color.BLACK, PAUSE_FADE);
    }

    public void resume()
    {
        // We only resume if the game has started and we're already paused
        if(!paused || !gameStarted)
            return;

        // Activate UI elements
        scoreText.toggle(false);
        eatableText.toggle(true);
        startingMessageText.toggle(true);
        pauseButton.toggle(false);
        resumeButton.toggle(false);
        pauseText.toggle(true);
        // Set the seconds
        seconds = 3;
        // Set the seconds tick
        secondTick = TimeUtils.millis();
        // Set the resuming flag
        resuming = true;
        // Toggle world lights on
        Lightening.getInstance().toggleLights(true);
        // Fade out the screen
        Core.getInstance().getGraphicsManager().getScreenEffects().fadeOut();
    }

    public void drawLevelText(String text, float x, float y)
    {
        Vector2 textSize = levelText.getSize(text);
        levelText.drawText(text, x - textSize.x / 2f, y + textSize.y / 2f);
    }

    public void disposeCurrentMusic()
    {
        currentMusic.dispose();
    }

    public static GameScreen getInstance()
    {
        return instance;
    }
}
