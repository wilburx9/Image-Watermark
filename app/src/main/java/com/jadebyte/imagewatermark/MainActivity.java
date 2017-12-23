package com.jadebyte.imagewatermark;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final int IMAGE_REQUEST_CODE = 1111;
    private final int WATERMARK_REQUEST_CODE = 2222;


    private ImageView ivResult, ivSaveResult, ivImgPlaceholder, ivWatermarkPlaceholder;
    private Button btnSelectImg, btnSelectWatermark, btnWatermark;
    private EditText etPercentage, etSize, etMargin;
    private RadioGroup rgPosVer, rgPosHor;
    private CheckBox cbPercentage;


    private Bitmap imageBitmap;
    private Bitmap watermarkBitmap;
    private Bitmap resultBitmap;
    private String currentPercent;
    private String currentSize;
    private String currentMargin;
    private WaterMarkTask waterMarkTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        setClickListeners();
        setupEditTexts();
    }

    private void findViewsById() {
        ivResult = findViewById(R.id.result_image);
        ivSaveResult = findViewById(R.id.save_result_image);
        ivImgPlaceholder = findViewById(R.id.img_placeholder);
        ivWatermarkPlaceholder = findViewById(R.id.watermark_placeholder);
        btnSelectImg = findViewById(R.id.select_img_btn);
        btnSelectWatermark = findViewById(R.id.select_watermark_btn);
        btnWatermark = findViewById(R.id.watermark_btn);
        etPercentage = findViewById(R.id.percentage_edit_text);
        etSize = findViewById(R.id.size_edit_text);
        etMargin = findViewById(R.id.margin_edit_text);
        rgPosVer = findViewById(R.id.position_radio_group_vertical);
        rgPosHor = findViewById(R.id.position_radio_group_horizontal);
        cbPercentage = findViewById(R.id.percentage_checkbox);
    }

    private void setClickListeners() {
        ivSaveResult.setOnClickListener(this);
        btnSelectImg.setOnClickListener(this);
        btnSelectWatermark.setOnClickListener(this);
        btnWatermark.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_img_btn:
                chooseImage(IMAGE_REQUEST_CODE, getString(R.string.select_image));
                break;
            case R.id.select_watermark_btn:
                chooseImage(WATERMARK_REQUEST_CODE, getString(R.string.select_watermark));
                break;
            case R.id.watermark_btn:
                generateWatermarkedImg();
                break;
            case R.id.save_result_image:
                saveResultWatermark();
                break;
        }
    }

    private void setupEditTexts() {
        etPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentPercent)) {
                    etPercentage.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[%,.]", "");
                    double parsed;
                    if (!TextUtils.isEmpty(cleanString)) {
                        parsed = Double.parseDouble(cleanString);

                    } else {
                        parsed = 1;
                    }

                    final NumberFormat percentFormat = NumberFormat.getPercentInstance();

                    String formatted = percentFormat.format(parsed/100);
                    currentPercent = formatted;
                    etPercentage.setText(formatted);
                    etPercentage.setSelection(formatted.length()-1);
                    etPercentage.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().equals(currentSize)) {
                    currentSize = pxFormatText(this, s, etSize);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etMargin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (!s.toString().equals(currentMargin)) {
                    currentMargin = pxFormatText(this, s, etMargin);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        cbPercentage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                etPercentage.setEnabled(isChecked);
                etSize.setEnabled(!isChecked);
            }
        });

        etPercentage.setText("20");
        etMargin.setText("40");
        etSize.setText("200");
    }

    private String pxFormatText(TextWatcher textWatcher, CharSequence s, EditText editText) {
        editText.removeTextChangedListener(textWatcher);

        String cleanString = s.toString().replaceAll("[px,.]", "");
        double parsed;
        if (!TextUtils.isEmpty(cleanString)) {
            parsed = Double.parseDouble(cleanString);
            if (parsed == 0) {
                parsed = 1;
            }

        } else {
            parsed = 1;
        }

        final NumberFormat percentFormat = NumberFormat.getNumberInstance();

        String formatted = percentFormat.format(parsed).concat("px");
        editText.setText(formatted);
        editText.setSelection(formatted.length()-2);
        editText.addTextChangedListener(textWatcher);
        return formatted;
    }

    private void generateWatermarkedImg() {
        if (imageBitmap == null) {
            Toast.makeText(this, R.string.select_image_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (watermarkBitmap == null) {
            Toast.makeText(this, R.string.select_watermark_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.generating_watermark, Toast.LENGTH_SHORT).show();
        waterMarkTask = new WaterMarkTask();
        waterMarkTask.execute();
    }

    private void saveResultWatermark() {
        if (resultBitmap == null) {
            Toast.makeText(this, R.string.generate_watermarked_img_first, Toast.LENGTH_SHORT).show();
            return;
        }


        if (MyPermission.isWriteExtStorPermGranted(this)) {
            Utils.saveImageFile(resultBitmap, this, findViewById(R.id.root_view));
        } else {
            MyPermission.askWriteExtStorPerm(this);
        }

    }

    private void chooseImage(int requestCode, String title) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, title), requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ivImgPlaceholder.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == WATERMARK_REQUEST_CODE) {
                try {
                    watermarkBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ivWatermarkPlaceholder.setImageBitmap(watermarkBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class WaterMarkTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            resultBitmap = bitmap;
            ivResult.setImageBitmap(resultBitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            int margin = Integer.parseInt(currentMargin.replaceAll("[^\\d.]", "")); // Removing non-numerical characters
            final Bitmap mutableBitmap = Utils.getMutableBitmap(imageBitmap); // Make the bitmap mutable so it can be written to
            int size = getWatermarkSize(mutableBitmap) - margin;
            Bitmap waterMark = Utils.getResizedBitmap(watermarkBitmap, size, size);

            Canvas canvas = new Canvas(mutableBitmap);
            int left = getHorizontalPos(mutableBitmap, waterMark, margin);
            int top = getVerticalPos(mutableBitmap, waterMark, margin);
            canvas.drawBitmap(waterMark, left, top, null);
            return mutableBitmap;
        }
    }

    private int getWatermarkSize(Bitmap bitmap) {
        int waterMarkSize;
        int smallestSideOfImg = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (cbPercentage.isChecked()) {
            int percentNum = Integer.parseInt(currentPercent.replaceAll("[^\\d.]", ""));
            waterMarkSize = (int) (smallestSideOfImg * (percentNum / 100.0f));
            // waterMarkSize is percentNum percentage of smallestSideOfImg
        } else {
            waterMarkSize = Integer.parseInt(currentSize.replaceAll("[^\\d.]", ""));
        }

        if (waterMarkSize >= smallestSideOfImg) {
            // Making the watermark size 20px less than the main image size if the width of the watermark is >= the smallest
            // side of the main image. This way, the width of the watermark can never be bigger that of the image
            waterMarkSize = smallestSideOfImg - 20;
        }
        return waterMarkSize;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MyPermission.WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveResultWatermark();
                } else {
                    Toast.makeText(this, R.string.no_storage_access, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private int getVerticalPos(Bitmap bitmap, Bitmap watermark, int margin) {
        int position;
        int checkedPos = rgPosVer.getCheckedRadioButtonId();
        switch (checkedPos) {
            case R.id.bottom:
                position = bitmap.getHeight() - (watermark.getHeight() + margin);
                break;
            case R.id.centre_ver:
                position = bitmap.getHeight() / 2 - (watermark.getHeight() / 2);
                break;
            default: //top
                position = margin;
        }

        return position;
    }

    private int getHorizontalPos(Bitmap bitmap, Bitmap watermark, int margin) {
        int position;
        int checkedPos = rgPosHor.getCheckedRadioButtonId();
        switch (checkedPos) {
            case R.id.right:
                position = bitmap.getWidth() - (watermark.getWidth() + margin);
                break;
            case R.id.centre_hor:
                position = bitmap.getWidth() / 2 - (watermark.getWidth() / 2);
                break;
            default: // left
                position = margin;
        }

        return position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancelling pending tasks
        if (waterMarkTask != null && waterMarkTask.getStatus() == AsyncTask.Status.RUNNING) {
            waterMarkTask.cancel(true);
        }
    }
}
