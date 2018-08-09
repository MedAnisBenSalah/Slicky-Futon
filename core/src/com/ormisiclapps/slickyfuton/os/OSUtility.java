package com.ormisiclapps.slickyfuton.os;

/**
 * Created by OrMisicL on 8/24/2017.
 */

/*
    This is used to handle OS specific features (scoreboard, achievements ...)
*/
public interface OSUtility
{
    enum Achievement
    {
        ACHIEVEMENT_FIRST_GAME,
        ACHIEVEMENT_10_GAMES,
        ACHIEVEMENT_100_GAMES,
        ACHIEVEMENT_1000_GAMES,
        ACHIEVEMENT_10_SCORE,
        ACHIEVEMENT_20_SCORE,
        ACHIEVEMENT_40_SCORE,
        ACHIEVEMENT_50_SCORE,
        ACHIEVEMENT_100_SCORE,
        ACHIEVEMENT_200_SCORE,
        ACHIEVEMENT_JUST_STARTING_LEVEL

    }

    interface RatingListener
    {
        void onDismissed(boolean never);
    }

    // Google Play services
    void signIn();
    void signOut();
    boolean isSignedIn();

    // Google play services features
    void rateGame();
    void submitScore(long highScore);
    void showLeaderboards();
    void showAchievements();
    void unlockAchievement(Achievement achievement);

    // Ad banner
    void showBannerAd();
    void hideBannerAd();

    // Ad Interstitial
    boolean showInterstitialAd();

    // Video ad
    void showSkippableVideoAd();
    void showRewardedVideoAd(RewardedVideoListener listener);
    boolean isRewardedVideoAdLoaded();
    boolean isSkippableVideoAdLoaded();

    // Utilities
    boolean isNetworkConnected();
    void showRateDialog(RatingListener listener);
}
