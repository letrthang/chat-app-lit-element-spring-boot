// message-input.ts
import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('message-input')
export class MessageInput extends LitElement {
    @property({ type: String })
    value = '';

    @property({ type: String })
    placeholder = 'Write your message...';

    @property({ type: Number })
    maxLength = 200;

    @property({ type: Number })
    minLength = 3;

    static styles = css`
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
    `;

    render() {
        const remaining = this.maxLength - this.value.length;
        const isValid = this.value.length >= this.minLength && this.value.length <= this.maxLength;
        const counterClass = remaining < 20 ? (remaining < 0 ? 'error' : 'warning') : '';

        return html`
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

    private handleInput(e: Event) {
        const target = e.target as HTMLTextAreaElement;
        this.value = target.value;
    }

    private handleSubmit() {
        if (this.value.length >= this.minLength && this.value.length <= this.maxLength) {
            this.dispatchEvent(new CustomEvent('message-submit', {
                detail: { content: this.value }
            }));
            this.value = '';
        }
    }
}