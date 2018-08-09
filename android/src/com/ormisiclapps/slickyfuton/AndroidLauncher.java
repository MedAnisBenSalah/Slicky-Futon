package com.ormisiclapps.slickyfuton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appodeal.gdx.GdxAppodeal;
import com.appodeal.gdx.callbacks.RewardedVideoCallback;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ormisiclapps.slickyfuton.core.GameLauncher;
import com.ormisiclapps.slickyfuton.os.OSUtility;
import com.ormisiclapps.slickyfuton.os.RewardedVideoListener;

public class AndroidLauncher extends AndroidApplication implements OSUtility
{
	// BaseGameUtils
	private GameHelper gameHelper;
	private final static int requestCode = 1;

    private GameHelper.GameHelperListener gameHelperListener;

    // Main layout that's used to contain all views (game and ads)
    private RelativeLayout mainLayout;

    // Views
    private View gameView;

    // Dialogs
    private Dialog ratingDialog;

    // Listeners
    private RatingListener ratingListener;
    private RewardedVideoListener rewardedVideoListener;

	@Override
	protected void onCreate (Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
        // Create the game helper instance
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        // Disable debug log
		gameHelper.enableDebugLog(false);
        // Set the game helper listener
		gameHelperListener = new GameHelper.GameHelperListener()
		{
			@Override
			public void onSignInFailed()
            {

            }

			@Override
			public void onSignInSucceeded()
            {

            }
		};
		// Reset listeners
        ratingListener = null;
        rewardedVideoListener = null;
		// Disable connect on start
        gameHelper.setConnectOnStart(false);
        // Setup game helper
		gameHelper.setup(gameHelperListener);
        // Initialize FireBase analytics
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        // Create the main layout
        mainLayout = new RelativeLayout(this);
        // Create a layout params instance
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        // Set the main layout's params
        mainLayout.setLayoutParams(params);
        // Setup views
        createGameView();
        // Setup dialogs
        createRatingDialog();
        // Add views to the main layout
        mainLayout.addView(gameView);
        // Set it to use this view
        setContentView(mainLayout);
        // Initialize the Apoodeal SDK
        GdxAppodeal.confirm(GdxAppodeal.SKIPPABLE_VIDEO);
        GdxAppodeal.initialize(getString(R.string.appodeal_apikey), GdxAppodeal.INTERSTITIAL | GdxAppodeal.NON_SKIPPABLE_VIDEO | GdxAppodeal.BANNER |
                GdxAppodeal.REWARDED_VIDEO | GdxAppodeal.SKIPPABLE_VIDEO);

        // Set the rewarded video listener
        GdxAppodeal.setRewardedVideoCallbacks(new RewardedVideoCallback() {
            @Override
            public void onRewardedVideoLoaded() {

            }

            @Override
            public void onRewardedVideoFailedToLoad() {

            }

            @Override
            public void onRewardedVideoShown() {

            }

            @Override
            public void onRewardedVideoFinished(int amount, String name)
            {
                // Call the listener
                if(rewardedVideoListener != null)
                    rewardedVideoListener.onRewarded();
            }

            @Override
            public void onRewardedVideoClosed() {

            }
        });
	}

    @Override
    protected void attachBaseContext(Context context)
    {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

	/*
	    Create and setup the libgdx's view
	*/
	private void createGameView()
    {
        // Create the application config instance
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        // 2 is a good value for average devices (Anti-aliasing)
        config.numSamples = 2;
        // Create the game's view
        gameView = initializeForView(new GameLauncher(this), config);
    }

    private void createRatingDialog()
    {
        // Create the rating dialog instance
        ratingDialog = new Dialog(AndroidLauncher.this);
        // Setup the dialog
        ratingDialog.setCanceledOnTouchOutside(true);
        ratingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ratingDialog.setContentView(R.layout.rating_dialog);
        ratingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // Set the dialog's width and height
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(ratingDialog.getWindow().getAttributes());
        layoutParams.width = screenSize.x / 4 * 3;
        layoutParams.height = screenSize.y / 3 * 2;
        System.out.println("withata: " + screenSize.x / 4 * 3 + " h: " + screenSize.y / 3 * 2);
        ratingDialog.getWindow().setAttributes(layoutParams);
        // Load the roboto bold font
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        final TextView rateText = (TextView)ratingDialog.findViewById(R.id.rateText);
        final Button neverButton = (Button)ratingDialog.findViewById(R.id.rateNeverButton);
        final Button notNowButton = (Button)ratingDialog.findViewById(R.id.rateNotNowButton);
        final Button rateButton = (Button)ratingDialog.findViewById(R.id.rateButton);
        // Set it for the dialog's components
        rateText.setTypeface(font);
        neverButton.setTypeface(font);
        notNowButton.setTypeface(font);
        rateButton.setTypeface(font);
        // Set button's listeners
        neverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratingListener != null)
                    ratingListener.onDismissed(true);

                ratingDialog.hide();
            }
        });

        notNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ratingListener != null)
                    ratingListener.onDismissed(false);

                ratingDialog.hide();
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // If the rating is positive then open the store
                if(((RatingBar)ratingDialog.findViewById(R.id.ratingBar)).getRating() >= 4)
                {
                    rateGame();
                    if(ratingListener != null)
                        ratingListener.onDismissed(true);

                    ratingDialog.hide();
                }
                else
                {
                    // Setup the thank you message
                    rateText.setText("Thank you for rating the game");
                    neverButton.setVisibility(View.GONE);
                    notNowButton.setVisibility(View.GONE);
                    rateButton.setText("OK");
                    rateButton.setGravity(Gravity.RIGHT);
                    ratingDialog.findViewById(R.id.ratingBar).setEnabled(false);
                    rateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(ratingListener != null)
                                ratingListener.onDismissed(true);

                            ratingDialog.hide();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        gameHelper.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn()
    {
        try
        {
            // Setup game helper
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    gameHelper.beginUserInitiatedSignIn();
                }
            });
        }
        catch (Exception e)
        {
            //Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void signOut()
    {
        try
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    gameHelper.signOut();
                }
            });
        }
        catch (Exception e)
        {
            //Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
        }
    }

    @Override
    public void rateGame()
    {
        // Open the play store page
        String str = "market://details?id=com.ormisiclapps.slickyfuton";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
    }

    @Override
    public void submitScore(long highScore)
    {
        // Only submit score if we're signed in
        if(isSignedIn())
            Games.Leaderboards.submitScore(gameHelper.getApiClient(),
                    getString(com.ormisiclapps.slickyfuton.R.string.leaderboard_slicky_futon), highScore);
    }

    @Override
    public void showLeaderboards()
    {
        // Make sure we're signed in already
        if(isSignedIn())
            // Set the leaderboards view
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
                    getString(com.ormisiclapps.slickyfuton.R.string.leaderboard_slicky_futon)), requestCode);
        else
            // NOTE: We force sign in since a player who wants to view the scoreboard must approve of Google Play services
            signIn();
    }

    @Override
    public void showAchievements()
    {
        // Make sure we're signed in already
        if(isSignedIn())
            // Set the leaderboards view
            startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
        else
            // NOTE: We force sign in since a player who wants to view the achievements must approve of Google Play services
            signIn();
    }

    @Override
    public void unlockAchievement(Achievement achievement)
    {
        // Check sign in state
        if(!isSignedIn())
            return;

        switch (achievement)
        {
            case ACHIEVEMENT_FIRST_GAME:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQAg");
                break;

            case ACHIEVEMENT_10_GAMES:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQAw");
                break;

            case ACHIEVEMENT_100_GAMES:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQBA");
                break;

            case ACHIEVEMENT_1000_GAMES:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQBQ");
                break;

            case ACHIEVEMENT_10_SCORE:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQBg");
                break;

            case ACHIEVEMENT_20_SCORE:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQBw");
                break;

            case ACHIEVEMENT_40_SCORE:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQCA");
                break;

            case ACHIEVEMENT_50_SCORE:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQCQ");
                break;

            case ACHIEVEMENT_100_SCORE:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQCg");
                break;

            case ACHIEVEMENT_200_SCORE:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQCw");
                break;

            case ACHIEVEMENT_JUST_STARTING_LEVEL:
                Games.Achievements.unlock(gameHelper.getApiClient(), "CgkInIG__usVEAIQDA");
                break;
        }
    }

    @Override
    public boolean isSignedIn()
    {
        return isNetworkConnected() && gameHelper.isSignedIn();
    }

    @Override
    public void showBannerAd()
    {
        GdxAppodeal.show(GdxAppodeal.BANNER);
    }

    @Override
    public void hideBannerAd()
    {
        GdxAppodeal.hide(GdxAppodeal.BANNER);
    }

    @Override
    public boolean showInterstitialAd()
    {
        boolean success = GdxAppodeal.isLoaded(GdxAppodeal.INTERSTITIAL);
        if(success)
            GdxAppodeal.show(GdxAppodeal.INTERSTITIAL);

        return success;
    }

    @Override
    public void showSkippableVideoAd()
    {
        if(GdxAppodeal.isLoaded(GdxAppodeal.SKIPPABLE_VIDEO))
            GdxAppodeal.show(GdxAppodeal.SKIPPABLE_VIDEO);
    }

    @Override
    public void showRewardedVideoAd(RewardedVideoListener listener)
    {
        // Set the listener
        rewardedVideoListener = listener;
        // Show rewarded video
        if(GdxAppodeal.isLoaded(GdxAppodeal.REWARDED_VIDEO))
            GdxAppodeal.show(GdxAppodeal.REWARDED_VIDEO);
    }

    @Override
    public boolean isRewardedVideoAdLoaded()
    {
        return GdxAppodeal.isLoaded(GdxAppodeal.REWARDED_VIDEO);
    }

    @Override
    public boolean isSkippableVideoAdLoaded()
    {
        return GdxAppodeal.isLoaded(GdxAppodeal.SKIPPABLE_VIDEO);
    }

    @Override
    public boolean isNetworkConnected()
    {
        // Check the device's connectivity
        return ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    public void showRateDialog(RatingListener listener)
    {
        // Set the rating listener
        ratingListener = listener;
        // Show the rating dialog
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Show the rating dialog
                ratingDialog.show();
            }
        });
    }
}
