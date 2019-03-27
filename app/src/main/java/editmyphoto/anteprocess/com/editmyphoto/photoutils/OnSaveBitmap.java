package editmyphoto.anteprocess.com.editmyphoto.photoutils;

import android.graphics.Bitmap;


public interface OnSaveBitmap {
    void onBitmapReady(Bitmap saveBitmap);

    void onFailure(Exception e);
}
