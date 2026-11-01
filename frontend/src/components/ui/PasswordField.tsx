"use client";

import { useTranslations } from "next-intl";
import { useState } from "react";
import type { InputHTMLAttributes } from "react";
import { EyeIcon, EyeOffIcon } from "./icons";

type PasswordFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, "type"> & {
  id: string;
  label: string;
};

export function PasswordField({ id, label, ...rest }: PasswordFieldProps) {
  const t = useTranslations("Auth.passwordToggle");
  const [visible, setVisible] = useState(false);
  const toggleLabel = visible ? t("hide") : t("show");

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
