"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const tslib_1 = require("tslib");
const react_1 = tslib_1.__importDefault(require("react"));
const react_native_1 = require("react-native");
const RCTAgoraMedia = react_native_1.requireNativeComponent("RCTAgoraMediaView");
class AgoraMediaPlayer extends react_1.default.Component {
    constructor(props, context) {
        super(props, context);
        this.ready = (param) => {
            this.props.ready && this.props.ready(param);
        };
        this.complete = (param) => {
            this.props.complete && this.props.complete(param);
        };
        this.stoped = (param) => {
            this.props.stoped && this.props.stoped(param);
        };
        this.seeking = (param) => {
            this.props.seeking && this.props.seeking(param);
        };
        this.ready = this.ready.bind(this);
        this.complete = this.complete.bind(this);
        this.stoped = this.stoped.bind(this);
        this.seeking = this.seeking.bind(this);
    }
    componentDidMount() {
        this.openComplete = react_native_1.DeviceEventEmitter.addListener('mediaPlayerState_open_Completed', this.ready);
        this.complete = react_native_1.DeviceEventEmitter.addListener('mediaPlayerState_playback_Completed', this.complete);
        this.stoped = react_native_1.DeviceEventEmitter.addListener('mediaPlayerState_stoped', this.stoped);
        this.seeking = react_native_1.DeviceEventEmitter.addListener('mediaPlayerState_seeking', this.seeking);
    }
    componentWillUnmount() {
        this.openComplete && this.openComplete.remove();
        this.complete && this.complete.remove();
        this.stoped && this.stoped.remove();
        this.seeking && this.seeking.remove();
    }
    /**
     * render
     *
     * It would render view for VideoStream
     */
    render() {
        return (react_1.default.createElement(RCTAgoraMedia, Object.assign({}, this.getHTMLProps())));
    }
    /**
     * getHTMLProps
     *
     * get agora view props
     */
    getHTMLProps() {
        let htmlProps = {};
        for (let key in this.props) {
            htmlProps[key] = this.props[key];
        }
        // convert uint32 to int32 for android
        if (react_native_1.Platform.OS === 'android') {
            const int32 = new Int32Array(1);
            int32[0] = htmlProps.remoteUid;
            htmlProps.remoteUid = int32[0];
        }
        return htmlProps;
    }
}
exports.default = AgoraMediaPlayer;
//# sourceMappingURL=AgoraMediaPlayer.native.js.map