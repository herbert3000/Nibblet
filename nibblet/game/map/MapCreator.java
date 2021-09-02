package nibblet.game.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import nibblet.Nibblet;
import nibblet.game.map.MapProvider;

public class MapCreator {

    private Image[] mapImages;
    private Graphics[] mapGraphics;
    private Image[] levelGraphics;
    private Image offI;
    private PixelGrabber pg;
    private MapProvider mapProvider;
    private long[][] mapData;
    private char[][] map = new char[21][21];
    private int[] pixels = new int[112896];
    private int[] pixCopy = new int[112896];
    private int nrDietPills;
    private int[][] dietPills = new int[50][2];
    private static final char[] dotChars = new char[]{' ', 'X', '.', '*'};

    public MapCreator(Nibblet nibblet, Image[] levelGraphics, Image[] mapImages) {
        this.levelGraphics = levelGraphics;
        this.mapImages = mapImages;
        this.mapGraphics = new Graphics[mapImages.length];
        for (int i = 0; i < this.mapGraphics.length; ++i) {
            this.mapGraphics[i] = mapImages[i].getGraphics();
        }
        this.mapData = MapProvider.mapData;
        this.offI = nibblet.createImage(new MemoryImageSource(336, 336, (ColorModel)new DirectColorModel(24, 0xFF0000, 65280, 255), this.pixels, 0, 336));
        this.pg = new PixelGrabber(mapImages[8], 0, 0, 336, 336, this.pixels, 0, 336);
    }

    public char[][] createLevel(int level) {
        int j;
        int i;
        char c;
        int x;
        this.nrDietPills = 0;
        if (level < 0 || level >= this.mapData.length) {
            return null;
        }
        Image levelImg = this.levelGraphics[level % 3];
        long[] mp = this.mapData[level];
        for (int y = 0; y < 21; ++y) {
            long val = mp[y];
            for (x = 0; x < 21; ++x) {
                c = dotChars[(int)(val >> x * 2) & 3];
                if (c == '*') {
                    System.out.println("DP at " + x + "," + y);
                    this.dietPills[this.nrDietPills][0] = x;
                    this.dietPills[this.nrDietPills][1] = y;
                    ++this.nrDietPills;
                }
                this.map[x][y] = c;
            }
        }
        Graphics g = this.mapGraphics[8];
        g.setColor(Color.black);
        g.fillRect(0, 0, 336, 336);
        for (int y = 0; y < 21; ++y) {
            for (x = 0; x < 21; ++x) {
                c = this.map[x][y];
                int srcX = (int)(2.0 + Math.random() * 14.0) * 16;
                g.drawImage(levelImg, x * 16, y * 16, x * 16 + 16, y * 16 + 16, srcX, 16, srcX + 16, 32, null);
                if (c != 'X') continue;
                int block = 0;
                if (y > 0 && this.map[x][y - 1] == 'X') {
                    ++block;
                }
                if (y < 20 && this.map[x][y + 1] == 'X') {
                    block += 2;
                }
                if (x < 20 && this.map[x + 1][y] == 'X') {
                    block += 4;
                }
                if (x > 0 && this.map[x - 1][y] == 'X') {
                    block += 8;
                }
                int sx = block * 16;
                g.drawImage(levelImg, x * 16, y * 16, x * 16 + 16, y * 16 + 16, sx, 0, sx + 16, 16, null);
            }
        }
        this.pg = new PixelGrabber(this.mapImages[8], 0, 0, 336, 336, this.pixels, 0, 336);
        try {
            this.pg.grabPixels();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.arraycopy(this.pixels, 0, this.pixCopy, 0, 112896);
        int shx = 5;
        int shy = shx * 2;
        for (int y = 0; y < 21; ++y) {
            for (int x2 = 0; x2 < 21; ++x2) {
                int offs;
                if (this.map[x2][y] != 'X') continue;
                int pbase = x2 * 16 + y * 16 * 336;
                if (x2 < 20 && this.map[x2 + 1][y] != 'X') {
                    pbase += 16;
                    for (i = 0; i < shx; ++i) {
                        for (j = i * 2; j < 16; ++j) {
                            offs = pbase + i + j * 336;
                            this.pixels[offs] = this.shadow(this.pixCopy[offs], 7, 10);
                        }
                    }
                    pbase -= 16;
                }
                if (y < 20 && this.map[x2][y + 1] != 'X') {
                    pbase += 5376;
                    for (i = 0; i < shy; ++i) {
                        for (j = i / 2; j < 16; ++j) {
                            offs = pbase + i * 336 + j;
                            this.pixels[offs] = this.shadow(this.pixCopy[offs], 7, 10);
                        }
                    }
                    pbase -= 5376;
                }
                if (x2 >= 20 || y >= 20 || this.map[x2 + 1][y + 1] == 'X') continue;
                pbase += 5392;
                for (i = 0; i < shx; ++i) {
                    for (j = 0; j < shy; ++j) {
                        offs = pbase + i + 336 * j;
                        this.pixels[offs] = this.shadow(this.pixCopy[offs], 7, 10);
                    }
                }
            }
        }
        this.offI.flush();
        g.drawImage(this.offI, 0, 0, null);
        for (int y = 0; y < 21; ++y) {
            for (int x3 = 0; x3 < 21; ++x3) {
                if (this.map[x3][y] != '.') continue;
                int pbase = (y * 16 + 9) * 336 + x3 * 16 + 7;
                for (j = 0; j < 6; ++j) {
                    for (int i2 = 0; i2 < 6; ++i2) {
                        if (i2 + j < 2 || i2 + j > 8 || j == 0 && i2 > 3 || j == 1 && i2 == 5 || j == 4 && i2 == 0 || j == 5 && i2 < 2) continue;
                        int offs = pbase + j * 336 + i2;
                        this.pixels[offs] = this.shadow(this.pixCopy[offs], 7, 10);
                    }
                }
            }
        }
        this.offI.flush();
        this.mapGraphics[0].drawImage(this.offI, 0, 0, null);
        this.pg = new PixelGrabber(this.mapImages[8], 0, 0, 336, 336, this.pixels, 0, 336);
        try {
            this.pg.grabPixels();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        g = this.mapGraphics[0];
        System.arraycopy(this.pixels, 0, this.pixCopy, 0, 112896);
        for (int y = 0; y < 21; ++y) {
            for (int x4 = 0; x4 < 21; ++x4) {
                if (this.map[x4][y] != '.') continue;
                g.drawImage(levelImg, x4 * 16, y * 16, x4 * 16 + 16, y * 16 + 16, 0, 16, 16, 32, null);
            }
        }
        for (i = 1; i < 8; ++i) {
            this.mapGraphics[i].drawImage(this.mapImages[0], 0, 0, null);
        }
        return this.map;
    }

    public int[][] getDietPillPos() {
        return this.dietPills;
    }

    public int getNumDietPills() {
        return this.nrDietPills;
    }

    public int getNumberOfLevels() {
        return this.mapData.length;
    }

    int shadow(int p, int mul, int div) {
        int r = (p >> 16 & 0xFF) * mul / div;
        int g = (p >> 8 & 0xFF) * mul / div;
        int b = (p & 0xFF) * mul / div;
        return r << 16 | g << 8 | b;
    }

    public void showSquare(int x1, int y1, int mul, int div, Graphics dst, int dstX, int dstY) {
        for (int y = y1; y < y1 + 112; ++y) {
            int offs = y * 336 + x1;
            for (int x = 0; x < 112; ++x) {
                this.pixels[offs] = mul == div ? this.pixCopy[offs] : this.shadow(this.pixCopy[offs], mul, div);
                ++offs;
            }
        }
        this.offI.flush();
        dst.drawImage(this.offI, dstX, dstY, dstX + 112, dstY + 112, x1, y1, x1 + 112, y1 + 112, null);
    }
}

