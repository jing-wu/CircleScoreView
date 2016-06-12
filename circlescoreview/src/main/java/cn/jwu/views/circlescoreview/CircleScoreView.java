package cn.jwu.views.circlescoreview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jwu on 16/6/8.
 */
public class CircleScoreView extends View {

    private int mWidth;//width of this view
    private int mHeight;//height of this view

    private String mText;

    private Paint mOutsideCirclePaint;
    private Paint mInnerCirclePaint;
    private Paint mTextPaint;
    private Rect mTextBound;
    private RectF mInnerCircleRect;
    private float mStartSweepValue;//the start angle of the inner circle
    private float mCurrentAngle;
    private int mCurrentPercent;
    private int mTargetPercent;

    private int mPadding = 20;
    private int mGapBetweenCircles = 10;//the space between the two circles
    private int mOutSideCircleColor = Color.BLACK;
    private int mOutSideCircleWidth = 2;
    private int mInnerCircleColor = Color.BLACK;
    private int mInnerCircleWidth = 10;
    private int mTextSize = 60;
    private int mTextColor = Color.BLACK;

    public CircleScoreView(Context context) {
        this(context, null);
    }

    public CircleScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTheme(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CircleScoreView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initTheme(context, attrs, defStyleAttr);
        init();
    }

    private void initTheme(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleScoreView, defStyleAttr, 0);
        mOutSideCircleColor = typedArray.getColor(R.styleable.CircleScoreView_outsideCircleColor, Color.BLACK);
        mOutSideCircleWidth = (int) typedArray.getDimension(R.styleable.CircleScoreView_outsideCircleWidth, 2);
        mInnerCircleColor = typedArray.getColor(R.styleable.CircleScoreView_innerCircleColor, Color.BLACK);
        mInnerCircleWidth = (int) typedArray.getDimension(R.styleable.CircleScoreView_innerCircleWidth, 10);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleScoreView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        mTextColor = typedArray.getColor(R.styleable.CircleScoreView_textColor, Color.BLACK);
        mPadding = (int) typedArray.getDimension(R.styleable.CircleScoreView_padding, 10);
        mGapBetweenCircles = (int) typedArray.getDimension(R.styleable.CircleScoreView_gap, 20);
        typedArray.recycle();
    }

    private void init() {
        mOutsideCirclePaint = new Paint();
        mOutsideCirclePaint.setColor(mOutSideCircleColor);
        mOutsideCirclePaint.setStyle(Paint.Style.STROKE);
        mOutsideCirclePaint.setStrokeWidth(mOutSideCircleWidth);
        mOutsideCirclePaint.setAntiAlias(true);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setAntiAlias(true);
        mInnerCirclePaint.setStyle(Paint.Style.STROKE);
        mInnerCirclePaint.setStrokeWidth(mInnerCircleWidth);
        mInnerCirclePaint.setColor(mInnerCircleColor);
        mInnerCirclePaint.setDither(true);
        mInnerCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mInnerCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);

        mTextBound = new Rect();

        mStartSweepValue = -90;
        mCurrentAngle = 0;
        mCurrentPercent = 0;
        mTargetPercent = 100;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthValue = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightValue = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            //use the exactly values
            mWidth = widthValue;
            mHeight = heightValue;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            //make the height same as width
            mWidth = Math.min(widthValue, heightValue);
            mHeight = mWidth;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            //make the width same as height
            mHeight = Math.min(widthValue, heightValue);
            mWidth = mHeight;
        } else {
            //the default width/height is half width of the view
            mWidth = widthValue / 2;
            mHeight = mWidth;
        }

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw outside circle
        if (mOutSideCircleWidth > 0) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - mPadding - mOutSideCircleWidth / 2, mOutsideCirclePaint);
        }

        //draw text
        mText = String.valueOf(mCurrentPercent);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        canvas.drawText(mText, mWidth / 2 - mTextBound.width() / 2, mHeight / 2 + mTextBound.height() / 2, mTextPaint);

        //draw inner circle
        if (mInnerCircleRect == null) {
            mInnerCircleRect = new RectF(mPadding + mGapBetweenCircles + mInnerCircleWidth / 2, mHeight / 2 - mWidth / 2 + mPadding + mGapBetweenCircles + mInnerCircleWidth / 2, mWidth - mPadding - mGapBetweenCircles - mInnerCircleWidth / 2, mHeight / 2 + mWidth / 2 - mPadding - mGapBetweenCircles - mInnerCircleWidth / 2);
        }
        canvas.drawArc(mInnerCircleRect, mStartSweepValue, mCurrentAngle, false, mInnerCirclePaint);

        //check and update current state
        if (mCurrentPercent < mTargetPercent) {
            mCurrentPercent += 1;
            mCurrentAngle += 3.6;

            postInvalidateDelayed(10);
        }
    }

    public void setTargetPercent(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > 100) {
            value = 100;
        }
        this.mTargetPercent = value;
        this.mCurrentPercent = 0;
        this.mCurrentAngle = 0;
        postInvalidate();
    }
}
