/*
 * Copyright 2016 Chris Renke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisrenke.giv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.widget.ImageView.ScaleType.MATRIX;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A small {@link ImageView} variant that allows for {@code imageGravity} and {@code
 * imageScaleMode} to be defined for the image bitmap itself, instead of just the view relative to
 * its parent. Great for large "hero image" content.
 */
public class GravityImageView extends ImageView {

  public static final int BOTTOM            = 0b00000001;
  public static final int CENTER_VERTICAL   = 0b00000010;
  public static final int TOP               = 0b00000100;
  public static final int LEFT              = 0b00001000;
  public static final int CENTER_HORIZONTAL = 0b00010000;
  public static final int RIGHT             = 0b00100000;
  public static final int START             = 0b01000000;
  public static final int END               = 0b10000000;
  public static final int CENTER = CENTER_HORIZONTAL | CENTER_VERTICAL;

  @IntDef(flag = true,
      value = {
          LEFT, //
          CENTER_HORIZONTAL, //
          RIGHT, //
          START, //
          END, //
          BOTTOM, //
          CENTER_VERTICAL, //
          TOP, //
          CENTER
      }) //
  @Retention(RetentionPolicy.SOURCE)
  public @interface Gravity {
  }

  /**
   * No scaling will be applied to the source drawable.
   *
   * <pre>
   *      ┏━━━━━━┓
   *      ┃      ┃
   *      ┃ ┌──┐ ┃
   *      ┃ └──┘ ┃
   *      ┃      ┃
   *      ┗━━━━━━┛
   * </pre>
   */
  public static final int NONE = 1;

  /**
   * Source drawable will be scaled up so that 100% of the drawable is visible while still being as
   * large as possible within the bounds of the view. 100% of the view is not necessarily filled
   * with the source drawable.
   *
   * <pre>
   *      ┏━━━━━━┓
   *      ┌──────┐
   *      │      │
   *      │      │
   *      └──────┘
   *      ┗━━━━━━┛
   * </pre>
   */
  public static final int INSIDE = 2;

  /**
   * Source drawable will be scaled up so that 100% of the view is filled by the source drawable.
   * 100% of the drawable is not necessarily visible within the view's bounds.
   *
   * <pre>
   * ┌────┏━━━━━━┓────┐
   * │    ┃      ┃    │
   * │    ┃      ┃    │
   * │    ┃      ┃    │
   * │    ┃      ┃    │
   * └────┗━━━━━━┛────┘
   * </pre>
   */
  public static final int CROP = 3;

  @IntDef(flag = true,
      value = {
          NONE, //
          INSIDE, //
          CROP
      }) //
  @Retention(RetentionPolicy.SOURCE)
  public @interface ScaleMode {
  }

  private final Matrix matrix;
  private final RectF imageRect;
  private final boolean isRtl;

  private int imageGravity;
  private int imageScaleMode;

  public GravityImageView(Context context) {
    this(context, null);
  }

  public GravityImageView(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.gravityImageViewStyle);
  }

  public GravityImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    matrix = new Matrix();
    imageRect = new RectF();

    // By nature of this view, only MATRIX is supported. If ya want to use other
    // scaleTypes, use an ImageView.
    setScaleType(MATRIX);

    // Support for END/START.
    isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;

    // Early exit for whiziwiggin' it.
    if (isInEditMode()) return;

    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GravityImageView, //
        defStyleAttr, 0);
    imageScaleMode = a.getInt(R.styleable.GravityImageView_imageScaleMode, NONE);
    imageGravity = a.getInt(R.styleable.GravityImageView_imageGravity, CENTER);
    a.recycle();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    updateMatrix(w, h);
  }

  public void setImageGravity(@Gravity int gravityFlags) {
    this.imageGravity = gravityFlags;
    updateMatrix(getWidth(), getHeight());
  }

  public void setImageScaleMode(@ScaleMode int scaleModeFlags) {
    this.imageScaleMode = scaleModeFlags;
    updateMatrix(getWidth(), getHeight());
  }

  @Gravity public int getImageGravity() {
    return imageGravity;
  }

  @ScaleMode public int getImageScaleMode() {
    return imageScaleMode;
  }

  @Override public void setImageDrawable(Drawable drawable) {
    super.setImageDrawable(drawable);
    updateMatrix(getWidth(), getHeight());
  }

  private void updateMatrix(int viewWidth, int viewHeight) {
    Drawable drawable = getDrawable();
    // If we're in the constructor pass, ignore this for now.
    if (drawable == null || matrix == null) return;

    matrix.reset();
    imageRect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

    // Adjust the matrix to center the image and scale as needed.
    applyCenterAndScaleMode(viewWidth, viewHeight);

    // Adjust the matrix for the imageGravity position
    applyGravity(viewWidth, viewHeight);

    // Apply the matrix changes.
    setImageMatrix(matrix);
  }

  private void applyCenterAndScaleMode(float viewWidth, float viewHeight) {
    float centerX = viewWidth / 2;
    float centerY = viewHeight / 2;

    // Center the image in the middle of the view.
    matrix.postTranslate(centerX - (imageRect.width() / 2), centerY - (imageRect.height() / 2));

    // If there is nothing to scale, escape after positioning the image in the middle of the view.
    if (imageScaleMode == NONE) {
      return;
    }

    float widthRatio = viewWidth / imageRect.width();
    float heightRatio = viewHeight / imageRect.height();
    float scaleRatio = imageScaleMode == INSIDE //
        ? min(widthRatio, heightRatio) // Inside
        : max(widthRatio, heightRatio); // Crop

    // Scale the view up relative to the center point.
    matrix.postScale(scaleRatio, scaleRatio, centerX, centerY);
  }

  private void applyGravity(float viewWidth, float viewHeight) {
    // Apply the current matrix manipulations in order to get the current actual size of the image.
    imageRect.set(imageRect);
    matrix.mapRect(imageRect);

    float horizontalShiftFromCenter = (viewWidth / 2) - (imageRect.width() / 2);
    float verticalShiftFromCenter = (viewHeight / 2) - (imageRect.height() / 2);

    if ((imageGravity & LEFT) != 0 || (isRtl && (imageGravity & END) != 0)) {
      matrix.postTranslate(-horizontalShiftFromCenter, 0);
    } else if ((imageGravity & RIGHT) != 0 || (isRtl && (imageGravity & START) != 0)) {
      matrix.postTranslate(horizontalShiftFromCenter, 0);
    }

    if ((imageGravity & TOP) != 0) {
      matrix.postTranslate(0, -verticalShiftFromCenter);
    } else if ((imageGravity & BOTTOM) != 0) {
      matrix.postTranslate(0, verticalShiftFromCenter);
    }
  }
}
