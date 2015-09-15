package io.evercam.androidapp.tasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import io.evercam.Camera;
import io.evercam.EvercamException;
import io.evercam.Model;
import io.evercam.PTZException;
import io.evercam.PTZPreset;
import io.evercam.androidapp.dto.EvercamCamera;
import io.evercam.androidapp.video.VideoActivity;

public class CheckOnvifTask extends AsyncTask<Void, Void, Boolean>
{
    private final String TAG = "CheckOnvifTask";
    private WeakReference<VideoActivity> videoActivityWeakReference;
    private String modelId;
    private String cameraId;

    public CheckOnvifTask(VideoActivity videoActivity, EvercamCamera camera)
    {
        videoActivityWeakReference = new WeakReference<>(videoActivity);
        this.modelId = camera.getModel().toLowerCase(Locale.UK);
        this.cameraId = camera.getCameraId();
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
                Camera camera = Camera.getById(cameraId, false);
                if(camera.getRights().isFullRight())
                {
                    ArrayList<PTZPreset> allPresets = PTZPreset.getAllPresets(cameraId);
                    getVideoActivity().presetList = allPresets;

                    ArrayList<PTZPreset> customPresets = removeSystemPresetsFrom(allPresets);

                    if(customPresets.size() > 0)
                    {
                        getVideoActivity().presetList = customPresets;
                    }

                    return true;
                }
            }
        }
        catch(EvercamException e)
        {
            e.printStackTrace();
        }
        catch(PTZException e)
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
            getVideoActivity().showPtzControl(hasPtz);
        }
    }

    private VideoActivity getVideoActivity()
    {
        return videoActivityWeakReference.get();
    }

    private ArrayList<PTZPreset> removeSystemPresetsFrom(ArrayList<PTZPreset> allPresets)
    {
        ArrayList<PTZPreset> customPresets = new ArrayList<>();
        if(allPresets.size() > 0)
        {
            //Exclude presets with token >= 33 and only keep those user defined presets
            for(PTZPreset preset : allPresets)
            {
                int tokenInt = Integer.valueOf(preset.getToken());
                if(tokenInt < 33)
                {
                    customPresets.add(preset);
                }
            }
        }
        return customPresets;
    }
}
