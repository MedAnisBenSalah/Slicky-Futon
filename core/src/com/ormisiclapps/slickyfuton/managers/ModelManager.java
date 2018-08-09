package com.ormisiclapps.slickyfuton.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.nodes.entity.PartedModelNode;
import com.ormisiclapps.slickyfuton.utility.GameMath;
import com.ormisiclapps.slickyfuton.game.nodes.entity.EntityBodyPartNode;
import com.ormisiclapps.slickyfuton.game.nodes.entity.ModelNode;
import com.ormisiclapps.slickyfuton.game.nodes.movement.MovementNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OrMisicL on 6/1/2016.
 */
public class ModelManager
{
    private Map<String, ModelNode> loadedModels;
    private Map<String, MovementNode[]> loadedMovements;
    private Map<String, PolygonShape[]> loadedBodies;

    public ModelManager()
    {
        // Create the maps
        loadedModels = new HashMap<String, ModelNode>();
        loadedMovements = new HashMap<String, MovementNode[]>();
        loadedBodies = new HashMap<String, PolygonShape[]>();
    }

    /*
        Will load the model's XML file and store it into the models map
    */
    public ModelNode loadModel(String modelName)
    {
        // If the model is already loaded then simply return it
        if(loadedModels.containsKey(modelName))
            return loadedModels.get(modelName);

        // Parse the settings file
        XmlReader.Element rootElement = Core.getInstance().getFileManager().parseModelFile(modelName);
        if(rootElement == null)
            return null;

        // Get the parts count
        int partsCount = rootElement.getChildCount();
        // Validate the parts count
        if(partsCount == 0)
            return null;

        // Get the child name
        String name = rootElement.getChild(0).getName();
        // Validate the render element
        XmlReader.Element renderElement = rootElement.getChildByName(name).getChildByName("Render");
        if(renderElement == null || renderElement.getChildByName("Shape") == null || renderElement.getChildByName("Color") == null ||
                (renderElement.getChildByName("Size") == null && renderElement.getChildByName("Radius") == null)
                || renderElement.getChildByName("EntryAnimation") == null)
            return null;

        // Get the shape
        String shape = renderElement.getChildByName("Shape").get("value");
        // If its a polygon then find the size
        Vector2 size = new Vector2();
        float radius = 0;
        if(shape.contains("Polygon") || shape.equals("Rectangle"))
            // Get the size
            size.set(renderElement.getChildByName("Size").getFloatAttribute("x"),
                    renderElement.getChildByName("Size").getFloatAttribute("y"));
        else
            // Get the radius
            radius = renderElement.getChildByName("Radius").getFloatAttribute("value");

        // Get the color element
        float r = renderElement.getChildByName("Color").getFloatAttribute("r");
        float g = renderElement.getChildByName("Color").getFloatAttribute("g");
        float b = renderElement.getChildByName("Color").getFloatAttribute("b");
        float a = renderElement.getChildByName("Color").getFloatAttribute("a");
        // Create the color instance
        Color color = new Color(r, g, b, a);
        // Get the entry animation flag
        boolean entryAnimation = renderElement.getChildByName("EntryAnimation").getBoolean("toggle");
        // Validate the light element
        XmlReader.Element lightElement = rootElement.getChildByName(name).getChildByName("Light");
        if(lightElement == null || lightElement.getChildByName("Distance") == null || lightElement.getChildByName("Color") == null)
            return null;

        // Get the light elements
        float lightDistance = lightElement.getChildByName("Distance").getFloatAttribute("value");
        // Get the light's color element
        float lr = lightElement.getChildByName("Color").getFloatAttribute("r");
        float lg = lightElement.getChildByName("Color").getFloatAttribute("g");
        float lb = lightElement.getChildByName("Color").getFloatAttribute("b");
        float la = lightElement.getChildByName("Color").getFloatAttribute("a");
        // Create the light color instance
        Color lightColor = new Color(lr, lg, lb, la);
        // Validate the physics element
        /*XmlReader.Element physicsElement = rootElement.getChildByName(name).getChildByName("Physics");
        if(physicsElement == null || physicsElement.getChildByName("Mass") == null || physicsElement.getChildByName("Friction") == null
                || physicsElement.getChildByName("Restitution") == null)
            return null;

        // Get the part parameters
        float mass = physicsElement.getChildByName("Mass").getFloatAttribute("value");
        float friction = physicsElement.getChildByName("Friction").getFloatAttribute("value");
        float restitution = physicsElement.getChildByName("Restitution").getFloatAttribute("value");*/
        // Validate the movement element
        XmlReader.Element movementElement = rootElement.getChildByName(name).getChildByName("Movement");
        if(movementElement == null || movementElement.getChildByName("Simple") == null  || movementElement.getChildByName("Composed") == null)
            return null;

        // Get the simple movements count
        int simpleMovementsCount = movementElement.getChildByName("Simple").getIntAttribute("count");
        // Get the composed movement points count
        int composedMovementsCount = movementElement.getChildByName("Composed").getChildCount();

        // Create the model node
        ModelNode modelNode;
        if(shape.equals("PolygonParted"))
            modelNode = new PartedModelNode();
        else
            modelNode = new ModelNode();

        // Setup the model node
        modelNode.setNode(name, shape, size, radius, color, entryAnimation, lightDistance, lightColor, simpleMovementsCount, composedMovementsCount);
        // Get the composed movements element
        XmlReader.Element composedMovementsElement = movementElement.getChildByName("Composed");
        // Add all the composed movements types
        for(int i = 0; i < composedMovementsCount; i++)
        {
            int movement1 = composedMovementsElement.getChildByName("Type" + (i + 1)).getIntAttribute("movement1");
            int movement2 = composedMovementsElement.getChildByName("Type" + (i + 1)).getIntAttribute("movement2");
            float positionFix = composedMovementsElement.getChildByName("Type" + (i + 1)).getFloatAttribute("positionFix");
            modelNode.setComposedMovementTypes(i, movement1, movement2, positionFix);
        }
        // Create the movements array
        MovementNode[] movementNodes = new MovementNode[simpleMovementsCount];
        // Add it to the loaded movements map
        loadedMovements.put(modelName, movementNodes);
        // Load all the movements
        for(int i = 0; i < simpleMovementsCount; i++)
            loadMovement(modelName, i);

        // Load the model parts if its a parted model
        if(shape.equals("PolygonParted"))
            loadParts(modelName, (PartedModelNode)modelNode);

        // Add it to the loaded models
        loadedModels.put(modelName, modelNode);
        return modelNode;
    }

    /*public void unloadModel(String model)
    {
        // Unload the model
        if(loadedModels.containsKey(model))
            loadedModels.remove(model);
    }*/

    /*
        Will load the movement's XML file and store it into the movements map
    */
    private void loadMovement(String modelName, int movementId)
    {
        // Parse the settings file
        XmlReader.Element rootElement = Core.getInstance().getFileManager().parseMovementFile(modelName, movementId + 1);
        if(rootElement == null)
            return;

        // Create the movement node
        MovementNode movementNode = new MovementNode();
        // Get the type
        String type = rootElement.getChildByName("Type").get("value");
        // Get the points count
        int pointsCount = rootElement.getChildByName("Path").getIntAttribute("points");
        // Get the distance
        float distance = rootElement.getChildByName("Path").getFloatAttribute("distance");
        // Get the rotation value
        float rotationSpeed = rootElement.getChildByName("Rotation").getFloatAttribute("speed");
        // Setup the movement node
        movementNode.setNode(type, pointsCount, distance, rotationSpeed);
        // Load all the movement points
        for(int i = 0; i < pointsCount; i++)
        {
            // Find the x and y positions
            String x = rootElement.getChildByName("Point" + (i + 1)).get("x");
            String y = rootElement.getChildByName("Point" + (i + 1)).get("y");
            // Add the point
            movementNode.setPoint(i, x, y);
        }

        // Add it to the loaded movements
        loadedMovements.get(modelName)[movementId] = movementNode;
    }

    /*
       Will load the parts XML file and store it into the model node
    */
    private void loadParts(String modelName, PartedModelNode modelNode)
    {
        // Parse the parts settings file
        XmlReader.Element rootElement = Core.getInstance().getFileManager().parsePartsFile(modelName);
        if(rootElement == null)
            return;

        // Get the parts count
        int parts = rootElement.getChildCount();
        // Validate the parts
        if(parts == 0)
            return;

        // Set the model parts
        modelNode.setPartsCount(parts);
        // Loop through all the parts
        for(int i = 0; i < parts; i++)
        {
            // Get the part element
            XmlReader.Element partElement = rootElement.getChildByName("Part" + (i + 1));
            // Validate the part element
            if(partElement == null)
                break;

            // Get the part position
            float x = partElement.getChildByName("Position").getFloatAttribute("x");
            float y = partElement.getChildByName("Position").getFloatAttribute("y");
            // Get the part rotation
            float rotation = partElement.getChildByName("Rotation").getFloatAttribute("value");
            // Set the part
            modelNode.setPart(i, new Vector2(x, y), rotation);
        }
    }

    /*
        This will read vertices from an OBM file, if its a complex body part, then it'll read fixtures separately
        And resize the whole object when its done
    */
    public PolygonShape[] loadBody(ModelNode modelNode, String modelName)
    {
        // Make sure the body is not already loaded
        if(loadedBodies.containsKey(modelName))
            return loadedBodies.get(modelName);

        // Create highest and lowest vectors
        Vector2 lowest = null, highest = null;
        // Create the nodes array
        Array<EntityBodyPartNode> nodesArray = new Array<EntityBodyPartNode>();
        // Create the center array
        Array<Vector2> centerArray = new Array<Vector2>(Vector2.class);
        // Open the body part file
        InputStream inputStream = Core.getInstance().getFileManager().openBodyFile(modelName, modelNode.name);
        // Create the object input stream
        ObjectInputStream objectInputStream = null;
        try
        {
            // Create the object input stream
            objectInputStream = new ObjectInputStream(inputStream);
            // Loop until the end of file
            EntityBodyPartNode node = (EntityBodyPartNode)(objectInputStream.readObject());
            while(node != null)
            {
                // Add it to the nodes array
                nodesArray.add(node);
                // Add its center to the center array
                centerArray.add(GameMath.getVectorsArrayCenter(node.getVertices()));
                // Get the node size
                Vector2[] nodeSize = GameMath.getVectorsArraySize(node.getVertices());
                // Find the lowest point
                if(lowest == null)
                    lowest = new Vector2(nodeSize[1]);
                else
                {
                    if(nodeSize[1].x < lowest.x)
                        lowest.x = nodeSize[1].x;

                    if(nodeSize[1].y < lowest.y)
                        lowest.y = nodeSize[1].y;
                }
                // Find the highest point
                if(highest == null)
                    highest = new Vector2(nodeSize[2]);
                else
                {
                    if(nodeSize[2].x > highest.x)
                        highest.x = nodeSize[2].x;

                    if(nodeSize[2].y > highest.y)
                        highest.y = nodeSize[2].y;
                }
                // Get the next entity body part node
                node = (EntityBodyPartNode)(objectInputStream.readObject());
            }
            // Close the file
            objectInputStream.close();
        }
        catch(Exception e)
        {
            // Close the file
            try
            {
                objectInputStream.close();
            }
            catch(IOException e2)
            {
                e2.printStackTrace();
            }
            catch(NullPointerException e2)
            {
                e2.printStackTrace();
            }
        }
        // Create the shapes array
        PolygonShape[] shapes = new PolygonShape[nodesArray.size];
        int shapesIterator = 0;
        // Calculate the body size
        Vector2 bodySize = new Vector2(highest.x - lowest.x, highest.y - lowest.y);
        Vector2 center = new Vector2(bodySize.x / 2 + lowest.x, bodySize.y / 2 + lowest.y);
        // Loop through the nodes array
        for(EntityBodyPartNode node : nodesArray)
        {
            // Create the body part shape
            PolygonShape shape = new PolygonShape();
            // Resize the vertices array
            GameMath.resizeVectorsArray(node.getVertices(), bodySize, modelNode.size);
            // Get the node center relative to the body center
            Vector2 relativeCenter = centerArray.get(0).cpy().sub(center);
            // Resize the body part's center to match the new size
            GameMath.resizeVector(relativeCenter, bodySize, modelNode.size);
            // Set the body part origin
            GameMath.setOrigin(node.getVertices(), new Vector2(), relativeCenter);
            // Remove the center from the center array
            centerArray.removeIndex(0);
            // Set the shape vertices
            shape.set(node.getVerticesFloatArray());
            // Add it to the shape's array
            shapes[shapesIterator] = shape;
            // Increase the iterator count
            shapesIterator++;
        }
        // Add it to the loaded bodies
        loadedBodies.put(modelName, shapes);
        return shapes;
    }

    public MovementNode[] getMovementNodes(String modelName)
    {
        if(loadedMovements.containsKey(modelName))
            return loadedMovements.get(modelName);
        else
            return null;
    }

    public ModelNode getModelNode(String modelName)
    {
        if(loadedModels.containsKey(modelName))
            return loadedModels.get(modelName);
        else
            return null;
    }
}
