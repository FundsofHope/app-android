package org.fundsofhope.androidapp.activities;

/**
 * Created by Anip on 12/3/2015.
 */
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fundsofhope.androidapp.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class TempActivity extends Activity {

    private ImageView downloadedImg;
    private ProgressDialog simpleWaitDialog;
    private String downloadUrl = "https://upload.wikimedia.org/wikipedia/commons/f/f6/Tampilan_Google_Search_Indonesia.png";
    SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temp);
        Button imageDownloaderBtn = (Button) findViewById(R.id.downloadButton);
        new ImageDownloader().execute(downloadUrl);
        downloadedImg = (ImageView) findViewById(R.id.imageView);

        imageDownloaderBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //new ImageDownloader().execute(downloadUrl);
            }

        });
    }

    private class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... param) {
            return downloadBitmap(param[0]);
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
            simpleWaitDialog = ProgressDialog.show(TempActivity.this,
                    "Wait", "Downloading Image");

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            Log.i("Async-Example", "onPostExecute Called");
            //downloadedImg.setImageBitmap(result);
            simpleWaitDialog.dismiss();
            //System.out.println("image downloaded");
            Log.i("Async-Example", "image downloaded");
           Bitmap immage = result;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();

            String encoded= Base64.encodeToString(b, Base64.DEFAULT);
            Log.i("Async-Example", encoded);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor=pref.edit();
            editor.putString("image", encoded);
            editor.commit();
            //System.out.println("image stored");
            String fetch=pref.getString("image", "");
            byte[] decodedByte = Base64.decode(fetch, 0);
            System.out.println("image fetched");
            downloadedImg.setImageBitmap(BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length));


        }

        private Bitmap downloadBitmap(String url) {
            // initilize the default HTTP client object
            final DefaultHttpClient client = new DefaultHttpClient();

            //forming a HttoGet request
            final HttpGet getRequest = new HttpGet(url);
            try {

                HttpResponse response = client.execute(getRequest);

                //check 200 OK for success
                final int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode +
                            " while retrieving bitmap from " + url);
                    return null;

                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        // getting contents from the stream
                        inputStream = entity.getContent();

                        // decoding stream data back into image Bitmap that android understands
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // You Could provide a more explicit error message for IOException
                getRequest.abort();
                Log.e("ImageDownloader", "Something went wrong while" +
                        " retrieving bitmap from " + url + e.toString());
            }

            return null;
        }
    }
}
