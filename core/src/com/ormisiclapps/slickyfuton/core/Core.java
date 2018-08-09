package com.ormisiclapps.slickyfuton.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.ormisiclapps.slickyfuton.audio.GameMusic;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.screens.GameScreen;
import com.ormisiclapps.slickyfuton.game.screens.LevelPickerScreen;
import com.ormisiclapps.slickyfuton.game.screens.ShopScreen;
import com.ormisiclapps.slickyfuton.game.world.Lightening;
import com.ormisiclapps.slickyfuton.managers.LevelManager;
import com.ormisiclapps.slickyfuton.os.OSUtility;
import com.ormisiclapps.slickyfuton.input.InputHandler;
import com.ormisiclapps.slickyfuton.managers.FileManager;
import com.ormisiclapps.slickyfuton.managers.GraphicsManager;
import com.ormisiclapps.slickyfuton.managers.ModelManager;
import com.ormisiclapps.slickyfuton.managers.ResourcesManager;
import com.ormisiclapps.slickyfuton.managers.ScreensManager;
import com.ormisiclapps.slickyfuton.utility.ModelSettings;
import com.ormisiclapps.slickyfuton.utility.PreSetCombinations;
import com.ormisiclapps.slickyfuton.utility.StatsSaver;

/**
 * Created by OrMisicL on 5/29/2016.
 */
public class Core
{
    private ResourcesManager resourcesManager;
    private GraphicsManager graphicsManager;
    private ModelManager modelManager;
    private ScreensManager screensManager;
    private FileManager fileManager;
    private LevelManager levelManager;
    private InputHandler inputHandler;
    private ModelSettings modelSettings;
    private PreSetCombinations preSetCombinations;
    private StatsSaver statsSaver;
    private OSUtility osUtility;

    public Texture emptyTexture;
    private GameMusic currentMusic;

    private static Core instance;

   /* public boolean isDebug = false;
    public boolean isDebugging = false;
    public boolean drawTexture = true;
    public boolean drawDebugObjects = true;
    public boolean drawDebugText = true;*/

    public int FPS = 0;

    public final String VERSION = "2.2.1";

    private boolean fadingMusic;

    private static final float FADING_FRAME = 0.025f;

    public Core(OSUtility osUtility)
    {
        // Set the instance
        instance = this;
        // Set the os utility instance
        this.osUtility = osUtility;
        // Reset instances
        emptyTexture = null;
        currentMusic = null;
    }

    public void initialize()
    {
        // Create the file manager instance
        fileManager = new FileManager();
        // Create the stats saved
        statsSaver = new StatsSaver();
        // Load save
        statsSaver.load();
        // Create the resources manager instance
        resourcesManager = new ResourcesManager();
        // Load initial resources
        resourcesManager.loadInitialResources();
        // Create the model manager instance
        modelManager = new ModelManager();
        // Create the graphics manager instance
        graphicsManager = new GraphicsManager();
        // Initialize graphics manager
        graphicsManager.initialize();
        // Create the level manager
        levelManager = new LevelManager();
        // Create the model settings instance
        modelSettings = new ModelSettings();
        // Create the pre set combination instance
        preSetCombinations = new PreSetCombinations();
        // Create the screens manager
        screensManager = new ScreensManager();
        // Create the input handler
        inputHandler = new InputHandler();
        // Sign in
        //googlePlayServices.signIn();
        // Initialize debug
        /*if(isDebug)
        {
            UIDebug.initialize();
        }*/
        // Reset flags
        fadingMusic = false;
    }

    public void update()
    {
        // Update FPS
        FPS = Gdx.graphics.getFramesPerSecond();
        //MathUtils.random.setSeed(TimeUtils.millis());
        // Check for debug mode
        /*if(isDebug)
        {
            // Check for debugging mode
            if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0))
                isDebugging = !isDebugging;

            // Check for texture key
            if(Gdx.input.isKeyJustPressed(Input.Keys.PLUS))
                drawTexture = !drawTexture;

            // Check for debug shapes key
            if(Gdx.input.isKeyJustPressed(Input.Keys.MINUS))
                drawDebugObjects = !drawDebugObjects;

            // Check for debug text key
            if(Gdx.input.isKeyJustPressed(Input.Keys.PERIOD))
                drawDebugText = !drawDebugText;
        }*/
        // Update the resources manager
        resourcesManager.update();
        // Update the graphics manager
        graphicsManager.update();
        // Update the screens manager
        screensManager.update();
        // Update the UI
        graphicsManager.updateUI();
        // Update lightening
        if(screensManager.getCurrentScreen() instanceof GameScreen || screensManager.getCurrentScreen() instanceof ShopScreen)
            Lightening.getInstance().update();

        // Take debug input snapshot
        /*if(isDebug)
            UIDebug.takeInputSnapShot();*/

        // Clear input
        inputHandler.clear();
        // Fade the music
        if(fadingMusic)
        {
            // Lower the volume
            if(currentMusic != null && currentMusic.getVolume() > 0)
                currentMusic.setVolume(currentMusic.getVolume() - FADING_FRAME);
            else
            {
                // Stop it
                //if(currentMusic != null)
                  //  currentMusic.stop();

                // Reset current music instance
                currentMusic = null;
                // Reset fading flag
                fadingMusic = false;
            }
        }
    }

    public void render()
    {
        // If the screen is flushing then skip rendering this frame
        if(graphicsManager.getScreenEffects().isFlushing())
        {
            // Render the white screen
            graphicsManager.renderFlush();
            // Reset the flushing effect
            graphicsManager.getScreenEffects().resetFlush();
            return;
        }
        // Get the texture
        if(emptyTexture == null && resourcesManager.isResourceLoaded("Empty", ResourceType.RESOURCE_TYPE_TEXTURE))
            // Get the empty texture
            emptyTexture = resourcesManager.getResource("Empty", ResourceType.RESOURCE_TYPE_TEXTURE);

        // Prepare rendering
        graphicsManager.prepareRendering();
        // Begin rendering
        graphicsManager.beginRendering();
        // Render the active screen
        screensManager.render();
        // Render debug UI
        /*if(screensManager.getCurrentScreen() instanceof GameScreen && isDebug && drawDebugText)
            graphicsManager.renderDebugUI();*/

        // PreFade UI rendering
        graphicsManager.preFadeUIRender();
        // Render the faded screen
        if(graphicsManager.getScreenEffects().isFaded())
        {
            // Ensure the empty texture
            if(emptyTexture != null)
            {
                // Set the color
                graphicsManager.setColor(graphicsManager.getScreenEffects().getFadeColor());
                // Render a full screen rectangle
                graphicsManager.drawTexture(emptyTexture, graphicsManager.EMPTY_VECTOR, graphicsManager.SCREEN_VECTOR, 0f);
                // Restore the color
                graphicsManager.setColor(Color.WHITE);
            }
        }
        // PostFade rendering cycle
        screensManager.postFadeRender();
        // PostFade UI rendering
        graphicsManager.postFadeUIRender();
        // Special case for level picker rendering
        if(screensManager.getCurrentScreen() instanceof LevelPickerScreen)
            screensManager.getLevelPickerScreen().postUIRender();

        // Finish rendering
        graphicsManager.finishRendering();
        // Render lightening
        if(screensManager.getCurrentScreen() instanceof GameScreen || screensManager.getCurrentScreen() instanceof ShopScreen)
            Lightening.getInstance().render();
    }

    public void pause()
    {
        // Save data
        if(statsSaver.savedData != null)
            statsSaver.save();

        // Pause the game
        if(screensManager.getCurrentScreen() instanceof GameScreen)
            screensManager.getGameScreen().pause();
    }

    public void resume()
    {
        // Nothing to do here
    }

    public void terminate()
    {
        // Dispose of the debug stuff
        /*if(isDebug)
            UIDebug.dispose();*/

        // Save data
        if(statsSaver.savedData != null)
            statsSaver.save();

        // Dispose of the screen manager
        screensManager.dispose();
        // Dispose all of the loaded resources
        resourcesManager.dispose();
        // Dispose of the graphics manager
        graphicsManager.dispose();
    }

    public void fadeMusic()
    {
        // Ensure we have a current on going music
        if(currentMusic == null)
            return;

        // Set the fading music flag
        fadingMusic = true;
    }

    public ResourcesManager getResourcesManager() { return resourcesManager; }
    public GraphicsManager getGraphicsManager() { return graphicsManager; }
    public ScreensManager getScreensManager() { return screensManager; }
    public InputHandler getInputHandler() { return inputHandler; }
    public FileManager getFileManager() { return fileManager; }
    public LevelManager getLevelManager() { return levelManager; }
    public ModelManager getModelManager() { return modelManager; }
    public ModelSettings getModelSettings() { return modelSettings; }
    public PreSetCombinations getPreSetCombinations() { return preSetCombinations; }
    public StatsSaver getStatsSaver() { return statsSaver; }

    public GameMusic getCurrentMusic() {
        return currentMusic;
    }

    public void setCurrentMusic(GameMusic music)
    {
        // Validate the current music
        if(currentMusic == music)
            return;

        // Stop any music if we have one
        if(currentMusic != null)
        {
            // Stop it
            currentMusic.stop();
            // Reset flag
            fadingMusic = false;
        }
        // Set the current music
        currentMusic = music;
        // Disable the music if sound is muted
        if(!statsSaver.savedData.musicState)
            currentMusic.setVolume(0f);
    }

    public OSUtility getOSUtility() { return osUtility; }

    public boolean isFadingMusic() {
        return fadingMusic;
    }

    public static Core getInstance()
    {
        return instance;
    }
}
