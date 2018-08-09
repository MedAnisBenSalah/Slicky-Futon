package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ScreenTouchType;

/**
 * Created by OrMisicL on 1/5/2016.
 */
public abstract class UIWidget
{
    private boolean visible;
    private boolean enabled;
    private Rectangle rectangle, clickRectangle;
    protected boolean clicked;
    protected boolean clicking;
    private boolean clickable;
    private boolean fadeEffect;
    private Vector2 tmpVector = new Vector2();
    private Vector2 tmpVector2 = new Vector2();

    public UIWidget(boolean clickable)
    {
        // Reset values
        visible = false;
        clicked = false;
        clicking = false;
        fadeEffect = true;
        enabled = true;
        // Set flags
        this.clickable = clickable;
        // Create the rectangle instance
        rectangle = new Rectangle();
        clickRectangle = new Rectangle();
    }

    public UIWidget(boolean clickable, Vector2 position, Vector2 size)
    {
        this(clickable);
        // Set coordinates
        if(this instanceof UIWindow)
            rectangle.setPosition(position);
        else
            setPosition(position);

        setSize(size);
    }

    public abstract void dispose();
    public abstract void render();

    public void process()
    {
        // Ensure we're clickable
        if(!clickable || !enabled)
            return;

        // Reset the clicked flags
        clicked = false;
        clicking = false;
        // Get the clicked position
        Vector2 position = Core.getInstance().getInputHandler().getScreenTouchPosition();
        // Check if its clicked
        if(Core.getInstance().getInputHandler().isScreenTouched() &&
                !Core.getInstance().getGraphicsManager().getScreenEffects().isTransitioning() && rectangle.contains(position.x, position.y))
        {
            // Handle click type
            if(Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_DOWN ||
                    Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_DRAGGED)
            {
                // Stop the down press reporting
                Core.getInstance().getInputHandler().unreportTouch();
                // Set the clicking flag
                clicking = true;
            }
            else if(Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_UP)
            {
                // Set the clicked flag
                clicked = true;
                // Reset the clicking flag
                clicking = false;
            }
        }
    }

    public Vector2 getPosition()
    {
        rectangle.getPosition(tmpVector);
        return tmpVector;
    }

    public Vector2 getSize()
    {
        rectangle.getSize(tmpVector);
        return tmpVector;
    }

    public boolean isVisible() { return visible; }
    public boolean isClicked() { return clicked; }
    public boolean isClicking() { return clicking; }
    public boolean isFadeEffected() { return fadeEffect; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void setFadeEffected(boolean toggle) { fadeEffect = toggle; }

    public void toggle(boolean toggle)
    {
        visible = toggle;
        if(!visible)
            clicked = false;
    }

    public void setPosition(Vector2 position)
    {
        rectangle.setPosition(position);
        rectangle.getSize(tmpVector2);
        clickRectangle.setPosition(position.x - tmpVector2.x * 0.25f, position.y - tmpVector2.y * 0.25f);
    }

    public void setSize(Vector2 size)
    {
        rectangle.setSize(size.x, size.y);
        clickRectangle.setSize(size.x * 1.5f, size.y * 1.5f);
        rectangle.getPosition(tmpVector2);
        clickRectangle.setPosition(tmpVector2.x - size.x * 0.25f, tmpVector2.y - size.y * 0.25f);
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setClicking(boolean clicking) {
        this.clicking = clicking;
    }
}
