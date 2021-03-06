package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;

/**
 * Created by OrMisicL on 1/5/2016.
 */
public class UIButton extends UIWidget
{
    private TextureRegion texture;
    private float rotation;
    private Vector2 tmpVector;
    private Color color;

    public UIButton(TextureRegion texture)
    {
        // Setup the widget
        super(true);
        // Set the button texture region
        this.texture = texture;
        // Reset values
        rotation = 270f;
        // Create vectors
        tmpVector = new Vector2();
        // Create the colro instance
        color = new Color(Color.WHITE);
    }

    @Override
    public void dispose()
    {
        // Textures should only be disposed from the asset manager
    }

    @Override
    public void render()
    {
        // Set the color depending on its click state
        float a = clicking || !isEnabled() ? 0.5f : 1f;
        Core.getInstance().getGraphicsManager().setColor(color.r, color.g, color.b, a);
        // Render the button texture
        tmpVector.set(getPosition());
        Core.getInstance().getGraphicsManager().drawTextureRegion(texture, tmpVector, getSize(), rotation);
        // Restore the color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
    }

    @Override
    public void process()
    {
        // Process the widget
        super.process();
        // Play the clicked sound
        if(clicked)
            UIMain.getInstance().getButtonClickSound().play();
    }

    public void setRotation(float rotation) { this.rotation = rotation; }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
