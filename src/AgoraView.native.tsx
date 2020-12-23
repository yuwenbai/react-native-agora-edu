import React from 'react';
import {
    requireNativeComponent,
    Platform
} from 'react-native'

import {
    AgoraViewProps,
} from "./types";

"mediaPlayerState_open_Completed";
 "mediaPlayerState_paused";
    "mediaPlayerState_stoped";
    "mediaPlayerState_seeking";

const RCTAgoraView = requireNativeComponent("RCTAgoraVideoView");

export default class AgoraView extends React.Component<AgoraViewProps, {}, {}> {
    /**
     * render
     *
     * It would render view for VideoStream
     */
    public render(): JSX.Element {
        return (
            <RCTAgoraView { ...this.getHTMLProps() } />
        )
    }

    /**
     * getHTMLProps
     *
     * get agora view props
     */
    private getHTMLProps(): AgoraViewProps {
        let htmlProps = {} as AgoraViewProps;
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

