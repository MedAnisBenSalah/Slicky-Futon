package com.ormisiclapps.slickyfuton.game.nodes.entity;

import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.utility.GameMath;

import java.io.Serializable;

public class EntityBodyPartNode implements Serializable
{
	public static final long serialVersionUID = -1;
	private Vector2[] vertices;
	private int verticesCount;
	
	public EntityBodyPartNode()	{
		// Reset the vertices array
		vertices = null; 
		// Reset the vertices count
		verticesCount = 0;
	}
	
	public EntityBodyPartNode(Vector2[] vertices, int verticesCount)
	{
		// Set the vertices array
		this.vertices = vertices; 
		// Set the vertices count
		this.verticesCount = verticesCount;
	}
	
	public Vector2[] getVertices()
	{
		return vertices;
	}
	
	public float[] getVerticesFloatArray()
	{
		return GameMath.convertToFloatArray(vertices);
	}
	
	public int getVerticesCount()
	{
		return verticesCount;
	}
}
