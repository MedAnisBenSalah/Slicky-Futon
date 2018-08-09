package com.ormisiclapps.slickyfuton.utility;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by OrMisicL on 5/29/2016.
 */
public class Configuration
{
        //**** General configuration ****///
    public static final int DEFAULT_TRANSITION_DURATION = 900;
    // Game intelligence properties
    public static final int EXCLUSIVE_STATE_MINIMUM_SCORE = 40;
    //public static final int CAVE_MODE_MINIMUM_SCORE = 50;

    public static final int MAXIMUM_VELOCITY_SCORE = 60;
    public static final int MINIMUM_STATIC_OBJECTS_COUNT = 10;
    public static final float START_POSITION = 110f;

    public static final int LOWEST_STATIC_COMBINATION_SCORE = 10;
    public static final int LOWEST_MOVABLE_SCORE = 25;
    public static final int LOWEST_COMPOSED_SCORE = 30;
    public static final int LOWEST_PARTED_SCORE = 40;
    public static final int LOWEST_CHAIN_SCORE = 45;
    public static final int LETHAL_GROUND_SCORE = 35;

        //**** Graphics configuration ****//
    public static final float BACKGROUND_COLOR = 47f / 255f;
    public static final Color PROGRESSBAR_CONTAINER_COLOR = new Color(1f, 1f, 1f, 1f);
    public static final Color PROGRESSBAR_BACKGROUND_COLOR = new Color(0f, 0f, 0f, 1f);
    public static final Color PROGRESSBAR_COLOR = new Color(0.2f, 0.4f, 0.6f, 1f);

        //**** Physics configuration ****//
    // Player properties
    public static final float PLAYER_VELOCITY = 55f;
    public static final float PLAYER_VERTICAL_VELOCITY = 35f;
    public static final float PLAYER_GRAVITY_VERTICAL_VELOCITY = 100f;
    /*public static final float PLAYER_UPPER_ROTATION = 315f;
    public static final float PLAYER_LOWER_ROTATION = 45f;*/
    // Terrain properties
    public static final float TERRAIN_ROOF_POSITION = 30.5f;
    public static final float TERRAIN_GRAVITY_SWITCH_ROOF_POSITION = TERRAIN_ROOF_POSITION * 0.75f;
    public static final float TERRAIN_STATIC_OBJECTS_SPACE = 17.5f;
    public static final float TERRAIN_GRAVITY_MODE_STATIC_OBJECTS_SPACE = TERRAIN_STATIC_OBJECTS_SPACE * 0.7f;
    public static final float TERRAIN_OBJECTS_SPACE = 10f;
    public static final float OBJECTS_ENTRY_SPEED = 17.5f;
    // Object properties
    public static final float MOVABLE_OBJECT_PATH_LENGTH = TERRAIN_ROOF_POSITION * 0.75f;
    public static final float CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH = MOVABLE_OBJECT_PATH_LENGTH / 2f;
    public static final float GRAVITY_SWITCH_MOVABLE_OBJECT_PATH_LENGTH = TERRAIN_GRAVITY_SWITCH_ROOF_POSITION * 0.75f;
    public static final float GRAVITY_SWITCH_CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH = GRAVITY_SWITCH_MOVABLE_OBJECT_PATH_LENGTH / 2f;

    public static final short PLAYER_CATEGORY_BITS = 0x0001;
    public static final short OBJECTS_CATEGORY_BITS = 0x0002;
    public static final short WALLS_CATEGORY_BITS = 0x0004;
    // Lightening properties
   /* public static final Color ENVIRONMENT_LIGHT_COLOR = new Color(1f, 1f, 1f, 0.25f);
    public static final int ENVIRONMENT_LIGHT_RAYS_NUMBER = 10;
    public static final float ENVIRONMENT_LIGHT_ANGLE = -90f;

    public static final float CAVE_LIGHT_COLOR_ALPHA = 0.6f;
    public static final Color CAVE_LIGHT_COLOR = new Color(1f, 1f, 1f, 0.6f);
    public static final int CAVE_LIGHT_RAYS_NUMBER = 100;
    public static final float CAVE_LIGHT_DISTANCE = 350f;
    public static final float CAVE_LIGHT_FIELD_ROTATION = 30f;*/

    public static final int HIGH_QUALITY_ENTITY_RAYS_NUMBER = 25;
    public static final int MEDIUM_QUALITY_ENTITY_RAYS_NUMBER = 12;
    public static final int LOW_QUALITY_ENTITY_RAYS_NUMBER = 5;
    public static int ENTITY_RAYS_NUMBER = HIGH_QUALITY_ENTITY_RAYS_NUMBER;
    // Point properties
    public static final float POINT_SIZE = 0.65f;
    // Reward properties
    public static final int REWARD_AMOUNT = 15;
        //**** Loading screen configuration ****//
    //public static final int LOGO_SCREEN_DURATION = 2000;
}
