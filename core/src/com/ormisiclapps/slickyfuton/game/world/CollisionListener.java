package com.ormisiclapps.slickyfuton.game.world;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;

import java.util.HashMap;
import java.util.Map;

public class CollisionListener implements ContactListener
{
	private Map<Body, Body> collisions;

    protected CollisionListener()
    {
        // Create the collisions map
        collisions = new HashMap<Body, Body>();
    }

    @Override
    public void endContact(Contact contact)
    {
        // Debug doesn't need collision
       // if(Core.getInstance().isDebug)
         //   return;

		// Get the bodies
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
        // Remove the collision
        if(collisions.get(bodyA) == bodyB)
            collisions.remove(bodyA);
    }

	@Override
    public void beginContact(Contact contact)
	{
		// Debug doesn't need collision
		//if(Core.getInstance().isDebug)
		//	return;

        // Get the bodies
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
        // Remove redundant collisions
        // NOTE: These usually occur when colliding complex bodies, due to their multiple fixture composition
        // The engine might trigger the same collision twice in a row if it fails to solve before the second set of
        // fixtures collision, (oh oh, and yeah, we're not going to make every body a bullet body, resources bitch !!)
        if(collisions.get(bodyA) == bodyB)
            return;

        // Add the collision
        collisions.put(bodyA, bodyB);
		// Process player's collision
        Player.getInstance().beginCollision(bodyA, bodyB, contact.getFixtureA(), contact.getFixtureB(),
                contact.getWorldManifold().getPoints()[0]);
    }

	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
    {
        // Get the bodies
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        // Disable collision for player and objects
        if((Player.getInstance().getBody() == bodyA && Terrain.getInstance().isObject(bodyB)) ||
                (Player.getInstance().getBody() == bodyB && Terrain.getInstance().isObject(bodyA)))
            contact.setEnabled(false);
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
    {
	    // Nothing to do here
	}
}
