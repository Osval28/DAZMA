import type { TextareaHTMLAttributes } from "react";

type TextAreaFieldProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
  id: string;
  label: string;
};

export function TextAreaField({ id, label, ...rest }: TextAreaFieldProps) {
  return (
    <div className="field">
      <label htmlFor={id} className="field__label">
        {label}
      </label>
      <textarea id={id} className="field__input field__input--multiline" {...rest} />
    </div>
  );
}
