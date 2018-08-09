package com.ormisiclapps.slickyfuton.game.entities.physical;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.Entity;
import com.ormisiclapps.slickyfuton.game.screens.GameScreen;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.EntityType;
import com.ormisiclapps.slickyfuton.game.entities.utility.Point;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 9/18/2015.
 */
public class Player extends Entity
{
    private boolean screenTouched;
    private Point[] points;
    private Point[] smoothedPoints;
    private int lastPointIndex;
    private int verticesIndex;
    private float[] vertices;
    private boolean shiftPointsLocked;
    private Texture tailTexture, coloredTailTexture;
    private boolean dying;
    private Vector2 tmpVector;
    private boolean started;
    private boolean gravityCollision;
    private boolean gravityDirectionUp;
    private boolean flyingToMiddle;
    private float startInitialPosition;
    private TextureRegion defaultSkin, skin, tmpTextureRegion;
    private Color secondaryColor, tailColor, secondaryTailColor;
    private int tailSkinId;
    private boolean tailState, rotationState;
    private ParticleEffect dyingEffect, explosionEffect;
    private Texture skinsTexture;
    private float verticalVelocity;
    private boolean controlTaken;
    private float shuffleDistance;
    private boolean exploding;
    private float gotoPosition, gotoSpeed;

    private static Player instance;

    private static final int TAIL_POINTS = 10;
    private static final int VERTICES_COUNT = TAIL_POINTS * 20 * 4;
    private static final float ANGULAR_VELOCITY = 2f;
    public static final float NOT_STARTED_POSITION = -400f;
    private static final float NOT_STARTED_ZIGZAG_DISTANCE = 4f;
    private static final float TAIL_ALPHA = 0.35f;

    private static final int SKINS_PER_LINE = 16;
    private static int SKIN_SIZE = 128;
    public static final int SKINS = 32;

    private static int TAIL_SKIN_SIZE = 32;
    public static int TAIL_SKINS = 5;

    public static final Color DEFAULT_COLOR = new Color(0.18f, 0.92f, 0.93f, 1f);

    public static final float MAX_LIGHT_DISTANCE = 2.5f;

    public Player()
    {
        // Create the actor instance
        super("Player", EntityType.ENTITY_TYPE_PLAYER);
        // Reset flags
        screenTouched = false;
        gravityCollision = false;
        gravityDirectionUp = false;
        flyingToMiddle = false;
        shiftPointsLocked = false;
        controlTaken = false;
        exploding = false;
        // Reset instances
        points = null;
        // Get the skins texture
        skinsTexture = Core.getInstance().getResourcesManager().getResource("Objects/Skins", ResourceType.RESOURCE_TYPE_TEXTURE);
        skinsTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Get the tail's texture
        tailTexture = Core.getInstance().getResourcesManager().getResource("Objects/TailSkins", ResourceType.RESOURCE_TYPE_TEXTURE);
        tailTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Create the colored tail texture
        coloredTailTexture = new Texture(32, 32, Pixmap.Format.RGBA8888);
        coloredTailTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Create vectors
        tmpVector = new Vector2();
        // Create color instances
        secondaryColor = new Color(Color.WHITE);
        tailColor = new Color();
        secondaryTailColor = new Color(Color.BLACK);
        // Reset values
        startInitialPosition = Configuration.TERRAIN_ROOF_POSITION / 2f;
        // Reset tail's skin id
        tailSkinId = 1;
        // Create the dying effect
        dyingEffect = new ParticleEffect();
        explosionEffect = new ParticleEffect();
        // Load the dying effect
        dyingEffect.load(Gdx.files.internal("Effects/Dying.oae"), Gdx.files.internal("Effects"));
        // Load the explosion effect
        explosionEffect.load(Gdx.files.internal("Effects/Explosion.oae"), Gdx.files.internal("Effects"));
        // Get the size
        float size = GameMath.pixelsPerMeters(4f);
        // Get the current height
        float height = dyingEffect.getEmitters().first().getScale().getHighMax() + dyingEffect.getEmitters().first().getScale().getLowMax();
        // Calculate the scale
        float scale = size / height;
        // Reset values
        gotoPosition = -1f;
        gotoSpeed = 0f;
        // Scale the effect
        dyingEffect.scaleEffect(scale);
        // Set the new size
        size = GameMath.pixelsPerMeters(15f);
        // Get the current height
        height = explosionEffect.getEmitters().first().getScale().getHighMax() + explosionEffect.getEmitters().first().getScale().getLowMax();
        // Calculate the scale
        scale = size / height;
        // Scale the effect
        explosionEffect.scaleEffect(scale);
        // Set the instance
        instance = this;
    }

    @Override
    public void create()
    {
        // Create the entity
        super.create();
        // Set it to be a bullet body (for a pixel perfect collision)
        setBulletBody(true);
        // Set fixed rotation
        setFixedRotation(true);
        // Reset player's position
        setPosition(new Vector2(NOT_STARTED_POSITION, Configuration.TERRAIN_ROOF_POSITION / 2));
        // Create the tail effect
        setupTailEffect();
        // Set the tail color
        tailColor.set(getColor());
        // Set it to be the default region
        skin = new TextureRegion(skinsTexture, SKIN_SIZE, SKIN_SIZE);
        // Set the default skin
        defaultSkin = new TextureRegion(skinsTexture, SKIN_SIZE, SKIN_SIZE);
        // Create the texture region instance
        tmpTextureRegion = new TextureRegion(skinsTexture);
    }

    public void setup()
    {
        // Reset values
        startInitialPosition = Configuration.TERRAIN_ROOF_POSITION / 2f;
        // Reset player's position
        setPosition(new Vector2(NOT_STARTED_POSITION, Configuration.TERRAIN_ROOF_POSITION / 2f));
        // Create the tail effect
        setupTailEffect();
        // Reset flags
        dying = false;
        screenTouched = false;
        started = false;
        flyingToMiddle = false;
        controlTaken = false;
        gotoPosition = -1f;
        gotoSpeed = 0f;
        // Set the player's initial velocity
        setVelocity(new Vector2(0f, Configuration.PLAYER_VERTICAL_VELOCITY));
        // Load the player
        load();
    }

    public void load()
    {
        // Set parameters
        setSkin(Core.getInstance().getStatsSaver().savedData.textureId);
        setTailSkin(Core.getInstance().getStatsSaver().savedData.tailTextureId);
        setColor(Core.getInstance().getStatsSaver().savedData.primaryColor);
        setSecondaryColor(Core.getInstance().getStatsSaver().savedData.secondaryColor);
        setTailColor(Core.getInstance().getStatsSaver().savedData.tailColor);
        setSecondaryTailColor(Core.getInstance().getStatsSaver().savedData.secondaryTailColor);
        setTailState(Core.getInstance().getStatsSaver().savedData.tailState);
        setRotationState(Core.getInstance().getStatsSaver().savedData.rotationState);
        setLightState(Core.getInstance().getStatsSaver().savedData.lightState);
        setLightColor(Core.getInstance().getStatsSaver().savedData.lightColor);
        setLightDistance(Core.getInstance().getStatsSaver().savedData.lightDistance);
    }

    private void setupTailEffect()
    {
        // Create the points array
        if(points == null)
        {
            points = new Point[TAIL_POINTS];
            // Create the tail points
            for(int i = 0; i < TAIL_POINTS; i++)
                points[i] = new Point();

            // Create the smoothed points array
            smoothedPoints = new Point[TAIL_POINTS * 2];
            // Create the smoothed points
            for(int i = 0; i < TAIL_POINTS * 2; i++)
                smoothedPoints[i] = new Point();
        }
        // Create the vertices array
        if(vertices == null)
            vertices = new float[VERTICES_COUNT];

        // Reset the vertices index
        verticesIndex = 0;
        // Reset the last point index
        lastPointIndex = 0;
        // Reset flags
        shiftPointsLocked = false;
    }

    @Override
    public void destroy()
    {
    	// Destroy the entity
    	super.destroy();
    }

    @Override
    public void process()
    {
        /*if(Gdx.input.isKeyJustPressed(Input.Keys.D))
            kill(getPosition());*/

        // Set the player's velocity
        if(GameLogic.getInstance().isEndless() || !(Core.getInstance().getScreensManager().getCurrentScreen() instanceof GameScreen))
            verticalVelocity = GameIntelligence.getInstance().getPlayerVelocity();

        // Keep rotating the player
        setAngularVelocity(rotationState ? ANGULAR_VELOCITY : 0f);
    	// Don't update the player if we're starting
        if(!started)
        {
            // Get the player's Y velocity
            float yVelocity = getVelocity().y;
            // Check if we need to rotate
            if((yVelocity > 0f && getPosition().y >= startInitialPosition + NOT_STARTED_ZIGZAG_DISTANCE) ||
                    (yVelocity < 0f && getPosition().y <= startInitialPosition - NOT_STARTED_ZIGZAG_DISTANCE))
            {
                // Fix the position
                setPosition(tmpVector.set(getPosition().x, yVelocity > 0f ? startInitialPosition + NOT_STARTED_ZIGZAG_DISTANCE :
                        startInitialPosition - NOT_STARTED_ZIGZAG_DISTANCE));

                // Invert velocity
                yVelocity = -yVelocity;
                // Set rotation
                //setRotationInDegrees(yVelocity >= 0f ? Configuration.PLAYER_UPPER_ROTATION : Configuration.PLAYER_LOWER_ROTATION);
            }
            setVelocity(tmpVector.set(verticalVelocity, yVelocity));
            // Process the entity
            super.process();
            // Process the tail effect
            processTail();
            return;
        }
        // Don't update the player if we're dying
        if(dying)
        {
            // Check if we've finished the death animation
            if(dyingEffect.isComplete())
            {
                // Notify the game lost
                GameLogic.getInstance().onLostGame();
                // Reset the dying flag
                dying = false;
            }
            // Process the tail effect
            processTail();
            return;
        }
        // Don't update the player if we're exploding
        if(exploding)
        {
            // Check if we've finished the explosion animation
            if(explosionEffect.isComplete())
            {
                // Notify the game lost
                GameLogic.getInstance().onLevelCompleted();
                // Reset the exploding flag
                exploding = false;
            }
            // Process the tail effect
            processTail();
            return;
        }
        // If we're flying to middle
        if(flyingToMiddle)
        {
            // Are we in the middle
            if(Math.abs((Configuration.TERRAIN_ROOF_POSITION / 2f) - getPosition().y) <= 4f)
                // Reset vertical velocity
                setVelocity(tmpVector.set(verticalVelocity, 0f));
            else
                setVelocity(tmpVector.set(verticalVelocity,
                        getVelocity().y > 0 ? Configuration.PLAYER_VERTICAL_VELOCITY : -Configuration.PLAYER_VERTICAL_VELOCITY));

            // Reset flying to middle flag
            if(GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING)
                flyingToMiddle = false;
            else
            {
                // Process the entity
                super.process();
                // Process the tail effect
                processTail();
                return;
            }
        }
        // Process goto position
        if(gotoPosition != -1f)
        {
            // Are we there yet ?
            if((getPosition().y >= gotoPosition && gotoSpeed > 0f) || (getPosition().y <= gotoPosition && gotoSpeed < 0f))
            {
                // Fix position
                setPosition(tmpVector.set(getPosition().x, gotoPosition));
                // Restore velocity
                //setVelocity(tmpVector.set(getVelocity().x, verticalVelocity));
                // Reset values
                gotoPosition = -1f;
                gotoSpeed = 0f;
                // Special case if we have the control taken
                if(controlTaken)
                {
                    takeControl(shuffleDistance);
                    // Set the y velocity
                    setVelocity(tmpVector.set(getVelocity().x, Configuration.PLAYER_VERTICAL_VELOCITY));
                }
            }
            else
                // Set the player's vertical velocity
                setVelocity(tmpVector.set(getVelocity().x, gotoSpeed));
        }
        // Process control taken
        else if(controlTaken)
        {
            // Get the player's Y velocity
            float yVelocity = shuffleDistance > 0f ? getVelocity().y : 0f;
            // Check if we need to rotate
            if((yVelocity > 0f && getPosition().y >= startInitialPosition + shuffleDistance) ||
                    (yVelocity < 0f && getPosition().y <= startInitialPosition - shuffleDistance))
            {
                // Fix the position
                setPosition(tmpVector.set(getPosition().x, yVelocity > 0f ? startInitialPosition + shuffleDistance :
                        startInitialPosition - shuffleDistance));

                // Invert velocity
                yVelocity = -yVelocity;
            }
            setVelocity(tmpVector.set(verticalVelocity, yVelocity));
        }
        else
            // Process input
            processInput();

        // Process gravity mode
        processGravityMode();
        // Always keep the player's velocity constant
        setVelocity(tmpVector.set(verticalVelocity, getVelocity().y));
        // Process the entity
        super.process();
        // Process the tail effect
        processTail();
    }

    private void processGravityMode()
    {
        // Check the mode
        if(GameLogic.getInstance().getGameMode() != GameMode.GAME_MODE_GRAVITY_SWITCH)
            return;

        // Set vertical velocity
        if(gravityDirectionUp)
            setVelocity(tmpVector.set(getVelocity().x, Configuration.PLAYER_GRAVITY_VERTICAL_VELOCITY));
        else
            setVelocity(tmpVector.set(getVelocity().x, -Configuration.PLAYER_GRAVITY_VERTICAL_VELOCITY));
    }

    private void processTail()
    {
        // Process tail effect
        if(points != null && tailState)
        {
            // Don't allow the counter to exceed the points limit
            if(lastPointIndex == TAIL_POINTS - 1 && !shiftPointsLocked &&
                    (!GameWorld.getInstance().isSlowMotion() ||
                            GameMath.getDistanceBetweenVectors(getPosition(), points[lastPointIndex - 1].getPosition()) > 1.4f))
            {
                // Refill the array so that the first point is overwritten
                for(int i = 0; i < lastPointIndex; i++)
                    // Update this point with its next
                    points[i].setPosition(points[i + 1].getPosition());
            }
            // Reset vertices index
            verticesIndex = 0;
            // Set the last point's position
            points[lastPointIndex].setPosition(getPosition());
            // Increase points count until we reach the limit
            if(lastPointIndex < TAIL_POINTS - 1)
            {
                lastPointIndex++;
                // Lock points shifting
                if(lastPointIndex == TAIL_POINTS - 1)
                    shiftPointsLocked = true;
            }
            else if(shiftPointsLocked)
                shiftPointsLocked = false;

            // Smooth the tail
            int outputSize = smoothTail(points, smoothedPoints, shiftPointsLocked ? lastPointIndex : lastPointIndex + 1);
            // Get the first point
            Point previousPoint = smoothedPoints[0];
            // Process the previous point
            previousPoint.process(0, outputSize, null);
            // Calculate the tail's skin starting position in the texture
            float tailSkinX = (float)(tailSkinId % SKINS_PER_LINE) * (float)TAIL_SKIN_SIZE / (float)(TAIL_SKIN_SIZE * SKINS_PER_LINE) + 0.01f;
            float tailSkinY = (float)(tailSkinId / SKINS_PER_LINE) * (float)TAIL_SKIN_SIZE / (float)(TAIL_SKIN_SIZE * SKINS_PER_LINE);
            float tailSkinW = (float)((tailSkinId + 1) % SKINS_PER_LINE) * (float)TAIL_SKIN_SIZE / (float)(TAIL_SKIN_SIZE * SKINS_PER_LINE) - 0.01f;
            float tailSkinH = (float)((tailSkinId / SKINS_PER_LINE) + 1) * (float)TAIL_SKIN_SIZE / (float)(TAIL_SKIN_SIZE * SKINS_PER_LINE);
            // Set the tail's color
            float packedColor = Color.toFloatBits(1f, 1f, 1f, TAIL_ALPHA);
            // Loop through the points
            for(int i = 1; i < outputSize; i++)
            {
                // Get the point
                Point point = smoothedPoints[i];
                // Process the current point
                point.process(i, outputSize, previousPoint.getPosition());

                // Lower left corner
                vertices[verticesIndex++] = previousPoint.getScreenLowerPosition().x;
                vertices[verticesIndex++] = previousPoint.getScreenLowerPosition().y;
                vertices[verticesIndex++] = packedColor;
                vertices[verticesIndex++] = tailSkinX;
                vertices[verticesIndex++] = tailSkinH;

                // Upper left corner
                vertices[verticesIndex++] = previousPoint.getScreenUpperPosition().x;
                vertices[verticesIndex++] = previousPoint.getScreenUpperPosition().y;
                vertices[verticesIndex++] = packedColor;
                vertices[verticesIndex++] = tailSkinX;
                vertices[verticesIndex++] = tailSkinY;

                // Upper right corner
                vertices[verticesIndex++] = point.getScreenUpperPosition().x;
                vertices[verticesIndex++] = point.getScreenUpperPosition().y;
                vertices[verticesIndex++] = packedColor;
                vertices[verticesIndex++] = tailSkinW;
                vertices[verticesIndex++] = tailSkinY;

                // Lower right corner
                vertices[verticesIndex++] = point.getScreenLowerPosition().x;
                vertices[verticesIndex++] = point.getScreenLowerPosition().y;
                vertices[verticesIndex++] = packedColor;
                vertices[verticesIndex++] = tailSkinW;
                vertices[verticesIndex++] = tailSkinH;
                // Save as the last point
                previousPoint = point;
            }
        }
    }

    private int smoothTail(Point[] input, Point[] output, int inputSize)
    {
        // Set the first point
        output[0].setPosition(input[0].getPosition());
        int index = 1;
        // Smooth points
        for(int i = 0; i < inputSize - 1; i++)
        {
            // Get positions
            Vector2 firstPosition = input[i].getPosition();
            Vector2 secondPosition = input[i + 1].getPosition();
            // Calculate the new points
            tmpVector.set(0.75f * firstPosition.x + 0.25f * secondPosition.x, 0.75f * firstPosition.y + 0.25f * secondPosition.y);
            output[index++].setPosition(tmpVector);
            tmpVector.set(0.25f * firstPosition.x + 0.75f * secondPosition.x, 0.25f * firstPosition.y + 0.75f * secondPosition.y);
            output[index++].setPosition(tmpVector);
        }
        // Set the last point
        if(inputSize > 1)
            output[index++].setPosition(input[inputSize - 1].getPosition());

        return index;
    }

    public void teleport(Vector2 targetPosition)
    {
        // Loop through the points
        for(int i = 1; i < lastPointIndex + 1; i++)
        {
            // Get the point's position
            Vector2 pointPosition = points[i].getPosition();
            // Get the relative point position
            tmpVector.set(getPosition()).sub(pointPosition);
            // Set the new position
            tmpVector.set(targetPosition.x - tmpVector.x, targetPosition.y - tmpVector.y);
            // Set the points position
            points[i].setPosition(tmpVector);
        }
        // Set the player's position
        setPosition(targetPosition);
    }

    @Override
    public void render()
    {
        // If we're dying then render the death effect
        if(dying)
            // Render the effect
            Core.getInstance().getGraphicsManager().drawParticleEffect(dyingEffect);
        else if(exploding)
            // Render the effect
            Core.getInstance().getGraphicsManager().drawParticleEffect(explosionEffect);
        else
        {
            // Render the tail
            renderTail();
            // Set the secondary color
            Core.getInstance().getGraphicsManager().setColor(secondaryColor);
            // Draw the default skin
            screenSize.sub(1f, 1f);
            screenPosition.add(0.5f, 0.5f);
            Core.getInstance().getGraphicsManager().drawTextureRegion(defaultSkin, screenPosition, screenSize, getRotationInDegrees());
            // Set the player's color
            Core.getInstance().getGraphicsManager().setColor(getColor());
            // Draw the skin
            screenSize.add(1f, 1f);
            screenPosition.sub(0.5f, 0.5f);
            Core.getInstance().getGraphicsManager().drawTextureRegion(skin, screenPosition, screenSize, getRotationInDegrees());
            // Restore the color
            Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        }
    }

    private void renderTail()
    {
        // Skip rendering if we don't have anything
        if(verticesIndex == 0 || lastPointIndex != TAIL_POINTS - 1 || !tailState)
            return;

        // Render the texture
        Core.getInstance().getGraphicsManager().drawTexture(coloredTailTexture, vertices, verticesIndex);
    }

    private void processInput()
    {
        // Skip if we have an unreported touch
        if(Core.getInstance().getInputHandler().isUnreportedTouch() || Core.getInstance().getScreensManager().getGameScreen().isPausePressed()
                || !(Core.getInstance().getScreensManager().getCurrentScreen() instanceof GameScreen))
            return;

        // Gravity mode
        if(GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_GRAVITY_SWITCH)
        {
            // Check press
            if((Core.getInstance().getInputHandler().isScreenTouched() || Gdx.input.isKeyPressed(Input.Keys.A)) && gravityCollision)
            {
                // Calculate the velocity
                float yVelocity = getPosition().y >= Configuration.TERRAIN_ROOF_POSITION / 2 ?
                        -Configuration.PLAYER_GRAVITY_VERTICAL_VELOCITY : Configuration.PLAYER_GRAVITY_VERTICAL_VELOCITY;

                // Set vertical velocity
                setVelocity(tmpVector.set(getVelocity().x, yVelocity));
                // Flip the gravity direction flag
                gravityDirectionUp = !gravityDirectionUp;
                // Set the gravity collision
                gravityCollision = false;
            }
        }
        else
        {
            if ((Core.getInstance().getInputHandler().isScreenTouched() || Gdx.input.isKeyPressed(Input.Keys.A)) && !screenTouched)
            {
                // Set vertical velocity
                setVelocity(tmpVector.set(getVelocity().x, Configuration.PLAYER_VERTICAL_VELOCITY));
                // Set the screen to be touched
                screenTouched = true;
            }
            else if (!(Core.getInstance().getInputHandler().isScreenTouched() || Gdx.input.isKeyPressed(Input.Keys.A)) && screenTouched)
            {
                // Set vertical velocity
                setVelocity(tmpVector.set(getVelocity().x, -Configuration.PLAYER_VERTICAL_VELOCITY));
                // Set the screen to be not touched
                screenTouched = false;
            }
        }
    }

    public boolean beginCollision(Body bodyA, Body bodyB, Fixture fixtureA, Fixture fixtureB, Vector2 point)
    {
        // Collision in main menu ? naaaah
        if(!(Core.getInstance().getScreensManager().getCurrentScreen() instanceof GameScreen))
            return false;

        // If the body belongs to the player
        if(getBody() == bodyA || getBody() == bodyB)
        {
            // Find the object's body
            Body body = bodyA;
            if(getBody() == bodyA)
                body = bodyB;

            // Find the object's fixture
            Fixture fixture = fixtureA;
            if(getBody().getFixtureList().contains(fixture, true))
                fixture = fixtureB;

            // Find if its an eatable object
            if(body.getUserData() != null && body.getUserData().equals("Eatable"))
                GameLogic.getInstance().eatObject(body);
            else if(fixture.getUserData() != null &&
                    (fixture.getUserData().equals("Ground") || fixture.getUserData().equals("Roof")))
            {
                // Are we in gravity switch mode ?
                if(GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_GRAVITY_SWITCH)
                {
                    // Set the gravity collision flag
                    gravityCollision = true;
                    // Reset the vertical velocity
                    setVelocity(tmpVector.set(getVelocity().x, 0f));
                }
                else
                {
                    // Kill us if its a lethal ground
                    if(GameLogic.getInstance().isLethalGround())
                        kill(point);
                    else
                    {
                        // Bounce away
                        if (fixture.getUserData().equals("Ground"))
                            flyTo(6f, Configuration.PLAYER_VERTICAL_VELOCITY);
                        else
                            flyTo(Configuration.TERRAIN_ROOF_POSITION - 6f, -Configuration.PLAYER_VERTICAL_VELOCITY);
                    }
                }
            }
            else if(body.getUserData() != null && body.getUserData().equals("Cube"))
                // Explode ourselves
                explode();
            else
                // Kill ourselves
                kill(point);

            return true;
        }
        return false;
    }

    public void kill(Vector2 point)
    {
        // Skip if we're already dying
        if(dying)
            return;

        // Set the dying effects color
        float[] colors = dyingEffect.getEmitters().first().getTint().getColors();
        colors[0] = getColor().r;
        colors[1] = getColor().g;
        colors[2] = getColor().b;
        // Start the dying effect
        dyingEffect.start();
        // Set the dying flag
        dying = true;
        // Reset velocity
        setVelocity(Core.getInstance().getGraphicsManager().EMPTY_VECTOR);
        // Save the death position
        Vector2 position = Camera.getInstance().worldToScreen(point);
        dyingEffect.setPosition(position.x, position.y);
        // Toggle off the cave mode
        // NOTE: game logic's cave mode needs to be toggled from here as it should not include the death animation
        GameLogic.getInstance().toggleCaveMode(false);
        // Perform the dying sound
        ((GameScreen)Core.getInstance().getScreensManager().getCurrentScreen()).dyingSound();
        // Disable lightening
        toggleLights(false);
        // Flush the screen
        Core.getInstance().getGraphicsManager().getScreenEffects().flush();
    }

    private void explode()
    {
        // Set the explosion effects color
        float[] colors = explosionEffect.getEmitters().first().getTint().getColors();
        colors[0] = getColor().r;
        colors[1] = getColor().g;
        colors[2] = getColor().b;
        // Start the explosion effect
        explosionEffect.start();
        // Set its position
        Vector2 position = Camera.getInstance().worldToScreen(getPosition());
        explosionEffect.setPosition(position.x, position.y);
        // Reset velocity
        setVelocity(Core.getInstance().getGraphicsManager().EMPTY_VECTOR);
        // Set the exploding flag
        exploding = true;
    }

    public void start()
    {
        // Set the started flag
        started = true;
        // Fall
        setVelocity(new Vector2(getVelocity().x, -Configuration.PLAYER_VERTICAL_VELOCITY));
    }

    public void backToFlying()
    {
        // Calculate the velocity
        float yVelocity = getPosition().x >= Configuration.TERRAIN_ROOF_POSITION / 2f ?
                -Configuration.PLAYER_VERTICAL_VELOCITY : Configuration.PLAYER_VERTICAL_VELOCITY;

        // Set the vertical velocity
        setVelocity(new Vector2(GameIntelligence.getInstance().getPlayerVelocity(), yVelocity));
        // Set to flying to middle
        flyingToMiddle = true;
    }

    public void setVerticalVelocity(float verticalVelocity) {
        this.verticalVelocity = verticalVelocity;
    }

    public void takeControl(float shuffleDistance)
    {
        // Set the control flag
        controlTaken = true;
        // Set the shuffle distance
        this.shuffleDistance = shuffleDistance;
        // Set the initial position
        startInitialPosition = getPosition().y;
    }

    public void restoreControl()
    {
        // Reset the control flag
        controlTaken = false;
        // Reset the shuffle distance
        this.shuffleDistance = 0f;
        // Reset the vertical velocity
        setVelocity(tmpVector.set(getVelocity().x, 0f));
    }

    public TextureRegion getSkin(int id)
    {
        // Get the texture's region
        tmpTextureRegion.setTexture(skinsTexture);
        tmpTextureRegion.setRegion((id % SKINS_PER_LINE) * SKIN_SIZE, (id / SKINS_PER_LINE) * SKIN_SIZE, SKIN_SIZE, SKIN_SIZE);
        return tmpTextureRegion;
    }

    public void setSkin(int id)
    {
        // Get the texture's region
        if(id < SKINS)
            skin.setRegion((id % SKINS_PER_LINE) * SKIN_SIZE, (id / SKINS_PER_LINE) * SKIN_SIZE, SKIN_SIZE, SKIN_SIZE);
    }

    public TextureRegion getTailSkin(int id)
    {
        // Get the texture's region
        tmpTextureRegion.setTexture(tailTexture);
        tmpTextureRegion.setRegion((id % SKINS_PER_LINE) * TAIL_SKIN_SIZE, (id / SKINS_PER_LINE) * TAIL_SKIN_SIZE, TAIL_SKIN_SIZE, TAIL_SKIN_SIZE);
        return tmpTextureRegion;
    }

    public void setTailSkin(int id)
    {
        if(id < TAIL_SKINS)
        {
            tailSkinId = id;
            // Update tail's color
            updateTailColor();
        }
    }

    public boolean isFlyingToMiddle()
    {
        return flyingToMiddle;
    }

    public boolean isDying() {
        return dying;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStartInitialPosition(float position) { startInitialPosition = position; }

    public void setSecondaryColor(Color secondaryColor)
    {
        this.secondaryColor.set(secondaryColor);
    }

    public void setTailColor(Color tailColor)
    {
        this.tailColor.set(tailColor);
        // Update the skin's color
        updateTailColor();
    }

    @Override
    public void setColor(Color color)
    {
        super.setColor(color);
        // Fix the alpha
        color.a = 1.0f;
    }

    public void setSecondaryTailColor(Color secondaryTailColor)
    {
        this.secondaryTailColor.set(secondaryTailColor);
        // Update the skin's color
        updateTailColor();
    }

    // Change the tail's skin black color to secondary and white to primary
    private void updateTailColor()
    {
        // Prepare the texture if its not
        if(!tailTexture.getTextureData().isPrepared())
            tailTexture.getTextureData().prepare();

        // Get the texture's pixmap
        Pixmap pixmap = tailTexture.getTextureData().consumePixmap();
        // Loop through the texture pixels
        int textureX = (tailSkinId % SKINS_PER_LINE) * TAIL_SKIN_SIZE;
        int textureY = (tailSkinId / SKINS_PER_LINE) * TAIL_SKIN_SIZE;
        for(int y = textureY; y < textureY + TAIL_SKIN_SIZE; y++)
        {
            for(int x = textureX; x < textureX + TAIL_SKIN_SIZE; x++)
            {
                // If the pixel is black
                if(pixmap.getPixel(x, y) == 255)
                    // Set the pixmap's color
                    pixmap.setColor(secondaryTailColor);
                else
                    pixmap.setColor(tailColor);

                // Change the pixel's color
                pixmap.fillRectangle(x, y, 1, 1);
            }
        }
        // Create the color tail texture
        coloredTailTexture = new Texture(pixmap);
    }

    public void setTailState(boolean tailState)
    {
        this.tailState = tailState;
        // Reset the tail points
        lastPointIndex = 0;
    }

    public void setRotationState(boolean rotationState)
    {
        this.rotationState = rotationState;
        // Reset rotation
        if(!rotationState)
            setRotationInDegrees(270f);
    }

    public void setLightState(boolean lightState)
    {
        // Toggle light
        toggleLights(lightState);
    }

    public void setLightAlpha(float a)
    {
        // Get the light's current color
        Color lightColor = getLightColor();
        // Set its alpha
        lightColor.a = a;
        // Set the new color
        setLightColor(lightColor);
    }

    public void flyTo(float positionY, float speed)
    {
        // Set the fly to values
        gotoPosition = positionY;
        gotoSpeed = speed;
    }

    public static Player getInstance()
    {
        return instance;
    }
}