package io.evercam.androidapp.ptz;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class PTZRelative implements PTZControl
{
    private final String TAG = "PTZRelative";

    private final String cameraId;
    private final int relativeLeft;
    private final int relativeRight;
    private final int relativeUp;
    private final int relativeDown;
    private final int relativeZoom;

    public PTZRelative(PTZBuilder builder)
    {
        this.cameraId = builder.getCameraId();
        relativeLeft = builder.getRelativeLeft();
        relativeRight = builder.getRelativeRight();
        relativeUp = builder.getRelativeUp();
        relativeDown = builder.getRelativeDown();
        relativeZoom = builder.getRelativeZoom();
    }

    @Override
    public void move()
    {
        try
        {
            relativeMove(relativeLeft, relativeRight, relativeUp, relativeDown, relativeZoom);
        }
        catch(PTZException e)
        {
            Log.e(TAG, e.toString());
        }
    }

    private boolean relativeMove(int left, int right, int up, int down, int zoom) throws PTZException
    {
        String relativeMoveUrl = BASE_URL + cameraId + "/ptz/relative?" + "left=" + left + "&right=" +
                right + "&up=" + up + "&down=" + down + "&zoom=" + zoom;

        Log.e(TAG, relativeMoveUrl);

        final OkHttpClient client = new OkHttpClient();

        try
        {
            Request request = new Request.Builder().url(relativeMoveUrl).post(new
                    FormEncodingBuilder().add("dummyBody","").build()).build();

            Response response = client.newCall(request).execute();

            if(response.code() == CODE_OK)
            {
                return true;
            }
            else
            {
                throw new PTZException("Relative move error with response code: " + response.code());
            }
        }
        catch(IOException e)
        {
            throw new PTZException(e);
        }
    }
}
