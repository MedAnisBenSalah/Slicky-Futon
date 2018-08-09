package com.ormisiclapps.slickyfuton.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.DisplayType;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.nodes.entity.ModelNode;
import com.ormisiclapps.slickyfuton.utility.Configuration;

/**
 * Created by OrMisicL on 5/29/2016.
 * Will automatically handle loading and disposing of game resources (assets) based on the game state.
 * Will also dispose of all of the remaining resources when leaving.
 */
public class ResourcesManager
{
    private AssetManager assetManager;
    private DisplayType displayType;

    public ResourcesManager()
    {
        // Create the asset manager instance
        assetManager = new AssetManager();
    }

    public void update()
    {
        // Update the assets manager
        if(isLoading())
            assetManager.update();
    }

    /* This will load the initial resources needed by the game in order to run */
    public void loadInitialResources()
    {
        // If the user never chosen his desired quality then decide for him
        if(Core.getInstance().getStatsSaver().savedData.usedQuality == -1)
        {
            // Find which display type we're using
            int w = Gdx.graphics.getWidth();
            if(w > 1024)
                displayType = DisplayType.DISPLAY_TYPE_HD;
            else if(w >= 512)
                displayType = DisplayType.DISPLAY_TYPE_MD;
            else
                displayType = DisplayType.DISPLAY_TYPE_SD;

            // Set the used quality
            Core.getInstance().getStatsSaver().savedData.usedQuality = displayType.ordinal();
        }
        else
        {
            // Set it to the saved display type
            if(Core.getInstance().getStatsSaver().savedData.usedQuality == 0)
                displayType = DisplayType.DISPLAY_TYPE_HD;
            else if(Core.getInstance().getStatsSaver().savedData.usedQuality == 1)
                displayType = DisplayType.DISPLAY_TYPE_MD;
            else
                displayType = DisplayType.DISPLAY_TYPE_SD;
        }
        // Set the lightening quality
        if(displayType == DisplayType.DISPLAY_TYPE_MD)
            Configuration.ENTITY_RAYS_NUMBER = Configuration.MEDIUM_QUALITY_ENTITY_RAYS_NUMBER;
        else if(displayType == DisplayType.DISPLAY_TYPE_SD)
            Configuration.ENTITY_RAYS_NUMBER = Configuration.LOW_QUALITY_ENTITY_RAYS_NUMBER;

        // Load the loading background
        requestResource("UI/LoadingBackground", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Load empty textures
        requestResource("Empty", ResourceType.RESOURCE_TYPE_TEXTURE);
        requestResource("HalfEmpty", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Load resources now
        assetManager.finishLoading();
    }

    public void loadResources()
    {
        // Load texture atlas
        requestResource("Main", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        requestResource("UI", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        requestResource("Terrain", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        requestResource("Models", ResourceType.RESOURCE_TYPE_MODEL);
        // Game resources
        loadGameResources();
    }

    private void loadGameResources()
    {
        // Load the model settings
        Core.getInstance().getModelSettings().load();
        // Load pre set combinations
        Core.getInstance().getPreSetCombinations().load();
        // Load UI textures
        requestResource("UI/ShopBackground", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Load terrain textures
        requestResource("Terrain/Ground", ResourceType.RESOURCE_TYPE_TEXTURE);
        requestResource("Terrain/Background", ResourceType.RESOURCE_TYPE_TEXTURE);
        requestResource("Terrain/Cube", ResourceType.RESOURCE_TYPE_TEXTURE);
        requestResource("Terrain/Spikes", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Load objects
        requestResource("Objects/Skins", ResourceType.RESOURCE_TYPE_TEXTURE);
        requestResource("Objects/TailSkins", ResourceType.RESOURCE_TYPE_TEXTURE);
        requestResource("Objects/WallLayer", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Load all the models
        for(int i = 0; i < Core.getInstance().getModelSettings().getModelsCount(); i++)
        {
            // Get the model name
            String modelName = Core.getInstance().getModelSettings().getModel(i);
            // Only load objects
            if(Core.getInstance().getModelSettings().getModelAttribute(modelName, "type").equals("Object")
                    || Core.getInstance().getModelSettings().getModelAttribute(modelName, "type").equals("CombinationObject")
                    || Core.getInstance().getModelSettings().getModelAttribute(modelName, "type").equals("ExclusiveObject")
                    || Core.getInstance().getModelSettings().getModelAttribute(modelName, "type").equals("EatableObject")
                    || Core.getInstance().getModelSettings().getModelAttribute(modelName, "type").equals("GravityObject"))
            {
                // Load the object's model
                ModelNode node = Core.getInstance().getModelManager().loadModel(modelName);
                // Load object's body if necessary
                if(node.shape.equals("PolygonShape"))
                    Core.getInstance().getModelManager().loadBody(node, modelName);
            }
        }
        // Load sounds
        requestResource("ButtonClick", ResourceType.RESOURCE_TYPE_SOUND);
        requestResource("Dying", ResourceType.RESOURCE_TYPE_SOUND);
        // Load music
        requestResource("MenuMusic", ResourceType.RESOURCE_TYPE_MUSIC);
        requestResource("GameMusic1", ResourceType.RESOURCE_TYPE_MUSIC);
        requestResource("GameMusic2", ResourceType.RESOURCE_TYPE_MUSIC);
        requestResource("GameMusic3", ResourceType.RESOURCE_TYPE_MUSIC);
        requestResource("GameMusic4", ResourceType.RESOURCE_TYPE_MUSIC);
    }

    public void dispose()
    {
        // Get all the loaded resources
        Array<Disposable> resources = new Array<Disposable>();
        assetManager.getAll(Disposable.class, resources);
        // Loop through all the loaded resources
        for(Disposable resource : resources)
            // Dispose of the resource
            resource.dispose();

        // Dispose of the asset manager
        assetManager.dispose();
    }

    public <T> T getResource(String name, ResourceType type)
    {
        // Return the resource depending on its type
        switch(type)
        {
            // Texture resource
            case RESOURCE_TYPE_TEXTURE:
                return assetManager.get("Textures/" + getTextureDisplay() + "/" + name + ".png");

            // Texture region resource
            case RESOURCE_TYPE_TEXTURE_REGION:
                // Get the texture's atlas name
                String atlasName = name.substring(0, name.indexOf("/"));
                // Find the texture atlas
                TextureAtlas atlas = assetManager.get("Textures/" + getTextureDisplay() + "/" + atlasName + ".pack");
                // Get the specified region
                return (T)atlas.findRegion(name.substring(name.indexOf("/") + 1));

            // Model resource
            case RESOURCE_TYPE_MODEL:
                // Find the texture atlas
                atlas = assetManager.get("Models/Models" + getTextureDisplay() + ".pack");
                // Get the model's region
                return (T)atlas.findRegion(name);

            // Sound resource
            case RESOURCE_TYPE_SOUND:
                return assetManager.get("Audio/" + name + ".ogg");

            // Music resource
            case RESOURCE_TYPE_MUSIC:
                return assetManager.get("Audio/" + name + ".ogg");
        }
        return null;
    }

    public void requestResource(String name, ResourceType type)
    {
        // Add the resource to the loading queue
        switch (type)
        {
            case RESOURCE_TYPE_MODEL:
                assetManager.load("Models/Models" + getTextureDisplay() + ".pack", TextureAtlas.class);
                break;

            case RESOURCE_TYPE_TEXTURE:
                assetManager.load("Textures/" + getTextureDisplay() + "/" + name + ".png", Texture.class);
                break;

            case RESOURCE_TYPE_TEXTURE_REGION:
                assetManager.load("Textures/" + getTextureDisplay() + "/" + name + ".pack", TextureAtlas.class);
                break;

            case RESOURCE_TYPE_SOUND:
                assetManager.load("Audio/" + name + ".ogg", Sound.class);
                break;

            case RESOURCE_TYPE_MUSIC:
                assetManager.load("Audio/" + name + ".ogg", Music.class);
                break;

        }
    }

    public boolean isResourceLoaded(String name, ResourceType type)
    {
        // Add the resource to the loading queue
        switch (type)
        {
            case RESOURCE_TYPE_MODEL:
                return getResource(name, type) != null;

            case RESOURCE_TYPE_TEXTURE:
                return assetManager.isLoaded("Textures/" + getTextureDisplay() + "/" + name + ".png", Texture.class);

            case RESOURCE_TYPE_TEXTURE_REGION:
                return assetManager.isLoaded("Textures/" + getTextureDisplay() + "/" + name + ".pack", TextureAtlas.class);

            case RESOURCE_TYPE_SOUND:
                return assetManager.isLoaded("Audio/" + name + ".ogg", Sound.class);

            case RESOURCE_TYPE_MUSIC:
                return assetManager.isLoaded("Audio/" + name + ".ogg", Music.class);
        }
        return false;
    }

    private String getTextureDisplay()
    {
        if(displayType == DisplayType.DISPLAY_TYPE_HD)
            return "HD";
        else if(displayType == DisplayType.DISPLAY_TYPE_MD)
            return "MD";
        else
            return "SD";
    }

    public DisplayType getDisplayType() { return displayType; }

    public boolean isLoading()
    {
        return assetManager.getQueuedAssets() != 0;
    }

    public int getLoadingProgress()
    {
        return (int)(assetManager.getProgress() * 100);
    }
}
