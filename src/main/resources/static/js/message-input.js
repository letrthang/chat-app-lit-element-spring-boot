var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
// message-input.ts
import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';
let MessageInput = class MessageInput extends LitElement {
    constructor() {
        super(...arguments);
        this.value = '';
        this.placeholder = 'Write your message...';
        this.maxLength = 200;
        this.minLength = 3;
    }
    static { this.styles = css `
        :host {
            display: block;
        }
        
        .input-container {
            position: relative;
        }
        
        textarea {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            resize: vertical;
            min-height: 60px;
            font-family: inherit;
            box-sizing: border-box;
        }
        
        textarea:focus {
            outline: none;
            border-color: #4CAF50;
        }
        
        .char-counter {
            position: absolute;
            bottom: 5px;
            right: 10px;
            font-size: 0.8rem;
            color: #666;
        }
        
        .char-counter.warning {
            color: #ff9800;
        }
        
        .char-counter.error {
            color: #f44336;
        }
        
        button {
            margin-top: 0.5rem;
            padding: 0.5rem 1rem;
            background: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
        }
        
        button:hover:not(:disabled) {
            background: #45a049;
        }
        
        button:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
    `; }
    render() {
        const remaining = this.maxLength - this.value.length;
        const isValid = this.value.length >= this.minLength && this.value.length <= this.maxLength;
        const counterClass = remaining < 20 ? (remaining < 0 ? 'error' : 'warning') : '';
        return html `
            <div class="input-container">
                <textarea
                    .value="${this.value}"
                    @input="${this.handleInput}"
                    placeholder="${this.placeholder}"
                    maxlength="${this.maxLength + 50}"
                ></textarea>
                <span class="char-counter ${counterClass}">
                    ${this.value.length}/${this.maxLength}
                </span>
            </div>
            <button 
                @click="${this.handleSubmit}"
                ?disabled="${!isValid}"
            >
                Send
            </button>
        `;
    }
    handleInput(e) {
        const target = e.target;
        this.value = target.value;
    }
    handleSubmit() {
        if (this.value.length >= this.minLength && this.value.length <= this.maxLength) {
            this.dispatchEvent(new CustomEvent('message-submit', {
                detail: { content: this.value }
            }));
            this.value = '';
        }
    }
};
__decorate([
    property({ type: String })
], MessageInput.prototype, "value", void 0);
__decorate([
    property({ type: String })
], MessageInput.prototype, "placeholder", void 0);
__decorate([
    property({ type: Number })
], MessageInput.prototype, "maxLength", void 0);
__decorate([
    property({ type: Number })
], MessageInput.prototype, "minLength", void 0);
MessageInput = __decorate([
    customElement('message-input')
], MessageInput);
export { MessageInput };
