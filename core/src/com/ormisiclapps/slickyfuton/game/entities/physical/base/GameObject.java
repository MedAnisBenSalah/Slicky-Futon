package com.ormisiclapps.slickyfuton.game.entities.physical.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.EntityType;
import com.ormisiclapps.slickyfuton.enumerations.ObjectsEntryType;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.movement.Movement;
import com.ormisiclapps.slickyfuton.game.entities.notphysical.EatableObject;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.entities.physical.objects.Flame;
import com.ormisiclapps.slickyfuton.game.nodes.movement.MovementNode;
import com.ormisiclapps.slickyfuton.game.world.Terrain;
import com.ormisiclapps.slickyfuton.utility.Configuration;

public class GameObject extends Entity
{
    private Movement movement;
    private boolean passed;
    private Vector2 initialPosition;
    private boolean entering, exiting, entered, exited;
    private float entrySpeed;
    private float initialAlpha;
    private Vector2 tmpVector;
    private ObjectsEntryType objectsEntryType;
    private float objectsEntrySpeed;

    public GameObject(String model, boolean passable)
    {
        // Create the entity
        super(model, EntityType.ENTITY_TYPE_OBJECT);
        // Reset flags
        passed = !passable;
        // Create vectors
        initialPosition = new Vector2();
        tmpVector = new Vector2();
        // Reset flags
        entering = false;
        entered = false;
        exiting = false;
        exited = false;
        // Reset values
        entrySpeed = 0f;
        // Reset instances
        movement = null;
    }

    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Create the entity
        super.create();
        // Get the movement nodes
        MovementNode[] movementNodes = Core.getInstance().getModelManager().getMovementNodes(getModel().getName());
        // Validate the movement nodes
        if(movementNodes != null && movementNodes.length != 0)
        {
            // Get the current movement
            MovementNode node = movementNodes[movementId];
            // Change the position so that it includes the movement length
            if (node.distance != 0)
                position.x += node.distance * (node.type.equals("Circular") ? Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH :
                                                Configuration.MOVABLE_OBJECT_PATH_LENGTH);
        }
        // Save the initial position
        initialPosition = position.cpy();
        // Set the object's position
        setPosition(position);
        // Reset values
        entrySpeed = 0f;
        // Validate the movement nodes
        if(movementNodes != null && movementNodes.length != 0)
            // Create the movement
            movement = new Movement(movementNodes[movementId], startingPoint, position, speed, this);

        // Set the object's entry type and speed
        objectsEntryType = Terrain.getInstance().getObjectsEntryType();
        objectsEntrySpeed = Terrain.getInstance().getObjectsEntrySpeed();
        // Reset flags
        entering = false;
        entered = false;
        exiting = false;
        exited = false;
    }

    @Override
    public void process()
    {
        // Process object entry
        if(objectsEntryType != ObjectsEntryType.OBJECTS_ENTRY_NORMAL)
            processObjectEntry();

        // Process movement
        if(movement != null)
            movement.process(this);

        // Process entity
        super.process();
        // Check if the player has passed this object
        if(!passed && !Player.getInstance().isDying() && getPosition().x <= Player.getInstance().getPosition().x)
        {
            // Notify score update
            GameLogic.getInstance().addScore();
            // Set the passed flag
            passed = true;
            // Increase the passed obstacles count
            Core.getInstance().getStatsSaver().savedData.obstaclesPassed++;
        }
    }

    private void processObjectEntry()
    {
        // Validate the entity
        if(this instanceof ChainGameObject || this instanceof MovableGameObject || !getModel().getNode().entryAnimation || (entered && exited))
            return;

        // Get screen coordinates
        Vector2 screen = Camera.getInstance().screenToWorld(Core.getInstance().getGraphicsManager().SCREEN_VECTOR);
        // Are we entering the screen ?
        float size = !parted ? (getSize().x >= getSize().y ? getSize().x / 2f : getSize().y / 2f) : -partedLowestPoint;
        if(!entered && !entering && getPosition().x - screen.x <= size)
        {
            // Set the entering flag
            entering = true;
            // Set the entry position
            if(objectsEntryType == ObjectsEntryType.OBJECTS_ENTRY_SCROLL_UP)
            {
                // Setup entry
                if(initialPosition.y <= Terrain.getInstance().getRoofPosition() / 2f)
                {
                    setPosition(tmpVector.set(getPosition().x, 0f));
                    entrySpeed = initialPosition.y / objectsEntrySpeed;
                }
                else
                {
                    setPosition(tmpVector.set(getPosition().x, Terrain.getInstance().getRoofPosition()));
                    entrySpeed = (Terrain.getInstance().getRoofPosition() - initialPosition.y) / objectsEntrySpeed;
                }
            }
            else if(objectsEntryType == ObjectsEntryType.OBJECTS_ENTRY_SCROLL_DOWN)
            {
                // Setup entry
                if(initialPosition.y < Terrain.getInstance().getRoofPosition() / 2f)
                {
                    setPosition(tmpVector.set(getPosition().x, 0f));
                    entrySpeed = initialPosition.y / objectsEntrySpeed;
                }
                else
                {
                    setPosition(tmpVector.set(getPosition().x, Terrain.getInstance().getRoofPosition()));
                    entrySpeed = (Terrain.getInstance().getRoofPosition() - initialPosition.y) / objectsEntrySpeed;
                }
            }
            else if(objectsEntryType == ObjectsEntryType.OBJECTS_ENTRY_FADE_IN)
            {
                // Get the initial alpha
                initialAlpha = getColor().a;
                // Calculate entry speed
                entrySpeed = initialAlpha / (objectsEntrySpeed * 2.25f);
                // Reset color
                Color color = getColor();
                color.a = 0f;
                setColor(color);
            }
        }
        // Are we exiting the screen ?
        else if(!exiting && !exited && Player.getInstance().getPosition().x - getPosition().x >=
                (objectsEntryType == ObjectsEntryType.OBJECTS_ENTRY_FADE_IN ? size : size * 1.25f))
        {
            // Set the exiting flag
            exiting = true;
            // Process objects fading
            if(objectsEntryType == ObjectsEntryType.OBJECTS_ENTRY_FADE_IN)
                // Calculate entry speed
                entrySpeed = getColor().a / objectsEntrySpeed;
            else
                // Calculate entry speed
                entrySpeed = initialPosition.y / objectsEntrySpeed;
        }
        // Skip if we're not processing anything
        if(!entering && !exiting)
            return;

        // Process each entry type
        switch(objectsEntryType)
        {
            case OBJECTS_ENTRY_SCROLL_UP:
                if(entering)
                    processEntryScroll(initialPosition.y <= Terrain.getInstance().getRoofPosition() / 2f);
                else
                    processExitScroll(false);

                break;

            case OBJECTS_ENTRY_SCROLL_DOWN:
                if(entering)
                    processEntryScroll(initialPosition.y < Terrain.getInstance().getRoofPosition() / 2f);
                else
                    processExitScroll(true);

                break;

            case OBJECTS_ENTRY_FADE_IN:
                if(entering)
                    processEntryFading(true);
                else
                    processEntryFading(false);

                break;
        }
    }

    private void processEntryScroll(boolean up)
    {
        // Scroll up
        setPosition(tmpVector.set(getPosition()).add(0f, up ? entrySpeed : -entrySpeed));
        // Did we finish ?
        if((up && getPosition().y >= initialPosition.y) || (!up && getPosition().y <= initialPosition.y))
        {
            // Set the entered flag
            entered = true;
            // Reset entering flag
            entering = false;
            // Fix the position
            setPosition(tmpVector.set(getPosition().x, initialPosition.y));
        }
    }

    private void processExitScroll(boolean up)
    {
        // Scroll down
        setPosition(tmpVector.set(getPosition()).add(0f, up ? entrySpeed : -entrySpeed));
        // Did we finish ?
        float size = getSize().x >= getSize().y ? getSize().x : getSize().y;
        if((up && getPosition().y >= Terrain.getInstance().getRoofPosition() + size) || (!up && getPosition().y <= -size))
        {
            // Set the exited flag
            exited = true;
            // Reset exiting flag
            exiting = false;
        }
    }

    private void processEntryFading(boolean fadeIn)
    {
        // Fade
        Color color = getColor();
        color.a += fadeIn ? entrySpeed : -entrySpeed;
        setColor(color);
        if(!fadeIn)
        {
            color = getLightColor();
            color.a -= entrySpeed;
            setLightColor(color);
        }
        // Did we finish ?
        if((fadeIn && color.a >= initialAlpha) || (!fadeIn && color.a <= 0f))
        {
            if(fadeIn)
            {
                // Set the entered flag
                entered = true;
                // Reset entering flag
                entering = false;
                // Fix color
                color.a = initialAlpha;
            }
            else
            {
                // Set the exited flag
                exited = true;
                // Reset exiting flag
                exiting = false;
                // Fix color
                color.a = 0f;
                // Fix light color
                color = getLightColor();
                color.a = 0f;
                setLightColor(color);
            }
        }
    }

    public void teleport(Vector2 position)
    {
        // Set the entity's position
        setPosition(position);
        // Do we have a movement
        if(movement != null)
            // Set the movement's position
            movement.setPosition(position, this);
    }

    public void setInitialPosition(Vector2 position)
    {
        // Set position
        setPosition(position);
        // Set the initial position
        initialPosition.set(position);
    }

    public Movement getMovement() { return movement; }

    public Vector2 getInitialPosition() {
        return initialPosition;
    }

    public boolean isPassed() {
        return passed;
    }
}
