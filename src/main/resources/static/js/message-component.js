var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
// message-component.ts
import { LitElement, html, css } from 'lit';
import { customElement, property, state } from 'lit/decorators.js';
import { repeat } from 'lit/directives/repeat.js';
import { ApiService } from './api-service.js';
import './message-input.js';
let MessageComponent = class MessageComponent extends LitElement {
    constructor() {
        super(...arguments);
        this.isReply = false;
        this.showReplyForm = false;
        this.replies = [];
    }
    static { this.styles = css `
        :host {
            display: block;
            margin-bottom: 1rem;
        }
        
        .message {
            background: white;
            padding: 1rem;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .message.reply {
            margin-left: 2rem;
            background: #f8f9fa;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }
        
        .username {
            font-weight: bold;
            color: #333;
        }
        
        .timestamp {
            color: #666;
            font-size: 0.85rem;
        }
        
        .content {
            color: #555;
            line-height: 1.5;
            word-wrap: break-word;
        }
        
        .actions {
            margin-top: 0.5rem;
        }
        
        .reply-btn {
            background: none;
            border: none;
            color: #4CAF50;
            cursor: pointer;
            font-size: 0.9rem;
            padding: 0.25rem 0.5rem;
        }
        
        .reply-btn:hover {
            text-decoration: underline;
        }
        
        .reply-count {
            color: #666;
            font-size: 0.85rem;
            margin-left: 1rem;
        }
        
        .reply-form {
            margin-top: 1rem;
        }
        
        .replies {
            margin-top: 1rem;
        }
    `; }
    render() {
        const formattedDate = new Date(this.message.createdAt).toLocaleString('vi-VN');
        return html `
            <div class="message ${this.isReply ? 'reply' : ''}">
                <div class="header">
                    <span class="username">${this.message.userName}</span>
                    <span class="timestamp">${formattedDate}</span>
                </div>
                <div class="content">${this.message.content}</div>
                <div class="actions">
                    <button class="reply-btn" @click="${this.toggleReplyForm}">
                        ${this.showReplyForm ? 'Cancel' : 'Reply'}
                    </button>
                    ${this.message.replyCount > 0 ? html `
                        <span class="reply-count">${this.message.replyCount} replies</span>
                    ` : ''}
                </div>
                ${this.showReplyForm ? html `
                    <div class="reply-form">
                        <message-input
                            placeholder="Write your reply..."
                            @message-submit="${this.handleReplySubmit}"
                        ></message-input>
                    </div>
                ` : ''}
                ${this.replies.length > 0 ? html `
                    <div class="replies">
                        ${repeat(this.replies, reply => reply.id, reply => html `
                            <message-component 
                                .message="${reply}"
                                ?isReply="${true}"
                            ></message-component>
                        `)}
                    </div>
                ` : ''}
            </div>
        `;
    }
    toggleReplyForm() {
        this.showReplyForm = !this.showReplyForm;
        if (!this.isReply && this.replies.length === 0) {
            this.loadReplies();
        }
    }
    async loadReplies() {
        try {
            this.replies = await ApiService.getReplies(this.message.id);
        }
        catch (error) {
            console.error('Failed to load replies:', error);
        }
    }
    async handleReplySubmit(e) {
        try {
            const newReply = await ApiService.createMessage(e.detail.content, this.message.id);
            this.replies = [...this.replies, newReply];
            this.message.replyCount = (this.message.replyCount || 0) + 1;
            this.showReplyForm = false;
            this.requestUpdate();
        }
        catch (error) {
            console.error('Failed to post reply:', error);
        }
    }
};
__decorate([
    property({ type: Object })
], MessageComponent.prototype, "message", void 0);
__decorate([
    property({ type: Boolean })
], MessageComponent.prototype, "isReply", void 0);
__decorate([
    state()
], MessageComponent.prototype, "showReplyForm", void 0);
__decorate([
    state()
], MessageComponent.prototype, "replies", void 0);
MessageComponent = __decorate([
    customElement('message-component')
], MessageComponent);
export { MessageComponent };
