package com.ormisiclapps.slickyfuton.utility;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by OrMisicL on 5/29/2016.
 */
public class GameMath
{
    public static float PPM;
    private static final Vector2 tmpVector = new Vector2();

    public static void initializePPM(float height)
    {
        PPM = height / 34f;
    }

    public static Vector2 getCenteredPosition(Vector2 position, Vector2 size)
    {
        return tmpVector.set(position.x - size.x / 2, position.y - size.y / 2);
    }

    /*****************************************************
     * Pixels and Meters conversion
     ****************************************************/

    public static float pixelsPerMeters(float meters)
    {
        return meters*PPM;
    }

    public static float metersPerPixels(float pixels)
    {
        return pixels/PPM;
    }

    public static Vector2 pixelsPerMeters(Vector2 meters)
    {
        return tmpVector.set(meters.x*PPM, meters.y*PPM);
    }

    public static Vector2 metersPerPixels(Vector2 pixels)
    {
        return tmpVector.set(pixels.x/PPM, pixels.y/PPM);
    }

    public static Vector2 pixelsPerMeters(float x, float y)
    {
        return tmpVector.set(x*PPM, y*PPM);
    }

    public static Vector2 metersPerPixels(float x, float y)
    {
        return tmpVector.set(x/PPM, y/PPM);
    }

    /*****************************************************
     * Arrays operations
     ****************************************************/

    /*
        Takes a Vector2 array and convert it to a float array
        Return array's size = input array's size * 2
    */
    public static float[] convertToFloatArray(Vector2[] vectorArray)
    {
        // Create the float array
        float[] floatArray = new float[vectorArray.length * 2];
        // Copy the vector array to the float array
        for(int i = 0; i < vectorArray.length * 2; i += 2)
        {
            floatArray[i] = vectorArray[i/2].x;
            floatArray[i+1] = vectorArray[i/2].y;
        }
        return floatArray;
    }

    /*
        Will resize(scale) a vector
        Formula: oldPoint = oldPoint / factor -> factor = oldSize / newSize
    */
    public static void resizeVector(Vector2 vector, Vector2 oldSize, Vector2 size)
    {
        // Resize the vertices array
        Vector2 resizeFactor = new Vector2(oldSize.x / size.x, oldSize.y / size.y);
        vector.x /= resizeFactor.x;
        vector.y /= resizeFactor.y;
    }

    /*
        Will use the method above to resize an array of vectors
    */
    public static void resizeVectorsArray(Vector2[] vectors, Vector2 oldSize, Vector2 size)
    {
        for(int i = 0; i < vectors.length; i++)
            resizeVector(vectors[i], oldSize, size);
    }

    /*
        This will take an array of vectors as a parameter, compute its center and return it
        It uses the formula: center = lowest point + (size / 2)
    */
    public static Vector2 getVectorsArrayCenter(Vector2[] vectors)
    {
        // Get the vectors size
        Vector2[] size = getVectorsArraySize(vectors);
        // Return the center
        return new Vector2(size[1].x + size[0].x / 2, size[1].y + size[0].y / 2);
    }

    /*
        This will take an array of vectors as a parameter, compute its size, highest and lowest point
        And will return a vector's array composed of: size, lowest, highest
    */
    public static Vector2[] getVectorsArraySize(Vector2[] vectors)
    {
        // Create vectors
        Vector2 highest = new Vector2(vectors[0].x, vectors[0].y);
        Vector2 lowest = new Vector2(vectors[0].x, vectors[0].y);
        // Loop through all the vectors and find the highest and lowest values
        for(int i = 0; i < vectors.length; i++)
        {
            if(vectors[i].x > highest.x)
                highest.x = vectors[i].x;
            else if(vectors[i].x < lowest.x)
                lowest.x = vectors[i].x;

            if(vectors[i].y > highest.y)
                highest.y = vectors[i].y;
            else if(vectors[i].y < lowest.y)
                lowest.y = vectors[i].y;
        }
        Vector2[] array = new Vector2[3];
        array[0] = new Vector2(highest.x - lowest.x, highest.y - lowest.y);
        array[1] = lowest;
        array[2] = highest;
        return array;
    }

    /*
        This will take an array of vectors as a parameter, and invert it according to the given center
        And will return a vector's array
    */
    public static Vector2[] flipVerticesArray(Vector2[] vectors, Vector2 center, Vector2 axis)
    {
        // Create ethe result array
        Vector2[] result = new Vector2[vectors.length];
        // Loop through all the vectors
        int idx = 0;
        for(Vector2 vector : vectors)
        {
            // Get its relative position to the center
            Vector2 relativePosition = vector.cpy().sub(center);
            // Flip it
            result[idx++] = center.cpy().sub(relativePosition);
            // Restore coordinates
            if(axis.x == 0)
                result[idx - 1].x = vector.x;

            if(axis.y == 0)
                result[idx - 1].y = vector.y;
        }
        return result;
    }


    /*
        This will adjust the vectors array passed as a parameter to match the new origin
        Formula: newPoint = newOrigin + factor -> factor = oldPoint - center
     */
    public static void setOrigin(Vector2[] vectors, Vector2 newOrigin, Vector2 bodyCenter)
    {
        // Get the vector center
        Vector2 shapeCenter = getVectorsArrayCenter(vectors);
        Vector2 center = shapeCenter.sub(bodyCenter).add(newOrigin);
        // Loop through all the vectors and adjust them to match the new center
        for(int i = 0; i < vectors.length; i++)
        {
            vectors[i].x = newOrigin.x + (vectors[i].x - center.x);
            vectors[i].y = newOrigin.y + (vectors[i].y - center.y);
        }
    }

    /*****************************************************
     * Rotation and angle operations
     ****************************************************/

    public static float radiansToDegrees(float angle)
    {
        // Calculate the new angle
        float newAngle = angle * MathUtils.radiansToDegrees;
        if(newAngle < 0)
            newAngle += 360;
        else if(newAngle >= 360)
            newAngle -= 360;

        return newAngle;
    }

    public static float degreesToRadians(float angle)
    {
        // Calculate the new angle
        float newAngle = angle * MathUtils.degreesToRadians;
        if(newAngle % (Math.PI * 2) >= 0 && newAngle >= Math.PI)
            newAngle -= Math.PI * 2;
        else if(angle < -Math.PI)
            newAngle += Math.PI * 2;

        return newAngle;
    }

    public static float adjustAngleInDegrees(float angle)
    {
        // Fix the angle interval
        if(angle >= 360)
            return angle - 360;
        else if(angle < 0)
            return angle + 360;
        else
            return angle;
    }

    public static float adjustAngleInRadians(float angle)
    {
        return degreesToRadians(adjustAngleInDegrees(radiansToDegrees(angle)));
    }

    /*****************************************************
     * Vectors operations
     ****************************************************/

    public static double getDistanceBetweenVectors(Vector2 firstVector, Vector2 secondVector)
    {
        // Get the distance between points
        tmpVector.set(secondVector.x - firstVector.x, secondVector.y - firstVector.y);
        return Math.sqrt(tmpVector.x * tmpVector.x + tmpVector.y * tmpVector.y);
    }

    public static float getAngleBetweenVectors(Vector2 origin, Vector2 target)
    {
        float angle = radiansToDegrees((float)Math.atan2(target.y - origin.y, target.x - origin.x));
        return angle < 0 ? angle + 360 : angle;
    }

    /*****************************************************
     * Other math utils
     ****************************************************/

    public static boolean isOnScreen(Vector2 position, Vector2 size, Vector2 screen)
    {
        // Get the lowest point of the entity
        Vector2 lowestPoint = tmpVector.set(position).sub(size.x / 2, size.y / 2);
        // Test screen coordinates
        return lowestPoint.x <= screen.x && lowestPoint.y <= screen.y && lowestPoint.x >= -size.x && lowestPoint.y >= -size.y;
    }

    public static boolean checkCollision(Vector2 position1, Vector2 size1, Vector2 position2, Vector2 size2)
    {
        // Calculate the sizes sum
        float width = (size1.x + size2.x) / 2f;
        float height = (size1.y + size2.y) / 2f;
        // Calculate the position difference
        float x = Math.abs(position1.x - position2.x);
        float y = Math.abs(position1.y - position2.y);
        // Check collision
        return x <= width && y <= height;
    }
}
