package org.openslide.dzi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import edu.cmu.cs.openslide.OpenSlide;

public class DeepZoomGenerator {

    static final int TILE_SIZE = 256;

    static final int OVERLAP = 1;

    static final int numThreads = Runtime.getRuntime().availableProcessors();

    static final ExecutorService executor = new ThreadPoolExecutor(numThreads,
            numThreads, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(
                    numThreads * 2), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws IOException {
        File f = new File(args[0]);

        final OpenSlide os = new OpenSlide(f);
        long w = os.getLayer0Width();
        long h = os.getLayer0Height();

        String dirname;
        if (args.length > 1) {
            dirname = args[1];
        } else {
            dirname = os.getProperties()
                    .get(OpenSlide.PROPERTY_NAME_QUICKHASH1);
        }

        if (dirname == null) {
            System.out.println("Please give directory name on command line");
            return;
        }

        File dir = new File(dirname);

        String bgcolorStr = os.getProperties().get(
                OpenSlide.PROPERTY_NAME_BACKGROUND_COLOR);
        if (bgcolorStr == null) {
            bgcolorStr = "FFFFFF";
        }
        final Color bgcolor = Color.decode("#" + bgcolorStr);

        final AtomicInteger tileCount = new AtomicInteger();
        long lastTime = System.currentTimeMillis();
        long lastTiles = 0;
        double tilesPerSec = 0.0;

        // determine dzi level
        int level = (int) Math.ceil(Math.log(Math.max(w, h)) / Math.log(2));
        final int topLevel = level;

        final long totalTiles = computeTotalTiles(w, h, level, TILE_SIZE);

        while (level >= 0) {
            System.out.println("generating level " + level + ": ("
                    + (w / TILE_SIZE) + "," + (h / TILE_SIZE) + ")");

            final File curDir = new File(dir, Integer.toString(level));
            curDir.mkdirs();

            int j = 0;
            for (int y = -OVERLAP; y < h; y += TILE_SIZE) {
                int th = TILE_SIZE + 2 * OVERLAP;
                if (y < 0) {
                    th += y;
                    y = 0;
                }
                if (y + th > h) {
                    th = (int) (h - y);
                }

                final int fth = th;
                final int fy = y;
                int i = 0;
                for (int x = -OVERLAP; x < w; x += TILE_SIZE) {
                    int tw = TILE_SIZE + 2 * OVERLAP;
                    if (x < 0) {
                        tw += x;
                        x = 0;
                    }
                    if (x + tw > w) {
                        th = (int) (w - x);
                    }

                    final int ftw = tw;
                    final int fx = x;
                    final String filename = i + "_" + j;
                    final File prevDir;
                    if (level == topLevel) {
                        prevDir = null;
                    } else {
                        prevDir = new File(dir, Integer.toString(level + 1));
                    }
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            tileCount.incrementAndGet();
                            File jpegFile = new File(curDir, filename + ".jpeg");

                            if (jpegFile.exists()) {
                                return;
                            }

                            try {
                                BufferedImage img = new BufferedImage(ftw, fth,
                                        BufferedImage.TYPE_INT_ARGB_PRE);
                                if (prevDir == null) {
                                    // get from OpenSlide level 0
                                    int dest[] = ((DataBufferInt) img
                                            .getRaster().getDataBuffer())
                                            .getData();
                                    os.paintRegionARGB(dest, fx, fy, 0, ftw,
                                            fth);

                                    // png
                                    File pngFile = new File(curDir, filename
                                            + ".png");
                                    ImageIO.write(img, "png", pngFile);
                                } else {
                                    // TODO generate from previous dir
                                    return;
                                }

                                // jpeg
                                BufferedImage img2 = new BufferedImage(ftw,
                                        fth, BufferedImage.TYPE_INT_RGB);
                                Graphics2D g = img2.createGraphics();
                                g.setColor(bgcolor);
                                g.fillRect(0, 0, ftw, fth);
                                g.drawImage(img, 0, 0, null);
                                g.dispose();
                                ImageIO.write(img2, "jpeg", jpegFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.exit(0);
                            }
                        }
                    });

                    long time = System.currentTimeMillis();
                    long tilesComputed = tileCount.longValue();
                    long deltaTime = time - lastTime;
                    if (deltaTime > 500) {
                        long deltaTiles = tilesComputed - lastTiles;

                        lastTime = time;
                        lastTiles = tilesComputed;

                        tilesPerSec = (0.9 * tilesPerSec)
                                + (0.1 * (deltaTiles / (deltaTime / 1000.0)));
                        int percent = (int) (100 * tilesComputed / totalTiles);
                        System.out
                                .print(" " + tilesComputed + "/" + totalTiles
                                        + " " + percent
                                        + "%, tiles per second: "
                                        + (int) tilesPerSec
                                        + "                     \r");
                        System.out.flush();

                    }

                    i++;
                }
                j++;
            }
            System.out.println();
            level--;
        }

        executor.shutdown();
    }

    private static long computeTotalTiles(long w, long h, int level,
            int tileSize) {
        long t = 0;
        while (level >= 0) {
            long ww = (w / TILE_SIZE) + ((w % TILE_SIZE) == 0 ? 0 : 1);
            long hh = (h / TILE_SIZE) + ((h % TILE_SIZE) == 0 ? 0 : 1);
            t += ww * hh;

            w /= 2;
            h /= 2;
            level--;
        }

        return t;
    }
}