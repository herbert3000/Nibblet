package nibblet.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import nibblet.Nibblet;
import nibblet.game.IMain;
import nibblet.game.map.MapCreator;

public class Main implements IMain {

    private Image screenImage;
    private Graphics screenGraphics;
    private Image[] levelGraphics = new Image[3];
    private Image animImage;
    private Image logoImage;
    private Image[] mapImages;
    private Graphics[] mapGraphics = new Graphics[8];
    private MapCreator mapCreator;
    private int stage = 0;
    private int logoX;
    private int logoY;
    private int[] pixels;
    private boolean[] dpix;
    private double[] sine = new double[1024];
    private Font introFont = new Font("Helvetica", 1, 12);
    private Font gameFont = new Font("Monospaced", 1, 12);
    private String[] levCodes = null;
    private int textBlink = 0;
    private static final int textBlinkSpeed = 40;
    double lpos = 0.0;
    int showY = 0;
    int rxs = 0;
    int rys = 0;
    int rxsa = 10;
    int rysa = 12;
    int rxd = 7;
    int ryd = 9;
    int gxs = 200;
    int gys = 600;
    int gxsa = -13;
    int gysa = 7;
    int gxd = 8;
    int gyd = -13;
    int bxs = 300;
    int bys = 900;
    int bxsa = 11;
    int bysa = -12;
    int bxd = -11;
    int byd = 10;
    int cut = 2;
    private Image ballImage;
    private static final int BALL_SIZE = 100;
    private static final int BALL_SIZE2 = 50;
    private int[] ballPix = new int[10000];
    private int[] ballPixClear = new int[10000];
    private static final int NUM_DOTS = 600;
    private double[][] ball = new double[600][3];
    private double[][] ballDraw = new double[600][3];
    private int alfa;
    private int beta;
    private int ballX = 0;
    private int ballY = 0;
    private boolean right = true;
    private int spin = 5;
    static int maxCol = 0;
    private int chew = 0;
    private char[][] map;
    private int nlCounter = 0;
    private int nlStage = 0;
    private String lcString = null;
    private final int[] _mapx;
    private final int[] _mapy;
    private static final int BRICK_FADE_DLY = 15;
    private Image curLevImg;
    private final Color sbarCol;
    private char[] dotChars;
    private String dotString;
    private static final int BAR_SKIP = 35;
    private int hCount;
    private boolean levelDone;
    private char[] levelChars;
    private String levelString;
    private int nrLives;
    private String livesString;
    private int startLevel;
    private int nrDots;
    private int screenCount;
    private int nrDietPills;
    private int[][] dietPills;
    private int mapPosX;
    private int mapPosY;
    private int pixPosX;
    private int pixPosY;
    private static final int hisSize = 2888;
    private int[] pixHisX;
    private int[] pixHisY;
    private int wormHead;
    private int wormLen;
    private double dWormLen;
    private int direction;
    private int headDir;
    private int munchType;
    private static final int[] moveX;
    private static final int[] moveY;
    private int moveBuf;
    private int dpCount;

    static {
        int[] arrn = new int[4];
        arrn[1] = 1;
        arrn[3] = -1;
        moveX = arrn;
        int[] arrn2 = new int[4];
        arrn2[0] = -1;
        arrn2[2] = 1;
        moveY = arrn2;
    }

    public Main() {
        int[] arrn = new int[10];
        arrn[3] = 1;
        arrn[4] = 2;
        arrn[5] = 2;
        arrn[6] = 2;
        arrn[7] = 1;
        arrn[8] = 1;
        arrn[9] = 1;
        this._mapx = arrn;
        int[] arrn2 = new int[10];
        arrn2[1] = 1;
        arrn2[2] = 2;
        arrn2[3] = 2;
        arrn2[4] = 2;
        arrn2[5] = 1;
        arrn2[8] = 1;
        arrn2[9] = 1;
        this._mapy = arrn2;
        this.curLevImg = null;
        this.sbarCol = new Color(0, 0, 128);
        this.dotChars = new char[]{'0', '0', '0'};
        this.dotString = new String(this.dotChars);
        this.hCount = 0;
        this.levelChars = new char[]{'0', '0'};
        this.levelString = new String(this.levelChars);
        this.nrLives = 5;
        this.livesString = String.valueOf(this.nrLives);
        this.startLevel = 0;
        this.nrDots = 0;
        this.screenCount = 0;
        this.mapPosX = 10;
        this.mapPosY = 19;
        this.pixPosX = this.mapPosX * 16;
        this.pixPosY = this.mapPosY * 16;
        this.pixHisX = new int[2888];
        this.pixHisY = new int[2888];
        this.wormHead = 1000;
        this.wormLen = 1;
        this.dWormLen = 1.5;
        this.direction = 0;
        this.headDir = 0;
        this.munchType = 0;
        this.moveBuf = 0;
        this.dpCount = 0;
    }

    private final void addPosToHis() {
        this.pixHisX[this.wormHead] = this.pixPosX;
        this.pixHisY[this.wormHead] = this.pixPosY;
        this.wormHead = (this.wormHead + 1) % 2888;
    }

    private final void clear(int back) {
        int xx = this.getHisX(back);
        int yy = this.getHisY(back);
        this.mapGraphics[this.screenCount].drawImage(this.mapImages[8], xx, yy, xx + 16, yy + 16, xx, yy, xx + 16, yy + 16, null);
    }

    private final void clearAll(int back) {
        int xx = this.getHisX(back);
        int yy = this.getHisY(back);
        for (int i = 0; i < 8; ++i) {
            this.mapGraphics[i].drawImage(this.mapImages[8], xx, yy, xx + 16, yy + 16, xx, yy, xx + 16, yy + 16, null);
        }
    }

    private final void doGame(int move, boolean die) {
        if (move > 0) {
            this.moveBuf = move;
        }
        if (this.screenCount == 0) {
            move = this.moveBuf;
            if (move == 1 && this.headDir == 3 || move == 2 && this.headDir == 4 || move == 3 && this.headDir == 1 || move == 4 && this.headDir == 2) {
                move = 0;
            }
            this.munchType = 0;
            int newDir = this.direction;
            if (move != 0 && this.map[this.mapPosX + moveX[move - 1]][this.mapPosY + moveY[move - 1]] != 'X') {
                newDir = move;
            }
            this.moveBuf = 0;
            if (newDir != 0 && this.map[this.mapPosX + moveX[newDir - 1]][this.mapPosY + moveY[newDir - 1]] != 'X') {
                this.direction = newDir;
                this.mapPosX += moveX[this.direction - 1];
                this.mapPosY += moveY[this.direction - 1];
                this.headDir = this.direction;
                switch (this.map[this.mapPosX][this.mapPosY]) {
                    case '.': {
                        this.munchType = 1;
                        --this.nrDots;
                        this.dWormLen += 0.65;
                        if ((int)this.dWormLen == this.wormLen) break;
                        ++this.wormLen;
                        ++this.munchType;
                        break;
                    }
                    case '*': {
                        this.goDiet();
                        this.munchType = 4;
                        break;
                    }
                    case 'O': {
                        this.munchType = 3;
                        break;
                    }
                }
                if (this.munchType != 2) {
                    int x = this.getHisX(this.wormLen * 8 - 7) / 16;
                    int y = this.getHisY(this.wormLen * 8 - 7) / 16;
                    this.map[x][y] = 32;
                }
                this.map[this.mapPosX][this.mapPosY] = 79;
            } else {
                this.direction = 0;
            }
        }
        if (this.direction != 0) {
            int yy;
            int xx;
            this.pixPosX += 2 * moveX[this.direction - 1];
            this.pixPosY += 2 * moveY[this.direction - 1];
            this.addPosToHis();
            this.clear(this.wormLen * 8 + 1);
            this.clear(this.wormLen * 8 - 6 + this.screenCount);
            this.clear(this.screenCount + 2);
            if (this.wormLen > 1) {
                xx = this.getHisX(9);
                yy = this.getHisY(9);
                this.mapGraphics[this.screenCount].drawImage(this.curLevImg, xx, yy, xx + 16, yy + 16, 16, 16, 32, 32, null);
            }
            if (this.wormLen > 2) {
                xx = this.getHisX(this.wormLen * 8 - 7);
                yy = this.getHisY(this.wormLen * 8 - 7);
                this.mapGraphics[this.screenCount].drawImage(this.curLevImg, xx, yy, xx + 16, yy + 16, 16, 16, 32, 32, null);
            }
            if (this.screenCount == 4) {
                switch (this.munchType) {
                    case 1: 
                    case 2: {
                        this.setDots(this.nrDots);
                        if (this.nrDots > 0) break;
                        this.levelDone = true;
                        this.stage = 4;
                        this.hCount = 0;
                        break;
                    }
                    case 3: {
                        die = true;
                        break;
                    }
                    case 4: {
                        this.removeDietPill(this.mapPosX, this.mapPosY);
                        break;
                    }
                }
            }
            this.screenGraphics.drawImage(this.mapImages[this.screenCount], 0, 16, null);
            this.screenCount = (this.screenCount + 1) % 8;
        } else {
            this.screenGraphics.drawImage(this.mapImages[(this.screenCount + 7) % 8], 0, 16, null);
        }
        this.drawDietPills();
        int fx = this.headDir == 0 ? 112 : (this.screenCount + 2) * 16 % 128;
        int fy = this.headDir == 0 ? 0 : (this.headDir - 1) * 16;
        this.screenGraphics.drawImage(this.animImage, this.pixPosX, this.pixPosY + 16, this.pixPosX + 16, this.pixPosY + 32, fx, fy, fx + 16, fy + 16, null);
        this.updateGameStatusBar();
        if (die) {
            this.levelDone = false;
            this.stage = 4;
            this.hCount = 0;
        }
    }

    public int doGame(int move, boolean fire1, boolean quit) {
        switch (this.stage) {
            case 0: {
                this.doIntro(move, fire1);
                break;
            }
            case 1: {
                this.hideIntro();
                if (!quit) break;
                this.stage = 0;
                break;
            }
            case 2: {
                this.showNextLevel(move, quit, fire1);
                if (!quit) break;
                this.goToIntro();
                break;
            }
            case 3: {
                this.doGame(move, quit);
                break;
            }
            case 4: {
                this.hideLevel();
                if (!quit) break;
                this.goToIntro();
                break;
            }
        }
        return 0;
    }

    private void doIntro(int move, boolean start) {
        this.screenGraphics.setColor(Color.black);
        this.screenGraphics.fillRect(0, 0, 336, 352);
        this.drawLogo(true);
        this.drawBall(true);
        if (this.textBlink > 20) {
            this.screenGraphics.setFont(this.introFont);
            this.screenGraphics.setColor(Color.white);
            this.screenGraphics.drawString("Press SPACE to play", 110, 30);
            this.screenGraphics.setColor(Color.gray);
            this.screenGraphics.drawString("or type level code", 120, 45);
        }
        this.textBlink = (this.textBlink + 1) % 40;
        if (start || move < 0) {
            int sl;
            int n = sl = move < 0 ? -move : 0;
            if (sl < this.mapCreator.getNumberOfLevels()) {
                this.startLevel = move < 0 ? -move : 0;
                this.nrLives = 5;
                this.stage = 1;
                this.nlCounter = 0;
                this.nlStage = 0;
            }
        }
    }

    private final void drawBall(boolean toShow) {
        System.arraycopy(this.ballPixClear, 0, this.ballPix, 0, this.ballPix.length);
        this.rotate(this.spin, 0);
        double d = 1.0;
        if (this.showY < this.logoY) {
            d = (double)this.showY / (double)this.logoY;
        }
        int bcol = (int)(80.0 * d);
        for (int i = 0; i < 600; ++i) {
            double[] b = this.ballDraw[i];
            int x = (int)b[0] + 50;
            int y = (int)b[1] + 50;
            int z = (int)b[2];
            int offs = x + y * 100;
            if (z < 0) {
                int c = (int)(d * 192.0 * (double)(-z) / 50.0 + 80.0 * d);
                int hcol = c << 16 | c << 8;
                bcol = hcol >> 1 & 0x7F7F7F;
                this.ballPix[offs] = hcol;
                this.ballPix[offs + 1] = bcol;
                this.ballPix[offs - 1] = bcol;
                this.ballPix[offs - 100] = bcol;
                this.ballPix[offs + 100] = bcol;
                continue;
            }
            this.ballPix[offs] = bcol;
        }
        this.ballImage.flush();
        int bY = (int)(252.0 - 100.0 * this.sine[this.ballY]);
        this.screenGraphics.drawImage(this.ballImage, this.ballX, bY, null);
        this.ballX += this.right ? 2 : -2;
        if (this.ballX >= 236) {
            this.spin = (int)(1022.0 - Math.random() * 10.0);
            this.right = false;
        } else if (this.ballX <= 0) {
            this.spin = (int)(2.0 + Math.random() * 10.0);
            this.right = true;
        }
        this.ballY = (this.ballY + 8) % 512;
    }

    private final void drawChars() {
        for (int y = 0; y < 21; ++y) {
            for (int x = 0; x < 21; ++x) {
                this.screenGraphics.drawString(String.valueOf(this.map[x][y]), x * 16 + 5, y * 16 + 28);
            }
        }
    }

    private final void drawDietPills() {
        for (int i = 0; i < this.nrDietPills; ++i) {
            int x = this.dietPills[i][0] * 16;
            if (x <= 0) continue;
            int y = 16 + this.dietPills[i][1] * 16;
            this.screenGraphics.drawImage(this.animImage, x, y, x + 16, y + 16, this.dpCount * 16, 64, this.dpCount * 16 + 16, 80, null);
        }
        this.dpCount = (this.dpCount + 1) % 8;
    }

    private final boolean drawLogo(boolean show) {
        int i = 0;
        double _rys = 200.0 * this.sine[this.rys];
        double _gys = 300.0 * this.sine[this.gys];
        double _bys = 400.0 * this.sine[this.bys];
        int ct = this.cut < 1536 ? this.cut : 3072 - this.cut;
        int ct2 = ct / 2;
        for (int y = 0; y < this.showY; ++y) {
            double ry = this.sine[(int)(_rys + (double)(y * this.ryd)) & 0x3FF];
            double gy = this.sine[(int)(_gys + (double)(y * this.gyd)) & 0x3FF];
            double by = this.sine[(int)(_bys + (double)(y * this.byd)) & 0x3FF];
            for (int x = 0; x < this.logoX; ++x) {
                if (this.dpix[i]) {
                    int r = (int)((2.0 + this.sine[this.rxs + x * this.rxd & 0x3FF] + ry) * 63.0);
                    int g = (int)((2.0 + this.sine[this.gxs + x * this.gxd & 0x3FF] + gy) * 63.0);
                    int b = (int)((2.0 + this.sine[this.bxs + x * this.bxd & 0x3FF] + by) * 63.0);
                    this.pixels[i] = (r + g + b) % ct < ct2 ? r << 16 | g << 8 | b : (r << 15 | g << 7 | b >> 1) & 0x7F7F7F;
                }
                ++i;
            }
        }
        this.rxs = this.rxs + this.rxsa & 0x3FF;
        this.rys = this.rys + this.rysa & 0x3FF;
        this.gxs = this.gxs + this.gxsa & 0x3FF;
        this.gys = this.gys + this.gysa & 0x3FF;
        this.bxs = this.bxs + this.bxsa & 0x3FF;
        this.bys = this.bys + this.bysa & 0x3FF;
        this.cut = (this.cut + 2) % 3072;
        if (this.cut < 2) {
            this.cut = 2;
        }
        int pos = 30;
        if (show) {
            if (this.lpos < Math.PI) {
                double d = -Math.cos(this.lpos) * (double)(this.logoX + 30) / 2.0;
                pos = (int)(d - (double)((this.logoX - 30) / 2));
                this.lpos += 0.05;
            } else if (this.showY < this.logoY) {
                ++this.showY;
            }
        } else if (this.showY > 0) {
            this.showY -= 4;
            if (this.showY < 0) {
                this.showY = 0;
            }
            int len = (this.showY + 4) * this.logoX;
            for (int j = this.showY * this.logoX; j < len; ++j) {
                if (!this.dpix[j]) continue;
                this.pixels[j] = 0;
            }
        } else if (this.lpos > 0.0) {
            double d = -Math.cos(this.lpos) * (double)(this.logoX + 30) / 2.0;
            pos = (int)(d - (double)((this.logoX - 30) / 2));
            this.lpos -= 0.1;
        } else {
            return false;
        }
        this.logoImage.flush();
        this.screenGraphics.drawImage(this.logoImage, pos, 50, null);
        return true;
    }

    private final int getHisX(int back) {
        return this.pixHisX[(this.wormHead - back + 2888) % 2888];
    }

    private final int getHisY(int back) {
        return this.pixHisY[(this.wormHead - back + 2888) % 2888];
    }

    private final void goDiet() {
        for (int i = 0; i < 5 && this.wormLen > 1; ++i) {
            int xx = this.getHisX(this.wormLen * 8 - 7);
            int yy = this.getHisY(this.wormLen * 8 - 7);
            int x = xx / 16;
            int y = yy / 16;
            this.map[x][y] = 32;
            this.clearAll(this.wormLen * 8 + 1);
            --this.wormLen;
            this.dWormLen -= 1.0;
        }
        this.clearAll(this.wormLen * 8 + 1);
    }

    private final void goToGame() {
        --this.nrLives;
        this.livesString = String.valueOf(this.nrLives);
        this.stage = 3;
        this.screenCount = 0;
        this.mapPosX = 10;
        this.mapPosY = 19;
        this.pixPosX = this.mapPosX * 16;
        this.pixPosY = this.mapPosY * 16;
        this.direction = 0;
        this.headDir = 0;
        this.wormHead = 0;
        this.wormLen = 1;
        this.dWormLen = 1.5;
        this.moveBuf = 0;
        this.mapGraphics[7].drawImage(this.animImage, this.pixPosX, this.pixPosY, this.pixPosX + 16, this.pixPosY + 16, 112, 0, 128, 16, null);
        this.screenGraphics.drawImage(this.mapImages[7], 0, 16, null);
        for (int i = 0; i < 16; ++i) {
            this.addPosToHis();
        }
    }

    private final void goToIntro() {
        int len = this.logoX * this.logoY;
        for (int i = 0; i < this.logoX * this.logoY; ++i) {
            if (!this.dpix[i]) continue;
            this.pixels[i] = 0;
        }
        this.textBlink = 0;
        this.lpos = 0.0;
        this.showY = 0;
        this.stage = 0;
    }

    private final void hideIntro() {
        this.screenGraphics.setColor(Color.black);
        this.screenGraphics.fillRect(0, 0, 336, 352);
        this.drawBall(false);
        if (!this.drawLogo(false)) {
            this.stage = 2;
            this.nlCounter = 0;
            this.nlStage = 0;
        }
    }

    private final void hideLevel() {
        if (this.hCount >= 42) {
            int hc = this.hCount - 42;
            int y = hc + 15;
            this.screenGraphics.setColor(Color.black);
            for (int i = 0; i < hc && y < 352; y += 35, ++i) {
                this.screenGraphics.fillRect(0, y, 336, 1);
            }
        } else {
            int v = this.hCount * 8 + 1;
            this.screenGraphics.setColor(Color.black);
            this.screenGraphics.drawLine(v, 16, 336, 352 - v);
            this.screenGraphics.drawLine(0, 16 + v, 336 - v, 352);
            this.screenGraphics.drawLine(v += 2, 16, 336, 352 - v);
            this.screenGraphics.drawLine(0, 16 + v, 336 - v, 352);
            this.screenGraphics.drawLine(v += 2, 16, 336, 352 - v);
            this.screenGraphics.drawLine(0, 16 + v, 336 - v, 352);
            this.screenGraphics.drawLine(v += 2, 16, 336, 352 - v);
            this.screenGraphics.drawLine(0, 16 + v, 336 - v, 352);
        }
        this.screenGraphics.setFont(this.introFont);
        if (this.levelDone) {
            if (this.startLevel == this.mapCreator.getNumberOfLevels()) {
                this.screenGraphics.setColor(Color.black);
                this.screenGraphics.drawString("That was it! You got em all!", 111, 156);
                this.screenGraphics.setColor(Color.white);
                this.screenGraphics.drawString("That was it! You got em all!", 110, 155);
            } else {
                this.screenGraphics.setColor(Color.black);
                this.screenGraphics.drawString("Well Done!", 141, 156);
                this.screenGraphics.setColor(Color.white);
                this.screenGraphics.drawString("Well Done!", 140, 155);
            }
        } else if (this.nrLives <= 0) {
            this.screenGraphics.setColor(Color.black);
            this.screenGraphics.drawString("GAME OVER", 141, 156);
            this.screenGraphics.setColor(Color.white);
            this.screenGraphics.drawString("GAME OVER", 140, 155);
        } else {
            this.screenGraphics.setColor(Color.black);
            this.screenGraphics.drawString("Ouch!", 156, 156);
            this.screenGraphics.setColor(Color.white);
            this.screenGraphics.drawString("Ouch!", 155, 155);
        }
        ++this.hCount;
        if (this.hCount > 127) {
            if (this.levelDone) {
                if (this.startLevel == this.mapCreator.getNumberOfLevels()) {
                    this.goToIntro();
                } else {
                    ++this.nrLives;
                    this.stage = 2;
                    this.nlCounter = 0;
                    this.nlStage = 0;
                }
            } else if (this.nrLives <= 0) {
                this.goToIntro();
            } else {
                --this.startLevel;
                this.stage = 2;
                this.nlCounter = 0;
                this.nlStage = 0;
            }
        }
    }

    public int init(Nibblet nibblet, Image screenImage, Graphics screenGraphics, Image[] loadImages, Image[] mapImages, char[][] lcs) {
        this.screenImage = screenImage;
        this.screenGraphics = screenGraphics;
        this.mapImages = mapImages;
        this.levelGraphics[0] = loadImages[0];
        this.levelGraphics[1] = loadImages[3];
        this.levelGraphics[2] = loadImages[4];
        this.animImage = loadImages[1];
        Image limg = loadImages[2];
        this.levCodes = new String[lcs.length];
        for (int i = 0; i < lcs.length; ++i) {
            this.levCodes[i] = new String(lcs[i]);
        }
        for (int i = 0; i < 8; ++i) {
            this.mapGraphics[i] = mapImages[i].getGraphics();
        }
        this.logoX = limg.getWidth(null);
        this.logoY = limg.getHeight(null);
        int len = this.logoX * this.logoY;
        this.pixels = new int[len];
        this.dpix = new boolean[len];
        this.logoImage = nibblet.createImage(new MemoryImageSource(this.logoX, this.logoY, (ColorModel)new DirectColorModel(24, 0xFF0000, 65280, 255), this.pixels, 0, this.logoX));
        PixelGrabber pg = new PixelGrabber(limg, 0, 0, this.logoX, this.logoY, this.pixels, 0, this.logoX);
        try {
            pg.grabPixels();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.logoImage.flush();
        for (int i = 0; i < len; ++i) {
            int p = this.pixels[i];
            if ((p >> 16 & 0xFF) <= 150 || (p & 0xFF) >= 150) continue;
            this.dpix[i] = true;
        }
        for (int i = 0; i < 1024; ++i) {
            this.sine[i] = Math.sin((double)i * Math.PI * 2.0 / 1024.0);
        }
        this.mapCreator = new MapCreator(nibblet, this.levelGraphics, mapImages);
        this.ballImage = nibblet.createImage(new MemoryImageSource(100, 100, (ColorModel)new DirectColorModel(24, 0xFF0000, 65280, 255), this.ballPix, 0, 100));
        for (int i = 0; i < 550; ++i) {
            this.alfa = (int)(Math.random() * 1024.0);
            this.beta = (int)(Math.random() * 1024.0);
            this.ball[i][0] = 45.0;
            this.rotateGen(i, this.alfa, (this.alfa + 256) % 1024, this.beta, (this.beta + 256) % 1024);
        }
        for (int i = 550; i < 600; ++i) {
            this.alfa = (int)(Math.random() * 512.0 + 256.0);
            this.ball[i][0] = 45.0;
            int dir = i % 2 == 0 ? 1 : 1023;
            this.rotateGen(i, dir, (dir + 256) % 1024, this.alfa, (this.alfa + 256) % 1024);
        }
        this.alfa = 0;
        this.beta = 0;
        this.goToIntro();
        return 0;
    }

    private void initGameStatusBar() {
        this.screenGraphics.setColor(this.sbarCol);
        this.screenGraphics.fillRect(0, 0, 336, 16);
        this.screenGraphics.setColor(Color.white);
        this.screenGraphics.setFont(this.gameFont);
        this.screenGraphics.drawString("LEVEL:", 20, 13);
        this.screenGraphics.drawString("DOTS:", 120, 13);
        this.screenGraphics.drawString("LIVES:", 230, 13);
    }

    public void kill() {
    }

    private final void removeDietPill(int x, int y) {
        for (int i = 0; i < this.nrDietPills; ++i) {
            int[] p = this.dietPills[i];
            if (p[0] != x || p[1] != y) continue;
            p[0] = -1;
            p[1] = -1;
            return;
        }
    }

    private void rotate(int a, int b) {
        this.alfa += a;
        this.beta += b;
        int asin = this.alfa % 1024;
        int acos = (this.alfa + 256) % 1024;
        int bsin = this.chew % 1024;
        int bcos = (this.chew + 256) % 1024;
        double ch = (this.sine[this.chew] + 1.0) / 2.0;
        for (int i = 0; i < 600; ++i) {
            this.rotate(i, asin, acos, ch);
        }
        this.chew = (this.chew + 20) % 1024;
    }

    private void rotate(int index, int asin, int acos, double ch) {
        double[] b = this.ball[index];
        double x = b[0];
        double y = b[1];
        double z = b[2];
        double x1 = x;
        double y1 = y;
        double z1 = z;
        if (x > 0.0) {
            int sn;
            ch = ch * x / 100.0;
            if (y > 0.0) {
                sn = (int)(256.0 + 256.0 * ch) % 1024;
                int cs = (sn + 256) % 1024;
                x1 = x * this.sine[sn] + y1 * this.sine[cs];
                y1 = -(x * this.sine[cs] - y1 * this.sine[sn]);
            } else {
                sn = (int)(1280.0 - 256.0 * ch) % 1024;
                int cs = (sn + 256) % 1024;
                x1 = x * this.sine[sn] + y1 * this.sine[cs];
                y1 = -(x * this.sine[cs] - y1 * this.sine[sn]);
            }
        }
        double xx = x1;
        x1 = xx * this.sine[acos] + z1 * this.sine[asin];
        z1 = xx * this.sine[asin] - z1 * this.sine[acos];
        this.ballDraw[index][0] = x1;
        this.ballDraw[index][1] = y1;
        this.ballDraw[index][2] = z1;
    }

    private void rotateGen(int index, int asin, int acos, int bsin, int bcos) {
        double[] b = this.ball[index];
        double x = b[0];
        double y = b[1];
        double z = b[2];
        double x1 = x * this.sine[acos] + y * this.sine[asin];
        double y1 = x * this.sine[asin] - y * this.sine[acos];
        double z1 = z * this.sine[bcos] + x1 * this.sine[bsin];
        this.ball[index][0] = x1 = z * this.sine[bsin] - x1 * this.sine[bcos];
        this.ball[index][1] = y1;
        this.ball[index][2] = z1;
    }

    private void setDots(int dots) {
        this.dotChars[0] = (char)(dots / 100 + 48);
        this.dotChars[1] = (char)(dots / 10 % 10 + 48);
        this.dotChars[2] = (char)(dots % 10 + 48);
        this.dotString = new String(this.dotChars);
    }

    private final void showNextLevel(int move, boolean quit, boolean speed) {
        if (this.nlCounter == 0 && this.nlStage == 0) {
            this.lcString = this.startLevel % 2 == 0 && this.startLevel > 1 ? "LEVEL CODE: " + this.levCodes[this.startLevel / 2 - 1] : null;
            this.map = this.mapCreator.createLevel(this.startLevel);
            this.curLevImg = this.levelGraphics[this.startLevel % 3];
            this.nrDietPills = this.mapCreator.getNumDietPills();
            this.dietPills = this.mapCreator.getDietPillPos();
            ++this.startLevel;
            this.nrDots = 0;
            this.setDots(0);
            this.levelChars[0] = (char)(48 + this.startLevel / 10);
            this.levelChars[1] = (char)(48 + this.startLevel % 10);
            this.levelString = new String(this.levelChars);
            for (int x = 0; x < 21; ++x) {
                for (int y = 0; y < 21; ++y) {
                    if (this.map[x][y] != '.') continue;
                    ++this.nrDots;
                }
            }
            this.screenGraphics.setColor(Color.black);
            this.screenGraphics.fillRect(0, 0, 336, 352);
            this.initGameStatusBar();
        }
        if (speed) {
            this.screenGraphics.drawImage(this.mapImages[0], 0, 16, null);
            this.goToGame();
            return;
        }
        if (this.nlStage == 0) {
            int part = this.nlCounter / 15;
            if (part >= this._mapx.length - 1) {
                this.nlCounter = 0;
                this.nlStage = 1;
            } else {
                int vis = this.nlCounter % 15 + 1;
                int mapx = this._mapx[part] * 112;
                int mapy = this._mapy[part] * 112;
                int fx = this._mapx[part + 1] * 112;
                int fy = this._mapy[part + 1] * 112;
                int dx = fx + (mapx - fx) * vis / 15;
                int dy = fy + (mapy - fy) * vis / 15;
                this.screenGraphics.setColor(Color.black);
                this.screenGraphics.fillRect(fx, fy + 16, 112, 112);
                this.mapCreator.showSquare(mapx, mapy, vis, 15, this.screenGraphics, dx, dy + 16);
            }
        } else {
            this.screenGraphics.drawImage(this.mapImages[0], 0, 16 + this.nlCounter * 16, 336, 32 + this.nlCounter * 16, 0, this.nlCounter * 16, 336, this.nlCounter * 16 + 16, null);
            this.setDots(this.nrDots * (this.nlCounter + 1) / 21);
            if (this.nlCounter == 20) {
                this.goToGame();
            }
        }
        this.updateGameStatusBar();
        this.screenGraphics.setFont(this.introFont);
        this.screenGraphics.setColor(Color.black);
        if (this.nlCounter == 20 && this.nlStage != 0) {
            this.screenGraphics.drawString("GO!", 161, 156);
            this.screenGraphics.setColor(Color.white);
            this.screenGraphics.drawString("GO!", 160, 155);
        } else {
            this.screenGraphics.drawString("Get Ready!", 151, 156);
            this.screenGraphics.setColor(Color.white);
            this.screenGraphics.drawString("Get Ready!", 150, 155);
        }
        if (this.lcString != null) {
            this.screenGraphics.setColor(Color.black);
            this.screenGraphics.drawString(this.lcString, 113, 176);
            this.screenGraphics.setColor(Color.white);
            this.screenGraphics.drawString(this.lcString, 112, 175);
        }
        ++this.nlCounter;
    }

    private void updateGameStatusBar() {
        this.screenGraphics.setColor(this.sbarCol);
        this.screenGraphics.fillRect(70, 0, 45, 16);
        this.screenGraphics.fillRect(165, 0, 55, 16);
        this.screenGraphics.fillRect(280, 0, 55, 16);
        this.screenGraphics.setColor(Color.white);
        this.screenGraphics.setFont(this.gameFont);
        this.screenGraphics.drawString(this.levelString, 70, 13);
        this.screenGraphics.drawString(this.dotString, 165, 13);
        this.screenGraphics.drawString(this.livesString, 280, 13);
    }
}

