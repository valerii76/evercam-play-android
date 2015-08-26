package io.evercam.androidapp.ptz;

public class PTZBuilder
{
    private final String cameraId;
    private int relativeUp = 0;
    private int relativeDown = 0;
    private int relativeLeft = 0;
    private int relativeRight = 0;
    private int relativeZoom = 0;

    public PTZBuilder(String cameraId)
    {
        this.cameraId = cameraId;
    }

    public PTZBuilder relativeUp(int value)
    {
        relativeUp = value;
        return this;
    }

    public PTZBuilder relativeDown(int value)
    {
        relativeDown = value;
        return this;
    }

    public PTZBuilder relativeLeft(int value)
    {
        relativeLeft = value;
        return this;
    }

    public PTZBuilder relativeRight(int value)
    {
        relativeRight = value;
        return this;
    }

    public PTZBuilder relativeZoom(int value)
    {
        relativeZoom = value;
        return this;
    }

    public PTZRelative build()
    {
        return new PTZRelative(this);
    }

    public String getCameraId()
    {
        return cameraId;
    }

    public int getRelativeUp()
    {
        return relativeUp;
    }

    public int getRelativeDown()
    {
        return relativeDown;
    }

    public int getRelativeLeft()
    {
        return relativeLeft;
    }

    public int getRelativeRight()
    {
        return relativeRight;
    }

    public int getRelativeZoom()
    {
        return relativeZoom;
    }
}
