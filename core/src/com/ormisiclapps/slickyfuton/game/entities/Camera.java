package com.ormisiclapps.slickyfuton.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.CameraState;
import com.ormisiclapps.slickyfuton.enumerations.EntityType;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.Entity;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 9/18/2015.
 */
public class Camera
{
    // Statics
    /*private static final float CAMERA_DEBUG_SPEED = 0.1f;
    private static final float CAMERA_ZOOM_SPEED = 0.004f;*/
    private static final float CAMERA_DEFAULT_INTERPOLATING_SPEED = 0.035f;

    private OrthographicCamera camera;
    private CameraState state;
    private Entity followedEntity;
    private boolean rightSidedFollow;
    private boolean interpolatingToSide;
    private boolean interpolatingToEntity;
    private float interpolationFactor;
    private float interpolationSpeed;

    private static Camera instance;

    /*
        These vectors are used to reduce the number of allocations (new) inside the frequently used methods
    */
    private final Vector2 position;
    private final Vector2 project;
    private final Vector3 vector3Tmp;

    public Camera()
    {
        // Reset flags
        rightSidedFollow = false;
        interpolatingToSide = false;
        interpolatingToEntity = false;
        // Reset values
        interpolationFactor = 0;
        // Create vectors
        position = new Vector2();
        project = new Vector2();
        vector3Tmp = new Vector3();
        // Get width and height
        float width = Core.getInstance().getGraphicsManager().WIDTH;
        float height = Core.getInstance().getGraphicsManager().HEIGHT;
        // Create the camera instance
        camera = new OrthographicCamera(GameMath.metersPerPixels(width), GameMath.metersPerPixels(height));
        // Set the camera view
        camera.setToOrtho(false, GameMath.metersPerPixels(width), GameMath.metersPerPixels(height));
        // Set the camera to be free
        setFree();
        // Set the instance
        instance = this;
    }

    public void update()
    {        
    	// Update the camera depending on its state
        if(state == CameraState.CAMERA_STATE_FOLLOWING_ENTITY/* && !Core.getInstance().isDebugging*/)
        {
            // If its not a static entity
            if(followedEntity.getType() != EntityType.ENTITY_TYPE_OBJECT)
            {
            	// Get the entity position
            	Vector2 entityPosition = followedEntity.getPosition();
                // Process interpolation
                if(interpolatingToSide)
                {
                    // Calculate the new interpolating factor
                    interpolationFactor += interpolationSpeed;
                    // Calculate the entity position to be at the interpolated position
                    position.set(entityPosition.x + GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH)
                            / 3 * interpolationFactor, GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().HEIGHT / 2.23f));

                    // Check for the interpolation finish
                    if(interpolationFactor <= -1 || interpolationFactor >= 1)
                        interpolatingToSide = false;

                }
                else if(interpolatingToEntity)
                {
                    // Calculate the new interpolated distance
                    float interpolatedDistance = interpolationSpeed * Core.getInstance().getGraphicsManager().DELTA_TIME;
                    // Calculate the entity position to be at the interpolated position
                    position.set(position.x + interpolatedDistance, GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().HEIGHT / 2.23f));
                    // Check for the interpolation finish
                    float w = Core.getInstance().getGraphicsManager().WIDTH;
                    if((interpolationSpeed >= 0f && position.x >= followedEntity.getPosition().x + GameMath.metersPerPixels(w) / 3) ||
                            (interpolationSpeed < 0f && position.x <= followedEntity.getPosition().x + GameMath.metersPerPixels(w) / 3))
                        interpolatingToEntity = false;
                }
                else
            	    // Calculate the entity position to be at the 1/6 of the screen
                    position.set(entityPosition.x + GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH)
                        / (rightSidedFollow ? -3 : 3), GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().HEIGHT / 2.23f));
            }
           
        }
        // Process camera debug
        /*if(Core.getInstance().isDebug && Core.getInstance().isDebugging)
        {	
	        // Camera movement
        	if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8))
	        	position.y += CAMERA_DEBUG_SPEED;
	       	else if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2))
                position.y -= CAMERA_DEBUG_SPEED;
	       	else if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4))
	       		position.x -= CAMERA_DEBUG_SPEED;
	       	else if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6))
	       		position.x += CAMERA_DEBUG_SPEED;
        		
        	// Camera zoom
        	if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1))
        		camera.zoom -= CAMERA_ZOOM_SPEED;
        	else if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_3))
        		camera.zoom += CAMERA_ZOOM_SPEED;
        }*/
        // Update the camera position
        updatePosition(position);
        // Update the camera
        camera.update();
    }

    public void setFree()
    {
        // Set the camera state to free
        state = CameraState.CAMERA_STATE_FREE;
        // Reset other variables
        followedEntity = null;
    }

    /*public void followEntity(Entity entity)
    {
        // Set the camera state to follow the entity
        state = CameraState.CAMERA_STATE_FOLLOWING_ENTITY;
        // Set the entity to follow
        followedEntity = entity;
        // Get the entity position
        Vector2 entityPosition = followedEntity.getPosition();
        // Calculate the entity position to be at the 1/6 of the screen
        Vector2 cameraPosition = new Vector2(entityPosition.x + GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().WIDTH) / 3,
                GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().HEIGHT / 2.25f));

        // Update the camera position
        setPosition(cameraPosition);
        // Update the camera
        camera.update();
    }*/

    public void interpolateToEntity(Entity entity, float speed)
    {
        // Set the camera state to follow the entity
        state = CameraState.CAMERA_STATE_FOLLOWING_ENTITY;
        // Set the entity to follow
        followedEntity = entity;
        // Set the interpolation speed
        interpolationSpeed = getPosition().x > entity.getPosition().x ? -speed : speed;
        // Toggle the entity interpolating flag
        interpolatingToEntity = true;
        // Fix the camera y position
        setPosition(new Vector2(position.x, GameMath.metersPerPixels(Core.getInstance().getGraphicsManager().HEIGHT / 2.25f)));
    }

    public void toggleRightSidedFollow(boolean rightSided)
    {
        toggleRightSidedFollow(rightSided, CAMERA_DEFAULT_INTERPOLATING_SPEED);

    }

    public void toggleRightSidedFollow(boolean rightSided, float speed)
    {
        // Ensure the camera state
        if(state != CameraState.CAMERA_STATE_FOLLOWING_ENTITY)
            return;

        // Set the right sided flag
        this.rightSidedFollow = rightSided;
        // Set the interpolating flag
        interpolatingToSide = true;
        // Initialize the interpolating factor
        interpolationFactor = rightSided ? 1 : -1;
        // Set the interpolation speed
        interpolationSpeed = rightSided ? -speed : speed;

    }

    public OrthographicCamera getInterface() { return camera; }

    public Vector2 getPosition()
    {
        return position;
    }

    public void setPosition(Vector2 position)
    {
        vector3Tmp.set(position.x, position.y, 0);
        camera.position.set(vector3Tmp);
        this.position.set(position);
    }

    private void updatePosition(Vector2 position)
    {
        vector3Tmp.set(position.x, position.y, 0);
        camera.position.set(vector3Tmp);
    }

    public Vector2 worldToScreen(Vector2 coordinates)
    {
        // Project the camera
        vector3Tmp.set(coordinates.x, coordinates.y, 0);
    	Vector3 camProjection = camera.project(vector3Tmp);
    	return project.set(camProjection.x, camProjection.y);
    }
    
    public Vector2 screenToWorld(Vector2 coordinates)
    {
        // Project the camera
        vector3Tmp.set(coordinates.x, coordinates.y, 0);
    	Vector3 camProjection = camera.unproject(vector3Tmp);
    	return project.set(camProjection.x, camProjection.y);
    }
    
    public CameraState getCameraState() { return state; }

    public boolean isInterpolatingToSide() { return interpolatingToSide; }

    public void resetInterpolatingToSide() { interpolatingToSide = false; }

    public void resetRightSidedFollow() { rightSidedFollow = false; }

    public float getZoom() { return camera.zoom; }

    public static Camera getInstance()
    {
        return instance;
    }
}
