package editmyphoto.anteprocess.com.editmyphoto.photoutils;

import android.graphics.Bitmap;

public class SaveSettings {
    private boolean isTransparencyEnabled;
    private boolean isClearViewsEnabled;

    boolean isTransparencyEnabled() {
        return isTransparencyEnabled;
    }

    boolean isClearViewsEnabled() {
        return isClearViewsEnabled;
    }

    private SaveSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
    }

    public static class Builder {
        private boolean isTransparencyEnabled = true;
        private boolean isClearViewsEnabled = true;

        /**
         * Define a flag to enable transparency while saving image
         *
         * @param transparencyEnabled true if enabled
         * @return Builder
         * @see BitmapUtil#removeTransparency(Bitmap)
         */
        public Builder setTransparencyEnabled(boolean transparencyEnabled) {
            isTransparencyEnabled = transparencyEnabled;
            return this;
        }

        /**
         * Define a flag to clear the view after saving the image
         *
         * @param clearViewsEnabled true if you want to clear all the views on {@link PhotoEditorView}
         * @return Builder
         */
        public Builder setClearViewsEnabled(boolean clearViewsEnabled) {
            isClearViewsEnabled = clearViewsEnabled;
            return this;
        }

        public SaveSettings build() {
            return new SaveSettings(this);
        }
    }
}
