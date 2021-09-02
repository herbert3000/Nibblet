package josm.client;

import josm.client.CSound;
import josm.client.CTune;

public class CChannel {
    CTune tune;
    CSound[] snd;
    private final int arpDel = (int)(CTune.mixFreq / 50.0);
    private final int arpDel2 = this.arpDel * 2;
    private final int arpDelMax = this.arpDel * 3;
    private final int arpDelSlow = (int)(CTune.mixFreq / 20.0);
    private final int arpDelSlow2 = this.arpDelSlow * 2;
    private final int arpDelSlowMax = this.arpDelSlow * 3;
    private final double vibSpeed = 30.0 / CTune.mixFreq;
    private int offs;
    private int index = 0;
    private int efct = 0;
    private int efctVal = 0;
    private int arpCount = 0;
    private double vibCount = 0.0;
    private double vibAmp = 0.0;
    private double port = 0.0;
    private CSound current = null;
    private double vib = 0.0;
    private double vol = 1.0;
    int i = 0;

    public void reset() {
        this.i = 0;
        this.current = null;
    }

    public CChannel(CTune tune, CSound[] snd) {
        this.snd = snd;
        this.tune = tune;
    }

    public final boolean playNext(int[] buffer, int[] sound, int[] note, int[] effect, int[] effectValue) {
        boolean last = false;
        if (sound[this.i] > 0) {
            this.current = this.snd[sound[this.i] - 1];
            this.arpCount = 0;
            this.vol = 1.0;
            this.vib = 0.0;
            this.offs = 0;
            this.port = 0.0;
            if (this.current != null) {
                this.current.reset(note[this.i]);
            }
        }
        this.efct = effect[this.i];
        this.efctVal = effectValue[this.i];
        if (this.efct == 1) {
            this.arpCount %= this.arpDelMax;
        } else if (this.efct == 2) {
            this.vibAmp = this.efctVal;
        } else if (this.efct == 3) {
            this.vol = (double)this.efctVal / 255.0;
        } else if (this.efct == 4) {
            this.port = (double)this.efctVal / 1000.0;
        } else if (this.efct == 5) {
            this.port = (double)(-this.efctVal) / 1000.0;
        } else if (this.efct == 6) {
            this.arpCount %= this.arpDelSlowMax;
        }
        if (this.current == null) {
            this.index += this.tune.samplesPerTick;
        } else {
            int j = 0;
            while (j < this.tune.samplesPerTick) {
                if (this.efct == 1) {
                    if (this.arpCount == this.arpDel) {
                        this.offs = this.efctVal % 16;
                    } else if (this.arpCount == this.arpDel2) {
                        this.offs = this.efctVal / 16;
                    } else if (this.arpCount == this.arpDelMax) {
                        this.offs = 0;
                        this.arpCount = 0;
                    }
                    ++this.arpCount;
                } else if (this.efct == 2) {
                    this.vib = this.vibAmp * Math.sin(this.vibCount);
                    this.vibCount += this.vibSpeed;
                    while (this.vibCount > Math.PI * 2) {
                        this.vibCount -= Math.PI * 2;
                    }
                } else if (this.efct == 4 || this.efct == 5) {
                    this.vib += this.port;
                } else if (this.efct == 6) {
                    if (this.arpCount == this.arpDelSlow) {
                        this.offs = this.efctVal % 16;
                    } else if (this.arpCount == this.arpDelSlow2) {
                        this.offs = this.efctVal / 16;
                    } else if (this.arpCount == this.arpDelSlowMax) {
                        this.offs = 0;
                        this.arpCount = 0;
                    }
                    ++this.arpCount;
                }
                int n = j++;
                buffer[n] = buffer[n] + this.current.nextSample(this.offs, this.vib, this.vol);
                ++this.index;
            }
        }
        ++this.i;
        if (this.i == 64) {
            last = true;
        }
        if (last) {
            this.i = 0;
        }
        return last;
    }
}

