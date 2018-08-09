package com.ormisiclapps.slickyfuton.utility;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.nodes.combination.*;

/**
 * Created by Anis on 11/27/2016.
 */

public class PreSetCombinations
{
    private StaticCombinationNode[] staticCombinationNodes;
    private SimpleCombinationNode[] simpleCombinationNodes;
    private ComposedCombinationNode[] composedCombinationNodes;
    private ChainCombinationNode[] chainCombinationNodes;
    private PartedCombinationNode[] partedCombinationNodes;
    private GravityCombinationNode[] gravityCombinationNodes;

    public PreSetCombinations()
    {
        // Reset instances
        staticCombinationNodes = null;
        simpleCombinationNodes = null;
        composedCombinationNodes = null;
        chainCombinationNodes = null;
        partedCombinationNodes = null;
        gravityCombinationNodes = null;
    }

    public boolean load()
    {
        // Parse the combinations file
        XmlReader.Element rootElement = Core.getInstance().getFileManager().parseConfigurationFile("Models/PreSetCombinations.oac");
        if(rootElement == null)
            return false;

        // Find the static combinations element
        XmlReader.Element staticCombinationsElement = rootElement.getChildByName("StaticCombinations");
        if(staticCombinationsElement == null || staticCombinationsElement.getChildCount() == 0)
            return false;

        // Create the static combination nodes array
        staticCombinationNodes = new StaticCombinationNode[staticCombinationsElement.getChildCount()];
        // Loop through the static combinations
        for(int i = 0; i < staticCombinationsElement.getChildCount(); i++)
        {
            // Get the combination
            XmlReader.Element combinationElement = staticCombinationsElement.getChildByName("Combination" + (i + 1));
            // Validate the element
            if(combinationElement == null || combinationElement.getChildCount() == 0)
                continue;

            // Get the movement speed
            float speed = combinationElement.getFloatAttribute("speed");
            // Get the end distance
            float endDistance = combinationElement.getFloatAttribute("endDistance");
            // Create a combination node
            staticCombinationNodes[i] = new StaticCombinationNode(speed, endDistance, combinationElement.getChildCount());
            // Loop through the combination objects
            for(int j = 0; j < combinationElement.getChildCount(); j++)
            {
                // Get the object
                XmlReader.Element objectElement = combinationElement.getChildByName("Object" + (j + 1));
                // Validate the element
                if(objectElement == null)
                    continue;

                // Get the attributes
                String model = objectElement.get("model");
                String position = objectElement.get("position");
                float distance = objectElement.getFloat("distance");
                boolean passable = objectElement.getBoolean("passable");
                // Add the object to the combination
                staticCombinationNodes[i].addObject(j, model, position, distance, passable);
            }
        }

        // Find the simple combinations element
        XmlReader.Element simpleCombinationsElement = rootElement.getChildByName("SimpleCombinations");
        if(simpleCombinationsElement == null || simpleCombinationsElement.getChildCount() == 0)
            return false;

        // Create the simple combination nodes array
        simpleCombinationNodes = new SimpleCombinationNode[simpleCombinationsElement.getChildCount()];
        // Loop through the simple combinations
        for(int i = 0; i < simpleCombinationsElement.getChildCount(); i++)
        {
            // Get the combination
            XmlReader.Element combinationElement = simpleCombinationsElement.getChildByName("Combination" + (i + 1));
            // Validate the element
            if(combinationElement == null || combinationElement.getChildCount() == 0)
                continue;

            // Get the movement speed
            float speed = combinationElement.getFloatAttribute("speed");
            // Get the end distance
            float endDistance = combinationElement.getFloatAttribute("endDistance");
            // Create a combination node
            simpleCombinationNodes[i] = new SimpleCombinationNode(speed, endDistance, combinationElement.getChildCount());
            // Loop through the combination objects
            for(int j = 0; j < combinationElement.getChildCount(); j++)
            {
                // Get the object
                XmlReader.Element objectElement = combinationElement.getChildByName("Object" + (j + 1));
                // Validate the element
                if(objectElement == null)
                    continue;

                // Get the attributes
                String model = objectElement.get("model");
                int movement = objectElement.getInt("movement");
                String position = objectElement.get("position");
                int startingPoint = objectElement.getInt("startingPoint");
                float distance = objectElement.getFloat("distance");
                boolean passable = objectElement.getBoolean("passable");
                // Add the object to the combination
                simpleCombinationNodes[i].addObject(j, model, movement, position, startingPoint, distance, passable);
            }
        }

        // Find the composed combinations element
        XmlReader.Element composedCombinationsElement = rootElement.getChildByName("ComposedCombinations");
        if(composedCombinationsElement == null || composedCombinationsElement.getChildCount() == 0)
            return false;

        // Create the composed combination nodes array
        composedCombinationNodes = new ComposedCombinationNode[composedCombinationsElement.getChildCount()];
        // Loop through the simple combinations
        for(int i = 0; i < composedCombinationsElement.getChildCount(); i++)
        {
            // Get the combination
            XmlReader.Element combinationElement = composedCombinationsElement.getChildByName("Combination" + (i + 1));
            // Validate the element
            if(combinationElement == null || combinationElement.getChildCount() == 0)
                continue;

            // Get the movement speed
            float speed = combinationElement.getFloatAttribute("speed");
            // Get the end distance
            float endDistance = combinationElement.getFloatAttribute("endDistance");
            // Create a combination node
            composedCombinationNodes[i] = new ComposedCombinationNode(speed, endDistance, combinationElement.getChildCount());
            // Loop through the combination objects
            for(int j = 0; j < combinationElement.getChildCount(); j++)
            {
                // Get the object
                XmlReader.Element objectElement = combinationElement.getChildByName("Object" + (j + 1));
                // Validate the element
                if(objectElement == null)
                    continue;

                // Get the attributes
                String model = objectElement.get("model");
                int movement = objectElement.getInt("movement");
                int startingPoint1 = objectElement.getInt("startingPoint1");
                int startingPoint2 = objectElement.getInt("startingPoint2");
                float distance = objectElement.getFloat("distance");
                boolean passable = objectElement.getBoolean("passable");
                // Add the object to the combination
                composedCombinationNodes[i].addObject(j, model, movement, startingPoint1, startingPoint2, distance, passable);
            }
        }

        // Find the chain combinations element
        XmlReader.Element chainCombinationsElement = rootElement.getChildByName("ChainCombinations");
        if(chainCombinationsElement == null || chainCombinationsElement.getChildCount() == 0)
            return false;

        // Create the chain combination nodes array
        chainCombinationNodes = new ChainCombinationNode[chainCombinationsElement.getChildCount()];
        // Loop through the simple combinations
        for(int i = 0; i < chainCombinationsElement.getChildCount(); i++)
        {
            // Get the combination
            XmlReader.Element combinationElement = chainCombinationsElement.getChildByName("Combination" + (i + 1));
            // Validate the element
            if(combinationElement == null || combinationElement.getChildCount() == 0)
                continue;

            // Get the end distance
            float endDistance = combinationElement.getFloatAttribute("endDistance");
            // Create a combination node
            chainCombinationNodes[i] = new ChainCombinationNode(endDistance, combinationElement.getChildCount());
            // Loop through the combination objects
            for(int j = 0; j < combinationElement.getChildCount(); j++)
            {
                // Get the object
                XmlReader.Element objectElement = combinationElement.getChildByName("Object" + (j + 1));
                // Validate the element
                if(objectElement == null)
                    continue;

                // Get the attributes
                String model = objectElement.get("model");
                boolean passable = objectElement.getBoolean("passable");
                String flags = objectElement.get("flags");
                int points = objectElement.getChildCount() / 2;
                // Add the object to the combination
                chainCombinationNodes[i].addObject(j, model, passable, flags, points);
                // Loop through the object points
                for(int z = 0; z < points; z++)
                {
                    // Get the point
                    XmlReader.Element groundPointElement = objectElement.getChildByName("GroundPoint" + (z + 1));
                    XmlReader.Element roofPointElement = objectElement.getChildByName("RoofPoint" + (z + 1));
                    // Validate the element
                    if (groundPointElement == null || roofPointElement == null)
                        continue;

                    // Get the coordinates
                    float x = groundPointElement.getFloat("x");
                    float y = groundPointElement.getFloat("y");
                    float x1 = roofPointElement.getFloat("x");
                    float y1 = roofPointElement.getFloat("y");
                    // Add the point
                    chainCombinationNodes[i].addObjectPoint(j, z, new Vector2(x, y), new Vector2(x1, y1));
                }
            }
        }

        // Find the parted combinations element
        XmlReader.Element partedCombinationsElement = rootElement.getChildByName("PartedCombinations");
        if(partedCombinationsElement == null || partedCombinationsElement.getChildCount() == 0)
            return false;

        // Create the parted combination nodes array
        partedCombinationNodes = new PartedCombinationNode[partedCombinationsElement.getChildCount()];
        // Loop through the parted combinations
        for(int i = 0; i < partedCombinationsElement.getChildCount(); i++)
        {
            // Get the combination
            XmlReader.Element combinationElement = partedCombinationsElement.getChildByName("Combination" + (i + 1));
            // Validate the element
            if(combinationElement == null || combinationElement.getChildCount() == 0)
                continue;

            // Get the movement speed
            float speed = combinationElement.getFloatAttribute("speed");
            // Get the end distance
            float endDistance = combinationElement.getFloatAttribute("endDistance");
            // Create a combination node
            partedCombinationNodes[i] = new PartedCombinationNode(speed, endDistance, combinationElement.getChildCount());
            // Loop through the combination objects
            for(int j = 0; j < combinationElement.getChildCount(); j++)
            {
                // Get the object
                XmlReader.Element objectElement = combinationElement.getChildByName("Object" + (j + 1));
                // Validate the element
                if(objectElement == null)
                    continue;

                // Get the attributes
                String model = objectElement.get("model");
                float distance = objectElement.getFloat("distance");
                boolean passable = objectElement.getBoolean("passable");
                int startingPoint = objectElement.getInt("startingPoint");
                // Add the object to the combination
                partedCombinationNodes[i].addObject(j, model, startingPoint, distance, passable);
            }
        }

        // Find the gravity combinations element
        XmlReader.Element gravityCombinationsElement = rootElement.getChildByName("GravityCombinations");
        if(gravityCombinationsElement == null || gravityCombinationsElement.getChildCount() == 0)
            return false;

        // Create the gravity combination nodes array
        gravityCombinationNodes = new GravityCombinationNode[gravityCombinationsElement.getChildCount()];
        // Loop through the gravity combinations
        for(int i = 0; i < gravityCombinationsElement.getChildCount(); i++)
        {
            // Get the combination
            XmlReader.Element combinationElement = gravityCombinationsElement.getChildByName("Combination" + (i + 1));
            // Validate the element
            if(combinationElement == null || combinationElement.getChildCount() == 0)
                continue;

            // Get the movement speed
            float speed = combinationElement.getFloatAttribute("speed");
            // Get the end distance
            float endDistance = combinationElement.getFloatAttribute("endDistance");
            // Create a combination node
            gravityCombinationNodes[i] = new GravityCombinationNode(speed, endDistance, combinationElement.getChildCount());
            // Loop through the combination objects
            for(int j = 0; j < combinationElement.getChildCount(); j++)
            {
                // Get the object
                XmlReader.Element objectElement = combinationElement.getChildByName("Object" + (j + 1));
                // Validate the element
                if(objectElement == null)
                    continue;

                // Get the attributes
                String model = objectElement.get("model");
                String position = objectElement.get("position");
                int movement = objectElement.getInt("movement");
                int startingPoint = objectElement.getInt("startingPoint");
                float distance = objectElement.getFloat("distance");
                boolean passable = objectElement.getBoolean("passable");
                // Add the object to the combination
                gravityCombinationNodes[i].addObject(j, model, position, movement, startingPoint, distance, passable);
            }
        }
        return true;
    }

    public StaticCombinationNode getStaticCombinationNode(int id)
    {
        return staticCombinationNodes[id];
    }

    public int getStaticCombinationsCount()
    {
        return staticCombinationNodes.length;
    }

    public SimpleCombinationNode getSimpleCombinationNode(int id)
    {
        return simpleCombinationNodes[id];
    }

    public int getSimpleCombinationsCount()
    {
        return simpleCombinationNodes.length;
    }

    public ComposedCombinationNode getComposedCombinationNode(int id)
    {
        return composedCombinationNodes[id];
    }

    public int getComposedCombinationsCount()
    {
        return composedCombinationNodes.length;
    }

    public ChainCombinationNode getChainCombinationNode(int id)
    {
        return chainCombinationNodes[id];
    }

    public int getChainCombinationsCount()
    {
        return chainCombinationNodes.length;
    }

    public PartedCombinationNode getPartedCombinationNode(int id)
    {
        return partedCombinationNodes[id];
    }

    public int getPartedCombinationsCount()
    {
        return partedCombinationNodes.length;
    }

    public GravityCombinationNode getGravityCombinationNode(int id)
    {
        return gravityCombinationNodes[id];
    }

    public int getGravityCombinationsCount()
    {
        return gravityCombinationNodes.length;
    }
}
