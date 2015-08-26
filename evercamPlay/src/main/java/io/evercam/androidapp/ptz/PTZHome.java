package io.evercam.androidapp.ptz;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class PTZHome implements PTZControl
{
    private final String TAG = "PTZHome";

    private final String cameraId;

    public PTZHome(String cameraId)
    {
        this.cameraId = cameraId;
    }

    @Override
    public void move()
    {
        try
        {
            moveToHome();
        }
        catch(PTZException e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private boolean moveToHome() throws PTZException
    {
        String homeUrl = BASE_URL + cameraId + "/ptz/home";

        final OkHttpClient client = new OkHttpClient();

        try
        {
            Request request = new Request.Builder().url(homeUrl).post(new FormEncodingBuilder()
                    .add("dummyBody","").build()).build();

            Response response = client.newCall(request).execute();

            if(response.code() == CODE_OK)
            {
                return true;
            }
            else
            {
                throw new PTZException("Home move error with response code: " + response.code());
            }
        }
        catch(IOException e)
        {
            throw new PTZException(e);
        }
    }
}
