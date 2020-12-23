import React from 'react';
import { AgoraMediaPlayerProps } from "./types";
export default class AgoraMediaPlayer extends React.Component<AgoraMediaPlayerProps, {}, {}> {
    constructor(props: any, context: any);
    componentDidMount(): void;
    componentWillUnmount(): void;
    ready: (param: any) => void;
    complete: (param: any) => void;
    stoped: (param: any) => void;
    seeking: (param: any) => void;
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
