package josm.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import josm.client.ICPlayer;

class CJSPlayer
implements ICPlayer {
    private Clip clip;
    private static /* synthetic */ Class class$javax$sound$sampled$Clip;

    public void stop() {
        this.clip.stop();
    }

    CJSPlayer() {
    }

    public boolean prepare(short[] pcm16) {
        int len = pcm16.length;
        byte[] b = new byte[len];
        int j = 0;
        for (int i = 0; i < len; ++i) {
            b[j++] = (byte)(pcm16[i] >> 8);
        }
        Line.Info li = new Line.Info(class$javax$sound$sampled$Clip != null ? class$javax$sound$sampled$Clip : (class$javax$sound$sampled$Clip = CJSPlayer.class$("javax.sound.sampled.Clip")));
        try {
            this.clip = (Clip)AudioSystem.getLine(li);
            AudioFormat af = new AudioFormat(8000.0f, 8, 1, true, true);
            this.clip.open(af, b, 0, b.length);
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void play() {
        this.clip.stop();
        this.clip.setFramePosition(0);
        this.clip.start();
    }

    private static /* synthetic */ Class class$(String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public void loop() {
        this.clip.stop();
        this.clip.setFramePosition(0);
        this.clip.loop(-1);
    }
}

