package com.ormisiclapps.slickyfuton.graphics.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.graphics.ui.UIButton;
import com.ormisiclapps.slickyfuton.graphics.ui.UIWindow;

/**
 * Created by OrMisicL on 8/20/2017.
 */

public class ShopItemWindow extends UIWindow
{
    private UIButton confirmButton;

    public ShopItemWindow(String title, Vector2 position, Vector2 size)
    {
        super(title, position, size, new Color(0.05f, 0.05f, 0.05f, 1f));
        // Load the button's texture
        TextureRegion texture = Core.getInstance().getResourcesManager().getResource("UI/ConfirmButton", ResourceType.RESOURCE_TYPE_TEXTURE_REGION);
        // Create the confirm button
        confirmButton = new UIButton(texture);
        // Setup the button
        Vector2 buttonSize = new Vector2(size.y / 4f, size.y / 4f);
        confirmButton.setSize(buttonSize);
        confirmButton.setPosition(new Vector2(size.x - buttonSize.x * 1.25f, buttonSize.y * 0.25f));
        // Add it to the window
        addWidget(confirmButton);
    }

    public void show(String item, String price, boolean canBuy)
    {
        // Clear all texts
        cleanTexts();
        // Add the item type text
        addSmallText("Item: " + item, new Vector2(getSize().x / 10f, getSize().y * 0.5f));
        addSmallText("Price: " + price + " Coins", new Vector2(getSize().x / 10f, getSize().y * 0.35f));
        // Enable/Disable the confirm button accordingly
        confirmButton.setEnabled(canBuy);
        // Toggle the window on
        toggle(true);
    }

    public boolean isPurchaseConfirmed() { return confirmButton.isClicked(); }
}
