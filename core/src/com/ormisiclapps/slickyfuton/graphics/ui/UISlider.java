package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 8/15/2017.
 */

public class UISlider extends UIWidget
{
    private Color containerColor;
    private Color backgroundColor;
    private Color color;
    private TextureRegion emptyTexture, sliderTexture;
    private int value;
    private Vector2 currentSize;
    private float containerSize;
    private Vector2 sliderPosition, sliderSize;
    private boolean sliding;
    private Vector2 tmpVector, tmpVector2;
    private boolean valueChanged;

    public UISlider(Vector2 position, Vector2 size, Color containerColor, Color backgroundColor, Color color)
    {
        // Call the UIWidget constructor
        super(true, position, size);
        // Save colors
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.containerColor = containerColor;
        // Create size vector
        currentSize = new Vector2();
        // Reset values
        value = 0;
        containerSize = 0f;
        // Get the texture
        emptyTexture = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        sliderTexture = Core.getInstance().getResourcesManager().getResource("UI/Slider", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Initialize the container's size
        containerSize = getSize().y / 7.5f;
        // Create vectors
        sliderPosition = new Vector2();
        sliderSize = new Vector2();
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        // Reset flags
        sliding = false;
        valueChanged = false;
    }

    @Override
    public void dispose()
    {
        // We don't have any disposable materials for this widget
    }

    @Override
    public void process()
    {
        super.process();
        // Reset value changed flag
        valueChanged = false;
        // Calculate the current size based on the progress
        currentSize.set(getSize().x * value / 100f, getSize().y);
        // Calculate the slider size
        sliderSize.set(getSize().y * 2f, getSize().y * 2f);
        // Calculate the slider position
        sliderPosition.set(getPosition().x + currentSize.x - sliderSize.x / 2f, getPosition().y + getSize().y / 2f - sliderSize.y / 2f);
        // Find if we're holding the slider down
        Vector2 touchPosition = Core.getInstance().getInputHandler().getScreenTouchPosition();
        if(Core.getInstance().getInputHandler().isScreenTouched())
        {
            // Start changing the slider's position
            if(!sliding && GameMath.checkCollision(touchPosition, tmpVector.set(1f, 1f),
                    tmpVector2.set(sliderPosition).add(sliderSize.x / 2f, sliderSize.y / 2f), sliderSize))
                // Set the sliding flag
                sliding = true;
            else if(sliding || (!sliding && isClicked()))
                // Set the value changed flag
                valueChanged = true;
        }
        else
            sliding = false;

        // Check if the value has changed
        if(valueChanged)
        {
            // Set the progress depending on the touch's horizontal position
            if(touchPosition.x < getPosition().x)
                value = 0;
            else if(touchPosition.x > getPosition().x + getSize().x)
                value = 100;
            else
                value = (int)((touchPosition.x - getPosition().x) / getSize().x * 100f);

            // Calculate the current size based on the progress
            currentSize.set(getSize().x * value / 100f, getSize().y);
            // Calculate the slider size
            sliderSize.set(getSize().y * 2f, getSize().y * 2f);
            // Calculate the slider position
            sliderPosition.set(getPosition().x + currentSize.x - sliderSize.x / 2f, getPosition().y + getSize().y / 2f - sliderSize.y / 2f);
        }
    }

    @Override
    public void render()
    {
        // Set the background color
        Core.getInstance().getGraphicsManager().setColor(backgroundColor);
        // Draw the background
        Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, getPosition().x, getPosition().y, getSize().x, getSize().y, 0f);
        // Set the main color
        Core.getInstance().getGraphicsManager().setColor(color);
        // Draw the shape
        Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, getPosition(), currentSize, 0f);
        // Set the container color
        Core.getInstance().getGraphicsManager().setColor(containerColor);
        // Draw the container

        // Left
        Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, getPosition().x - containerSize, getPosition().y,
                containerSize, getSize().y, 0f);

        // Right
        Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, getPosition().x + getSize().x, getPosition().y,
                containerSize, getSize().y, 0f);

        // Top
        Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, getPosition().x - containerSize, getPosition().y + getSize().y,
                getSize().x + containerSize * 2f, containerSize, 0f);

        // Bottom
        Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, getPosition().x - containerSize, getPosition().y - containerSize,
                getSize().x + containerSize * 2f, containerSize, 0f);

        // Draw the slider
        Core.getInstance().getGraphicsManager().drawTextureRegion(sliderTexture, sliderPosition, sliderSize, 0f);

        // Restore color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
    }

    public void setValue(int value)
    {
        if(value > 100)
            this.value = 100;
        else if(value < 0)
            this.value = 0;
        else
            this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isValueChanged() {
        return valueChanged;
    }
}
