package josm.client;

import josm.client.CChannel;
import josm.client.CTune;

public class CPattern {
    private int offset;
    public int[][] sound = new int[16][64];
    public int[][] note = new int[16][64];
    public int[][] effect = new int[16][64];
    public int[][] effectValue = new int[16][64];
    private final CTune tune;
    private static int[] clear;
    private static int[] mixBuf;

    public CPattern(CTune tune) {
        this.tune = tune;
    }

    int play(short[] buffer, int index, int channelsUsed, CChannel[] channels, int samplesPerTick, boolean yield) {
        if (clear == null) {
            clear = new int[samplesPerTick];
            mixBuf = new int[samplesPerTick];
        }
        boolean done = false;
        boolean a = false;
        while (!done) {
            int i;
            for (i = 0; i < channelsUsed; ++i) {
                done |= channels[i].playNext(mixBuf, this.sound[i], this.note[i], this.effect[i], this.effectValue[i]);
                if (!yield) continue;
                Thread.yield();
            }
            for (i = 0; i < this.tune.samplesPerTick; ++i) {
                buffer[index++] = (short)(mixBuf[i] / channelsUsed);
            }
            System.arraycopy(clear, 0, mixBuf, 0, samplesPerTick);
        }
        return index;
    }
}

