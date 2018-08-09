package com.ormisiclapps.slickyfuton.game.nodes.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ModelNode
{
	public String name;
	public String shape;
	public Vector2 size;
	public float radius;
	public Color color;
	public boolean entryAnimation;

	public float lightDistance;
	public Color lightColor;

	// Unused values
	/*public float mass;
	public float friction;
	public float restitution;*/

	public int simpleMovementsCount;
	public int composedMovementsCount;
	public int[] composedMovement1;
	public int[] composedMovement2;
	public float[] composedMovementPositionFix;

	public ModelNode()
	{
		// Create the vectors
		size = new Vector2();
	}

	public void setNode(String name, String shape, Vector2 size, float radius, Color color, boolean entryAnimation, float lightDistance,
                        Color lightColor, int simpleMovementsCount, int composedMovementsCount)
	{
		this.name = name;
		this.shape = shape;
		this.size.set(size);
		this.radius = radius;
        this.color = new Color(color);
        this.entryAnimation = entryAnimation;

		this.lightDistance = lightDistance;
		this.lightColor = new Color(lightColor);

		this.simpleMovementsCount = simpleMovementsCount;
        this.composedMovementsCount = composedMovementsCount;
		// Create arrays
        composedMovement1 = new int[composedMovementsCount];
        composedMovement2 = new int[composedMovementsCount];
        composedMovementPositionFix = new float[composedMovementsCount];
	}

	public void setComposedMovementTypes(int index, int movement1, int movement2, float positionFix)
	{
        composedMovement1[index] = movement1;
        composedMovement2[index] = movement2;
        composedMovementPositionFix[index] = positionFix;
	}
}