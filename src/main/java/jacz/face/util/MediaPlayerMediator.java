package jacz.face.util;

import jacz.face.state.PropertiesAccessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Mediator for launching media player processes
 */
public class MediaPlayerMediator {

    public enum MediaPlayer {
        VLC
    }

    public static void play(MediaPlayer mediaPlayer, String videoPath, String subtitlePath) throws IOException {
        switch (mediaPlayer) {
            case VLC:
                String vlcPath = PropertiesAccessor.getInstance().getMediaPlayerProperties().getVLCPath();
                List<String> args = new ArrayList<>();
                args.add(vlcPath);
                args.add(videoPath);
                if (subtitlePath != null) {
                    args.add("--sub-file=" + subtitlePath);
                }
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                processBuilder.start();
        }
    }
}
