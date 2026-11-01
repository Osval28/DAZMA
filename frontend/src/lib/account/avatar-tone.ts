export const AVATAR_TONES = 6;

const HASH_MULTIPLIER = 31;

export function toneOf(name: string): number {
  let hash = 0;
  for (const character of name.trim().toLocaleLowerCase("es-CO")) {
    hash = (hash * HASH_MULTIPLIER + (character.codePointAt(0) as number)) >>> 0;
  }
  return hash % AVATAR_TONES;
}
