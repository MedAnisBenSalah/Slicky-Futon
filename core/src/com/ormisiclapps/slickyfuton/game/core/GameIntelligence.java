package com.ormisiclapps.slickyfuton.game.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import com.ormisiclapps.slickyfuton.enumerations.DisplayType;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.enumerations.ObjectsEntryType;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ObjectType;

/**
 * Created by OrMisicL on 9/1/2016.
 */
public class GameIntelligence
{
    private Array<String> objectModels;
    private Array<String> gravityObjectModels;
    private Array<String> exclusiveObjectModels;
    private Array<String> terrainGroundObjectModels;
    private Array<String> terrainObjectModels;
    private boolean wasCave;
    private boolean wasExclusive;
    private int lastStaticCombinationId;
    private int lastSimpleCombinationId;
    private int lastComposedCombinationId;
    private int lastPartedCombinationId;
    private int lastChainCombinationId;
    private int lastGravityCombinationId;
    private Color environmentColor, entitiesColor, nextEnvironmentColor;
    private boolean lastUp;
    private int gravitySwitchPositionChain;
    private int updateTicks;

    private static GameIntelligence instance;

    public static final String[][] startingMessages =  {
            // 0 - 4
            new String[] {
                    "It's simple, just avoid the obstacles !",
                    "If it feels hard, try the \"Just Starting\" level.",
                    "All you had to do is to avoid the obstacles !",
            },

            // 5 - 9
            new String[] {
                    "Try harder!",
                    "Maybe this time!",
                    "You can do better!",
            },

            // 10 - 14
            new String[] {
                    "You're getting used to it.",
                    "Try even harder!",
            },

            // 15 - 19
            new String[] {
                    "You're alright!"
            },

            // 20 - 24
            new String[] {
                    "Keep going.",
                    "Push harder!",
            },

            // 25 - 29
            new String[] {
                    "Is that what you've got ?",
                    "Push harder!",
                    "Focus!"
            },

            // 30 - 34
            new String[] {
                    "Not bad!",
                    "Focus!"
            },

            // 35 - 39
            new String[] {
                    "Maybe you can do better this time.",
                    "Can you exceed 40 this time ?",
                    "Try to focus harder!",
            },

            // 40 - 44
            new String[] {
                    "Maybe you can do better this time.",
                    "Can you exceed 50 this time ?",
                    "Try to focus harder!",
            },

            // 45 - 49
            new String[] {
                    "Maybe you can do better this time.",
                    "Can you exceed 50 this time ?",
                    "One more little push",
            },

            // 50 - 100
            new String[] {
                    "Maybe you can do better.",
                    "It's not as easy as you expected it!",
                    "Focus is key!",
            },

            // 100+
            new String[] {
                    "You're a good player!",
                    "Bravo for your last performance!",
                    "It's easy for you isn't it ?",
            }

    };

    public static final Color[] shopColors = {
            // Blue variations
            new Color(0.18f, 0.92f, 0.93f, 1f), // Default light blue
            new Color(0.1373f, 0.9216f, 0.7648f, 1f), // Cyan
            new Color(0.2509f, 0.8784f, 0.8156f, 1f), // Turquoise
            new Color(0.5294f, 0.8078f, 0.7648f, 0.9215f), // Sky blue
            new Color(0f, 0.7490f, 1f, 1f), // Deep sky blue
            new Color(0.1176f, 0.5647f, 1f, 1f), // Dodger blue
            // Green variations
            new Color(0.4980f, 1f, 0f, 1f), // Light green
            new Color(0.6784f, 1f, 0.1843f, 1f), // Greeny yellow
            new Color(0f, 1f, 0.4980f, 1f), // Spring green
            new Color(0.5960f, 0.9843f, 0.5960f, 1f), // Pale green
            // Orange variations
            new Color(1f, 0.4980f, 0.3137f, 1f), // Coral
            new Color(1f, 0.3882f, 0.2784f, 1f), // Tomato
            new Color(1f, 0.2705f, 0f, 1f), // Orange red
            // Red variations
            new Color(0.9803f, 0.5019f, 0.4470f, 1f), // Salmon
            new Color(0.9411f, 0.5019f, 0.5019f, 1f), // Light coral
            new Color(0.9f, 0f, 0f, 1f), // Red
            // Yellow variations
            new Color(1f, 0.8941f, 0.7098f, 1f), // Moccasin
            new Color(0.9411f, 0.9019f, 0.5490f, 1f), // Khaki
            new Color(1f, 1f, 0.2f, 1f), // Light yellow
            // Pink variations
            new Color(1f, 0.7529f, 0.7960f, 1f), // Pink
            new Color(1f, 0.4117f, 0.7058f, 1f), // Hot pink
            new Color(0.8588f, 0.4392f, 0.5764f, 1f), // Pale violet red
            // Purple variations
            new Color(0.8470f, 0.7490f, 0.8470f, 1f), // Thistle
            new Color(0.9333f, 0.5098f, 0.9333f, 1f), // Violet
            // Brown variations
            new Color(0.8235f, 0.7058f, 0.5490f, 1f), // Tan
            new Color(0.9568f, 0.6431f, 0.3764f, 1f), // Sandy brown
            new Color(0.8235f, 0.4117f, 0.1176f, 1f), // Chocolate
            // Maroon variations
            new Color(0.8627f, 0.0784f, 0.2352f, 1f), // Crimson
            // Grey variations
            new Color(0.4666f, 0.5333f, 0.6f, 1f), // Lights late grey
            new Color(0.7529f, 0.7529f, 0.7529f, 1f), // Silver
            new Color(0.8627f, 0.8627f, 0.8627f, 1f), // Gainsboro
            // White variations
            new Color(0.9411f, 1f, 0.9411f, 1f), // Honeydew
            new Color(1f, 1f, 1f, 1f), // White
            // Black variations
            new Color(0f, 0f, 0f, 1f), // Black
    };

    public static final String[] colorNames = {
            "Light blue",
            "Cyan",
            "Turquoise",
            "Sky blue",
            "Deep sky blue",
            "Dodger blue",
            "Light green",
            "Greeny yellow",
            "Spring green",
            "Pale green",
            "Coral",
            "Tomato",
            "Orange red",
            "Salmon",
            "Light coral",
            "Red",
            "Moccasin",
            "Khaki",
            "Light yellow",
            "Pink",
            "Hot pink",
            "Pale violet red",
            "Thistle",
            "Violet",
            "Tan",
            "Sandy brown",
            "Chocolate",
            "Crimson",
            "Lights late grey",
            "Silver",
            "Gainsboro",
            "Honeydew",
            "White",
            "Black"
    };

    public static final int[] characterPrices = {
            0,
            10,
            10,
            10,
            10,
            10,
            10,
            10,
            10,
            10,
            20,
            20,
            20,
            20,
            10,
            20,
            35,
            35,
            35,
            35,
            35,
            40,
            40,
            45,
            45,
            45,
            40,
            40,
            40,
            50,
            50,
            50
    };

    public static final int[] tailSkinPrices = {
            0,
            40,
            55,
            70,
            90
    };

    public static final int COLOR_PRICE = 25;

    private static final Color[] environmentColors = {
            //new Color(0.6f, 0.6f, 0.6f, 1f), // Grey
            new Color(0.8627f, 0.0784f, 0.2352f, 1f), // Red
            new Color(0.2f, 0.4f, 0.6f, 1f), // Blue
            new Color(0.0980f, 0.5058f, 0.0980f, 1f), // Green
            new Color(0.42f, 0f, 0.42f, 1f), // Purple
            new Color(0f, 0.5450f, 0.5450f, 1f) // Cyan
    };

    private static final float COLOR_TRANSITION_PER_FRAME = 0.05f;

    public GameIntelligence()
    {
        // Create arrays
        objectModels = new Array<String>();
        gravityObjectModels = new Array<String>();
        exclusiveObjectModels = new Array<String>();
        terrainGroundObjectModels = new Array<String>();
        terrainObjectModels = new Array<String>();
        // Reset flags
        wasExclusive = false;
        // Reset values
        lastStaticCombinationId = -1;
        lastSimpleCombinationId = -1;
        lastComposedCombinationId = -1;
        lastPartedCombinationId = -1;
        lastChainCombinationId = -1;
        lastGravityCombinationId = -1;
        updateTicks = 0;
        // Create colors
        environmentColor = new Color();
        entitiesColor = new Color();
        // Initialize object model
        initializeObjectModels();
        // Set the instance
        instance = this;
    }

    private void initializeObjectModels()
    {
        // Loop through all the model's count
        for(int i = 0; i < Core.getInstance().getModelSettings().getModelsCount(); i++)
        {
            // Get the model
            String model = Core.getInstance().getModelSettings().getModel(i);
            // Add it to the appropriate array
            if(Core.getInstance().getModelSettings().getModelAttribute(model, "type").equals("Object"))
            {
                objectModels.add(model);
                gravityObjectModels.add(model);
            }
            else if(Core.getInstance().getModelSettings().getModelAttribute(model, "type").equals("GravityObject"))
                gravityObjectModels.add(model);
            else if(Core.getInstance().getModelSettings().getModelAttribute(model, "type").equals("ExclusiveObject"))
                exclusiveObjectModels.add(model);
            else if(Core.getInstance().getModelSettings().getModelAttribute(model, "type").equals("TerrainGroundObject"))
                terrainGroundObjectModels.add(model);
            else if(Core.getInstance().getModelSettings().getModelAttribute(model, "type").equals("TerrainObject"))
                terrainObjectModels.add(model);
        }
    }

    public void initialize()
    {
        // Reset flags
        wasCave = false;
        wasExclusive = false;
        lastUp = false;
        // Reset values
        lastStaticCombinationId = -1;
        lastSimpleCombinationId = -1;
        lastComposedCombinationId = -1;
        lastChainCombinationId = -1;
        lastGravityCombinationId = -1;
        gravitySwitchPositionChain = 0;
        updateTicks = 0;
        // Initialize colors
        environmentColor = environmentColors[MathUtils.random(environmentColors.length - 1)].cpy();
        // Reset instances
        nextEnvironmentColor = null;
    }

    /*
        Will find an appropriate object type
    */
    public ObjectType findObjectType()
    {
        ObjectType result;
        // Get the score
        long score = GameLogic.getInstance().getScore();
        // Calculate the object type's range
        int objectTypeRangeStart = 0;
        int objectTypeRangeEnd = 10;
        if(score >= Configuration.LOWEST_MOVABLE_SCORE)
        {
            objectTypeRangeStart += 3;
            objectTypeRangeEnd += 10;
        }
        if(score >= Configuration.LOWEST_COMPOSED_SCORE)
        {
            objectTypeRangeStart += 2;
            objectTypeRangeEnd += 10;
        }
        if(score >= Configuration.LOWEST_PARTED_SCORE)
        {
            objectTypeRangeStart += 3;
            objectTypeRangeEnd += 10;
        }
        if(score >= Configuration.LOWEST_CHAIN_SCORE)
        {
            objectTypeRangeStart += 1;
            objectTypeRangeEnd += 10;
        }
        // Find the object type
        int objectType = MathUtils.random(objectTypeRangeStart, objectTypeRangeEnd - 1);
        // Choose the object type
        if(objectType < 10)
            result = ObjectType.OBJECT_TYPE_STATIC;
        else if(objectType < 20)
            result = ObjectType.OBJECT_TYPE_MOVABLE;
        else if(objectType < 30)
            result = ObjectType.OBJECT_TYPE_COMPOSED;
        else if(objectType < 40)
            result = ObjectType.OBJECT_TYPE_PARTED;
        else
            result = ObjectType.OBJECT_TYPE_CHAIN;

        return result;
    }

    /*
       Will find the appropriate position for the passed string
    */
    public static float getPositionFromName(String position)
    {
        // Return the fixed position
        if(position.equals("Down"))
            return Configuration.TERRAIN_ROOF_POSITION / 4;
        else if(position.equals("Up"))
            return Configuration.TERRAIN_ROOF_POSITION / 4 * 3;
        else
            return Configuration.TERRAIN_ROOF_POSITION / 2;
    }

    /*
       Will find the appropriate position for the passed string
    */
    public static float getGravityPositionFromName(String position)
    {
        // Return the fixed position
        if (position.equals("Down"))
            return 0f;
        else if(position.equals("Up"))
            return Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION;
        else
            return Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION / 2;
    }

    /*
        Will return the appropriate string for the current display type
    */
    public static String getDisplayType()
    {
        // Return the appropriate string
        DisplayType displayType = Core.getInstance().getResourcesManager().getDisplayType();
        if(displayType == DisplayType.DISPLAY_TYPE_HD)
            return "High";
        else if(displayType == DisplayType.DISPLAY_TYPE_MD)
            return "Medium";
        else
            return "Low";
    }

    /*
       Will return the appropriate entry type for the given name
    */
    public static ObjectsEntryType getObjectsEntryTypeFromName(String name)
    {
        if(name.equals("ScrollUp"))
            return ObjectsEntryType.OBJECTS_ENTRY_SCROLL_UP;
        else if(name.equals("ScrollDown"))
            return ObjectsEntryType.OBJECTS_ENTRY_SCROLL_DOWN;
        else if(name.equals("FadeIn"))
            return ObjectsEntryType.OBJECTS_ENTRY_FADE_IN;
        else
            return ObjectsEntryType.OBJECTS_ENTRY_NORMAL;
    }

    /*
        Will find an appropriate (vertical) position for the upcoming object
    */
    public float findObjectPosition(String model)
    {
        // If the object has a fixed position then return it
        if(Core.getInstance().getModelSettings().doesModelAttributeExist(model, "fixedPosition"))
        {
            // Get the fixed position
            String fixedPosition = Core.getInstance().getModelSettings().getModelAttribute(model, "fixedPosition");
            // Return the fixed position
            return getPositionFromName(fixedPosition);
        }

        // Choose a random position (down, middle, up)
        int place = MathUtils.random(0, 2);
        // Return the position depending on its place
        if(place == 0)
            return Configuration.TERRAIN_ROOF_POSITION / 4;
        else if(place == 1)
            return Configuration.TERRAIN_ROOF_POSITION / 2;
        else
            return Configuration.TERRAIN_ROOF_POSITION / 4 * 3;
    }

    /*
        Will find an appropriate (vertical) position for the upcoming object
    */
    public float findObjectPosition(String model, float excludeY)
    {
        // Get a random object position
        float position = findObjectPosition(model);
        // Exclude the y
        while(position == excludeY)
            position = findObjectPosition(model);

        return position;
    }

    /*
        Will find an appropriate static combination id
    */
    public int findStaticCombination()
    {
        // Get the combinations count
        int combinationsCount = Core.getInstance().getPreSetCombinations().getStaticCombinationsCount();
        // Don't allow redundant combinations
        int combinationId;
        do
        {
            // Find a random movement (different from 0 as its the default static movement)
            combinationId = MathUtils.random(0, combinationsCount - 1);
        } while(combinationId == lastStaticCombinationId);
        // Return the static combination
        return (lastStaticCombinationId = combinationId);
    }

    /*
        Will find an appropriate simple combination id
    */
    public int findSimpleCombination()
    {
        // Get the combinations count
        int combinationsCount = Core.getInstance().getPreSetCombinations().getSimpleCombinationsCount();
        // Don't allow redundant combinations
        int combinationId;
        do
        {
            // Find a random movement (different from 0 as its the default static movement)
            combinationId = MathUtils.random(0, combinationsCount - 1);
        } while(combinationId == lastSimpleCombinationId);
        // Return the simple combination
        return (lastSimpleCombinationId = combinationId);
    }

    /*
        Will find an appropriate composed combination id
    */
    public int findComposedCombination()
    {
        // Get the combinations count
        int combinationsCount = Core.getInstance().getPreSetCombinations().getComposedCombinationsCount();
        // Don't allow redundant combinations
        int combinationId;
        do
        {
            // Find a random movement (different from 0 as its the default static movement)
            combinationId = MathUtils.random(0, combinationsCount - 1);
        } while(combinationId == lastComposedCombinationId);
        // Return the composed combination
        return (lastComposedCombinationId = combinationId);
    }

    /*
        Will find an appropriate chain combination id
    */
    public int findChainCombination()
    {
        // Get the combinations count
        int combinationsCount = Core.getInstance().getPreSetCombinations().getChainCombinationsCount();
        // Don't allow redundant combinations
        int combinationId;
        do
        {
            // Find a random combination id
            combinationId = MathUtils.random(0, combinationsCount - 1);
        } while(combinationId == lastChainCombinationId);
        // Return the chain combination
        return (lastChainCombinationId = combinationId);
    }

    /*
        Will find an appropriate parted combination id
    */
    public int findPartedCombination()
    {
        // Get the combinations count
        int combinationsCount = Core.getInstance().getPreSetCombinations().getPartedCombinationsCount();
        // Don't allow redundant combinations
        int combinationId;
        do
        {
            // Find a random movement
            combinationId = MathUtils.random(0, combinationsCount - 1);
        } while(combinationId == lastPartedCombinationId);
        // Return the parted combination
        return (lastPartedCombinationId = combinationId);
    }

    /*
       Will find an appropriate gravity combination id
    */
    public int findGravityCombination()
    {
        // Get the combinations count
        int combinationsCount = Core.getInstance().getPreSetCombinations().getGravityCombinationsCount();
        // Don't allow redundant combinations
        int combinationId;
        do
        {
            // Find a random combination
            combinationId = MathUtils.random(0, combinationsCount - 1);
        } while(combinationId == lastGravityCombinationId);
        // Return the gravity combination
        return (lastGravityCombinationId = combinationId);
    }

    /*
        Will find an random terrain model for the upcoming object
    */
    public String findTerrainObjectModel()
    {
        // Choose a random model id
        int model = MathUtils.random(0, terrainObjectModels.size - 1);
        // Return the model
        return terrainObjectModels.get(model);
    }

    /*
       Will find an random model for the upcoming object
    */
    public String findObjectModel(ObjectType objectType)
    {
        // Find a random exclusive model
        if (objectType == ObjectType.OBJECT_TYPE_EXCLUSIVE)
            return exclusiveObjectModels.get(MathUtils.random(0, exclusiveObjectModels.size - 1));

        // Choose a random model id
        int model = MathUtils.random(0, objectModels.size - 1);
        // Return the model
        String objectModel = objectModels.get(model);
        // Validate the model's score
        if (Core.getInstance().getModelSettings().getModelAttributeAsInt(objectModel, "score") > GameLogic.getInstance().getScore())
            return findObjectModel(objectType);
        else
            return objectModel;
    }

    /*
      Will find an random gravity model for the upcoming object
    */
    public String findGravityObjectModel()
    {
        // Choose a random model id
        int model = MathUtils.random(0, gravityObjectModels.size - 1);
        // Return the model
        return gravityObjectModels.get(model);
    }

    /*
        Will decide whether or not we should create multiple objects (block)
    */
    public boolean createMultipleObjects(String model)
    {
        // Check if the model accepts multiple objects
        return Core.getInstance().getModelSettings().getModelAttributeAsBoolean(model, "multiple") && MathUtils.random(0, 10) > 7;
    }

    /*
        Will decide whether or not the laser is facing down depending on its given position
    */
    public boolean isLaserDown(Vector2 position)
    {
        return position.y > Configuration.TERRAIN_ROOF_POSITION / 2;
    }

    /*
        Will decide whether or not to create the coin
    */
    public boolean shouldCreateCoin()
    {
        return MathUtils.random(10) >= 3;
    }

    /*
       Will decide whether or not we'll create terrain objects
    */
    public boolean isGenerateTerrainObjects()
    {
        return MathUtils.random(30) > 25;
    }

    /*
       Will decide whether or not to change the game mode
    */
    public boolean isGravitySwitchMode()
    {
        return GameLogic.getInstance().getGameMode() != GameMode.GAME_MODE_GRAVITY_SWITCH && MathUtils.random(30) > 22;
    }

    /*
        Will decide whether or not the next object is gonna be exclusive
    */
    public boolean isExclusive()
    {
        // Wait until we reach the required score and finish the cave mode
        if(GameLogic.getInstance().getScore() < Configuration.EXCLUSIVE_STATE_MINIMUM_SCORE ||
                GameLogic.getInstance().getGameMode() != GameMode.GAME_MODE_FLYING || wasCave)
            return false;

        // Skip if we were in exclusive state
        if(wasExclusive)
        {
            // Reset exclusive flag
            wasExclusive = false;
            return false;
        }
        // Find if we're getting into exclusive mode
        return wasExclusive = MathUtils.random(30) >= 26;
    }

    /*
        Will decide whether or not we'll get back to flying mode
    */
    public boolean isBackToFlyingMode()
    {
        return MathUtils.random(10) >= 5;
    }

    /*
        Will decide whether or not we'll enter a cave mode
        NOTE: Cave mode is disabled until a further update, just activate this flag and fix few more problems
    */
    public boolean isCaveMode()
    {
        // Wait until we reach the required score and finish the exclusive state
       /* if(GameLogic.getInstance().getScore() < Configuration.CAVE_MODE_MINIMUM_SCORE || wasExclusive)
            return false;

        // Skip if we were in cave mode
        if(wasCave)
        {
            // Reset cave flag
            wasCave = false;
            return false;
        }
        // Find if we're getting into cave mode
        return (wasCave = MathUtils.random(30) >= 28);*/
       return false;
    }

    /*
        Will decide whether or not we'll use a static combination instead of random objects
    */
    public boolean isStaticCombination()
    {
        return GameLogic.getInstance().getScore() >= Configuration.LOWEST_STATIC_COMBINATION_SCORE && MathUtils.randomBoolean();
    }

    /*
        Will decide whether or not we'll use a gravity combination instead of random objects
    */
    public boolean isGravityCombination()
    {
        return MathUtils.randomBoolean();
    }

    /*
        Will calculate the player's velocity based on the score
    */
    public float getPlayerVelocity()
    {
        // If we've passed the maximum velocity score then don't calculate
        if(GameLogic.getInstance().getScore() >= Configuration.MAXIMUM_VELOCITY_SCORE)
            return Configuration.PLAYER_VELOCITY;

        // Calculate the score factor
        float scoreFactor = (float)GameLogic.getInstance().getScore() / (float)Configuration.MAXIMUM_VELOCITY_SCORE;
        // Calculate the velocity factor
        float velocityFactor = 0.5f * scoreFactor;
        // Calculate the velocity
        return Configuration.PLAYER_VELOCITY * 0.5f + Configuration.PLAYER_VELOCITY * velocityFactor;
    }

    /*
        Will calculate the object's entry speed based on the score
    */
    public float getObjectsEntrySpeed()
    {
        // If we've passed the maximum velocity score then don't calculate
        if(GameLogic.getInstance().getScore() >= Configuration.MAXIMUM_VELOCITY_SCORE)
            return Configuration.OBJECTS_ENTRY_SPEED;

        // Calculate the score factor
        float scoreFactor = (float)GameLogic.getInstance().getScore() / (float)Configuration.MAXIMUM_VELOCITY_SCORE;
        // Calculate the velocity factor
        float velocityFactor = 0.5f * scoreFactor;
        // Calculate the velocity
        return Configuration.OBJECTS_ENTRY_SPEED * 0.5f + Configuration.OBJECTS_ENTRY_SPEED * velocityFactor;
    }

    /*
        Will randomly select a new entry type
    */
    public ObjectsEntryType updateObjectsEntryType(ObjectsEntryType currentEntryType)
    {
        ObjectsEntryType newEntryType;
        do {
            // Find a random number
            int newType = MathUtils.random(2);
            // Get the new entry type
            newEntryType = ObjectsEntryType.values()[newType + 1];
        } while(currentEntryType == newEntryType);
        return newEntryType;
    }

    /*
        Will return a message to show at the beginning of the game
    */
    public String getStartingMessage()
    {
        long lastScore = GameLogic.getInstance().getLastScore();
        if(lastScore < 50)
            return GameLogic.getInstance().isFirstGame() ?
                    startingMessages[(int)(lastScore / 5)][MathUtils.random(startingMessages[(int)(lastScore / 5)].length - 2)] :
                    startingMessages[(int)(lastScore / 5)][MathUtils.random(startingMessages[(int)(lastScore / 5)].length - 1)];
        else if(lastScore >= 50 && lastScore < 100)
            return startingMessages[10][MathUtils.random(startingMessages[10].length - 1)];
        else
            return startingMessages[11][MathUtils.random(startingMessages[11].length - 1)];
    }

    /*
        Will decide if the next gravity mode object is up
    */
    public boolean isGravityModeObjectUp()
    {
        // Find if its up
        boolean isUp = MathUtils.randomBoolean();
        // Check against the last choice
        if(isUp == lastUp)
            // Increase the number of position chain
            gravitySwitchPositionChain++;
        else
            // Reset the number of position chain
            gravitySwitchPositionChain = 0;

        // Limit the chain
        if(gravitySwitchPositionChain == 4)
        {
            // Switch the position
            isUp = !isUp;
            // Reset the number of position chain
            gravitySwitchPositionChain = 0;
        }
        return (lastUp = isUp);
    }

    public boolean isJustUpdatedColors()
    {
        return updateTicks == 0;
    }

    public boolean isGravityModeObjectPassable()
    {
        return gravitySwitchPositionChain == 0;
    }

    public void update()
    {
        // Update only if we reached the target update ticks
        updateTicks++;
        if(updateTicks < 5)
            return;

        // Reset update ticks
        updateTicks = 0;
        // Find a new environment color
        int colorId = MathUtils.random(environmentColors.length - 1);
        while(environmentColor.equals(environmentColors[colorId]))
            colorId = MathUtils.random(environmentColors.length - 1);

        // Set the next environment color
        nextEnvironmentColor = environmentColors[colorId].cpy();
    }

    public void process()
    {
        // Transit the color if necessary
        if(nextEnvironmentColor != null)
        {
            // Slowly transit colors
            // Red
            if(nextEnvironmentColor.r > environmentColor.r)
            {
                environmentColor.r += COLOR_TRANSITION_PER_FRAME;
                if (environmentColor.r > nextEnvironmentColor.r)
                    environmentColor.r = nextEnvironmentColor.r;
            }
            else if(nextEnvironmentColor.r < environmentColor.r)
            {
                environmentColor.r -= COLOR_TRANSITION_PER_FRAME;
                if (environmentColor.r < nextEnvironmentColor.r)
                    environmentColor.r = nextEnvironmentColor.r;
            }

            // Green
            if(nextEnvironmentColor.g > environmentColor.g)
            {
                environmentColor.g += COLOR_TRANSITION_PER_FRAME;
                if (environmentColor.g >= nextEnvironmentColor.g)
                    environmentColor.g = nextEnvironmentColor.g;
            }
            else if(nextEnvironmentColor.g < environmentColor.g)
            {
                environmentColor.g -= COLOR_TRANSITION_PER_FRAME;
                if (environmentColor.g <= nextEnvironmentColor.g)
                    environmentColor.g = nextEnvironmentColor.g;
            }

            // Blue
            if(nextEnvironmentColor.b > environmentColor.b)
            {
                environmentColor.b += COLOR_TRANSITION_PER_FRAME;
                if (environmentColor.b >= nextEnvironmentColor.b)
                    environmentColor.b = nextEnvironmentColor.b;
            }
            else if(nextEnvironmentColor.b < environmentColor.b)
            {
                environmentColor.b -= COLOR_TRANSITION_PER_FRAME;
                if (environmentColor.b <= nextEnvironmentColor.b)
                    environmentColor.b = nextEnvironmentColor.b;
            }

            // Did we finish the transition ?
            if(nextEnvironmentColor.equals(environmentColor))
                nextEnvironmentColor = null;
        }
    }

    public Color getEnvironmentColor()
    {
        return environmentColor;
    }

    public Color getEntitiesColor()
    {
        // Grey is a special case
        /*if(environmentColor.equals(environmentColors[0]))
            return entitiesColor.set(0.9686f, 0.0784f, 0.0784f, 1f);
        else*/
            return entitiesColor.set(1f - environmentColor.r, 1f - environmentColor.g, 1f - environmentColor.b, 1f);
    }

    public static Color getRedColor() { return environmentColors[0]; }

    public static GameIntelligence getInstance()
    {
        return instance;
    }
}
