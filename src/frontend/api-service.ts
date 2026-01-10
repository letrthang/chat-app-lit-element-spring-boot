// api-service.ts
export interface User {
    id: number;
    userName: string;
    email: string;
    token?: string;
    refreshToken?: string;
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
    private static isRefreshing = false;
    private static refreshPromise: Promise<User> | null = null;

    static async request<T>(endpoint: string, options: RequestInit = {}, isRetry = false): Promise<T> {
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

        // Add JWT token in Authorization header
        if (user.token && endpoint !== '/auth/refresh') {
            headers['Authorization'] = `Bearer ${user.token}`;
        }

        if (user.id) {
            headers['User-Id'] = user.id.toString();
        }

        const response = await fetch(`${this.baseURL}${endpoint}`, {
            ...options,
            headers
        });

        // Handle 401 - token expired
        if (response.status === 401 && !isRetry && endpoint !== '/auth/login' && endpoint !== '/auth/register' && endpoint !== '/auth/refresh') {
            try {
                // Refresh the token
                const newUser = await this.refreshAccessToken();

                // Retry the original request with new token
                return this.request<T>(endpoint, options, true);
            } catch (refreshError) {
                // Refresh failed, redirect to login
                localStorage.removeItem('user');
                window.dispatchEvent(new CustomEvent('auth-expired'));
                throw new Error('Session expired. Please login again.');
            }
        }

        if (!response.ok) {
            const error = await response.json().catch(() => ({ error: 'Request failed' }));
            throw new Error(error.error || 'Request failed');
        }

        return response.json();
    }

    private static async refreshAccessToken(): Promise<User> {
        // Prevent multiple simultaneous refresh attempts
        if (this.isRefreshing && this.refreshPromise) {
            return this.refreshPromise;
        }

        this.isRefreshing = true;
        this.refreshPromise = (async () => {
            try {
                const user = JSON.parse(localStorage.getItem('user') || '{}');

                if (!user.refreshToken) {
                    throw new Error('No refresh token available');
                }

                const response = await fetch(`${this.baseURL}/auth/refresh`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ refreshToken: user.refreshToken })
                });

                if (!response.ok) {
                    throw new Error('Refresh token expired');
                }

                const newUser: User = await response.json();
                localStorage.setItem('user', JSON.stringify(newUser));
                return newUser;
            } finally {
                this.isRefreshing = false;
                this.refreshPromise = null;
            }
        })();

        return this.refreshPromise;
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

    static logout(): Promise<void> {
        return this.request<void>('/auth/logout', {
            method: 'POST'
        });
    }
}