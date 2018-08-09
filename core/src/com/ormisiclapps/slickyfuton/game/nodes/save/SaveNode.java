package com.ormisiclapps.slickyfuton.game.nodes.save;

import com.badlogic.gdx.graphics.Color;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.screens.ShopScreen;

import java.util.Arrays;

/**
 * Created by Anis on 2/13/2017.
 */

public class SaveNode
{
    public long bestScore;
    public long gamesPlayed;
    public double longestDistance;
    public long obstaclesPassed;
    public long eatablesCollected;
    public boolean soundState;
    public boolean musicState;
    public int usedQuality;
    public Color primaryColor, secondaryColor, tailColor, secondaryTailColor, lightColor;
    public int textureId, tailTextureId;
    public boolean tailState;
    public boolean rotationState;
    public boolean lightState;
    public float lightDistance;
    public boolean[] colorsState, characterSkinsState, tailSkinsState;
    public boolean[] levelCompleted;
    public int[] levelAttempts;
    public boolean neverRate;

    private static final int LEVELS_COUNT = 1;

    public SaveNode()
    {
        bestScore = 0;
        gamesPlayed = 0;
        longestDistance = 0f;
        obstaclesPassed = 0;
        eatablesCollected = 0;
        soundState = true;
        musicState = true;
        usedQuality = -1;
        primaryColor = new Color(Player.DEFAULT_COLOR);
        secondaryColor = new Color(1f, 1f, 1f, 1f);
        tailColor = new Color(Player.DEFAULT_COLOR);
        secondaryTailColor = new Color(1f, 1f, 1f, 1f);
        lightColor = new Color(Player.DEFAULT_COLOR);
        textureId = 0;
        tailTextureId = 0;
        tailState = true;
        rotationState = true;
        lightState = true;
        lightDistance = 1.7f;
        neverRate = false;
        // Create the colors state array
        colorsState = new boolean[GameIntelligence.shopColors.length];
        colorsState[0] = true;
        colorsState[GameIntelligence.shopColors.length - 1] = true;
        colorsState[GameIntelligence.shopColors.length - 2] = true;
        for(int i = 1; i < GameIntelligence.shopColors.length - 2; i++)
            colorsState[i] = false;

        // Create the character skins array
        characterSkinsState = new boolean[Player.SKINS];
        characterSkinsState[0] = true;
        for(int i = 1; i < Player.SKINS; i++)
            characterSkinsState[i] = false;

        // Create the tail skins array
        tailSkinsState = new boolean[Player.TAIL_SKINS];
        tailSkinsState[0] = true;
        for(int i = 1; i < Player.TAIL_SKINS; i++)
            tailSkinsState[i] = false;

        // Create the level arrays
        levelCompleted = new boolean[LEVELS_COUNT];
        levelAttempts = new int[LEVELS_COUNT];
        for(int i = 0; i < LEVELS_COUNT; i++)
        {
            levelCompleted[i] = false;
            levelAttempts[i] = 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SaveNode)) return false;

        SaveNode saveNode = (SaveNode) o;

        if (bestScore != saveNode.bestScore) return false;
        if (gamesPlayed != saveNode.gamesPlayed) return false;
        if (Double.compare(saveNode.longestDistance, longestDistance) != 0) return false;
        if (obstaclesPassed != saveNode.obstaclesPassed) return false;
        if (eatablesCollected != saveNode.eatablesCollected) return false;
        if (soundState != saveNode.soundState) return false;
        if (musicState != saveNode.musicState) return false;
        if (usedQuality != saveNode.usedQuality) return false;
        if (textureId != saveNode.textureId) return false;
        if (tailTextureId != saveNode.tailTextureId) return false;
        if (tailState != saveNode.tailState) return false;
        if (rotationState != saveNode.rotationState) return false;
        if (lightState != saveNode.lightState) return false;
        if (Float.compare(saveNode.lightDistance, lightDistance) != 0) return false;
        if (neverRate != saveNode.neverRate) return false;
        if (!primaryColor.equals(saveNode.primaryColor)) return false;
        if (!secondaryColor.equals(saveNode.secondaryColor)) return false;
        if (!tailColor.equals(saveNode.tailColor)) return false;
        if (!secondaryTailColor.equals(saveNode.secondaryTailColor)) return false;
        if (!lightColor.equals(saveNode.lightColor)) return false;
        if (!Arrays.equals(colorsState, saveNode.colorsState)) return false;
        if (!Arrays.equals(characterSkinsState, saveNode.characterSkinsState)) return false;
        if (!Arrays.equals(tailSkinsState, saveNode.tailSkinsState)) return false;
        if (!Arrays.equals(levelCompleted, saveNode.levelCompleted)) return false;
        return Arrays.equals(levelAttempts, saveNode.levelAttempts);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (bestScore ^ (bestScore >>> 32));
        result = 31 * result + (int) (gamesPlayed ^ (gamesPlayed >>> 32));
        temp = Double.doubleToLongBits(longestDistance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (obstaclesPassed ^ (obstaclesPassed >>> 32));
        result = 31 * result + (int) (eatablesCollected ^ (eatablesCollected >>> 32));
        result = 31 * result + (soundState ? 1 : 0);
        result = 31 * result + (musicState ? 1 : 0);
        result = 31 * result + usedQuality;
        result = 31 * result + primaryColor.hashCode();
        result = 31 * result + secondaryColor.hashCode();
        result = 31 * result + tailColor.hashCode();
        result = 31 * result + secondaryTailColor.hashCode();
        result = 31 * result + lightColor.hashCode();
        result = 31 * result + textureId;
        result = 31 * result + tailTextureId;
        result = 31 * result + (tailState ? 1 : 0);
        result = 31 * result + (rotationState ? 1 : 0);
        result = 31 * result + (lightState ? 1 : 0);
        result = 31 * result + (lightDistance != +0.0f ? Float.floatToIntBits(lightDistance) : 0);
        result = 31 * result + Arrays.hashCode(colorsState);
        result = 31 * result + Arrays.hashCode(characterSkinsState);
        result = 31 * result + Arrays.hashCode(tailSkinsState);
        result = 31 * result + Arrays.hashCode(levelCompleted);
        result = 31 * result + Arrays.hashCode(levelAttempts);
        result = 31 * result + (neverRate ? 1 : 0);
        return result;
    }
}
