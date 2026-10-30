"use client";

import { useState } from "react";
import type { InputHTMLAttributes } from "react";
import { authMessages } from "@/lib/auth/messages";
import { EyeIcon, EyeOffIcon } from "./icons";

type PasswordFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, "type"> & {
  id: string;
  label: string;
};

export function PasswordField({ id, label, ...rest }: PasswordFieldProps) {
  const [visible, setVisible] = useState(false);
  const toggleLabel = visible ? authMessages.passwordToggle.hide : authMessages.passwordToggle.show;

  return (
    <div className="field">
      <label htmlFor={id} className="field__label">
        {label}
      </label>
      <div className="field__control">
        <input id={id} className="field__input field__input--with-action" type={visible ? "text" : "password"} {...rest} />
        <button type="button" className="field__action" onClick={() => setVisible((current) => !current)} aria-label={toggleLabel}>
          {visible ? <EyeOffIcon /> : <EyeIcon />}
        </button>
      </div>
    </div>
  );
}
