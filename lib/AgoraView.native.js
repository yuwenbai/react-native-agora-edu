"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const tslib_1 = require("tslib");
const react_1 = tslib_1.__importDefault(require("react"));
const react_native_1 = require("react-native");
"mediaPlayerState_open_Completed";
"mediaPlayerState_paused";
"mediaPlayerState_stoped";
"mediaPlayerState_seeking";
const RCTAgoraView = react_native_1.requireNativeComponent("RCTAgoraVideoView");
class AgoraView extends react_1.default.Component {
    /**
     * render
     *
     * It would render view for VideoStream
     */
    render() {
        return (react_1.default.createElement(RCTAgoraView, Object.assign({}, this.getHTMLProps())));
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
exports.default = AgoraView;
//# sourceMappingURL=AgoraView.native.js.map