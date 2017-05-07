package com.iot.elca.Activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marcos.elca.R;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageRecognitionActivity extends AppCompatActivity {
    public VisionServiceClient visionServiceClient = new VisionServiceRestClient("84f2942359dd4dfd90d52e22894c6115");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ferro1);
        //final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //imageView.setImageBitmap(mBitmap);


        Button btnProcess = (Button) findViewById(R.id.btnEmotion); // Acha o botão que inicia o processo de reconhecimento
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<InputStream, String, String> describeTask = new AsyncTask<InputStream, String, String>() {
                    ProgressDialog mDialog = new ProgressDialog(ImageRecognitionActivity.this);

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }

                    @Override
                    protected String doInBackground(InputStream... params) {

                        Gson gson = new Gson();
                        String[] features = {"Tags"};
                        String[] details = {};
                        publishProgress("Recognizing...");
                        AnalysisResult v = null;
                        try {
                            v = visionServiceClient.analyzeImage(inputStream, features, details);
                        } catch (VisionServiceException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String result = gson.toJson(v);
                        return result;
                    }
                };
                describeTask.execute(inputStream);
            }
        });


    }
}
