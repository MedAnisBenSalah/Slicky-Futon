package com.ormisiclapps.slickyfuton.graphics.others;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.nodes.level.LevelNode;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIText;

/**
 * Created by OrMisicL on 9/7/2017.
 */

public class LevelContainer
{
    private Vector2 position;
    private LevelNode node;
    private boolean endlessMode;
    private float currentScreenPosition;
    private TextureRegion containerTexture, coinTexture;
    private Vector2 coinPosition, coinSize;
    private UIButton playButton;

    private static Vector2 size = null;
    private static UIText levelNameText = null;
    private static UIText rewardText = null;
    private static UIText infoTitleText = null;
    private static UIText infoText = null;
    private static UIText completedText = null;
    private static UIText attemptsText = null;

    private static final Color COLOR = new Color(0f, 0f, 0f, 0.6f);

    public LevelContainer(LevelNode node, int position)
    {
        // Create the size vector
        if(size == null)
            size = new Vector2(Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR);

        // Create texts if necessary
        if(levelNameText == null)
        {
            // Create texts
            levelNameText = new UIText((int)size.x / 14, Color.WHITE, 0f, Color.WHITE);
            rewardText = new UIText((int)size.x / 24, new Color(1f, 0.9372f, 0f, 1f), 0f, Color.WHITE);
            infoTitleText = new UIText((int)size.x / 18, new Color(0.2f, 0.4f, 0.6f, 1f), 0f, Color.WHITE);
            infoText = new UIText((int)size.x / 20, Color.WHITE, 0f, Color.WHITE);
            completedText = new UIText((int)size.x / 24, new Color(0f, 0.8f, 0f, 1f), 0f, Color.WHITE);
            attemptsText = new UIText((int)size.x / 24, Color.WHITE, 0f, Color.WHITE);
            // Setup texts
            levelNameText.setFadeEffected(false);
            rewardText.setFadeEffected(false);
            infoTitleText.setFadeEffected(false);
            infoText.setFadeEffected(false);
            completedText.setFadeEffected(false);
            attemptsText.setFadeEffected(false);
            // Add them to the main UI
            UIMain.getInstance().addWidget(levelNameText);
            UIMain.getInstance().addWidget(rewardText);
            UIMain.getInstance().addWidget(infoTitleText);
            UIMain.getInstance().addWidget(infoText);
            UIMain.getInstance().addWidget(completedText);
            UIMain.getInstance().addWidget(attemptsText);
        }
        // Set parameters
        this.position = new Vector2(Core.getInstance().getGraphicsManager().WIDTH * position - Core.getInstance().getGraphicsManager().WIDTH / 2f - size.x / 2f,
                Core.getInstance().getGraphicsManager().HEIGHT / 2f - size.y / 2f);

        this.node = node;
        // Create the play button
        playButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource
                ("UI/LevelPlayButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Setup the button
        Vector2 buttonSize = new Vector2(size.y / 6f, size.y / 6f);
        playButton.setPosition(new Vector2(this.position.x + size.x / 2f - buttonSize.x / 2f, this.position.y + size.y / 2f - buttonSize.y / 2f));
        playButton.setSize(buttonSize);
        // Set the endless flag
        endlessMode = node == null;
        // Get the level container texture
        containerTexture = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Get the coin texture
        coinTexture = Core.getInstance().getResourcesManager().getResource("UI/EatableIcon", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Reset values
        currentScreenPosition = 0f;
        // Create vectors
        coinPosition = new Vector2();
        coinSize = new Vector2(size.x / 24f, size.x / 24f);
    }

    public void activate()
    {
        // Turn on texts
        if(!levelNameText.isVisible())
        {
            levelNameText.toggle(true);
            rewardText.toggle(true);
            infoTitleText.toggle(true);
            infoText.toggle(true);
            completedText.toggle(true);
            attemptsText.toggle(true);
        }
    }

    public void deactivate()
    {
        // Turn off texts
        if(levelNameText.isVisible())
        {
            levelNameText.toggle(false);
            rewardText.toggle(false);
            infoTitleText.toggle(false);
            infoText.toggle(false);
            completedText.toggle(false);
            attemptsText.toggle(false);
        }
    }

    public boolean update(float currentScreenPosition)
    {
        // Update the container's position if necessary
        if(this.currentScreenPosition != currentScreenPosition)
        {
            // Calculate the new position factor
            float newPositionFactor = this.currentScreenPosition - currentScreenPosition;
            // Update our position
            position.add(newPositionFactor, 0f);
            // Update widgets
            playButton.setPosition(playButton.getPosition().add(newPositionFactor, 0f));
            // Update the current screen position
            this.currentScreenPosition = currentScreenPosition;
        }
        // If it's not on screen then don't update
        if(!isOnScreen())
            return false;

        // Draw level name
        if(endlessMode)
        {
            Vector2 textSize = levelNameText.getSize("Endless");
            levelNameText.drawText("Endless", position.x + size.x / 2f - textSize.x / 2f, position.y + size.y * 0.75f + textSize.y / 2f);
            // Draw best score text
            textSize = infoTitleText.getSize("Best score:");
            infoTitleText.drawText("Best score:", position.x + size.x / 2f - textSize.x / 2f,
                    position.y + size.y / 4f + textSize.y / 2f);

            Vector2 infoTextSize = infoText.getSize("" + Core.getInstance().getStatsSaver().savedData.bestScore);
            infoText.drawText("" + Core.getInstance().getStatsSaver().savedData.bestScore, position.x + size.x / 2f - infoTextSize.x / 2f,
                    position.y + size.y / 4f + textSize.y / 2f - infoTextSize.y * 1.75f);
        }
        else
        {
            Vector2 textSize = levelNameText.getSize(node.name);
            levelNameText.drawText(node.name, position.x + size.x / 2f - textSize.x / 2f, position.y + size.y * 0.75f + textSize.y / 2f);
            // Draw reward
            textSize = rewardText.getSize("+" + node.reward);
            rewardText.drawText("+" + node.reward, position.x + size.x - textSize.x * 2f, position.y + size.y - textSize.y);
            // Set the coin position
            coinPosition.set(position.x + size.x - textSize.x + coinSize.x / 4f, position.y + size.y - textSize.y * 2f - coinSize.y / 8f);
            // Draw difficulty text
            textSize = infoTitleText.getSize("Difficulty:");
            infoTitleText.drawText("Difficulty:", position.x + size.x / 2f - textSize.x / 2f,
                    position.y + size.y / 4f + textSize.y / 2f);

            Vector2 infoTextSize = infoText.getSize(node.difficulty);
            infoText.drawText(node.difficulty, position.x + size.x / 2f - infoTextSize.x / 2f,
                    position.y + size.y / 4f + textSize.y / 2f - infoTextSize.y * 1.75f);

            // Draw attempts
            int attempts = Core.getInstance().getStatsSaver().savedData.levelAttempts[node.id - 1];
            textSize = attemptsText.getSize(attempts + " attempts");
            attemptsText.drawText(attempts + " attempts", position.x + textSize.x * 0.1f, position.y + size.y - textSize.y);

            // Draw the completed text
            if(Core.getInstance().getStatsSaver().savedData.levelCompleted[node.id - 1])
            {
                textSize = completedText.getSize("Completed");
                completedText.drawText("Completed", position.x + size.x / 2f - textSize.x / 2f,
                        position.y + size.y * 0.85f + textSize.y / 2f);
            }
        }
        // Update widgets
        playButton.process();
        // Did we click the button ?
        return playButton.isClicked();
    }

    public void render()
    {
        // Only render if we're visible on screen
        if(!isOnScreen())
            return;

        // Set the color
        Core.getInstance().getGraphicsManager().setColor(COLOR);
        // Draw the level containers
        Core.getInstance().getGraphicsManager().drawTextureRegion(containerTexture, position, size, 0f);
        // Restore the color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw the coin
        if(!endlessMode)
            Core.getInstance().getGraphicsManager().drawTextureRegion(coinTexture, coinPosition, coinSize, 0f);

        // Draw widgets
        playButton.render();

    }

    private boolean isOnScreen()
    {
        return position.x <= currentScreenPosition || position.x + size.x >= currentScreenPosition;
    }

    public LevelNode getNode() {
        return node;
    }
}
