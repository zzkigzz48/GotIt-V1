package com.example.gotit;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageInput;
    private final int GALLERY_REQUEST = 1001;
    private Bitmap selectedImage;
    private TessBaseAPI mTess;
    private Button mButtonRecognize;
    private TextView mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProps();
        setEvent();
        prepareData();
        dataInit();
    }

    public void prepareData() {
        try {
            File dir = new File(getFilesDir() + "/tessdata");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File trainedData = new File(getFilesDir() + "/tessdata/vie.traineddata");
            if(!trainedData.exists()) {
                Log.d("DMM ANDROID", "prepareData: ");
                AssetManager assetManager = getAssets();
                InputStream inputStream = assetManager.open("tessdata/vie.traineddata");
                OutputStream outputStream = new FileOutputStream(getFilesDir() + "/tessdata/vie.traineddata");
                byte[] buffer = new byte[1024];
                int read;
                while ((read=inputStream.read(buffer))!=-1) {
                    outputStream.write(buffer,0,read);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataInit() {
        mTess = new TessBaseAPI();
        mTess.init(getFilesDir() + "","vie");
        selectedImage = BitmapFactory.decodeResource(getResources(),R.drawable.capture1);
        mImageInput.setImageBitmap(selectedImage);
    }

    public void setProps() {
        mImageInput = findViewById(R.id.image_input);
        mButtonRecognize = findViewById(R.id.button_recognize);
        mResult = findViewById(R.id.result_area);
    }
    public void setEvent() {
        mImageInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
        mButtonRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTess.setImage(selectedImage);
                String result = mTess.getUTF8Text();
                mResult.setText(result);
            }
        });
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (reqCode) {
                case GALLERY_REQUEST :
                {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);
                        mImageInput.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }break;
            }
        }else {
        }
    }
}
