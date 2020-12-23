package com.syan.agora;

import android.os.Build;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DB on 2017/6/23.
 */

public class AgoraViewManager extends SimpleViewManager<AgoraVideoView> {

    public static final String REACT_CLASS = "RCTAgoraVideoView";

    public SurfaceView surfaceView;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AgoraVideoView createViewInstance(ThemedReactContext reactContext) {
        return new AgoraVideoView(reactContext);
    }

    @ReactProp(name = "mode")
    public void setRenderMode(final AgoraVideoView agoraVideoView, Integer renderMode) {
        agoraVideoView.setRenderMode(renderMode);
    }

    @ReactProp(name = "showLocalVideo")
    public void setShowLocalVideo(final AgoraVideoView agoraVideoView, boolean showLocalVideo) {
        agoraVideoView.setShowLocalVideo(showLocalVideo);
        if (showLocalVideo) {
            AgoraManager.getInstance().setupLocalVideo(agoraVideoView.getRenderMode());
            surfaceView = AgoraManager.getInstance().getLocalSurfaceView();
            surfaceView.setZOrderMediaOverlay(agoraVideoView.getZOrderMediaOverlay());
            agoraVideoView.addView(surfaceView);
        }
    }

    @ReactProp(name = "zOrderMediaOverlay")
    public void setZOrderMediaOverlay(final AgoraVideoView agoraVideoView, boolean zOrderMediaOverlay) {
        surfaceView.setZOrderMediaOverlay(zOrderMediaOverlay);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @ReactProp(name = "zOrderIndex")
    public void setOrderIndex(final AgoraVideoView agoraVideoView, int zOrderIndex) {
//        surfaceView.setZOrderMediaOverlay(zOrderIndex);
        Integer remoteUid = agoraVideoView.getRemoteUid();
        surfaceView  = agoraVideoView.findViewWithTag(remoteUid);
        if(surfaceView != null) {
            agoraVideoView.removeView(surfaceView);
            surfaceView.setZOrderOnTop(true);
            surfaceView.setZOrderMediaOverlay(true);
//            surfaceView.setZ(zOrderIndex);
            agoraVideoView.addView(surfaceView);
        }
    }

    @ReactProp(name = "remoteUid")
    public void setRemoteUid(final AgoraVideoView agoraVideoView, final int remoteUid) {
        agoraVideoView.setRemoteUid(remoteUid);
        if (remoteUid != 0) {
            AgoraManager.getInstance().setupRemoteVideo(remoteUid, agoraVideoView.getRenderMode());
            surfaceView = AgoraManager.getInstance().getSurfaceView(remoteUid);
            surfaceView.setZOrderMediaOverlay(agoraVideoView.getZOrderMediaOverlay());
            agoraVideoView.addView(surfaceView);
        }
    }

    //    JSONObject jsonObject = new JSONObject(videoIdSet);
//    roomId = jsonObject.getInteger("remoteUid");
//    roomPwd = jsonObject.getString("channelId");
    @ReactProp(name = "videoIdSet")
    public void setRemoteUid(final AgoraVideoView agoraVideoView, final  String videoIdSet) throws JSONException {
        if(agoraVideoView == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject(videoIdSet);
        int remoteUid = jsonObject.getInt("remoteUid");
        String channelId = jsonObject.getString("channelId");
        int originRemoteUid = agoraVideoView.getRemoteUid();
//        if(originRemoteUid != remoteUid) {
            agoraVideoView.setRemoteUid(remoteUid);
            if (remoteUid != 0) {
                AgoraManager.getInstance().setupRemoteVideo(remoteUid, channelId, agoraVideoView.getRenderMode());
                surfaceView = AgoraManager.getInstance().getSurfaceView(remoteUid);
                surfaceView.setZOrderMediaOverlay(agoraVideoView.getZOrderMediaOverlay());
                surfaceView.setTag(remoteUid);
                agoraVideoView.addView(surfaceView);
            }
//        }
    }

}
