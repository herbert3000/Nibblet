package josm.client;

import java.io.DataInputStream;
import java.io.IOException;
import josm.client.CSound;
import josm.client.CTune;

public class CSoundSynth
extends CSound {
    private final double vol;
    private final double time;
    private final double A;
    private final double D;
    private final double S;
    private final double R;
    private final double fSin;
    private final double tSin;
    private final double fSaw;
    private final double tSaw;
    private final double fSqr;
    private final double tSqr;
    private final double fNse;
    private final double tNse;
    private final double DF;
    private final double RF;
    private final double atAdd;
    private double at = 0.0;
    private double waveF = 0.0;
    private int note = 0;
    private int lastNote = 0;
    private double freq = 0.0;
    private final double mixFreq = CTune.mixFreq;
    private static final double[] freqTable = new double[]{440.0, 446.16, 493.88, 523.25, 554.37, 587.33, 622.25, 659.25, 689.46, 739.99, 783.99, 830.61};
    private static final double[] octFacts = new double[]{0.0625, 0.125, 0.25, 0.5, 1.0, 2.0, 4.0, 8.0};

    final void reset(int note) {
        this.note = note;
        this.lastNote = note;
        this.freq = this.getFreq(note);
        this.at = 0.0;
    }

    CSoundSynth(String name, double volume, double stime, double a, double d, double s, double r, double sinFrom, double sinTo, double sawFrom, double sawTo, double sqrFrom, double sqrTo, double nseFrom, double nseTo) {
        super(name);
        this.time = stime;
        this.vol = volume;
        this.A = a;
        this.D = d;
        this.S = s;
        this.R = r;
        this.fSin = sinFrom;
        this.tSin = sinTo;
        this.fSaw = sawFrom;
        this.tSaw = sawTo;
        this.fSqr = sqrFrom;
        this.tSqr = sqrTo;
        this.fNse = nseFrom;
        this.tNse = nseTo;
        this.DF = (1.0 - this.S) / (this.D - this.A);
        this.RF = this.S / (1.0 - this.R);
        this.atAdd = 1.0 / (this.mixFreq * this.time);
    }

    CSound cloneSound() {
        return new CSoundSynth(this.name, this.vol, this.time, this.A, this.D, this.S, this.R, this.fSin, this.tSin, this.fSaw, this.tSaw, this.fSqr, this.tSqr, this.fNse, this.tNse);
    }

    static CSoundSynth loadSynth(DataInputStream dis) throws IOException {
        return new CSoundSynth(dis.readUTF(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readDouble());
    }

    private final double getFreq(int note) {
        if (note < 0 || note >= 96) {
            return 440.0;
        }
        return freqTable[note % 12] * octFacts[note / 12];
    }

    final int nextSample(int noteOffs, double freqOffs, double newVol) {
        if (this.at >= 1.0) {
            return 0;
        }
        if (this.lastNote != this.note + noteOffs) {
            this.freq = this.getFreq(this.note + noteOffs);
            this.lastNote = this.note + noteOffs;
        }
        double sound = 0.0;
        double sinP = this.fSin + (this.tSin - this.fSin) * this.at;
        double sawP = this.fSaw + (this.tSaw - this.fSaw) * this.at;
        double sqrP = this.fSqr + (this.tSqr - this.fSqr) * this.at;
        double nseP = this.fNse + (this.tNse - this.fNse) * this.at;
        if (sinP > 0.0) {
            double sineBase = Math.sin(this.waveF * 2.0 * Math.PI);
            sound += sineBase * sinP;
        }
        if (sawP > 0.0) {
            double sawBase = this.waveF < 0.5 ? this.waveF * 2.0 : -1.0 + (this.waveF - 0.5) * 2.0;
            sound += sawBase * sawP;
        }
        if (sqrP > 0.0) {
            double sqrBase = this.waveF < 0.5 ? 1 : -1;
            sound += sqrBase * sqrP;
        }
        if (nseP > 1.0E-5) {
            double nseBase = Math.random() * 2.0 - 1.0;
            sound += nseBase * nseP;
        }
        double env = 0.0;
        env = this.at < this.A ? this.at / this.A : (this.at < this.D ? 1.0 - (this.at - this.A) * this.DF : (this.at < this.R ? this.S : this.S - (this.at - this.R) * this.RF));
        int sample = (int)((sound *= env * this.vol * newVol) * 32767.0);
        if (sample > 32767) {
            sample = 32767;
        } else if (sample < -32767) {
            sample = -32767;
        }
        this.waveF += 1.0 / (this.mixFreq / (this.freq + freqOffs));
        while (this.waveF >= 1.0) {
            this.waveF -= 1.0;
        }
        this.at += this.atAdd;
        return sample;
    }
}

