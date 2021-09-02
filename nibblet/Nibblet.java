package nibblet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import josm.ICTune;
import nibblet.NibbletLoader;
import nibblet.game.IMain;

public class Nibblet extends Applet implements Runnable, KeyListener, FocusListener {

    private int sleepTime = 30;
    private int xSize;
    private int ySize;
    private boolean hasFocus = true;
    ICTune tune;
    Image screenI;
    Graphics screenG;
    private Image screenI2;
    private Graphics screenG2;
    private Image offI;
    private int[] pixels;
    private boolean running = true;
    private Thread mainThread;
    private NibbletLoader loaderThread;
    private int counter = 0;
    private boolean introMode = true;
    IMain mainMan;
    private Font introFont = new Font("Monospaced", 1, 14);
    private Font focusFont = new Font("Helvetica", 0, 10);
    private int colCount = 0;
    private static final int LETTER_SPACE_X = 10;
    private static final int LETTER_SPACE_Y = 12;
    private static final int RADIUS = 70;
    private static final int NUM_COLS = 100;
    private static final int MAX_LETTS = 15;
    private static final int NEW_LETT_DLY = 2;
    private static final int LETT_TOT = 30;
    private static final int NEW_PAGE_DLY = 10;
    private Color[] fadeCols = new Color[100];
    private int lettDlyCount = 0;
    private int[][] lettPos = new int[15][4];
    private int pageStage = 0;
    String[] active = new String[15];
    private static final String[][] textPages = new String[][]{{"-------------------------", " NIBBLET V0.8 IS LOADING", "-------------------------", "(C)2001 Mikael Johansson", "", "The loading progress is", "shown in bottom left of", "this applet.", "", "The object of the game is", "to munch all the yummy", "dots without taking a", "bite of your own tail.", "(Sounds familiar, huh?)", "", "There is a special kind", "of dot, the \"diet-pill\",", "which makes you loose the", "five last segments (won't", "make you loose your head", "though;-)", "", "On level 3,5,7 etc level", "codes are given. Just type", "these on the main screen", "to start at that level"}, {"", "", "", "", "", "Keyboard:", " SPACE - start game", " UP - up", " DOWN - down", " LEFT - left", " RIGHT - right", " ESC - suicide", "  (takes you back to the", "   main screen between", "   levels)", " ENTER - music on/off", "", "That's about it, I guess.", "", "        Good luck!"}};
    private int lettX = 0;
    private int lettY = 0;
    private int lettPage = 0;
    private char[] cc = new char[1];
    private int[] syncLen = new int[1];
    private int move = 0;
    private boolean fire = false;
    private boolean quit = false;
    boolean music = false;
    static final char[][] lc = new char[][]{{'E', 'A', 'T', 'D', 'O', 'T', 'S'}, {'E', 'A', 'S', 'Y', 'M', 'A', 'N'}, {'T', 'K', '2', 'R', 'U', 'L', 'E', 'S'}, {'B', 'A', 'L', 'L', 'C', 'H', 'A', 'I', 'N'}, {'T', 'H', 'E', 'K', 'N', 'I', 'F', 'E'}, {'A', 'G', 'E', 'N', 'T', '0', '0', '7'}, {'W', 'O', 'O', 'D', 'L', 'A', 'N', 'D'}, {'Y', 'E', 'A', 'R', 'N', 'I', 'N', 'G'}, {'F', 'T', 'P', 'S', 'I', 'T', 'E'}, {'G', 'R', 'E', 'E', 'D', 'Y'}, {'S', 'P', 'E', 'C', 'I', 'A', 'L'}, {'F', 'R', 'O', 'Z', 'E', 'N'}, {'S', 'M', 'U', 'R', 'F', 'S'}, {'P', 'O', 'L', 'I', 'C', 'E'}};
    private static int[] offs = new int[lc.length];
    MediaTracker mediaTracker;
    static String[] loadImageNames = new String[]{"graphics/MapBricks.gif", "graphics/HeadAndPill.gif", "graphics/Logo.gif", "graphics/MapBricks2.gif", "graphics/MapBricks3.gif"};
    Image[] loadImages = new Image[loadImageNames.length];
    Image[] mapImages = new Image[9];
    int progress = 0;

    public void focusGained(FocusEvent fe) {
        this.hasFocus = true;
    }

    public void focusLost(FocusEvent fe) {
        this.hasFocus = false;
    }

    private final void grabEm() {
        PixelGrabber pg = new PixelGrabber(this.screenI, 0, 0, this.xSize, this.ySize, this.pixels, 0, this.xSize);
        try {
            pg.grabPixels();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        resize(336, 352); // fix by herbert3000
        Dimension d = this.getSize();
        
        this.xSize = d.width;
        this.ySize = d.height;
        this.screenI = this.createImage(this.xSize, this.ySize);
        this.screenG = this.screenI.getGraphics();
        this.screenI2 = this.createImage(this.xSize, this.ySize);
        this.screenG2 = this.screenI2.getGraphics();
        this.screenG.setColor(Color.black);
        this.screenG.fillRect(0, 0, this.xSize, this.ySize);
        this.screenG2.setColor(Color.black);
        this.screenG2.fillRect(0, 0, this.xSize, this.ySize);
        this.pixels = new int[this.xSize * this.ySize];
        this.offI = this.createImage(new MemoryImageSource(this.xSize, this.ySize, (ColorModel)new DirectColorModel(24, 0xFF0000, 65280, 255), this.pixels, 0, this.xSize));
        for (int i = 0; i < 100; ++i) {
            this.fadeCols[i] = new Color(i * 255 / 100, i * 255 / 100, i * 255 / 100);
        }
        this.addKeyListener(this);
        this.addFocusListener(this);
    }

    public void keyPressed(KeyEvent ke) {
        this.hasFocus = true;
        int keyCode = ke.getKeyCode();
        if (keyCode == 10 && this.tune != null) {
            if (this.music) {
                this.tune.stop();
            } else {
                this.tune.loop();
            }
            this.music ^= true;
        }
        if (this.introMode && this.progress >= 100 && keyCode == 32) {
            this.introMode = false;
            return;
        }
        if (keyCode == 38) {
            this.move = 1;
        } else if (keyCode == 39) {
            this.move = 2;
        } else if (keyCode == 40) {
            this.move = 3;
        } else if (keyCode == 37) {
            this.move = 4;
        } else if (keyCode == 32) {
            this.fire = true;
        } else if (keyCode == 27) {
            this.quit = true;
        }
    }

    public void keyReleased(KeyEvent ke) {
        this.hasFocus = true;
        int keyCode = ke.getKeyCode();
        if (keyCode == 38 && this.move == 1) {
            this.move = 11;
        } else if (keyCode == 39 && this.move == 2) {
            this.move = 12;
        } else if (keyCode == 40 && this.move == 3) {
            this.move = 13;
        } else if (keyCode == 37 && this.move == 4) {
            this.move = 14;
        } else if (keyCode == 32) {
            this.fire = false;
        } else if (keyCode == 27) {
            this.quit = false;
        }
    }

    public void keyTyped(KeyEvent ke) {
        int i;
        char c = Character.toUpperCase(ke.getKeyChar());
        if (c == '-' && this.sleepTime > 5) {
            this.sleepTime -= 5;
        } else if (c == '+' && this.sleepTime < 40) {
            this.sleepTime += 5;
        } else if (!this.introMode && c >= '0' && c <= 'Z' && (i = this.scanForLC(c)) > 0) {
            this.move = -i * 2;
            this.fire = true;
        }
    }

    byte[] loadBinary(String s) {
        URL url1 = null;
        try {
            url1 = new URL(this.getCodeBase(), s);
        }
        catch (MalformedURLException malformedURLException) {}
        byte[] b = new byte[]{};
        try {
            URLConnection urlCon = url1.openConnection();
            urlCon.connect();
            BufferedInputStream bis = new BufferedInputStream(urlCon.getInputStream());
            int len = urlCon.getContentLength();
            b = new byte[len];
            int read = 0;
            for (int tot = 0; read >= 0 && tot < len; tot += read) {
                read = bis.read(b, tot, len - tot);
            }
            urlCon = null;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return b;
    }

    Image loadImage(String s) {
        if (this.mediaTracker == null) {
            this.mediaTracker = new MediaTracker(this);
        }
        byte[] b = this.loadBinary(s);
        Image i = this.getToolkit().createImage(b);
        this.mediaTracker.addImage(i, 0);
        try {
            this.mediaTracker.waitForID(0);
        }
        catch (InterruptedException interruptedException) {}
        return i;
    }

    public boolean mouseDown(Event evt, int x, int y) {
        return true;
    }

    public boolean mouseUp(Event evt, int x, int y) {
        return true;
    }

    private boolean nextLetter() {
        while (true) {
            if (this.lettY >= textPages[this.lettPage].length) {
                this.active[14] = null;
                return false;
            }
            if (this.lettX >= textPages[this.lettPage][this.lettY].length()) {
                this.lettX = 0;
                ++this.lettY;
                continue;
            }
            this.cc[0] = textPages[this.lettPage][this.lettY].charAt(this.lettX);
            ++this.lettX;
            if (this.cc[0] != ' ') break;
        }
        this.active[14] = new String(this.cc);
        double ang = Math.random() * 2.0 * Math.PI;
        this.lettPos[14][0] = (int)(70.0 * Math.cos(ang));
        this.lettPos[14][1] = (int)(70.0 * Math.sin(ang));
        this.lettPos[14][2] = 40 + this.lettX * 10;
        this.lettPos[14][3] = 20 + this.lettY * 12;
        return true;
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, this.xSize, this.ySize);
        g.setColor(Color.white);
        g.drawString("Nibblet", this.xSize / 2 - 20, this.ySize / 2 + 5);
    }

    private void render() {
        if (this.introMode) {
            this.renderText();
            this.showProgress();
        } else {
            int _move = 0;
            if (this.move < 10) {
                _move = this.move;
            }
            boolean _fire = this.fire;
            this.mainMan.doGame(_move, _fire, this.quit);
            this.fire = false;
            this.quit = false;
        }
    }

    private void renderText() {
        if (this.pageStage <= 1) {
            this.screenG.drawImage(this.screenI2, 0, 0, null);
            this.screenG.setFont(this.introFont);
            boolean noneToDraw = true;
            for (int i = 0; i < 15; ++i) {
                if (this.active[i] == null) continue;
                noneToDraw = false;
                double progr = ((double)(i + 1) * 2.0 - (double)this.lettDlyCount) / 30.0;
                int dx = this.lettPos[i][0];
                int dy = this.lettPos[i][1];
                int tx = this.lettPos[i][2];
                int ty = this.lettPos[i][3];
                int x = (int)((double)tx + (double)dx * progr);
                int y = (int)((double)ty + (double)dy * progr);
                int col = (int)(100.0 * (1.0 - progr));
                this.screenG.setColor(this.fadeCols[col]);
                this.screenG.drawString(this.active[i], x, y);
            }
            this.lettDlyCount = (this.lettDlyCount + 1) % 2;
            if (this.lettDlyCount == 0) {
                this.lettDlyCount = 0;
                if (this.active[0] != null) {
                    this.screenG2.setFont(this.introFont);
                    this.screenG2.setColor(Color.white);
                    this.screenG2.drawString(this.active[0], this.lettPos[0][2], this.lettPos[0][3]);
                }
                int[] t = this.lettPos[0];
                for (int i = 0; i < 14; ++i) {
                    this.active[i] = this.active[i + 1];
                    this.lettPos[i] = this.lettPos[i + 1];
                }
                this.lettPos[14] = t;
                if (this.pageStage == 0) {
                    if (!this.nextLetter() && noneToDraw) {
                        this.pageStage = 1;
                        this.counter = 0;
                    }
                } else if (this.counter++ >= 10) {
                    this.pageStage = 2;
                    this.grabEm();
                    this.counter = 0;
                }
            }
        } else if (this.pageStage == 2) {
            int len = this.xSize * this.ySize;
            int c2 = this.counter * 2;
            int al = c2 * this.xSize;
            for (int i = (c2 - 80) * this.xSize; i < al; ++i) {
                if (i < this.xSize + 1 || i > len - this.xSize - 1) continue;
                int p = this.pixels[i];
                int pl = this.pixels[i - 1];
                int pu = this.pixels[i - this.xSize];
                int pr = this.pixels[i + 1];
                int pd = this.pixels[i + this.xSize];
                this.pixels[i] = (p >> 1 & 0x7F7F7F) + (pl >> 3 & 0x1F1F1F) + (pu >> 3 & 0x1F1F1F) + (pr >> 3 & 0x1F1F1F) + (pd >> 3 & 0x1F1F1F);
            }
            this.offI.flush();
            this.screenG.drawImage(this.offI, 0, 0, null);
            ++this.counter;
            if (c2 * this.xSize >= len + this.xSize * 80) {
                this.counter = 0;
                this.screenG2.setColor(Color.black);
                this.screenG2.fillRect(0, 0, this.xSize, this.ySize);
                this.lettPage = (this.lettPage + 1) % textPages.length;
                this.pageStage = 0;
                this.lettDlyCount = 0;
                this.lettX = 0;
                this.lettY = 0;
            }
        }
    }

    public void run() {
        while (this.running) {
            try {
                Thread.sleep(this.sleepTime);
            }
            catch (Exception exception) {}
            if (this.syncLen[0] != 1) {
                try {
                    this.syncLen.wait();
                }
                catch (Exception exception) {}
            }
            this.syncLen[0] = 0;
            this.repaint();
        }
    }

    private int scanForLC(char c) {
        boolean ret = false;
        for (int i = 0; i < lc.length; ++i) {
            if (lc[i][offs[i]] == c) {
                int n = i;
                offs[n] = offs[n] + 1;
                if (offs[i] != lc[i].length) continue;
                for (int j = 0; j < lc.length; ++j) {
                    Nibblet.offs[j] = 0;
                }
                return i + 1;
            }
            Nibblet.offs[i] = 0;
        }
        return 0;
    }

    private final void showProgress() {
        this.screenG.setColor(Color.black);
        this.screenG.fillRect(0, this.ySize - 15, 90, 15);
        this.screenG.setFont(this.focusFont);
        if (this.progress < 100) {
            this.screenG.setColor(Color.white);
            this.screenG.drawString("Loading: " + this.progress + '%', 5, this.ySize - 5);
        } else {
            int col = this.colCount >= 100 ? 200 - this.colCount - 1 : this.colCount;
            this.screenG.setColor(this.fadeCols[col]);
            this.screenG.drawString("Press SPACE...", 5, this.ySize - 5);
            this.colCount = (this.colCount + 4) % 200;
        }
    }

    public void start() {
        this.mainThread = new Thread(this);
        this.mainThread.start();
        this.loaderThread = new NibbletLoader(this);
        this.loaderThread.start();
    }

    public void stop() {
        this.running = false;
        this.loaderThread.running = false;
        if (this.tune != null && this.music) {
            this.tune.stop();
            this.music = false;
        }
    }

    public void update(Graphics g) {
        if (this.syncLen[0] == 0) {
            this.render();
            g.drawImage(this.screenI, 0, 0, null);
            if (this.progress >= 100 && !this.hasFocus) {
                g.setColor(Color.white);
                g.setFont(this.focusFont);
                g.drawString("Click for focus!", 155 + (int)(Math.random() * 3.0), 100 + (int)(Math.random() * 3.0));
            }
            this.syncLen[0] = this.syncLen[0] + 1;
            int[] arrn = this.syncLen;
            synchronized (arrn) {
                this.syncLen.notifyAll();
            }
        }
    }
}

