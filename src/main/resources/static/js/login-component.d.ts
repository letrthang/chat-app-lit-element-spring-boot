import { LitElement } from 'lit';
export declare class LoginComponent extends LitElement {
    isLogin: boolean;
    error: string;
    static styles: import("lit").CSSResult;
    render(): import("lit-html").TemplateResult<1>;
    handleSubmit(e: Event): Promise<void>;
}
