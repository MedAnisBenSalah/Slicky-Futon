package com.ormisiclapps.slickyfuton.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;
import com.ormisiclapps.slickyfuton.game.nodes.save.SaveNode;

import java.io.*;
import java.security.MessageDigest;

/**
 * Created by Anis on 2/13/2017.
 */

public class StatsSaver
{
    private static final String SAVE_FILE = "SlickyFuton.oap";
    // Save file keys
    private static final String CHECKSUM_KEY = "Key";
    private static final String ENCRYPTED_SAVE_DATA_KEY = "Value";
    private static final String GAMES_PLAYED_KEY = "GamesPlayed";
    private static final String BEST_SCORE_KEY = "BestScore";
    private static final String LONGEST_DISTANCE_KEY = "LongestDistance";
    private static final String OBSTACLES_PASSED_KEY = "ObstaclesPassed";
    private static final String EATABLES_COLLECTED_KEY = "EatablesCollected";
    private static final String SOUND_STATE_KEY = "SoundState";
    private static final String MUSIC_STATE_KEY = "MusicState";
    private static final String USED_QUALITY_KEY = "UsedQuality";
    private static final String PRIMARY_COLOR_KEY = "PrimaryColor";
    private static final String SECONDARY_COLOR_KEY = "SecondaryColor";
    private static final String TAIL_COLOR_KEY = "TailColor";
    private static final String TAIL_SECONDARY_COLOR_KEY = "SecondaryTailColor";
    private static final String TEXTURE_ID_KEY = "TextureId";
    private static final String TAIL_TEXTURE_ID_KEY = "TailTextureId";
    private static final String LIGHT_COLOR_KEY = "LightColor";
    private static final String TAIL_STATE_KEY = "TailState";
    private static final String ROTATION_STATE_KEY = "RotationState";
    private static final String LIGHT_STATE_KEY = "LightState";
    private static final String LIGHT_DISTANCE_KEY = "LightDistance";
    private static final String COLOR_STATE_KEY = "ColorState";
    private static final String CHARACTER_SKIN_STATE_KEY = "CharacterSkinState";
    private static final String TAIL_SKIN_STATE_KEY = "TailSkinState";
    private static final String LEVEL_COMPLETED_KEY = "LevelCompleted";
    private static final String LEVEL_ATTEMPTS_KEY = "LevelAttempts";
    private static final String NEVER_RATE_KEY = "NeverRate";

    private Preferences preferences;
    private XmlWriter xmlWriter;
    private XmlReader xmlReader;
    private StringWriter stringWriter;

    public SaveNode savedData;

    public StatsSaver()
    {
        // Reset instances
        savedData = null;
        // Create XML writer and reader instances
        stringWriter = new StringWriter();
        xmlReader = new XmlReader();
        xmlWriter = new XmlWriter(stringWriter);
        // Get the preferences instance
        preferences = Gdx.app.getPreferences(SAVE_FILE);
    }

    public void load()
    {
        // Create the save node instance
        savedData = new SaveNode();
        // Check if we have a save file
        if(!preferences.contains(CHECKSUM_KEY))
            return;

        try
        {
            // Get the encrypted saved data
            String encryptedSaveData = preferences.getString(ENCRYPTED_SAVE_DATA_KEY);
            // Decrypt it
            byte[] decodedBytes = Base64Coder.decode(encryptedSaveData);
            String decodedSaveData = new String(decodedBytes, "US-ASCII");
            // Get the encrypted hash
            String encryptedHash = preferences.getString(CHECKSUM_KEY);
            // Decrypt it
            byte[] decodedHashBytes = Base64Coder.decode(encryptedHash);
            String decodedHash = new String(decodedHashBytes, "UTF-8");
            // Compare both keys
            if(!decodedHash.equals(generateMD5Hash(decodedSaveData)))
            {
                // Save a blank save (resetting data)
                save();
                return;
            }
            // Parse the XML data
            XmlReader.Element element = xmlReader.parse(decodedSaveData);
            // Load each and every key
            if(element.getChildByName(GAMES_PLAYED_KEY) != null)
                savedData.gamesPlayed = Long.parseLong(element.get(GAMES_PLAYED_KEY));

            if(element.getChildByName(BEST_SCORE_KEY) != null)
                savedData.bestScore = Long.parseLong(element.get(BEST_SCORE_KEY));

            if(element.getChildByName(LONGEST_DISTANCE_KEY) != null)
                savedData.longestDistance = Double.parseDouble(element.get(LONGEST_DISTANCE_KEY));

            if(element.getChildByName(OBSTACLES_PASSED_KEY) != null)
                savedData.obstaclesPassed = Long.parseLong(element.get(OBSTACLES_PASSED_KEY));

            if(element.getChildByName(EATABLES_COLLECTED_KEY) != null)
                savedData.eatablesCollected = Long.parseLong(element.get(EATABLES_COLLECTED_KEY));

            if(element.getChildByName(SOUND_STATE_KEY) != null)
                savedData.soundState = element.getBoolean(SOUND_STATE_KEY);

            if(element.getChildByName(MUSIC_STATE_KEY) != null)
                savedData.musicState = element.getBoolean(MUSIC_STATE_KEY);

            if(element.getChildByName(USED_QUALITY_KEY) != null)
                savedData.usedQuality = element.getInt(USED_QUALITY_KEY);

            if(element.getChildByName(PRIMARY_COLOR_KEY) != null)
            {
                XmlReader.Element colorElement = element.getChildByName(PRIMARY_COLOR_KEY);
                savedData.primaryColor.set(colorElement.getFloatAttribute("r"), colorElement.getFloatAttribute("g"),
                        colorElement.getFloatAttribute("b"), 1f);
            }

            if(element.getChildByName(SECONDARY_COLOR_KEY) != null)
            {
                XmlReader.Element colorElement = element.getChildByName(SECONDARY_COLOR_KEY);
                savedData.secondaryColor.set(colorElement.getFloatAttribute("r"), colorElement.getFloatAttribute("g"),
                        colorElement.getFloatAttribute("b"), 1f);
            }

            if(element.getChildByName(TAIL_COLOR_KEY) != null)
            {
                XmlReader.Element colorElement = element.getChildByName(TAIL_COLOR_KEY);
                savedData.tailColor.set(colorElement.getFloatAttribute("r"), colorElement.getFloatAttribute("g"),
                        colorElement.getFloatAttribute("b"), 1f);
            }

            if(element.getChildByName(TAIL_SECONDARY_COLOR_KEY) != null)
            {
                XmlReader.Element colorElement = element.getChildByName(TAIL_SECONDARY_COLOR_KEY);
                savedData.secondaryTailColor.set(colorElement.getFloatAttribute("r"), colorElement.getFloatAttribute("g"),
                        colorElement.getFloatAttribute("b"), 1f);
            }

            if(element.getChildByName(TEXTURE_ID_KEY) != null)
                savedData.textureId = element.getInt(TEXTURE_ID_KEY);

            if(element.getChildByName(TAIL_TEXTURE_ID_KEY) != null)
                savedData.tailTextureId = element.getInt(TAIL_TEXTURE_ID_KEY);

            if(element.getChildByName(LIGHT_COLOR_KEY) != null)
            {
                XmlReader.Element colorElement = element.getChildByName(LIGHT_COLOR_KEY);
                savedData.lightColor.set(colorElement.getFloatAttribute("r"), colorElement.getFloatAttribute("g"),
                        colorElement.getFloatAttribute("b"), colorElement.getFloatAttribute("a"));
            }

            if(element.getChildByName(TAIL_STATE_KEY) != null)
                savedData.tailState = element.getBoolean(TAIL_STATE_KEY);

            if(element.getChildByName(ROTATION_STATE_KEY) != null)
                savedData.rotationState = element.getBoolean(ROTATION_STATE_KEY);

            if(element.getChildByName(LIGHT_STATE_KEY) != null)
                savedData.lightState = element.getBoolean(LIGHT_STATE_KEY);

            if(element.getChildByName(LIGHT_DISTANCE_KEY) != null)
                savedData.lightDistance = element.getFloat(LIGHT_DISTANCE_KEY);

            if(element.getChildByName(COLOR_STATE_KEY) != null)
            {
                // Get the element
                XmlReader.Element stateElement = element.getChildByName(COLOR_STATE_KEY);
                // Loop through the elements
                for(int i = 0; i < stateElement.getInt("count"); i++)
                    savedData.colorsState[i] = stateElement.getBoolean("element" + i);
            }

            if(element.getChildByName(CHARACTER_SKIN_STATE_KEY) != null)
            {
                // Get the element
                XmlReader.Element stateElement = element.getChildByName(CHARACTER_SKIN_STATE_KEY);
                // Loop through the elements
                for(int i = 0; i < stateElement.getInt("count"); i++)
                    savedData.characterSkinsState[i] = stateElement.getBoolean("element" + i);
            }

            if(element.getChildByName(TAIL_SKIN_STATE_KEY) != null)
            {
                // Get the element
                XmlReader.Element stateElement = element.getChildByName(TAIL_SKIN_STATE_KEY);
                // Loop through the elements
                for(int i = 0; i < stateElement.getInt("count"); i++)
                    savedData.tailSkinsState[i] = stateElement.getBoolean("element" + i);
            }

            if(element.getChildByName(LEVEL_COMPLETED_KEY) != null)
            {
                // Get the element
                XmlReader.Element levelCompletedElement = element.getChildByName(LEVEL_COMPLETED_KEY);
                // Loop through the elements
                for(int i = 0; i < levelCompletedElement.getInt("count"); i++)
                    savedData.levelCompleted[i] = levelCompletedElement.getBoolean("element" + i);
            }

            if(element.getChildByName(LEVEL_ATTEMPTS_KEY) != null)
            {
                // Get the element
                XmlReader.Element levelAttemptsElement = element.getChildByName(LEVEL_ATTEMPTS_KEY);
                // Loop through the elements
                for(int i = 0; i < levelAttemptsElement.getInt("count"); i++)
                    savedData.levelAttempts[i] = levelAttemptsElement.getInt("element" + i);
            }

            if(element.getChildByName(NEVER_RATE_KEY) != null)
                savedData.neverRate = element.getBoolean(NEVER_RATE_KEY);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void save()
    {
        try
        {
            // Create an xml version of the save data
            xmlWriter.element("Data")
                    .element(GAMES_PLAYED_KEY, ""+savedData.gamesPlayed)
                    .element(BEST_SCORE_KEY, ""+savedData.bestScore)
                    .element(LONGEST_DISTANCE_KEY, ""+savedData.longestDistance)
                    .element(OBSTACLES_PASSED_KEY, ""+savedData.obstaclesPassed)
                    .element(EATABLES_COLLECTED_KEY, ""+savedData.eatablesCollected)
                    .element(SOUND_STATE_KEY, ""+savedData.soundState)
                    .element(MUSIC_STATE_KEY, ""+savedData.musicState)
                    .element(USED_QUALITY_KEY, ""+savedData.usedQuality)
                    .element(PRIMARY_COLOR_KEY)
                        .attribute("r", savedData.primaryColor.r)
                        .attribute("g", savedData.primaryColor.g)
                        .attribute("b", savedData.primaryColor.b)
                        .pop()
                    .element(SECONDARY_COLOR_KEY)
                        .attribute("r", savedData.secondaryColor.r)
                        .attribute("g", savedData.secondaryColor.g)
                        .attribute("b", savedData.secondaryColor.b)
                        .pop()
                    .element(TAIL_COLOR_KEY)
                        .attribute("r", savedData.tailColor.r)
                        .attribute("g", savedData.tailColor.g)
                        .attribute("b", savedData.tailColor.b)
                        .pop()
                    .element(TAIL_SECONDARY_COLOR_KEY)
                        .attribute("r", savedData.secondaryTailColor.r)
                        .attribute("g", savedData.secondaryTailColor.g)
                        .attribute("b", savedData.secondaryTailColor.b)
                        .pop()
                    .element(TEXTURE_ID_KEY, ""+savedData.textureId)
                    .element(TAIL_TEXTURE_ID_KEY, ""+savedData.tailTextureId)
                    .element(LIGHT_COLOR_KEY)
                        .attribute("r", savedData.lightColor.r)
                        .attribute("g", savedData.lightColor.g)
                        .attribute("b", savedData.lightColor.b)
                        .attribute("a", savedData.lightColor.a)
                        .pop()
                    .element(TAIL_STATE_KEY, ""+savedData.tailState)
                    .element(ROTATION_STATE_KEY, ""+savedData.rotationState)
                    .element(LIGHT_STATE_KEY, ""+savedData.lightState)
                    .element(LIGHT_DISTANCE_KEY, ""+savedData.lightDistance);

            // Create the color state element
            xmlWriter.element(COLOR_STATE_KEY).attribute("count", savedData.colorsState.length);
            // Loop through the color states
            for(int i = 0; i < savedData.colorsState.length; i++)
                xmlWriter.attribute("element" + i, savedData.colorsState[i]);

            // Leave the element
            xmlWriter.pop();

            // Create the character skin state element
            xmlWriter.element(CHARACTER_SKIN_STATE_KEY).attribute("count", savedData.characterSkinsState.length);
            // Loop through the color states
            for(int i = 0; i < savedData.characterSkinsState.length; i++)
                xmlWriter.attribute("element" + i, savedData.characterSkinsState[i]);

            // Leave the element
            xmlWriter.pop();

            // Create the color state element
            xmlWriter.element(TAIL_SKIN_STATE_KEY).attribute("count", savedData.tailSkinsState.length);
            // Loop through the color states
            for(int i = 0; i < savedData.tailSkinsState.length; i++)
                xmlWriter.attribute("element" + i, savedData.tailSkinsState[i]);

            // Leave the element
            xmlWriter.pop();

            // Create the color state element
            xmlWriter.element(LEVEL_COMPLETED_KEY).attribute("count", savedData.levelCompleted.length);
            // Loop through the level completed
            for(int i = 0; i < savedData.levelCompleted.length; i++)
                xmlWriter.attribute("element" + i, savedData.levelCompleted[i]);

            // Leave the element
            xmlWriter.pop();

            // Create the color state element
            xmlWriter.element(LEVEL_ATTEMPTS_KEY).attribute("count", savedData.levelAttempts.length);
            // Loop through the level attempts
            for(int i = 0; i < savedData.levelAttempts.length; i++)
                xmlWriter.attribute("element" + i, savedData.levelAttempts[i]);

            // Leave the element
            xmlWriter.pop()

            // Create the never rate element
            .element(NEVER_RATE_KEY, ""+savedData.neverRate);
            // Close the XML writer
            xmlWriter.close();
            // Generate an MD5 hash for the xml
            String hash = generateMD5Hash(stringWriter.toString());
            // Encode the XML data
            String encodedSaveData = String.valueOf(Base64Coder.encode(stringWriter.toString().getBytes()));
            // Write it to the preferences file
            preferences.putString(CHECKSUM_KEY, String.valueOf(Base64Coder.encode(hash.getBytes())));
            preferences.putString(ENCRYPTED_SAVE_DATA_KEY, encodedSaveData);
            // Flush the preferences
            preferences.flush();
        }
        catch(IOException exception)
        {
            // Save file failed somehow
            exception.printStackTrace();
        }
    }

    private String generateMD5Hash(String data)
    {
        try
        {
            // Get message digest instance
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // Hash the string
            byte[] hashedBytes = messageDigest.digest(data.getBytes("UTF-8"));
            // Get the result as a string
            return new String(hashedBytes);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //Gdx.app.exit();
        }
        return "";
    }
}
