package com.ormisiclapps.slickyfuton.managers;

import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.screens.*;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIWindow;
import com.ormisiclapps.slickyfuton.graphics.windows.SettingsWindow;
import com.ormisiclapps.slickyfuton.utility.Configuration;


/**
 * Created by OrMisicL on 5/29/2016.
 * Will handle screen initializing, activating, deactivating
 */
public class ScreensManager
{
    private Screen currentScreen;
    private LoadingScreen loadingScreen;
    private MainMenuScreen mainMenuScreen;
    private LevelPickerScreen levelPickerScreen;
    private GameScreen gameScreen;
    private DeathScreen deathScreen;
    private ShopScreen shopScreen;
    //private CreditsScreen creditsScreen;
    private Screen transitingToScreen;
    private boolean transitioning;
    private UIWindow currentWindow;
    private SettingsWindow settingsWindow;

    public ScreensManager()
    {
        // Create screen instances
        loadingScreen = new LoadingScreen();
        mainMenuScreen = new MainMenuScreen();
        levelPickerScreen = new LevelPickerScreen();
        gameScreen = new GameScreen();
        deathScreen = new DeathScreen();
        shopScreen = new ShopScreen();
        //creditsScreen = new CreditsScreen();
        // Reset instances
        currentWindow = null;
        settingsWindow = null;
        // Initialize the loading screen
        loadingScreen.initialize();
        // Activate the loading screen
        loadingScreen.activate();
        // Reset transition screen
        transitioning = false;
        transitingToScreen = null;
        // Set the current screen
        currentScreen = loadingScreen;
    }

    public void initializeAllScreens()
    {
        // Initialize all screens
        mainMenuScreen.initialize();
        levelPickerScreen.initialize();
        gameScreen.initialize();
        deathScreen.initialize();
        shopScreen.initialize();
        //creditsScreen.initialize();
    }

    public void transitToScreen(Screen targetScreen, long duration)
    {
        // Ensure we're not already transitioning
        if(transitioning)
            return;

        // Set the transitioning screen
        transitioning = true;
        // Set the target screen
        transitingToScreen = targetScreen;
        // Start the transiting effect
        Core.getInstance().getGraphicsManager().getScreenEffects().transit(duration);
        currentScreen.update();
    }

    public void transitToScreen(Screen targetScreen)
    {
        transitToScreen(targetScreen, Configuration.DEFAULT_TRANSITION_DURATION);
    }

    public void update()
    {
        // Are we transitioning ?
        if(transitioning)
        {
            // Are we fading out ?
            if(Core.getInstance().getGraphicsManager().getScreenEffects().isTransitionFadeOut() && currentScreen != transitingToScreen)
            {
                // Deactivate the current screen
                currentScreen.deactivate();
                // Activate the new screen
                transitingToScreen.activate();
                // Set the current screen
                currentScreen = transitingToScreen;
            }
            // Did we finish transiting ?
            if(!Core.getInstance().getGraphicsManager().getScreenEffects().isTransitioning())
                transitioning = false;
        }
        // Do we have an active window ?
        if(currentWindow != null)
        {
            // Update the window
            //currentWindow.process();
            // Skip handling this frame's input
            UIMain.getInstance().skipFrameInput();
            // Toggle off if we're closed
            if(currentWindow.isClosed())
                hideCurrentWindow();
        }
        // Update the current screen
        currentScreen.update();
    }

    public void postFadeRender()
    {
        // PostFade rendering cycle for the current screen
        currentScreen.postFadeRender();
    }

    public void render()
    {
        // Render the current screen
        currentScreen.render();
    }

    public void dispose()
    {
        // Destroy the current screen
        currentScreen.dispose();
    }

    public void toggleSettingsWindow(boolean toggle)
    {
        // Check if the settings window is not created
        if(settingsWindow == null)
        {
            // Create the settings window instance
            settingsWindow = new SettingsWindow();
            // Add it to the main UI
            UIMain.getInstance().addWidget(settingsWindow);
        }
        // Toggle the settings window
        settingsWindow.toggle(toggle);
        // Set the current window
        currentWindow = toggle ? settingsWindow : null;
    }

    public void hideCurrentWindow()
    {
        // Check if the settings window is not created
        if(currentWindow != null)
        {
            // Toggle the settings window
            currentWindow.toggle(false);
            // Reset the current window
            currentWindow = null;
        }
    }

    public void setCurrentWindow(UIWindow window)
    {
        currentWindow = window;
    }

    public UIWindow getCurrentWindow() {
        return currentWindow;
    }

    public void setDeathScreen()
    {
        // Deactivate the current screen
        currentScreen.deactivate();
        // Activate the death screen
        deathScreen.activate();
        // Set the current used screen to death
        currentScreen = deathScreen;
        // Update it once
        currentScreen.update();
    }

    public Screen getCurrentScreen() { return currentScreen; }
    public GameScreen getGameScreen() { return gameScreen; }
    public MainMenuScreen getMainMenuScreen() { return mainMenuScreen; }
    public LevelPickerScreen getLevelPickerScreen() { return levelPickerScreen; }
    public ShopScreen getShopScreen() { return shopScreen; }
    //public CreditsScreen getCreditsScreen() { return creditsScreen; }
    public Screen getTransitionScreen() { return transitingToScreen; }

    public boolean isTransitioning() { return transitioning; }
}
