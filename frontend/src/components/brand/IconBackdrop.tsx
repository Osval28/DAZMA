import type { CSSProperties, ReactNode } from "react";

type BackdropIconName = "cart" | "exchange" | "phone" | "laptop" | "console" | "tag" | "coin" | "pin" | "handshake" | "shield";

type BackdropItem = {
  icon: BackdropIconName;
  x: number;
  y: number;
  size: number;
  rotate: number;
  delay: number;
  duration: number;
};

type IconBackdropProps = {
  tone?: "bold" | "subtle";
};

const iconShapes: Record<BackdropIconName, ReactNode> = {
  cart: (
    <>
      <circle cx="9" cy="21" r="1" />
      <circle cx="20" cy="21" r="1" />
      <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6" />
    </>
  ),
  exchange: (
    <>
      <path d="M17 1l4 4-4 4" />
      <path d="M3 11V9a4 4 0 0 1 4-4h14" />
      <path d="M7 23l-4-4 4-4" />
      <path d="M21 13v2a4 4 0 0 1-4 4H3" />
    </>
  ),
  phone: (
    <>
      <rect x="5" y="2" width="14" height="20" rx="2" />
      <path d="M12 18h.01" />
    </>
  ),
  laptop: (
    <>
      <rect x="3" y="4" width="18" height="12" rx="2" />
      <path d="M2 20h20" />
    </>
  ),
  console: (
    <>
      <rect x="2" y="6" width="20" height="12" rx="6" />
      <path d="M6 12h4M8 10v4" />
      <path d="M15 13h.01M18 11h.01" />
    </>
  ),
  tag: (
    <>
      <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" />
      <path d="M7 7h.01" />
    </>
  ),
  coin: (
    <>
      <circle cx="12" cy="12" r="9" />
      <path d="M14.5 9.5A2.5 2 0 0 0 12 8c-1.4 0-2.5.8-2.5 2s1.1 1.7 2.5 2 2.5.8 2.5 2-1.1 2-2.5 2a2.5 2 0 0 1-2.5-1.5" />
      <path d="M12 6v2M12 16v2" />
    </>
  ),
  pin: (
    <>
      <path d="M21 10c0 7-9 13-9 13S3 17 3 10a9 9 0 0 1 18 0z" />
      <circle cx="12" cy="10" r="3" />
    </>
  ),
  handshake: (
    <>
      <path d="m11 17 2 2a1 1 0 1 0 3-3" />
      <path d="m14 14 2.5 2.5a1 1 0 1 0 3-3l-3.88-3.88a3 3 0 0 0-4.24 0l-.88.88a1 1 0 1 1-3-3l2.81-2.81a5.79 5.79 0 0 1 7.06-.87l.47.28a2 2 0 0 0 1.42.25L21 4" />
      <path d="m21 3 1 11h-2" />
      <path d="M3 3 2 14l6.5 6.5a1 1 0 1 0 3-3" />
      <path d="M3 4h8" />
    </>
  ),
  shield: (
    <>
      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
      <path d="M9 12l2 2 4-4" />
    </>
  ),
};

const backdropItems: readonly BackdropItem[] = [
  { icon: "cart", x: 6, y: 10, size: 3.5, rotate: -12, delay: 0, duration: 9 },
  { icon: "exchange", x: 24, y: 6, size: 3, rotate: 8, delay: -3, duration: 11 },
  { icon: "tag", x: 44, y: 14, size: 2.75, rotate: 14, delay: -6, duration: 10 },
  { icon: "phone", x: 66, y: 7, size: 3.25, rotate: -8, delay: -2, duration: 12 },
  { icon: "handshake", x: 86, y: 13, size: 3.5, rotate: 10, delay: -5, duration: 9.5 },
  { icon: "pin", x: 12, y: 34, size: 3, rotate: 6, delay: -4, duration: 10.5 },
  { icon: "laptop", x: 34, y: 40, size: 3.5, rotate: -10, delay: -1, duration: 11.5 },
  { icon: "coin", x: 56, y: 36, size: 2.75, rotate: 12, delay: -7, duration: 9 },
  { icon: "cart", x: 78, y: 42, size: 3, rotate: -6, delay: -3, duration: 12 },
  { icon: "shield", x: 93, y: 38, size: 3, rotate: 8, delay: -8, duration: 10 },
  { icon: "console", x: 5, y: 62, size: 3.5, rotate: 9, delay: -6, duration: 11 },
  { icon: "exchange", x: 26, y: 70, size: 3.25, rotate: -14, delay: -2, duration: 9.5 },
  { icon: "tag", x: 48, y: 64, size: 3, rotate: 6, delay: -9, duration: 12 },
  { icon: "handshake", x: 68, y: 72, size: 3, rotate: -10, delay: -4, duration: 10 },
  { icon: "phone", x: 88, y: 66, size: 3.25, rotate: 12, delay: -1, duration: 11 },
  { icon: "pin", x: 14, y: 90, size: 3, rotate: -8, delay: -5, duration: 10.5 },
  { icon: "coin", x: 38, y: 92, size: 3.25, rotate: 10, delay: -7, duration: 9 },
  { icon: "cart", x: 60, y: 90, size: 3.5, rotate: -12, delay: -3, duration: 11.5 },
  { icon: "shield", x: 80, y: 94, size: 3, rotate: 7, delay: -8, duration: 10 },
  { icon: "laptop", x: 95, y: 84, size: 3, rotate: -9, delay: -2, duration: 12 },
];

function itemStyle({ x, y, size, rotate, delay, duration }: BackdropItem): CSSProperties {
  return {
    "--x": `${x}%`,
    "--y": `${y}%`,
    "--size": `${size}rem`,
    "--rotate": `${rotate}deg`,
    "--delay": `${delay}s`,
    "--duration": `${duration}s`,
  } as CSSProperties;
}

export function IconBackdrop({ tone = "bold" }: IconBackdropProps) {
  return (
    <div className={`backdrop backdrop--${tone}`} aria-hidden="true">
      {backdropItems.map((item, index) => (
        <svg
          key={index}
          className="backdrop__item"
          style={itemStyle(item)}
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.5"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          {iconShapes[item.icon]}
        </svg>
      ))}
    </div>
  );
}
