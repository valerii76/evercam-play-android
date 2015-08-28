package io.evercam.androidapp.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.evercam.EvercamException;
import io.evercam.Model;
import io.evercam.androidapp.video.VideoActivity;

public class CheckOnvifTask extends AsyncTask<Void, Void, Boolean>
{
    private final String TAG = "CheckOnvifTask";
    private WeakReference<VideoActivity> videoActivityWeakReference;
    private String modelId;

    public CheckOnvifTask(VideoActivity videoActivity, String modelId)
    {
        videoActivityWeakReference = new WeakReference<>(videoActivity);
        this.modelId = modelId;
    }

    @Override
    protected void onPreExecute()
    {
        getVideoActivity().isPtz = false;
    }

    @Override
    protected Boolean doInBackground(Void... params)
    {
        try
        {
            Model model = Model.getById(modelId);
            if(model.isOnvif() && model.isPTZ())
            {
                return  true;
            }
        }
        catch(EvercamException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean hasPtz)
    {
        if(getVideoActivity() != null)
        {
            getVideoActivity().isPtz = hasPtz;
        }
    }

    private VideoActivity getVideoActivity()
    {
        return videoActivityWeakReference.get();
    }
}
