package fun.wxy.annoy_o_tron.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fun.wxy.annoy_o_tron.service.OverlayService;
import fun.wxy.annoy_o_tron.utils.U;

public class OverlayReceiver extends BroadcastReceiver {
    private static final String TAG = OverlayReceiver.class.getSimpleName();
    public final static String REDRAW_ZOO_KEEPER_OVERLAY = "redraw.zoo.keeper.overlay";

    public OverlayReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // snapshot
        U.snapshot("zookeeper.png");
        // check block in image
        ImageView iv = OverlayService.instance.imageView;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/zookeeper.png";
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(5.0f);
            paint.setStyle(Paint.Style.STROKE); // drawRect 空心效果

            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(40);

            Bitmap fileBitmap = null;
            try {
                int i = 0;
                while (fileBitmap == null) {
                    FileInputStream fis = new FileInputStream(imgFile.getAbsolutePath());
                    fileBitmap = BitmapFactory.decodeStream(new BufferedInputStream(fis)); // !important 要加上 BufferedInputStream 才不會出錯
                    i++;
                }
                Log.v(TAG, "do BitmapFactory.decodeStream() time: " + i);
            } catch (Exception e) {
                Log.e(TAG, "fileBitmap: " + fileBitmap);
            }
            Bitmap bitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888); // 畫布是 ImageView 的寬高
            Canvas canvas = new Canvas(bitmap);
            int len = canvas.getWidth() / 8;
            // detect block
            BLOCK[][] blocks = new BLOCK[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    int x = len * i;
                    int y = 434 + len * j; // for Nexus 5X   434 for ImageView  (434 + 66) for Screenshot
                    blocks[i][j] = detectIcon(fileBitmap, x, y + 66, len); // 找圖像是 screenshot 的寬高
                    //canvas.drawCircle(x, y, 5, textPaint); // TODO: REMOVE - paint a circle
                    //canvas.drawText(blocks[i][j].toString(), x + 3, y + 35, textPaint); // TODO: REMOVE - show block text
                }
            }
            // check block and draw link
            int matchNum = 0;
            for (int j = 0; j < 8; j++) {
                for (int i = 0; i < 8; i++) {
                    int[] match = matchTile(blocks, i, j);
                    if (match != null) {
                        // draw rect
                        //int left = len * (i + match[3]);
                        //int top = 434 + len * (j + match[4]);
                        //int right = left + len * match[1];
                        //int bottom = top + len * match[2];
                        //canvas.drawRect(left, top, right, bottom, paint);
                        // draw line
                        drawLine(canvas, match, len * i, 434 + len * j, len, paint);

                        matchNum++;
                    }
                }
            }
            canvas.drawText("### " + matchNum + " match(s) ###", 50, 90, textPaint);

            iv.setImageBitmap(bitmap);
            OverlayService.instance.isReceiverRunning = false;
        }
    }

    private void drawLine(Canvas canvas, int[] match, int left, int top, int len, Paint paint) {
        int sLeft = left + len / 2;
        int sTop = top + len / 2;
        if (match[0] == 1) {
            drawLine(canvas, paint, len, new int[] {sLeft, sTop}, new int[] {0, 1}, new int[] {1, 1}, new int[] {-1, 1}, new int[] {0, 1});
        } else if (match[0] == 2) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{0, 1}, new int[]{-1, 1}, new int[]{1, 1}, new int[]{0, 1});
        } else if (match[0] == 3) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 0}, new int[]{1, 1}, new int[]{1, -1}, new int[]{1, 0});
        } else if (match[0] == 4) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 0}, new int[]{1, -1}, new int[]{1, 1}, new int[]{1, 0});
        } else if (match[0] == 5) {
            drawLine(canvas, paint, len, new int[] {sLeft, sTop}, new int[] {0, 1}, new int[] {1, 1}, new int[] {-1, 1});
        } else if (match[0] == 6) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{0, 1}, new int[]{-1, 1}, new int[]{1, 1});
        } else if (match[0] == 7) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 0}, new int[]{1, 1}, new int[]{1, -1});
        } else if (match[0] == 8) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 0}, new int[]{1, -1}, new int[]{1, 1});
        } else if (match[0] == 9) {
            drawLine(canvas, paint, len, new int[] {sLeft, sTop}, new int[] {1, 1}, new int[] {-1, 1}, new int[] {0, 1});
        } else if (match[0] == 10) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{-1, 1}, new int[]{1, 1}, new int[]{0, 1});
        } else if (match[0] == 11) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 1}, new int[]{1, -1}, new int[]{1, 0});
        } else if (match[0] == 12) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, -1}, new int[]{1, 1}, new int[]{1, 0});
        } else if (match[0] == 13) {
            drawLine(canvas, paint, len, new int[] {sLeft, sTop}, new int[] {0, 1}, new int[] {1, 1});
        } else if (match[0] == 14) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{0, 1}, new int[]{-1, 1});
        } else if (match[0] == 15) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 0}, new int[]{1, 1});
        } else if (match[0] == 16) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 0}, new int[]{1, -1});
        } else if (match[0] == 17) {
            drawLine(canvas, paint, len, new int[] {sLeft, sTop}, new int[] {1, 1}, new int[] {0, 1});
        } else if (match[0] == 18) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{-1, 1}, new int[]{0, 1});
        } else if (match[0] == 19) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 1}, new int[]{1, 0});
        } else if (match[0] == 20) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, -1}, new int[]{1, 0});
        } else if (match[0] == 21) {
            drawLine(canvas, paint, len, new int[] {sLeft, sTop}, new int[] {1, 1}, new int[] {-1, 1});
        } else if (match[0] == 22) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{-1, 1}, new int[]{1, 1});
        } else if (match[0] == 23) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, 1}, new int[]{1, -1});
        } else if (match[0] == 24) {
            drawLine(canvas, paint, len, new int[]{sLeft, sTop}, new int[]{1, -1}, new int[]{1, 1});
        }
    }

    private void drawLine(Canvas canvas, Paint paint, int len, int[]... locs) {
        int[] point = locs[0];
        for (int i = 1; i < locs.length; i++) { // 第一個位置是 start point 的 x, y 後面都是相對位置
            int[] loc = locs[i];
            int[] endPoint = new int[] {point[0] + loc[0] * len, point[1] + loc[1] * len};
            canvas.drawLine(point[0], point[1], endPoint[0], endPoint[1], paint);
            point = endPoint;
        }
    }


    private int[] matchTile(BLOCK[][] bs, int i, int j) {
        int[] result = null; // { "match type", "number of i", "number of j", "move_i", "move_j" }
        BLOCK b = bs[i][j];
        if (b == BLOCK.UNKNOWN) {
            // pass
        } else if (isTileInRange(i, j, 1, 4) && b == bs[i][j + 1] && b == bs[i + 1][j + 2] && b == bs[i][j + 3] && b == bs[i][j + 4]) {
            result = new int[] {1, 1, 5, 0, 0};
        } else if (isTileInRange(i, j, -1, 4) && b == bs[i][j + 1] && b == bs[i - 1][j + 2] && b == bs[i][j + 3] && b == bs[i][j + 4]) {
            result = new int[] {2, 1, 5, 0, 0};
        } else if (isTileInRange(i, j, 4, 1) && b == bs[i + 1][j] && b == bs[i + 2][j + 1] && b == bs[i + 3][j] && b == bs[i + 4][j]) {
            result = new int[] {3, 5, 1, 0, 0};
        } else if (isTileInRange(i, j, 4, -1) && b == bs[i + 1][j] && b == bs[i + 2][j - 1] && b == bs[i + 3][j] && b == bs[i + 4][j]) {
            result = new int[] {4, 5, 1, 0, 0};
        } else if (isTileInRange(i, j, 1, 3) && b == bs[i][j + 1] && b == bs[i + 1][j + 2] && b == bs[i][j + 3]) {
            result = new int[] {5, 1, 4, 0, 0};
        } else if (isTileInRange(i, j, -1, 3) && b == bs[i][j + 1] && b == bs[i - 1][j + 2] && b == bs[i][j + 3]) {
            result = new int[] {6, 1, 4, 0, 0};
        } else if (isTileInRange(i, j, 3, 1) && b == bs[i + 1][j] && b == bs[i + 2][j + 1] && b == bs[i + 3][j]) {
            result = new int[] {7, 4, 1, 0, 0};
        } else if (isTileInRange(i, j, 3, -1) && b == bs[i + 1][j] && b == bs[i + 2][j - 1] && b == bs[i + 3][j]) {
            result = new int[] {8, 4, 1, 0, 0};
        } else if (isTileInRange(i, j, 1, 3) && b == bs[i + 1][j + 1] && b == bs[i][j + 2] && b == bs[i][j + 3]) {
            result = new int[] {9, 1, 4, 0, 0};
        } else if (isTileInRange(i, j, -1, 3) && b == bs[i - 1][j + 1] && b == bs[i][j + 2] && b == bs[i][j + 3]) {
            result = new int[] {10, 1, 4, 0, 0};
        } else if (isTileInRange(i, j, 3, 1) && b == bs[i + 1][j + 1] && b == bs[i + 2][j] && b == bs[i + 3][j]) {
            result = new int[] {11, 4, 1, 0, 0};
        } else if (isTileInRange(i, j, 3, -1) && b == bs[i + 1][j - 1] && b == bs[i + 2][j] && b == bs[i + 3][j]) {
            result = new int[] {12, 4, 1, 0, 0};
        } else if (isTileInRange(i, j, 1, 2) && b == bs[i][j + 1] && b == bs[i + 1][j + 2]) {
            result = new int[] {13, 1, 3, 0, 0};
        } else if (isTileInRange(i, j, -1, 2) && b == bs[i][j + 1] && b == bs[i - 1][j + 2]) {
            result = new int[] {14, 1, 3, 0, 0};
        } else if (isTileInRange(i, j, 2, 1) && b == bs[i + 1][j] && b == bs[i + 2][j + 1]) {
            result = new int[] {15, 3, 1, 0, 0};
        } else if (isTileInRange(i, j, 2, -1) && b == bs[i + 1][j] && b == bs[i + 2][j - 1]) {
            result = new int[] {16, 3, 1, 0, 0};
        } else if (isTileInRange(i, j, 1, 2) && b == bs[i + 1][j + 1] && b == bs[i + 1][j + 2]) {
            result = new int[] {17, 1, 3, 1, 0};
        } else if (isTileInRange(i, j, -1, 2) && b == bs[i - 1][j + 1] && b == bs[i - 1][j + 2]) {
            result = new int[] {18, 1, 3, -1, 0};
        } else if (isTileInRange(i, j, 2, 1) && b == bs[i + 1][j + 1] && b == bs[i + 2][j + 1]) {
            result = new int[] {19, 3, 1, 0, 1};
        } else if (isTileInRange(i, j, 2, -1) && b == bs[i + 1][j - 1] && b == bs[i + 2][j - 1]) {
            result = new int[] {20, 3, 1, 0, -1};
        } else if (isTileInRange(i, j, 1, 2) && b == bs[i + 1][j + 1] && b == bs[i][j + 2]) {
            result = new int[] {21, 1, 3, 0, 0};
        } else if (isTileInRange(i, j, -1, 2) && b == bs[i - 1][j + 1] && b == bs[i][j + 2]) {
            result = new int[] {22, 1, 3, 0, 0};
        } else if (isTileInRange(i, j, 2, 1) && b == bs[i + 1][j + 1] && b == bs[i + 2][j]) {
            result = new int[] {23, 3, 1, 0, 0};
        } else if (isTileInRange(i, j, 2, -1) && b == bs[i + 1][j - 1] && b == bs[i + 2][j]) {
            result = new int[] {24, 3, 1, 0, 0};
        }
        return result;
    }

    private boolean isTileInRange(int i, int j, int moveI, int moveJ) {
        return i + moveI >= 0 && i + moveI < 8 && j + moveJ >= 0 && j + moveJ < 8;
    }


    private enum BLOCK {
        Hippo("紫"), Giraffe("黃"), Panda("黑"), Ape("紅"), Crocodile("綠"), Lion("橘"), Elephant("藍"),
        Remove("[R]"), UNKNOWN("[X]");

        BLOCK(String name) {
            this.name = name;
        }
        private String name;
        @Override
        public String toString() {
            return this.name;
        }
    }

    private BLOCK detectIcon(Bitmap bitmap, int x, int y, int len) {
        BLOCK result = BLOCK.UNKNOWN;
        // pass bg color when picked
        Set<String> bgSet = new HashSet<>();
        bgSet.add("#FAFCDC");
        bgSet.add("#FAFCDE");
        bgSet.add("#FBF8CB");
        bgSet.add("#FBF8CD");
        bgSet.add("#FCF5C0");
        bgSet.add("#FCF5C2");

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            String rgb = getColorString(bitmap, x, y, len);
            if (bgSet.contains(rgb)) { // 取到非主要顏色 就跳過
                continue; // pass it
            }
            if (!map.containsKey(rgb)) {
                map.put(rgb, 0);
            }
            map.put(rgb, map.get(rgb) + 1);
        }

        if (map.containsKey("#00D880")) {
            result = BLOCK.Remove;
        } else if (map.containsKey("#480C60") && map.containsKey("#D070A8")) {
            result = BLOCK.Hippo;
        } else if (map.containsKey("#481800") && map.containsKey("#F8F804")) {
            result = BLOCK.Giraffe;
        } else if (map.containsKey("#181818") && map.containsKey("#F9F8F8")) {
            result = BLOCK.Panda;
        } else if (map.containsKey("#501000") && map.containsKey("#F86046")) {
            result = BLOCK.Ape;
        } else if (map.containsKey("#013820") && map.containsKey("#48D000")) {
            result = BLOCK.Crocodile;
        } else if (map.containsKey("#400C00") && map.containsKey("#F8A800")) {
            result = BLOCK.Lion;
        } else if (map.containsKey("#082078") && map.containsKey("#80C8F8")) {
            result = BLOCK.Elephant;
        } else {
            // Log.v(TAG, map.toString());
        }
        return result;
    }

    private String getColorString(Bitmap bitmap, int x, int y, int len) {
        int xp = (int) (Math.random() * len);
        int yp = (int) (Math.random() * len);
        int colorInt = bitmap.getPixel(x + xp, y + yp);
        return String.format("#%06X", (0xFFFFFF & colorInt));
    }

}
