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
 * Created by OrMisicL on 8/4/2017.
 */

public class UIPicker<T> extends UIWidget
{
    private Array<T> elements;
    private boolean[] elementsState;
    private float elementSlotSize;
    private TextureRegion emptyTexture, selectorTexture, lockIconTexture;
    private Vector2 tmpVector, tmpVector2, tmpVector3;
    private T selectedElement;
    private int selectedElementId;
    private boolean selectChanged;
    private Color elementColor, elementBackgroundColor, tmpColor;
    private int rows;
    private int pages, currentPage, elementsPerPage;

    public UIPicker(Vector2 position, Vector2 size, int rows, float elementSlotSize, boolean[] elementsState)
    {
        // Create the widget
        super(false, position, size);
        // Create the elements array
        elements = new Array<T>();
        // Set the elements state array
        this.elementsState = elementsState;
        // Calculate the element's slot size
        this.elementSlotSize = elementSlotSize;
        // Get the empty texture
        emptyTexture = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Get the selector texture
        selectorTexture = Core.getInstance().getResourcesManager().getResource("UI/Selector", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Get the lock icon texture
        lockIconTexture = Core.getInstance().getResourcesManager().getResource("UI/LockIcon", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        tmpVector3 = new Vector2();
        // Create colors
        elementColor = new Color(Color.WHITE);
        elementBackgroundColor = new Color(Color.BLACK);
        // Set the rows number
        this.rows = rows;
        // Reset the selected element instance
        selectedElement = null;
        selectedElementId = -1;
        // Reset pages
        pages = 0;
        currentPage = 0;
        // Calculate the elements per page
        elementsPerPage = (int)((getSize().x * rows) - (elementSlotSize * 3.75f)) / (int)(elementSlotSize * 1.25f);
        // Create color instance
        tmpColor = new Color();
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void process()
    {
        super.process();
        // Reset the select changed flag
        selectChanged = false;
        // Only if the screen is pressed
        if(!UIMain.getInstance().isSkipInput() && Core.getInstance().getInputHandler().isScreenTouched() &&
                Core.getInstance().getInputHandler().getScreenTouchType() == ScreenTouchType.SCREEN_TOUCH_UP)
        {
            if(rows > 1)
                // Set the starting position
                tmpVector.set(getPosition().x + elementSlotSize * 2.5f, getPosition().y + elementSlotSize * 0.5f +
                    getSize().y / 2f + elementSlotSize * 0.125f * (rows / 2) + elementSlotSize * 0.625f * (rows % 2));
            else
                tmpVector.set(getPosition().x + elementSlotSize * 2.5f, getPosition().y + elementSlotSize * 0.5f + elementSlotSize * 0.625f);

            // Get the touch position
            Vector2 touchPosition = Core.getInstance().getInputHandler().getScreenTouchPosition();
            // Loop through the colors
            int id = -1;
            for(T element : elements)
            {
                // Increase the id count
                id++;
                // Get only the page elements
                if(id >= elementsPerPage * (currentPage + 1))
                    break;
                else if(id < elementsPerPage * currentPage)
                    continue;

                // Check the touched position
                if(GameMath.checkCollision(tmpVector, tmpVector3.set(elementSlotSize, elementSlotSize), touchPosition, tmpVector2.set(1f, 1f)))
                {
                    // Set the selected element
                    selectedElement = element;
                    // Set the selected item id
                    selectedElementId = id;
                    // Set the select changed flag
                    selectChanged = true;
                    break;
                }
                // Increase the the position
                tmpVector.add(elementSlotSize * 1.25f, 0f);
                // If its surpasses the actual size then get down
                if(tmpVector.x >= getPosition().x + getSize().x - elementSlotSize * 1.25f)
                    tmpVector.set(getPosition().x + elementSlotSize * 2.5f, tmpVector.y - elementSlotSize * 1.25f);
            }
        }
    }

    @Override
    public void render()
    {
        // Set the starting position
        if(rows > 1)
            tmpVector.set(getPosition().x + elementSlotSize * 2f,
                getPosition().y + getSize().y / 2f + elementSlotSize * 0.125f * (rows / 2) + elementSlotSize * 0.625f * (rows % 2));
        else
            tmpVector.set(getPosition().x + elementSlotSize * 2f, getPosition().y + elementSlotSize * 0.625f);

        // Loop through the colors
        T firstElement = null;
        int id = -1;
        for(T element : elements)
        {
            // Get the first element
            if(firstElement == null)
                firstElement = element;

            // Increase the id count
            id++;
            // Get only the page elements
            if(id >= elementsPerPage * (currentPage + 1))
                break;
            else if(id < elementsPerPage * currentPage)
                continue;

            // If its the selected element then highlight it
            if(selectedElement.equals(element))
            {
                // Calculate the glow size
                float glowSize = elementSlotSize / 12f;
                // Set the drawing color
                Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
                // Draw the element slot
                Core.getInstance().getGraphicsManager().drawTextureRegion(selectorTexture, tmpVector.x - glowSize, tmpVector.y - glowSize,
                        elementSlotSize + glowSize * 2f, elementSlotSize + glowSize * 2f, 270f);
            }
            // Set the drawing color
            if(element instanceof Color)
            {
                // If the element is locked then reduce its alpha
                if(!elementsState[id])
                {
                    // Set the color's alpha
                    tmpColor.set((Color)element);
                    tmpColor.a = 0.5f;
                    // Set the drawing color
                    Core.getInstance().getGraphicsManager().setColor(tmpColor);
                }
                else
                    // Set the drawing color
                    Core.getInstance().getGraphicsManager().setColor((Color)element);

                // Draw the color slot
                Core.getInstance().getGraphicsManager().drawTextureRegion(emptyTexture, tmpVector.x, tmpVector.y, elementSlotSize, elementSlotSize, 0f);
            }
            else if(element instanceof TextureRegion)
            {
                // If the element is locked then reduce its alpha
                if(!elementsState[id])
                {
                    // Set the color's alpha
                    tmpColor.set(elementBackgroundColor);
                    tmpColor.a = 0.5f;
                    // Set the drawing color
                    Core.getInstance().getGraphicsManager().setColor(tmpColor);
                }
                else
                    // Set the background color
                    Core.getInstance().getGraphicsManager().setColor(elementBackgroundColor);

                // Draw the background
                Core.getInstance().getGraphicsManager().drawTextureRegion((TextureRegion)firstElement, tmpVector, tmpVector2.set(elementSlotSize, elementSlotSize), 270f);

                // If the element is locked then reduce its alpha
                if(!elementsState[id])
                {
                    // Set the color's alpha
                    tmpColor.set(elementColor);
                    tmpColor.a = 0.5f;
                    // Set the drawing color
                    Core.getInstance().getGraphicsManager().setColor(tmpColor);
                }
                else
                    // Set the element color
                    Core.getInstance().getGraphicsManager().setColor(elementColor);

                // Draw the element
                Core.getInstance().getGraphicsManager().drawTextureRegion((TextureRegion)element, tmpVector, tmpVector2.set(elementSlotSize, elementSlotSize), 270f);
            }
            // If its locked then draw the lock icon
            if(!elementsState[id])
            {
                // Restore the color
                Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
                // Draw the element
                float sizeY = elementSlotSize / 1.75f;
                float sizeX = sizeY * 1.25f;
                tmpVector3.set(tmpVector).add(elementSlotSize / 2f - sizeX / 2f, elementSlotSize / 2f - sizeY / 2f);
                Core.getInstance().getGraphicsManager().drawTextureRegion(lockIconTexture, tmpVector3, tmpVector2.set(sizeX, sizeY), 270f);
            }
            // Increase the the position
            tmpVector.add(elementSlotSize * 1.25f, 0f);
            // If its surpasses the actual size then get down
            if(tmpVector.x >= getPosition().x + getSize().x - elementSlotSize * 2f)
                tmpVector.set(getPosition().x + elementSlotSize * 2f, tmpVector.y - elementSlotSize * 1.25f);
        }
        // Restore the color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
    }

    public void addElement(T element)
    {
        // Add the element
        if(!elements.contains(element, false))
        {
            elements.add(element);
            // Calculate the pages count
            pages = elements.size / elementsPerPage;
        }
    }

    public void nextPage()
    {
        if(currentPage < pages)
            currentPage++;
    }

    public void previousPage()
    {
        if(currentPage > 0)
            currentPage--;
    }

    public void setElements(Array<T> elements) {

        this.elements.addAll(elements);
        // Calculate the pages count
        pages = elements.size / elementsPerPage;
    }

    public Array<T> getElements() {
        return elements;
    }

    public T getSelectedElement() {
        return selectedElement;
    }

    public boolean isSelectChanged() {
        return selectChanged;
    }

    public int getSelectedElementId() {
        return selectedElementId;
    }

    public Vector2 getElementsPosition()
    {
        return tmpVector.set(getPosition().x + elementSlotSize * 2f,
                getPosition().y + getSize().y / 2f + elementSlotSize * 0.125f * (rows / 2) + elementSlotSize * 0.625f * (rows % 2) -
                        elementSlotSize);
    }

    public void setSelectedElement(T selectedElement) {
        this.selectedElement = selectedElement;
    }

    public void setSelectedElementId(int selectedElementId) {
        this.selectedElementId = selectedElementId;
        selectedElement = elements.get(selectedElementId);
    }
}
