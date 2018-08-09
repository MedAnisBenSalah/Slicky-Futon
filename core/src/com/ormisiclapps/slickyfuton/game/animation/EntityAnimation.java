package com.ormisiclapps.slickyfuton.game.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;

/**
 * Created by Anis on 11/20/2016.
 */

public class EntityAnimation
{
    private Texture texture;
    private TextureRegion textureRegion;
    private int linesCount;
    private int columnsCount;
    private float frameDuration;
    private float animationTime;
    private boolean looping;
    private Animation<TextureRegion> animation;

    public EntityAnimation(Texture texture, int linesCount, int columnsCount, float frameDuration, boolean looping)
    {
        // Save the animation texture
        this.texture = texture;
        // Save lines and columns count
        this.linesCount = linesCount;
        this.columnsCount = columnsCount;
        // Save the frame duration
        this.frameDuration = frameDuration;
        // Set the looping flag
        this.looping = looping;
        // Initialize the animations
        initialize();
    }

    public EntityAnimation(TextureRegion textureRegion, int linesCount, int columnsCount, float frameDuration, boolean looping)
    {
        // Save the animation texture
        this.textureRegion = textureRegion;
        texture = null;
        // Save lines and columns count
        this.linesCount = linesCount;
        this.columnsCount = columnsCount;
        // Save the frame duration
        this.frameDuration = frameDuration;
        // Set the looping flag
        this.looping = looping;
        // Initialize the animations
        initialize();
    }

    private void initialize()
    {
        // Split the texture into a matrix of regions
        TextureRegion[][] regions;
        if(texture == null)
            regions = textureRegion.split(textureRegion.getRegionWidth() / columnsCount, textureRegion.getRegionHeight() / linesCount);
        else
            regions = TextureRegion.split(texture, texture.getWidth() / columnsCount, texture.getHeight() / linesCount);

        // Create the texture regions array
        TextureRegion[] textureRegions = new TextureRegion[linesCount * columnsCount];
        // Fill the texture regions array
        for(int i = 0; i < linesCount; i++)
            System.arraycopy(regions[i], 0, textureRegions, regions[i].length * i, regions[i].length);

        // Create the animation instance
        animation = new Animation<TextureRegion>(frameDuration, textureRegions);
    }

    public void start()
    {
        // Reset the animation start time
        animationTime = 0;
    }

    public void render(Vector2 position, Vector2 size, float rotation, Color color)
    {
        // Validate the animation
        if(animation == null)
            return;

        // Update the animation time
        animationTime += Gdx.graphics.getDeltaTime();
        // Get the animation frame
        TextureRegion textureRegion = animation.getKeyFrame(animationTime, looping);
        // Validate the texture region
        if(textureRegion == null)
            return;

        // Set the drawing color color
        Core.getInstance().getGraphicsManager().setColor(color);
        // Draw the animation texture
        Core.getInstance().getGraphicsManager().drawTextureRegion(textureRegion, position, size, rotation);
        // Restore the drawing color
        Core.getInstance().getGraphicsManager().setColor(1f, 1f, 1f, 1f);
    }
}
