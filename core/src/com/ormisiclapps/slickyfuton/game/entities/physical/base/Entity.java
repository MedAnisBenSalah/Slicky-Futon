package com.ormisiclapps.slickyfuton.game.entities.physical.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.nodes.entity.PartedModelNode;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.game.world.Lightening;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.utility.GameMath;
import com.ormisiclapps.slickyfuton.enumerations.EntityType;
import com.ormisiclapps.slickyfuton.game.entities.utility.Model;
import com.ormisiclapps.slickyfuton.game.nodes.entity.ModelNode;

import box2dLight.PointLight;

/**
 * Created by OrMisicL on 9/18/2015.
 *
 */
public abstract class Entity
{
    private Model model;
    private EntityType type;
    private String modelName;
    private Body body;
    private Vector2 size;
    private float radius;
    private Color color;
    protected Vector2 screenPosition;
    protected Vector2 screenSize;
    public PointLight light;
    private boolean circleShape;
    private boolean shouldRender;
    private Vector2 tmpVector;
    protected boolean parted;
    private Body[] partedBodies;
    private PointLight[] partedLights;
    private Vector2[] partedRenderingSize;
    private boolean[] partedShouldRender;
    private Vector2 partedCenterPosition;
    private float partedRotation;
    protected float partedLowestPoint, partedHighestPoint;
    private Vector2 renderingSize;
    private boolean nextFramePosition;
    private Vector2 entityPosition;

    public Entity(String name, EntityType type)
    {
        // Save the entity type
        this.type = type;
        // Create vectors
        size = new Vector2();
        screenPosition = new Vector2();
        screenSize = new Vector2();
        tmpVector = new Vector2();
        renderingSize = new Vector2();
        entityPosition = new Vector2();
        // Save the model name
        modelName = name;
        // Reset instances
        model = null;
        body = null;
        light = null;
        color = null;
        partedBodies = null;
        partedLights = null;
        partedShouldRender = null;
        partedRenderingSize = null;
        // Reset flags
        circleShape = false;
        shouldRender = true;
        parted = false;
        nextFramePosition = false;
        // Reset values
        radius = 0f;
        partedLowestPoint = 0f;
        partedHighestPoint = 0f;
    }

    public void create(Vector2 size)
    {
        // Create the model instance
        model = new Model(modelName);
        // Load the model
        model.load();
        // Set the entity's color
        color = model.getNode().color.cpy();
        // Create the body
        createBody(size == null ? model.getNode().size : size);
    }

    public void create()
    {
       create(null);
    }

    private void createBody(Vector2 size)
    {
        // Get the model node
        ModelNode node = model.getNode();
        // Save the entity's size
        this.size.set(size);
        // Save the entity's radius
        radius = node.radius;
        // Create the body definition instance
        BodyDef bodyDef = new BodyDef();
        bodyDef.allowSleep = true;
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(size.x / 2, size.y / 2);
        // Create the body instance
        body = GameWorld.getInstance().addToWorld(bodyDef);
        // Reset velocity
        body.setLinearVelocity(0f, 0f);
        // Is this body part a polygon shape ?
        if(node.shape.equals("Polygon"))
        {
            // Load the body if its not already loaded
            PolygonShape[] shapes = Core.getInstance().getModelManager().loadBody(node, modelName);
            // Loop through all the shapes
            for(PolygonShape shape : shapes)
            {
                // Create the fixture definition
                FixtureDef fixtureDef = new FixtureDef();
                // Set the body physics
                fixtureDef.shape = shape;
                fixtureDef.density = 1f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 0f;
                // Set the collision filters
                fixtureDef.filter.categoryBits = this instanceof Player ? Configuration.PLAYER_CATEGORY_BITS : Configuration.OBJECTS_CATEGORY_BITS;
                fixtureDef.filter.maskBits = this instanceof Player ? (short)(Configuration.WALLS_CATEGORY_BITS | Configuration.OBJECTS_CATEGORY_BITS) :
                        Configuration.PLAYER_CATEGORY_BITS;

                // Create the fixture
                body.createFixture(fixtureDef);
            }
        }
        // Rectangle shape
        else if(node.shape.equals("Rectangle"))
        {
            // Load the body part
            PolygonShape shape = new PolygonShape();
            // Set the polygon shape
            shape.setAsBox(size.x / 2, size.y / 2);
             // Create the fixture definition
            FixtureDef fixtureDef = new FixtureDef();
            // Set the body physics
            fixtureDef.shape = shape;
            fixtureDef.density = 1f;
            fixtureDef.friction = 0f;
            fixtureDef.restitution = 0f;
            // Set the collision filters
            fixtureDef.filter.categoryBits = this instanceof Player ? Configuration.PLAYER_CATEGORY_BITS : Configuration.OBJECTS_CATEGORY_BITS;
            fixtureDef.filter.maskBits = this instanceof Player ? (short)(Configuration.WALLS_CATEGORY_BITS | Configuration.OBJECTS_CATEGORY_BITS) :
                    Configuration.PLAYER_CATEGORY_BITS;

            // Create the fixture
            body.createFixture(fixtureDef);
        }
        else if(node.shape.equals("Circle"))
        {
            // Create the circle shape instance
            CircleShape circleShape = new CircleShape();
            // Set the shape radius
            circleShape.setRadius(radius);
            // Create the fixture definition
            FixtureDef fixtureDef = new FixtureDef();
            // Set the body physics
            fixtureDef.shape = circleShape;
            fixtureDef.density = 1f;
            fixtureDef.friction = 0f;
            fixtureDef.restitution = 0f;
            // Set the collision filters
            fixtureDef.filter.categoryBits = this instanceof Player ? Configuration.PLAYER_CATEGORY_BITS : Configuration.OBJECTS_CATEGORY_BITS;
            fixtureDef.filter.maskBits = this instanceof Player ? (short)(Configuration.WALLS_CATEGORY_BITS | Configuration.OBJECTS_CATEGORY_BITS) :
                    Configuration.PLAYER_CATEGORY_BITS;

            // Create the fixture
            body.createFixture(fixtureDef);
            // Set the body's size
            this.size.set(radius * 2, radius * 2);
            // Mark as a circle shape
            this.circleShape = true;
        }
        else if(node.shape.equals("PolygonParted"))
        {
            // Load the body if its not already loaded
            PolygonShape[] shapes = Core.getInstance().getModelManager().loadBody(node, modelName);
            // Get the parts count
            int parts = ((PartedModelNode)getModel().getNode()).positions.length;
            // Create the bodies array
            partedBodies = new Body[parts];
            // Create the lights array (if necessary)
            if(model.getNode().lightDistance != 0)
                partedLights = new PointLight[parts];

            // Create the should render array
            partedShouldRender = new boolean[parts];
            // Create the rendering size array
            partedRenderingSize = new Vector2[parts];
            // Create the tmp body
            Body tmpBody = body;
            // Loop through all the parts
            for(int i = 0; i < parts; i++)
            {
                // Set the parted points
                Vector2 partPosition = ((PartedModelNode)getModel().getNode()).positions[i];
                if(partPosition.x < partedLowestPoint)
                    partedLowestPoint = partPosition.x;
                else if(partPosition.x > partedHighestPoint)
                    partedHighestPoint = partPosition.x;

                // Create the rendering size vector
                partedRenderingSize[i] = new Vector2();
                // Loop through all the shapes
                for(PolygonShape shape : shapes)
                {
                    // Create the fixture definition
                    FixtureDef fixtureDef = new FixtureDef();
                    // Set the body physics
                    fixtureDef.shape = shape;
                    fixtureDef.density = 1f;
                    fixtureDef.friction = 0f;
                    fixtureDef.restitution = 0f;
                    // Set the collision filters
                    fixtureDef.filter.categoryBits = Configuration.OBJECTS_CATEGORY_BITS;
                    fixtureDef.filter.maskBits = Configuration.PLAYER_CATEGORY_BITS;
                    // Create the fixture
                    tmpBody.createFixture(fixtureDef);
                }
                // Set the current body
                partedBodies[i] = tmpBody;
                // Set their rotation
                partedBodies[i].setTransform(partedBodies[i].getPosition(),
                        GameMath.degreesToRadians(((PartedModelNode)getModel().getNode()).rotations[i]));

                // Set the next body position
                bodyDef.position.set(size.x / 2, size.y / 2);
                // Create the body instance
                tmpBody = GameWorld.getInstance().addToWorld(bodyDef);
                // Create the parted light
                if(model.getNode().lightDistance != 0)
                    createLight(partedBodies[i],
                        partedLights[i] = new PointLight(Lightening.getInstance().getHandler(), Configuration.ENTITY_RAYS_NUMBER));
            }
            // Set the parted flag
            parted = true;
            // Create the parted center position
            partedCenterPosition = new Vector2();
            // Reset the parted rotation
            partedRotation = 0f;
        }
        // Create the light
        if(!parted && model.getNode().lightDistance != 0)
            createLight(body, light = new PointLight(Lightening.getInstance().getHandler(), Configuration.ENTITY_RAYS_NUMBER));
    }

    private void createLight(Body body, PointLight light)
    {
        // Attach it to the player
        light.attachToBody(body);
        // Setup
        light.setColor(model.getNode().lightColor);
        light.setDistance(model.getNode().lightDistance);
        light.setSoft(true);
        light.setActive(true);
        //light.setContactFilter((short)0x1, (short)0x0, (short)0x0);
    }

    public void createChainBody(ChainShape chainShape, String name)
    {
        // Validate the chain shape and entity's shape
        if(!getModel().getNode().shape.equals("Chain") || chainShape == null)
            return;

        // Create the fixture definition
        FixtureDef fixtureDef = new FixtureDef();
        // Set the body physics
        fixtureDef.shape = chainShape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        // Set the collision filters
        fixtureDef.filter.categoryBits = Configuration.OBJECTS_CATEGORY_BITS;
        fixtureDef.filter.maskBits = Configuration.PLAYER_CATEGORY_BITS;
        // Create the fixture
        body.createFixture(fixtureDef).setUserData(name);
    }
    
    public void destroy()
    {
        // Destroy the light
        if(!parted)
        {
            if(light != null)
                light.remove();
        }
        else
        {
            if(partedLights != null)
            {
                for (PointLight light : partedLights)
                    light.remove();
            }
        }

        // Destroy the body
        if(!parted)
            GameWorld.getInstance().removeFromWorld(body);
        else
        {
            // Destroy all the parted bodies
            for(Body partedBody : partedBodies)
                GameWorld.getInstance().removeFromWorld(partedBody);
        }
    }

    public void process()
    {
        // Set the position
        if(nextFramePosition)
        {
            // Set the entity's position
            setPosition(entityPosition);
            // Reset the next frame position
            nextFramePosition = false;
        }
        // Calculate the screen size
        Vector2 pixels = GameMath.pixelsPerMeters(size);
        screenSize.set(pixels.y, pixels.x);
        // Calculate the screen position
        screenPosition.set(Camera.getInstance().worldToScreen(getPosition())).sub(screenSize.x / 2, screenSize.y / 2);
        // Check if its not a parted entity
        if(!parted)
        {
            // Get the rendering area
            tmpVector.set(Camera.getInstance().worldToScreen(getPosition()).sub(renderingSize.x / 2, renderingSize.y / 2));
            // Check if we should render
            shouldRender = (tmpVector.x <= Core.getInstance().getGraphicsManager().WIDTH && tmpVector.x + renderingSize.x >= 0 &&
                    tmpVector.y <= Core.getInstance().getGraphicsManager().HEIGHT && tmpVector.y + renderingSize.y >= 0);
        }
        else
        {
            // Loop through the parted bodies
            for(int i = 0; i < partedRenderingSize.length; i++)
            {
                // Get the rendering area
                tmpVector.set(Camera.getInstance().worldToScreen(partedBodies[i].getPosition()).sub(partedRenderingSize[i].x / 2,
                        partedRenderingSize[i].y / 2));

                // Check if we should render
                partedShouldRender[i] = true;//(tmpVector.x <= Core.getInstance().getGraphicsManager().WIDTH && tmpVector.x + partedRenderingSize[i].x >= 0 &&
                        //tmpVector.y <= Core.getInstance().getGraphicsManager().HEIGHT && tmpVector.y + partedRenderingSize[i].y >= 0);
            }
        }
    }

    public void render()
    {
        // Verify if we should render the entity
        if(model != null)
        {
            // If its a parted object
            if(parted)
            {
                // Loop through the parted bodies
                for(int i = 0; i < partedRenderingSize.length; i++)
                {
                    // Skip if we're not rendering the part
                    if(!partedShouldRender[i])
                        continue;

                    // Get the body
                    Body partedBody = partedBodies[i];
                    // Get the body's position
                    Vector2 position = Camera.getInstance().worldToScreen(partedBody.getPosition()).sub(screenSize.x / 2, screenSize.y / 2);
                    // Get the body rotation
                    float rotation = GameMath.radiansToDegrees(partedBody.getAngle());
                    // Set the entity's color
                    Core.getInstance().getGraphicsManager().setColor(color);
                    // Render the entity's texture
                    Core.getInstance().getGraphicsManager().drawTextureRegion(model.getTexture(), position, screenSize,
                            GameMath.adjustAngleInDegrees(rotation + 270f));

                    // Restore the rendering color
                    Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
                }
            }
            else if(body != null && shouldRender && model.isShouldRender())
            {
                // Set the entity's color
                Core.getInstance().getGraphicsManager().setColor(color);
                // Render the entity's texture
                Core.getInstance().getGraphicsManager().drawTextureRegion(model.getTexture(), screenPosition, screenSize,
                        GameMath.adjustAngleInDegrees(getRotationInDegrees() + 270f));

                // Restore the rendering color
                Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
            }
        }
    }

    public void setPosition(Vector2 position)
    {
        // Is the world locked ?
        if(GameWorld.getInstance().isLocked())
        {
            // Set the next frame position flag
            nextFramePosition = true;
            // Set the position
            entityPosition.set(position);
            return;
        }
        // Set the body's position if its parted
        if(parted)
        {
            // Save the parted center position
            partedCenterPosition.set(position);
            // Get the parts count
            int parts = ((PartedModelNode)getModel().getNode()).positions.length;
            // Loop through all the parts
            for(int i = 0; i < parts; i++)
            {
                // Get the part position
                tmpVector.set(((PartedModelNode)getModel().getNode()).positions[i]);
                // Calculate the part position relative to the center
                tmpVector.add(position);
                // Set the part's position
                partedBodies[i].setTransform(tmpVector, GameMath.degreesToRadians(((PartedModelNode)getModel().getNode()).rotations[i]));
            }
        }
        else if(body != null)
            body.setTransform(position, body.getAngle());
    }

    public Vector2 getPosition()
    {
        if(parted)
            return partedCenterPosition;
        else
            return body != null ? tmpVector.set(body.getPosition()) : tmpVector.set(0f, 0f);
    }

    /*
        This is used to adjust the entity's rendering size according to its rotation (used to check for rendering)
     */
    private void adjustRenderingSize()
    {
        // If its not a parted entity
        if(!parted)
        {
            // Get the entity rotation
            float rotation = getRotationInRadians();
            // Calculate the new size
            float x = Math.abs(size.x * MathUtils.cos(rotation)) + Math.abs(size.y * MathUtils.sin(rotation));
            float y = Math.abs(size.x * MathUtils.sin(rotation)) + Math.abs(size.y * MathUtils.cos(rotation));
            // Adjust the entity's size to match the rotation
            renderingSize.set(GameMath.pixelsPerMeters(x, y));
        }
        else
        {
            // Loop through the parted bodies
            for(int i = 0; i < partedShouldRender.length; i++)
            {
                // Get the body rotation
                float rotation = partedBodies[i].getAngle();
                // Calculate the new size
                float x = Math.abs(size.x * MathUtils.cos(rotation)) + Math.abs(size.y * MathUtils.sin(rotation));
                float y = Math.abs(size.x * MathUtils.sin(rotation)) + Math.abs(size.y * MathUtils.cos(rotation));
                // Adjust the entity's size to match the rotation
                partedRenderingSize[i].set(GameMath.pixelsPerMeters(x, y));
            }
        }
    }

    public void setRotationInRadians(float rotation)
    {
        // Set the body's rotation if its parted
        if(parted)
        {
            // Loop through all the parted bodies
            for(Body partedBody : partedBodies)
            {
                // Get the rotation speed
                float rotationSpeed = rotation - partedRotation;
                // Get the body's center
                Vector2 worldCenter = partedBody.getPosition();
                // Translate the body back to its origin
                tmpVector.set(worldCenter).sub(partedCenterPosition);
                // Rotate the body and compute its rotation position
                tmpVector.set(tmpVector.x * MathUtils.cos(rotationSpeed) - tmpVector.y * MathUtils.sin(rotationSpeed),
                        tmpVector.x * MathUtils.sin(rotationSpeed) + tmpVector.y * MathUtils.cos(rotationSpeed));

                // Translate the point back
                partedBody.setTransform(tmpVector.x + partedCenterPosition.x, tmpVector.y + partedCenterPosition.y,
                        GameMath.adjustAngleInRadians(partedBody.getAngle() + rotationSpeed));
            }
            // Set the parted rotation
            partedRotation = rotation;
        }
        else if(body != null)
            body.setTransform(getPosition(), rotation);

        // Adjust the rendering size
        adjustRenderingSize();
    }

    public void setRotationInDegrees(float rotation)
    {
        // Set the entity's rotation
        setRotationInRadians(GameMath.degreesToRadians(rotation));
    }

    public float getRotationInRadians()
    {
        if(parted)
            return partedRotation;
        else
            return body != null ? body.getAngle() : 0.0f;
    }

    public float getRotationInDegrees()
    {
        return GameMath.radiansToDegrees(getRotationInRadians());
    }

    public void setVelocity(Vector2 velocity)
    {
        // Set the entity's velocity
        if(body != null)
            body.setLinearVelocity(velocity);
    }

    public void setBulletBody(boolean toggle)
    {
        // Set the entity's bullet body
        if(body != null)
            body.setBullet(toggle);
    }

    public void setFixedRotation(boolean toggle)
    {
        // Set the entity's bullet body
        if(body != null)
            body.setFixedRotation(toggle);
    }

    public void resize(Vector2 newSize, float newRadius)
    {
        // Skip parted bodies
        if(parted)
            return;

        // Create a body def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getPosition());
        // Create a new body instance
        Body newBody = GameWorld.getInstance().addToWorld(bodyDef);
        // If its a circle shape
        if(circleShape)
        {
            // Create the circle shape
            CircleShape shape = new CircleShape();
            shape.setRadius(newRadius);
            // Create fixture def
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = 1;
            fixtureDef.friction = 0;
            fixtureDef.restitution = 0;
            // Create a fixture def
            newBody.createFixture(fixtureDef);
            // Destroy the shape
            shape.dispose();
        }
        else
        {
            // Loop through the entity fixtures
            for (Fixture fixture : body.getFixtureList())
            {
                // Get the shape
                PolygonShape shape = (PolygonShape) fixture.getShape();
                // Get the fixtures vertices
                Vector2[] fixtureVertices = new Vector2[shape.getVertexCount()];
                for (int i = 0; i < shape.getVertexCount(); i++)
                {
                    fixtureVertices[i] = new Vector2();
                    shape.getVertex(i, fixtureVertices[i]);
                }
                // Resize the vertices
                GameMath.resizeVectorsArray(fixtureVertices, size, newSize);
                // Create the new shape
                PolygonShape newShape = new PolygonShape();
                newShape.set(fixtureVertices);
                // Create fixture def
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = newShape;
                fixtureDef.density = 1f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 0f;
                // Create a fixture def
                newBody.createFixture(fixtureDef);
                // Destroy the shape
                newShape.dispose();
            }
        }
        // Remove the old body
        GameWorld.getInstance().removeFromWorld(body);
        // Set the body to use the new body instance
        body = newBody;
        // Attach the light to the new body
        if(light != null)
            light.attachToBody(body);

        // Set the new size
        if(circleShape)
        {
            // Set the radius
            radius = newRadius;
            // Set the size
            size.set(radius * 2, radius * 2);
        }
        else
            // Set the size to match the new size
            size.set(newSize);

        // Adjust the rendering size
        adjustRenderingSize();
    }

    public void flip()
    {
        // Can't flip circular and parted objects
        if(circleShape || parted)
            return;

        // Create a body def
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getPosition());
        // Create a new body instance
        Body newBody = GameWorld.getInstance().addToWorld(bodyDef);
        // Create the shapes array
        PolygonShape[] shapes = new PolygonShape[body.getFixtureList().size];
        int count = 0;
        // Create vertices array
        Array<Vector2> vertices = new Array(Vector2.class);
        // Loop through the entity fixtures
        for(Fixture fixture : body.getFixtureList())
        {
            // Get the shape
            PolygonShape shape = (PolygonShape)fixture.getShape();
            // Get the fixtures vertices
            Vector2[] fixtureVertices = new Vector2[shape.getVertexCount()];
            for(int i = 0; i < shape.getVertexCount(); i++)
            {
                fixtureVertices[i] = new Vector2();
                shape.getVertex(i, fixtureVertices[i]);
            }
            // Add it to the vertices array
            vertices.addAll(fixtureVertices);
        }
        // Calculate their center
        Vector2 center = GameMath.getVectorsArrayCenter(vertices.toArray());
        // Loop through the entity fixtures
        for(Fixture fixture : body.getFixtureList())
        {
            // Get the shape
            PolygonShape shape = (PolygonShape)fixture.getShape();
            // Get the fixtures vertices
            Vector2[] fixtureVertices = new Vector2[shape.getVertexCount()];
            for(int i = 0; i < shape.getVertexCount(); i++)
            {
                fixtureVertices[i] = new Vector2();
                shape.getVertex(i, fixtureVertices[i]);
            }
            // Flip the vertices
            fixtureVertices = GameMath.flipVerticesArray(fixtureVertices, center, new Vector2(1, 0));
            // Create the new shape
            PolygonShape newShape = new PolygonShape();
            newShape.set(fixtureVertices);
            // Add it to the shapes array
            shapes[count++] = newShape;
        }
        // Loop through all the shapes
        for(int i = 0; i < count; i++)
        {
            // Create fixture def
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shapes[i];
            fixtureDef.density = 1;
            fixtureDef.friction = 0;
            fixtureDef.restitution = 0;
            // Create a fixture def
            newBody.createFixture(fixtureDef);
            // Destroy the shape
            shapes[i].dispose();
        }
        // Remove the old body
        GameWorld.getInstance().removeFromWorld(body);
        // Set the body to use the new body instance
        body = newBody;
        // Flip the texture region
        if(model.isShouldRender())
            model.getTexture().flip(true, false);
    }

    public Vector2 getVelocity() { return body.getLinearVelocity(); }

    public Body getBody()
    {
        return body;
    }

    public boolean hasBody(Body body)
    {
        if(!parted)
            return this.body == body;
        else
        {
            for(Body partedBody : partedBodies)
            {
                if(partedBody == body)
                    return true;
            }
            return false;
        }
    }

    public EntityType getType()
    {
        return type;
    }

    public Model getModel()
    {
        return model;
    }

    public Vector2 getSize() { return size; }

    public float getRadius() { return radius; }

    public float getLightDistance() { return light != null ? light.getDistance() : 0f; }

    public void setLightDistance(float distance)
    {
        if(!parted)
        {
            if(light != null)
                light.setDistance(distance);
        }
        else
        {
            if(partedLights != null)
            {
                for (PointLight light : partedLights)
                    light.setDistance(distance);
            }
        }
    }

    public void toggleLights(boolean toggle)
    {
        if(!parted)
        {
            if(light != null)
                light.setActive(toggle);
        }
        else
        {
            if(partedLights != null)
            {
                for (PointLight light : partedLights)
                    light.setActive(toggle);
            }
        }
    }

    public Color getColor() { return color; }

    public void setColor(Color color)
    {
        // Keep the alpha man!!
        float a = this.color.a;
        this.color.set(color);
        this.color.a = a;
    }

    public void setLightColor(Color color)
    {
        // Change the light's color
        if(light != null)
        {
            light.setColor(color.r, color.g, color.b, color.a);
            light.update();
        }
    }

    public void toggleCaveMode(boolean toggle)
    {
        // Set the light's distance accordingly
       // if(light != null)
         //   light.setDistance(toggle ? model.getNode().lightDistance * 0.5f : model.getNode().lightDistance);
    }

    public void setAngularVelocity(float velocity)
    {
        if(!parted)
            body.setAngularVelocity(velocity);
    }

    public Color getLightColor()
    {
        return light != null ? light.getColor() : Color.BLACK;
    }
}
