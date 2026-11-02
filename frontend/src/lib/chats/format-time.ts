const TIME_FORMAT = new Intl.DateTimeFormat(undefined, { hour: "2-digit", minute: "2-digit" });

export function formatMessageTime(timestamp: Date): string {
  return TIME_FORMAT.format(timestamp);
}
