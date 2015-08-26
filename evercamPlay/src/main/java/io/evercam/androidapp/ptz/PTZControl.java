package io.evercam.androidapp.ptz;

public interface PTZControl
{
    int CODE_OK = 200;
    String BASE_URL = MediaApi.URL + "cameras/";

    void move();
}
