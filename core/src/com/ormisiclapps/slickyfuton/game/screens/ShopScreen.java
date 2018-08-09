package com.ormisiclapps.slickyfuton.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.game.world.Lightening;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UICheckBox;
import com.ormisiclapps.slickyfuton.graphics.ui.UIPicker;
import com.ormisiclapps.slickyfuton.graphics.ui.UIMain;
import com.ormisiclapps.slickyfuton.graphics.ui.UISlider;
import com.ormisiclapps.slickyfuton.graphics.ui.UITabView;
import com.ormisiclapps.slickyfuton.graphics.ui.UIText;
import com.ormisiclapps.slickyfuton.graphics.windows.ShopItemWindow;
import com.ormisiclapps.slickyfuton.utility.Configuration;

/**
 * Created by OrMisicL on 7/27/2017.
 */

public class ShopScreen implements Screen
{
    private UIText mainText, eatablesText;
    private UIButton homeButton, colorNextButton, colorPreviousButton, secondaryColorNextButton, secondaryColorPreviousButton;
    private Texture background;
    private TextureRegion empty, coinTexture;
    private Vector2 tmpVector, tmpVector2;
    private Color tmpColor;
    private UITabView tabView;
    private UIPicker<Color> colorPicker, secondaryColorPicker, tailColorPicker, secondaryTailColorPicker, lightColorPicker;
    private UIPicker<TextureRegion> texturePicker, tailTexturePicker;
    private UICheckBox tailCheckBox, rotationCheckBox, lightCheckBox;
    private UISlider lightRadiusSlider, lightAlphaSlider;
    private float coinSize;
    private ShopItemWindow shopItemWindow;
    private int purchasingFrom;

    private static final float FADE_ALPHA = 0.6f;

    public ShopScreen()
    {
        // Reset instances
        mainText = null;
        eatablesText = null;
        background = null;
        coinTexture = null;
        tabView = null;
        colorNextButton = null;
        colorPreviousButton = null;
        secondaryColorNextButton = null;
        secondaryColorPreviousButton = null;
        lightColorPicker = null;
        tailCheckBox = null;
        lightRadiusSlider = null;
        lightAlphaSlider = null;
        shopItemWindow = null;
        // Create vectors
        tmpVector = new Vector2();
        tmpVector2 = new Vector2();
        // Create colors
        tmpColor = new Color();
        // Reset values
        purchasingFrom = 0;
    }

    @Override
    public void initialize()
    {
        // Get the shop background
        background = Core.getInstance().getResourcesManager().getResource("UI/ShopBackground", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Get the empty texture
        empty = Core.getInstance().getResourcesManager().getResource("UI/Empty", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Get the coin texture
        coinTexture = Core.getInstance().getResourcesManager().getResource("UI/EatableIcon", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create the main text instance
        mainText = new UIText(Core.getInstance().getGraphicsManager().HEIGHT / 35, Color.WHITE, 0f, Color.WHITE);
        // Set parameters
        mainText.setFadeEffected(false);
        mainText.toggle(false);
        // Calculate the coin size
        coinSize = Core.getInstance().getGraphicsManager().HEIGHT / 30f;
        // Create the secondary text instance
        eatablesText = new UIText((int)coinSize, Color.WHITE, 0f, Color.WHITE);
        // Set parameters
        eatablesText.setFadeEffected(false);
        eatablesText.toggle(false);
        // Create the home button
        homeButton = new UIButton(
                (TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/HomeButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set parameters
        float size = Core.getInstance().getGraphicsManager().HEIGHT / 15f;
        homeButton.setSize(new Vector2(size, size));
        homeButton.setPosition(new Vector2(size * 0.25f, Core.getInstance().getGraphicsManager().HEIGHT - size * 1.25f));
        homeButton.setFadeEffected(false);
        homeButton.toggle(false);
        // Create the tab view
        tabView = new UITabView(new Vector2(), new Vector2(Core.getInstance().getGraphicsManager().WIDTH,
                Core.getInstance().getGraphicsManager().HEIGHT * 0.75f));

        // Setup the tab view
        tabView.setFadeEffected(false);
        tabView.addTab("Futon");
        tabView.addTab("Tail");
        tabView.addTab("Traits");

        // Get state arrays
        boolean[] colorStates = Core.getInstance().getStatsSaver().savedData.colorsState;
        boolean[] characterSkinStates = Core.getInstance().getStatsSaver().savedData.characterSkinsState;
        boolean[] tailSkinStates = Core.getInstance().getStatsSaver().savedData.tailSkinsState;

        // Create the color picker
        colorPicker = new UIPicker<Color>(new Vector2(0f, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f), 1,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, colorStates);

        // Add colors
        for(int i = 0; i < GameIntelligence.shopColors.length; i++)
            colorPicker.addElement(GameIntelligence.shopColors[i]);

        // Set the selected element
        colorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.primaryColor);
        // Create the secondary color picker
        secondaryColorPicker = new UIPicker<Color>(new Vector2(0f, 0f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f), 1,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, colorStates);

        secondaryColorPicker.setFadeEffected(false);
        // Copy the primary color picker elements
        secondaryColorPicker.setElements(colorPicker.getElements());
        // Set the selected element
        secondaryColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.secondaryColor);

        // Create the tail color picker
        tailColorPicker = new UIPicker<Color>(new Vector2(0f, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f), 1,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, colorStates);

        // Copy the primary color picker elements
        tailColorPicker.setElements(colorPicker.getElements());
        // Set the selected element
        tailColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.tailColor);

        // Create the tail color picker
        secondaryTailColorPicker =  new UIPicker<Color>(new Vector2(0f, 0f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f), 1,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, colorStates);

        // Copy the primary color picker elements
        secondaryTailColorPicker.setElements(colorPicker.getElements());
        // Set the selected element
        secondaryTailColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.secondaryTailColor);

        // Create the texture picker
        texturePicker = new UIPicker<TextureRegion>(new Vector2(0f, (tabView.getSize().y - tabView.getSize().y / 10f) / 2f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 2f), 3,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, characterSkinStates);

        texturePicker.setFadeEffected(false);
        // Add textures
        for(int i = 0; i < Player.SKINS; i++)
            texturePicker.addElement(new TextureRegion(Player.getInstance().getSkin(i)));

        // Set selected element id
        texturePicker.setSelectedElementId(Core.getInstance().getStatsSaver().savedData.textureId);

        // Create the tail texture picker
        tailTexturePicker = new UIPicker<TextureRegion>(new Vector2(0f, (tabView.getSize().y - tabView.getSize().y / 10f) / 2f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 2f), 3,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, tailSkinStates);

        tailTexturePicker.setFadeEffected(false);
        // Add textures
        for(int i = 0; i < Player.TAIL_SKINS; i++)
            tailTexturePicker.addElement(new TextureRegion(Player.getInstance().getTailSkin(i)));

        // Set selected element id
        tailTexturePicker.setSelectedElementId(Core.getInstance().getStatsSaver().savedData.tailTextureId);

        // Create the light color picker
        lightColorPicker = new UIPicker<Color>(new Vector2(0f, 0f),
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH, (tabView.getSize().y - tabView.getSize().y / 10f) / 4f), 1,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 12f, colorStates);

        lightColorPicker.setFadeEffected(false);
        // Copy the primary color picker elements
        lightColorPicker.setElements(colorPicker.getElements());
        // Set the selected color
        Color lightColor = Core.getInstance().getStatsSaver().savedData.lightColor.cpy();
        lightColor.a = 1.0f;
        lightColorPicker.setSelectedElement(lightColor);

        // Create the next button
        colorNextButton = new UIButton(
                (TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/NextButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set parameters
        size = Core.getInstance().getGraphicsManager().HEIGHT / 25f;
        colorNextButton.setSize(new Vector2(size, size));
        colorNextButton.setPosition(new Vector2(Core.getInstance().getGraphicsManager().WIDTH - size * 1.75f,
                colorPicker.getElementsPosition().y - size / 2f));

        colorNextButton.setFadeEffected(false);
        colorNextButton.toggle(false);
        // Create the next button
        colorPreviousButton = new UIButton(
                (TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/NextButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set parameters
        colorPreviousButton.setSize(new Vector2(size, size));
        colorPreviousButton.setPosition(new Vector2(size * 0.75f, colorNextButton.getPosition().y));
        colorPreviousButton.setFadeEffected(false);
        colorPreviousButton.toggle(false);
        colorPreviousButton.setRotation(90f);

        // Create the secondary color next button
        secondaryColorNextButton = new UIButton(
                (TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/NextButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set parameters
        size = Core.getInstance().getGraphicsManager().HEIGHT / 25f;
        secondaryColorNextButton.setSize(new Vector2(size, size));
        secondaryColorNextButton.setPosition(new Vector2(Core.getInstance().getGraphicsManager().WIDTH - size * 1.75f,
                secondaryColorPicker.getElementsPosition().y - size / 2f));

        secondaryColorNextButton.setFadeEffected(false);
        secondaryColorNextButton.toggle(false);
        // Create the next button
        secondaryColorPreviousButton = new UIButton(
                (TextureRegion)Core.getInstance().getResourcesManager().getResource("UI/NextButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION));

        // Set parameters
        secondaryColorPreviousButton.setSize(new Vector2(size, size));
        secondaryColorPreviousButton.setPosition(new Vector2(size * 0.75f, secondaryColorNextButton.getPosition().y));
        secondaryColorPreviousButton.setFadeEffected(false);
        secondaryColorPreviousButton.toggle(false);
        secondaryColorPreviousButton.setRotation(90f);

        // Create the tail checkbox
        size = Core.getInstance().getGraphicsManager().HEIGHT / 20f;
        tailCheckBox = new UICheckBox(new Vector2(tabView.getSize().x * 0.2f, tabView.getSize().y * 0.625f), new Vector2(size, size),
                "Tail");

        // Set the position
        tailCheckBox.setPosition(new Vector2(tabView.getSize().x * 0.2f - tailCheckBox.getSize().x / 2f, tabView.getSize().y * 0.625f));
        // Set checked state
        tailCheckBox.setChecked(Core.getInstance().getStatsSaver().savedData.tailState);
        // Create the rotation checkbox
        rotationCheckBox = new UICheckBox(new Vector2(tabView.getSize().x * 0.5f, tabView.getSize().y * 0.625f), new Vector2(size, size),
                "Rotation");

        // Set the position
        rotationCheckBox.setPosition(new Vector2(tabView.getSize().x * 0.5f - rotationCheckBox.getSize().x / 2f, tabView.getSize().y * 0.625f));
        // Set checked state
        rotationCheckBox.setChecked(Core.getInstance().getStatsSaver().savedData.rotationState);
        // Create the rotation checkbox
        lightCheckBox = new UICheckBox(new Vector2(tabView.getSize().x * 0.8f, tabView.getSize().y * 0.625f), new Vector2(size, size),
                "Light");

        // Set the position
        lightCheckBox.setPosition(new Vector2(tabView.getSize().x * 0.8f - lightCheckBox.getSize().x / 2f, tabView.getSize().y * 0.625f));
        // Set checked state
        lightCheckBox.setChecked(Core.getInstance().getStatsSaver().savedData.lightState);

        // Create the light radius slider
        Vector2 sliderSize = new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2f,
                (tabView.getSize().y - tabView.getSize().y / 10f) / 36f);

        lightRadiusSlider = new UISlider(new Vector2(tabView.getSize().x / 2f - sliderSize.x / 2f, tabView.getSize().y / 2f - sliderSize.y * 5f), sliderSize,
                Color.WHITE, Color.BLACK, Color.BLUE);

        // Set the slider's position
        lightRadiusSlider.setValue((int)(Core.getInstance().getStatsSaver().savedData.lightDistance / Player.MAX_LIGHT_DISTANCE * 100f));

        // Create the light alpha slider
        lightAlphaSlider = new UISlider(new Vector2(tabView.getSize().x / 2f - sliderSize.x / 2f, tabView.getSize().y / 4f + sliderSize.y), sliderSize,
                Color.WHITE, Color.BLACK, Color.BLUE);

        // Set the slider's position
        lightAlphaSlider.setValue((int)(Core.getInstance().getStatsSaver().savedData.lightColor.a * 100f));

        // Create the item window
        Vector2 windowSize = new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 2f, Core.getInstance().getGraphicsManager().HEIGHT / 2.9f);
        shopItemWindow = new ShopItemWindow("Confirm Purchase",
                new Vector2(Core.getInstance().getGraphicsManager().WIDTH / 4f, Core.getInstance().getGraphicsManager().HEIGHT / 4f),
                windowSize);

        // Add to the main UI
        UIMain.getInstance().addWidget(mainText);
        UIMain.getInstance().addWidget(eatablesText);
        UIMain.getInstance().addWidget(homeButton);
        UIMain.getInstance().addWidget(colorNextButton);
        UIMain.getInstance().addWidget(colorPreviousButton);
        UIMain.getInstance().addWidget(secondaryColorNextButton);
        UIMain.getInstance().addWidget(secondaryColorPreviousButton);

        // Add to the tab view
        tabView.addElement("Futon", colorPicker);
        tabView.addElement("Futon", secondaryColorPicker);
        tabView.addElement("Futon", texturePicker);

        tabView.addElement("Tail", tailColorPicker);
        tabView.addElement("Tail", secondaryTailColorPicker);
        tabView.addElement("Tail", tailTexturePicker);

        tabView.addElement("Traits", tailCheckBox);
        tabView.addElement("Traits", rotationCheckBox);
        tabView.addElement("Traits", lightCheckBox);
        tabView.addElement("Traits", lightColorPicker);
        tabView.addElement("Traits", lightRadiusSlider);
        tabView.addElement("Traits", lightAlphaSlider);
    }

    @Override
    public void activate()
    {
        mainText.toggle(true);
        homeButton.toggle(true);
        eatablesText.toggle(true);
        colorNextButton.toggle(true);
        colorPreviousButton.toggle(true);
        secondaryColorNextButton.toggle(true);
        secondaryColorPreviousButton.toggle(true);
        // Restore selected items
        colorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.primaryColor);
        secondaryColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.secondaryColor);
        tailColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.tailColor);
        secondaryTailColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.secondaryTailColor);
        lightColorPicker.setSelectedElement(Core.getInstance().getStatsSaver().savedData.lightColor);
        texturePicker.setSelectedElementId(Core.getInstance().getStatsSaver().savedData.textureId);
        tailTexturePicker.setSelectedElementId(Core.getInstance().getStatsSaver().savedData.tailTextureId);
        // Initialize the player
        Player.getInstance().teleport(new Vector2(30f, Configuration.TERRAIN_ROOF_POSITION));
        // Set the camera to free
        Camera.getInstance().setFree();
        // Set the camera position
        Camera.getInstance().setPosition(new Vector2(30f, Configuration.TERRAIN_ROOF_POSITION / 2f));
        // Set the current music
        if(Core.getInstance().getCurrentMusic() != Core.getInstance().getScreensManager().getMainMenuScreen().getMusic())
        {
            Core.getInstance().getScreensManager().getMainMenuScreen().getMusic().play(true, 1.0f);
            Core.getInstance().setCurrentMusic(Core.getInstance().getScreensManager().getMainMenuScreen().getMusic());
        }
        // Enable lights
        Lightening.getInstance().toggleLights(true);
        // Enable player lights accordingly
        Player.getInstance().setLightState(lightCheckBox.isChecked());
        // Hide the ad banner
        Core.getInstance().getOSUtility().hideBannerAd();
    }

    @Override
    public void deactivate()
    {
        mainText.toggle(false);
        homeButton.toggle(false);
        eatablesText.toggle(false);
        colorNextButton.toggle(false);
        colorPreviousButton.toggle(false);
        secondaryColorNextButton.toggle(false);
        secondaryColorPreviousButton.toggle(false);
    }

    @Override
    public void update()
    {
        // Fade the screen
        if(Core.getInstance().getScreensManager().isTransitioning() && Core.getInstance().getScreensManager().getTransitionScreen() == this &&
                Core.getInstance().getGraphicsManager().getScreenEffects().getTransitionColor().a <= FADE_ALPHA)
        {
            // Halt the transition
            Core.getInstance().getGraphicsManager().getScreenEffects().haltTransition();
            // Fade the screen
            Core.getInstance().getGraphicsManager().getScreenEffects().fadeIn(Color.BLACK, FADE_ALPHA);
        }
        // Update the shop item window
        if(shopItemWindow.isVisible())
            shopItemWindow.process();

        // Toggle next and previous buttons based on the current tab
        if(tabView.isSelectChanged())
        {
            colorNextButton.toggle(!tabView.getSelectedTab().equals("Traits"));
            colorPreviousButton.toggle(!tabView.getSelectedTab().equals("Traits"));
        }
        // Check the current tab
        if(!tabView.getSelectedTab().equals("Traits"))
        {
            // Draw the color texts
            if(!shopItemWindow.isVisible())
            {
                Vector2 textSize = mainText.getSize("Primary Color");
                mainText.drawText("Primary Color", tabView.getSize().x / 2f - textSize.x / 2f, tabView.getSize().y / 2f - textSize.y * 3f);
            }
            Vector2 textSize = mainText.getSize("Secondary Color");
            mainText.drawText("Secondary Color", tabView.getSize().x / 2f - textSize.x / 2f, tabView.getSize().y / 4f - textSize.y * 2f);
        }
        else
        {
            // Draw the characteristics text
            Vector2 textSize = mainText.getSize("Futon Traits");
            mainText.drawText("Futon Traits", tabView.getSize().x / 2f - textSize.x / 2f, tabView.getSize().y * 0.8f);
            // Draw the light radius text
            if(!shopItemWindow.isVisible())
            {
                textSize = mainText.getSize("Light Radius & Alpha");
                mainText.drawText("Light Radius & Alpha", tabView.getSize().x / 2f - textSize.x / 2f, tabView.getSize().y / 1.75f - textSize.y * 2.5f);
            }
            // Draw the color text
            textSize = mainText.getSize("Light Color");
            mainText.drawText("Light Color", tabView.getSize().x / 2f - textSize.x / 2f, tabView.getSize().y / 4f - textSize.y * 2f);
        }
        // If the button is clicked then advance
        if(!Core.getInstance().getScreensManager().isTransitioning() && homeButton.isClicked())
            // Transit the screen
            Core.getInstance().getScreensManager().transitToScreen(Core.getInstance().getScreensManager().getMainMenuScreen());

        // If we confirmed a purchase
        if(shopItemWindow.isVisible() && shopItemWindow.isPurchaseConfirmed())
        {
            // Process purchase accordingly
            switch(purchasingFrom)
            {
                // Primary color
                case 0:
                {
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.COLOR_PRICE;
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.colorsState[colorPicker.getSelectedElementId()] = true;
                    // Set it as the current color
                    Core.getInstance().getStatsSaver().savedData.primaryColor.set(colorPicker.getSelectedElement());
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }

                // Secondary color
                case 1:
                {
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.COLOR_PRICE;
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.colorsState[secondaryColorPicker.getSelectedElementId()] = true;
                    // Set it as the current color
                    Core.getInstance().getStatsSaver().savedData.secondaryColor.set(secondaryColorPicker.getSelectedElement());
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }

                // Tail color
                case 2:
                {
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.COLOR_PRICE;
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.colorsState[tailColorPicker.getSelectedElementId()] = true;
                    // Set it as the current color
                    Core.getInstance().getStatsSaver().savedData.tailColor.set(tailColorPicker.getSelectedElement());
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }

                // Tail secondary color
                case 3:
                {
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.COLOR_PRICE;
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.colorsState[secondaryTailColorPicker.getSelectedElementId()] = true;
                    // Set it as the current color
                    Core.getInstance().getStatsSaver().savedData.secondaryTailColor.set(secondaryTailColorPicker.getSelectedElement());
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }

                // Light color
                case 4:
                {
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.COLOR_PRICE;
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.colorsState[lightColorPicker.getSelectedElementId()] = true;
                    // Set it as the current color
                    Core.getInstance().getStatsSaver().savedData.lightColor.set(lightColorPicker.getSelectedElement());
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }

                // Texture skin
                case 5:
                {
                    int id = texturePicker.getSelectedElementId();
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.characterPrices[id];
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.characterSkinsState[id] = true;
                    // Set it as the current skin
                    Core.getInstance().getStatsSaver().savedData.textureId = id;
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }

                // Tail skin
                case 6:
                {
                    int id = tailTexturePicker.getSelectedElementId();
                    // Reduce the eatables collected
                    Core.getInstance().getStatsSaver().savedData.eatablesCollected -= GameIntelligence.tailSkinPrices[id];
                    // Unlock the item
                    Core.getInstance().getStatsSaver().savedData.tailSkinsState[id] = true;
                    // Set it as the current skin
                    Core.getInstance().getStatsSaver().savedData.tailTextureId = id;
                    // Save
                    Core.getInstance().getStatsSaver().save();
                    break;
                }
            }
            // Hide the item purchasing window
            Core.getInstance().getScreensManager().hideCurrentWindow();
        }

        // Draw the coins number
        Vector2 textSize = eatablesText.getSize("" + Core.getInstance().getStatsSaver().savedData.eatablesCollected);
        eatablesText.drawText("" + Core.getInstance().getStatsSaver().savedData.eatablesCollected,
                Core.getInstance().getGraphicsManager().WIDTH - coinSize * 2.25f - textSize.x,
                Core.getInstance().getGraphicsManager().HEIGHT - coinSize * 1.5f + textSize.y / 2f);

        // Do we have a new color ?
        if(colorPicker.isSelectChanged())
        {
            // Get the selected color
            Color selectedColor = colorPicker.getSelectedElement();
            // Set the player's color
            Player.getInstance().setColor(selectedColor);
            // Only save the choice if the color is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.colorsState[colorPicker.getSelectedElementId()])
            {
                // Set the new color
                Core.getInstance().getStatsSaver().savedData.primaryColor.set(selectedColor);
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.COLOR_PRICE;
                // Show the item window
                shopItemWindow.show(GameIntelligence.colorNames[colorPicker.getSelectedElementId()] + " color",
                        "" + GameIntelligence.COLOR_PRICE, canBuy);

                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 0;
            }
        }
        // Do we have a new secondary color ?
        else if(secondaryColorPicker.isSelectChanged())
        {
            // Get the selected color
            Color selectedColor = secondaryColorPicker.getSelectedElement();
            // Set the player's secondary color
            Player.getInstance().setSecondaryColor(selectedColor);
            // Only save the choice if the color is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.colorsState[secondaryColorPicker.getSelectedElementId()])
            {
                // Set the secondary color
                Core.getInstance().getStatsSaver().savedData.secondaryColor.set(selectedColor);
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.COLOR_PRICE;
                // Show the item window
                shopItemWindow.show(GameIntelligence.colorNames[secondaryColorPicker.getSelectedElementId()] + " color",
                        "" + GameIntelligence.COLOR_PRICE, canBuy);

                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 1;
            }
        }
        // Do we have a new tail color ?
        else if(tailColorPicker.isSelectChanged())
        {
            // Get the selected color
            Color selectedColor = tailColorPicker.getSelectedElement();
            // Set the player's color
            Player.getInstance().setTailColor(selectedColor);
            // Only save the choice if the color is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.colorsState[tailColorPicker.getSelectedElementId()])
            {
                // Set the tail color
                Core.getInstance().getStatsSaver().savedData.tailColor.set(selectedColor);
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.COLOR_PRICE;
                // Show the item window
                shopItemWindow.show(GameIntelligence.colorNames[tailColorPicker.getSelectedElementId()] + " color",
                        "" + GameIntelligence.COLOR_PRICE, canBuy);

                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 2;
            }
        }
        // Do we have a new tail secondary color ?
        else if(secondaryTailColorPicker.isSelectChanged())
        {
            // Get the selected color
            Color selectedColor = secondaryTailColorPicker.getSelectedElement();
            // Set the player's tail secondary color
            Player.getInstance().setSecondaryTailColor(selectedColor);
            // Only save the choice if the color is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.colorsState[secondaryTailColorPicker.getSelectedElementId()])
            {
                // Set the tail secondary color
                Core.getInstance().getStatsSaver().savedData.secondaryTailColor.set(selectedColor);
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.COLOR_PRICE;
                // Show the item window
                shopItemWindow.show(GameIntelligence.colorNames[secondaryTailColorPicker.getSelectedElementId()] + " color",
                        "" + GameIntelligence.COLOR_PRICE, canBuy);

                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 3;
            }
        }
        // Do we have a new light color ?
        else if(lightColorPicker.isSelectChanged())
        {
            // Get the selected color
            Color selectedColor = lightColorPicker.getSelectedElement();
            // Set the light color
            tmpColor.set(selectedColor);
            tmpColor.a = Core.getInstance().getStatsSaver().savedData.lightColor.a;
            // Set the player's light color
            Player.getInstance().setLightColor(tmpColor);
            // Only save the choice if the color is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.colorsState[lightColorPicker.getSelectedElementId()])
            {
                // Set the light's color
                Core.getInstance().getStatsSaver().savedData.lightColor.set(tmpColor);
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.COLOR_PRICE;
                // Show the item window
                shopItemWindow.show(GameIntelligence.colorNames[lightColorPicker.getSelectedElementId()] + " color",
                        "" + GameIntelligence.COLOR_PRICE, canBuy);

                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 4;
            }
        }
        // Do we have a new texture ?
        else if(texturePicker.isSelectChanged())
        {
            // Get the selected texture id
            int skinId = texturePicker.getSelectedElementId();
            // Set the player's skin
            Player.getInstance().setSkin(skinId);
            // Only save the choice if the skin is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.characterSkinsState[texturePicker.getSelectedElementId()])
            {
                // Set the new skin
                Core.getInstance().getStatsSaver().savedData.textureId = skinId;
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                int id = texturePicker.getSelectedElementId();
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.characterPrices[id];
                // Show the item window
                shopItemWindow.show("Futon skin", "" + GameIntelligence.characterPrices[id], canBuy);
                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 5;
            }
        }
        // Do we have a new tail texture ?
        else if(tailTexturePicker.isSelectChanged())
        {
            // Get the selected texture id
            int skinId = tailTexturePicker.getSelectedElementId();
            // Set the player's tail skin
            Player.getInstance().setTailSkin(skinId);
            // Only save the choice if the tail skin is already unlocked
            if(Core.getInstance().getStatsSaver().savedData.tailSkinsState[tailTexturePicker.getSelectedElementId()])
            {
                // Set the new tail skin
                Core.getInstance().getStatsSaver().savedData.tailTextureId = skinId;
                // Save
                Core.getInstance().getStatsSaver().save();
            }
            else
            {
                int id = tailTexturePicker.getSelectedElementId();
                // Set the can buy flag
                boolean canBuy = Core.getInstance().getStatsSaver().savedData.eatablesCollected >= GameIntelligence.tailSkinPrices[id];
                // Show the item window
                shopItemWindow.show("Tail skin", "" + GameIntelligence.tailSkinPrices[id], canBuy);
                // Set it as the current window
                Core.getInstance().getScreensManager().setCurrentWindow(shopItemWindow);
                // Set the purchasing from flag
                purchasingFrom = 6;
            }
        }
        // Next page ?
        else if(colorNextButton.isClicked())
        {
            if(tabView.getSelectedTab().equals("Futon"))
                colorPicker.nextPage();
            else
                tailColorPicker.nextPage();
        }
        // Previous page ?
        else if(colorPreviousButton.isClicked())
        {
            if(tabView.getSelectedTab().equals("Futon"))
                colorPicker.previousPage();
            else
                tailColorPicker.previousPage();
        }
        // Next page (secondary color) ?
        else if(secondaryColorNextButton.isClicked())
        {
            if(tabView.getSelectedTab().equals("Futon"))
                secondaryColorPicker.nextPage();
            else if(tabView.getSelectedTab().equals("Tail"))
                secondaryTailColorPicker.nextPage();
            else
                lightColorPicker.nextPage();
        }
        // Previous page (secondary color) ?
        else if(secondaryColorPreviousButton.isClicked())
        {
            if(tabView.getSelectedTab().equals("Futon"))
                secondaryColorPicker.previousPage();
            else if(tabView.getSelectedTab().equals("Tail"))
                secondaryTailColorPicker.previousPage();
            else
                lightColorPicker.previousPage();
        }
        // Tail state ?
        else if(tailCheckBox.isClicked())
        {
            Player.getInstance().setTailState(tailCheckBox.isChecked());
            // Set the save tail state
            Core.getInstance().getStatsSaver().savedData.tailState = tailCheckBox.isChecked();
            // Save
            Core.getInstance().getStatsSaver().save();
        }
        // Rotation state ?
        else if(rotationCheckBox.isClicked())
        {
            Player.getInstance().setRotationState(rotationCheckBox.isChecked());
            // Set the save rotation state
            Core.getInstance().getStatsSaver().savedData.rotationState = rotationCheckBox.isChecked();
            // Save
            Core.getInstance().getStatsSaver().save();
        }
        // Light state ?
        else if(lightCheckBox.isClicked())
        {
            Player.getInstance().setLightState(lightCheckBox.isChecked());
            // Set the save light state
            Core.getInstance().getStatsSaver().savedData.lightState = lightCheckBox.isChecked();
            // Save
            Core.getInstance().getStatsSaver().save();
        }
        // Light radius ?
        else if(lightRadiusSlider.isValueChanged())
        {
            // Calculate the new light's distance
            float distance = Player.MAX_LIGHT_DISTANCE * (float)lightRadiusSlider.getValue() / 100f;
            // Set the player's light distance
            Player.getInstance().setLightDistance(distance);
            // Set the save light distance
            Core.getInstance().getStatsSaver().savedData.lightDistance = distance;
            // Save
            Core.getInstance().getStatsSaver().save();
        }
        // Light alpha ?
        else if(lightAlphaSlider.isValueChanged())
        {
            // Calculate the new light's alpha
            float alpha = (float)lightAlphaSlider.getValue() / 100f;
            // Set the player's light alpha
            Player.getInstance().setLightAlpha(alpha);
            // Set the save light alpha
            Core.getInstance().getStatsSaver().savedData.lightColor.a = alpha;
            // Save
            Core.getInstance().getStatsSaver().save();
        }
        // Update game world
        GameWorld.getInstance().process();
        // Update camera
        tmpVector.set(Camera.getInstance().getPosition()).add(0.75f, 0f);
        Camera.getInstance().setPosition(tmpVector);
        // Update player's position
        Vector2 position = Camera.getInstance().screenToWorld(tmpVector2.set(0f, Core.getInstance().getGraphicsManager().HEIGHT * 0.1f));
        Player.getInstance().setPosition(tmpVector.set(tmpVector.x + 2f, position.y));
        Camera.getInstance().update();
        // Update player
        Player.getInstance().process();
        // Update tab view
        tabView.process();
    }

    @Override
    public void render()
    {
        // Set the color (TODO: Find where the FUCK we change the WHITE color and fuck things up)
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw background
        Core.getInstance().getGraphicsManager().drawTexture(background, Core.getInstance().getGraphicsManager().EMPTY_VECTOR,
                Core.getInstance().getGraphicsManager().SCREEN_VECTOR, 0f);
    }

    @Override
    public void postFadeRender()
    {
        // Draw tab view
        tabView.render();
        // Ensure that we're not in the "Other" tab
        if(!tabView.getSelectedTab().equals("Traits"))
        {
            // Draw the strip's line
            float position = tabView.getPosition().y + tabView.getSize().y / 2.25f;
            float lineSize = tabView.getSize().y / 240f;
            Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, position - lineSize,
                    Core.getInstance().getGraphicsManager().WIDTH, lineSize, 0f);
        }
        else
        {
            // Draw the strip's line
            float position = tabView.getSize().y / 1.75f;
            float lineSize = tabView.getSize().y / 240f;
            Core.getInstance().getGraphicsManager().drawTextureRegion(empty, 0f, position - lineSize,
                    Core.getInstance().getGraphicsManager().WIDTH, lineSize, 0f);
        }
        // Draw player
        Player.getInstance().render();
        // Draw the coin texture
        Core.getInstance().getGraphicsManager().drawTextureRegion(coinTexture, Core.getInstance().getGraphicsManager().WIDTH - coinSize * 2f,
                Core.getInstance().getGraphicsManager().HEIGHT - coinSize * 2f, coinSize, coinSize, 0f);

        // Draw the shop item window
        if(shopItemWindow.isVisible())
            shopItemWindow.render();
    }

    @Override
    public void dispose()
    {

    }
}
