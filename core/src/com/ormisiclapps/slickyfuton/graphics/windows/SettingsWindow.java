package com.ormisiclapps.slickyfuton.graphics.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UIDropList;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIWindow;

/**
 * Created by OrMisicL on 7/18/2017.
 */

public class SettingsWindow extends UIWindow
{
    private UIButton soundButton, musicButton;
    private UIDropList qualityDropList;
    private TextureRegion qualityIconTexture;
    private Vector2 qualityIconPosition;

    public SettingsWindow()
    {
        // Create the window
        super("Settings", new Vector2(Core.getInstance().getGraphicsManager().WIDTH * 0.25f,
                        Core.getInstance().getGraphicsManager().HEIGHT * 0.125f),
                        new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2f,
                                Core.getInstance().getGraphicsManager().HEIGHT * 0.75f), new Color(0f, 0.2901f, 0.4901f, 0.99f));

        // Get the states flags
        boolean sound = Core.getInstance().getStatsSaver().savedData.soundState;
        // Create sound button
        soundButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource(sound ? "UI/SoundOnButton" : "UI/SoundOffButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        boolean music = Core.getInstance().getStatsSaver().savedData.musicState;
        float size = Core.getInstance().getGraphicsManager().HEIGHT / 8f;
        soundButton.setSize(new Vector2(size, size));
        soundButton.setPosition(new Vector2(getSize().x / 6f - size / 2f, getSize().y / 6f * 4f - size / 2f));
        musicButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource(music ? "UI/MusicOnButton" : "UI/MusicOffButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        musicButton.setSize(new Vector2(size, size));
        musicButton.setPosition(new Vector2(getSize().x / 6f - size / 2f, getSize().y / 6f * 2.9f - size / 2f));
        // Calculate the quality size
        Vector2 textSize = getText().getSize("Quality: ");
        // Create the quality drop box
        qualityDropList = new UIDropList(new Vector2(getSize().x / 6f + size * 0.75f + textSize.x, getSize().y / 6f * 1.8f + size / 4f),
                textSize.x * 1.5f);

        // Add buttons
        addWidget(soundButton);
        addWidget(musicButton);
        // Add texts
        addText("Sound: " + (sound ? "ON" : "OFF"), new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 4f + size / 4f));
        addSmallText("Turn sound ON/OFF.", new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 4f - size / 8f));
        addText("Music: "+ (music ? "ON" : "OFF"), new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 2.9f + size / 4f));
        addSmallText("Turn music ON/OFF.", new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 2.9f - size / 8f));
        addText("Quality:", new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 1.8f + size / 4f));
        addSmallText("Change texture's quality.", new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 1.8f - size / 8f));
        addSmallText("v" + Core.getInstance().VERSION, new Vector2(getSize().x - getSize().x / 6f, getSize().y / 18f));
        // Add the drop box
        addWidget(qualityDropList);
        // Add drop list options
        qualityDropList.addOption("High");
        qualityDropList.addOption("Medium");
        qualityDropList.addOption("Low");
        // Set the selected option
        qualityDropList.setSelectedOption(GameIntelligence.getDisplayType());
        // Disable the window
        toggle(false);
        // Get the quality icon texture
        qualityIconTexture = Core.getInstance().getResourcesManager().getResource("UI/QualityIcon", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Set the quality icon position
        qualityIconPosition = new Vector2(getPosition().x + getSize().x / 6f - size / 2f, getPosition().y + getSize().y / 6f * 1.8f - size / 2f);
        // Set to be not effected with fading
        setFadeEffected(false);
    }

    @Override
    public void process()
    {
        super.process();
        // Set the quality icon position
        float buttonSize = Core.getInstance().getGraphicsManager().HEIGHT / 8f;
        qualityIconPosition = new Vector2(getPosition().x + getSize().x / 6f - buttonSize / 2f,
                getPosition().y + getSize().y / 6f * 1.8f - buttonSize / 2f);

        // Skip button's input if the alert is visible
        if(UIMain.getInstance().isAlertWindowVisible())
            return;

        // Check button press
        if(soundButton.isClicked())
        {
            // Toggle sound state
            Core.getInstance().getStatsSaver().savedData.soundState = !Core.getInstance().getStatsSaver().savedData.soundState;
            // Update texture
            TextureRegion texture = Core.getInstance().getResourcesManager().getResource(Core.getInstance().getStatsSaver().savedData.soundState
                    ? "UI/SoundOnButton" : "UI/SoundOffButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);

            soundButton.setTexture(texture);
            // Update text
            float size = Core.getInstance().getGraphicsManager().HEIGHT / 8f;
            addText("Sound: " + (Core.getInstance().getStatsSaver().savedData.soundState ? "ON" : "OFF"),
                    new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 4f + size / 4f));

            // Save
            Core.getInstance().getStatsSaver().save();
        }

        if(musicButton.isClicked())
        {
            // Toggle music state
            Core.getInstance().getStatsSaver().savedData.musicState = !Core.getInstance().getStatsSaver().savedData.musicState;
            // Toggle music
            Core.getInstance().getCurrentMusic().setVolume(Core.getInstance().getStatsSaver().savedData.musicState ? 1.0f: 0.0f);
            // Update texture
            TextureRegion texture = Core.getInstance().getResourcesManager().getResource(Core.getInstance().getStatsSaver().savedData.musicState
                            ? "UI/MusicOnButton" : "UI/MusicOffButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);

            musicButton.setTexture(texture);
            // Update text
            float size = Core.getInstance().getGraphicsManager().HEIGHT / 8f;
            addText("Music: " + (Core.getInstance().getStatsSaver().savedData.musicState ? "ON" : "OFF"),
                    new Vector2(getSize().x / 6f + size * 0.75f, getSize().y / 6f * 2.9f + size / 4f));

            // Save
            Core.getInstance().getStatsSaver().save();
        }

        if(qualityDropList.isSelectChanged())
        {
            // Get the selected option
            String selectedOption = qualityDropList.getSelectedOption();
            // Find what quality we're using
            int quality = 0;
            if(selectedOption.equals("Medium"))
                quality = 1;
            else if(selectedOption.equals("Low"))
                quality = 2;

            // Set the save selected quality
            Core.getInstance().getStatsSaver().savedData.usedQuality = quality;
            // Save
            Core.getInstance().getStatsSaver().save();
            // Display alert
            UIMain.getInstance().alert("Quality changed", "You need to restart in order for the changes to take effect.");
        }
    }

    @Override
    public void render()
    {
        super.render();
        // Draw the quality icon
        Core.getInstance().getGraphicsManager().drawTextureRegion(qualityIconTexture, qualityIconPosition, soundButton.getSize(), 270f);
    }
}
