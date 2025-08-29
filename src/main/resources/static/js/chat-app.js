var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
// chat-app.ts
import { LitElement, html, css } from 'lit';
import { customElement, state } from 'lit/decorators.js';
import { repeat } from 'lit/directives/repeat.js';
import { ApiService } from './api-service.js';
import './login-component.js';
import './message-input.js';
import './message-component.js';
let ChatApp = class ChatApp extends LitElement {
    constructor() {
        super();
        this.user = null;
        this.posts = [];
        this.loading = false;
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            this.user = JSON.parse(storedUser);
        }
    }
    static { this.styles = css `
        :host {
            display: block;
            min-height: 100vh;
            background: #f5f5f5;
        }
        
        .header {
            background: #4CAF50;
            color: white;
            padding: 1rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .header-content {
            max-width: 800px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        h1 {
            margin: 0;
            font-size: 1.5rem;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .logout-btn {
            background: white;
            color: #4CAF50;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        
        .logout-btn:hover {
            background: #f5f5f5;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 2rem 1rem;
        }
        
        .post-form {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }
        
        .post-form h3 {
            margin-top: 0;
            color: #333;
        }
        
        .posts {
            margin-top: 2rem;
        }
        
        .loading {
            text-align: center;
            color: #666;
            padding: 2rem;
        }
        
        .empty {
            text-align: center;
            color: #666;
            padding: 3rem;
            background: white;
            border-radius: 8px;
        }
    `; }
    render() {
        if (!this.user) {
            return html `<login-component @login-success="${this.handleLogin}"></login-component>`;
        }
        return html `
            <div class="header">
                <div class="header-content">
                    <h1>Chat Application</h1>
                    <div class="user-info">
                        <span>Welcome, ${this.user.userName}!</span>
                        <button class="logout-btn" @click="${this.handleLogout}">Logout</button>
                    </div>
                </div>
            </div>
            
            <div class="container">
                <div class="post-form">
                    <h3>Create a new post</h3>
                    <message-input @message-submit="${this.handleNewPost}"></message-input>
                </div>
                
                <div class="posts">
                    ${this.loading ? html `
                        <div class="loading">Loading posts...</div>
                    ` : this.posts.length === 0 ? html `
                        <div class="empty">No posts yet. Be the first to post!</div>
                    ` : repeat(this.posts, (post) => post.id, (post) => html `
                        <message-component .message="${post}"></message-component>
                    `)}
                </div>
            </div>
        `;
    }
    connectedCallback() {
        super.connectedCallback();
        if (this.user) {
            this.loadPosts();
        }
    }
    handleLogin(e) {
        this.user = e.detail;
        this.loadPosts();
    }
    handleLogout() {
        localStorage.removeItem('user');
        this.user = null;
        this.posts = [];
    }
    async loadPosts() {
        this.loading = true;
        try {
            this.posts = await ApiService.getPosts();
        }
        catch (error) {
            console.error('Failed to load posts:', error);
        }
        finally {
            this.loading = false;
        }
    }
    async handleNewPost(e) {
        try {
            const newPost = await ApiService.createMessage(e.detail.content);
            this.posts = [newPost, ...this.posts];
        }
        catch (error) {
            console.error('Failed to create post:', error);
        }
    }
};
__decorate([
    state()
], ChatApp.prototype, "user", void 0);
__decorate([
    state()
], ChatApp.prototype, "posts", void 0);
__decorate([
    state()
], ChatApp.prototype, "loading", void 0);
ChatApp = __decorate([
    customElement('chat-app')
], ChatApp);
export { ChatApp };
