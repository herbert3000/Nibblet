package josm.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import josm.ICTune;
import josm.client.CChannel;
import josm.client.CPattern;
import josm.client.CSound;
import josm.client.ICPlayer;

public class CTune implements ICTune {

    private ICPlayer player;
    private static final int MAX_PATTERNS = 64;
    private static final int MAX_CHANNELS = 8;
    CPattern[] patterns = new CPattern[64];
    CChannel[] channels = new CChannel[8];
    CSound[][] sounds = new CSound[8][64];
    int numPattUsed = 0;
    int[] songOrder = new int[128];
    int numChannelsUsed = 6;
    static double mixFreq = 8000.0;
    int speed = 6;
    int samplesPerTick = (int)(mixFreq / (double)this.speed);
    private String lastError = "";
    private String audioPlayer = "Not initialized";
    private int moduleSize = 0;
    private int samplingSize = 0;

    public void stop() {
        if (this.player != null) {
            this.player.stop();
        }
    }

    public boolean generate() {
        return this.generate(true);
    }

    public boolean generate(boolean yield) {
        this.cloneSounds();
        this.samplesPerTick = (int)(mixFreq / (double)this.speed);
        int maxLen = this.numPattUsed * 64 * this.samplesPerTick;
        short[] pcm16 = new short[maxLen];
        int len = 0;
        for (int i = 0; i < this.numPattUsed; ++i) {
            len = this.patterns[this.songOrder[i]].play(pcm16, len, this.numChannelsUsed, this.channels, this.samplesPerTick, yield);
        }
        if (len < maxLen) {
            short[] s = new short[len];
            System.arraycopy(pcm16, 0, s, 0, len);
            pcm16 = s;
        }
        this.samplingSize = len;
        try {
            Class<?> c = Class.forName("josm.client.CJSPlayer");
            this.player = (ICPlayer)c.newInstance();
            this.player.prepare(pcm16);
            this.audioPlayer = "javax.sound.sampled.AudioSystem";
            return true;
        }
        catch (Throwable e) {
            try {
                Class<?> c = Class.forName("josm.client.CSUPlayer");
                this.player = (ICPlayer)c.newInstance();
                this.player.prepare(pcm16);
                this.audioPlayer = "sun.audio.AudioPlayer";
                return true;
            }
            catch (Throwable e2) {
                this.lastError = "Unable to create/allocate sound device (javax.sound or sun.audio)";
                return false;
            }
        }
    }

    public CTune() {
        int i;
        for (i = 0; i < 64; ++i) {
            this.patterns[i] = new CPattern(this);
        }
        for (i = 0; i < 8; ++i) {
            this.channels[i] = new CChannel(this, this.sounds[i]);
        }
    }

    public int getModuleSize() {
        return this.moduleSize;
    }

    public void play() {
        if (this.player != null) {
            this.player.play();
        }
    }

    public boolean load(byte[] josmModule) {
        this.lastError = "";
        try {
            ByteArrayInputStream ins = new ByteArrayInputStream(josmModule);
            DataInputStream dis = new DataInputStream(ins);
            this.load(dis);
            dis.close();
            this.moduleSize = josmModule.length;
        }
        catch (Exception e) {
            this.lastError = e.toString();
            return false;
        }
        return true;
    }

    public boolean load(URL josmModuleURL) {
        this.lastError = "";
        try {
            URLConnection urlCon = josmModuleURL.openConnection();
            urlCon.connect();
            BufferedInputStream bis = new BufferedInputStream(urlCon.getInputStream());
            int len = urlCon.getContentLength();
            byte[] b = new byte[len];
            int read = 0;
            for (int tot = 0; read >= 0 && tot < len; tot += read) {
                read = bis.read(b, tot, len - tot);
            }
            return this.load(b);
        }
        catch (Exception e) {
            this.lastError = e.toString();
            return false;
        }
    }

    public boolean load(File f) {
        this.lastError = "";
        try {
            int len = (int)f.length();
            byte[] b = new byte[len];
            FileInputStream fis = new FileInputStream(f);
            fis.read(b, 0, b.length);
            return this.load(b);
        }
        catch (Exception e) {
            this.lastError = e.toString();
            return false;
        }
    }

    private void load(DataInputStream dis) throws Exception {
        int i;
        dis.readInt();
        this.speed = dis.readInt();
        this.numPattUsed = dis.readInt();
        int highestPattern = 0;
        for (i = 0; i < this.numPattUsed; ++i) {
            this.songOrder[i] = dis.readInt();
            if (this.songOrder[i] <= highestPattern) continue;
            highestPattern = this.songOrder[i];
        }
        for (i = 0; i < highestPattern + 1; ++i) {
            CPattern pat = this.patterns[i];
            for (int j = 0; j < this.numChannelsUsed; ++j) {
                int nrPack;
                for (int p = 0; p < 64; p += nrPack) {
                    int v = dis.readInt();
                    nrPack = (v >>> 25) + 1;
                    int _sound = v >> 19 & 0x3F;
                    int _note = v >> 12 & 0x7F;
                    int _effect = v >> 8 & 0xF;
                    int _effVal = v & 0xFF;
                    for (int pp = 0; pp < nrPack; ++pp) {
                        pat.sound[j][p + pp] = _sound;
                        pat.note[j][p + pp] = _note;
                        pat.effect[j][p + pp] = _effect;
                        pat.effectValue[j][p + pp] = _effVal;
                    }
                }
            }
        }
        for (i = 0; i < 64; ++i) {
            this.sounds[0][i] = CSound.load(dis);
        }
    }

    public void loop() {
        if (this.player != null) {
            this.player.loop();
        }
    }

    public int getSamplingSize() {
        return this.samplingSize;
    }

    public String getAudioPlayer() {
        return this.audioPlayer;
    }

    private void cloneSounds() {
        for (int j = 0; j < 64; ++j) {
            int i;
            if (this.sounds[0][j] == null) {
                for (i = 1; i < 8; ++i) {
                    this.sounds[i][j] = null;
                }
                continue;
            }
            for (i = 1; i < 8; ++i) {
                this.sounds[i][j] = this.sounds[0][j].cloneSound();
            }
        }
    }

    public String getLastError() {
        return this.lastError;
    }
}

