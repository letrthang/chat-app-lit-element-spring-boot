import { LitElement } from 'lit';
export declare class MessageInput extends LitElement {
    value: string;
    placeholder: string;
    maxLength: number;
    minLength: number;
    static styles: import("lit").CSSResult;
    render(): import("lit-html").TemplateResult<1>;
    private handleInput;
    private handleSubmit;
}
