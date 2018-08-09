package com.ormisiclapps.slickyfuton.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.enumerations.ScreenTouchType;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.nodes.level.LevelNode;
import com.ormisiclapps.slickyfuton.graphics.others.LevelContainer;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIProgressBar;
import com.ormisiclapps.slickyfuton.graphics.ui.UIText;

/**
 * Created by OrMisicL on 9/7/2017.
 */

public class LevelPickerScreen implements Screen
{
    private Texture background;
    private LevelContainer[] levelContainers;
    private float currentScreenPosition;
    private float maximumScreenPosition;
    private float scrollPadding;
    private float scrollTo;
    private float scrollStep;
    private float verticalStripPosition, verticalStripSize;
    private UIText musicText, musicAuthorText;
    private float lastScrollMovement;
    private UIButton nextButton, previousButton, returnButton;
    private UIProgressBar loadingProgressBar;
    private boolean loading;
    private Vector2 loadingWindowPosition, loadingWindowSize;

    public LevelPickerScreen()
    {
        // Reset instances
        background = null;
        levelContainers = null;
        nextButton = null;
        previousButton = null;
        loadingProgressBar = null;
        // Reset values
        currentScreenPosition = 0f;
        scrollPadding = 0f;
        scrollTo = 0f;
        lastScrollMovement = 0f;
        // Reset flags
        loading = false;
    }

    @Override
    public void initialize()
    {
        // Get the shop background
        background = Core.getInstance().getResourcesManager().getResource("UI/ShopBackground", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Create the level containers array
        levelContainers = new LevelContainer[Core.getInstance().getLevelManager().getLevelsCount() + 1];
        // Create the endless mode container
        levelContainers[0] = new LevelContainer(null, 1);
        // Setup level containers
        for(int i = 1; i < levelContainers.length; i++)
            levelContainers[i] = new LevelContainer(Core.getInstance().getLevelManager().getLevelNode(i - 1), i + 1);

        // Set te vertical strip size and position
        verticalStripSize = Core.getInstance().getGraphicsManager().HEIGHT / 6f;
        verticalStripPosition = Core.getInstance().getGraphicsManager().HEIGHT - verticalStripSize;
        // Create next and previous buttons
        TextureRegion buttonTexture = Core.getInstance().getResourcesManager().getResource("UI/NextButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION);

        TextureRegion homeButton = Core.getInstance().getResourcesManager().getResource("UI/HomeButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION);

        nextButton = new UIButton(buttonTexture);
        previousButton = new UIButton(buttonTexture);
        returnButton = new UIButton(homeButton);
        // Setup buttons
        float buttonSize = Core.getInstance().getGraphicsManager().HEIGHT / 17f;
        nextButton.setSize(new Vector2(buttonSize, buttonSize));
        nextButton.setPosition(new Vector2(Core.getInstance().getGraphicsManager().WIDTH - buttonSize * 2f,
                Core.getInstance().getGraphicsManager().HEIGHT / 2f - buttonSize / 2f));

        previousButton.setSize(new Vector2(buttonSize, buttonSize));
        previousButton.setPosition(new Vector2(buttonSize,
                Core.getInstance().getGraphicsManager().HEIGHT / 2f - buttonSize / 2f));

        previousButton.setRotation(90f);

        buttonSize = Core.getInstance().getGraphicsManager().HEIGHT / 12f;

        returnButton.setSize(new Vector2(buttonSize, buttonSize));
        returnButton.setPosition(new Vector2(buttonSize / 2f,
                verticalStripPosition + verticalStripSize / 2f - buttonSize / 2f));

        nextButton.setFadeEffected(false);
        previousButton.setFadeEffected(false);
        returnButton.setFadeEffected(false);
        // Create the music text
        musicText = new UIText((int)verticalStripSize / 3, Color.WHITE, 0f, Color.WHITE);
        musicAuthorText = new UIText((int)verticalStripSize / 6, Color.WHITE, 0f, Color.WHITE);
        // Setup music text
        musicText.setFadeEffected(false);
        musicAuthorText.setFadeEffected(false);
        // Create loading window vectors
        float w = Core.getInstance().getGraphicsManager().WIDTH;
        float h = Core.getInstance().getGraphicsManager().HEIGHT;
        float sizeX = w / 3f * 2f;
        float sizeY = h / 4f;
        loadingWindowSize = new Vector2(sizeX, sizeY);
        loadingWindowPosition = new Vector2(w / 2f - sizeX / 2f, h / 2f - sizeY / 2f);
        // Create loading progress bar
        Vector2 progressBarSize = new Vector2(sizeX * 0.75f, sizeY / 5f);
        loadingProgressBar = new UIProgressBar(new Vector2(w / 2f - progressBarSize.x / 2f, h / 2f - progressBarSize.y / 2f),
                progressBarSize, Color.WHITE, Color.BLACK, new Color(0f, 0.8f, 0f, 1f));

        // Add it to the main UI
        UIMain.getInstance().addWidget(musicText);
        UIMain.getInstance().addWidget(musicAuthorText);
        UIMain.getInstance().addWidget(nextButton);
        UIMain.getInstance().addWidget(previousButton);
        UIMain.getInstance().addWidget(returnButton);
        // Set the current screen position
        currentScreenPosition = 0f;
        // Calculate the maximum screen position
        maximumScreenPosition = Core.getInstance().getGraphicsManager().WIDTH * (levelContainers.length - 1);
        // calculate the scroll padding
        scrollPadding = Core.getInstance().getGraphicsManager().WIDTH * 0.15f;
        // Calculate the scroll step
        scrollStep = Core.getInstance().getGraphicsManager().WIDTH * 0.05f;
        // Reset values
        scrollTo = -1f;

        // Play the music if its not already playing
        if(Core.getInstance().getCurrentMusic() != Core.getInstance().getScreensManager().getMainMenuScreen().getMusic())
        {
            Core.getInstance().getScreensManager().getMainMenuScreen().getMusic().play(true, 1.0f);
            Core.getInstance().setCurrentMusic(Core.getInstance().getScreensManager().getMainMenuScreen().getMusic());
        }
    }

    @Override
    public void activate()
    {
        // Activate widgets
        musicText.toggle(true);
        musicAuthorText.toggle(true);
        nextButton.toggle(true);
        previousButton.toggle(true);
        returnButton.toggle(true);
        loadingProgressBar.toggle(false);
        // Reset loading
        loading = false;
        // Activate level containers
        for(int i = 0; i < levelContainers.length; i++)
            levelContainers[i].activate();
    }

    @Override
    public void deactivate()
    {
        // Deactivate widgets
        musicText.toggle(false);
        musicAuthorText.toggle(true);
        nextButton.toggle(false);
        previousButton.toggle(false);
        returnButton.toggle(false);
        loadingProgressBar.toggle(false);
        // Deactivate level containers
        for(int i = 0; i < levelContainers.length; i++)
            levelContainers[i].deactivate();
    }

    @Override
    public void update()
    {
        // Do we have a button click ?
        if(nextButton.isClicked() && currentScreenPosition < Core.getInstance().getGraphicsManager().WIDTH * (levelContainers.length - 1))
            scrollTo = currentScreenPosition + Core.getInstance().getGraphicsManager().WIDTH;
        else if(previousButton.isClicked() && currentScreenPosition > 0f)
            scrollTo = currentScreenPosition - Core.getInstance().getGraphicsManager().WIDTH;

        // Do we have a previous button ?
        if(returnButton.isClicked() && !Core.getInstance().getScreensManager().isTransitioning())
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getMainMenuScreen());

        // If we're dragging then update the current screen position
        if(Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_DRAGGED)
        {
            // Scroll to the right if possible
            if(Core.getInstance().getInputHandler().getTouchMovementPosition().x < 0f && currentScreenPosition < maximumScreenPosition + scrollPadding)
            {
                // Scroll to the right
                currentScreenPosition += -Core.getInstance().getInputHandler().getTouchMovementPosition().x * 1.75f;
                // Don't allow it to exceed the limit
                if(currentScreenPosition > maximumScreenPosition + scrollPadding)
                    currentScreenPosition = maximumScreenPosition + scrollPadding;
            }
            // Scroll to the right if possible
            else if(Core.getInstance().getInputHandler().getTouchMovementPosition().x > 0f && currentScreenPosition > -scrollPadding)
            {
                // Scroll to the right
                currentScreenPosition += -Core.getInstance().getInputHandler().getTouchMovementPosition().x * 1.75f;
                // Don't allow it to exceed the limit
                if(currentScreenPosition < -scrollPadding)
                    currentScreenPosition = -scrollPadding;
            }
            // Save the last scroll movement
            lastScrollMovement = Core.getInstance().getInputHandler().getTouchMovementPosition().x;
        }
        else
        {
            // Scroll to the next one if necessary
            if(Math.abs(lastScrollMovement) > Core.getInstance().getGraphicsManager().WIDTH / 50f)
            {
                if(lastScrollMovement > 0f && currentScreenPosition > 0f)
                    scrollTo = currentScreenPosition - Core.getInstance().getGraphicsManager().WIDTH;
                else if(lastScrollMovement < 0f && currentScreenPosition < Core.getInstance().getGraphicsManager().WIDTH * (levelContainers.length - 1))
                    scrollTo = currentScreenPosition + Core.getInstance().getGraphicsManager().WIDTH;
            }
            else if(currentScreenPosition % Core.getInstance().getGraphicsManager().WIDTH != 0 && scrollTo == -1)
                scrollTo = Core.getInstance().getGraphicsManager().WIDTH * (int)(currentScreenPosition / (Core.getInstance().getGraphicsManager().WIDTH * 0.5f));

            // Fix position
            if(currentScreenPosition > maximumScreenPosition)
                scrollTo = maximumScreenPosition;
            else if(currentScreenPosition < 0f)
                scrollTo = 0f;

            // Reset the last scroll movement
            lastScrollMovement = 0f;
        }
        // Process automatic scrolling
        if(scrollTo != -1f)
        {
            // Get the difference between the current position and target
            float step = scrollTo - currentScreenPosition > 0f ? scrollStep : -scrollStep;
            // Update our current position
            currentScreenPosition += step;
            // Did we finish ?
            if(currentScreenPosition == scrollTo || (currentScreenPosition > scrollTo && step > 0f) || (currentScreenPosition < scrollTo && step < 0f))
            {
                // Fix position
                currentScreenPosition = scrollTo;
                // Reset scrolling
                scrollTo = -1f;
            }
        }
        // Get the current level id
        int currentLevelId = (int)currentScreenPosition / (Core.getInstance().getGraphicsManager().WIDTH / 2);
        if(currentLevelId >= levelContainers.length)
            currentLevelId = levelContainers.length - 1;
        else if(currentLevelId < 0)
            currentLevelId = 0;

        // Get the current level's node
        LevelNode node = levelContainers[currentLevelId].getNode();
        String music = "Playlist";
        String musicAuthor = "OrMisicL Apps";
        if(node != null)
        {
            music = node.musicName;
            musicAuthor = node.musicAuthor;
        }
        // Draw the music text
        Vector2 textSize = musicText.getSize(music);
        musicText.drawText(music, Core.getInstance().getGraphicsManager().WIDTH / 2f - textSize.x / 2f,
                verticalStripPosition + verticalStripSize / 2f + textSize.y / 2f);

        Vector2 authorTextSize = musicAuthorText.getSize("By " + musicAuthor);
        musicAuthorText.drawText("By " + musicAuthor, Core.getInstance().getGraphicsManager().WIDTH / 2f - authorTextSize.x / 2f,
                verticalStripPosition + verticalStripSize / 2f + textSize.y / 2f - authorTextSize.y * 3f);

        // Update level containers
        for(int i = 0; i < levelContainers.length; i++)
        {
            // Update and check if we clicked the play button
            if(levelContainers[i].update(currentScreenPosition))
            {
                // Set the game logic's level
                GameLogic.getInstance().setEndless(i == 0);
                GameLogic.getInstance().setCurrentLevelNode(levelContainers[i].getNode());
                // Load the level's music
                if(i != 0)
                {
                    Core.getInstance().getResourcesManager().requestResource(levelContainers[i].getNode().musicFile,
                            ResourceType.RESOURCE_TYPE_MUSIC);

                    // Enable progress bar
                    loadingProgressBar.toggle(true);
                    // Set the loading flag
                    loading = true;
                }
                // Transit to the game's screen
                if(!loading)
                {
                    Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getGameScreen());
                    // Fade out the current music
                    Core.getInstance().fadeMusic();
                }
                break;
            }
        }
        // Are we loading ?
        if(loading)
        {
            // Set the loading progress
            loadingProgressBar.setProgress(Core.getInstance().getResourcesManager().getLoadingProgress());
            // Update the progress bar
            loadingProgressBar.process();
            // Did we finish loading ?
            if(!Core.getInstance().getResourcesManager().isLoading())
            {
                // Transit to the game's screen
                Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getGameScreen());
                // Fade out the current music
                Core.getInstance().fadeMusic();
            }
        }
    }

    @Override
    public void render()
    {
        // Set the color (TODO: Find where the FUCK we change the WHITE color and fuck things up)
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw background
        Core.getInstance().getGraphicsManager().drawTexture(background, Core.getInstance().getGraphicsManager().EMPTY_VECTOR,
                Core.getInstance().getGraphicsManager().SCREEN_VECTOR, 0f);

    }

    @Override
    public void postFadeRender()
    {
        // Set the stripe's color
        Core.getInstance().getGraphicsManager().setColor(0f, 0f, 0f, 0.4f);
        // Draw the stripe
        TextureRegion empty = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, verticalStripPosition,
                Core.getInstance().getGraphicsManager().WIDTH, verticalStripSize, 0f);

        // Restore the default color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw the strip's line
        float lineSize = verticalStripSize / 120f;
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, verticalStripPosition - lineSize,
                Core.getInstance().getGraphicsManager().WIDTH, lineSize, 0f);

        // Render level containers
        for(int i = 0; i < levelContainers.length; i++)
            levelContainers[i].render();
    }

    public void postUIRender()
    {
        // Render loading
        if(loading)
        {
            // Set the background's color
            Core.getInstance().getGraphicsManager().setColor(0f, 0f, 0f, 0.95f);
            // Draw the background
            TextureRegion empty = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
            Core.getInstance().getGraphicsManager().drawTextureRegion(empty, loadingWindowPosition, loadingWindowSize, 0f);
            // Restore the default color
            Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
            // Draw the loading text
            Vector2 textSize = musicText.getSize("Loading level ...");
            musicText.drawText("Loading level ...", loadingWindowPosition.x + loadingWindowSize.x / 2f - textSize.x / 2f,
                    loadingWindowPosition.y + loadingWindowSize.y - textSize.y * 0.5f);

            // Draw the progress bar
            loadingProgressBar.render();
            // Render the text again
            musicText.render();
        }
    }

    @Override
    public void dispose() {

    }
}
