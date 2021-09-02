package josm.client;

import java.io.DataInputStream;
import java.io.IOException;
import josm.client.CSound;

public class CSoundSampled
extends CSound {
    private final byte[] buffer;
    private final int length;
    private int offset = 0;

    final void reset(int dontCare) {
        this.offset = 0;
    }

    static final CSoundSampled loadSampled(DataInputStream dis) throws IOException {
        String _name = dis.readUTF();
        int len = dis.readInt();
        byte[] b = new byte[len];
        dis.read(b, 0, len);
        return new CSoundSampled(_name, b);
    }

    CSoundSampled(String name, byte[] rawpcm) {
        super(name);
        this.buffer = rawpcm;
        this.length = this.buffer.length;
    }

    CSound cloneSound() {
        return new CSoundSampled(this.name, this.buffer);
    }

    final int nextSample(int dontCare, double doNotCare, double vol) {
        if (this.offset >= this.length) {
            return 0;
        }
        return (int)((double)this.buffer[this.offset++] * vol * 256.0);
    }
}

