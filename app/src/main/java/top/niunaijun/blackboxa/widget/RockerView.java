package top.niunaijun.blackboxa.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Virtual joystick / rocker view for in-game virtual controller overlay.
 */
public class RockerView extends View {
    private Paint bgPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint stickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float centerX, centerY;
    private float stickX, stickY;
    private float maxRadius;

    private OnRockerListener listener;

    public interface OnRockerListener {
        void onMove(float x, float y);
        void onRelease();
    }

    public RockerView(Context context) { this(context, null); }
    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bgPaint.setColor(Color.argb(100, 150, 150, 150));
        stickPaint.setColor(Color.argb(180, 80, 80, 200));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        centerX = w / 2f;
        centerY = h / 2f;
        stickX  = centerX;
        stickY  = centerY;
        maxRadius = Math.min(w, h) / 4f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = Math.min(getWidth(), getHeight()) / 2.2f;
        canvas.drawCircle(centerX, centerY, radius, bgPaint);
        canvas.drawCircle(stickX, stickY, maxRadius, stickPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                float dx = event.getX() - centerX;
                float dy = event.getY() - centerY;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > maxRadius) {
                    float scale = maxRadius / dist;
                    dx *= scale;
                    dy *= scale;
                }
                stickX = centerX + dx;
                stickY = centerY + dy;
                if (listener != null) listener.onMove(dx / maxRadius, dy / maxRadius);
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stickX = centerX;
                stickY = centerY;
                if (listener != null) listener.onRelease();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnRockerListener(OnRockerListener l) { this.listener = l; }
}
