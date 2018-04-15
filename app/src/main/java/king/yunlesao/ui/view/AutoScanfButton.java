package king.yunlesao.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by King on 2018/3/31.
 */

public class AutoScanfButton extends View {

    private int width;
    private int height;
    private Paint mPaint;
    private int mColor= Color.WHITE;
    private int textColor=Color.BLACK;
    private float textSize;
    private int radius;
    private String text="扫";
    public AutoScanfButton(Context context) {
        this(context,null);
    }
    public AutoScanfButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public AutoScanfButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width=getMeasuredWidth();
        height=getMeasuredHeight();
        radius=((width<height)? width : height)/2;
        textSize=radius*0.6f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mColor);
        canvas.drawCircle(width/2,height/2,10,mPaint);
        mPaint.setColor(textColor);
        mPaint.setTextSize(textSize);
        canvas.drawText(text,width/2,height/2,mPaint);
    }
}
