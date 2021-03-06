package josm.client;

import java.io.InputStream;
import josm.client.ICPlayer;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.ContinuousAudioDataStream;

public class CSUPlayer
implements ICPlayer {
    private AudioData audioData;
    private ContinuousAudioDataStream continuosAudioDataStream;
    private AudioDataStream audioDataStream;
    private final int[] ai;

    public void stop() {
        AudioPlayer.player.stop((InputStream)this.audioDataStream);
        AudioPlayer.player.stop((InputStream)this.continuosAudioDataStream);
    }

    public CSUPlayer() {
        int[] arrn = new int[256];
        arrn[2] = 1;
        arrn[3] = 1;
        arrn[4] = 2;
        arrn[5] = 2;
        arrn[6] = 2;
        arrn[7] = 2;
        arrn[8] = 3;
        arrn[9] = 3;
        arrn[10] = 3;
        arrn[11] = 3;
        arrn[12] = 3;
        arrn[13] = 3;
        arrn[14] = 3;
        arrn[15] = 3;
        arrn[16] = 4;
        arrn[17] = 4;
        arrn[18] = 4;
        arrn[19] = 4;
        arrn[20] = 4;
        arrn[21] = 4;
        arrn[22] = 4;
        arrn[23] = 4;
        arrn[24] = 4;
        arrn[25] = 4;
        arrn[26] = 4;
        arrn[27] = 4;
        arrn[28] = 4;
        arrn[29] = 4;
        arrn[30] = 4;
        arrn[31] = 4;
        arrn[32] = 5;
        arrn[33] = 5;
        arrn[34] = 5;
        arrn[35] = 5;
        arrn[36] = 5;
        arrn[37] = 5;
        arrn[38] = 5;
        arrn[39] = 5;
        arrn[40] = 5;
        arrn[41] = 5;
        arrn[42] = 5;
        arrn[43] = 5;
        arrn[44] = 5;
        arrn[45] = 5;
        arrn[46] = 5;
        arrn[47] = 5;
        arrn[48] = 5;
        arrn[49] = 5;
        arrn[50] = 5;
        arrn[51] = 5;
        arrn[52] = 5;
        arrn[53] = 5;
        arrn[54] = 5;
        arrn[55] = 5;
        arrn[56] = 5;
        arrn[57] = 5;
        arrn[58] = 5;
        arrn[59] = 5;
        arrn[60] = 5;
        arrn[61] = 5;
        arrn[62] = 5;
        arrn[63] = 5;
        arrn[64] = 6;
        arrn[65] = 6;
        arrn[66] = 6;
        arrn[67] = 6;
        arrn[68] = 6;
        arrn[69] = 6;
        arrn[70] = 6;
        arrn[71] = 6;
        arrn[72] = 6;
        arrn[73] = 6;
        arrn[74] = 6;
        arrn[75] = 6;
        arrn[76] = 6;
        arrn[77] = 6;
        arrn[78] = 6;
        arrn[79] = 6;
        arrn[80] = 6;
        arrn[81] = 6;
        arrn[82] = 6;
        arrn[83] = 6;
        arrn[84] = 6;
        arrn[85] = 6;
        arrn[86] = 6;
        arrn[87] = 6;
        arrn[88] = 6;
        arrn[89] = 6;
        arrn[90] = 6;
        arrn[91] = 6;
        arrn[92] = 6;
        arrn[93] = 6;
        arrn[94] = 6;
        arrn[95] = 6;
        arrn[96] = 6;
        arrn[97] = 6;
        arrn[98] = 6;
        arrn[99] = 6;
        arrn[100] = 6;
        arrn[101] = 6;
        arrn[102] = 6;
        arrn[103] = 6;
        arrn[104] = 6;
        arrn[105] = 6;
        arrn[106] = 6;
        arrn[107] = 6;
        arrn[108] = 6;
        arrn[109] = 6;
        arrn[110] = 6;
        arrn[111] = 6;
        arrn[112] = 6;
        arrn[113] = 6;
        arrn[114] = 6;
        arrn[115] = 6;
        arrn[116] = 6;
        arrn[117] = 6;
        arrn[118] = 6;
        arrn[119] = 6;
        arrn[120] = 6;
        arrn[121] = 6;
        arrn[122] = 6;
        arrn[123] = 6;
        arrn[124] = 6;
        arrn[125] = 6;
        arrn[126] = 6;
        arrn[127] = 6;
        arrn[128] = 7;
        arrn[129] = 7;
        arrn[130] = 7;
        arrn[131] = 7;
        arrn[132] = 7;
        arrn[133] = 7;
        arrn[134] = 7;
        arrn[135] = 7;
        arrn[136] = 7;
        arrn[137] = 7;
        arrn[138] = 7;
        arrn[139] = 7;
        arrn[140] = 7;
        arrn[141] = 7;
        arrn[142] = 7;
        arrn[143] = 7;
        arrn[144] = 7;
        arrn[145] = 7;
        arrn[146] = 7;
        arrn[147] = 7;
        arrn[148] = 7;
        arrn[149] = 7;
        arrn[150] = 7;
        arrn[151] = 7;
        arrn[152] = 7;
        arrn[153] = 7;
        arrn[154] = 7;
        arrn[155] = 7;
        arrn[156] = 7;
        arrn[157] = 7;
        arrn[158] = 7;
        arrn[159] = 7;
        arrn[160] = 7;
        arrn[161] = 7;
        arrn[162] = 7;
        arrn[163] = 7;
        arrn[164] = 7;
        arrn[165] = 7;
        arrn[166] = 7;
        arrn[167] = 7;
        arrn[168] = 7;
        arrn[169] = 7;
        arrn[170] = 7;
        arrn[171] = 7;
        arrn[172] = 7;
        arrn[173] = 7;
        arrn[174] = 7;
        arrn[175] = 7;
        arrn[176] = 7;
        arrn[177] = 7;
        arrn[178] = 7;
        arrn[179] = 7;
        arrn[180] = 7;
        arrn[181] = 7;
        arrn[182] = 7;
        arrn[183] = 7;
        arrn[184] = 7;
        arrn[185] = 7;
        arrn[186] = 7;
        arrn[187] = 7;
        arrn[188] = 7;
        arrn[189] = 7;
        arrn[190] = 7;
        arrn[191] = 7;
        arrn[192] = 7;
        arrn[193] = 7;
        arrn[194] = 7;
        arrn[195] = 7;
        arrn[196] = 7;
        arrn[197] = 7;
        arrn[198] = 7;
        arrn[199] = 7;
        arrn[200] = 7;
        arrn[201] = 7;
        arrn[202] = 7;
        arrn[203] = 7;
        arrn[204] = 7;
        arrn[205] = 7;
        arrn[206] = 7;
        arrn[207] = 7;
        arrn[208] = 7;
        arrn[209] = 7;
        arrn[210] = 7;
        arrn[211] = 7;
        arrn[212] = 7;
        arrn[213] = 7;
        arrn[214] = 7;
        arrn[215] = 7;
        arrn[216] = 7;
        arrn[217] = 7;
        arrn[218] = 7;
        arrn[219] = 7;
        arrn[220] = 7;
        arrn[221] = 7;
        arrn[222] = 7;
        arrn[223] = 7;
        arrn[224] = 7;
        arrn[225] = 7;
        arrn[226] = 7;
        arrn[227] = 7;
        arrn[228] = 7;
        arrn[229] = 7;
        arrn[230] = 7;
        arrn[231] = 7;
        arrn[232] = 7;
        arrn[233] = 7;
        arrn[234] = 7;
        arrn[235] = 7;
        arrn[236] = 7;
        arrn[237] = 7;
        arrn[238] = 7;
        arrn[239] = 7;
        arrn[240] = 7;
        arrn[241] = 7;
        arrn[242] = 7;
        arrn[243] = 7;
        arrn[244] = 7;
        arrn[245] = 7;
        arrn[246] = 7;
        arrn[247] = 7;
        arrn[248] = 7;
        arrn[249] = 7;
        arrn[250] = 7;
        arrn[251] = 7;
        arrn[252] = 7;
        arrn[253] = 7;
        arrn[254] = 7;
        arrn[255] = 7;
        this.ai = arrn;
    }

    public boolean prepare(short[] pcm16) {
        byte[] b = this.linear2uLaw(pcm16);
        this.audioData = new AudioData(b);
        this.continuosAudioDataStream = new ContinuousAudioDataStream(this.audioData);
        this.audioDataStream = new AudioDataStream(this.audioData);
        return true;
    }

    public void play() {
        AudioPlayer.player.stop((InputStream)this.audioDataStream);
        AudioPlayer.player.stop((InputStream)this.continuosAudioDataStream);
        this.audioDataStream.reset();
        AudioPlayer.player.start((InputStream)this.audioDataStream);
    }

    public void loop() {
        AudioPlayer.player.stop((InputStream)this.audioDataStream);
        AudioPlayer.player.stop((InputStream)this.continuosAudioDataStream);
        this.continuosAudioDataStream.reset();
        AudioPlayer.player.start((InputStream)this.continuosAudioDataStream);
    }

    private byte[] linear2uLaw(short[] b) {
        int len = b.length;
        byte[] dst = new byte[len];
        for (int i = 0; i < len; ++i) {
            int j1 = b[i];
            int j = j1 >> 8 & 0x80;
            if (j != 0) {
                j1 = -j1;
            }
            if (j1 > 32635) {
                j1 = 32635;
            }
            int k = this.ai[(j1 += 132) >> 7 & 0xFF];
            int m = j1 >> k + 3 & 0xF;
            dst[i] = (byte)(~(j | k << 4 | m));
        }
        return dst;
    }
}

