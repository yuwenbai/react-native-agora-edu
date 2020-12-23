package com.syan.agora;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtc.IMetadataObserver;
import io.agora.rtc.IRtcChannelEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcChannel;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcEngineEx;
import io.agora.rtc.models.ChannelMediaOptions;
import io.agora.rtc.video.BeautyOptions;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static io.agora.rtc.video.VideoEncoderConfiguration.*;

/**
 * Created by Leon on 2017/4/9.
 */

public class AgoraManager {

    public String token = "006fae45cd7c4324e468ba10e54fce090c3IAAA9+5JzuBqgIWY4v+/BRz8f7IRLVGN5RscXpwD7NfA3uP7EAcAAAAAIgBNxwAAgHjDXgQAAQDAz8JeAwDAz8JeAgDAz8JeBADAz8Je";
    public static AgoraManager sAgoraManager;

    public RtcEngineEx mRtcEngine;
    public RtcChannel mRtcChannel;

    private Context context;

    private int mLocalUid = 0;

    private HashMap<String, RtcChannel> mRtcChannels;

    private AgoraManager() {
        mSurfaceViews = new SparseArray<SurfaceView>();
        mRtcChannels = new HashMap<String,RtcChannel>() ;  // key: channelId, object RtcChannel
    }

    private SparseArray<SurfaceView> mSurfaceViews;

    public static AgoraManager getInstance() {
        if (sAgoraManager == null) {
            synchronized (AgoraManager.class) {
                if (sAgoraManager == null) {
                    sAgoraManager = new AgoraManager();
                }
            }
        }
        return sAgoraManager;
    }

    private FRAME_RATE getVideoEncoderEnum (int val) {
        FRAME_RATE type = FRAME_RATE.FRAME_RATE_FPS_1;
        switch (val) {
            case 1:
                type = FRAME_RATE.FRAME_RATE_FPS_1;
                break;
            case 7:
                type = FRAME_RATE.FRAME_RATE_FPS_7;
                break;
            case 10:
                type = FRAME_RATE.FRAME_RATE_FPS_10;
                break;
            case 15:
                type = FRAME_RATE.FRAME_RATE_FPS_15;
                break;
            case 24:
                type = FRAME_RATE.FRAME_RATE_FPS_24;
                break;
            case 30:
                type = FRAME_RATE.FRAME_RATE_FPS_30;
                break;
        }
        return type;
    }

    private ORIENTATION_MODE getOrientationModeEnum (int val) {
        ORIENTATION_MODE type = ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
        switch (val) {
            case 0:
                type = ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE;
                break;
            case 1:
                type = ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE;
                break;
            case 2:
                type = ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT;
                break;
        }
        return type;
    }

    /**
     * initialize rtc engine
     */
    public int init(Context context, IRtcEngineEventHandler mRtcEventHandler, ReadableMap options) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            this.context = context;
            this.mRtcEngine = (RtcEngineEx) RtcEngineEx.create(context, options.getString("appid"), mRtcEventHandler);
            this.mRtcEngine.setAppType(8);
            if (options.hasKey("secret") && null != options.getString("secret")) {
                mRtcEngine.setEncryptionSecret(options.getString("secret"));
                if (options.hasKey("secretMode") && null != options.getString("secretMode")) {
                    mRtcEngine.setEncryptionMode(options.getString("secretMode"));
                }
            }
            if (options.hasKey("channelProfile")) {
                mRtcEngine.setChannelProfile(options.getInt("channelProfile"));
            }
            if (options.hasKey("dualStream")) {
                mRtcEngine.enableDualStreamMode(options.getBoolean("dualStream"));
            }
            if (options.hasKey("mode")) {
                Integer mode = options.getInt("mode");
                switch (mode) {
                    case 0: {
                        mRtcEngine.enableAudio();
                        mRtcEngine.disableVideo();
                        break;
                    }
                    case 1: {
                        mRtcEngine.enableVideo();
                        mRtcEngine.disableAudio();
                        break;
                    }
                }
            } else {
                mRtcEngine.enableVideo();
                mRtcEngine.enableAudio();
            }

            if (options.hasKey("beauty") && null != options.getMap("beauty")) {
                ReadableMap beauty = options.getMap("beauty");
                BeautyOptions beautyOption = new BeautyOptions();
                beautyOption.lighteningContrastLevel = beauty.getInt("lighteningContrastLevel");
                beautyOption.lighteningLevel = (float) beauty.getDouble("lighteningLevel");
                beautyOption.smoothnessLevel = (float) beauty.getDouble("smoothnessLevel");
                beautyOption.rednessLevel = (float) beauty.getDouble("rednessLevel");
                mRtcEngine.setBeautyEffectOptions(true, beautyOption);
            }

            if (options.hasKey("voice") && null != options.getMap("voice")) {
                ReadableMap voice = options.getMap("voice");
                final String voiceType = voice.getString("type");
                final Integer voiceValue = voice.getInt("value");
                if (voiceType.equals("changer")) {
                    mRtcEngine.setLocalVoiceChanger(voiceValue);
                }
                if (voiceType.equals("reverbPreset")) {
                    mRtcEngine.setLocalVoiceReverbPreset(voiceValue);
                }
            }

            if (options.hasKey("videoEncoderConfig") && null != options.getMap("videoEncoderConfig")) {
                ReadableMap config = options.getMap("videoEncoderConfig");
                VideoEncoderConfiguration encoderConfig = new VideoEncoderConfiguration(
                        config.getInt("width"),
                        config.getInt("height"),
                        getVideoEncoderEnum(config.getInt("frameRate")),
                        config.getInt("bitrate"),
                        getOrientationModeEnum(config.getInt("orientationMode"))
                );
                mRtcEngine.setVideoEncoderConfiguration(encoderConfig);
            }

            if (options.hasKey("audioProfile") &&
                    options.hasKey("audioScenario")) {
                mRtcEngine.setAudioProfile(options.getInt("audioProfile"), options.getInt("audioScenario"));
            }

            if (options.hasKey("clientRole")) {
                mRtcEngine.setClientRole(options.getInt("clientRole"));
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    /**
     * setupLocalVideo will render video from local side capture into ui layout
     */
    public int setupLocalVideo(Integer mode) {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(context);
        mSurfaceViews.put(mLocalUid, surfaceView);
        return mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, mode, mLocalUid));
    }

    /**
     * setupRemoteVideo will render video from remote side capture into ui layout
     */
    public int setupRemoteVideo(final int uid, final String channelId, final Integer mode) {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(context);
        mSurfaceViews.put(uid, surfaceView);
        return mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, mode, channelId, uid));
    }

    /**
     * setupRemoteVideo will render video from remote side capture into ui layout
     */
    public int setupRemoteVideo(final int uid, final Integer mode) {
        SurfaceView surfaceView = RtcEngine.CreateRendererView(context);
        mSurfaceViews.put(uid, surfaceView);
        return mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, mode, uid));
    }



    /**
     * set local video render mode
     * @param renderMode Integer
     * @return result state
     */
    public int setLocalRenderMode(final Integer renderMode) {
        return mRtcEngine.setLocalRenderMode(renderMode);
    }

    /**
     * set remote video render mode
     //     * @param Integer uid
     //     * @param Integer renderMode
     * @return result state
     */
    public int setRemoteRenderMode(final Integer uid, final Integer renderMode) {
        return mRtcEngine.setRemoteRenderMode(uid, renderMode);
    }

    public int setEnableSpeakerphone(boolean enabled) {
        return mRtcEngine.setEnableSpeakerphone(enabled);
    }

    public int setDefaultAudioRouteToSpeakerphone(boolean enabled) {
        return mRtcEngine.setDefaultAudioRoutetoSpeakerphone(enabled);
    }

    public int renewToken(String token) {
        return mRtcEngine.renewToken(token);
    }

    public int setClientRole(int role) {
        return mRtcEngine.setClientRole(role);
    }


    public int getConnectionState() {
        return mRtcEngine.getConnectionState();
    }
    public int joinChannel(ReadableMap options) {
        String token = options.hasKey("token") ? options.getString("token") : null;
        String channelName = options.hasKey("channelName") ? options.getString("channelName") : null;
        String optionalInfo = options.hasKey("optionalInfo") ? options.getString("optionalInfo") : null;
        int uid = options.hasKey("uid") ? options.getInt("uid") : 0;
        this.mLocalUid = uid;
        return mRtcEngine.joinChannel(token, channelName, optionalInfo, uid);
    }

    public int enableLastmileTest() {
        return mRtcEngine.enableLastmileTest();
    }

    public int disableLastmileTest() {
        return mRtcEngine.disableLastmileTest();
    }

    public int startPreview() {
        return mRtcEngine.startPreview();
    }

    public int stopPreview() {
        return mRtcEngine.stopPreview();
    }

    public int leaveChannel() {
        return mRtcEngine.leaveChannel();
    }

    public void removeSurfaceView(int uid) {
        mSurfaceViews.remove(uid);
    }

    public List<SurfaceView> getSurfaceViews() {
        List<SurfaceView> list = new ArrayList<SurfaceView>();
        for (int i = 0; i < mSurfaceViews.size(); i++) {
            SurfaceView surfaceView = mSurfaceViews.valueAt(i);
            list.add(surfaceView);
        }
        return list;
    }

    public SurfaceView getLocalSurfaceView() {
        return mSurfaceViews.get(mLocalUid);
    }

    public SurfaceView getSurfaceView(int uid) {
        return mSurfaceViews.get(uid);
    }

    /**
     * create second channel
     */
    public int createJoinChannel(IRtcChannelEventHandler mRtcChannelEventHandler, String token, int uid, String channelId) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;
            RtcChannel mRtcChannel = this.mRtcEngine.createRtcChannel(channelId);

            if (mRtcChannel == null) return -2;
            mRtcChannels.put(channelId, mRtcChannel);

            mRtcChannel.setRtcChannelEventHandler(mRtcChannelEventHandler);
            ChannelMediaOptions cmOptions = new ChannelMediaOptions();
            cmOptions.autoSubscribeAudio = true;
            cmOptions.autoSubscribeVideo = true;
            mRtcChannel.setClientRole(1);
            int ret = mRtcChannel.joinChannel(token, "",  uid, cmOptions);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    /**
     * join second channel
     */
    public int joinSecondChannel(int uid, String channelId, String token) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;
            if(mRtcChannels.get(channelId) == null)
                return -1;

            ChannelMediaOptions cmOptions = new ChannelMediaOptions();
            cmOptions.autoSubscribeAudio = true;
            cmOptions.autoSubscribeVideo = true;
            int ret = mRtcChannels.get(channelId).joinChannel(token, "", uid, cmOptions);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    public int leaveSecondChannel(String channelId) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;

            if(mRtcChannels.get(channelId) == null)
                return -1;


            int ret = mRtcChannels.get(channelId).leaveChannel();
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    public int muteSecondRemoteVideoStream(String channelId, int uid, boolean muted) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;

            if(mRtcChannels.get(channelId) == null)
                return -1;


            int ret = mRtcChannels.get(channelId).muteRemoteVideoStream(uid, muted);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    public int muteSecondRemoteAudioStream(String channelId, int uid, boolean muted) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;

            if(mRtcChannels.get(channelId) == null)
                return -1;


            int ret = mRtcChannels.get(channelId).muteRemoteAudioStream(uid, muted);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    public void leaveAllChannel() {
        try {
            if(this.mRtcEngine == null)
                return;
            for (Map.Entry<String, RtcChannel> entry : mRtcChannels.entrySet()) {
//                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                entry.getValue().leaveChannel();
            }

        } catch (Exception e) {
            throw new RuntimeException("leaveAllChannel rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    public int publishSecondChannel(String channelId) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;

            if(mRtcChannels.get(channelId) == null)
                return -1;


            int ret = mRtcChannels.get(channelId).publish();
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }

    public int unPublishSecondChannel(String channelId) {
        //create rtcEngine instance and setup rtcEngine eventHandler
        try {
            if(this.mRtcEngine == null)
                return -1;

            if(mRtcChannels.get(channelId) == null)
                return -1;


            int ret = mRtcChannels.get(channelId).unpublish();
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("create rtc engine failed\n" + Log.getStackTraceString(e));
        }
    }


//    public int setupMediaVideo() {
//        SurfaceView surfaceView = new SurfaceView();
//        mSurfaceViews.put(uid, surfaceView);
//        return mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, mode, uid));
//    }


}
