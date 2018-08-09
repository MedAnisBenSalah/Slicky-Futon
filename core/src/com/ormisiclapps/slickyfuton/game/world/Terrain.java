package com.ormisiclapps.slickyfuton.game.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.ormisiclapps.slickyfuton.audio.GameMusic;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.enumerations.ObjectsEntryType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.notphysical.Dot;
import com.ormisiclapps.slickyfuton.game.entities.notphysical.EatableObject;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.ChainGameObject;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.MovableGameObject;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.ChainsawStick;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.ChainsawTriangle;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.Cube;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.Flame;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.Inflatable;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.Laser;
import com.ormisiclapps.slickyfuton.game.nodes.combination.*;
import com.ormisiclapps.slickyfuton.game.nodes.entity.ModelNode;
import com.ormisiclapps.slickyfuton.game.nodes.level.*;
import com.ormisiclapps.slickyfuton.game.screens.GameScreen;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ObjectType;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.entities.notphysical.TerrainObject;
import com.ormisiclapps.slickyfuton.utility.GameMath;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by OrMisicL on 9/29/2015.
 *
 * A terrain's part consists of two fixtures (ground and roof), the part's length is STREAMING_DISTANCE constant
 *
 * At any given time, only two parts of the terrain are present in the physics engine (4 fixtures: 2 ground + 2 roof)
 *
 * The internal streamer updates itself at some point where the player reaches a certain distance from the part's end
 * it creates the next part and destroys the unused one (the part before the player's current one)
 *
 * The terrain renders each part on two steps (ground and roof), each fixture will use 2 triangles and renders itself as a rectangle
 *
 * Rendering optimizations works as follows:
 *      If (the parts beginning > screen width) - Skip
 *      If (the parts ending < 0) - Skip
 */
public class Terrain
{
    private Body body;
    private float lastPosition;
    private Array<GameObject> objects;
    private Array<TerrainObject> terrainObjects;
    private Array<String> fixedTerrainObjects;
    private float lastObjectPosition;
    private TextureRegion layerTexture;
    private Texture backgroundTexture, groundTexture, spikesTexture;
    private float[] groundVertices;
    private int groundVerticesIndex;
    private float[] backgroundVertices;
    private int backgroundVerticesIndex;
    private float[] spikesVertices;
    private int spikesVerticesIndex;
    private Vector2 layerPosition;
    private Vector2 roofLayerPosition;
    private Vector2 layerSize, roofLayerSize;
    private boolean isExclusive;
    private GameObject lastExclusiveObject;
    private int exclusivePhases;
    private boolean exclusiveStarted;
    private boolean wasExclusive;
    private boolean caveMode;
    private ObjectType lastObjectType;
    private Vector2 tmpVector;
    private Vector2 tmpVector2;
    private Vector2 tmpVector3;
    private Vector2 tmpVector4;
    private Color groundColor;
    private GameMode incomingGameMode;
    private float changeModePosition;
    private boolean changeMode;
    private boolean wasLethalGround;
    private float roofSize;
    private int changeModeLocked;
    private Color environmentColor, nextEnvironmentColor, oldEnvironmentColor;
    private Color entitiesColor, nextEntitiesColor, oldEntitiesColor;
    private int transitionDuration, entitiesColorTransitDuration;
    private boolean inGameScreen;
    private Array<LevelObjectNode> levelObjectsArray;
    private Array<LevelEventNode> levelEventsArray;
    private boolean levelMode;
    private float spikesAlpha;
    private int levelExclusiveObjects;
    private float levelFlameSpeed;
    private ObjectsEntryType objectsEntryType;
    private float objectsEntrySpeed;

    // Constants
    private static final float STREAMING_DISTANCE = 100f;
    private static final float TEXTURE_WIDTH = 5f; // Will render a texture for every x pixels of the terrain
    private static final float BACKGROUND_TEXTURE_WIDTH = 40f; // Will render a texture for every x pixels of the terrain
    private static final float BACKGROUND_TEXTURE_HEIGHT = 1f;
    private static final int VERTICES_COUNT = 2000;
    private static final int BACKGROUND_VERTICES_COUNT = 20;
    private static final int EXCLUSIVE_PHASES = 5;
    private static final float TERRAIN_OBJECTS_STREAMING = 50f;
    private static final int MINIMUM_TERRAIN_OBJECTS = 5;
    private static final int MINIMUM_GAME_MODE_UPDATES = 4;
    private static final float LEVEL_STREAMING_TIME = 10; // In seconds

    private static Terrain instance;

    public Terrain()
    {
        // Create arrays
        objects = new Array<GameObject>();
        terrainObjects = new Array<TerrainObject>();
        fixedTerrainObjects = new Array<String>();
        // Create the level arrays
        levelEventsArray = new Array<LevelEventNode>();
        levelObjectsArray = new Array<LevelObjectNode>();
        // Create the vertices arrays
        // 5 (vertex elements number) * 4 (fixture rectangle sides) * 2 (fixtures number) * 2 (roof and ground ) = 80
        groundVertices = new float[VERTICES_COUNT];
        backgroundVertices = new float[BACKGROUND_VERTICES_COUNT];
        spikesVertices = new float[BACKGROUND_VERTICES_COUNT * 2];
        // Reset indexes
        groundVerticesIndex = 0;
        backgroundVerticesIndex = 0;
        spikesVerticesIndex = 0;
        lastObjectType = ObjectType.OBJECT_TYPE_STATIC;
        incomingGameMode = GameMode.GAME_MODE_FLYING;
        changeModePosition = 0f;
        roofSize = 0f;
        changeModeLocked = 0;
        spikesAlpha = 0f;
        levelExclusiveObjects = 0;
        levelFlameSpeed = 0f;
        objectsEntrySpeed = 0f;
        // Reset instances
        body = null;
        lastExclusiveObject = null;
        environmentColor = null;
        nextEnvironmentColor = null;
        oldEnvironmentColor = null;
        entitiesColor = null;
        nextEntitiesColor = null;
        oldEntitiesColor = null;
        // Reset flags
        wasExclusive = false;
        exclusiveStarted = false;
        caveMode = false;
        changeMode = false;
        inGameScreen = false;
        levelMode = false;
        wasLethalGround = false;
        // Reset exclusive phases
        exclusivePhases = 0;
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        tmpVector3 = new Vector2();
        tmpVector4 = new Vector2();
        layerPosition = new Vector2();
        layerSize = new Vector2();
        roofLayerSize = new Vector2();
        roofLayerPosition = new Vector2();
        // Create ground color instance
        groundColor = new Color();
        // Load the texture
        groundTexture = Core.getInstance().getResourcesManager().getResource("Terrain/Ground", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Set the ground texture's wrap
        groundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        // Get the terrain's textures
        layerTexture = Core.getInstance().getResourcesManager().getResource("Terrain/GroundLayer", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        backgroundTexture = Core.getInstance().getResourcesManager().getResource("Terrain/Background", ResourceType.RESOURCE_TYPE_TEXTURE);
        spikesTexture = Core.getInstance().getResourcesManager().getResource("Terrain/Spikes", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Set the texture's properties
        //layerTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        spikesTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        spikesTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Set the instance
        instance = this;
    }

    public void initialize()
    {
        // Are we in game screen
        inGameScreen = Core.getInstance().getScreensManager().getCurrentScreen() instanceof GameScreen ||
                Core.getInstance().getScreensManager().getTransitionScreen() instanceof GameScreen;

        // Set level mode
        levelMode = inGameScreen && !GameLogic.getInstance().isEndless();
        // Reset values
        lastPosition = 0;
        lastObjectPosition = Configuration.START_POSITION + STREAMING_DISTANCE / 2;
        changeModeLocked = 0;
        levelExclusiveObjects = 0;
        levelFlameSpeed = 0f;
        // Initialize the entry speed
        objectsEntrySpeed = Configuration.OBJECTS_ENTRY_SPEED / 2f;
        // Initialize the objects entry type
        objectsEntryType = GameIntelligence.getInstance().updateObjectsEntryType(ObjectsEntryType.OBJECTS_ENTRY_NORMAL);
        // Clean terrain objects
        terrainObjects.clear();
        fixedTerrainObjects.clear();
        // Clear level arrays
        levelEventsArray.clear();
        levelObjectsArray.clear();
        // Reset values
        wasLethalGround = false;
        // Disable lethal ground
        GameLogic.getInstance().toggleLethalGround(false);
        // Loop through all the model's count
        for(int i = 0; i < Core.getInstance().getModelSettings().getModelsCount(); i++)
        {
            // Get the model
            String model = Core.getInstance().getModelSettings().getModel(i);
            // Add it to the fixed terrain objects if necessary
            if(Core.getInstance().getModelSettings().getModelAttribute(model, "type").equals("TerrainGroundObject") &&
                    Core.getInstance().getModelSettings().doesModelAttributeExist(model, "fixedPosition"))
                fixedTerrainObjects.add(model);
        }
        // Create the terrain body def
        BodyDef bodyDef = new BodyDef();
        // Setup the body def
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Create the terrain body
        body = GameWorld.getInstance().addToWorld(bodyDef);
        // Reset floats
        changeModePosition = 0f;
        // Reset flags
        changeMode = false;
        // Reset incoming game mode
        incomingGameMode = GameMode.GAME_MODE_FLYING;
        // Create the initial terrain
        createTerrain(Player.NOT_STARTED_POSITION * 1.2f, 0);
        createTerrain(0, STREAMING_DISTANCE);
        // Set the layer size vector
        layerSize.set(GameMath.pixelsPerMeters(0f, 0.100f).y, Core.getInstance().getGraphicsManager().WIDTH);
        roofLayerSize.set(Core.getInstance().getGraphicsManager().WIDTH, GameMath.pixelsPerMeters(0f, 0.125f).y);
        // Setup the level mode
        if(levelMode)
            setupLevel();
    }

    private void update()
    {
        // Update roof size
        if(roofSize == 0f)
            roofSize = Core.getInstance().getGraphicsManager().HEIGHT -
                    Camera.getInstance().worldToScreen(new Vector2(0f, Configuration.TERRAIN_ROOF_POSITION)).y;

        // Check if we need to generate a new terrain
        if(lastPosition - Player.getInstance().getPosition().x <= STREAMING_DISTANCE / 2)
        {
            // Create new terrain
            createTerrain(lastPosition, lastPosition + STREAMING_DISTANCE);
            // Clean the terrain
            cleanTerrain();
            // Update the game intelligence
            GameIntelligence.getInstance().update();
            // Update the objects entry type
            if(!levelMode && GameIntelligence.getInstance().isJustUpdatedColors())
                objectsEntryType = GameIntelligence.getInstance().updateObjectsEntryType(objectsEntryType);
        }
        // Set the environment color if necessary
        if(GameLogic.getInstance().isEndless() || !(Core.getInstance().getScreensManager().getCurrentScreen() instanceof GameScreen))
            environmentColor = GameIntelligence.getInstance().getEnvironmentColor();

        // Set the object's entry speed
        if(!levelMode)
            objectsEntrySpeed = GameIntelligence.getInstance().getObjectsEntrySpeed();
    }

    private void cleanTerrain()
    {
        // Destroy the first two fixtures
        body.destroyFixture(body.getFixtureList().first());
        body.destroyFixture(body.getFixtureList().first());
    }

    private void setupLevel()
    {
        // Set the environment and entities color
        LevelNode levelNode = GameLogic.getInstance().getCurrentLevelNode();
        environmentColor = new Color(levelNode.r, levelNode.g, levelNode.b, levelNode.a);
        entitiesColor = new Color(levelNode.entitiesColorR, levelNode.entitiesColorG, levelNode.entitiesColorB,
                levelNode.entitiesColorA);

        // Set player's velocity
        Player.getInstance().setVerticalVelocity(levelNode.playerSpeed);
        // Set objects entry animation
        objectsEntryType = levelNode.objectsEntryType;
        objectsEntrySpeed = levelNode.objectsEntrySpeed;
        // Create all the level objects
        for(int i = 0; i < GameLogic.getInstance().getCurrentLevelNode().objectNodes.length; i++)
        {
            // Get the object node
            LevelObjectNode objectNode = GameLogic.getInstance().getCurrentLevelNode().objectNodes[i];
            // Add it to the object positions array
            levelObjectsArray.add(objectNode);
        }
        // Sort objects by time
        levelObjectsArray.sort(new Comparator<LevelObjectNode>()
        {
            @Override
            public int compare(LevelObjectNode o1, LevelObjectNode o2)
            {
                if(o1.positionX < o2.positionX)
                    return -1;
                else if(o1.positionX == o2.positionX)
                    return 0;
                else
                    return 1;
            }
        });
        // Setup all the level events
        for(int i = 0; i < GameLogic.getInstance().getCurrentLevelNode().eventNodes.length; i++)
        {
            // Get the event node
            LevelEventNode node = GameLogic.getInstance().getCurrentLevelNode().eventNodes[i];
            // Add the event to the array
            levelEventsArray.add(node);
        }
        // Sort events by time
        levelEventsArray.sort(new Comparator<LevelEventNode>()
        {
            @Override
            public int compare(LevelEventNode o1, LevelEventNode o2)
            {
                if(o1.position < o2.position)
                    return -1;
                else if(o1.position == o2.position)
                    return 0;
                else
                    return 1;
            }
        });
    }

    private void cleanObjects()
    {
        // Get the screen width in world coordinates
        float screenWidth = GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH);
        // Loop through all the objects
        Iterator<GameObject> iterator = objects.iterator();
        while(iterator.hasNext())
        {
            // get the object
            GameObject object = iterator.next();
            // Flames are special case
            if(object instanceof Flame)
            {
                // Get the screen coordinates
                tmpVector2.set(Camera.getInstance().screenToWorld(tmpVector.set(Core.getInstance().getGraphicsManager().WIDTH, 0f)));
                // Check if we need to remove the object
                if(object.getPosition().x - object.getSize().x / 2f >= tmpVector2.x)
                {
                    // Destroy the object
                    object.destroy();
                    // Remove it
                    iterator.remove();
                }
            }
            else
            {
                // Check if we need to remove the object
                if(Player.getInstance().getPosition().x >= object.getPosition().x + screenWidth)
                {
                    // Destroy the object
                    object.destroy();
                    // Remove it
                    iterator.remove();
                }
            }
        }
    }

    private void cleanTerrainObjects()
    {
        // Loop through all the objects
        Iterator<TerrainObject> iterator = terrainObjects.iterator();
        while(iterator.hasNext())
        {
            // get the object
            TerrainObject object = iterator.next();
            // Check if we need to remove the object
            if(Player.getInstance().getPosition().x >= object.getPosition().x + STREAMING_DISTANCE / 2)
                // Remove it
                iterator.remove();
        }
    }


    private void processObjects()
    {
        // Loop through all the objects
        for(GameObject object : objects)
            // Update it
            object.process();

        // Loop through all the terrain objects
        for(TerrainObject object : terrainObjects)
            // Update it
            object.process();
    }

    private void updateObjects()
    {
        // Find if we need to create new objects
        if(Player.getInstance().isStarted() &&
                lastObjectPosition - Player.getInstance().getPosition().x <= STREAMING_DISTANCE / 2)
        {
            // Special case to treat exclusive state
            if(!isExclusive || lastPosition - Player.getInstance().getPosition().x <= STREAMING_DISTANCE / 2)
            {
                // Check update ticks
                if(changeModeLocked >= MINIMUM_GAME_MODE_UPDATES && GameLogic.getInstance().isEndless())
                {
                    if(incomingGameMode != GameMode.GAME_MODE_GRAVITY_SWITCH &&
                            GameLogic.getInstance().getGameMode() != GameMode.GAME_MODE_GRAVITY_SWITCH && !isExclusive && GameIntelligence.getInstance().isGravitySwitchMode())
                    {
                        // Set the incoming game mode
                        incomingGameMode = GameMode.GAME_MODE_GRAVITY_SWITCH;
                        // Set the change mode flag
                        changeMode = true;
                        // Set the last object position
                        changeModePosition = (lastPosition > lastObjectPosition ? lastPosition : lastObjectPosition)
                                + Configuration.TERRAIN_STATIC_OBJECTS_SPACE + 5f;

                        lastObjectPosition = changeModePosition + Configuration.TERRAIN_STATIC_OBJECTS_SPACE * 3f;
                        // Add terrain object
                        generateTerrainObject(changeModePosition - 10f, 0f, "GravitySwitchLine");
                        // Reset change mode count
                        changeModeLocked = 0;
                    }
                    else
                    {
                        // Change back to flying mode
                        if(incomingGameMode != GameMode.GAME_MODE_FLYING &&
                                GameLogic.getInstance().getGameMode() != GameMode.GAME_MODE_FLYING &&
                                changeModeLocked >= MINIMUM_GAME_MODE_UPDATES && GameIntelligence.getInstance().isBackToFlyingMode())
                        {
                            // Set the incoming game mode
                            incomingGameMode = GameMode.GAME_MODE_FLYING;
                            // Set the change mode flag
                            changeMode = true;
                            // Set the last object position
                            changeModePosition = (lastPosition > lastObjectPosition ? lastPosition : lastObjectPosition)
                                    + Configuration.TERRAIN_STATIC_OBJECTS_SPACE + 5f;

                            lastObjectPosition = changeModePosition + Configuration.TERRAIN_STATIC_OBJECTS_SPACE * 3f;
                            // Add terrain object
                            generateTerrainObject(changeModePosition, 0f, "FlyingLine");
                            // Reset change mode count
                            changeModeLocked = 0;
                        }

                    }
                }
                else
                    changeModeLocked++;

                // Create new objects
                createObjects();
                // Clean terrain objects
                cleanTerrainObjects();
            }
        }
        // Clean all objects
        cleanObjects();
        // If we're in exclusive phase then try to create objects
        if(isExclusive)
        {
            // Wait until we pass all the objects in order to start the exclusive state
            if(!exclusiveStarted && GameLogic.getInstance().isEndless())
            {
                // Loop through all the objects
                for (GameObject object : objects)
                {
                    // If the object is not passed then set the started flag
                    if (!object.isPassed())
                    {
                        processObjects();
                        return;
                    }
                }
                // Set the exclusive flag
                exclusiveStarted = true;
                // Set the camera's right sided follow
                Camera.getInstance().toggleRightSidedFollow(true);
            }
            // Wait until we finish interpolating the camera
            if(Camera.getInstance().isInterpolatingToSide())
            {
                processObjects();
                return;
            }
            // Create the first object if it doesn't exist
            if(lastExclusiveObject == null)
            {
                // Generate the object
                generateObject(Camera.getInstance().screenToWorld(Core.getInstance().getGraphicsManager().EMPTY_VECTOR).x,
                        ObjectType.OBJECT_TYPE_EXCLUSIVE);

                // Decrease the number of exclusive objects
                levelExclusiveObjects--;
            }
            else
            {
                // If the last object has traveled a certain distance then create the next one
                if(lastExclusiveObject.getPosition().x - lastExclusiveObject.getInitialPosition().x >=
                        Configuration.TERRAIN_OBJECTS_SPACE * 4)
                {
                    generateObject(Camera.getInstance().screenToWorld(Core.getInstance().getGraphicsManager().EMPTY_VECTOR).x,
                            ObjectType.OBJECT_TYPE_EXCLUSIVE);

                    // Decrease the number of exclusive objects
                    levelExclusiveObjects--;
                }
            }
            // Did we finish the exclusive state ?
            if(levelExclusiveObjects == 0)
            {
                // Reset exclusive flag
                isExclusive = false;
                // Set the was exclusive flag
                wasExclusive = true;
                // Interpolate the camera to the left
                Camera.getInstance().toggleRightSidedFollow(false);
            }
            // Clean all objects
            cleanObjects();
        }
        // Generate fixed terrain objects
        generateFixedTerrainObjects();
        // Generate terrain objects
        generateTerrainObjects();
        // Process objects
        processObjects();
    }

    /*
        Will generate a terrain along the length given
    */
    private void createTerrain(float start, float end)
    {
        // Create a chain shape
        ChainShape chainShape = new ChainShape();
        // Setup the chain shape
        chainShape.createChain(new Vector2[] { new Vector2(start, 0), new Vector2(end, 0)});
        // Create the fixture def
        FixtureDef fixtureDef = new FixtureDef();
        // Setup the fixture def
        fixtureDef.shape = chainShape;
        // Set the collision filters
        fixtureDef.filter.categoryBits = Configuration.WALLS_CATEGORY_BITS;
        fixtureDef.filter.maskBits = Configuration.PLAYER_CATEGORY_BITS;
        // Create the fixture
        body.createFixture(fixtureDef).setUserData("Ground");
        // Finish the rest of the terrain before changing the mode
        if(changeMode)
        {
            // Create a tmp roof shape
            ChainShape tmpRoofShape = new ChainShape();
            // Setup the chain shape
            if(incomingGameMode == GameMode.GAME_MODE_GRAVITY_SWITCH)
                tmpRoofShape.createChain(new Vector2[] { new Vector2(start, Configuration.TERRAIN_ROOF_POSITION),
                        new Vector2(changeModePosition - 10f, Configuration.TERRAIN_ROOF_POSITION)});
            else
                tmpRoofShape.createChain(new Vector2[] { new Vector2(start, Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION),
                        new Vector2(changeModePosition - 10f, Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION)});

            // Setup the fixture def
            fixtureDef.shape = tmpRoofShape;
            // Set the collision filters
            fixtureDef.filter.categoryBits = Configuration.WALLS_CATEGORY_BITS;
            fixtureDef.filter.maskBits = Configuration.PLAYER_CATEGORY_BITS;
            // Create the fixture
            body.createFixture(fixtureDef).setUserData("Roof");

            // Create a tmp roof shape
            ChainShape tmpRoofShape2 = new ChainShape();
            // Setup the chain shape
            if(incomingGameMode == GameMode.GAME_MODE_GRAVITY_SWITCH)
                tmpRoofShape2.createChain(new Vector2[] { new Vector2(changeModePosition - 10f, Configuration.TERRAIN_ROOF_POSITION),
                    new Vector2(changeModePosition, Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION)});
            else
                tmpRoofShape2.createChain(new Vector2[] { new Vector2(changeModePosition - 10f, Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION),
                        new Vector2(changeModePosition, Configuration.TERRAIN_ROOF_POSITION)});

            // Setup the fixture def
            fixtureDef.shape = tmpRoofShape2;
            // Set the collision filters
            fixtureDef.filter.categoryBits = Configuration.WALLS_CATEGORY_BITS;
            fixtureDef.filter.maskBits = Configuration.PLAYER_CATEGORY_BITS;
            // Create the fixture
            body.createFixture(fixtureDef).setUserData("Roof");

            // Change the start position
            start = changeModePosition;
            // Reset change mode flag
            changeMode = false;
        }
        // Calculate the roof position based on the game mode
        float y = incomingGameMode == GameMode.GAME_MODE_GRAVITY_SWITCH ?
                Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION : Configuration.TERRAIN_ROOF_POSITION;

        // Create a roof shape
        ChainShape roofShape = new ChainShape();
        // Setup the chain shape
        roofShape.createChain(new Vector2[] { new Vector2(start, y), new Vector2(end, y)});
        // Setup the fixture def
        fixtureDef.shape = roofShape;
        // Set the collision filters
        fixtureDef.filter.categoryBits = Configuration.WALLS_CATEGORY_BITS;
        fixtureDef.filter.maskBits = Configuration.PLAYER_CATEGORY_BITS;
        // Create the fixture
        body.createFixture(fixtureDef).setUserData("Roof");
        // Update the last terrain
        lastPosition = end;
        // Destroy shapes
        chainShape.dispose();
        roofShape.dispose();
        // Update the terrain for the game logic
        GameLogic.getInstance().updateTerrain();
    }

    private void createObjects()
    {
        // Are in an exclusive state ?
        if(isExclusive && !levelMode)
        {
            // Increase the exclusive phases count
            exclusivePhases++;
            // Reset the exclusive flag if we've reached an end
            if(exclusivePhases == EXCLUSIVE_PHASES)
            {
                // Reset exclusive flag
                isExclusive = false;
                // Set the was exclusive flag
                wasExclusive = true;
                // Interpolate the camera to the left
                Camera.getInstance().toggleRightSidedFollow(false);
            }
            return;
        }
        // Check for exclusive objects
        if(GameLogic.getInstance().isEndless() && !wasExclusive && lastObjectType != ObjectType.OBJECT_TYPE_EXCLUSIVE &&
                incomingGameMode == GameMode.GAME_MODE_FLYING)
            isExclusive = GameIntelligence.getInstance().isExclusive();

        if(!isExclusive && !Camera.getInstance().isInterpolatingToSide())
        {
            // If it was exclusive
            if(wasExclusive)
            {
                // Set the last object position to the last terrain's position
                lastObjectPosition = lastPosition;
                // Reset the was exclusive flag
                wasExclusive = false;
            }
            // Generate objects
            generateObjects();
        }
        else
        {
            // Reset the exclusive started flag
            exclusiveStarted = false;
            // Reset exclusive phases
            exclusivePhases = 0;
            // Set the last object type
            lastObjectType = ObjectType.OBJECT_TYPE_EXCLUSIVE;
        }
    }

    /*
        Will search for an event with the specified type that will occur before the specified time
    */
    private LevelEventNode getEventInTime(LevelEventNode.LevelEventType type, float timeStart, float timeEnd)
    {
        // Loop through all the events
        for(LevelEventNode node : levelEventsArray)
        {
            // Check the time
            if(node.type == type && node.position > timeStart && node.position <= timeEnd)
                return node;
            else if(node.position > timeEnd)
                break;
        }
        return null;
    }

    /*
       Will generate random objects for the next terrain part
    */
    private void generateObjects()
    {
        // Only generate objects if we're in endless mode
        if(GameLogic.getInstance().isEndless())
        {
            // Fill the whole terrain part
            while(lastObjectPosition < lastPosition)
            {
                // Check the game mode
                if(GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING && incomingGameMode == GameMode.GAME_MODE_FLYING)
                {
                    // Find a random object type
                    ObjectType objectType = GameIntelligence.getInstance().findObjectType();
                    // Create the object if its static
                    if (objectType == ObjectType.OBJECT_TYPE_STATIC) {
                        // Find if we're going for a combination or generating random objects
                        if (GameIntelligence.getInstance().isStaticCombination()) {
                            // Find a simple combination
                            int combinationId = GameIntelligence.getInstance().findStaticCombination();
                            // Get the combination node
                            StaticCombinationNode node = Core.getInstance().getPreSetCombinations().getStaticCombinationNode(combinationId);
                            // Loop through the combination objects
                            Vector2 position = new Vector2();
                            for (int i = 0; i < node.model.length; i++) {
                                // Get the object position
                                position.set(lastObjectPosition + Configuration.TERRAIN_STATIC_OBJECTS_SPACE * node.distance[i],
                                        GameIntelligence.getPositionFromName(node.position[i]));

                                // Create the object
                                createObject(node.model[i], position, 0, 0, node.speed, false, node.passable[i]);
                            }
                            // Add the end distance
                            lastObjectPosition += node.endDistance;
                            // Add the regular static distance
                            lastObjectPosition += Configuration.TERRAIN_STATIC_OBJECTS_SPACE;
                        } else {
                            // Fix the static objects after combination issue
                            if (lastObjectType != ObjectType.OBJECT_TYPE_STATIC && lastObjectType != ObjectType.OBJECT_TYPE_EXCLUSIVE)
                                lastObjectPosition += Configuration.TERRAIN_OBJECTS_SPACE;

                            // Always generate at least "MINIMUM_STATIC_OBJECTS_COUNT" static objects
                            for(int i = 0; i < Configuration.MINIMUM_STATIC_OBJECTS_COUNT; i++)
                            {
                                // Create the object accordingly
                                generateObject(lastObjectPosition, objectType);
                                // Get last objects
                                if(objects.size >= 3)
                                {
                                    // Get the last objects
                                    GameObject thisObject = objects.peek();
                                    GameObject lastObject = objects.get(objects.size - 2);
                                    if(lastObject.getPosition().x == thisObject.getPosition().x)
                                        lastObject = objects.get(objects.size - 3);

                                    // Create coin if necessary
                                    if(lastObject.getPosition().y == thisObject.getPosition().y && !(lastObject instanceof EatableObject))
                                        createObject("Coin", tmpVector.set((lastObject.getPosition().x + thisObject.getPosition().x) / 2f,
                                                thisObject.getPosition().y), 0, 0, 0, false, false);
                                }
                            }
                        }
                    }
                    else
                    {
                        // Process simple combination
                        if (objectType == ObjectType.OBJECT_TYPE_MOVABLE) {
                            // Find a simple combination
                            int combinationId = GameIntelligence.getInstance().findSimpleCombination();
                            // Get the combination node
                            SimpleCombinationNode node = Core.getInstance().getPreSetCombinations().getSimpleCombinationNode(combinationId);
                            // Loop through the combination objects
                            Vector2 position = new Vector2();
                            for (int i = 0; i < node.model.length; i++) {
                                // Don't include eatables
                                float x = lastObjectPosition + Configuration.TERRAIN_STATIC_OBJECTS_SPACE * node.distance[i];
                                if (!node.model[i].equals("Coin")) {
                                    // Find the movement type
                                    String movementType = Core.getInstance().getModelManager().getMovementNodes(node.model[i])[node.movementId[i]].type;
                                    // Get the object position
                                    x = lastObjectPosition + (movementType.equals("Circular") ?
                                            Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH * node.distance[i] : Configuration.MOVABLE_OBJECT_PATH_LENGTH * node.distance[i]);
                                }
                                // Get the object position
                                position.set(x, GameIntelligence.getPositionFromName(node.position[i]));
                                // Create the object
                                createObject(node.model[i], position, node.movementId[i], node.startingPoint[i], node.speed, false, node.passable[i]);
                            }
                            // Add the end distance
                            lastObjectPosition += node.endDistance;
                        }
                        // Process composed objects
                        else if (objectType == ObjectType.OBJECT_TYPE_COMPOSED) {
                            // Find a composed combination
                            int combinationId = GameIntelligence.getInstance().findComposedCombination();
                            // Get the combination node
                            ComposedCombinationNode node = Core.getInstance().getPreSetCombinations().getComposedCombinationNode(combinationId);
                            // Create the position position
                            Vector2 position = new Vector2();
                            // Loop through the combination objects
                            for (int i = 0; i < node.model.length; i++) {
                                // Find the two composed movements
                                int movement1 = Core.getInstance().getModelManager().getModelNode(node.model[i]).composedMovement1[node.movementId[i]];
                                int movement2 = Core.getInstance().getModelManager().getModelNode(node.model[i]).composedMovement2[node.movementId[i]];
                                float translateX =
                                        Core.getInstance().getModelManager().getModelNode(node.model[i]).composedMovementPositionFix[node.movementId[i]];

                                // Find the movement type
                                String movementType = Core.getInstance().getModelManager().getMovementNodes(node.model[i])[movement1].type;
                                // Get the object position
                                float x = lastObjectPosition + (movementType.equals("Circular") ?
                                        Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH * node.distance[i] : Configuration.MOVABLE_OBJECT_PATH_LENGTH * node.distance[i]);

                                // Set the position
                                position.set(x, Configuration.TERRAIN_ROOF_POSITION / 2);
                                // Create the first object
                                createObject(node.model[i], position, movement1, node.startingPoint1[i], node.speed, false, node.passable[i]);
                                // Create the second object
                                createObject(node.model[i], position.sub(translateX * (movementType.equals("Circular") ?
                                                Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH : Configuration.MOVABLE_OBJECT_PATH_LENGTH), 0),
                                        movement2, node.startingPoint2[i], node.speed, false, false);

                            }
                            // Add the end distance
                            lastObjectPosition += node.endDistance;
                        }
                        // Process parted objects
                        else if (objectType == ObjectType.OBJECT_TYPE_PARTED) {
                            // Find a composed combination
                            int combinationId = GameIntelligence.getInstance().findPartedCombination();
                            // Get the combination node
                            PartedCombinationNode node = Core.getInstance().getPreSetCombinations().getPartedCombinationNode(combinationId);
                            // Create the position vector
                            Vector2 position = new Vector2();
                            // Loop through the combination objects
                            for (int i = 0; i < node.model.length; i++) {
                                // Get the object position
                                position.set(lastObjectPosition + Configuration.TERRAIN_STATIC_OBJECTS_SPACE * node.distance[i],
                                        Configuration.TERRAIN_ROOF_POSITION / 2);

                                // Create the object
                                createObject(node.model[i], position, 0, node.startingPoint[i], node.speed, false, node.passable[i]);
                                // Add the middle coin
                                createObject("Coin", position, 0, 0, 0f, false, false);
                            }
                            // Add the end distance
                            lastObjectPosition += node.endDistance;
                        }
                        // Process chain objects
                        else {
                            // Find a chain combination
                            int combinationId = GameIntelligence.getInstance().findChainCombination();
                            // Get the combination node
                            ChainCombinationNode node = Core.getInstance().getPreSetCombinations().getChainCombinationNode(combinationId);
                            // Reset the last position
                            float lastPosition = 0f;
                            // Add some distance before any chain object
                            lastObjectPosition += Configuration.TERRAIN_STATIC_OBJECTS_SPACE * 0.5f;
                            // Loop through the combination objects
                            for (int i = 0; i < node.model.length; i++) {
                                // Create the vertices array
                                Vector2[] groundVertices = new Vector2[node.objectPoints[i].groundPositions.length];
                                Vector2[] roofVertices = new Vector2[node.objectPoints[i].roofPositions.length];
                                // Set the current and starting position
                                float currentPosition = lastObjectPosition + lastPosition;
                                float startingPosition = lastPosition;
                                // Loop through the object points
                                for (int j = 0; j < node.objectPoints[i].groundPositions.length; j++) {
                                    // Get coordinates
                                    float x = node.objectPoints[i].groundPositions[j].x;
                                    float y = node.objectPoints[i].groundPositions[j].y;
                                    float x1 = node.objectPoints[i].roofPositions[j].x;
                                    float y1 = node.objectPoints[i].roofPositions[j].y;
                                    // Add the vectors
                                    groundVertices[j] = new Vector2(x - startingPosition, y);
                                    roofVertices[j] = new Vector2(x1 - startingPosition, y1);
                                    // Set the last position
                                    if (x > lastPosition)
                                        lastPosition = x;
                                }
                                // Create the object
                                createObject(node.model[i], new Vector2(currentPosition, 0f), groundVertices, roofVertices,
                                        node.flags[i], node.passable[i]);
                            }
                            // Increase the last object position
                            lastObjectPosition += lastPosition;
                        }
                        // Add the regular static distance
                        lastObjectPosition += Configuration.TERRAIN_STATIC_OBJECTS_SPACE;
                    }
                    // Save the last object type
                    lastObjectType = objectType;
                }
                else
                {
                    // Find if we're using combinations
                    if(GameIntelligence.getInstance().isGravityCombination())
                    {
                        // Find a random combination id
                        int combinationId = GameIntelligence.getInstance().findGravityCombination();
                        // Get the combination
                        GravityCombinationNode node = Core.getInstance().getPreSetCombinations().getGravityCombinationNode(combinationId);
                        // Loop through the combination objects
                        for(int i = 0; i < node.position.length; i++)
                        {
                            // Get the position from its name
                            float position = GameIntelligence.getGravityPositionFromName(node.position[i]);
                            // Calculate the x position
                            float x = lastObjectPosition + Configuration.TERRAIN_STATIC_OBJECTS_SPACE * node.distance[i];
                            // Create the object
                            GameObject object = createObject(node.model[i], tmpVector.set(x, position), node.movement[i], node.startingPoint[i],
                                    node.speed, true, node.passable[i]);

                            // Ignore if its an ignored coin
                            if(object == null)
                                continue;

                            // Set its position
                            if(node.position[i].equals("Up"))
                            {
                                object.setInitialPosition(tmpVector.set(object.getPosition().x, position - object.getSize().y / 2f));
                                // Rotate
                                object.setRotationInDegrees(180f);
                            }
                            else if(node.position[i].equals("Down"))
                                object.setInitialPosition(tmpVector.set(object.getPosition().x, position + object.getSize().y / 2f));
                        }
                        // Add the end distance
                        lastObjectPosition += node.endDistance;
                    }
                    else
                    {
                        // Find a random object model
                        String model = GameIntelligence.getInstance().findGravityObjectModel();
                        // Always generate at least "MINIMUM_STATIC_OBJECTS_COUNT * 2" static objects
                        for(int i = 0; i < Configuration.MINIMUM_STATIC_OBJECTS_COUNT * 2; i++)
                        {
                            // Find the object's position
                            boolean isUp = GameIntelligence.getInstance().isGravityModeObjectUp();
                            // Create a random object
                            GameObject object = createObject(model, tmpVector.set(lastObjectPosition, 0f), 0, 0, 0, true,
                                    GameIntelligence.getInstance().isGravityModeObjectPassable());

                            // Set its position
                            object.setInitialPosition(tmpVector.set(object.getPosition().x,
                                    isUp ? Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION - object.getSize().y / 2f :
                                            object.getSize().y / 2f));

                            // Rotate if necessary
                            if(isUp)
                                object.setRotationInDegrees(180f);
                        }
                    }
                }
            }
        }
        else
        {
            // Get current music
            GameMusic currentMusic = Core.getInstance().getCurrentMusic();
            if(currentMusic == null)
                return;

            // Calculate the time to the streaming distance
            float streamingTime = currentMusic.getPosition() + LEVEL_STREAMING_TIME;
            System.out.println("check with: " + currentMusic.getPosition() + " for " + levelObjectsArray.size + " objs");
            // Loop through the objects
            Iterator<LevelObjectNode> iterator = levelObjectsArray.iterator();
            while(iterator.hasNext())
            {
                // Get the node
                LevelObjectNode node = iterator.next();
                // Get the position in seconds
                float secondsPosition = node.positionX;
                // Do we need to create the object ?
                if(secondsPosition <= streamingTime)
                {
                    // Set the object's creation size
                    /*float objectSize = object.getMovement() != null ?
                            object.getInitialPosition().x - object.getMovement().getLowestPoint().x : object.getSize().x / 2f;

                    // Fix the creation position
                    if(objectSize == 0f)
                        objectSize = object.getSize().x / 2f;*/

                    float objectPosition;
                    // Do we have a speed changing event in these seconds ?
                    LevelEventNode eventNode = getEventInTime(LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_SPEED_CHANGE,
                            currentMusic.getPosition(), secondsPosition);

                    // Set the object's position
                    if(eventNode == null)
                        objectPosition = Player.getInstance().getPosition().x +
                            (secondsPosition - currentMusic.getPosition()) * Player.getInstance().getVelocity().x;
                    else
                    {
                        // Initialize the object's position
                        objectPosition = Player.getInstance().getPosition().x;
                        float currentSpeed = Player.getInstance().getVelocity().x;
                        float lastPosition = currentMusic.getPosition();
                        do
                        {
                            // Add distance
                            objectPosition += (eventNode.position - lastPosition) * currentSpeed;
                            // Update current speed
                            currentSpeed = ((LevelSpeedChangeEventNode)eventNode).speed;
                            // Update last position
                            lastPosition = eventNode.position;
                            // Get the next node
                            eventNode = getEventInTime(LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_SPEED_CHANGE,
                                    lastPosition, secondsPosition);

                        } while(eventNode != null);
                        // Add the object's position
                        objectPosition += (secondsPosition - lastPosition) * currentSpeed;
                    }
                    // Process chain objects
                    if(node.model.equals("Wall"))
                    {
                        // Get the combination node
                        ChainCombinationNode combinationNode =
                                Core.getInstance().getPreSetCombinations().getChainCombinationNode(node.movementId);

                        // Reset the last position
                        float lastPosition = 0f;
                        // Loop through the combination objects
                        for (int i = 0; i < combinationNode.model.length; i++) {
                            // Create the vertices array
                            Vector2[] groundVertices = new Vector2[combinationNode.objectPoints[i].groundPositions.length];
                            Vector2[] roofVertices = new Vector2[combinationNode.objectPoints[i].roofPositions.length];
                            // Set the current and starting position
                            float currentPosition = objectPosition + lastPosition;
                            float startingPosition = lastPosition;
                            // Loop through the object points
                            for (int j = 0; j < combinationNode.objectPoints[i].groundPositions.length; j++)
                            {
                                // Get coordinates
                                float x = combinationNode.objectPoints[i].groundPositions[j].x;
                                float y = combinationNode.objectPoints[i].groundPositions[j].y;
                                float x1 = combinationNode.objectPoints[i].roofPositions[j].x;
                                float y1 = combinationNode.objectPoints[i].roofPositions[j].y;
                                // Add the vectors
                                groundVertices[j] = new Vector2(x - startingPosition, y);
                                roofVertices[j] = new Vector2(x1 - startingPosition, y1);
                                // Set the last position
                                if (x > lastPosition)
                                    lastPosition = x;
                            }
                            // Create the object
                            createObject(node.model, new Vector2(currentPosition, 0f), groundVertices, roofVertices,
                                    combinationNode.flags[i], false);
                        }
                    }
                    else
                        // Create the object
                        createLevelObject(node.model, new Vector2(objectPosition, node.positionY), node.movementId, node.startingPoint,
                                node.speed);

                    System.out.println("new obj: " + node.model + " at " + node.positionX + "sec on " + objectPosition);
                    // Remove the object
                    iterator.remove();
                }
                else
                    break;
            }
        }
    }

    /*
       Will generate a random object
    */
    private void generateObject(float x, ObjectType objectType)
    {
        // Find a random object model
        String model = GameIntelligence.getInstance().findObjectModel(objectType);
        if(objectType == ObjectType.OBJECT_TYPE_EXCLUSIVE)
        {
            // Get the last object's vertical position
            float lastY = lastExclusiveObject == null ? 0 : lastExclusiveObject.getInitialPosition().y;
            // Create the object
            lastExclusiveObject =
                    createObject(model, new Vector2(x, GameIntelligence.getInstance().findObjectPosition(model, lastY)), 0, 0, 0, false, true);
        }
        else
        {
            // Check if we need to create one or two objects
            boolean multipleObjects = GameIntelligence.getInstance().createMultipleObjects(model);
            // Add a little bit of distance before the block
            if(multipleObjects)
                x += Configuration.TERRAIN_STATIC_OBJECTS_SPACE * 0.25f;

            // Find the y position for the object
            float y = GameIntelligence.getInstance().findObjectPosition(model);
            // Create the object
            createObject(model, new Vector2(x, y), 0, 0, 0, !multipleObjects, true);
            // Create the second object in case of multiple objects
            if(multipleObjects)
                createObject(model, new Vector2(x, GameIntelligence.getInstance().findObjectPosition(model, y)), 0, 0, 0, true, false);
        }
    }

    /*
        Will create an object with the model and position specified
    */
    private GameObject createObject(String model, Vector2 position, int movement, int startingPoint, float speed, boolean increasePosition, boolean passable)
    {
        // If its a coin then see if we'll create it
        if(model.equals("Coin") && !levelMode && !GameIntelligence.getInstance().shouldCreateCoin())
            return null;

        // Create the object instance
        GameObject object;
        if(model.equals("Inflatable"))
            object = new Inflatable();
        else if(model.equals("Laser"))
            object = new Laser();
        else if(model.equals("Flame"))
            object = new Flame();
        else if(model.equals("ChainsawStick"))
            object = new ChainsawStick(passable);
        else if(model.equals("ChainsawTriangle"))
            object = new ChainsawTriangle(passable);
        else if(model.equals("Coin"))
            object = new EatableObject(model);
        else if(model.equals("Cube"))
            object = new Cube();
        else if(movement > 0)
            object = new MovableGameObject(model, passable);
        else
            object = new GameObject(model, passable);

        // Create the object
        object.create(position, movement, startingPoint, speed);
        // Set the object's cave mode
        object.toggleCaveMode(caveMode);
        // Add it to the objects array
        objects.add(object);
        // If its not an eatable object
        if(!(object instanceof EatableObject))
        {
            // Set the last object position
            if (increasePosition)
                lastObjectPosition = position.x + (incomingGameMode == GameMode.GAME_MODE_GRAVITY_SWITCH ?
                        Configuration.TERRAIN_GRAVITY_MODE_STATIC_OBJECTS_SPACE : Configuration.TERRAIN_STATIC_OBJECTS_SPACE);
            else
                lastObjectPosition = position.x;
        }
        return object;
    }

    /*
        Will create a level object
    */
    private GameObject createLevelObject(String model, Vector2 position, int movement, int startingPoint, float speed)
    {
        // Get the model's node
        ModelNode node = Core.getInstance().getModelManager().getModelNode(model);
        return createObject(model, position.add(node.size.x == 0f ? node.radius : node.size.x / 2f, 0f), movement, startingPoint, speed, false, false);
    }

    /*
       Will create a chain object with the model and position specified
    */
    private GameObject createObject(String model, Vector2 position, Vector2[] groundVertices, Vector2[] roofVertices,
                                    String flags, boolean passable)
    {
        // Create the object instance
        ChainGameObject object = new ChainGameObject(model, passable);
        // Create the object
        object.create(position, groundVertices, roofVertices, flags);
        // Add it to the objects array
        objects.add(object);
        return object;
    }

    /*
       Will generate random terrain objects
    */
    private void generateTerrainObjects()
    {
        // Find the current limits
        Vector2 startPosition = Camera.getInstance().screenToWorld(tmpVector.set(Core.getInstance().getGraphicsManager().WIDTH, 0f));
        // Generate random terrain objects
        if(GameIntelligence.getInstance().isGenerateTerrainObjects())
        {
            // Find the object model
            String model = GameIntelligence.getInstance().findTerrainObjectModel();
            // Set the model's size
            tmpVector2.set(2f, 2f);
            // Create the minimum terrain objects
            for(int i = 0; i < MINIMUM_TERRAIN_OBJECTS; i++)
            {
                // Find a random position
                float x = MathUtils.random(startPosition.x, startPosition.x + 30f);
                float y = MathUtils.random(1f, Configuration.TERRAIN_ROOF_POSITION - 1f);
                // Ensure that it doesn't collide with other terrain objects
                if(checkTerrainObjectCollision(x, y, tmpVector2))
                    // Create the terrain object
                    generateTerrainObject(x, y, model);
            }
        }
    }

    /*
       Will check the collision between terrain objects
    */
    private boolean checkTerrainObjectCollision(float x, float y, Vector2 size)
    {
        // Loop through all the terrain objects
        for(TerrainObject object : terrainObjects)
        {
            // Check object's collision with a radius
            if(GameMath.checkCollision(tmpVector.set(x, y), size, object.getPosition(), tmpVector3.set(object.getSize()).scl(2f)))
                return false;
        }
        return true;
    }

    /*
       Will generate fixed terrain objects
    */
    private void generateFixedTerrainObjects()
    {
        // Check the fixed terrain objects array
        if(fixedTerrainObjects.size == 0)
            return;

        // Loop through the fixed terrain objects
        Iterator<String> iterator = fixedTerrainObjects.iterator();
        while(iterator.hasNext())
        {
            // Get the model
            String model = iterator.next();
            // Find the fixed position
            float position = Core.getInstance().getModelSettings().getModelAttributeAsFloat(model, "fixedPosition");
            // Ensure that we're close to it
            if(Math.abs(position - Player.getInstance().getPosition().x) >= TERRAIN_OBJECTS_STREAMING)
            {
                // Generate the object
                generateTerrainObject(position, 0f, model);
                // Remove the fixed model
                iterator.remove();
            }
        }
    }

    /*
        Will generate a terrain object
    */
    private void generateTerrainObject(float x, float y, String model)
    {
        // Create terrain object instance
        TerrainObject object;
        if(model.equals("Dot"))
            object = new Dot(model, new Vector2(x, y));
        else
            object = new TerrainObject(model, new Vector2(x, y));

        // Add it to the objects arrays
        terrainObjects.add(object);
    }

    private void processEvents()
    {
        // Skip if we have no music
        if(Core.getInstance().getCurrentMusic() == null || !inGameScreen || Core.getInstance().isFadingMusic())
            return;

        // Loop through all the events
        Iterator<LevelEventNode> iterator = levelEventsArray.iterator();
        while(iterator.hasNext())
        {
            // Get the event
            LevelEventNode event = iterator.next();
            // Did we pass the event
            if(Core.getInstance().getCurrentMusic().getPosition() >= event.position)
            {
                boolean remove = true;
                // Trigger the event
                if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_BACKGROUND_TRANSITION)
                {
                    LevelBackgroundTransitionEventNode backgroundEvent = (LevelBackgroundTransitionEventNode)event;
                    nextEnvironmentColor = new Color(backgroundEvent.r, backgroundEvent.g, backgroundEvent.b, backgroundEvent.a);
                    oldEnvironmentColor = environmentColor.cpy();
                    // Set the transition duration
                    transitionDuration = backgroundEvent.transitDuration;
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_SPEED_CHANGE)
                {
                    LevelSpeedChangeEventNode speedEvent = (LevelSpeedChangeEventNode)event;
                    // Set the player's speed
                    Player.getInstance().setVerticalVelocity(speedEvent.speed);
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_TAKE_CONTROL)
                {
                    LevelTakeControlEventNode takeControlEvent = (LevelTakeControlEventNode)event;
                    // Take the player's control
                    Player.getInstance().takeControl(takeControlEvent.shuffleDistance);
                    // Fly to position
                    float speed = Player.getInstance().getPosition().y >= takeControlEvent.positionY ? -takeControlEvent.speed :
                            takeControlEvent.speed;

                    Player.getInstance().flyTo(takeControlEvent.positionY, speed);
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_RESTORE_CONTROL)
                {
                    // Restore the player's control
                    Player.getInstance().restoreControl();
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_TOGGLE_LETHAL_GROUND)
                {
                    LevelToggleLethalGroundEventNode toggleLethalGroundEvent = (LevelToggleLethalGroundEventNode)event;
                    // Toggle lethal ground
                    GameLogic.getInstance().toggleLethalGround(toggleLethalGroundEvent.toggle);
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_ENTITIES_COLOR_TRANSITION)
                {
                    LevelEntitiesColorTransitionEventNode entitiesColorEvent = (LevelEntitiesColorTransitionEventNode)event;
                    nextEntitiesColor = new Color(entitiesColorEvent.r, entitiesColorEvent.g, entitiesColorEvent.b,
                            entitiesColorEvent.a);

                    oldEntitiesColor = entitiesColor.cpy();
                    // Set the transition duration
                    entitiesColorTransitDuration = entitiesColorEvent.transitDuration;
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_ENTER_EXCLUSIVE_STATE)
                {
                    LevelEnterExclusiveStateEventNode exclusiveStateEvent = (LevelEnterExclusiveStateEventNode)event;
                    // Set the exclusive flag
                    isExclusive = true;
                    // Set the exclusive objects
                    levelExclusiveObjects = exclusiveStateEvent.objects;
                    // Set the flame's speed
                    levelFlameSpeed = exclusiveStateEvent.speed;
                    // Set the camera's right sided follow
                    Camera.getInstance().toggleRightSidedFollow(true);
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_SET_OBJECTS_ENTRY_ANIMATION)
                {
                    LevelSetObjectsEntryAnimationEventNode setObjectsEntryEvent = (LevelSetObjectsEntryAnimationEventNode)event;
                    // Set the entry type and speed
                    objectsEntryType = setObjectsEntryEvent.type;
                    objectsEntrySpeed = setObjectsEntryEvent.speed;
                }
                else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_DISPLAY_TEXT)
                {
                    LevelDisplayTextEventNode displayTextEvent = (LevelDisplayTextEventNode)event;
                    // Draw the text
                    if(Core.getInstance().getCurrentMusic().getPosition() < displayTextEvent.endPosition)
                        GameScreen.getInstance().drawLevelText(displayTextEvent.text, Core.getInstance().getGraphicsManager().WIDTH * displayTextEvent.screenPositionX,
                                Core.getInstance().getGraphicsManager().HEIGHT * displayTextEvent.screenPositionY);

                    // Stop it from being removed
                    remove = Core.getInstance().getCurrentMusic().getPosition() >= displayTextEvent.endPosition;
                }
                // Remove the event
                if(remove)
                    iterator.remove();
            }
            else if(event.type == LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_LEVEL_COMPLETED)
            {
                // Get the screen width in world coordinates
                float screenWidth = Camera.getInstance().screenToWorld(Core.getInstance().getGraphicsManager().SCREEN_VECTOR).x;
                // Do we need to create the cube ?
                if(event.position - Core.getInstance().getCurrentMusic().getPosition() <= (screenWidth - Player.getInstance().getPosition().x) /
                        Player.getInstance().getVelocity().x)
                {
                    // Create the level ending wall
                    createLevelObject("Cube", new Vector2(screenWidth, 0f), 0, 0, 0f);
                    // Toggle right sided follow
                    Camera.getInstance().toggleRightSidedFollow(true, 0.1f);
                    // Remove the event
                    iterator.remove();
                }
            }
            else
                break;
        }
    }

    private void updateEnvironmentColor()
    {
        // Transit the color if necessary
        if(nextEnvironmentColor != null)
        {
            // Slowly transit colors
            // Red
            if(nextEnvironmentColor.r > environmentColor.r)
            {
                environmentColor.r += (nextEnvironmentColor.r - oldEnvironmentColor.r) / (float)transitionDuration;
                if (environmentColor.r > nextEnvironmentColor.r)
                    environmentColor.r = nextEnvironmentColor.r;
            }
            else if(nextEnvironmentColor.r < environmentColor.r)
            {
                environmentColor.r -= (oldEnvironmentColor.r - nextEnvironmentColor.r) / (float)transitionDuration;
                if (environmentColor.r < nextEnvironmentColor.r)
                    environmentColor.r = nextEnvironmentColor.r;
            }

            // Green
            if(nextEnvironmentColor.g > environmentColor.g)
            {
                environmentColor.g += (nextEnvironmentColor.g - oldEnvironmentColor.g) / (float)transitionDuration;
                if (environmentColor.g >= nextEnvironmentColor.g)
                    environmentColor.g = nextEnvironmentColor.g;
            }
            else if(nextEnvironmentColor.g < environmentColor.g)
            {
                environmentColor.g -= (oldEnvironmentColor.g - nextEnvironmentColor.g) / (float)transitionDuration;
                if (environmentColor.g <= nextEnvironmentColor.g)
                    environmentColor.g = nextEnvironmentColor.g;
            }

            // Blue
            if(nextEnvironmentColor.b > environmentColor.b)
            {
                environmentColor.b += (nextEnvironmentColor.b - oldEnvironmentColor.b) / (float)transitionDuration;
                if (environmentColor.b >= nextEnvironmentColor.b)
                    environmentColor.b = nextEnvironmentColor.b;
            }
            else if(nextEnvironmentColor.b < environmentColor.b)
            {
                environmentColor.b -= (oldEnvironmentColor.b - nextEnvironmentColor.b) / (float)transitionDuration;
                if (environmentColor.b <= nextEnvironmentColor.b)
                    environmentColor.b = nextEnvironmentColor.b;
            }

            // Alpha
            if(nextEnvironmentColor.a > environmentColor.a)
            {
                environmentColor.a += (nextEnvironmentColor.a - oldEnvironmentColor.a) / (float)transitionDuration;
                if (environmentColor.a >= nextEnvironmentColor.a)
                    environmentColor.a = nextEnvironmentColor.a;
            }
            else if(nextEnvironmentColor.a < environmentColor.a)
            {
                environmentColor.a -= (oldEnvironmentColor.a - nextEnvironmentColor.a) / (float)transitionDuration;
                if (environmentColor.a <= nextEnvironmentColor.a)
                    environmentColor.a = nextEnvironmentColor.a;
            }

            // Did we finish the transition ?
            if(nextEnvironmentColor.equals(environmentColor))
                nextEnvironmentColor = null;
        }

        // Transit the entities color if necessary
        if(nextEntitiesColor != null)
        {
            // Slowly transit colors
            // Red
            if(nextEntitiesColor.r > entitiesColor.r)
            {
                entitiesColor.r += (nextEntitiesColor.r - oldEntitiesColor.r) / (float)entitiesColorTransitDuration;
                if (entitiesColor.r > nextEntitiesColor.r)
                    entitiesColor.r = nextEntitiesColor.r;
            }
            else if(nextEntitiesColor.r < entitiesColor.r)
            {
                entitiesColor.r -= (oldEntitiesColor.r - nextEntitiesColor.r) / (float)entitiesColorTransitDuration;
                if (entitiesColor.r < nextEntitiesColor.r)
                    entitiesColor.r = nextEntitiesColor.r;
            }

            // Green
            if(nextEntitiesColor.g > entitiesColor.g)
            {
                entitiesColor.g += (nextEntitiesColor.g - oldEntitiesColor.g) / (float)entitiesColorTransitDuration;
                if (entitiesColor.g >= nextEntitiesColor.g)
                    entitiesColor.g = nextEntitiesColor.g;
            }
            else if(nextEntitiesColor.g < entitiesColor.g)
            {
                entitiesColor.g -= (oldEntitiesColor.g - nextEntitiesColor.g) / (float)entitiesColorTransitDuration;
                if (entitiesColor.g <= nextEntitiesColor.g)
                    entitiesColor.g = nextEntitiesColor.g;
            }

            // Blue
            if(nextEntitiesColor.b > entitiesColor.b)
            {
                entitiesColor.b += (nextEntitiesColor.b - oldEntitiesColor.b) / (float)entitiesColorTransitDuration;
                if (entitiesColor.b >= nextEntitiesColor.b)
                    entitiesColor.b = nextEntitiesColor.b;
            }
            else if(nextEntitiesColor.b < entitiesColor.b)
            {
                entitiesColor.b -= (oldEntitiesColor.b - nextEntitiesColor.b) / (float)entitiesColorTransitDuration;
                if (entitiesColor.b <= nextEntitiesColor.b)
                    entitiesColor.b = nextEntitiesColor.b;
            }

            // Alpha
            if(nextEntitiesColor.a > entitiesColor.a)
            {
                entitiesColor.a += (nextEntitiesColor.a - oldEntitiesColor.a) / (float)entitiesColorTransitDuration;
                if (entitiesColor.a >= nextEntitiesColor.a)
                    entitiesColor.a = nextEntitiesColor.a;
            }
            else if(nextEntitiesColor.a < entitiesColor.a)
            {
                entitiesColor.a -= (oldEntitiesColor.a - nextEntitiesColor.a) / (float)entitiesColorTransitDuration;
                if (entitiesColor.a <= nextEntitiesColor.a)
                    entitiesColor.a = nextEntitiesColor.a;
            }

            // Did we finish the transition ?
            if(nextEntitiesColor.equals(entitiesColor))
                nextEntitiesColor = null;
        }
    }

    public void process()
    {
        // Update objects
        if(inGameScreen)
            updateObjects();
        else
        {
            generateTerrainObjects();
            // Loop through all the terrain objects
            for(TerrainObject object : terrainObjects)
                // Update it
                object.process();

            cleanTerrainObjects();
        }
        // Update the terrain
        update();
        // Process events
        if(levelMode)
            processEvents();

        // Update environment color
        updateEnvironmentColor();
        // Prepare rendering vertices
        prepareGroundRendering();
        // Prepare background rendering
        prepareBackgroundRendering();
        // Check if the player have reached the change mode
        if(GameLogic.getInstance().getGameMode() != incomingGameMode && Player.getInstance().getPosition().x >= changeModePosition)
        {
            // Set the new game mode
            GameLogic.getInstance().setGameMode(incomingGameMode);
            // Are we in flying mode ?
            if(incomingGameMode == GameMode.GAME_MODE_FLYING)
                // Restore the lethal ground's state
                GameLogic.getInstance().toggleLethalGround(wasLethalGround);
            else
            {
                // Get the lethal ground flag
                wasLethalGround = GameLogic.getInstance().isLethalGround();
                // Disable lethal ground
                GameLogic.getInstance().toggleLethalGround(false);
            }
            // Reset slow motion state
            GameWorld.getInstance().setSlowMotion(false);
        }
        else if(changeModePosition != 0 && Player.getInstance().getPosition().x < changeModePosition &&
                !GameWorld.getInstance().isSlowMotion() && Math.abs(Player.getInstance().getPosition().x - changeModePosition) <= 15f)
            GameWorld.getInstance().setSlowMotion(true);
        // Set flying to middle
        if(incomingGameMode == GameMode.GAME_MODE_FLYING && GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_GRAVITY_SWITCH &&
                Player.getInstance().getPosition().x >= (changeModePosition - Configuration.TERRAIN_STATIC_OBJECTS_SPACE - 10f)
                && !Player.getInstance().isFlyingToMiddle())
            Player.getInstance().backToFlying();
    }

    public void render()
    {
        // Draw the background
        drawBackground();
        // Draw paths
        drawMovableObjectsPath();
        // Draw terrain objects
        drawTerrainObjects();
        // Render objects
        drawObjects();
        // Render the ground
        drawGround();
    }

    private void drawBackground()
    {
        // Draw the background
        Core.getInstance().getGraphicsManager().drawTexture(backgroundTexture, backgroundVertices, backgroundVerticesIndex);
        // Draw spikes
        if(spikesVerticesIndex != 0)
            Core.getInstance().getGraphicsManager().drawTexture(spikesTexture, spikesVertices, spikesVerticesIndex);
    }
    
    private void drawObjects()
    {
        // Loop through all the objects
        for(GameObject object : objects)
            // Render it
            object.render();
    }

    private void drawTerrainObjects()
    {
        // Loop through all the terrain objects
        for(TerrainObject object : terrainObjects)
            // Render it
            object.render();
    }

    /*
        This will render the all the paths (rails) of the movable objects
    */
    public void drawMovableObjectsPath()
    {
        // Loop through all the objects
        for(GameObject object : objects)
        {
            // Render it
            if(object instanceof MovableGameObject)
                ((MovableGameObject)object).drawPath();
        }
    }

    private void prepareBackgroundRendering()
    {
        // Get the color
        groundColor.set(environmentColor);
        // Reduce the alpha
        if(!levelMode)
            groundColor.a = 0.5f;

        // Reset background vertices index
        backgroundVerticesIndex = 0;
        // Get the texture's width position
        float startingTextureU = Camera.getInstance().getPosition().x / BACKGROUND_TEXTURE_WIDTH;
        float finishingTextureU = (Camera.getInstance().getPosition().x + GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH))
                / BACKGROUND_TEXTURE_WIDTH;

        float textureV = BACKGROUND_TEXTURE_HEIGHT;
        // Convert to screen coordinates
        tmpVector.set(Camera.getInstance().worldToScreen(tmpVector.set(0f, Configuration.TERRAIN_ROOF_POSITION)));
        tmpVector2.set(Camera.getInstance().worldToScreen(tmpVector2.set(0f, 0f)));

        // Set the background vertices
        // Down Left
        backgroundVertices[backgroundVerticesIndex++] = 0f;
        backgroundVertices[backgroundVerticesIndex++] = tmpVector2.y;
        backgroundVertices[backgroundVerticesIndex++] = groundColor.toFloatBits();
        backgroundVertices[backgroundVerticesIndex++] = startingTextureU;
        backgroundVertices[backgroundVerticesIndex++] = textureV;

        // Up Left
        backgroundVertices[backgroundVerticesIndex++] = 0f;
        backgroundVertices[backgroundVerticesIndex++] = tmpVector.y;
        backgroundVertices[backgroundVerticesIndex++] = groundColor.toFloatBits();;
        backgroundVertices[backgroundVerticesIndex++] = startingTextureU;
        backgroundVertices[backgroundVerticesIndex++] = 0f;

        // Up Right
        backgroundVertices[backgroundVerticesIndex++] = Core.getInstance().getGraphicsManager().WIDTH;
        backgroundVertices[backgroundVerticesIndex++] = tmpVector.y;
        backgroundVertices[backgroundVerticesIndex++] = groundColor.toFloatBits();;
        backgroundVertices[backgroundVerticesIndex++] = finishingTextureU;
        backgroundVertices[backgroundVerticesIndex++] = 0f;

        // Down Right
        backgroundVertices[backgroundVerticesIndex++] = Core.getInstance().getGraphicsManager().WIDTH;
        backgroundVertices[backgroundVerticesIndex++] = tmpVector2.y;
        backgroundVertices[backgroundVerticesIndex++] = groundColor.toFloatBits();
        backgroundVertices[backgroundVerticesIndex++] = finishingTextureU;
        backgroundVertices[backgroundVerticesIndex++] = textureV;

        // Reset spikes vertices index
        spikesVerticesIndex = 0;
        // Update the spikes alpha
        if(GameLogic.getInstance().isLethalGround() && spikesAlpha < 0.6f)
            spikesAlpha += 0.05f;
        else if(!GameLogic.getInstance().isLethalGround() && spikesAlpha > 0f)
            spikesAlpha -= 0.05f;

        // Skip preparing if we have invisible spikes
        if(spikesAlpha <= 0f)
            return;

        // Get the texture's width position
        startingTextureU = Camera.getInstance().getPosition().x / 4f;
        finishingTextureU = (Camera.getInstance().getPosition().x + GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH))
                / 4f;

        textureV = 1f;
        // Convert to screen coordinates
        tmpVector.set(Camera.getInstance().worldToScreen(tmpVector.set(0f, 0.75f)));
        tmpVector2.set(Camera.getInstance().worldToScreen(tmpVector2.set(0f, 0f)));
        // Set the color
        groundColor.set(getEntitiesColor());
        // Set it to the spikes alpha
        groundColor.a = spikesAlpha;

        // Set the ground spikes vertices
        // Down Left
        spikesVertices[spikesVerticesIndex++] = 0f;
        spikesVertices[spikesVerticesIndex++] = tmpVector2.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = startingTextureU;
        spikesVertices[spikesVerticesIndex++] = textureV;

        // Up Left
        spikesVertices[spikesVerticesIndex++] = 0f;
        spikesVertices[spikesVerticesIndex++] = tmpVector.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = startingTextureU;
        spikesVertices[spikesVerticesIndex++] = 0f;

        // Up Right
        spikesVertices[spikesVerticesIndex++] = Core.getInstance().getGraphicsManager().WIDTH;
        spikesVertices[spikesVerticesIndex++] = tmpVector.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = finishingTextureU;
        spikesVertices[spikesVerticesIndex++] = 0f;

        // Down Right
        spikesVertices[spikesVerticesIndex++] = Core.getInstance().getGraphicsManager().WIDTH;
        spikesVertices[spikesVerticesIndex++] = tmpVector2.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = finishingTextureU;
        spikesVertices[spikesVerticesIndex++] = textureV;

        // Set the roof spikes vertices
        tmpVector.set(Camera.getInstance().worldToScreen(tmpVector.set(0f, Configuration.TERRAIN_ROOF_POSITION)));
        tmpVector2.set(Camera.getInstance().worldToScreen(tmpVector2.set(0f, Configuration.TERRAIN_ROOF_POSITION - 0.75f)));
        // Down Left
        spikesVertices[spikesVerticesIndex++] = 0f;
        spikesVertices[spikesVerticesIndex++] = tmpVector2.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = startingTextureU;
        spikesVertices[spikesVerticesIndex++] = 0f;

        // Up Left
        spikesVertices[spikesVerticesIndex++] = 0f;
        spikesVertices[spikesVerticesIndex++] = tmpVector.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = startingTextureU;
        spikesVertices[spikesVerticesIndex++] = textureV;

        // Up Right
        spikesVertices[spikesVerticesIndex++] = Core.getInstance().getGraphicsManager().WIDTH;
        spikesVertices[spikesVerticesIndex++] = tmpVector.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = finishingTextureU;
        spikesVertices[spikesVerticesIndex++] = textureV;

        // Down Right
        spikesVertices[spikesVerticesIndex++] = Core.getInstance().getGraphicsManager().WIDTH;
        spikesVertices[spikesVerticesIndex++] = tmpVector2.y;
        spikesVertices[spikesVerticesIndex++] = groundColor.toFloatBits();
        spikesVertices[spikesVerticesIndex++] = finishingTextureU;
        spikesVertices[spikesVerticesIndex++] = 0f;
    }

    private void prepareGroundRendering()
    {
        // Set the ground color
        groundColor.set(environmentColor);
        groundColor.a = 1f;
        // Reset ground vertices index
        groundVerticesIndex = 0;
        // Loop through all the terrain fixtures
        for(Fixture fixture : body.getFixtureList())
        {
            // Get the shape
            ChainShape shape = (ChainShape)fixture.getShape();
            // Skip roof fixtures
            if(fixture.getUserData().equals("Ground"))
            {
                // Get the shape vertices
                for(int i = 0; i < shape.getVertexCount(); i++)
                {
                    // Get the vertex
                    shape.getVertex(i, tmpVector);
                    // Convert it to screen coordinates
                    tmpVector.set(Camera.getInstance().worldToScreen(tmpVector));
                    // Add it to the ground vertices array
                    fillVertices(groundVertices, tmpVector, true);
                    // Make the ground vertices
                    if(i % 2 == 1)
                        makeGroundVertices();
                }
            }
            else if(fixture.getUserData().equals("Roof"))
            {
                // Get the shape vertices
                for(int i = 0; i < shape.getVertexCount(); i++)
                {
                    // Skip the third vertex (change mode rendering)
                    //if(i == 2)
                      //  break;

                    // Get the vertex
                    shape.getVertex(i, tmpVector);
                    // Convert it to screen coordinates
                    tmpVector.set(Camera.getInstance().worldToScreen(tmpVector));
                    // Add it to the ground vertices array
                    fillVertices(groundVertices, tmpVector, true);
                    // Make the ground vertices
                    if(i % 2 == 1)
                        makeRoofVertices();
                }
            }
        }
        // Prepare layers
        layerPosition.set(tmpVector2.set(layerSize.y / 2f, 0f));
        roofLayerPosition.set(Camera.getInstance().worldToScreen(tmpVector3.set(0f,
                GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING ? Configuration.TERRAIN_ROOF_POSITION :
                Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION)));

        layerPosition.y = Camera.getInstance().worldToScreen(tmpVector2.set(0f, 0f)).y - layerSize.y / 2f;
        roofLayerPosition.x = 0f;
        roofLayerPosition.y -= roofLayerSize.y;
    }

    private void drawGround()
    {
        // Validate the rendering
        if(groundVerticesIndex == 0)
            return;

        // Draw the ground
        Core.getInstance().getGraphicsManager().drawTexture(groundTexture, groundVertices, groundVerticesIndex);
        // Draw the layers
        Core.getInstance().getGraphicsManager().drawTextureRegion(layerTexture, layerPosition, layerSize, 270f);
        if(changeModePosition == 0 || Math.abs(Player.getInstance().getPosition().x - changeModePosition) > 50f)
            Core.getInstance().getGraphicsManager().drawTextureRegion(layerTexture, roofLayerPosition, roofLayerSize, 0f);
    }

    /*
        Every vertices fill will add 5 elements to the vertices array
        x, y, color, textureX, textureY
    */
    private void fillVertices(float[] vertices, Vector2 vertex, boolean isGround)
    {
        // Limit vertex
        if(vertex.x < 0)
            vertex.x = 0;
        else if(vertex.x > Core.getInstance().getGraphicsManager().WIDTH)
            vertex.x = Core.getInstance().getGraphicsManager().WIDTH;

        // Get the world vertex
        tmpVector4.set(Camera.getInstance().screenToWorld(vertex));
        // Add the vertex to the vertices array
        vertices[groundVerticesIndex++] = vertex.x;
        vertices[groundVerticesIndex++] = vertex.y;
        vertices[groundVerticesIndex++] = groundColor.toFloatBits();
        vertices[groundVerticesIndex++] = tmpVector4.x / TEXTURE_WIDTH;
        if(isGround)
            vertices[groundVerticesIndex++] = vertex.y == 0 ? 1 : 0;
        else
            vertices[groundVerticesIndex++] = vertex.y == Core.getInstance().getGraphicsManager().HEIGHT ?
                    1f : 0f;
    }

    /*
        Every vertices fill will add 5 elements to the vertices array
        x, y, color, textureX, textureY
    */
    private void fillVertices(float[] vertices, int index, Vector2 vertex, boolean isGround)
    {
        // Limit vertex
        if(vertex.x < 0)
            vertex.x = 0;
        else if(vertex.x > Core.getInstance().getGraphicsManager().WIDTH)
            vertex.x = Core.getInstance().getGraphicsManager().WIDTH;


        // Get the world vertex
        tmpVector4.set(Camera.getInstance().screenToWorld(vertex));
        // Add the vertex to the vertices array
        vertices[index] = vertex.x;
        vertices[index + 1] = vertex.y;
        vertices[index + 2] = groundColor.toFloatBits();
        vertices[index + 3] = tmpVector4.x / TEXTURE_WIDTH;
        if(isGround)
            vertices[index + 4] = vertex.y == 0 ? 1 : 0;
        else
            vertices[index + 4] = vertex.y == Core.getInstance().getGraphicsManager().HEIGHT ? 1f : 0f;
    }

    /*
        Because OpenGL/LibGdx renders meshes in forms of triangles, we need to specify the triangle
        sides as part of the rectangle (1 rectangle = 2 triangles)

         1          2/4          6             3/5
                          ==>
        3/5          6          1/4             2
    */
    private void makeGroundVertices()
    {
        // Find the needed vertices
        tmpVector2.set(groundVertices[groundVerticesIndex - 10], groundVertices[groundVerticesIndex - 9]);
        tmpVector3.set(groundVertices[groundVerticesIndex - 5], groundVertices[groundVerticesIndex - 4]);
        // Verify that the ground is visible on screen
        if(tmpVector3.x < 0 || tmpVector2.x > Core.getInstance().getGraphicsManager().WIDTH)
        {
            // Remove the points
            groundVerticesIndex -= 10;
            return;
        }
        // Make the triangle
        fillVertices(groundVertices, groundVerticesIndex - 10, tmpVector.set(tmpVector2.x, 0), true);
        fillVertices(groundVertices, groundVerticesIndex - 5, tmpVector2, true);
        fillVertices(groundVertices, tmpVector3, true);
        fillVertices(groundVertices, tmpVector.set(tmpVector3.x, 0), true);
    }

    private void makeRoofVertices()
    {
        // Find the needed vertices
        tmpVector2.set(groundVertices[groundVerticesIndex - 10], groundVertices[groundVerticesIndex - 9]);
        tmpVector3.set(groundVertices[groundVerticesIndex - 5], groundVertices[groundVerticesIndex - 4]);
        // Verify that the ground is visible on screen
        if(tmpVector3.x < 0 || tmpVector2.x > Core.getInstance().getGraphicsManager().WIDTH)
        {
            // Remove the points
            groundVerticesIndex -= 10;
            return;
        }
        // Make the triangle
        fillVertices(groundVertices, groundVerticesIndex - 10, tmpVector2, false);
        fillVertices(groundVertices, groundVerticesIndex - 5, tmpVector3, false);
        fillVertices(groundVertices, tmpVector.set(tmpVector3.x, Core.getInstance().getGraphicsManager().HEIGHT), false);
        fillVertices(groundVertices, tmpVector.set(tmpVector2.x, Core.getInstance().getGraphicsManager().HEIGHT), false);
    }

    public void toggleCaveMode(boolean toggle)
    {
        // Loop through all the objects and toggle cave mode
        for(GameObject object : objects)
            object.toggleCaveMode(toggle);

        // Set the cave model flag
        caveMode = toggle;
    }

    public void reset()
    {
        // Destroy the terrain's body
        GameWorld.getInstance().removeFromWorld(body);
        // Reset body instance
        body = null;
        // Reset exclusive flag
        isExclusive = false;
        // Reset last exclusive object instance
        lastExclusiveObject = null;
        // Reset the camera's right sided follow
        Camera.getInstance().toggleRightSidedFollow(false);
        // Remove all objects
        clearObjects();
    }

    public void destroy()
    {
        // Reset the terrain
        reset();
    }

    private void clearObjects()
    {
        // Loop through all the objects
        for(GameObject object : objects)
            object.destroy();

        // Clear the objects array
        objects.clear();
    }

    /*
       Will handle an eaten object
    */
    public void eatObject(Body object)
    {
        // Loop through all the bodies
        Iterator<GameObject> iterator = objects.iterator();
        while(iterator.hasNext())
        {
            // Get the object
            GameObject thisObject = iterator.next();
            // Is it the worm we're looking for ?
            if(thisObject.getBody() == object)
            {
                // Destroy it
                thisObject.destroy();
                // Remove it from the objects array
                iterator.remove();
                break;
            }
        }
    }

    public boolean isObject(Body body)
    {
        // Loop through all the terrain objects
        for(GameObject object : objects)
        {
            if(object.hasBody(body))
                return true;
        }
        return false;
    }

    public float getRoofPosition()
    {
        return GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING ? Configuration.TERRAIN_ROOF_POSITION :
                Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION;
    }

    public boolean isLevelMode() {
        return levelMode;
    }

    public Color getEntitiesColor() { return levelMode ? entitiesColor : GameIntelligence.getInstance().getEntitiesColor(); }

    public Color getEnvironmentColor() { return levelMode ? environmentColor : GameIntelligence.getInstance().getEnvironmentColor(); }

    public float getLevelFlameSpeed() {
        return levelFlameSpeed;
    }

    public ObjectsEntryType getObjectsEntryType() { return objectsEntryType; }

    public float getObjectsEntrySpeed() { return objectsEntrySpeed; }

    public static Terrain getInstance() { return instance; }
}
