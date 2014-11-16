package com.melnykov.letterimageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Random;

@SuppressWarnings({"UnusedDeclaration", "NullableProblems"})
public class LetterImageView extends ImageView {

    public static final int SHAPE_RECTANGLE = 0;
    public static final int SHAPE_OVAL = 1;

    private static String[] colorsArray;
    private static int defaultShape;

    private char mLetter;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private int mTextColor = Color.WHITE;
    private int shape;
    private Rect textBounds;
    private float textPadding = -1;
    private int shapeColor = -1;

    /**
     * Initialize default parameters.
     * @param context not null context
     * @param colorsArrayRes resource ID of string-array with colors
     * @param defaultShape default shape
     */
    public static void init(final Context context, int colorsArrayRes, int defaultShape) throws Resources.NotFoundException {
        LetterImageView.defaultShape = defaultShape;
        LetterImageView.colorsArray = context.getResources().getStringArray(colorsArrayRes);
    }

    public LetterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Init default values
     */
    private void init() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(shapeColor = randomColor());
        textBounds = new Rect();
        // Set a default padding to 8dp
        textPadding = 8 * getResources().getDisplayMetrics().density;
        shape = defaultShape;
    }

    /**
     * Get letter
     * @return letter
     */
    public char getLetter() {
        return mLetter;
    }

    /**
     * Set letter
     * @param letter letter
     */
    public void setLetter(char letter) {
        mLetter = letter;
        invalidate();
    }

    /**
     * Get text color
     * @return text color
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Set text color
     * @param textColor text color
     */
    public void setTextColor(int textColor) {
        mTextColor = textColor;
        invalidate();
    }

    /**
     * Set shape of image view.<br/>
     * Deprecated. Use {@link com.melnykov.letterimageview.LetterImageView#setShape(int) setShape} instead.
     */
    @Deprecated
    public void setOval(boolean oval) {
        if (oval)
            shape = SHAPE_OVAL;
        else
            shape = SHAPE_RECTANGLE;
    }

    /**
     * Get shape of image view
     * @return true if shape is oval
     * @see com.melnykov.letterimageview.LetterImageView#setShape(int)
     * @see com.melnykov.letterimageview.LetterImageView#getShape()
     */
    @Deprecated
    public boolean isOval() {
        return shape == SHAPE_OVAL;
    }

    /**
     * Set shape of image view
     * @param shape shape constant
     */
    public void setShape(int shape) {
        if (
                shape != SHAPE_RECTANGLE && // is rectangle
                shape != SHAPE_OVAL &&      // is oval
                !isCustomShape(shape)       // is custom shape
                )
            // no, throw an error
            throw new IllegalArgumentException("Shape value should be taken from constants");
        this.shape = shape;
    }

    /**
     * Get shape of image view
     * @return shape of image view
     */
    public int getShape() {
        return shape;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getDrawable() == null) {
            // Set a text font size based on the height of the view
            mTextPaint.setTextSize(canvas.getHeight() - getTextPadding(true));
            if (!drawShape(canvas)) {
                // if children can't draw shape, try to draw it on our own
                switch (shape) {
                    case SHAPE_RECTANGLE:
                        drawRectangle(canvas);
                        break;
                    case SHAPE_OVAL:
                        drawOval(canvas);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid shape value");
                }
            }
            // Measure a text
            mTextPaint.getTextBounds(String.valueOf(mLetter), 0, 1, textBounds);
            float textWidth = mTextPaint.measureText(String.valueOf(mLetter));
            float textHeight = textBounds.height();
            // Draw the text
            canvas.drawText(String.valueOf(mLetter), canvas.getWidth() / 2f - textWidth / 2f,
                    canvas.getHeight() / 2f + textHeight / 2f, mTextPaint);
        }
    }

    private void drawRectangle(final Canvas canvas) {
        canvas.drawRect(getPaddingLeft(), getPaddingTop(), canvas.getWidth() - getPaddingRight(),
                canvas.getHeight() - getPaddingBottom(), mBackgroundPaint);
    }

    private void drawOval(final Canvas canvas) {
        // check for padding
        final int minSize = Math.min(canvas.getWidth(), canvas.getHeight());
        final int padding;
        if (minSize == canvas.getWidth())
            padding = getPaddingRight() + getPaddingLeft();
        else
            padding = getPaddingTop() + getPaddingBottom();
        float radius = (minSize - padding) / 2f;
        canvas.drawCircle(canvas.getWidth() / 2f, canvas.getHeight() / 2f, radius, mBackgroundPaint);
    }

    /**
     * Get text padding
     * @param withImageViewPadding set true if need padding with image view's padding
     * @return text padding
     */
    public float getTextPadding(boolean withImageViewPadding) {
        float padding = textPadding;
        if (withImageViewPadding)
            padding += getPaddingTop() + getPaddingBottom();
        return padding;
    }

    /**
     * Set additional text padding
     * @param textPadding text padding
     */
    public void setTextPadding(float textPadding) {
        if (textPadding < 0)
            throw new IllegalArgumentException("Text padding must be greater or equals zero");
        this.textPadding = textPadding;
    }

    /**
     * Get shape color
     * @return shape color
     */
    public int getShapeColor() {
        return shapeColor;
    }

    /**
     * Set shape color
     * @param shapeColor shape color
     */
    public void setShapeColor(int shapeColor) {
        this.shapeColor = shapeColor;
        mBackgroundPaint.setColor(shapeColor);
        invalidate();
    }

    /**
     * Generate random color
     * @return random color or {@link Color#TRANSPARENT} if colors array not specified
     */
    private int randomColor() {
        if (colorsArray == null)
            return Color.TRANSPARENT;
        Random random = new Random();
        return Color.parseColor(colorsArray[random.nextInt(colorsArray.length)]);
    }

    /**
     * Draw custom shape. Place your custom implementation of drawShape here.
     * @param canvas canvas
     * @return true if shape was drawn
     */
    protected boolean drawShape(Canvas canvas) {
        return false;
    }

    /**
     * Check if specified shape can be drawn
     * @param shape shape constant
     * @return true if shape can be drawn
     */
    protected boolean isCustomShape(int shape) {
        return false;
    }
}