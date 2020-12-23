package com.syan.agora;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.syan.agora.media.MediaDataAudioObserver;
import com.syan.agora.media.MediaDataVideoObserver;

import io.agora.mediaplayer.AgoraMediaPlayerKit;
import io.agora.mediaplayer.AudioFrameObserver;
import io.agora.mediaplayer.Constants;
import io.agora.mediaplayer.MediaPlayerObserver;
import io.agora.mediaplayer.data.AudioFrame;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static com.syan.agora.AgoraConst.AGWarning;
import static com.syan.agora.AgoraConst.AG_PREFIX;

import static com.syan.agora.AgoraConst.*;

//package io.agora.agoraplayer;


/**
 *
 */

public class AgoraMediaView extends SurfaceView implements SurfaceHolder.Callback{

    private AgoraMediaPlayerKit player;
    SurfaceHolder holder;
    private  Context mContext;
    private  Boolean mLoop = false;
    private MediaPlayerObserver mediaObserver;
    public AgoraMediaView(final Context context) {
        super(context);
        mContext = context;
        player = new AgoraMediaPlayerKit(context);
        holder=this.getHolder();
        holder.addCallback(this);
        player.setView(this);
        mediaObserver = new MediaPlayerObserver() {
            @Override
            public void onPlayerStateChanged(Constants.MediaPlayerState mediaPlayerState, Constants.MediaPlayerError mediaPlayerError) {
                if(mediaPlayerState == Constants.MediaPlayerState.PLAYER_STATE_OPEN_COMPLETED){
                    player.play();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WritableMap map = Arguments.createMap();
                            sendEvent((ReactContext)mContext, AGMediaPlayerStateOpenCompleted, map);
                        }
                    });
                } else if(mediaPlayerState == Constants.MediaPlayerState.PLAYER_STATE_PLAYBACK_COMPLETED){
                    //如果当前视频是循环播放 则调用play
                    if(mLoop) {
                        player.seek(0);
                        player.play();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WritableMap map = Arguments.createMap();
                                sendEvent((ReactContext)mContext, AGMediaPlayerStatePlayCompleted, map);
                            }
                        });
                    }

                } else if(mediaPlayerState == Constants.MediaPlayerState.PLAYER_STATE_STOPPED){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WritableMap map = Arguments.createMap();
                            sendEvent((ReactContext)mContext, AGMediaPlayerStateStoped, map);
                        }
                    });
                }
            }

            @Override
            public void onPositionChanged(final long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WritableMap map = Arguments.createMap();
                        map.putDouble("value", l);
                        sendEvent((ReactContext)mContext, AGMediaPlayerStateSeeking, map);
                    }
                });
            }

            @Override
            public void onPlayerEvent(Constants.MediaPlayerEvent mediaPlayerEvent) {

            }

            @Override
            public void onMetaData(Constants.MediaPlayerMetadataType mediaPlayerMetadataType, byte[] bytes) {

            }
        };
        player.registerPlayerObserver(mediaObserver);

    }

    public void vLoop(Boolean loop) { mLoop = loop;}

    public void vOpen(String uri){
        player.open(uri,0);
    }

    public void vRestart(){
        player.play();
    }

    public void vDestory(){
        if(mediaObserver != null) {
            player.unregisterPlayerObserver(mediaObserver);
        }
        player.setView(null);
        player.destroy();
        player = null;
    }

    public void vPause(){
        player.pause();
    }

    public void vSeek(long l){
        player.seek(l);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        player.destroy();
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        if(reactContext == null) return;
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName.toString(), params);
    }
}

