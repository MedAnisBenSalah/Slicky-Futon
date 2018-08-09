package com.ormisiclapps.slickyfuton.game.entities.utility;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.nodes.entity.ModelNode;

/**
 * Created by OrMisicL on 9/29/2015.
 * Will handle the model's texture for each entity
 */
public class Model
{
    private ModelNode node;
    private TextureRegion texture;
    private String name;
    private boolean shouldRender;

    public Model(String name)
    {
        // Save the name
        this.name = name;
        // Reset instances
        texture = null;
        node = null;
        // Reset flags
        shouldRender = true;
    }

    public void load()
    {
        // Load the model
        node = Core.getInstance().getModelManager().loadModel(name);
        // Get the texture
        texture = Core.getInstance().getResourcesManager().getResource(name, ResourceType.RESOURCE_TYPE_MODEL);
        // If the model has no texture then use the default empty one
        if(texture == null)
        {
            // Treat special cases
            if(name.equals("Inflatable"))
                texture = Core.getInstance().getResourcesManager().getResource("Chainsaw", ResourceType.RESOURCE_TYPE_MODEL);
            else if(name.equals("RectangularCircularParted"))
                texture = Core.getInstance().getResourcesManager().getResource("CircularParted", ResourceType.RESOURCE_TYPE_MODEL);
            else if(name.equals("RectangularParted"))
                texture = Core.getInstance().getResourcesManager().getResource("LinearParted", ResourceType.RESOURCE_TYPE_MODEL);
            else
            {
                texture = Core.getInstance().getResourcesManager().getResource("Empty", ResourceType.RESOURCE_TYPE_MODEL);
                shouldRender = false;
            }
        }
    }

    public String getName() { return name; }
    public ModelNode getNode() { return node; }
    public TextureRegion getTexture() { return texture; }
    public boolean isShouldRender() { return shouldRender; }
}
