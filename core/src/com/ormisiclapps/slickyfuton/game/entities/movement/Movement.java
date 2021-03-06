package com.ormisiclapps.slickyfuton.game.entities.movement;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;
import com.ormisiclapps.slickyfuton.enumerations.MovementPointType;
import com.ormisiclapps.slickyfuton.enumerations.MovementType;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.game.nodes.movement.MovementNode;

/**
 * Created by Anis on 10/30/2016.
 */

public class Movement
{
    private MovementType movementType;
    private int pointsCount;
    private float rotation;
    private float distance;
    private MovementPoint[] points;
    private int currentPoint;
    private boolean forward;
    private long movementTime;
    private long movementTimeSinceStart;
    private float speed; // Meters per second
    private float direction;
    private Vector2 lowestPoint;
    private Vector2 highestPoint;
    private Vector2 origin;
    private float circularAngle;
    private float circularNextAngle;
    private float angularSpeed;
    private Vector2 tmpVector;

    public static final float ANGLE_DIFFERENCE = 90f;
    private static final float DEFAULT_SPEED = 12.5f;

    public Movement(String movementType, int pointsCount, int startingPoint, float distance, float rotation, float speed)
    {
        // Get the movement type
        this.movementType = getMovementType(movementType);
        // Save the points count
        this.pointsCount = pointsCount;
        // Save the rotation
        this.rotation = rotation;
        // Save the distance
        this.distance = distance;
        // Create the points array
        points = new MovementPoint[pointsCount];
        // Create the origin vector
        origin = new Vector2();
        // Create the size vectors
        lowestPoint = new Vector2();
        highestPoint = new Vector2();
        // Reset the circular movement  and speed
        circularAngle = 0f;
        angularSpeed = 0f;
        movementTimeSinceStart = 0;
        // Always initialize the movement current point to the starting point
        currentPoint = startingPoint;
        // This flag is used to indicate if we're going forward in points
        forward = currentPoint != pointsCount - 1;
        // This will indicate the circular movement's direction (set to 1 or -1)
        direction = 1f;
        // Initialize speed
        if(speed == 0)
            this.speed = DEFAULT_SPEED;
        else
            this.speed = speed;

        // Create tmp vector
        tmpVector = new Vector2();
    }

    public Movement(MovementNode movementNode, int startingPoint, Vector2 position, float speed, GameObject parent)
    {
        // Call the upper constructor
        this(movementNode.type, movementNode.points, startingPoint, movementNode.distance, movementNode.rotationSpeed, speed);
        // Get the starting position
        Vector2 pointPosition = position.cpy();
        // Initialize the lowest and highest points
        lowestPoint.set(pointPosition);
        highestPoint.set(pointPosition);
        // Create all the points
        for(int i = 0; i < movementNode.points; i++)
        {
            // Create the point
            points[i] = new MovementPoint(pointPosition, movementNode.pointsX[i], movementNode.pointsY[i]);
            // Set the next point position
            pointPosition.set(points[i].getPosition());
            // Find if its the lowest point or the highest point
            if(pointPosition.x < lowestPoint.x)
                lowestPoint.x = pointPosition.x;
            else if(pointPosition.x > highestPoint.x)
                highestPoint.x = pointPosition.x;

            if(pointPosition.y < lowestPoint.y)
                lowestPoint.y = pointPosition.y;
            else if(pointPosition.y > highestPoint.y)
                highestPoint.y = pointPosition.y;
        }
        if(movementType != MovementType.MOVEMENT_TYPE_STATIC)
        {
            // Set the parent's position
            parent.setPosition(points[currentPoint].getPosition());
            // Setup stage
            setupStage(parent);
        }
    }

    public void process(GameObject parent)
    {
        // Process depending on the movement type
        switch(movementType)
        {
            case MOVEMENT_TYPE_STATIC:
                // Static movements only process rotation
                processRotationMovement(parent);
                break;

            case MOVEMENT_TYPE_LINEAR:
                // Process linear movement
                processLinearMovement(parent);
                break;

            case MOVEMENT_TYPE_CIRCULAR:
                // Process circular movement
                processCircularMovement(parent);
                break;
        }
    }

    private void processRotationMovement(GameObject parent)
    {
        // Rotate the object if it has a rotational movement
        parent.setRotationInDegrees(GameMath.adjustAngleInDegrees(parent.getRotationInDegrees() + rotation));
    }

    private void processLinearMovement(GameObject parent)
    {
        // Process rotation animation
        processRotationMovement(parent);
        // Update the movements time
        movementTimeSinceStart += Core.getInstance().getGraphicsManager().DELTA_TIME * 1000;
        // Did we reach the destination ?
        if(!isMovementFinished())
        {
            // Get the next point position
            tmpVector.set(getNextPointPosition());
            // Find the angle between the point vectors
            float angle = GameMath.getAngleBetweenVectors(parent.getPosition(), tmpVector);
            // Calculate the speed
            float speed = Core.getInstance().getGraphicsManager().DELTA_TIME * this.speed;
            // Calculate the new position
            tmpVector.set(parent.getPosition().x + MathUtils.cos(GameMath.degreesToRadians(angle)) * speed,
                    parent.getPosition().y + MathUtils.sin(GameMath.degreesToRadians(angle)) * speed);

            // Update the object's position
            parent.setPosition(tmpVector);
        }
        else
        {
            // Update the stage
            updateStage(parent);
            // Re-process the movement
            processLinearMovement(parent);
        }
    }

    private void processCircularMovement(GameObject parent)
    {
        // Process rotation animation
        processRotationMovement(parent);
        // Update the movements time
        movementTimeSinceStart += Core.getInstance().getGraphicsManager().DELTA_TIME * 1000;
        // Did we reach the destination ?
        if(!isMovementFinished())
        {
            // Calculate the speed
            float speed = Core.getInstance().getGraphicsManager().DELTA_TIME * angularSpeed;
            // Find the new angle
            circularAngle += speed * direction;
            // Don't allow the angle to exceed its limits
            if(circularAngle * direction > circularNextAngle * direction)
                circularAngle = circularNextAngle;

            // Set the radius
            float radius = Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH;
            // Calculate the new position
            tmpVector.set(origin.x + MathUtils.cos(GameMath.degreesToRadians(circularAngle)) * radius,
                    origin.y + MathUtils.sin(GameMath.degreesToRadians(circularAngle)) * radius);

            // Update the object's position
            parent.setPosition(tmpVector);
        }
        else
        {
            // Update the stage
            updateStage(parent);
            // Re-process the movement
            processLinearMovement(parent);
        }
    }

    private void setupStage(GameObject parent)
    {
        // Get the next point
        Vector2 nextPosition = getNextPointPosition();
        // Setup linear stage
        float distance = 0f;
        if(movementType == MovementType.MOVEMENT_TYPE_LINEAR)
            // Calculate the distance
            distance = (float)GameMath.getDistanceBetweenVectors(parent.getPosition(), nextPosition);
        // Setup circular stage
        else if(movementType == MovementType.MOVEMENT_TYPE_CIRCULAR)
        {
            // Set the circle's origin point
            if(getNextPoint().getPointTypeY() != MovementPointType.MOVEMENT_POINT_TYPE_MIDDLE)
                origin.set(nextPosition.x, points[currentPoint].getPosition().y);
            else
                origin.set(points[currentPoint].getPosition().x, nextPosition.y);

            // Set the circular angle
            circularAngle = GameMath.getAngleBetweenVectors(origin, points[currentPoint].getPosition());
            // Calculate the next angle
            circularNextAngle = GameMath.getAngleBetweenVectors(origin, nextPosition);
            // Fix the 360 degrees
            if(circularAngle == 270 && circularNextAngle == 0)
                circularNextAngle = 360f;
            else if(circularAngle == 0 && circularNextAngle == 270)
                circularAngle = 360f;

            // Set the direction
            direction = circularNextAngle < circularAngle ? -1f : 1f;
            // Calculate the distance
            distance = (2 * Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH * MathUtils.PI) / 4;
            // Calculate the angular speed
            angularSpeed = ANGLE_DIFFERENCE / (distance / speed);
        }
        // Calculate the path time
        movementTime = (long)(distance / speed * 1000f);
        // Set the movement start
        movementTimeSinceStart = 0;
    }

    private void updateStage(GameObject parent)
    {
        currentPoint += forward ? 1 : -1;
        if(forward && currentPoint == pointsCount - 1)
            forward = false;
        else if(!forward && currentPoint == 0)
            forward = true;

        // Setup the stage
        setupStage(parent);
    }

    private Vector2 getNextPointPosition()
    {
        return getNextPoint().getPosition();
    }

    private MovementPoint getNextPoint()
    {
        if(forward)
            return points[currentPoint + 1];
        else
            return points[currentPoint - 1];
    }

    private boolean isMovementFinished()
    {
        return movementTimeSinceStart >= movementTime;
    }

    public float getDistance() { return distance; }
    public int getPointsCount() { return pointsCount; }
    public MovementPoint[] getPoints() { return points; }
    public MovementType getType() { return movementType; }
    public Vector2 getLowestPoint() { return lowestPoint; }
    public Vector2 getHighestPoint() { return highestPoint; }

    public Vector2 getOriginOfPoints(int point1, int point2)
    {
        // Get points
        MovementPoint currentPoint = points[point1];
        MovementPoint nextPoint = points[point2];
        // Set the circle's origin point
        if(nextPoint.getPointTypeY() != MovementPointType.MOVEMENT_POINT_TYPE_MIDDLE)
            return tmpVector.set(nextPoint.getPosition().x, currentPoint.getPosition().y);
        else
            return tmpVector.set(currentPoint.getPosition().x, nextPoint.getPosition().y);
    }

    public void setPosition(Vector2 position, GameObject parent)
    {
        // Loop through the points
        tmpVector.set(position);
        for(MovementPoint point : points)
        {
            // Create the point
            point.setPosition(tmpVector);
            // Set the next point position
            tmpVector.set(point.getPosition());
            // Find if its the lowest point or the highest point
            if(tmpVector.x < lowestPoint.x)
                lowestPoint.x = tmpVector.x;
            else if(tmpVector.x > highestPoint.x)
                highestPoint.x = tmpVector.x;

            if(tmpVector.y < lowestPoint.y)
                lowestPoint.y = tmpVector.y;
            else if(tmpVector.y > highestPoint.y)
                highestPoint.y = tmpVector.y;
        }
        // Update the parent's position to eliminate freezing
        if(movementType != MovementType.MOVEMENT_TYPE_STATIC)
        {
            // Put it at the next point
            parent.setPosition(points[currentPoint].getPosition());
            // Update stage
            setupStage(parent);
        }
    }

    private static MovementType getMovementType(String movementType)
    {
        if(movementType.equals("Linear"))
            return MovementType.MOVEMENT_TYPE_LINEAR;
        else if(movementType.equals("Circular"))
            return MovementType.MOVEMENT_TYPE_CIRCULAR;
        else
            return MovementType.MOVEMENT_TYPE_STATIC;
    }


}
