package com.sharma.dhruv.dijkstrasalgo;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ShortestDist extends AppCompatActivity {
    public int mode = 0, pux = 0, puy = 0, dox = 0, doy = 0;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortest_dist);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.imageView);


        // Set up the user interaction to manually show or hide the system UI.
        main(this);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    public void main(final ShortestDist shortestDist) {
        int btrack[][] = new int [][]{{0,0,0,0,0,0,0}, {0,0,0,0,0,0,0}, {0,1,1,1,1,1,0}, {0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0}, {0,1,1,1,1,1,0}, {0,1,0,0,0,1,0}, {0,1,0,0,0,1,0}, {0,1,0,0,0,1,0},
                {0,1,0,0,0,1,0}, {0,1,0,0,0,1,0}, {0,1,1,1,1,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,1,1,1,1,0}, {0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,1,1,0}, {0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0},
                {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,0,1,0,1,0}, {0,1,1,1,1,1,0},
                {0,1,0,0,0,1,0}, {0,1,0,0,0,1,0}, {0,1,0,0,0,1,0}, {0,1,0,0,0,1,0}, {0,1,1,1,1,1,0},
                {0,0,0,0,0,0,0}};
        ImageView track = (ImageView) shortestDist.findViewById(R.id.imageView);
        int ih = track.getHeight();
        int iw = track.getWidth();
        ih = ih/45;
        iw = iw/7;

        if(mode < 2) {
            track.setOnTouchListener( new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        int tx = (int) event.getX();
                        int ty = (int) event.getY();

                        drop(tx, ty);

                        return true;
                    }
                    return false;
                }

            });
        }
        pux = pux/iw;
        puy = puy/ih;
        dox = dox/iw;
        doy = doy/ih;

        if(btrack[pux][puy] != 1){
            //for(int a = pux; a > 0; a--)
        }

    }

    private void drop(int px, int py) {
        TextView put = (TextView) findViewById(R.id.pick_up);
        TextView dot = (TextView) findViewById(R.id.drop_off);
        ImageView iv = new ImageView(getApplicationContext());
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        if(mode == 0) {
            iv.setImageDrawable(getDrawable(R.drawable.pick_up));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100 , 100);
            lp.setMargins((px-50), (py-100), 0, 0);
            iv.setLayoutParams(lp);
            rl.addView(iv);
            pux = px; puy = py;
            put.setVisibility(View.INVISIBLE);
            dot.setVisibility(View.VISIBLE);
            mode++;
        }
        else if(mode == 1) {

            iv.setImageDrawable(getDrawable(R.drawable.drop_off));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(100, 100);
            lp.setMargins((px-50), (py-100), 0, 0);
            iv.setLayoutParams(lp);
            rl.addView(iv);
            dox = px; doy = py;
            put.setVisibility(View.INVISIBLE);
            dot.setVisibility(View.INVISIBLE);
            mode++;
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
