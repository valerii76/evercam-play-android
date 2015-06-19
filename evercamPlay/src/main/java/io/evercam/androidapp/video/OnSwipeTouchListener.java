package io.evercam.androidapp.video;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

public class OnSwipeTouchListener implements View.OnTouchListener
{
    private final String TAG = "OnSwipeTouchListener";
    private float lastX = -1;
    private float lastY = -1;
    private ScaleListener scaleListener;
    private ScaleGestureDetector gestureDetector;
    private long time = 0;
//    private int screenHeight;
//    private int screenWidth;

    public OnSwipeTouchListener(Activity activity)
    {
        scaleListener = new ScaleListener();
        gestureDetector = new ScaleGestureDetector(activity, scaleListener);

//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        screenWidth = size.x;
//        screenHeight = size.y;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if(gestureDetector != null)
        {
            gestureDetector.onTouchEvent(event);
        }

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                lastX = event.getX();
                lastY = event.getY();
                break;

            case MotionEvent.ACTION_UP:

                lastX = -1;
                lastY = -1;
                break;

            case MotionEvent.ACTION_MOVE:

                if(scaleListener != null && scaleListener.zoom)
                {
                    onActionZoom(view);
                }
                else if(!scaleListener.zoom)
                {
                    onActionMove(event, view);
                }
                lastX = event.getX();
                lastY = event.getY();

                break;
        }
        return true;
    }

    private void onActionZoom(View view)
    {
        int originalWidth = view.getWidth();
        int originalHeight = view.getHeight();

        long currentTime = System.nanoTime();
        if(time != 0 && (currentTime - time) > 10000000)
        {
            int leftOffset = (int) (originalWidth - (originalWidth * scaleListener.scaleFactor));
            int topOffset = (int) (originalHeight - (originalHeight * scaleListener.scaleFactor));
            Log.e(TAG, "Offset: " + leftOffset + "," + topOffset);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            layoutParams.setMargins(leftOffset, topOffset, leftOffset, topOffset);
            view.setLayoutParams(layoutParams);
        }

        time = System.nanoTime();
    }

    private void onActionMove(MotionEvent event, View view)
    {
        long currentTime = System.nanoTime();
        if(time != 0 && (currentTime - time) > 10000000)
        {
            if(lastX >= 0 && lastY > 0)
            {
                float newX = event.getX();
                float newY = event.getY();

                int xDiffInt = (int) (newX - lastX);
                int yDiffInt = (int) (newY - lastY);
                Log.d(TAG, "Swiping - Xdiff " + newX + " - " + lastX + " = " + xDiffInt + " Ydiff: " + newY + "-" + lastY + " = " + yDiffInt);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                        .getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin + xDiffInt, layoutParams
                        .topMargin + yDiffInt, layoutParams.rightMargin - xDiffInt, layoutParams.bottomMargin - yDiffInt);
                view.setLayoutParams(layoutParams);
            }
        }
        else
        {
            time = System.nanoTime();
        }

    }

    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        public static final float MIN_ZOOM = 1.0f;
        public static final float MAX_ZOOM = 1.5f;
        public float scaleFactor = 1.0f;
        public float originalScaleFactor = -1;
        public boolean zoom = false;

        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            originalScaleFactor = detector.getScaleFactor();
            scaleFactor *= originalScaleFactor;
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            Log.e(TAG, "Scale Factor = " + scaleFactor);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            //Log.e(TAG, "onScaleBegin");
            zoom = true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector)
        {
            //Log.e(TAG, "onScaleEnd");
            zoom = false;
        }

        public float getOriginalScaleFactor()
        {
            return originalScaleFactor;
        }
    }
}
