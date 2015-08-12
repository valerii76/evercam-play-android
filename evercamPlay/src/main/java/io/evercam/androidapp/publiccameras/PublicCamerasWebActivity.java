package io.evercam.androidapp.publiccameras;

import android.os.Bundle;

import io.evercam.androidapp.R;
import io.evercam.androidapp.WebActivity;
import io.evercam.androidapp.utils.Constants;

public class PublicCamerasWebActivity extends WebActivity
{
    private final String TAG = "PublicCamerasWebActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_public_cameras);

        loadPage();
    }

    @Override
    protected void loadPage()
    {
        PublicCamerasWebView webView = (PublicCamerasWebView) findViewById(R.id.public_cameras_webview);
        webView.webActivity = this;
        webView.loadPublicCameras();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        setResult(Constants.RESULT_TRUE);
    }
}
