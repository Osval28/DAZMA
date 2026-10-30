import Link from "next/link";
import { LogoMark } from "@/components/brand/LogoMark";

export function Wordmark() {
  return (
    <Link href="/" className="wordmark" aria-label="DAZMA">
      <LogoMark />
      <span className="wordmark__text">DAZMA</span>
    </Link>
  );
}
