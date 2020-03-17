package com.example.nutritionixapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    ImageView foodImage;

    public DownloadImage(ImageView foodImage) {
        this.foodImage = foodImage;
    }

    protected Bitmap doInBackground(String[] urls){
        String urldispay = urls [0];
        Bitmap icon1 = null;

        try{
            InputStream in = new java.net.URL(urldispay).openStream();
            icon1 = BitmapFactory.decodeStream(in);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return icon1;
    }

    protected void onPostExecute(Bitmap result) {
        if(result==null){
            foodImage.setImageResource(R.drawable.image_not_found);
        }else {
            foodImage.setImageBitmap(result);
        }

    }
}
