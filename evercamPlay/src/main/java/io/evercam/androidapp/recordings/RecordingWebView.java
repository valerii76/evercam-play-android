package io.evercam.androidapp.recordings;

import android.content.Context;
import android.util.AttributeSet;

import io.evercam.API;
import io.evercam.androidapp.BaseWebView;
import io.evercam.androidapp.WebActivity;

public class RecordingWebView extends BaseWebView
{
    private final String TAG = "RecordingWebView";
    public WebActivity webActivity;

    public RecordingWebView(Context context)
    {
        super(context);
    }

    public RecordingWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void loadRecordingWidget(String cameraId)
    {
        enableJavascript(true);
        enableChromeDebugging();

        setWebViewClient(webActivity.getWebViewClient());

        //TODO remove <body style='margin:0;padding:0;'>, it's here only to overwrite the widget
        // margin
        //private=false is ignored because it's pre-authenticated
        String customHtml = "<html><body style='margin:0;padding:0;'><div " +
                "evercam=\"snapshot-navigator" +
                "\"></div><script type=\"text/javascript\" src=\"https://dashboard" +
                ".evercam.io/snapshot.navigator.js?camera=" + cameraId + "&private=false&api_id="
                + API.getUserKeyPair()[1] +
                "&api_key=" + API.getUserKeyPair()[0] +
                "\"></script></body></html>";

        loadData(customHtml, "text/html", "UTF-8");
    }

}
