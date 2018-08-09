package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ormisiclapps.slickyfuton.audio.GameSound;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;

/**
 * Created by OrMisicL on 9/22/2015.
 */
public class UIMain
{
    private UIText mainText;
    private UIWindow alertWindow;
    private Array<UIWidget> widgets;
    private FreeTypeFontGenerator fontGenerator;
    private GameSound buttonClickSound;
    private boolean skipInput;
    private UIWindow previousWindow;

    private static UIMain instance;

    public UIMain()
    {
        // Reset instances
        mainText = null;
        widgets = null;
        buttonClickSound = null;
        fontGenerator = null;
        alertWindow = null;
        // Reset flags
        skipInput = false;
        // Set the instance
        instance = this;
    }

    public void initialize()
    {
        // Create the widgets array
        widgets = new Array<UIWidget>();
        // Get the font
        FileHandle font = Gdx.files.internal("Fonts/Main.ttf");
        // Create font type generators
        fontGenerator = new FreeTypeFontGenerator(font);
        // Create the main fonts
        mainText = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 30, Color.WHITE, 0f, Color.WHITE);
    }

    public void dispose()
    {
        // Destroy all the widgets
        for(UIWidget widget : widgets)
            widget.dispose();

        // Destroy the font generator
        fontGenerator.dispose();
    }

    public void preFadeRender()
    {
        // Render the main text
        //mainText.render();
        // Loop through all the widgets
        for(UIWidget widget : widgets)
        {
            // Render it
            if(widget.isVisible() && widget.isFadeEffected())
                widget.render();
        }
    }

    public void postFadeRender()
    {
        // Render the main text
        mainText.render();
        // Loop through all the widgets
        for(UIWidget widget : widgets)
        {
            // Render it
            if(widget.isVisible() && !widget.isFadeEffected())
                widget.render();
        }
        // Draw the alert if its visible
        if((alertWindow != null &&alertWindow.isVisible()))
            alertWindow.render();
    }

    public void process()
    {
        // Get the button click sound
        if(buttonClickSound == null && Core.getInstance().getResourcesManager().isResourceLoaded("ButtonClick", ResourceType.RESOURCE_TYPE_SOUND))
            buttonClickSound = new GameSound("ButtonClick");

        // Process the main font
        mainText.process();
        // Update the alert if its visible
        if((alertWindow != null && alertWindow.isClosed()) && previousWindow != null)
        {
            // Show the previous window
            Core.getInstance().getScreensManager().setCurrentWindow(previousWindow);
            // Reset previous window instance
            previousWindow = null;
        }
        // Update the alert if its visible
        if((alertWindow != null &&alertWindow.isVisible()))
        {
            alertWindow.process();
            previousWindow.process();
            // Set input skip flag
            skipInput = true;
        }

        // Skip the input if necessary
        if(!skipInput)
        {
            // Loop through all the widgets
            for(UIWidget widget : widgets)
            {
                // Update it
                if(widget.isVisible())
                    widget.process();
            }
        }
        else
        {
            // Loop through all the texts
            for(UIWidget widget : widgets)
            {
                // Update it
                if(widget.isVisible() && (widget instanceof UIText || widget instanceof UIWindow))
                    widget.process();
            }
        }
        // Reset flag
        skipInput = false;
    }

    public void skipFrameInput()
    {
        skipInput = true;
    }

    protected BitmapFont generateFont(FreeTypeFontGenerator.FreeTypeFontParameter parameter)
    {
        return fontGenerator.generateFont(parameter);
    }

    public void alert(String title, String text)
    {
        // Create the alert window if its not
        if(alertWindow == null)
        {
            // Create the alert window
            Vector2 size = new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 1.25f, Core.getInstance().getGraphicsManager().HEIGHT / 2.75f);
            Vector2 position = new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2f - size.x / 2f,
                    Core.getInstance().getGraphicsManager().HEIGHT / 2f - size.y / 2f);

            alertWindow = new UIWindow("Alert", position, size, new Color(0f, 0f, 0f, 0.99f));
            alertWindow.setFadeEffected(false);
        }
        // Clear texts
        alertWindow.cleanTexts();
        // Add texts
        Vector2 size = alertWindow.getSize();
        Vector2 textSize = alertWindow.getText().getSize(title);
        Vector2 smallTextSize = alertWindow.getSmallText().getSize(text);
        alertWindow.addText(title, new Vector2(size.x / 2f - textSize.x / 2f, size.y / 2f - textSize.y / 2f));
        size = alertWindow.getSize();
        alertWindow.addSmallText(text, new Vector2(size.x / 2f - smallTextSize.x / 2f, size.y / 2 - textSize.y * 1.75f - smallTextSize.y / 2f));
        // Toggle the alert window
        alertWindow.toggle(true);
        // Get the previous window
        previousWindow = Core.getInstance().getScreensManager().getCurrentWindow();
        // Set the current window
        Core.getInstance().getScreensManager().setCurrentWindow(alertWindow);
    }

    public boolean isAlertWindowVisible() { return alertWindow != null && alertWindow.isVisible(); }

    public void addWidget(UIWidget widget)
    {
        widgets.add(widget);
    }

    public void removeWidget(UIWidget widget)
    {
        widgets.removeValue(widget, false);
    }

    public UIText getMainText() { return mainText; }

    public GameSound getButtonClickSound() { return buttonClickSound; }

    public static UIMain getInstance()
    {
        return instance;
    }

    public boolean isSkipInput() {
        return skipInput;
    }
}
