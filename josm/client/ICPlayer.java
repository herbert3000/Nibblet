package josm.client;

interface ICPlayer {
    public void stop();

    public boolean prepare(short[] var1);

    public void play();

    public void loop();
}

