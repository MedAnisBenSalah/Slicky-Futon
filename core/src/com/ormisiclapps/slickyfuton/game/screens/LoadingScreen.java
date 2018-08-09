package com.ormisiclapps.slickyfuton.game.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.utility.GameMath;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.graphics.ui.UIProgressBar;

/**
 * Created by OrMisicL on 1/1/2016.
 * Used to render the first loading screen of the game.
 */
public class LoadingScreen implements Screen
{
    private Texture background;
    private UIProgressBar uiProgressBar;

    public LoadingScreen()
    {
        // Reset instances
        background = null;
        uiProgressBar = null;
    }

    @Override
    public void initialize()
    {
        // Compute the loading bar position and size
        Vector2 loadingBarSize = new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2f,
                Core.getInstance().getGraphicsManager().HEIGHT / 40f);
        
        Vector2 loadingBarPosition = new Vector2(GameMath.getCenteredPosition(
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2, Core.getInstance().getGraphicsManager().HEIGHT / 6),
                loadingBarSize));

        // Create the progress bar instance
        uiProgressBar = new UIProgressBar(loadingBarPosition, loadingBarSize,
                Configuration.PROGRESSBAR_CONTAINER_COLOR, Configuration.PROGRESSBAR_BACKGROUND_COLOR,
                Configuration.PROGRESSBAR_COLOR);

        // Set it to be visible
        uiProgressBar.toggle(true);
        // Add to the UI main
        Core.getInstance().getGraphicsManager().getUIMain().addWidget(uiProgressBar);
        // Get the background texture
        background = Core.getInstance().getResourcesManager().getResource("UI/LoadingBackground", ResourceType.RESOURCE_TYPE_TEXTURE);
    }

    @Override
    public void activate()
    {
        // Load all resources
        Core.getInstance().getResourcesManager().loadResources();
    }

    @Override
    public void deactivate()
    {
        // Hide the progress bar
        uiProgressBar.toggle(false);
    }

    @Override
    public void update()
    {
        // If the resources has finished loading then transit to the main menu
        if(!Core.getInstance().getResourcesManager().isLoading())
        {
            // If its not already transitioning
            if(!Core.getInstance().getScreensManager().isTransitioning())
            {
                // Load all levels
                Core.getInstance().getLevelManager().loadAllLevels();
                // Initialize all screens
                Core.getInstance().getScreensManager().initializeAllScreens();
                // Sign in to Google Play Services
                Core.getInstance().getOSUtility().signIn();
                // Transit to the main menu
                Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getMainMenuScreen());
            }
        }
        // Get loading progress
        int progress = Core.getInstance().getResourcesManager().getLoadingProgress();
        // Set the progress bar's progress
        uiProgressBar.setProgress(progress);
        // Draw the loading text
        Vector2 textSize = UIMain.getInstance().getMainText().getSize("Loading (" + progress + " %) ...");
        UIMain.getInstance().getMainText().drawText("Loading (" + progress + " %) ... ",
                uiProgressBar.getPosition().x + uiProgressBar.getSize().x / 2 - textSize.x / 2,
                uiProgressBar.getPosition().y - textSize.y / 1.5f);
    }

    @Override
    public void render()
    {
        // Draw the background
        Core.getInstance().getGraphicsManager().drawTexture(background, Core.getInstance().getGraphicsManager().EMPTY_VECTOR,
                Core.getInstance().getGraphicsManager().SCREEN_VECTOR, 0f);
    }

    @Override
    public void postFadeRender() {

    }

    @Override
    public void dispose()
    {
        // Dispose of the loading progress bar
        uiProgressBar.dispose();
    }
}
