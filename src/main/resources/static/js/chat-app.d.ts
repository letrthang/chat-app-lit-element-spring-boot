import { LitElement } from 'lit';
import './login-component.js';
import './message-input.js';
import './message-component.js';
export declare class ChatApp extends LitElement {
    private user;
    private posts;
    private loading;
    constructor();
    static styles: import("lit").CSSResult;
    render(): import("lit-html").TemplateResult<1>;
    connectedCallback(): void;
    private handleLogin;
    private handleLogout;
    private loadPosts;
    private handleNewPost;
}
