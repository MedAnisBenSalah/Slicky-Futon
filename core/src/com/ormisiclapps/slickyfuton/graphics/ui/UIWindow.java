package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OrMisicL on 7/18/2017.
 */

public class UIWindow extends UIWidget
{
    private String title;
    private UIText titleText, text, smallText;
    private TextureRegion texture, contour;
    private UIButton closeButton;
    private Vector2 tmpVector, tmpVector2, tmpVector3, origin;
    private Array<UIWidget> widgetsArray;
    private Map<String, Vector2> textsMap, smallTextsMap;
    private boolean transitioning;
    private Vector2 transitionPosition;
    private Color color;
    private boolean closed;

    public UIWindow(String title, Vector2 position, Vector2 size, Color color)
    {
        // Create the widget
        super(false, position, size);
        // Set the window's title
        this.title = title;
        // Set color
        this.color = color;
        // Create vector
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        tmpVector3 = new Vector2();
        transitionPosition = new Vector2(position);
        // Get the window's textures
        texture = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Get the contour texture
        contour = Core.getInstance().getResourcesManager().getResource("UI/WindowContour", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create the close button instance
        TextureRegion buttonTexture = Core.getInstance().getResourcesManager().getResource("UI/CloseButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION);

        closeButton = new UIButton(buttonTexture);
        // Create texts
        titleText = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 17, Color.WHITE, 0f, Color.WHITE);
        text = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 25, Color.WHITE, 0f, Color.WHITE);
        smallText = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 35, new Color(0.7f, 0.7f, 0.7f, 1f), 0f, Color.WHITE);
        // Set not fade effected
        text.setFadeEffected(false);
        titleText.setFadeEffected(false);
        // Create the text's maps
        textsMap = new HashMap<String, Vector2>();
        smallTextsMap = new HashMap<String, Vector2>();
        // Create the widgets array
        widgetsArray = new Array<UIWidget>();
        // Add the widgets
        addWidget(closeButton);
        addWidget(text);
        addWidget(titleText);
        addWidget(smallText);
        // Setup the close button
        setupCloseButton();
        // Reset flags
        transitioning = false;
        closed = true;
        // Set the origin vector
        origin = new Vector2(size.x / 2f, size.y / 2f);
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void process()
    {
        // Process the widget itself
        super.process();
        // Process transition
        if(transitioning)
            transit();

        // Get the title's size
        Vector2 titleSize = titleText.getSize(title);
        // Draw the title
        titleText.drawText(title, getPosition().x + getSize().x / 2 - titleSize.x / 2, getPosition().y + getSize().y
                        - titleSize.y * 1.5f);

        // Update the widgets
        for(UIWidget widget : widgetsArray)
            widget.process();

        // Draw texts
        for(String text : textsMap.keySet())
            this.text.drawText(text, textsMap.get(text));

        // Draw small texts
        for(String text : smallTextsMap.keySet())
            smallText.drawText(text, smallTextsMap.get(text));

        // If the close button clicked
        if(closeButton.isClicked())
            closed = true;
    }

    @Override
    public void render()
    {
        // Set the drawing color to the window's alpha
        Core.getInstance().getGraphicsManager().setColor(color);
        // Draw the window
        Core.getInstance().getGraphicsManager().drawTextureRegion(texture, tmpVector.set(getPosition()), getSize(), 0f);
        // Set the contour color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw sides
        float thickness = Core.getInstance().getGraphicsManager().HEIGHT / 200f;
        // Bottom
        Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector.set(getPosition()),
                tmpVector2.set(getSize().x, thickness), 0f, origin);

        // Top
        Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector.set(getPosition().x, getPosition().y + getSize().y - thickness),
                tmpVector2.set(getSize().x, thickness), 0f, origin);

        // Left
        tmpVector.set(getPosition().x, getPosition().y + getSize().y);
        tmpVector2.set(getSize().y, thickness);
        Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector, tmpVector2, 270f, tmpVector3.set(0f, 0f));

        // Right
        tmpVector.set(getPosition().x + getSize().x - thickness, getPosition().y + getSize().y);
        tmpVector2.set(getSize().y, thickness);
        Core.getInstance().getGraphicsManager().drawTextureRegion(contour, tmpVector, tmpVector2, 270f, tmpVector3.set(0f, 0f));

        // Render the widgets
        for(UIWidget widget : widgetsArray)
            widget.render();
    }

    @Override
    public void toggle(boolean toggle)
    {
        // Toggle the window
        super.toggle(toggle);
        // Toggle the widgets
        for(UIWidget widget : widgetsArray)
        {
            widget.toggle(toggle);
            // Clean texts arrays
            if(widget instanceof UIText)
                ((UIText)widget).clean();
        }
        // Only if we're toggled on
        if(toggle)
        {
            // Set the transitioning flag
            transitioning = true;
            // Reset closed flag
            closed = false;
            // Change the position
            setPosition(tmpVector2.set(getPosition().x, Core.getInstance().getGraphicsManager().HEIGHT));
        }
    }


    public void addWidget(UIWidget widget)
    {
        // Ensure the widget's validity
        if(!validateWidget(widget))
            return;

        // Set the widget's position to be absolute
        widget.setPosition(widget.getPosition().add(getPosition()));
        // Add it to the widget's array
        widgetsArray.add(widget);
    }

    public void addText(String text, Vector2 position)
    {
        // Set the text's position to be absolute
        position.add(getPosition());
        // If we have another text in the same position then just update the old one
        for(String t : textsMap.keySet())
        {
            // Check position
            if(textsMap.get(t).equals(position))
            {
                textsMap.remove(t);
                break;
            }
        }
        // Add it to the texts map
        textsMap.put(text, position);
    }

    public void addSmallText(String text, Vector2 position)
    {
        // Set the text's position to be absolute
        position.add(getPosition());
        // Add it to the small texts map
        smallTextsMap.put(text, position);
    }

    private boolean validateWidget(UIWidget widget)
    {
        return widget != null && !(widget instanceof UIWindow) && !widgetsArray.contains(widget, true);
    }

    private void transit()
    {
        // Decrease the transition position
        setPosition(tmpVector2.set(getPosition()).sub(0f, Core.getInstance().getGraphicsManager().HEIGHT / 20f));
        // Check the transition position
        if(getPosition().y <= transitionPosition.y)
        {
            // Fix position
            setPosition(transitionPosition);
            // Reset transition flag
            transitioning = false;
        }
    }

    @Override
    public void setPosition(Vector2 position)
    {
        // Get the old position
        tmpVector3.set(getPosition());
        // Change position
        super.setPosition(position);
        // Change widgets positions
        for(UIWidget widget : widgetsArray)
            widget.setPosition(tmpVector.set(position.x + (widget.getPosition().x - tmpVector3.x),
                    position.y + (widget.getPosition().y - tmpVector3.y)));

        // Change texts positions
        for(String text : textsMap.keySet())
        {
            // Get the position
            Vector2 textPosition = textsMap.get(text);
            // Fix position
            textPosition.set(tmpVector.set(position.x + (textPosition.x - tmpVector3.x), position.y + (textPosition.y - tmpVector3.y)));
        }

        // Change small texts positions
        for(String text : smallTextsMap.keySet())
        {
            // Get the position
            Vector2 textPosition = smallTextsMap.get(text);
            // Fix position
            textPosition.set(tmpVector.set(position.x + (textPosition.x - tmpVector3.x), position.y + (textPosition.y - tmpVector3.y)));
        }
    }

    private void setupCloseButton()
    {
        // Calculate the button's size
        float closeSize = Core.getInstance().getGraphicsManager().HEIGHT / 14f;
        Vector2 size = new Vector2(closeSize, closeSize);
        float thickness = Core.getInstance().getGraphicsManager().HEIGHT / 200f;
        // Calculate the button's position
        Vector2 position = new Vector2(getPosition().x + getSize().x - size.x / 2f - thickness,
                getPosition().y + getSize().y - size.y / 2f - thickness);

        // Set the button parameters
        closeButton.setPosition(position);
        closeButton.setSize(size);
    }

    public void cleanTexts()
    {
        textsMap.clear();
        smallTextsMap.clear();
    }

    public UIText getText() {
        return text;
    }

    public UIText getSmallText() {
        return smallText;
    }

    public boolean isClosed() {
        return closed;
    }
}
