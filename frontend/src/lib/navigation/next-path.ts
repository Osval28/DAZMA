const DEFAULT_PATH = "/";

export function safeNextPath(candidate: string | null): string {
  if (candidate === null) {
    return DEFAULT_PATH;
  }
  const isLocalPath = candidate.startsWith("/") && !candidate.startsWith("//") && !candidate.includes("\\");
  return isLocalPath ? candidate : DEFAULT_PATH;
}
