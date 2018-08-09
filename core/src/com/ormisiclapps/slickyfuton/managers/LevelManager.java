package com.ormisiclapps.slickyfuton.managers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.nodes.level.LevelEventNode;
import com.ormisiclapps.slickyfuton.game.nodes.level.LevelNode;

/**
 * Created by OrMisicL on 9/7/2017.
 */

public class LevelManager
{
    private Array<LevelNode> loadedLevels;
    private int levelsCount;

    public LevelManager()
    {
        // Create the loaded levels array
        loadedLevels = new Array<LevelNode>();
    }

    /*
        This will load all available levels to the game
    */
    public void loadAllLevels()
    {
        // Parse the settings file
        XmlReader.Element rootElement = Core.getInstance().getFileManager().parseLevelsFile();
        if(rootElement == null)
            return;

        // Save the levels count
        levelsCount = rootElement.getInt("count");
        // Load all level's basic information
        for(int i = 0; i < levelsCount; i++)
        {
            // Load the level
            LevelNode node = loadLevel(i + 1);
            // Add it to the loaded levels array
            if(node != null)
                loadedLevels.add(node);
        }
    }

    /*
        Will load the level's OAC file and store it's general settings
        NOTE: This will only load the level's general information
    */
    public LevelNode loadLevel(int levelId)
    {
        // Parse the level's file
        XmlReader.Element rootElement = Core.getInstance().getFileManager().parseLevelFile("Level" + levelId);
        if(rootElement == null)
            return null;

        // Validate the general element
        XmlReader.Element generalElement = rootElement.getChildByName("General");
        if(generalElement == null)
            return null;

        // Get the level's parameters
        String name = generalElement.get("name");
        String musicFile = generalElement.get("musicFile");
        String difficulty = generalElement.get("difficulty");
        int reward = generalElement.getInt("reward");
        String musicName = generalElement.get("musicName");
        String musicAuthor = generalElement.get("musicAuthor");
        String message = generalElement.get("message");
        float bR = generalElement.getFloat("backgroundColorR");
        float bG = generalElement.getFloat("backgroundColorG");
        float bB = generalElement.getFloat("backgroundColorB");
        float bA = generalElement.getFloat("backgroundColorA");
        float eR = generalElement.getFloat("entitiesColorR");
        float eG = generalElement.getFloat("entitiesColorG");
        float eB = generalElement.getFloat("entitiesColorB");
        float eA = generalElement.getFloat("entitiesColorA");
        float playerSpeed = generalElement.getFloat("playerSpeed");
        String objectsEntryType = generalElement.get("objectsEntryAnimationType");
        float objectsEntrySpeed = generalElement.getFloat("objectsEntryAnimationSpeed");
        // Validate the objects element
        XmlReader.Element objectsElement = rootElement.getChildByName("Objects");
        if(objectsElement == null)
            return null;

        // Validate the events element
        XmlReader.Element eventsElement = rootElement.getChildByName("Events");
        if(eventsElement == null)
            return null;

        // Get the object's count
        int objectsCount = objectsElement.getChildCount();
        int eventsCount = eventsElement.getChildCount();
        // Create the level node parameters
        LevelNode levelNode = new LevelNode(levelId, objectsCount, eventsCount, name, difficulty, musicFile, musicName, musicAuthor, message,
                reward, bR, bG, bB, bA, eR, eG, eB, eA, playerSpeed, GameIntelligence.getObjectsEntryTypeFromName(objectsEntryType),
                objectsEntrySpeed);

        // Loop through the objects
        for(int i = 0; i < objectsCount; i++)
        {
            // Get the object element
            XmlReader.Element objectElement = objectsElement.getChild(i);
            // Get the object's parameters
            String model = objectElement.getChildByName("Model").get("name");
            float positionX = objectElement.getChildByName("Position").getFloat("x");
            float positionY = objectElement.getChildByName("Position").getFloat("y");
            float sizeX = objectElement.getChildByName("Size").getFloat("x");
            float sizeY = objectElement.getChildByName("Size").getFloat("y");
            int movementId = objectElement.getChildByName("Movement").getInt("id");
            int startingPoint = objectElement.getChildByName("Movement").getInt("startingPoint");
            float speed = objectElement.getChildByName("Movement").getFloat("speed");
            // Add the object
            levelNode.addObject(i, model, positionX, positionY, sizeX, sizeY, movementId, startingPoint, speed);
        }

        // Loop through the events
        for(int i = 0; i < eventsCount; i++)
        {
            // Get the event element
            XmlReader.Element element = eventsElement.getChild(i);
            // Get the object's parameters
            LevelEventNode.LevelEventType type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_BACKGROUND_TRANSITION;
            if(element.getName().equals("SpeedChange"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_SPEED_CHANGE;
            else if(element.getName().equals("TakeControl"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_TAKE_CONTROL;
            else if(element.getName().equals("RestoreControl"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_RESTORE_CONTROL;
            else if(element.getName().equals("DisplayText"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_DISPLAY_TEXT;
            else if(element.getName().equals("ToggleLethalGround"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_TOGGLE_LETHAL_GROUND;
            else if(element.getName().equals("EntitiesColorTransition"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_ENTITIES_COLOR_TRANSITION;
            else if(element.getName().equals("EnterExclusiveState"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_ENTER_EXCLUSIVE_STATE;
            else if(element.getName().equals("SetObjectsEntryAnimation"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_SET_OBJECTS_ENTRY_ANIMATION;
            else if(element.getName().equals("LevelCompleted"))
                type = LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_LEVEL_COMPLETED;

            float position = element.getFloat("position");
            // Add the event
            switch(type)
            {
                case LEVEL_EVENT_TYPE_BACKGROUND_TRANSITION:
                {
                    float r = element.getFloat("r");
                    float g = element.getFloat("g");
                    float b = element.getFloat("b");
                    float a = element.getFloat("a");
                    int transitDuration = element.getInt("transitDuration");
                    levelNode.addBackgroundTransitionEvent(i, position, r, g, b, a, transitDuration);
                    break;
                }
                case LEVEL_EVENT_TYPE_SPEED_CHANGE:
                {
                    float speed = element.getFloat("speed");
                    levelNode.addSpeedChangeEvent(i, position, speed);
                    break;
                }

                case LEVEL_EVENT_TYPE_TAKE_CONTROL:
                {
                    float positionY = element.getFloat("positionY");
                    float speed = element.getFloat("speed");
                    float shuffleDistance = element.getFloat("shuffleDistance");
                    levelNode.addTakeControlEvent(i, position, positionY, speed, shuffleDistance);
                    break;
                }

                case LEVEL_EVENT_TYPE_RESTORE_CONTROL:
                {
                    levelNode.addRestoreControlEvent(i, position);
                    break;
                }

                case LEVEL_EVENT_TYPE_DISPLAY_TEXT:
                {
                    String text = element.get("text");
                    float screenPositionX = element.getFloat("screenPositionX");
                    float screenPositionY = element.getFloat("screenPositionY");
                    float endPosition = element.getFloat("endPosition");
                    levelNode.addDisplayTextEvent(i, position, text, screenPositionX, screenPositionY, endPosition);
                    break;
                }

                case LEVEL_EVENT_TYPE_TOGGLE_LETHAL_GROUND:
                {
                    boolean toggle = element.getBoolean("toggle");
                    levelNode.addLevelToggleLethalGroundEvent(i, position, toggle);
                    break;
                }

                case LEVEL_EVENT_TYPE_ENTITIES_COLOR_TRANSITION:
                {
                    float r = element.getFloat("r");
                    float g = element.getFloat("g");
                    float b = element.getFloat("b");
                    float a = element.getFloat("a");
                    int transitDuration = element.getInt("transitDuration");
                    levelNode.addEntitiesColorTransitionEvent(i, position, r, g, b, a, transitDuration);
                    break;
                }

                case LEVEL_EVENT_TYPE_ENTER_EXCLUSIVE_STATE:
                {
                    int objects = element.getInt("objects");
                    float speed = element.getFloat("speed");
                    levelNode.addEnterExclusiveStateEvent(i, position, objects, speed);
                    break;
                }

                case LEVEL_EVENT_TYPE_SET_OBJECTS_ENTRY_ANIMATION:
                {
                    String entryType = element.get("type");
                    float speed = element.getFloat("speed");
                    levelNode.addSetObjectsEntryAnimationEvent(i, position, GameIntelligence.getObjectsEntryTypeFromName(entryType), speed);
                    break;
                }

                case LEVEL_EVENT_TYPE_LEVEL_COMPLETED:
                {
                    levelNode.addLevelCompletedEvent(i, position);
                    break;
                }
            }

        }
        return levelNode;
    }

    public void reloadLevel(LevelNode node)
    {
        // Load the level
        loadLevel(node.id);
        // Set it in the loaded levels array
        loadedLevels.set(node.id - 1, node);
    }

    public int getLevelsCount() { return levelsCount; }
    public LevelNode getLevelNode(int id) { return loadedLevels.get(id); }
}
