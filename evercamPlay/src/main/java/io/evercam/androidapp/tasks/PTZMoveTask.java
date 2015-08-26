package io.evercam.androidapp.tasks;

import android.os.AsyncTask;

import io.evercam.androidapp.ptz.PTZControl;

public class PTZMoveTask extends AsyncTask<Void, Void, Void>
{
    private PTZControl ptzControl;

    public PTZMoveTask(PTZControl ptzControl)
    {
        this.ptzControl = ptzControl;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        ptzControl.move();
        return null;
    }

    public static void launch(PTZControl ptzControl)
    {
        new PTZMoveTask(ptzControl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
