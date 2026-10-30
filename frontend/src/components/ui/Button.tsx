import type { ButtonHTMLAttributes } from "react";

type ButtonVariant = "primary" | "secondary";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: ButtonVariant;
};

export function Button({ variant = "primary", className, type = "button", ...rest }: ButtonProps) {
  const classes = ["button", `button--${variant}`, className].filter(Boolean).join(" ");
  return <button type={type} className={classes} {...rest} />;
}
