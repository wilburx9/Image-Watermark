package com.jadebyte.imagewatermark;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wilberforce on 11/27/17 at 9:20 PM.
 */

public class Utils {
    public static void saveImageFile(Bitmap bitmap, Context cxt, View rootV) {
        Context context = new WeakReference<>(cxt).get();
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String imageFileName = "Watermark_" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(),
                "Watermark");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, imageFileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            boolean isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            if (isCompressed) {
                refreshMedia(imageFile, context);
                showSavedSnackBar(imageFile, new WeakReference<>(rootV).get(), context);

            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.error_saving_img, Toast.LENGTH_SHORT).show();
        }

    }

    // Updating the MediaStore so the saved image will show in the Gallery app
    private static void refreshMedia(final File imageFile, Context context) {
        MediaScannerConnection.scanFile(context,
                new String[]{imageFile.getAbsolutePath()}, null, null);
    }

    private static void showSavedSnackBar(final File imageFile, View view, final Context context) {
        Snackbar snackbar = Snackbar.make(view, context.getString(R.string.file_saved, imageFile
                .getName()), Snackbar.LENGTH_LONG)
                .setAction(R.string.open, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyPermission.isExternalStorageReadable()) {
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(context, context.getString(R.string.provider), imageFile);
                            } else {
                                uri = Uri.fromFile(imageFile);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(uri, "image/*");
                            try {
                                context.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, R.string.image_viewer_no, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, R.string.storage_not_accessible, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        View snackBarView = snackbar.getView();
        TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        snackbar.show();
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
    }

    public static Bitmap getMutableBitmap(Bitmap immutableBitmap) {
        if (immutableBitmap.isMutable()) {
            return immutableBitmap;
        }

        Bitmap workingBitmap = Bitmap.createBitmap(immutableBitmap);
        return workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }


}
