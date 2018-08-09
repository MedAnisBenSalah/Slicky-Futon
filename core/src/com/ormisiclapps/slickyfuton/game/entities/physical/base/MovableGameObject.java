package com.ormisiclapps.slickyfuton.game.entities.physical.base;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.world.Terrain;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;
import com.ormisiclapps.slickyfuton.enumerations.MovementType;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.entities.movement.Movement;
import com.ormisiclapps.slickyfuton.game.entities.movement.MovementPoint;

/**
 * Created by Anis on 11/5/2016.
 */

public class MovableGameObject extends GameObject
{
    private Texture railTexture;
    private float[] vertices;
    private int verticesIndex;
    private boolean shouldRender;
    private Vector2 tmpVector;
    private Vector2 tmpVector2;
    private Vector2 tmpVector3;
    private Color color;

    private static final float RAIL_SIZE = 0.15f;
    private static final int CIRCULAR_RAIL_SEGMENTS = 5;
    private static final float LINEAR_TEXTURE_U = 10f;

    public MovableGameObject(String model, boolean passable)
    {
        // Create the game object
        super(model, passable);
        // Get the rail texture
        railTexture = Core.getInstance().getResourcesManager().getResource("HalfEmpty", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Set the texture wrap
        railTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        tmpVector3 = new Vector2();
        // Create color
        color = new Color();
        // Reset flags
        shouldRender = false;
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Create the game object
        super.create(position, movementId, startingPoint, speed);
        // Get the object's movement
        Movement movement = getMovement();
        // Skip if its a single point movement
        if(movement.getPointsCount() <= 1)
            return;

        // Create the vertices array
        vertices = new float[2000];
        // Reset vertices index
        verticesIndex = 0;

    }

    @Override
    public void process()
    {
        // Process the game object
        super.process();
        // Get the object's movement
        Movement movement = getMovement();
        // Skip if its a single point movement
        if(movement.getPointsCount() <= 1)
            return;

        // Get the screen coordinates for the lowest and highest points of the movement
        tmpVector.set(Camera.getInstance().worldToScreen(movement.getLowestPoint()));
        tmpVector2.set(Camera.getInstance().worldToScreen(movement.getHighestPoint()));
        // Check if we need to render the movement rails
        shouldRender = (tmpVector.x <= Core.getInstance().getGraphicsManager().WIDTH && tmpVector2.x >= 0 &&
                tmpVector.y <= Core.getInstance().getGraphicsManager().HEIGHT && tmpVector2.y >= 0);

        // Skip if we're not rendering this frame
        if(!shouldRender)
            return;

        // Reset vertices index
        verticesIndex = 0;
        // Convert the rail size
        float railSize = GameMath.pixelsPerMeters(RAIL_SIZE);
        // Loop through the movement points
        for(int i = 1; i < movement.getPointsCount(); i++)
        {
            // Get the previous point
            MovementPoint previousPoint = movement.getPoints()[i - 1];
            // Get the current point
            MovementPoint point = movement.getPoints()[i];
            // Find the movement axis
            tmpVector3.set(point.getPosition().x - previousPoint.getPosition().x != 0 ? 1f : 0f,
                    point.getPosition().y - previousPoint.getPosition().y != 0 ? 1f : 0f);

            // Get the points screen positions
            tmpVector.set(Camera.getInstance().worldToScreen(previousPoint.getPosition()));
            tmpVector2.set(Camera.getInstance().worldToScreen(point.getPosition()));
            // Fill vertices depending on the movement type
            if(movement.getType() == MovementType.MOVEMENT_TYPE_LINEAR)
            {
                // Get the path angle
                float angle = GameMath.getAngleBetweenVectors(previousPoint.getPosition(), point.getPosition());
                // These angles are "straight up/straight to the left" movements
                // These movements doesn't need any calculations for their axis, so we use this dirty code to
                // ensure they're being rendered properly
                if(angle != 0 & angle != 90 && angle != 180 && angle != 270)
                    // Set the axis according to the angle
                    tmpVector3.scl(-MathUtils.sin(GameMath.degreesToRadians(angle)), MathUtils.cos(GameMath.degreesToRadians(angle)));

                // Fill the linear vertices
                fillLinearVertices(tmpVector, tmpVector2, railSize, tmpVector3, LINEAR_TEXTURE_U);
            }
            else if(movement.getType() == MovementType.MOVEMENT_TYPE_CIRCULAR)
            {
                // Get the origin's screen position
                Vector2 originScreenPosition = Camera.getInstance().worldToScreen(movement.getOriginOfPoints(i - 1, i));
                // Fill the vertices array
                fillCircularVertices(tmpVector, tmpVector2, originScreenPosition, railSize, tmpVector3);
            }

        }
    }

    private void fillLinearVertices(Vector2 previousScreenPosition, Vector2 screenPosition, float size, Vector2 axis, float textureU)
    {
        // Set the path's color
        color.set(Terrain.getInstance().getEntitiesColor());
        color.a = 0.9f;
        float packedColor = color.toFloatBits();
        // Lower left corner
        vertices[verticesIndex++] = previousScreenPosition.x - size * axis.y;
        vertices[verticesIndex++] = previousScreenPosition.y - size * axis.x;
        vertices[verticesIndex++] = packedColor;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;

        // Upper left corner
        vertices[verticesIndex++] = previousScreenPosition.x + size * axis.y;
        vertices[verticesIndex++] = previousScreenPosition.y + size * axis.x;
        vertices[verticesIndex++] = packedColor;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = textureU;

        // Upper right corner
        vertices[verticesIndex++] = screenPosition.x + size * axis.y;
        vertices[verticesIndex++] = screenPosition.y + size * axis.x;
        vertices[verticesIndex++] = packedColor;
        vertices[verticesIndex++] = textureU;
        vertices[verticesIndex++] = textureU;

        // Lower right corner
        vertices[verticesIndex++] = screenPosition.x - size * axis.y;
        vertices[verticesIndex++] = screenPosition.y - size * axis.x;
        vertices[verticesIndex++] = packedColor;
        vertices[verticesIndex++] = textureU;
        vertices[verticesIndex++] = 0;

    }

    private void fillCircularVertices(Vector2 previousScreenPosition, Vector2 screenPosition, Vector2 origin, float size, Vector2 axis)
    {
        // Get the line segments count (cast it to float)
        float segments = CIRCULAR_RAIL_SEGMENTS;
        // Calculate the segment length
        float deltaAngle = Movement.ANGLE_DIFFERENCE / segments;
        float angle = GameMath.getAngleBetweenVectors(origin, previousScreenPosition);
        float nextAngle = GameMath.getAngleBetweenVectors(origin, screenPosition);
        // Find the radius
        float radius = GameMath.pixelsPerMeters(Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH);
        // Create the last position
        Vector2 lastPosition = tmpVector2.set(previousScreenPosition);
        // Fix the 360 degrees
        if(angle == 270 && nextAngle == 0)
            nextAngle = 360f;
        else if(angle == 0 && nextAngle == 270)
            angle = 360f;

        // Set the direction
        float direction = nextAngle < angle ? -1f : 1f;
        // Generate a smooth edge
        for(int i = 0; i < segments + 1; i++)
        {
            // Calculate the next point
            tmpVector.set(origin.x + MathUtils.cos(GameMath.degreesToRadians(angle)) * radius,
                    origin.y + MathUtils.sin(GameMath.degreesToRadians(angle)) * radius);

            // Skip the first position
            // NOTE: Because the x axis radius is different from the y axis radius, the first iteration is used
            // to calculate the correct y starting position as well as the first angle, hence not rendering it
            if(i != 0)
                // Set the position's vertices
                fillLinearVertices(lastPosition, tmpVector, size, axis.cpy().scl(MathUtils.sin(GameMath.degreesToRadians(angle)),
                        MathUtils.cos(GameMath.degreesToRadians(angle))), 1f);

            // Set the last position
            lastPosition.set(tmpVector);
            // Add the angle
            angle += deltaAngle * direction;
        }
    }

    public void drawPath()
    {
        // Skip if we're not rendering this frame
        if(!shouldRender)
            return;

        // Get the object's movement
        Movement movement = getMovement();
        // Skip if its a single point movement
        if(movement.getPointsCount() <= 1)
            return;

        // Draw the rails
        Core.getInstance().getGraphicsManager().drawTexture(railTexture, vertices, verticesIndex);
    }
}
