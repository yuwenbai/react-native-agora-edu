package com.syan.agora;

import android.os.Build;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.json.JSONException;
import org.json.JSONObject;

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

public class AograMediaManager extends SimpleViewManager<AgoraMediaView> {

    public static final String REACT_CLASS = "RCTAgoraMediaView";

    public SurfaceView surfaceView;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AgoraMediaView createViewInstance(ThemedReactContext reactContext) {
        return new AgoraMediaView(reactContext);
    }

    @ReactProp(name = "videoIdSet")
    public void setRemoteUid(final AgoraMediaView agoraVideoView, final String videoIdSet) throws JSONException {
        JSONObject jsonObject = new JSONObject(videoIdSet);
        String url  = jsonObject.getString("url");
        Boolean loop = jsonObject.getBoolean("loop");
        agoraVideoView.vLoop(loop);
        agoraVideoView.vOpen(url);
    }

    @ReactProp(name = "paused")
    public void setPaused(final AgoraMediaView agoraVideoView, final String paused){
        Long lv = Long.parseLong(paused);
        if(lv == 1){
            agoraVideoView.vPause();
        } else {
            agoraVideoView.vRestart();
        }
    }

    @ReactProp(name = "seek")
    public void setSeek(final AgoraMediaView agoraVideoView, final String value){
        Long lv = Long.parseLong(value);
        if(lv > 0){
            agoraVideoView.vSeek(lv);
        }
    }

    @ReactProp(name = "destory")
    public void setDestory(final AgoraMediaView agoraVideoView, final String value){
        if(value.equals("true")){
            agoraVideoView.vDestory();
        }
    }



//    //    JSONObject jsonObject = new JSONObject(videoIdSet);
////    roomId = jsonObject.getInteger("remoteUid");
////    roomPwd = jsonObject.getString("channelId");
//    @ReactProp(name = "videoIdSet")
//    public void setRemoteUid(final AgoraVideoView agoraVideoView, final  String videoIdSet) throws JSONException {
//        if(agoraVideoView == null) {
//            return;
//        }
//        JSONObject jsonObject = new JSONObject(videoIdSet);
//        int remoteUid = jsonObject.getInt("remoteUid");
//        String channelId = jsonObject.getString("channelId");
//        int originRemoteUid = agoraVideoView.getRemoteUid();
////        if(originRemoteUid != remoteUid) {
//        agoraVideoView.setRemoteUid(remoteUid);
//        if (remoteUid != 0) {
//            AgoraManager.getInstance().setupRemoteVideo(remoteUid, channelId, agoraVideoView.getRenderMode());
//            surfaceView = AgoraManager.getInstance().getSurfaceView(remoteUid);
//            surfaceView.setZOrderMediaOverlay(agoraVideoView.getZOrderMediaOverlay());
//            surfaceView.setTag(remoteUid);
//            agoraVideoView.addView(surfaceView);
//        }
////        }
//    }

}
