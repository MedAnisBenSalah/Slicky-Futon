package com.ormisiclapps.slickyfuton.game.entities.notphysical;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.world.Terrain;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 9/13/2016.
 */
public class TerrainObject
{
    private TextureRegion texture;
    private Vector2 position;
    protected Vector2 screenPosition;
    protected Vector2 screenSize;
    private Vector2 origin;
    private Vector2 size;
    private boolean renderObject;
    private String type;

    public TerrainObject(String model, Vector2 position)
    {
        // Get the object's terrain
        texture = Core.getInstance().getResourcesManager().getResource("Terrain/" + model, ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Save the object's coordinates
        this.position = new Vector2(position);
        // Save the model type
        type = Core.getInstance().getModelSettings().getModelAttribute(model, "type");
        // Get the model's size
        float x = Core.getInstance().getModelSettings().getModelAttributeAsFloat(model, "sizeX");
        float y;
        if(Core.getInstance().getModelSettings().getModelAttribute(model, "sizeY").equals("roof"))
            y = Configuration.TERRAIN_ROOF_POSITION;// + 1f;
        else
            y = Core.getInstance().getModelSettings().getModelAttributeAsFloat(model, "sizeY");

        // Set the size
        size = new Vector2(x, y);
        // Convert to screen coordinates
        screenSize = new Vector2(GameMath.pixelsPerMeters(size));
        // Create the origin vector
        origin = new Vector2(screenSize.y / 2f, screenSize.x / 2f);
        // Create the screen position vector
        screenPosition = new Vector2();
        // Reset flags
        renderObject = false;
    }

    public void process()
    {
        // Convert to screen coordinates
        screenPosition.set(Camera.getInstance().worldToScreen(position));
        // Check if we should render
        renderObject = (screenPosition.x <= Core.getInstance().getGraphicsManager().WIDTH && screenPosition.x + screenSize.x >= 0 &&
                screenPosition.y <= Core.getInstance().getGraphicsManager().HEIGHT && screenPosition.y + screenSize.y >= 0);
    }

    public void render()
    {
        // Validate rendering
        if(renderObject && screenPosition != null && screenSize != null)
        {
            // Set color if necessary
            if(type.equals("TerrainGroundObject"))
                Core.getInstance().getGraphicsManager().setColor(Terrain.getInstance().getEntitiesColor());

            // Draw the texture
            Core.getInstance().getGraphicsManager().drawTextureRegion(texture, screenPosition, screenSize, 0f, origin);
            // Restore color
            Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        }
    }

    public Vector2 getPosition() { return position; }
    public Vector2 getSize() { return size; }
}
