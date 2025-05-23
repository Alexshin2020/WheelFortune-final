package com.example.wheelfortune;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WheelView extends View {
    private List<String> items = new ArrayList<>();
    private Paint paint = new Paint();
    private RectF rectF = new RectF();
    private float currentAngle = 0;
    private boolean isSpinning = false;
    private RoundItemSelectedListener listener;
    private int backgroundColor = Color.RED; // Default color
    private int textColor = Color.WHITE; // Default color
    private Drawable cursorDrawable;

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setTextSize(30);

        // Получение кастомных атрибутов
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.WheelView,
                0, 0);

        try {
            backgroundColor = a.getColor(R.styleable.WheelView_BackgroundColor, Color.RED);
            textColor = a.getColor(R.styleable.WheelView_TextColor, Color.WHITE);
            cursorDrawable = a.getDrawable(R.styleable.WheelView_Cursor);
        } finally {
            a.recycle();
        }

        paint.setColor(textColor); // Устанавливаем цвет для paint
    }

    public void setData(List<String> items) {
        this.items = items;
        invalidate();
    }

    public void setRoundItemSelectedListener(RoundItemSelectedListener listener) {
        this.listener = listener;
    }

    public void reset() {
        currentAngle = 0;
        isSpinning = false;
        invalidate();
    }

    public void spin() {
        isSpinning = true;
        new Thread(() -> {
            Random random = new Random();
            float targetAngle = random.nextFloat() * 360;
            float spinAngle = 360 * 5 + targetAngle;
            for (float i = 0; i < spinAngle; i += 10) {
                currentAngle = i % 360;
                postInvalidate();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isSpinning = false;
            int index = (int) (360 - (targetAngle % 360)) * items.size() / 360;
            index = (index + items.size()) % items.size();
            if (listener != null) {
                int finalIndex = index;
                post(() -> listener.onRoundItemSelected(finalIndex));
            }
            postInvalidate();
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        rectF.set(0, 0, width, height);
        float startAngle = 0;

        // Рисование секторов
        for (int i = 0; i < items.size(); i++) {
            paint.setColor(Color.HSVToColor(new float[]{(i * 360f / items.size()), 1, 1}));
            canvas.drawArc(rectF, startAngle, 360f / items.size(), true, paint);
            startAngle += 360f / items.size();
        }

        // Рисование текста
        for (int i = 0; i < items.size(); i++) {
            canvas.save();
            canvas.rotate(180 + (i * 360f / items.size()), width / 2, height / 2);
            canvas.drawText(items.get(i), width / 2, height / 2, paint);
            canvas.restore();
        }

        // Рисование курсора
        canvas.save();
        canvas.rotate(currentAngle, width / 2, height / 2);
        paint.setColor(Color.RED);
        canvas.drawLine(width / 2, 0, width / 2, height / 2, paint);
        canvas.restore();
    }

    public interface RoundItemSelectedListener {
        void onRoundItemSelected(int index);
    }
}