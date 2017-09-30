package com.zxinglib.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zeng on 2017/2/15.
 */

public class QrCodeDecoder {
    private static Map<DecodeHintType, Object> getHints() {
        List<BarcodeFormat> allFormats = new ArrayList<>();
        allFormats.add(BarcodeFormat.AZTEC);
        allFormats.add(BarcodeFormat.CODABAR);
        allFormats.add(BarcodeFormat.CODE_39);
        allFormats.add(BarcodeFormat.CODE_93);
        allFormats.add(BarcodeFormat.CODE_128);
        allFormats.add(BarcodeFormat.DATA_MATRIX);
        allFormats.add(BarcodeFormat.EAN_8);
        allFormats.add(BarcodeFormat.EAN_13);
        allFormats.add(BarcodeFormat.ITF);
        allFormats.add(BarcodeFormat.MAXICODE);
        allFormats.add(BarcodeFormat.PDF_417);
        allFormats.add(BarcodeFormat.QR_CODE);
        allFormats.add(BarcodeFormat.RSS_14);
        allFormats.add(BarcodeFormat.RSS_EXPANDED);
        allFormats.add(BarcodeFormat.UPC_A);
        allFormats.add(BarcodeFormat.UPC_E);
        allFormats.add(BarcodeFormat.UPC_EAN_EXTENSION);
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, BarcodeFormat.QR_CODE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, allFormats);
        return hints;
    }

    /**
     * 解析本地图片二维码。
     * @param picturePath 要解析的二维码图片本地路径
     */
    public static void decodeQRCode(final String picturePath, final ResultCallback callback) {
        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {
                return decodeQRCode(getDecodeAbleBitmap(picturePath));
            }

            @Override
            protected void onPostExecute(Result result) {
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        }.execute();
    }

    private static Result decodeQRCode(Bitmap bitmap) {
        RGBLuminanceSource source = null;
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            source = new RGBLuminanceSource(width, height, pixels);
            return new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), getHints());
        } catch (Exception e) {
            e.printStackTrace();
            if (source != null) {
                try {
                    return new MultiFormatReader().decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)), getHints());
                } catch (Throwable e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }
    }
    
    /**
     * 解析bitmap二维码。
     * @param bitmap 要解析的二维码图片
     */
    public static void decodeQRCode(final Bitmap bitmap, final ResultCallback callback) {
        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {
                RGBLuminanceSource source = null;
                try {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    source = new RGBLuminanceSource(width, height, pixels);
                    return new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), getHints());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (source != null) {
                        try {
                            return new MultiFormatReader().decode(new BinaryBitmap(new GlobalHistogramBinarizer(source)), getHints());
                        } catch (Throwable e2) {
                            e2.printStackTrace();
                        }
                    }
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Result result) {
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        }.execute();
    }
    
    /**
     * 将本地图片文件转换成可解码二维码的 Bitmap。为了避免图片太大，这里对图片进行了压缩。
     * @param picturePath 本地图片文件路径
     */
    private static Bitmap getDecodeAbleBitmap(String picturePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            int sampleSize = options.outHeight / 400;
            if (sampleSize <= 0) {
                sampleSize = 1;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(picturePath, options);
        } catch (Exception e) {
            return null;
        }
    }
}
