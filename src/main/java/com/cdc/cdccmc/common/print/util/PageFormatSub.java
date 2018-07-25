package com.cdc.cdccmc.common.print.util;


import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * PageFormatSub
 * description:
 *
 * @author shichuang
 * @date 15-10-17
 */
public class PageFormatSub extends PageFormat implements Cloneable{

 /* Class Constants */

    /**
     *  The origin is at the bottom left of the paper with
     *  x running bottom to top and y running left to right.
     *  Note that this is not the Macintosh landscape but
     *  is the Window's and PostScript landscape.
     */
    public static final int LANDSCAPE = 0;

    /**
     *  The origin is at the top left of the paper with
     *  x running to the right and y running down the
     *  paper.
     */
    public static final int PORTRAIT = 1;

    /**
     *  The origin is at the top right of the paper with x
     *  running top to bottom and y running right to left.
     *  Note that this is the Macintosh landscape.
     */
    public static final int REVERSE_LANDSCAPE = 2;
    public static final int REVERSE_PORTRAIT = 3;

 /* Instance Variables */

    /**
     * A description of the physical piece of paper.
     */
    private Paper mPaper;

    /**
     * The orientation of the current page. This will be
     * one of the constants: PORTRIAT, LANDSCAPE, or
     * REVERSE_LANDSCAPE,
     */
    private int mOrientation = PORTRAIT;

 /* Constructors */

    /**
     * Creates a default, portrait-oriented
     * <code>PageFormat</code>.
     */
    public PageFormatSub()
    {
        mPaper = new Paper();
    }

 /* Instance Methods */

    /**
     * Makes a copy of this <code>PageFormat</code> with the same
     * contents as this <code>PageFormat</code>.
     * @return a copy of this <code>PageFormat</code>.
     */
    public Object clone() {
        PageFormatSub newPage;
        newPage = (PageFormatSub) super.clone();
        newPage.mPaper = (Paper)mPaper.clone();
        return newPage;
    }


    /**
     * Returns the width, in 1/72nds of an inch, of the page.
     * This method takes into account the orientation of the
     * page when determining the width.
     * @return the width of the page.
     */
    public double getWidth() {
        double width;
        int orientation = getOrientation();

        if (orientation == PORTRAIT || orientation == REVERSE_PORTRAIT) {
            width = mPaper.getWidth();//+200 modify 2015-10-26
        }else {
            width = mPaper.getHeight();
        }

        return width;
    }

    /**
     * Returns the height, in 1/72nds of an inch, of the page.
     * This method takes into account the orientation of the
     * page when determining the height.
     * @return the height of the page.
     */
    public double getHeight() {
        double height;
        int orientation = getOrientation();

        if (orientation == PORTRAIT || orientation == REVERSE_PORTRAIT) {
            height = mPaper.getHeight();
        } else {
            height = mPaper.getWidth();
        }

        return height;
    }

    /**
     * Returns the x coordinate of the upper left point of the
     * imageable area of the <code>Paper</code> object
     * associated with this <code>PageFormat</code>.
     * This method takes into account the
     * orientation of the page.
     * @return the x coordinate of the upper left point of the
     * imageable area of the <code>Paper</code> object
     * associated with this <code>PageFormat</code>.
     */
    public double getImageableX() {
        double x;

        switch (getOrientation()) {

            case LANDSCAPE:
                x = mPaper.getHeight()
                        - (mPaper.getImageableY() + mPaper.getImageableHeight());
                break;

            case PORTRAIT:
                x = mPaper.getImageableX();
                break;

            case REVERSE_LANDSCAPE:
                x = mPaper.getImageableY();
                break;
            case REVERSE_PORTRAIT:
                x = mPaper.getImageableX();
                break;

            default:
            /* This should never happen since it signifies that the
             * PageFormat is in an invalid orientation.
             */
                throw new InternalError("unrecognized orientation");

        }

        return x;
    }

    /**
     * Returns the y coordinate of the upper left point of the
     * imageable area of the <code>Paper</code> object
     * associated with this <code>PageFormat</code>.
     * This method takes into account the
     * orientation of the page.
     * @return the y coordinate of the upper left point of the
     * imageable area of the <code>Paper</code> object
     * associated with this <code>PageFormat</code>.
     */
    public double getImageableY() {
        double y;

        switch (getOrientation()) {

            case LANDSCAPE:
                y = mPaper.getImageableX();
                break;

            case PORTRAIT:
                y = mPaper.getImageableY();
                break;

            case REVERSE_LANDSCAPE:
                y = mPaper.getWidth()
                        - (mPaper.getImageableX() + mPaper.getImageableWidth());
                break;
            case REVERSE_PORTRAIT:
                y = mPaper.getImageableY();
                break;
            default:
            /* This should never happen since it signifies that the
             * PageFormat is in an invalid orientation.
             */
                throw new InternalError("unrecognized orientation");

        }

        return y;
    }

    /**
     * Returns the width, in 1/72nds of an inch, of the imageable
     * area of the page. This method takes into account the orientation
     * of the page.
     * @return the width of the page.
     */
    public double getImageableWidth() {
        double width;

        if (getOrientation() == PORTRAIT || getOrientation() == REVERSE_PORTRAIT) {
            width = mPaper.getImageableWidth();
        } else {
            width = mPaper.getImageableHeight();
        }

        return width;
    }

    /**
     * Return the height, in 1/72nds of an inch, of the imageable
     * area of the page. This method takes into account the orientation
     * of the page.
     * @return the height of the page.
     */
    public double getImageableHeight() {
        double height;

        if (getOrientation() == PORTRAIT  || getOrientation() == REVERSE_PORTRAIT) {
            height = mPaper.getImageableHeight();
        } else {
            height = mPaper.getImageableWidth();
        }

        return height;
    }


    /**
     * Returns a copy of the {@link Paper} object associated
     * with this <code>PageFormat</code>.  Changes made to the
     * <code>Paper</code> object returned from this method do not
     * affect the <code>Paper</code> object of this
     * <code>PageFormat</code>.  To update the <code>Paper</code>
     * object of this <code>PageFormat</code>, create a new
     * <code>Paper</code> object and set it into this
     * <code>PageFormat</code> by using the {@link #setPaper(Paper)}
     * method.
     * @return a copy of the <code>Paper</code> object associated
     *          with this <code>PageFormat</code>.
     * @see #setPaper
     */
    public Paper getPaper() {
        return (Paper)mPaper.clone();
    }

    /**
     * Sets the <code>Paper</code> object for this
     * <code>PageFormat</code>.
     * @param paper the <code>Paper</code> object to which to set
     * the <code>Paper</code> object for this <code>PageFormat</code>.
     * @exception <code>NullPointerException</code>
     *              a null paper instance was passed as a parameter.
     * @see #getPaper
     */
    public void setPaper(Paper paper) {
        mPaper = (Paper)paper.clone();
    }

    /**
     * Sets the page orientation. <code>orientation</code> must be
     * one of the constants: PORTRAIT, LANDSCAPE,
     * or REVERSE_LANDSCAPE.
     * @param orientation the new orientation for the page
     * @throws IllegalArgumentException if
     *          an unknown orientation was requested
     * @see #getOrientation
     */
    public void setOrientation(int orientation) throws IllegalArgumentException
    {
        if (0 <= orientation && orientation <= REVERSE_PORTRAIT) {
            mOrientation = orientation;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the orientation of this <code>PageFormat</code>.
     * @return this <code>PageFormat</code> object's orientation.
     * @see #setOrientation
     */
    public int getOrientation() {
        return mOrientation;
    }

    /**
     * Returns a transformation matrix that translates user
     * space rendering to the requested orientation
     * of the page.  The values are placed into the
     * array as
     * {&nbsp;m00,&nbsp;m10,&nbsp;m01,&nbsp;m11,&nbsp;m02,&nbsp;m12} in
     * the form required by the {@link java.awt.geom.AffineTransform}
     * constructor.
     * @return the matrix used to translate user space rendering
     * to the orientation of the page.
     * @see java.awt.geom.AffineTransform
     */

    public double[] getMatrix() {
        /**
         m00 - 3x3 矩阵缩放元素的 X 坐标
         m10 - 3x3 矩阵剪切元素的 Y 坐标
         m01 - 3x3 矩阵剪切元素的 X 坐标
         m11 - 3x3 矩阵缩放元素的 Y 坐标
         m02 - 3x3 矩阵平移元素的 X 坐标
         m12 - 3x3 矩阵平移元素的 Y 坐标
         */
        double[] matrix = new double[6];

        switch (mOrientation) {

            case LANDSCAPE:
                matrix[0] =  0;     matrix[1] = -1;
                matrix[2] =  1;     matrix[3] =  0;
                matrix[4] =  0;     matrix[5] =  mPaper.getHeight();
                break;

            case PORTRAIT:
                matrix[0] =  1;     matrix[1] =  0;
                matrix[2] =  0;     matrix[3] =  1;
                matrix[4] =  0;     matrix[5] =  0;
                break;

            case REVERSE_LANDSCAPE://  matrix[4] =  mPaper.getWidth();
                matrix[0] =  0;                     matrix[1] =  1;
                matrix[2] = -1;                     matrix[3] =  0;
                matrix[4] =  mPaper.getWidth();     matrix[5] =  0;
                break;
            case REVERSE_PORTRAIT:
                matrix[0] =  -1;     matrix[1] =  0;
                matrix[2] =  0;     matrix[3] =  -1;
                matrix[4] =  mPaper.getWidth();     matrix[5] =  mPaper.getHeight();
                break;

            default:
                throw new IllegalArgumentException();
        }

        return matrix;
    }
}