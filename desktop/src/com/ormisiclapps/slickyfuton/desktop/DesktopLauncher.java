package com.ormisiclapps.slickyfuton.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ormisiclapps.slickyfuton.core.GameLauncher;
import com.ormisiclapps.slickyfuton.os.OSUtility;
import com.ormisiclapps.slickyfuton.os.RewardedVideoListener;

public class DesktopLauncher implements OSUtility
{
	public static void main (String[] arg)
	{
        // Create the configuration instance
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// Its okay to have a higher samples value on desktop
		config.samples = 10;
		// Edit these values if necessary
		/*config.width = 1366;
		config.height = 768;
		config.fullscreen = true;*/

		config.width = 854;
		config.height = 480;
        // Start the application
		new LwjglApplication(new GameLauncher(new DesktopLauncher()), config);
	}

	@Override
	public void signIn() {

	}

	@Override
	public void signOut() {

	}

	@Override
	public boolean isSignedIn() {
		return false;
	}

	@Override
	public void rateGame() {

	}

	@Override
	public void submitScore(long highScore) {

	}

	@Override
	public void showLeaderboards() {

	}

	@Override
	public void showAchievements() {

	}

	@Override
	public void unlockAchievement(Achievement achievement) {

	}

	@Override
	public void showBannerAd() {

	}

	@Override
	public void hideBannerAd() {

	}

	@Override
	public boolean showInterstitialAd() {
		return false;
	}

	@Override
	public void showSkippableVideoAd() {

	}

	@Override
	public void showRewardedVideoAd(RewardedVideoListener listener) {

	}

	@Override
	public boolean isRewardedVideoAdLoaded() {
		return false;
	}

	@Override
	public boolean isSkippableVideoAdLoaded() {
		return false;
	}

	@Override
	public boolean isNetworkConnected() {
		return false;
	}

	@Override
	public void showRateDialog(RatingListener listener) {

	}
}
