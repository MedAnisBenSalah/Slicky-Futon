package com.ormisiclapps.slickyfuton.game.entities.physical.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 9/10/2017.
 */

public class Cube extends GameObject
{
    private Texture texture;
    private TextureRegion layerTexture;
    private float[] vertices;
    private int verticesIndex;
    private Vector2 tmpVector, tmpVector2;
    private Vector2 layerSize;

    private final static int VERTICES_COUNT = 20;

    public Cube()
    {
        super("Cube", false);
        // Reset instances
        texture = null;
        vertices = null;
        verticesIndex = 0;
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        layerSize = new Vector2();
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Create the object
        create(new Vector2(GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH), Configuration.TERRAIN_ROOF_POSITION));
        // Set the body's name
        getBody().setUserData("Cube");
        // Set position
        setPosition(position.add(getSize().x / 2f, getSize().y / 2f));
        // Get the cube's texture
        texture = Core.getInstance().getResourcesManager().getResource("Terrain/Cube", ResourceType.RESOURCE_TYPE_TEXTURE);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        // Get the layer's texture
        layerTexture = Core.getInstance().getResourcesManager().getResource("Terrain/GroundLayer", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create the vertices array
        vertices = new float[VERTICES_COUNT];
        // Set the layer size
        layerSize.set(GameMath.pixelsPerMeters(0.1f, Configuration.TERRAIN_ROOF_POSITION));
    }

    @Override
    public void process()
    {
        // Update the game object
        super.process();
        Color groundColor = new Color(0.2f, 0.6f, 0.8f, 1f);
        // Convert to screen coordinates
        tmpVector.set(Camera.getInstance().worldToScreen(getPosition()));
        tmpVector2.set(GameMath.pixelsPerMeters(getSize()));
        tmpVector.sub(tmpVector2.x / 2f, tmpVector2.y / 2f);
        if(tmpVector.x + tmpVector2.x > Core.getInstance().getGraphicsManager().WIDTH)
            tmpVector2.x = Core.getInstance().getGraphicsManager().WIDTH - tmpVector.x;

        verticesIndex = 0;
        // Convert the texture's unit size in screen coordinates
        float unitSize = GameMath.pixelsPerMeters(getSize().y / 12f);
        // Set the background vertices
        // Down Left
        vertices[verticesIndex++] = tmpVector.x;
        vertices[verticesIndex++] = tmpVector.y;
        vertices[verticesIndex++] = groundColor.toFloatBits();
        vertices[verticesIndex++] = 0f;
        vertices[verticesIndex++] = tmpVector2.y / unitSize;

        // Up Left
        vertices[verticesIndex++] = tmpVector.x;
        vertices[verticesIndex++] = tmpVector.y + tmpVector2.y;
        vertices[verticesIndex++] = groundColor.toFloatBits();
        vertices[verticesIndex++] = 0f;
        vertices[verticesIndex++] = 0f;

        // Up Right
        vertices[verticesIndex++] = tmpVector.x + tmpVector2.x;
        vertices[verticesIndex++] = tmpVector.y + tmpVector2.y;
        vertices[verticesIndex++] = groundColor.toFloatBits();
        vertices[verticesIndex++] = tmpVector2.x / unitSize;
        vertices[verticesIndex++] = 0f;

        // Down Right
        vertices[verticesIndex++] = tmpVector.x + tmpVector2.x;
        vertices[verticesIndex++] = tmpVector.y;
        vertices[verticesIndex++] = groundColor.toFloatBits();
        vertices[verticesIndex++] = tmpVector2.x / unitSize;
        vertices[verticesIndex++] = tmpVector2.y / unitSize;
    }

    @Override
    public void render()
    {
        // Draw the cube
        Core.getInstance().getGraphicsManager().drawTexture(texture, vertices, verticesIndex);
        // Draw the layer
        Core.getInstance().getGraphicsManager().drawTextureRegion(layerTexture, tmpVector, layerSize, 0f);
    }
}
