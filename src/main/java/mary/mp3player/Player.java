package mary.mp3player;

import mary.mp3player.Track;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class Player {

    MainApp mainApp;
    static Track currentTrack;
    static Media media;
    static MediaPlayer mediaPlayer = null;
    Status status;

    public static Duration getTrackDuration() {
        if (media != null)
            return media.getDuration();
        else
            return null;
    }

    public static Duration getCurrentTime() {
        if (mediaPlayer != null)
            return mediaPlayer.getCurrentTime();
        else
            return null;
    }

    public static String getStatusString() {
        if (mediaPlayer != null)
            return mediaPlayer.getStatus().toString();
        else
            return "UNKNOWN";
    }

    public static void playPause(Track track) {
        if (mediaPlayer == null || media == null || track != currentTrack) {
            currentTrack = track;
            media = new Media(new File(track.getPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setOnReady(new Runnable() {
                public void run() {
                    //TODO with controller
                    //TODO without Duration class (for example double)
                    MainApp.updateValues();
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    MainApp.updateValues();
                }
            });
        }
        if (mediaPlayer.getStatus() == Status.PLAYING)
            mediaPlayer.pause();
        else
            mediaPlayer.play();
    }

    static void stop() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    static void seek(Double value) {
        if (mediaPlayer != null)
            mediaPlayer.seek(media.getDuration().multiply(value / 100.0));
    }

    static void setVolume(Double value) {
        if (mediaPlayer != null)
            mediaPlayer.setVolume(value / 100.0);
    }

}
