package fun.wxy.annoy_o_tron.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.app.Fragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fun.wxy.annoy_o_tron.R;

/**
 * Created by 0_o on 2016/4/27.
 */
public class U {

    public static Bitmap roundCorner(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap toSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = Math.min(width, height);
        int leftTopX = (width - length) / 2;
        int leftTopY = (height - length) / 2;
        Bitmap result = Bitmap.createBitmap(bitmap, leftTopX, leftTopY, length, length);
        return result;
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int width) {
        float scale = (float) width / (float) bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return result;
    }

    public static String formatDate(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Date parseDate(String str) {
        return parseDate(str, "yyyy-MM-dd");
    }

    public static Date parseDate(String str, String pattern) {
        Date result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            result = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        return DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.18:1433/iwill", "sa", "123456_abc");
    }

}
