package com.ormisiclapps.slickyfuton.game.nodes.level;

import com.ormisiclapps.slickyfuton.enumerations.ObjectsEntryType;

/**
 * Created by OrMisicL on 9/7/2017.
 */

public class LevelNode
{
    // General
    public int id;
    public String name, difficulty, musicFile, musicName, musicAuthor, message;
    public int reward;
    public float r, g, b, a;
    public float entitiesColorR, entitiesColorG, entitiesColorB, entitiesColorA;
    public float playerSpeed;
    public ObjectsEntryType objectsEntryType;
    public float objectsEntrySpeed;
    public LevelObjectNode[] objectNodes;
    public LevelEventNode[] eventNodes;

    public LevelNode(int id, int objectsCount, int eventsCount, String name, String difficulty, String musicFile, String musicName,
                     String musicAuthor, String message, int reward, float r, float g, float b, float a, float entitiesColorR,
                     float entitiesColorG, float entitiesColorB, float entitiesColorA, float playerSpeed, ObjectsEntryType objectsEntryType,
                     float objectsEntrySpeed)
    {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.musicFile = musicFile;
        this.musicName = musicName;
        this.musicAuthor = musicAuthor;
        this.message = message;
        this.reward = reward;
        this.playerSpeed = playerSpeed;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.entitiesColorR = entitiesColorR;
        this.entitiesColorG = entitiesColorG;
        this.entitiesColorB = entitiesColorB;
        this.entitiesColorA = entitiesColorA;
        this.objectsEntryType = objectsEntryType;
        this.objectsEntrySpeed = objectsEntrySpeed;
        // Create the objects array
        objectNodes = new LevelObjectNode[objectsCount];
        // Create the events array
        eventNodes = new LevelEventNode[eventsCount];
    }

    public void addObject(int id, String model, float positionX, float positionY, float sizeX, float sizeY, int movementId, int startingPoint,
                          float speed)
    {
        // Create the object node
        objectNodes[id] = new LevelObjectNode(model, positionX, positionY, sizeX, sizeY, movementId, startingPoint, speed);
    }

    public void addBackgroundTransitionEvent(int id, float position, float r, float g, float b, float a, int transitDuration)
    {
        // Create the event node
        eventNodes[id] = new LevelBackgroundTransitionEventNode(position, r, g, b, a, transitDuration);
    }

    public void addSpeedChangeEvent(int id, float position, float speed)
    {
        // Create the event node
        eventNodes[id] = new LevelSpeedChangeEventNode(position, speed);
    }

    public void addTakeControlEvent(int id, float position, float positionY, float speed, float shuffleDistance)
    {
        // Create the event node
        eventNodes[id] = new LevelTakeControlEventNode(position, positionY, speed, shuffleDistance);
    }

    public void addRestoreControlEvent(int id, float position)
    {
        // Create the event node
        eventNodes[id] = new LevelRestoreControlEventNode(position);
    }

    public void addDisplayTextEvent(int id, float position, String text, float screenPositionX, float screenPositionY, float endDistance)
    {
        // Create the event node
        eventNodes[id] = new LevelDisplayTextEventNode(position, text, screenPositionX, screenPositionY, endDistance);
    }

    public void addLevelToggleLethalGroundEvent(int id, float position, boolean toggle)
    {
        // Create the event node
        eventNodes[id] = new LevelToggleLethalGroundEventNode(position, toggle);
    }

    public void addEntitiesColorTransitionEvent(int id, float position, float r, float g, float b, float a, int transitDuration)
    {
        // Create the event node
        eventNodes[id] = new LevelEntitiesColorTransitionEventNode(position, r, g, b, a, transitDuration);
    }

    public void addEnterExclusiveStateEvent(int id, float position, int objects, float speed)
    {
        // Create the event node
        eventNodes[id] = new LevelEnterExclusiveStateEventNode(position, objects, speed);
    }

    public void addSetObjectsEntryAnimationEvent(int id, float position, ObjectsEntryType type, float speed)
    {
        // Create the event node
        eventNodes[id] = new LevelSetObjectsEntryAnimationEventNode(position, type, speed);
    }

    public void addLevelCompletedEvent(int id, float position)
    {
        // Create the event node
        eventNodes[id] = new LevelCompletedEvent(position);
    }
}
