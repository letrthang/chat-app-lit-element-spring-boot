// api-service.ts
export interface User {
    id: number;
    userName: string;
    email: string;
    token?: string;
}

export interface Message {
    id: number;
    content: string;
    userName: string;
    userId: number;
    createdAt: string;
    messageType: string;
    parentMessageId?: number;
    replies?: Message[];
    replyCount: number;
}

export interface LoginRequest {
    userName: string;
    password: string;
}

export interface RegisterRequest {
    userName: string;
    email: string;
    password: string;
}

export interface MessageRequest {
    content: string;
    parentMessageId?: number;
}

export class ApiService {
    static baseURL = 'http://localhost:8080/api';

    static async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
        const user = JSON.parse(localStorage.getItem('user') || '{}');

        // Create headers as a Record type to allow string indexing
        const headers: Record<string, string> = {
            'Content-Type': 'application/json'
        };

        // Merge existing headers if any
        if (options.headers) {
            if (options.headers instanceof Headers) {
                options.headers.forEach((value, key) => {
                    headers[key] = value;
                });
            } else if (Array.isArray(options.headers)) {
                options.headers.forEach(([key, value]) => {
                    headers[key] = value;
                });
            } else {
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

    static login(userName: string, password: string): Promise<User> {
        return this.request<User>('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ userName, password })
        });
    }

    static register(userName: string, email: string, password: string): Promise<User> {
        return this.request<User>('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ userName, email, password })
        });
    }

    static getPosts(): Promise<Message[]> {
        return this.request<Message[]>('/messages/posts');
    }

    static createMessage(content: string, parentMessageId?: number): Promise<Message> {
        return this.request<Message>('/messages', {
            method: 'POST',
            body: JSON.stringify({ content, parentMessageId })
        });
    }

    static getReplies(messageId: number): Promise<Message[]> {
        return this.request<Message[]>(`/messages/${messageId}/replies`);
    }
}