package com.ormisiclapps.slickyfuton.graphics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ScreenTouchType;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;

/**
 * Created by OrMisicL on 9/22/2015.
 */
public class UIDebug
{
    /*private static Vector2 inputPosition;
    private static Vector2 inputMovementPosition;
    private static ScreenTouchType inputTouchType;
    private static BitmapFont debugFont;
    private static BitmapFont debugTitleFont;
    private static Vector2 position;
    
    public static void initialize()
    {
        // Create font type generators
    	FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Debug.ttf"));
        // Create the font parameter instance
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        // Set the parameters
        parameter.color = Color.WHITE;
        parameter.size = 10;
        // Generate the font
        debugFont = fontGenerator.generateFont(parameter);
        // Set the title parameters
        parameter.color = Color.BLACK;
        parameter.size = 15;
        parameter.borderColor = Color.WHITE;
        parameter.borderWidth = 1.5f;
        // Generate the font
        debugTitleFont = fontGenerator.generateFont(parameter);
        // Dispose the font generator
        fontGenerator.dispose();
        // Create vectors
        inputPosition = new Vector2();
        inputMovementPosition = new Vector2();
        position = new Vector2();
    }

    public static void dispose()
    {
        debugFont.dispose();
        debugTitleFont.dispose();
    }

    public static void takeInputSnapShot()
    {
        // Save the input information
        inputPosition.set(Core.getInstance().getInputHandler().getScreenTouchPosition());
        inputMovementPosition.set(Core.getInstance().getInputHandler().getTouchMovementPosition());
        inputTouchType = Core.getInstance().getInputHandler().getScreenTouchType();
    }

    public static void render()
    {
        // Reset the position vector
        position.set(20f, Core.getInstance().getGraphicsManager().HEIGHT - 20);
        // Get the camera coordinates
        Vector2 camPosition = Camera.getInstance().getPosition();
        // Render camera information
        drawTitle("Camera debug information", position);
        drawText("State: " + getCameraStateAsString(), position.sub(0f, 20f));
        drawText("Position: " + camPosition.x + " " + camPosition.y, position.sub(0f, 15f));
        drawText("Zoom: " + Camera.getInstance().getZoom(), position.sub(0f, 15f));
        // Render input information
        drawTitle("Input debug information", position.sub(0f, 30f));
        drawText("Touch: " + getInputTouchAsString(), position.sub(0f, 20f));
        drawText("Position: " + inputPosition.x + " " + inputPosition.y, position.sub(0f, 15f));
        drawText("Movement: " + inputMovementPosition.x + " " + inputMovementPosition.y, position.sub(0f, 15f));
        // Render player information
        Vector2 playerPosition = Player.getInstance().getBody().getPosition();
        float playerRotation = Player.getInstance().getBody().getAngle();
        Vector2 playerVelocity = Player.getInstance().getBody().getLinearVelocity();
        float playerAngularVelocity = Player.getInstance().getBody().getAngularVelocity();
        drawTitle("Player debug information", position.sub(0f, 30f));
        drawText("Position: " + playerPosition.x + " " + playerPosition.y, position.sub(0f, 20f));
        drawText("Rotation: " + playerRotation, position.sub(0f, 15f));
        drawText("Velocity: " + playerVelocity.x + "  |  " + playerVelocity.y, position.sub(0f, 15f));
        drawText("Angular Velocity: " + playerAngularVelocity, position.sub(0f, 15f));
        // Render game information
        float gravity = GameWorld.getInstance().getGravity();
        drawTitle("Game debug information", position.sub(0f, 30f));
        drawText("Gravity: " + gravity, position.sub(0f, 20f));
        drawText("Size: " + Core.getInstance().getGraphicsManager().WIDTH + "x" + Core.getInstance().getGraphicsManager().HEIGHT, position.sub(0f, 15f));
        drawText("FPS: " + Core.getInstance().FPS, position.sub(0f, 15f));
    }
    
    private static void drawText(String text, Vector2 position)
    {
    	// Draw the text
        Core.getInstance().getGraphicsManager().drawText(debugFont, text, position);
    }

    private static void drawTitle(String text, Vector2 position)
    {
        // Draw the text
        Core.getInstance().getGraphicsManager().drawText(debugTitleFont, text, position);
    }

    public static String getCameraStateAsString()
    {
        // Return the state string according to the camera type
        switch(Camera.getInstance().getCameraState())
        {
            case CAMERA_STATE_FREE:
                return "Free";

            case CAMERA_STATE_FOLLOWING_ENTITY:
                return "Following entity";
        }
        return "Unknown";
    }

    public static String getInputTouchAsString()
    {
        // Return the state string according to the camera type
        switch(inputTouchType)
        {
            case SCREEN_TOUCH_NONE:
                return "None";

            case SCREEN_TOUCH_DOWN:
                return "Down";

            case SCREEN_TOUCH_UP:
                return "Up";

            case SCREEN_TOUCH_DRAGGED:
                return "Dragging";
        }
        return "Unknown";
    }*/
}
