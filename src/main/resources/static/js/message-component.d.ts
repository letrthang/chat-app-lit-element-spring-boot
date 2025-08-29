import { LitElement } from 'lit';
import { Message } from './api-service.js';
import './message-input.js';
export declare class MessageComponent extends LitElement {
    message: Message;
    isReply: boolean;
    private showReplyForm;
    private replies;
    static styles: import("lit").CSSResult;
    render(): import("lit-html").TemplateResult<1>;
    private toggleReplyForm;
    private loadReplies;
    private handleReplySubmit;
}
