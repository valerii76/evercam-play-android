package io.evercam.androidapp.ptz;

import java.io.IOException;

public class PTZException extends Exception
{
    public PTZException(IOException e)
    {
        super(e);
    }

    public  PTZException(String message)
    {
        super(message);
    }
}
