export class ApiService {
    static { this.baseURL = 'http://localhost:8080/api'; }
    static async request(endpoint, options = {}) {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        // Create headers as a Record type to allow string indexing
        const headers = {
            'Content-Type': 'application/json'
        };
        // Merge existing headers if any
        if (options.headers) {
            if (options.headers instanceof Headers) {
                options.headers.forEach((value, key) => {
                    headers[key] = value;
                });
            }
            else if (Array.isArray(options.headers)) {
                options.headers.forEach(([key, value]) => {
                    headers[key] = value;
                });
            }
            else {
                Object.assign(headers, options.headers);
            }
        }
        if (user.id) {
            headers['User-Id'] = user.id.toString();
        }
        const response = await fetch(`${this.baseURL}${endpoint}`, {
            ...options,
            headers
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Request failed');
        }
        return response.json();
    }
    static login(userName, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ userName, password })
        });
    }
    static register(userName, email, password) {
        return this.request('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ userName, email, password })
        });
    }
    static getPosts() {
        return this.request('/messages/posts');
    }
    static createMessage(content, parentMessageId) {
        return this.request('/messages', {
            method: 'POST',
            body: JSON.stringify({ content, parentMessageId })
        });
    }
    static getReplies(messageId) {
        return this.request(`/messages/${messageId}/replies`);
    }
}
