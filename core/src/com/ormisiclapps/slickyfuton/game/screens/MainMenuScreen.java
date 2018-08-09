package com.ormisiclapps.slickyfuton.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.audio.GameMusic;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIText;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;

/**
 * Created by OrMisicL on 6/1/2016.
 */
public class MainMenuScreen implements Screen
{
    private UIButton playButton, rateButton, leaderboardsButton, achvButton, settingsButton, shopButton;// creditsButton;
    private TextureRegion gameLogoTexture, eatableTexture;
    private UIText versionText, defaultText;
    private Vector2 logoPosition, logoSize;
    private Vector2 eatablePosition, eatableSize;
    private Vector2 tmpVector;
    private boolean playButtonIncreaseSize;
    private float playButtonInitialSize;
    private float verticalStripPosition, verticalStripSize;
    private Color stripColor;
    private GameMusic music;
    private float colorUpdateTime;

    protected static final float PLAY_BUTTON_SIZE_INCREASE = 0.5f;
    private static final float FADE_ALPHA = 0.55f;

    public MainMenuScreen()
    {
        // Reset instances
        playButton = null;
        rateButton = null;
        leaderboardsButton = null;
        achvButton = null;
        settingsButton = null;
        //creditsButton = null;
        shopButton = null;
        gameLogoTexture = null;
        eatableTexture = null;
        versionText = null;
        defaultText = null;
        stripColor = null;
        music = null;
        // Reset flags
        playButtonIncreaseSize = false;
        // Reset values
        colorUpdateTime = 0f;
        // Create vectors
        logoPosition = new Vector2();
        logoSize = new Vector2();
        tmpVector = new Vector2();
        eatablePosition = new Vector2();
        eatableSize = new Vector2();
    }

    @Override
    public void initialize()
    {
        // Get height and width
        float w = Core.getInstance().getGraphicsManager().WIDTH;
        float h = Core.getInstance().getGraphicsManager().HEIGHT;
        // Create game logo texture
        gameLogoTexture = Core.getInstance().getResourcesManager().getResource("Main/GameLogo", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create the eatable texture
        eatableTexture = Core.getInstance().getResourcesManager().getResource("UI/EatableIcon", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create buttons
        playButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/PlayButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        rateButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/RateButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        leaderboardsButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/StatisticsButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        achvButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/AchievementsButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        settingsButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/SettingsButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        shopButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/ShopButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        /*creditsButton = new UIButton((Texture)Core.getInstance().getResourcesManager().getResource("UI/CreditsButton",
                ResourceType.RESOURCE_TYPE_TEXTURE));*/

        // Set their positions
        float size = h / 5f;
        playButton.setFadeEffected(false);
        playButton.setSize(new Vector2(size, size));
        playButton.setPosition(new Vector2(GameMath.getCenteredPosition(new Vector2(w / 2, h / 1.75f), new Vector2(size, size))));
        // Save the player button initial's size
        playButtonInitialSize = size;
        // Calculate the button's size
        size = h / 6f;
        // Setup buttons
        shopButton.setFadeEffected(false);
        shopButton.setSize(new Vector2(size, size));
        shopButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f - size * 2.25f, h / 8f + size), new Vector2(size, size))));

        leaderboardsButton.setFadeEffected(false);
        leaderboardsButton.setSize(new Vector2(size, size));
        leaderboardsButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f - size * 0.75f, h / 8f + size), new Vector2(size, size))));

        achvButton.setFadeEffected(false);
        achvButton.setSize(new Vector2(size, size));
        achvButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f + size * 0.75f, h / 8f + size), new Vector2(size, size))));

        rateButton.setFadeEffected(false);
        rateButton.setSize(new Vector2(size, size));
        rateButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f + size * 2.25f, h / 8f + size), new Vector2(size, size))));

        size = h / 16f;
        settingsButton.setFadeEffected(false);
        settingsButton.setSize(new Vector2(size, size));
        settingsButton.setPosition(new Vector2(w - w / 20f - size / 2f, h - h / 10f - size / 2f));

        /*creditsButton.setFadeEffected(false);
        creditsButton.setSize(new Vector2(size, size));
        creditsButton.setPosition(new Vector2(w - w / 20f - size / 2f, h - h / 10f - size * 2f));*/

        size = h / 20f;
        // Create texts
        defaultText = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 22, Color.WHITE, 1.5f, Color.BLACK);
        versionText = new UIText((int)size, Color.WHITE, 0f, Color.BLACK);
        versionText.setFadeEffected(false);
        // Hide elements
        playButton.toggle(false);
        rateButton.toggle(false);
        leaderboardsButton.toggle(false);
        achvButton.toggle(false);
        settingsButton.toggle(false);
        shopButton.toggle(false);
        versionText.toggle(false);
        defaultText.toggle(false);
        // Add them to the UI
        UIMain.getInstance().addWidget(playButton);
        UIMain.getInstance().addWidget(rateButton);
        UIMain.getInstance().addWidget(leaderboardsButton);
        UIMain.getInstance().addWidget(achvButton);
        UIMain.getInstance().addWidget(settingsButton);
        //UIMain.getInstance().addWidget(creditsButton);
        UIMain.getInstance().addWidget(shopButton);
        UIMain.getInstance().addWidget(versionText);
        UIMain.getInstance().addWidget(defaultText);
        // Set the game logo position
        logoSize.set(Core.getInstance().getGraphicsManager().HEIGHT / 6f, Core.getInstance().getGraphicsManager().WIDTH / 2.5f);
        logoPosition.set(Core.getInstance().getGraphicsManager().WIDTH / 2 - logoSize.x / 2,
                Core.getInstance().getGraphicsManager().HEIGHT / 6 * 5 - logoSize.y / 2);

        // Initialize the play button to increase its size
        playButtonIncreaseSize = true;
        // Set te vertical strip size and position
        verticalStripSize = Core.getInstance().getGraphicsManager().HEIGHT / 2.5f;
        verticalStripPosition = 0f;
        // Set the strip color
        stripColor = new Color(0f, 0f, 0f, 0.4f);
        // Get text size
        Vector2 textSize = versionText.getSize("1000");
        // Set the eatable size
        eatableSize = new Vector2(textSize.y, textSize.y);
        // Create the eatable position instance
        eatablePosition = new Vector2(w / 28f - textSize.y / 2f, h - h / 10f - textSize.y / 4f);
        // Create the music instance
        music = new GameMusic("MenuMusic");
    }

    @Override
    public void activate()
    {
        // Show the UI elements
        playButton.toggle(true);
        shopButton.toggle(true);
        leaderboardsButton.toggle(true);
        //creditsButton.toggle(true);
        achvButton.toggle(true);
        rateButton.toggle(true);
        settingsButton.toggle(true);
        versionText.toggle(true);
        //defaultText.toggle(true);*/
        // Start playing the music
        if(!music.isPlaying())
            music.play(true, 1f);

        // Set it as the current game's music
        Core.getInstance().setCurrentMusic(music);
        // Initialize terrain
        GameLogic.getInstance().getTerrain().initialize();
        // Initialize the player
        Player.getInstance().setup();
        Player.getInstance().setPosition(new Vector2(30f, Configuration.TERRAIN_ROOF_POSITION / 2f));
        // Set the camera to free
        Camera.getInstance().setFree();
        // Set the camera position
        Camera.getInstance().setPosition(new Vector2(30f, Configuration.TERRAIN_ROOF_POSITION / 2f));
        // Display the banner ad
        Core.getInstance().getOSUtility().showBannerAd();
}

    @Override
    public void deactivate()
    {
        // Hide the UI elements
        playButton.toggle(false);
        rateButton.toggle(false);
        leaderboardsButton.toggle(false);
        achvButton.toggle(false);
        settingsButton.toggle(false);
        //creditsButton.toggle(false);
        shopButton.toggle(false);
        versionText.toggle(false);
        defaultText.toggle(false);
    }

    @Override
    public void update()
    {
        // Update the color update time
        colorUpdateTime += Core.getInstance().getGraphicsManager().DELTA_TIME;
        if(colorUpdateTime >= 1.5f)
        {
            // Update the environment's color
            GameIntelligence.getInstance().update();
            // Reset time
            colorUpdateTime = 0f;
        }
        // Process game intelligence
        GameIntelligence.getInstance().process();
        // Update the game world
        GameWorld.getInstance().process();
        // Update player
        Player.getInstance().process();
        // Update camera
        tmpVector.set(Camera.getInstance().getPosition()).add(0.25f, 0f);
        Camera.getInstance().setPosition(tmpVector);
        Camera.getInstance().update();
        // Update terrain
        GameLogic.getInstance().getTerrain().process();
        // Restore the player's position
        float gap = Core.getInstance().getGraphicsManager().WIDTH * 1.1f;
        if(Camera.getInstance().worldToScreen(Player.getInstance().getPosition()).x >= gap)
        {
            Player.getInstance().setStartInitialPosition(MathUtils.random(Configuration.TERRAIN_ROOF_POSITION / 3f,
                    Configuration.TERRAIN_ROOF_POSITION / 4f * 3f));

            Player.getInstance().teleport(Camera.getInstance().screenToWorld(tmpVector.set(-gap, 0f)));
        }
        // Fade the screen
        if(Core.getInstance().getScreensManager().isTransitioning() && Core.getInstance().getScreensManager().getTransitionScreen() == this &&
                Core.getInstance().getGraphicsManager().getScreenEffects().getTransitionColor().a <= FADE_ALPHA)
        {
            // Halt the transition
            Core.getInstance().getGraphicsManager().getScreenEffects().haltTransition();
            // Fade the screen
            Core.getInstance().getGraphicsManager().getScreenEffects().fadeIn(Color.BLACK, FADE_ALPHA);
        }
        // If the button is clicked then advance
        if(!Core.getInstance().getScreensManager().isTransitioning() && playButton.isClicked())
        {
            // Transit the screen
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getLevelPickerScreen());
            // Fade the music
            //Core.getInstance().fadeMusic();
        }
        // If the shop button is clicked then advance
        if(!Core.getInstance().getScreensManager().isTransitioning() && shopButton.isClicked())
            // Transit the screen
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getShopScreen());
        // If the settings button is clicked then advance
        else if(!Core.getInstance().getScreensManager().isTransitioning() && settingsButton.isClicked())
            // Toggle the settings screen
            Core.getInstance().getScreensManager().toggleSettingsWindow(true);
        // If the leaderboards button is clicked then advance
        else if(!Core.getInstance().getScreensManager().isTransitioning() && leaderboardsButton.isClicked())
            // Show leaderboards
            Core.getInstance().getOSUtility().showLeaderboards();
        // If the achievements button is clicked then advance
        else if(!Core.getInstance().getScreensManager().isTransitioning() && achvButton.isClicked())
            // Show achievements
            Core.getInstance().getOSUtility().showAchievements();
        // If the rate button is clicked then advance
        else if(!Core.getInstance().getScreensManager().isTransitioning() && rateButton.isClicked())
            // Go to the rating link
            Core.getInstance().getOSUtility().rateGame();
        else
        {
            // Animate the play button
            tmpVector.set(playButton.getSize()).add(playButtonIncreaseSize ? PLAY_BUTTON_SIZE_INCREASE : -PLAY_BUTTON_SIZE_INCREASE,
                    playButtonIncreaseSize ? PLAY_BUTTON_SIZE_INCREASE : -PLAY_BUTTON_SIZE_INCREASE);

            // Check if we've reached the increasing limit
            if(playButtonIncreaseSize && tmpVector.x >= playButtonInitialSize * 1.15f)
            {
                // Invert the increasing flag
                playButtonIncreaseSize = false;
                // Fix its position
                tmpVector.set(playButtonInitialSize * 1.15f, playButtonInitialSize * 1.15f);
            }
            else if(!playButtonIncreaseSize && tmpVector.x <= playButtonInitialSize * 0.85f)
            {
                // Invert the increasing flag
                playButtonIncreaseSize = true;
                // Fix its position
                tmpVector.set(playButtonInitialSize * 0.85f, playButtonInitialSize * 0.85f);
            }
            // Set the button's size
            playButton.setSize(tmpVector);
            // Fix its position to be centered
            playButton.setPosition(new Vector2(GameMath.getCenteredPosition(new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2,
                    Core.getInstance().getGraphicsManager().HEIGHT / 1.75f), tmpVector)));

            // Get the text size
            long eatablesCount = Core.getInstance().getStatsSaver().savedData.eatablesCollected;
            // Draw the eatables count
            versionText.drawText(Long.toString(eatablesCount),
                    eatablePosition.x + eatableSize.x * 1.5f, eatablePosition.y + eatableSize.y);
        }
    }

    @Override
    public void render()
    {
        // Draw terrain
        GameLogic.getInstance().getTerrain().render();
        // Draw player
        Player.getInstance().render();
        // Draw the background
        drawBackground();
    }

    private void drawBackground()
    {

    }

    @Override
    public void postFadeRender()
    {
        // Set the stripe's color
        Core.getInstance().getGraphicsManager().setColor(stripColor);
        // Draw the stripe
        TextureRegion empty = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, verticalStripPosition,
                Core.getInstance().getGraphicsManager().WIDTH, verticalStripSize, 0f);

        // Restore the default color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw the strip's line
        float lineSize = verticalStripSize / 120f;
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, verticalStripSize - lineSize,
                Core.getInstance().getGraphicsManager().WIDTH, lineSize, 0f);

        // Draw the eatable icon
        Core.getInstance().getGraphicsManager().drawTextureRegion(eatableTexture, eatablePosition, eatableSize, 0f);
        // Draw the game logo
        Core.getInstance().getGraphicsManager().drawTextureRegion(gameLogoTexture, logoPosition, logoSize, 270f);
    }

    @Override
    public void dispose() {

    }

    public GameMusic getMusic() {
        return music;
    }
}
