package com.ormisiclapps.slickyfuton.game.entities.physical.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.world.Terrain;

/**
 * Created by Anis on 1/14/2017.
 */

public class ChainGameObject extends GameObject
{
    private float[] groundVertices;
    private float[] roofVertices;
    private float[] groundLayerVertices;
    private float[] roofLayerVertices;
    private Texture texture, layerTexture;
    private Color wallColor;

    private Vector2 vertex;
    private Vector2 vertexTmp;
    private Vector2 position;

    private static final int VERTICES_COUNT = 20;
    private static final float LAYER_SIZE = 1.5f;

    public ChainGameObject(String model, boolean passable)
    {
        // Create the game object
        super(model, passable);
        // Reset values
        groundVertices = null;
        roofVertices = null;
        groundLayerVertices = null;
        roofLayerVertices = null;
        // Get the textures
        texture = Core.getInstance().getResourcesManager().getResource("Empty", ResourceType.RESOURCE_TYPE_TEXTURE);
        layerTexture = Core.getInstance().getResourcesManager().getResource("Objects/WallLayer", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Set the layer texture to be repetitive
        layerTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        // Create vectors
        vertex = new Vector2();
        vertexTmp = new Vector2();
        position = new Vector2();
        wallColor = new Color();
    }

    public void create(Vector2 position, Vector2[] groundVertices, Vector2[] roofVertices, String flags)
    {
        // Create the game object
        super.create(position, 0, 0, 0f);
        // Create arrays
        this.groundVertices = new float[VERTICES_COUNT];
        this.roofVertices = new float[VERTICES_COUNT];
        this.groundLayerVertices = new float[VERTICES_COUNT];
        this.roofLayerVertices = new float[VERTICES_COUNT];
        // Create the body
        createBody(groundVertices, roofVertices, flags);

    }

    private void createBody(Vector2[] groundVertices, Vector2[] roofVertices, String flags)
    {
        // Validate the vertices arrays
        if(groundVertices == null || groundVertices.length == 0 || roofVertices == null || roofVertices.length == 0)
            return;

        // Create the ground chain shape instance
        ChainShape groundChainShape = new ChainShape();
        groundChainShape.createChain(groundVertices);
        // Create the body
        createChainBody(groundChainShape, flags.concat("Ground"));
        // Create the roof chain shape
        ChainShape roofChainShape = new ChainShape();
        roofChainShape.createChain(roofVertices);
        // Create the body
        createChainBody(roofChainShape, flags.concat("Roof"));
        // Destroy the shapes
        groundChainShape.dispose();
        roofChainShape.dispose();
    }

    /*
        Fill a one point vertices
     */
    private void fillPointVertices(float[] vertices, int index, Color color, Vector2 screenVertex, float textureU, float textureV)
    {
        // Set the point vertices
        vertices[index] = screenVertex.x;
        vertices[index + 1] = screenVertex.y;
        vertices[index + 2] = color.toFloatBits();
        vertices[index + 3] = textureU;
        vertices[index + 4] = textureV;
    }

    /*
        Fills a single point in the vertices starting from the index
    */
    private void fillWorldPointVertices(float[] vertices, int index, Color color, Vector2 vertex, float textureV)
    {
        // Set the position
        position.set(getPosition()).add(vertex);
        // Get the screen vertices
        Vector2 screenVertex = Camera.getInstance().worldToScreen(position);
        // Fill the point
        fillPointVertices(vertices, index * 5, color, screenVertex, vertex.x / 2f, textureV);
    }

    private void prepareGroundRendering()
    {
        // Loop through all the ground fixtures
        for(Fixture fixture : getBody().getFixtureList())
        {
            // Ensure its a ground fixture
            if(!((String)fixture.getUserData()).contains("Ground"))
                continue;

            // Get the chain shape
            ChainShape shape = (ChainShape)fixture.getShape();
            // Get the starting index
            int startingIndex = ((String)fixture.getUserData()).contains("First") ? 1 : 0;
            // Get the very first point
            shape.getVertex(startingIndex, vertex);
            // Fill the first vertex
            if(!((String)fixture.getUserData()).contains("Unrelated"))
            {
                // Fill the layer point
                fillWorldPointVertices(groundLayerVertices, 0, getLayerColor(), vertex.sub(0f, LAYER_SIZE), 1f);
                // Fill the point
                fillWorldPointVertices(groundVertices, 0, getColor(), vertex.set(vertex.x, 0), 1f);
            }
            else
            {
                // Fill the point
                fillWorldPointVertices(groundVertices, 0, getColor(), vertex, 1f);
                // Get the vertices
                shape.getVertex(startingIndex, vertex);
                shape.getVertex(startingIndex + 1, vertexTmp);
                // Set the target point
                if(vertex.y < vertexTmp.y)
                    vertex.set(vertex.x + LAYER_SIZE * 1.5f, vertex.y);
                else
                    vertex.set(vertex.x, vertex.y - LAYER_SIZE);

                // Fill the layer point
                fillWorldPointVertices(groundLayerVertices, 0, getLayerColor(), vertex, 1f);
            }
            // Get the point
            shape.getVertex(startingIndex++, vertex);
            // Fill point
            fillWorldPointVertices(groundVertices, 1, getColor(), vertex, 0f);
            // Fill the layer point
            fillWorldPointVertices(groundLayerVertices, 1, getLayerColor(), vertex, 0f);
            // Get the point
            shape.getVertex(startingIndex, vertex);
            // Fill point
            fillWorldPointVertices(groundVertices, 2, getColor(), vertex, 0f);
            // Fill the layer point
            fillWorldPointVertices(groundLayerVertices, 2, getLayerColor(), vertex, 0f);
            // Check for the unrelated ground flag
            if(((String)fixture.getUserData()).contains("Unrelated"))
            {
                // Get the vertices
                shape.getVertex(startingIndex - 1, vertex);
                shape.getVertex(startingIndex, vertexTmp);
                // Set the target point
                if(vertex.y < vertexTmp.y)
                    vertex.set(vertexTmp.x, vertex.y);
                else
                    vertex.set(vertex.x, vertexTmp.y);

                // Fill the point
                fillWorldPointVertices(groundVertices, 3, getColor(), vertex, 1f);
                // Get the vertices
                shape.getVertex(startingIndex - 1, vertex);
                shape.getVertex(startingIndex, vertexTmp);
                // Set the target point
                if(vertex.y < vertexTmp.y)
                    vertex.set(vertexTmp.x, vertexTmp.y - LAYER_SIZE);
                else
                    vertex.set(vertexTmp.x - LAYER_SIZE * 1.5f, vertexTmp.y);

                // Fill the layer point
                fillWorldPointVertices(groundLayerVertices, 3, getLayerColor(), vertex, 1f);
            }
            else
            {
                // Fill the layer point
                fillWorldPointVertices(groundLayerVertices, 3, getLayerColor(), vertex.sub(0f, LAYER_SIZE), 1f);
                // Fill the last point
                fillWorldPointVertices(groundVertices, 3, getColor(), vertex.set(vertex.x, 0f), 1f);
            }
        }
    }

    private void prepareRoofRendering()
    {
        // Loop through all the roof fixtures
        for(Fixture fixture : getBody().getFixtureList())
        {
            // Ensure its a ground fixture
            if(!((String)fixture.getUserData()).contains("Roof"))
                continue;

            // Get the chain shape
            ChainShape shape = (ChainShape)fixture.getShape();
            // Get the starting index
            int startingIndex = ((String)fixture.getUserData()).contains("First") ? 1 : 0;
            // Get the very first point
            shape.getVertex(startingIndex, vertex);
            // Fill the first vertex
            if(!((String)fixture.getUserData()).contains("Unrelated"))
            {
                // Fill the layer point
                fillWorldPointVertices(roofLayerVertices, 0, getLayerColor(), vertex.add(0f, LAYER_SIZE), 1f);
                // Fill the point
                fillWorldPointVertices(roofVertices, 0, getColor(), vertex.set(vertex.x, Core.getInstance().getGraphicsManager().HEIGHT), 1f);
            }
            else
            {
                // Fill the point
                fillWorldPointVertices(roofVertices, 0, getColor(), vertex, 1f);
                // Get the vertices
                shape.getVertex(startingIndex, vertex);
                shape.getVertex(startingIndex + 1, vertexTmp);
                // Set the target point
                if(vertex.y > vertexTmp.y)
                    vertex.set(vertex.x + LAYER_SIZE * 1.5f, vertex.y);
                else
                    vertex.set(vertex.x, vertex.y + LAYER_SIZE);

                // Fill the layer point
                fillWorldPointVertices(roofLayerVertices, 0, getLayerColor(), vertex, 1f);
            }
            // Get the point
            shape.getVertex(startingIndex++, vertex);
            // Fill point
            fillWorldPointVertices(roofVertices, 1, getColor(), vertex, 0f);
            // Fill the layer point
            fillWorldPointVertices(roofLayerVertices, 1, getLayerColor(), vertex, 0f);
            // Get the point
            shape.getVertex(startingIndex, vertex);
            // Fill point
            fillWorldPointVertices(roofVertices, 2, getColor(), vertex, 0f);
            // Fill the layer point
            fillWorldPointVertices(roofLayerVertices, 2, getLayerColor(), vertex, 0f);
            // Check for the unrelated ground flag
            if(((String)fixture.getUserData()).contains("Unrelated"))
            {
                // Get the vertices
                shape.getVertex(startingIndex - 1, vertex);
                shape.getVertex(startingIndex, vertexTmp);
                // Set the target point
                if(vertex.y > vertexTmp.y)
                    vertex.set(vertexTmp.x, vertex.y);
                else
                    vertex.set(vertex.x, vertexTmp.y);

                // Fill the point
                fillWorldPointVertices(roofVertices, 3, getColor(), vertex, 1f);
                // Get the vertices
                shape.getVertex(startingIndex - 1, vertex);
                shape.getVertex(startingIndex, vertexTmp);
                // Set the target point
                if(vertex.y > vertexTmp.y)
                    vertex.set(vertexTmp.x, vertexTmp.y + LAYER_SIZE);
                else
                    vertex.set(vertexTmp.x - LAYER_SIZE * 1.5f, vertexTmp.y);

                // Fill the layer point
                fillWorldPointVertices(roofLayerVertices, 3, getLayerColor(), vertex, 1f);
            }
            else
            {
                // Fill the layer point
                fillWorldPointVertices(roofLayerVertices, 3, getLayerColor(), vertex.add(0f, LAYER_SIZE), 1f);
                // Fill the last point
                fillWorldPointVertices(roofVertices, 3, getColor(), vertex.set(vertex.x, Core.getInstance().getGraphicsManager().HEIGHT), 1f);
            }
        }
    }

    @Override
    public void process()
    {
        // Process the object
        super.process();
        // Prepare ground rendering
        prepareGroundRendering();
        // Prepare roof rendering
        prepareRoofRendering();
    }

    @Override
    public void render()
    {
        // Draw the ground
        Core.getInstance().getGraphicsManager().drawTexture(texture, groundVertices, VERTICES_COUNT);
        // Draw the roof
        Core.getInstance().getGraphicsManager().drawTexture(texture, roofVertices, VERTICES_COUNT);
        // Draw layers
        Core.getInstance().getGraphicsManager().drawTexture(layerTexture, groundLayerVertices, VERTICES_COUNT);
        Core.getInstance().getGraphicsManager().drawTexture(layerTexture, roofLayerVertices, VERTICES_COUNT);
    }

    @Override
    public Color getColor()
    {
        wallColor.set(Terrain.getInstance().getEnvironmentColor());
        wallColor.a = 0.6f;
        return wallColor;
    }

    public Color getLayerColor()
    {
        wallColor.set(Terrain.getInstance().getEntitiesColor());
        wallColor.a = 0.9f;
        return wallColor;
    }
}
