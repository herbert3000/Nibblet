package josm.client;

import java.io.DataInputStream;
import java.io.IOException;
import josm.client.CSoundSampled;
import josm.client.CSoundSynth;

public abstract class CSound {
    protected String name;

    abstract void reset(int var1);

    CSound(String name) {
        this.name = name;
    }

    abstract CSound cloneSound();

    static CSound load(DataInputStream dis) throws IOException {
        int type = dis.readInt();
        if (type == 0) {
            return null;
        }
        if (type == 1) {
            return CSoundSampled.loadSampled(dis);
        }
        return CSoundSynth.loadSynth(dis);
    }

    abstract int nextSample(int var1, double var2, double var4);
}

