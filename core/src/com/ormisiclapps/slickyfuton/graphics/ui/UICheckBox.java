package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;

/**
 * Created by OrMisicL on 8/14/2017.
 */

public class UICheckBox extends UIWidget
{
    private TextureRegion texture, selectedTexture;
    private boolean checked;
    private String text;
    private UIText checkBoxText;
    private Vector2 tmpVector;
    private Vector2 realSize;

    public UICheckBox(Vector2 position, Vector2 size, String text)
    {
        // Create the widget
        super(true, position, size);
        // Set the text
        this.text = text;
        // Get textures
        texture = Core.getInstance().getResourcesManager().getResource("UI/CheckBox", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        selectedTexture = Core.getInstance().getResourcesManager().getResource("UI/SelectedCheckBox", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Reset flags
        checked = false;
        // Create the checkbox text
        checkBoxText = new UIText((int)(size.y * 0.75f), Color.WHITE, 0f, Color.WHITE);
        // Create vectors
        tmpVector = new Vector2();
        // Create the real size vector
        realSize = new Vector2(getSize());
        // Adjust the size to include the text
        Vector2 textSize = checkBoxText.getSize(text);
        setSize(new Vector2(getSize().x * 1.25f + textSize.x, getSize().y));
    }

    @Override
    public void dispose() {

    }

    @Override
    public void process()
    {
        super.process();
        // Draw the checkbox text
        Vector2 textSize = checkBoxText.getSize(text);
        checkBoxText.drawText(text, getPosition().x + realSize.x * 1.25f, getPosition().y + realSize.y / 2f + textSize.y / 2f);
        // Update the text
        checkBoxText.process();
        // Update the checked state
        if(isClicked())
            checked = !checked;
    }

    @Override
    public void render()
    {
        // Draw the checkbox texture
        Core.getInstance().getGraphicsManager().drawTextureRegion(checked ? selectedTexture : texture, tmpVector.set(getPosition()), realSize, 270f);
        // Draw the text
        checkBoxText.render();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
