// login-component.ts
import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { ApiService, User } from './api-service.js';

@customElement('login-component')
export class LoginComponent extends LitElement {
    @property({ type: Boolean })
    isLogin = true;

    @property({ type: String })
    error = '';

    static styles = css`
        :host {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: #f5f5f5;
        }
        
        .container {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 400px;
        }
        
        h2 {
            margin-bottom: 1.5rem;
            text-align: center;
            color: #333;
        }
        
        .form-group {
            margin-bottom: 1rem;
        }
        
        label {
            display: block;
            margin-bottom: 0.5rem;
            color: #555;
            font-weight: 500;
        }
        
        input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            box-sizing: border-box;
        }
        
        input:focus {
            outline: none;
            border-color: #4CAF50;
        }
        
        button {
            width: 100%;
            padding: 0.75rem;
            background: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            margin-top: 1rem;
        }
        
        button:hover {
            background: #45a049;
        }
        
        .toggle {
            text-align: center;
            margin-top: 1rem;
            color: #666;
        }
        
        .toggle a {
            color: #4CAF50;
            cursor: pointer;
            text-decoration: none;
        }
        
        .error {
            color: #f44336;
            margin-bottom: 1rem;
            text-align: center;
        }
    `;

    render() {
        return html`
            <div class="container">
                <h2>${this.isLogin ? 'Login' : 'Register'}</h2>
                ${this.error ? html`<div class="error">${this.error}</div>` : ''}
                <form @submit="${this.handleSubmit}">
                    <div class="form-group">
                        <label>Username</label>
                        <input type="text" name="userName" required>
                    </div>
                    ${!this.isLogin ? html`
                        <div class="form-group">
                            <label>Email</label>
                            <input type="email" name="email" required>
                        </div>
                    ` : ''}
                    <div class="form-group">
                        <label>Password</label>
                        <input type="password" name="password" required minlength="6">
                    </div>
                    <button type="submit">${this.isLogin ? 'Login' : 'Register'}</button>
                </form>
                <div class="toggle">
                    ${this.isLogin ?
            html`Don't have an account? <a @click="${() => this.isLogin = false}">Register</a>` :
            html`Already have an account? <a @click="${() => this.isLogin = true}">Login</a>`
        }
                </div>
            </div>
        `;
    }

    async handleSubmit(e: Event) {
        e.preventDefault();
        const formData = new FormData(e.target as HTMLFormElement);
        const data = Object.fromEntries(formData) as any;

        try {
            const response = this.isLogin ?
                await ApiService.login(data.userName, data.password) :
                await ApiService.register(data.userName, data.email, data.password);

            localStorage.setItem('user', JSON.stringify(response));
            this.dispatchEvent(new CustomEvent('login-success', {
                bubbles: true,
                composed: true,
                detail: response
            }));
        } catch (error: any) {
            this.error = error.message;
        }
    }
}