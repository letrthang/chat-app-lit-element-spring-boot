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
export declare class ApiService {
    static baseURL: string;
    static request<T>(endpoint: string, options?: RequestInit): Promise<T>;
    static login(userName: string, password: string): Promise<User>;
    static register(userName: string, email: string, password: string): Promise<User>;
    static getPosts(): Promise<Message[]>;
    static createMessage(content: string, parentMessageId?: number): Promise<Message>;
    static getReplies(messageId: number): Promise<Message[]>;
}
