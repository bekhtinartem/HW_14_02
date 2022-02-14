package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

class MyDraw extends View{

    public MyDraw(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        // Выбираем кисть
        paint.setStyle(Paint.Style.FILL);
        // Белый цвет кисти
        paint.setColor(Color.WHITE);
        // Закрашиваем холст
        canvas.drawPaint(paint);

        // Включаем антиальясинг
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(127,0,0,255));
        // Полупрозрачный синий круг радиусом 100 пикселей в центре экрана
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 100, paint);

        // Синий прямоугольник вверху экрана
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, getWidth(),200, paint);

        // Текст
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30);
        canvas.drawText("Samsung IT school", 50, 100, paint);

        // текст под углом
        float rotate_center_x = 200; //центр поворота по оси X
        float rotate_center_y = 200; // центр поворота по оси Y
        float rotate_angle = 45; //угол поворота

        // поворачиваем холст
        canvas.rotate(-rotate_angle, rotate_center_x, rotate_center_y);

        paint.setColor(Color.BLACK);
        paint.setTextSize(40);

        canvas.drawText("Samsung IT school",0,450,paint);

        // возвращаем холст на прежний угол
        canvas.rotate(rotate_angle, rotate_center_x, rotate_center_y);

        // Выведем изображение логотипа Samsung на экран в правом нижнем углу экрана
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.samsung);
        int xx = canvas.getWidth(), yy = canvas.getHeight();
        canvas.drawBitmap(image, xx - image.getWidth(), yy - image.getHeight(), paint);

    }
}

class GameView extends View{
    private Sprite enemyBird;
    private Sprite playerBird;
    private int viewWidth;
    private int viewHeight;
    private int points = 0;
    private final int timerInterval = 30;
/*
    public GameView(Context context) {
        super(context);
    }*/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;
    }
/*
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.drawARGB(250, 127, 199, 255); // заливаем цветом
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText(points+"", viewWidth - 100, 70, p);
    }*/
    public GameView(Context context) {

        super(context);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);

        int w = b.getWidth()/5;
        int h = b.getHeight()/3;

        Rect firstFrame = new Rect(0, 0, w, h);

        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth()/5;
        h = b.getHeight()/3;

        firstFrame = new Rect(4*w, 0, 5*w, h);

        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {
                if (i ==0 && j == 4) {
                    continue;
                }
                if (i ==2 && j == 0) {
                    continue;
                }
                enemyBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        Timer t = new Timer();
        t.start();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(250, 127, 199, 255); // цвет фона
        playerBird.draw(canvas);
        enemyBird.draw(canvas);

    }
    protected void update () {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval);
        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportEnemy ();
            points +=10;
        }
        if (enemyBird.intersect(playerBird)) {
            teleportEnemy ();
            points -= 40;
        }
        invalidate();

    }
    private void teleportEnemy () {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }
    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN)  {
            // Движение вверх
            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-100);
                points--;
            }
            else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(100);
                points--;
            }
        }
        return true;
    }
}


public class MainActivity extends AppCompatActivity {

    //first example
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(new MyDraw(this));
//        //setContentView(R.layout.activity_main);
//    }


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        setContentView(new GameView(this));
    }
}

