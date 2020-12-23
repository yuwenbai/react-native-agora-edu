import React from 'react';
import {
    requireNativeComponent,
    Platform,
    DeviceEventEmitter
} from 'react-native'

import {
    AgoraMediaPlayerProps,
} from "./types";

const RCTAgoraMedia = requireNativeComponent("RCTAgoraMediaView");


export default class AgoraMediaPlayer  extends React.Component<AgoraMediaPlayerProps, {}, {}> {
    constructor(props, context) {
        super(props, context);
        this.ready = this.ready.bind(this);
        this.complete = this.complete.bind(this);
        this.stoped = this.stoped.bind(this);
        this.seeking = this.seeking.bind(this);
    }
    public componentDidMount() {
        this.openComplete = DeviceEventEmitter.addListener(
            'mediaPlayerState_open_Completed',
            this.ready
        )

        this.complete = DeviceEventEmitter.addListener(
            'mediaPlayerState_playback_Completed',
            this.complete
        )

        this.stoped = DeviceEventEmitter.addListener(
            'mediaPlayerState_stoped',
            this.stoped
        )

        this.seeking = DeviceEventEmitter.addListener(
            'mediaPlayerState_seeking',
            this.seeking
        )
    }

    public componentWillUnmount() {
        this.openComplete && this.openComplete.remove()
        this.complete && this.complete.remove()
        this.stoped && this.stoped.remove()
        this.seeking && this.seeking.remove()
    }

    ready = (param) => {
        this.props.ready && this.props.ready(param)
    }

    complete = (param) => {
        this.props.complete && this.props.complete(param)
    }

    stoped = (param) => {
        this.props.stoped && this.props.stoped(param)
    }

    seeking = (param) => {
        this.props.seeking && this.props.seeking(param)
    }


    /**
     * render
     *
     * It would render view for VideoStream
     */
    public render(): JSX.Element {
        return (
            <RCTAgoraMedia { ...this.getHTMLProps() } />
        )
    }

    /**
     * getHTMLProps
     *
     * get agora view props
     */
    private getHTMLProps(): AgoraMediaPlayerProps {
        let htmlProps = {} as AgoraMediaPlayerProps;
        for (let key in this.props) {
            htmlProps[key] = this.props[key];
        }

        // convert uint32 to int32 for android
        if (Platform.OS === 'android') {
            const int32 = new Int32Array(1);
            int32[0] = htmlProps.remoteUid;
            htmlProps.remoteUid = int32[0]
        }
        return htmlProps;
    }
}

