package com.ormisiclapps.slickyfuton.audio;

import com.badlogic.gdx.audio.Music;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;

/**
 * Created by Anis on 1/22/2017.
 */

public class GameMusic
{
    private Music music;

    public GameMusic(String name)
    {
        // Get the music
        music = Core.getInstance().getResourcesManager().getResource(name, ResourceType.RESOURCE_TYPE_MUSIC);
    }

    public void dispose()
    {
        // Manual disposing
        // NOTE: Use this ONLY when you need to dispose of the music immediately, otherwise leave it for the
        // ResourcesManager class to take care of it
        music.dispose();
    }

    public void play(boolean looped, float volume)
    {
        // Set the music volume
        music.setVolume(volume);
        // Set the looped flag
        music.setLooping(looped);
        // Play the music
        music.play();
    }

    public boolean isPlaying()
    {
        return music.isPlaying();
    }

    public void pause()
    {
        music.pause();
    }

    public void resume()
    {
        music.play();
    }

    public void stop()
    {
        music.stop();
    }

    public void setPosition(float position) { music.setPosition(position); }

    public float getPosition() { return music.getPosition(); }

    public float getVolume() { return music.getVolume(); }

    public void setVolume(float volume) { music.setVolume(volume < 0f ? 0f: volume); }
}
