package com.ormisiclapps.slickyfuton.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UIProgressBar;
import com.ormisiclapps.slickyfuton.graphics.ui.UIText;
import com.ormisiclapps.slickyfuton.os.OSUtility;
import com.ormisiclapps.slickyfuton.os.RewardedVideoListener;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by Anis on 3/18/2017.
 */

public class DeathScreen implements Screen
{
    private UIButton restartButton, homeButton, shopButton, rewardButton;
    private TextureRegion eatableTexture, empty;
    private UIText eatableText, scoreText, newRecordText, coinsInThisGameText, levelProgressText, levelCompletionText;
    private UIProgressBar levelCompletionProgressBar;
    private Vector2 eatableSize;
    private Vector2 eatablePosition;
    private Vector2 scoreTextPosition;
    private float verticalStripPosition, verticalStripSize;
    private Color stripColor;
    private Vector2 tmpVector;
    private int deathsSinceLastInterstitialAd, deathsSinceLastVideoAd, deathsSinceLastReward, deathsSinceRating;
    private int coinsInThisGame;
    private float positionFactor;
    private long coinsShown;

    private static final float FADE_ALPHA = 0.7f;

    private static final int INTERSTITIAL_AD_DEATHS = 2;
    private static final int VIDEO_AD_DEATHS = 12;
    private static final int REWARD_AD_DEATHS = 10;
    private static final int RATING_DEATHS = 10;

    public DeathScreen()
    {
        // Reset instances
        restartButton = null;
        homeButton = null;
        shopButton = null;
        rewardButton = null;
        coinsInThisGameText = null;
        eatableTexture = null;
        eatableText = null;
        scoreText = null;
        newRecordText = null;
        eatablePosition = null;
        scoreTextPosition = null;
        stripColor = null;
        tmpVector = null;
    }

    @Override
    public void initialize()
    {
        // Create vectors
        tmpVector = new Vector2();
        scoreTextPosition = new Vector2();
        // Get height and width
        float w = Core.getInstance().getGraphicsManager().WIDTH;
        float h = Core.getInstance().getGraphicsManager().HEIGHT;
        // Set the vertical strip size and position
        verticalStripSize = h / 2.5f;
        verticalStripPosition = 0f;
        // Set the strip color
        stripColor = new Color(0f, 0f, 0f, 0.4f);
        // Load empty texture
        empty = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create the eatable texture
        eatableTexture = Core.getInstance().getResourcesManager().getResource("UI/EatableIcon", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create buttons
        restartButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/RestartButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        homeButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/HomeButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        shopButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/ShopButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        rewardButton = new UIButton((TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/RewardButton",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set their positions
        float size = h / 5f;
        restartButton.setFadeEffected(false);
        restartButton.setSize(new Vector2(size, size));
        restartButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f, h / 6f + size / 2f), new Vector2(size, size))));

        size = h / 6f;
        homeButton.setFadeEffected(false);
        homeButton.setSize(new Vector2(size, size));
        homeButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f - size * 1.5f, h / 6f + size / 2f), new Vector2(size, size))));

        shopButton.setFadeEffected(false);
        shopButton.setSize(new Vector2(size, size));
        shopButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w / 2f + size * 1.5f, h / 6f + size / 2f), new Vector2(size, size))));

        rewardButton.setFadeEffected(false);
        rewardButton.setSize(new Vector2(size, size));
        rewardButton.setPosition(new Vector2(GameMath.getCenteredPosition(
                new Vector2(w * 0.88f, verticalStripSize), new Vector2(size, size))));


        // Create the score text
        scoreText = new UIText((int)h / 8, Color.WHITE, 0f, null);
        scoreText.setFadeEffected(false);
        // Create the eatable text
        eatableText = new UIText((int)h / 20, Color.WHITE, 0f, null);
        eatableText.setFadeEffected(false);
        // Create the level completion text
        levelCompletionText = new UIText((int)h / 20, Color.WHITE, 0f, null);
        levelCompletionText.setFadeEffected(false);
        // Create the level progress text
        levelProgressText = new UIText((int)h / 30, Color.WHITE, 0f, null);
        levelProgressText.setFadeEffected(false);
        // Create the new record text
        newRecordText = new UIText((int)h / 35, new Color(0.18f, 0.92f, 0.93f, 1f), 0f, null);
        newRecordText.setFadeEffected(false);
        // Create the coins in this game text text
        coinsInThisGameText = new UIText((int)h / 20, new Color(0.2f, 0.4f, 0.6f, 1f), 0f, null);
        coinsInThisGameText.setFadeEffected(false);
        // Create the level completion progress bar
        Vector2 progressBarSize = new Vector2(Core.getInstance().getGraphicsManager().WIDTH * 0.6f,
                    Core.getInstance().getGraphicsManager().HEIGHT / 24f);

        levelCompletionProgressBar = new UIProgressBar(new Vector2(
                Core.getInstance().getGraphicsManager().HALF_SCREEN_VECTOR).sub(progressBarSize.x / 2f, progressBarSize.y / 2f),
                progressBarSize, Color.WHITE, Color.BLACK, new Color(0.2f, 0.4f, 0.6f, 1f));

        levelCompletionProgressBar.setFadeEffected(false);

        // Hide elements
        scoreText.toggle(false);
        eatableText.toggle(false);
        newRecordText.toggle(false);
        restartButton.toggle(false);
        homeButton.toggle(false);
        shopButton.toggle(false);
        levelProgressText.toggle(false);
        levelCompletionText.toggle(false);
        levelCompletionProgressBar.toggle(false);
        // Add them to the UI
        UIMain.getInstance().addWidget(restartButton);
        UIMain.getInstance().addWidget(homeButton);
        UIMain.getInstance().addWidget(shopButton);
        UIMain.getInstance().addWidget(rewardButton);
        UIMain.getInstance().addWidget(scoreText);
        UIMain.getInstance().addWidget(eatableText);
        UIMain.getInstance().addWidget(newRecordText);
        UIMain.getInstance().addWidget(coinsInThisGameText);
        UIMain.getInstance().addWidget(levelCompletionProgressBar);
        UIMain.getInstance().addWidget(levelProgressText);
        UIMain.getInstance().addWidget(levelCompletionText);
        // Get text size
        Vector2 textSize = eatableText.getSize("1000");
        // Set the eatable size
        eatableSize = new Vector2(textSize.y, textSize.y);
        // Create the eatable position instance
        eatablePosition = new Vector2(w / 28f - textSize.y / 2f, h - h / 10f - textSize.y / 4f);
        // Reset values
        deathsSinceLastInterstitialAd = 0;
        deathsSinceLastVideoAd = 0;
        deathsSinceLastReward = -4;
        deathsSinceRating = 0;
        coinsInThisGame = 0;
        coinsShown = 0;
    }

    @Override
    public void activate()
    {
        // Show elements
        restartButton.toggle(true);
        scoreText.toggle(true);
        eatableText.toggle(true);
        newRecordText.toggle(true);
        coinsInThisGameText.toggle(true);
        homeButton.toggle(true);
        shopButton.toggle(true);
        rewardButton.toggle(false);
        // Do we need to toggle the progress bar ?
        if(!GameLogic.getInstance().isEndless())
        {
            levelCompletionProgressBar.setProgress(GameLogic.getInstance().getLevelCompletion());
            levelCompletionProgressBar.toggle(true);
            levelProgressText.toggle(true);
            levelCompletionText.toggle(true);
            if(GameLogic.getInstance().isLevelCompleted())
                levelCompletionText.setColor(new Color(0f, 0.75f, 0f, 1f));
            else
                levelCompletionText.setColor(Color.WHITE);
        }
        // Fade the screen
        Core.getInstance().getGraphicsManager().getScreenEffects().fadeIn(Color.BLACK, FADE_ALPHA);
        // Save stats
        Core.getInstance().getStatsSaver().save();
        // Always submit the best score whenever a connectivity is available
        if(Core.getInstance().getOSUtility().isNetworkConnected())
            Core.getInstance().getOSUtility().submitScore(Core.getInstance().getStatsSaver().savedData.bestScore);

        // Get coins in this game
        coinsInThisGame = GameLogic.getInstance().getCoinsInThisGame();
        // Set the coins shown
        coinsShown = Core.getInstance().getStatsSaver().savedData.eatablesCollected - coinsInThisGame;
        // Set the position factor
        positionFactor = coinsInThisGame == 0 ? 1f : 2f;
        // Increase values
        deathsSinceLastInterstitialAd++;
        deathsSinceLastVideoAd++;
        deathsSinceLastReward++;
        deathsSinceRating++;
        // Show ads accordingly
        if(deathsSinceLastVideoAd >= VIDEO_AD_DEATHS)
        {
            // Show the native express ad
            if(!Core.getInstance().getOSUtility().isSkippableVideoAdLoaded())
            {
                // Display the banner ad
                Core.getInstance().getOSUtility().showBannerAd();
                // Reset counter
                deathsSinceLastVideoAd = -3;
            }
            else
            {
                // Show the native express add
                Core.getInstance().getOSUtility().showSkippableVideoAd();
                // Reset counter
                deathsSinceLastVideoAd = 0;
            }
        }
        else if(deathsSinceLastInterstitialAd >= INTERSTITIAL_AD_DEATHS)
        {
            // Show interstitial ad
            if(!Core.getInstance().getOSUtility().showInterstitialAd())
                // Display the banner ad
                Core.getInstance().getOSUtility().showBannerAd();

            // Reset counter
            deathsSinceLastInterstitialAd = 0;
        }
        if(deathsSinceLastReward >= REWARD_AD_DEATHS || deathsSinceLastReward == -1)
        {
            // Ensure the rewarded video is loaded
            if(Core.getInstance().getOSUtility().isNetworkConnected() && Core.getInstance().getOSUtility().isRewardedVideoAdLoaded())
            {
                // Show the rewarded button
                rewardButton.toggle(true);
                // Reset counter
                deathsSinceLastReward = 0;
            }
            else
            {
                // Display the banner ad
                Core.getInstance().getOSUtility().showBannerAd();
                // Set the counter to check reward for the next death
                deathsSinceLastReward = REWARD_AD_DEATHS - 1;
            }
        }
        else
            // Display the banner ad
            Core.getInstance().getOSUtility().showBannerAd();

        // Unlock first game achievement
        Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_FIRST_GAME);
        // 10 games
        if(Core.getInstance().getStatsSaver().savedData.gamesPlayed >= 10)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_10_GAMES);
        if(Core.getInstance().getStatsSaver().savedData.gamesPlayed >= 100)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_100_GAMES);
        if(Core.getInstance().getStatsSaver().savedData.gamesPlayed >= 1000)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_1000_GAMES);
        if(Core.getInstance().getStatsSaver().savedData.bestScore >= 10)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_10_SCORE);
        if(Core.getInstance().getStatsSaver().savedData.bestScore >= 20)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_20_SCORE);
        if(Core.getInstance().getStatsSaver().savedData.bestScore >= 40)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_40_SCORE);
        if(Core.getInstance().getStatsSaver().savedData.bestScore >= 50)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_50_SCORE);
        if(Core.getInstance().getStatsSaver().savedData.bestScore >= 100)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_100_SCORE);
        if(Core.getInstance().getStatsSaver().savedData.bestScore >= 200)
            Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_200_SCORE);

        // Level achievements
        if(!GameLogic.getInstance().isEndless() && GameLogic.getInstance().isLevelCompleted())
        {
            // Just starting
            if(GameLogic.getInstance().getCurrentLevelNode().id == 0)
                Core.getInstance().getOSUtility().unlockAchievement(OSUtility.Achievement.ACHIEVEMENT_JUST_STARTING_LEVEL);
        }
        if(!Core.getInstance().getStatsSaver().savedData.neverRate && deathsSinceRating >= RATING_DEATHS
                && Core.getInstance().getOSUtility().isNetworkConnected())
        {
            // Show the rating dialog
            Core.getInstance().getOSUtility().showRateDialog(new OSUtility.RatingListener() {
                @Override
                public void onDismissed(boolean never) {
                    if (never)
                    {
                        Core.getInstance().getStatsSaver().savedData.neverRate = true;
                        Core.getInstance().getStatsSaver().save();
                    }
                }
            });
            // Reset the rating value
            deathsSinceRating = -20;
        }

    }

    @Override
    public void update()
    {
        // Process game intelligence
        GameIntelligence.getInstance().process();
        // Update the game world
        GameWorld.getInstance().process();
        // Restart the game if the restart button is pressed
        if(!Core.getInstance().getGraphicsManager().getScreenEffects().isTransitioning() && restartButton.isClicked())
            // Transit to the game screen
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getGameScreen());
        // Back to main menu if the home button is pressed
        else if(!Core.getInstance().getGraphicsManager().getScreenEffects().isTransitioning() && homeButton.isClicked())
        {
            // Transit to the game screen
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getMainMenuScreen());
            // Free the current level's music
            if(!GameLogic.getInstance().isEndless())
                GameScreen.getInstance().disposeCurrentMusic();
        }
        // If the shop button is clicked then advance
        else if(!Core.getInstance().getScreensManager().isTransitioning() && shopButton.isClicked())
            // Transit the screen
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getShopScreen());
        // If the reward button is clicked then advance
        else if(!Core.getInstance().getScreensManager().isTransitioning() && rewardButton.isClicked())
        {
            // Show the rewarded video
            Core.getInstance().getOSUtility().showRewardedVideoAd(new RewardedVideoListener() {
                @Override
                public void onRewarded()
                {
                    // Add the reward
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected += Configuration.REWARD_AMOUNT;
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    // Hide the reward button
                    rewardButton.toggle(false);
                    // Reset the position factor
                    positionFactor = 2f;
                    // Set the coins in this game
                    coinsInThisGame = Configuration.REWARD_AMOUNT;
                }
            });
            // Hide the rewarded button
            rewardButton.toggle(false);
        }
        else
        {
            float a = 0f;
            // Decrease the position factor
            if(positionFactor > 1f)
            {
                // Decrease the position factor
                positionFactor -= 0.03f;
                // Calculate the alpha
                a = (positionFactor - 1f);
            }
            else if(coinsShown < Core.getInstance().getStatsSaver().savedData.eatablesCollected)
                coinsShown++;

            // Draw the eatables count
            eatableText.drawText(Long.toString(coinsShown),
                    eatablePosition.x + eatableSize.x * 1.5f, eatablePosition.y + eatableSize.y);

            // Validate the text
            if(a > 0f)
            {
                // Enable the text
                coinsInThisGameText.toggle(true);
                // Set the text's alpha
                coinsInThisGameText.setAlpha(a);
                // Get the text size
                Vector2 textSize = coinsInThisGameText.getSize("+" + coinsInThisGame);
                // Draw the eatables count
                coinsInThisGameText.drawText("+" + coinsInThisGame,
                        eatablePosition.x + eatableSize.x * 1.5f + textSize.x / 2f, eatablePosition.y + eatableSize.y - textSize.y * positionFactor);
            }
            else
                // Disable text
                coinsInThisGameText.toggle(false);

            // Are we in endless mode ?
            if(GameLogic.getInstance().isEndless())
            {
                // Get the score text size
                Vector2 textSize = scoreText.getSize(Long.toString(GameLogic.getInstance().getLastScore()));
                // Calculate the score text position
                scoreTextPosition.set(Core.getInstance().getGraphicsManager().WIDTH / 2 - textSize.x / 2,
                        Core.getInstance().getGraphicsManager().HEIGHT / 4f * 3f - textSize.y / 2f);

                // Draw the score
                scoreText.drawText(Long.toString(GameLogic.getInstance().getLastScore()), scoreTextPosition);

                // Get the high score text size
                Vector2 bestScoreTextSize = eatableText.getSize("Best score: " + Core.getInstance().getStatsSaver().savedData.bestScore);
                // Calculate the high score text position
                tmpVector.set(Core.getInstance().getGraphicsManager().WIDTH / 2 - bestScoreTextSize.x / 2,
                        scoreTextPosition.y - textSize.y * 1.75f);

                // Draw the high score
                eatableText.drawText("Best score: " + Core.getInstance().getStatsSaver().savedData.bestScore, tmpVector);

                // Write the new record
                if(GameLogic.getInstance().isNewRecord())
                {
                    // Get new record text size
                    Vector2 recordTextSize = newRecordText.getSize("New Record!");
                    // Draw the score
                    newRecordText.drawText("New Record!", Core.getInstance().getGraphicsManager().WIDTH / 2 - recordTextSize.x / 2,
                            tmpVector.y - bestScoreTextSize.y * 1.25f);
                }

                // Draw game over
                textSize = eatableText.getSize("Game Over");
                eatableText.drawText("Game Over", Core.getInstance().getGraphicsManager().WIDTH / 2f - textSize.x / 2f,
                        eatablePosition.y + eatableSize.y);
            }
            else
            {
                // Did we successfully completed the level ?
                if(GameLogic.getInstance().isLevelCompleted())
                {
                    // Draw level name
                    Vector2 textSize = eatableText.getSize(GameLogic.getInstance().getCurrentLevelNode().name);
                    eatableText.drawText(GameLogic.getInstance().getCurrentLevelNode().name, Core.getInstance().getGraphicsManager().WIDTH / 2f - textSize.x / 2f,
                            Core.getInstance().getGraphicsManager().HEIGHT * 0.75f);

                    // Draw level completed
                    textSize = levelCompletionText.getSize("Level completed");
                    levelCompletionText.drawText("Level completed",
                            Core.getInstance().getGraphicsManager().WIDTH / 2f - textSize.x / 2f,
                            levelCompletionProgressBar.getPosition().y + levelCompletionProgressBar.getSize().y + textSize.y * 2f);
                }
                else
                {
                    // Draw game over
                    Vector2 textSize = eatableText.getSize("Game Over");
                    eatableText.drawText("Game Over", Core.getInstance().getGraphicsManager().WIDTH / 2f - textSize.x / 2f,
                            eatablePosition.y + eatableSize.y);

                    // Draw level completion
                    textSize = levelCompletionText.getSize("Level completion");
                    levelCompletionText.drawText("Level completion",
                            Core.getInstance().getGraphicsManager().WIDTH / 2f - textSize.x / 2f,
                            levelCompletionProgressBar.getPosition().y + levelCompletionProgressBar.getSize().y + textSize.y * 2f);
                }
                // Draw the completion percentage
                Vector2 textSize = levelProgressText.getSize("" + GameLogic.getInstance().getLevelCompletion() + "%");
                levelProgressText.drawText("" + GameLogic.getInstance().getLevelCompletion() + "%",
                        levelCompletionProgressBar.getPosition().x + levelCompletionProgressBar.getSize().x / 2f - textSize.x / 2f,
                        levelCompletionProgressBar.getPosition().y + levelCompletionProgressBar.getSize().y / 2f + textSize.y / 2f);
            }
        }
    }

    @Override
    public void render()
    {
        // Draw terrain
        GameLogic.getInstance().getTerrain().render();
    }

    @Override
    public void postFadeRender()
    {
        // Draw the eatable icon
        Core.getInstance().getGraphicsManager().drawTextureRegion(eatableTexture, eatablePosition, eatableSize, 0f);
        // Set the stripe's color
        Core.getInstance().getGraphicsManager().setColor(stripColor);
        // Draw the stripe
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, verticalStripPosition, Core.getInstance().getGraphicsManager().WIDTH, verticalStripSize, 0f);
        // Restore the default color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw the strip's line
        float lineSize = verticalStripSize / 120f;
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, verticalStripSize - lineSize,
                Core.getInstance().getGraphicsManager().WIDTH, lineSize, 0f);

        // Get the score line
        Vector2 textSize = scoreText.getSize(Long.toString(GameLogic.getInstance().getLastScore()));
        // Calculate the score text position
        tmpVector.set(textSize.x * 1.5f, textSize.y / 10f);
        scoreTextPosition.set(Core.getInstance().getGraphicsManager().WIDTH / 2f - tmpVector.x / 2f, scoreTextPosition.y - textSize.y * 1.5f);
        Core.getInstance().getGraphicsManager().drawTextureRegion(empty, scoreTextPosition, tmpVector, 0f);
    }

    @Override
    public void dispose()
    {

    }

    @Override
    public void deactivate()
    {
        // Hide UI elements
        restartButton.toggle(false);
        scoreText.toggle(false);
        eatableText.toggle(false);
        homeButton.toggle(false);
        shopButton.toggle(false);
        rewardButton.toggle(false);
        levelCompletionText.toggle(false);
        levelProgressText.toggle(false);
        coinsInThisGameText.toggle(false);
        levelCompletionProgressBar.toggle(false);
        // Debug stuff
        if(!GameLogic.getInstance().isEndless())
            Core.getInstance().getLevelManager().reloadLevel(GameLogic.getInstance().getCurrentLevelNode());
    }
}
