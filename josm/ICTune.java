package josm;

import java.io.File;
import java.net.URL;

public interface ICTune {
    public boolean generate();

    public boolean generate(boolean var1);

    public String getAudioPlayer();

    public String getLastError();

    public int getModuleSize();

    public int getSamplingSize();

    public boolean load(File var1);

    public boolean load(URL var1);

    public boolean load(byte[] var1);

    public void loop();

    public void play();

    public void stop();
}

