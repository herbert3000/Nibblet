package nibblet.game;

import java.awt.Graphics;
import java.awt.Image;
import nibblet.Nibblet;

public interface IMain {

    public int doGame(int var1, boolean var2, boolean var3);

    public int init(Nibblet var1, Image var2, Graphics var3, Image[] var4, Image[] var5, char[][] var6);

    public void kill();
}

