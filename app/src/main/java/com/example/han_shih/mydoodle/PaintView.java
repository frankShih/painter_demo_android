package com.example.han_shih.mydoodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Han_Shih on 2017/5/16.
 */

public class PaintView extends View{
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF000000;
    private int brushSize = 1;
    //canvas
    Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    public PaintView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    void reset_canvas(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    int getPaintColor(){
        return paintColor;
    }

    void setPaintColor(String colorCode){
        invalidate();
        paintColor = Color.parseColor(colorCode);
        drawPaint.setColor(Color.parseColor(colorCode));
    }

    void setPaintColor(int colorCode){
        invalidate();
        paintColor = colorCode;
        drawPaint.setColor(colorCode);
    }

    int getBrushSize(){
        return brushSize;
    }

    void setBrushSize(int b_size){
        brushSize = b_size;
        drawPaint.setStrokeWidth(b_size);
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setAntiAlias(true);
        //drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //view given size
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //Draw specified bitmap, with top/left corner, using specified paint
        canvas.drawPath(drawPath, drawPaint);
        //Draw specified path using specified paint

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //drawing cache be invalidated. If view is visible, onDraw(graphics.Canvas) will be called
        invalidate();
        return true;
    }

}
