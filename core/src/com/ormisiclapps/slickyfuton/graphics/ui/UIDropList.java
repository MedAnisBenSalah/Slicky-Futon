package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.enumerations.ScreenTouchType;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 7/25/2017.
 */

public class UIDropList extends UIWidget
{
    private TextureRegion texture, contour;
    private UIText text;
    private boolean enabled;
    private Vector2 tmpVector, tmpVector2, tmpVector3, dropBoxPosition;
    private UIButton chevronButton;
    private Color color;
    private Array<String> options;
    private float optionSize;
    private boolean optionPressed;
    private Vector2 optionPressedPosition;
    private String selectedOption;
    private boolean selectChanged;

    public UIDropList(Vector2 position, float width)
    {
        super(true, position, new Vector2(width, 0f));
        // Set the parameters
        text = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 25, Color.WHITE, 0f, Color.WHITE);
        text.setFadeEffected(false);
        // Get the texture
        texture = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Get the contour texture
        contour = Core.getInstance().getResourcesManager().getResource("UI/WindowContour", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create button
        TextureRegion chevronTexture = Core.getInstance().getResourcesManager().getResource("UI/Chevron", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        chevronButton = new UIButton(chevronTexture);
        // Setup the button
        Vector2 textSize = text.getSize("A");
        chevronButton.setSize(new Vector2(textSize.y * 0.9f, textSize.y * 1.1f));
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        tmpVector3 = new Vector2();
        dropBoxPosition = new Vector2(position);
        optionPressedPosition = new Vector2();
        // Create color
        color = new Color(1f, 1f, 1f, 1f);
        // Create the options array
        options = new Array<String>();
        // Calculate the single option's size
        optionSize = textSize.y * 3f;
        selectedOption = "High";
        // Reset flags
        enabled = false;
        optionPressed = false;
        selectChanged = false;
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void process()
    {
        // Process widget
        super.process();
        // Reset flags
        selectChanged = false;
        // Draw the chosen option
        text.drawText(selectedOption, getPosition());
        // Calculate the new chevron's position
        Vector2 textSize = text.getSize(selectedOption);
        tmpVector.set(getPosition().x + textSize.x + chevronButton.getSize().x / 4f, getPosition().y - textSize.y);
        // Set the button's position
        chevronButton.setPosition(tmpVector);
        // Restore color
        color.a = 1f;
        text.setColor(color);
        // Reset option pressed flag
        optionPressed = false;
        // Draw texts if its enabled
        if(enabled)
        {
            // Set the initial option position
            tmpVector.set(getPosition()).sub(0f, optionSize);
            // Loop through the options
            for(String option : options)
            {
                // Fix the position
                Vector2 optionTextSize = text.getSize(option);
                tmpVector.sub(0f, optionTextSize.y / 2f);
                // Draw the current option
                text.drawText(option, tmpVector);
                // Prepare the next option's position
                tmpVector.sub(0f, optionSize);
            }
        }
        // Update UI elements
        chevronButton.process();
        text.process();
        // Handle click for the text as well
        if(Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_UP)
        {
            // Get the click position
            Vector2 touchPosition = Core.getInstance().getInputHandler().getScreenTouchPosition();
            // Check if we pressed the text
            if(GameMath.checkCollision(touchPosition, tmpVector.set(1f, 1f),
                    tmpVector2.set(getPosition()).add(textSize.x / 2f, -textSize.y / 2f), textSize))
                chevronButton.setClicked(true);
            else if(!chevronButton.isClicked())
            {
                // Set the initial option position
                tmpVector.set(getPosition()).sub(-getSize().x / 2f, optionSize);
                tmpVector2.set(getSize().x, optionSize * 2f);
                // Loop through the options
                for(String option : options)
                {
                    // Check press
                    if(GameMath.checkCollision(touchPosition, tmpVector3.set(1f, 1f),
                            tmpVector, tmpVector2) && touchPosition.y <= dropBoxPosition.y + getSize().y)
                    {
                        // Set the select changed flag
                        if(!selectedOption.equals(option))
                        {
                            // Set the select changed flag
                            selectChanged = true;
                            // Set the current chosen option
                            selectedOption = option;
                        }
                        break;
                    }
                    // Prepare the next option's position
                    tmpVector.sub(0f, optionSize);
                }
                // Disable the drop list anyways
                enabled = false;
            }
        }
        // Handle press for the text as well
        else if(Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_DOWN ||
                Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_DRAGGED)
        {
            // Get the click position
            Vector2 touchPosition = Core.getInstance().getInputHandler().getScreenTouchPosition();
            // Check if we pressed the text
            if(GameMath.checkCollision(touchPosition, tmpVector.set(1f, 1f),
                    tmpVector2.set(getPosition()).add(textSize.x / 2f, -textSize.y / 2f), textSize))
            {
                color.a = 0.5f;
                text.setColor(color);
                chevronButton.setClicking(true);
            }
            else
            {
                // Set the initial option position
                tmpVector.set(dropBoxPosition.x, getPosition().y).sub(0f, optionSize * 1.7f);
                tmpVector2.set(getSize().x, optionSize * 2f);
                // Loop through the options
                for(String option : options)
                {
                    Vector2 optionTextSize = text.getSize(option);
                    tmpVector.sub(0f, optionTextSize.y / 2f);
                    // Check press
                    if(GameMath.checkCollision(tmpVector.add(tmpVector2.x / 2f, tmpVector2.y / 2f), tmpVector2, touchPosition, tmpVector3.set(1f, 1f))
                            && touchPosition.y <= dropBoxPosition.y + getSize().y)
                    {
                        // Set the current pressed option
                        optionPressed = true;
                        optionPressedPosition.set(tmpVector.sub(tmpVector2.x / 2f, tmpVector2.y / 2f));
                        break;
                    }
                    // Prepare the next option's position
                    tmpVector.sub(tmpVector2.x / 2f, tmpVector2.y / 2f + optionSize);
                }
            }
        }
        // Enable/Disable the drop list if the chevron is clicked
        if(chevronButton.isClicked())
            enabled = !enabled;
        else if(chevronButton.isClicking())
        {
            color.a = 0.5f;
            text.setColor(color);
        }
        // Set the button's rotation
        chevronButton.setRotation(enabled ? 90f : 270f);
    }

    @Override
    public void render()
    {
        // Draw the drop box if its enabled
        if(enabled)
        {
            // Set the drawing color
            Core.getInstance().getGraphicsManager().setColor(0f, 0.2901f, 0.4901f, 0.99f);
            // Draw the background
            Core.getInstance().getGraphicsManager().drawTextureRegion(texture, dropBoxPosition, getSize(), 0f);
            // Restore the drawing color
            Core.getInstance().getGraphicsManager().setColor(Color.WHITE);

            // Draw sides
            float thickness = Core.getInstance().getGraphicsManager().HEIGHT / 200f;
            // Bottom
            Core.getInstance().getGraphicsManager().drawTextureRegion(contour, dropBoxPosition,
                    tmpVector.set(getSize().x, thickness), 0f, tmpVector2.set(getSize().x / 2f, getSize().y / 2f));

            // Top
            Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector.set(dropBoxPosition.x, dropBoxPosition.y + getSize().y - thickness),
                    tmpVector2.set(getSize().x, thickness), 0f, tmpVector3.set(getSize().x / 2f, getSize().y / 2f));

            // Left
            tmpVector.set(dropBoxPosition.x, dropBoxPosition.y + getSize().y);
            tmpVector2.set(getSize().y, thickness);
            Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector, tmpVector2, 270f, tmpVector3.set(0f, 0f));

            // Right
            tmpVector.set(dropBoxPosition.x + getSize().x - thickness, dropBoxPosition.y + getSize().y);
            tmpVector2.set(getSize().y, thickness);
            Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector, tmpVector2, 270f, tmpVector3.set(0f, 0f));

            // Do we have a pressed option ?
            if(optionPressed)
            {
                // Set the drawing color
                Core.getInstance().getGraphicsManager().setColor(0f, 0f, 0f, 0.6f);
                // Draw the option pressed
                Core.getInstance().getGraphicsManager().drawTextureRegion(texture, optionPressedPosition.add(thickness, 0f),
                        tmpVector.set(getSize().x - thickness * 2f, optionSize), 0f);

                // Restore the drawing color
                Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
            }
        }
        // Draw UI elements
        chevronButton.render();
        text.render();
    }

    public void addOption(String option)
    {
        // Add the option
        options.add(option);
        // Recalculate the size
        setSize(tmpVector.set(getSize().x, (options.size + 1) * optionSize));
        // Set the position
        Vector2 position = getPosition();
        Vector2 textSize = text.getSize("A");
        dropBoxPosition.set(position.x - tmpVector.x / 4f, position.y - tmpVector.y - textSize.y * 1.4f);
    }

    @Override
    public void toggle(boolean toggle)
    {
        super.toggle(toggle);
        if(toggle)
            text.clean();
    }

    public void setSelectedOption(String option)
    {
        selectedOption = option;
    }

    public boolean isSelectChanged() {
        return selectChanged;
    }

    public String getSelectedOption() {
        return selectedOption;
    }
}
