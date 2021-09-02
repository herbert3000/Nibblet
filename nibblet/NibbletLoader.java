package nibblet;

import josm.ICTune;
import nibblet.Nibblet;
import nibblet.game.IMain;

class NibbletLoader extends Thread {

    Nibblet nibblet;
    boolean running = true;

    NibbletLoader(Nibblet n) {
        this.nibblet = n;
    }

    public void run() {
        try {
            try {
                Class<?> c = Class.forName("josm.client.CTune");
                ++this.nibblet.progress;
                ICTune tune = (ICTune)c.newInstance();
                ++this.nibblet.progress;
                byte[] josm = this.nibblet.loadBinary("music/nib");
                ++this.nibblet.progress;
                tune.load(josm);
                ++this.nibblet.progress;
                tune.generate(true);
                ++this.nibblet.progress;
                tune.loop();
                this.nibblet.music = true;
                this.nibblet.tune = tune;
            }
            catch (Exception e) {
                System.out.println("No music:");
                e.printStackTrace();
            }
            for (int i = 0; i < this.nibblet.mapImages.length; ++i) {
                this.nibblet.mapImages[i] = this.nibblet.createImage(336, 336);
                ++this.nibblet.progress;
            }
            for (int i = 0; i < Nibblet.loadImageNames.length; ++i) {
                this.nibblet.loadImages[i] = this.nibblet.loadImage(Nibblet.loadImageNames[i]);
                ++this.nibblet.progress;
            }
            Class<?> c = Class.forName("nibblet.game.Main");
            this.nibblet.mainMan = (IMain)c.newInstance();
            ++this.nibblet.progress;
            this.nibblet.mainMan.init(this.nibblet, this.nibblet.screenI, this.nibblet.screenG, this.nibblet.loadImages, this.nibblet.mapImages, Nibblet.lc);
            ++this.nibblet.progress;
            this.nibblet.requestFocus();
            this.nibblet.progress = 100;
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

