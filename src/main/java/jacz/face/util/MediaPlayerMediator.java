package jacz.face.util;

import jacz.face.state.PropertiesAccessor;

import java.io.IOException;

/**
 * Created by alberto on 7/6/16.
 */
public class MediaPlayerMediator {

    public enum MediaPlayer {
        VLC
    }

    public static void play(MediaPlayer mediaPlayer, String videoPath, String subtitlePath) throws IOException {
        switch (mediaPlayer) {
            case VLC:
                String vlcPath = PropertiesAccessor.getInstance().getMediaPlayerProperties().getVLCPath();
                StringBuilder command = new StringBuilder(vlcPath).append(" ").append(videoPath);
                if (subtitlePath != null) {
                    command.append(" --sub-file=").append(subtitlePath);
                }
                Runtime.getRuntime().exec(command.toString());
        }
    }
}
