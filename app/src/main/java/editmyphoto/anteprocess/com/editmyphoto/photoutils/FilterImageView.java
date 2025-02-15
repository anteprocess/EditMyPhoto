package editmyphoto.anteprocess.com.editmyphoto.photoutils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

class FilterImageView extends AppCompatImageView {

    private OnImageChangedListener mOnImageChangedListener;

    public FilterImageView(Context context) {
        super(context);
    }

    public FilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnImageChangedListener(OnImageChangedListener onImageChangedListener) {
        mOnImageChangedListener = onImageChangedListener;
    }

    interface OnImageChangedListener {
        void onBitmapLoaded(@Nullable Bitmap sourceBitmap);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageIcon(@Nullable Icon icon) {
        super.setImageIcon(icon);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageState(int[] state, boolean merge) {
        super.setImageState(state, merge);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageTintList(@Nullable ColorStateList tint) {
        super.setImageTintList(tint);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageTintMode(@Nullable PorterDuff.Mode tintMode) {
        super.setImageTintMode(tintMode);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Override
    public void setImageLevel(int level) {
        super.setImageLevel(level);
        if (mOnImageChangedListener != null) {
            mOnImageChangedListener.onBitmapLoaded(getBitmap());
        }
    }

    @Nullable
    Bitmap getBitmap() {
        if (getDrawable() != null) {
            return ((BitmapDrawable) getDrawable()).getBitmap();
        }
        return null;
    }
}
