import React from 'react';
import { AgoraViewProps } from "./types";
export default class AgoraView extends React.Component<AgoraViewProps, {}, {}> {
    /**
     * render
     *
     * It would render view for VideoStream
     */
    render(): JSX.Element;
    /**
     * getHTMLProps
     *
     * get agora view props
     */
    private getHTMLProps;
}
