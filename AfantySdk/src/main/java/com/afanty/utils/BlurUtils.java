package com.afanty.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlurUtils {
    private static final String TAG = "BlurUtils";

    private static final int COMPRESS_RATIO = 5;
    private static final int BLUR_RADIUS = 5; // 1 <= radius <= 25

    private static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

    public interface OnBlurProcessListener {
        void onCompleted(Bitmap blurResult);
    }

    public static void blurView(View view, OnBlurProcessListener listener) {
        blur(getViewDrawingCache(view), listener);
    }

    private static void blur(final Bitmap bmp, final OnBlurProcessListener listener) {
        ThreadManager.getInstance().run(new DelayRunnableWork.UICallBackDelayRunnableWork() {
            private Bitmap blurBmp = null;

            @Override
            public void execute() {
                blurBmp = blur(bmp, COMPRESS_RATIO, BLUR_RADIUS);
            }

            @Override
            public void callBackOnUIThread() {
                if (listener == null)
                    return;
                listener.onCompleted(blurBmp);
            }
        });
    }

    private static Bitmap blur(Bitmap bmp, int compressRatio, int radius) {
        if (bmp == null) {
            return bmp;
        }
        long time = System.currentTimeMillis();
        Bitmap blurBmp = null;
        Bitmap comBmp;
        if (compressRatio > 1) {
            Matrix matrix = getScaleMatrix(1f / compressRatio);
            comBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        } else {
            comBmp = bmp;
        }
        if (radius <= 0)
            radius = 1;
        if (radius > 25)
            radius = 25;
        if (comBmp.getWidth() > radius && comBmp.getHeight() > radius) {
            BlurProcess blurProcess = new BlurProcess();
            blurBmp = blurProcess.blur(comBmp, radius);
        }
        if (!comBmp.equals(bmp))
            comBmp.recycle();
        return blurBmp;
    }

    private static Bitmap getViewDrawingCache(View view) {
        if (view == null)
            return null;
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        if (cacheBitmap == null)
            return null;
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap scaleBitmap = Bitmap.createBitmap(cacheBitmap, 0, 0, width, height, getScaleMatrix(0.5f), false);
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        return scaleBitmap;
    }

    private static Matrix getScaleMatrix(float scale) {
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return matrix;
    }

    /**
     * Blur using Java code.
     * <p>
     * This is a compromise between Gaussian Blur and Box blur
     * It creates much better looking blurs than Box Blur, but is
     * 7x faster than my Gaussian Blur implementation.
     * <p>
     * I called it Stack Blur because this describes best how this
     * filter works internally: it creates a kind of moving stack
     * of colors whilst scanning through the image. Thereby it
     * just has to add one new block of color to the right side
     * of the stack and remove the leftmost color. The remaining
     * colors on the topmost layer of the stack are either added on
     * or reduced by one, depending on if they are on the right or
     * on the left side of the stack.
     *
     * @author Enrique L??pez Ma??as <eenriquelopez@gmail.com>
     * http://www.neo-tech.es
     * <p>
     * Author of the original algorithm: Mario Klingemann <mario.quasimondo.com>
     * <p>
     * Based heavily on http://vitiy.info/Code/stackblur.cpp
     * See http://vitiy.info/stackblur-algorithm-multi-threaded-blur-for-cpp/
     * @copyright: Enrique L??pez Ma??as
     * @license: Apache License 2.0
     */
    private static class BlurProcess {

        private static final short[] stackblur_mul = {
                512, 512, 456, 512, 328, 456, 335, 512, 405, 328, 271, 456, 388, 335, 292, 512,
                454, 405, 364, 328, 298, 271, 496, 456, 420, 388, 360, 335, 312, 292, 273, 512,
                482, 454, 428, 405, 383, 364, 345, 328, 312, 298, 284, 271, 259, 496, 475, 456,
                437, 420, 404, 388, 374, 360, 347, 335, 323, 312, 302, 292, 282, 273, 265, 512,
                497, 482, 468, 454, 441, 428, 417, 405, 394, 383, 373, 364, 354, 345, 337, 328,
                320, 312, 305, 298, 291, 284, 278, 271, 265, 259, 507, 496, 485, 475, 465, 456,
                446, 437, 428, 420, 412, 404, 396, 388, 381, 374, 367, 360, 354, 347, 341, 335,
                329, 323, 318, 312, 307, 302, 297, 292, 287, 282, 278, 273, 269, 265, 261, 512,
                505, 497, 489, 482, 475, 468, 461, 454, 447, 441, 435, 428, 422, 417, 411, 405,
                399, 394, 389, 383, 378, 373, 368, 364, 359, 354, 350, 345, 341, 337, 332, 328,
                324, 320, 316, 312, 309, 305, 301, 298, 294, 291, 287, 284, 281, 278, 274, 271,
                268, 265, 262, 259, 257, 507, 501, 496, 491, 485, 480, 475, 470, 465, 460, 456,
                451, 446, 442, 437, 433, 428, 424, 420, 416, 412, 408, 404, 400, 396, 392, 388,
                385, 381, 377, 374, 370, 367, 363, 360, 357, 354, 350, 347, 344, 341, 338, 335,
                332, 329, 326, 323, 320, 318, 315, 312, 310, 307, 304, 302, 299, 297, 294, 292,
                289, 287, 285, 282, 280, 278, 275, 273, 271, 269, 267, 265, 263, 261, 259
        };

        private static final byte[] stackblur_shr = {
                9, 11, 12, 13, 13, 14, 14, 15, 15, 15, 15, 16, 16, 16, 16, 17,
                17, 17, 17, 17, 17, 17, 18, 18, 18, 18, 18, 18, 18, 18, 18, 19,
                19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 19, 20, 20, 20,
                20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 21,
                21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21,
                21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22,
                22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
                22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 23,
                23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23,
                23, 23, 23, 23, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,
                24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24
        };

        public Bitmap blur(Bitmap original, int radius) {
            int w = original.getWidth();
            int h = original.getHeight();
            int[] currentPixels = new int[w * h];
            original.getPixels(currentPixels, 0, w, 0, 0, w, h);
            int cores = BlurUtils.EXECUTOR_THREADS;

            ArrayList<BlurTask> horizontal = new ArrayList<>(cores);
            ArrayList<BlurTask> vertical = new ArrayList<>(cores);
            for (int i = 0; i < cores; i++) {
                horizontal.add(new BlurTask(currentPixels, w, h, radius, cores, i, 1));
                vertical.add(new BlurTask(currentPixels, w, h, radius, cores, i, 2));
            }

            try {
                BlurUtils.EXECUTOR.invokeAll(horizontal);
            } catch (InterruptedException e) {
                return null;
            }

            try {
                BlurUtils.EXECUTOR.invokeAll(vertical);
            } catch (InterruptedException e) {
                return null;
            }

            return Bitmap.createBitmap(currentPixels, w, h, Bitmap.Config.ARGB_8888);
        }

        private static void blurIteration(int[] src, int w, int h, int radius, int cores, int core, int step) {
            int x, y, xp, yp, i;
            int sp;
            int stack_start;
            int stack_i;

            int src_i;
            int dst_i;

            long sum_r, sum_g, sum_b, sum_in_r, sum_in_g, sum_in_b, sum_out_r, sum_out_g, sum_out_b;

            int wm = w - 1;
            int hm = h - 1;
            int div = (radius * 2) + 1;
            int mul_sum = stackblur_mul[radius];
            byte shr_sum = stackblur_shr[radius];
            int[] stack = new int[div];

            if (step == 1) {
                int minY = core * h / cores;
                int maxY = (core + 1) * h / cores;

                for (y = minY; y < maxY; y++) {
                    sum_r = sum_g = sum_b =
                            sum_in_r = sum_in_g = sum_in_b =
                                    sum_out_r = sum_out_g = sum_out_b = 0;

                    src_i = w * y;

                    for (i = 0; i <= radius; i++) {
                        stack_i = i;
                        stack[stack_i] = src[src_i];
                        sum_r += ((src[src_i] >>> 16) & 0xff) * (i + 1);
                        sum_g += ((src[src_i] >>> 8) & 0xff) * (i + 1);
                        sum_b += (src[src_i] & 0xff) * (i + 1);
                        sum_out_r += ((src[src_i] >>> 16) & 0xff);
                        sum_out_g += ((src[src_i] >>> 8) & 0xff);
                        sum_out_b += (src[src_i] & 0xff);
                    }

                    for (i = 1; i <= radius; i++) {
                        if (i <= wm)
                            src_i += 1;
                        stack_i = i + radius;
                        stack[stack_i] = src[src_i];
                        sum_r += ((src[src_i] >>> 16) & 0xff) * (radius + 1 - i);
                        sum_g += ((src[src_i] >>> 8) & 0xff) * (radius + 1 - i);
                        sum_b += (src[src_i] & 0xff) * (radius + 1 - i);
                        sum_in_r += ((src[src_i] >>> 16) & 0xff);
                        sum_in_g += ((src[src_i] >>> 8) & 0xff);
                        sum_in_b += (src[src_i] & 0xff);
                    }

                    sp = radius;
                    xp = radius;
                    if (xp > wm)
                        xp = wm;
                    src_i = xp + y * w;
                    dst_i = y * w;
                    for (x = 0; x < w; x++) {
                        src[dst_i] = (int)
                                ((src[dst_i] & 0xff000000) |
                                        ((((sum_r * mul_sum) >>> shr_sum) & 0xff) << 16) |
                                        ((((sum_g * mul_sum) >>> shr_sum) & 0xff) << 8) |
                                        ((((sum_b * mul_sum) >>> shr_sum) & 0xff)));
                        dst_i += 1;

                        sum_r -= sum_out_r;
                        sum_g -= sum_out_g;
                        sum_b -= sum_out_b;

                        stack_start = sp + div - radius;
                        if (stack_start >= div)
                            stack_start -= div;
                        stack_i = stack_start;

                        sum_out_r -= ((stack[stack_i] >>> 16) & 0xff);
                        sum_out_g -= ((stack[stack_i] >>> 8) & 0xff);
                        sum_out_b -= (stack[stack_i] & 0xff);

                        if (xp < wm) {
                            src_i += 1;
                            ++xp;
                        }

                        stack[stack_i] = src[src_i];

                        sum_in_r += ((src[src_i] >>> 16) & 0xff);
                        sum_in_g += ((src[src_i] >>> 8) & 0xff);
                        sum_in_b += (src[src_i] & 0xff);
                        sum_r += sum_in_r;
                        sum_g += sum_in_g;
                        sum_b += sum_in_b;

                        ++sp;
                        if (sp >= div)
                            sp = 0;
                        stack_i = sp;

                        sum_out_r += ((stack[stack_i] >>> 16) & 0xff);
                        sum_out_g += ((stack[stack_i] >>> 8) & 0xff);
                        sum_out_b += (stack[stack_i] & 0xff);
                        sum_in_r -= ((stack[stack_i] >>> 16) & 0xff);
                        sum_in_g -= ((stack[stack_i] >>> 8) & 0xff);
                        sum_in_b -= (stack[stack_i] & 0xff);
                    }

                }
            }

            else if (step == 2) {
                int minX = core * w / cores;
                int maxX = (core + 1) * w / cores;

                for (x = minX; x < maxX; x++) {
                    sum_r = sum_g = sum_b =
                            sum_in_r = sum_in_g = sum_in_b =
                                    sum_out_r = sum_out_g = sum_out_b = 0;

                    src_i = x;
                    for (i = 0; i <= radius; i++) {
                        stack_i = i;
                        stack[stack_i] = src[src_i];
                        sum_r += ((src[src_i] >>> 16) & 0xff) * (i + 1);
                        sum_g += ((src[src_i] >>> 8) & 0xff) * (i + 1);
                        sum_b += (src[src_i] & 0xff) * (i + 1);
                        sum_out_r += ((src[src_i] >>> 16) & 0xff);
                        sum_out_g += ((src[src_i] >>> 8) & 0xff);
                        sum_out_b += (src[src_i] & 0xff);
                    }
                    for (i = 1; i <= radius; i++) {
                        if (i <= hm)
                            src_i += w;

                        stack_i = i + radius;
                        stack[stack_i] = src[src_i];
                        sum_r += ((src[src_i] >>> 16) & 0xff) * (radius + 1 - i);
                        sum_g += ((src[src_i] >>> 8) & 0xff) * (radius + 1 - i);
                        sum_b += (src[src_i] & 0xff) * (radius + 1 - i);
                        sum_in_r += ((src[src_i] >>> 16) & 0xff);
                        sum_in_g += ((src[src_i] >>> 8) & 0xff);
                        sum_in_b += (src[src_i] & 0xff);
                    }

                    sp = radius;
                    yp = radius;
                    if (yp > hm)
                        yp = hm;
                    src_i = x + yp * w;
                    dst_i = x;
                    for (y = 0; y < h; y++) {
                        src[dst_i] = (int)
                                ((src[dst_i] & 0xff000000) |
                                        ((((sum_r * mul_sum) >>> shr_sum) & 0xff) << 16) |
                                        ((((sum_g * mul_sum) >>> shr_sum) & 0xff) << 8) |
                                        ((((sum_b * mul_sum) >>> shr_sum) & 0xff)));
                        dst_i += w;

                        sum_r -= sum_out_r;
                        sum_g -= sum_out_g;
                        sum_b -= sum_out_b;

                        stack_start = sp + div - radius;
                        if (stack_start >= div)
                            stack_start -= div;
                        stack_i = stack_start;

                        sum_out_r -= ((stack[stack_i] >>> 16) & 0xff);
                        sum_out_g -= ((stack[stack_i] >>> 8) & 0xff);
                        sum_out_b -= (stack[stack_i] & 0xff);

                        if (yp < hm) {
                            src_i += w;
                            ++yp;
                        }

                        stack[stack_i] = src[src_i];

                        sum_in_r += ((src[src_i] >>> 16) & 0xff);
                        sum_in_g += ((src[src_i] >>> 8) & 0xff);
                        sum_in_b += (src[src_i] & 0xff);
                        sum_r += sum_in_r;
                        sum_g += sum_in_g;
                        sum_b += sum_in_b;

                        ++sp;
                        if (sp >= div)
                            sp = 0;
                        stack_i = sp;

                        sum_out_r += ((stack[stack_i] >>> 16) & 0xff);
                        sum_out_g += ((stack[stack_i] >>> 8) & 0xff);
                        sum_out_b += (stack[stack_i] & 0xff);
                        sum_in_r -= ((stack[stack_i] >>> 16) & 0xff);
                        sum_in_g -= ((stack[stack_i] >>> 8) & 0xff);
                        sum_in_b -= (stack[stack_i] & 0xff);
                    }
                }
            }

        }

        private static class BlurTask implements Callable<Void> {
            private final int[] _src;
            private final int _w;
            private final int _h;
            private final int _radius;
            private final int _totalCores;
            private final int _coreIndex;
            private final int _round;

            public BlurTask(int[] src, int w, int h, int radius, int totalCores, int coreIndex, int round) {
                _src = src;
                _w = w;
                _h = h;
                _radius = radius;
                _totalCores = totalCores;
                _coreIndex = coreIndex;
                _round = round;
            }

            @Override
            public Void call() throws Exception {
                blurIteration(_src, _w, _h, _radius, _totalCores, _coreIndex, _round);
                return null;
            }
        }
    }
}
