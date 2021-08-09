//package com.example.clinicapplication.model;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapShader;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Shader;
//import android.os.Build;
//import android.service.autofill.Transformation;
//
//import androidx.annotation.RequiresApi;
//
//
//
//@RequiresApi(api = Build.VERSION_CODES.O_MR1)
//public class CircleTransform implements Transformation {
//    @Override
//    public Bitmap transform(Bitmap source) {
//        int size = Math.min(source.getWidth(), source.getHeight());
//
//        int x = (source.getWidth() - size) / 2;
//        int y = (source.getHeight() - size) / 2;
//
//        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
//        if (squaredBitmap != source) {
//            source.recycle();
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
//
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        BitmapShader shader = new BitmapShader(squaredBitmap,
//                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        paint.setShader(shader);
//        paint.setAntiAlias(true);
//
//        float r = size / 2f;
//        canvas.drawCircle(r, r, r, paint);
//
//        squaredBitmap.recycle();
//        return bitmap;
//    }
//
//    @Override
//    public String key() {
//        return "circle";
//    }
//}
