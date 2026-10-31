export class ConfigurationError extends Error {
  constructor(variableName: string) {
    super(`Falta la variable de entorno ${variableName}`);
    this.name = "ConfigurationError";
  }
}

function required(name: string, value: string | undefined): string {
  if (!value) {
    throw new ConfigurationError(name);
  }
  return value;
}

export const config = {
  apiBaseUrl: required("NEXT_PUBLIC_API_BASE_URL", process.env.NEXT_PUBLIC_API_BASE_URL).replace(/\/+$/, ""),
  firebase: {
    apiKey: required("NEXT_PUBLIC_FIREBASE_API_KEY", process.env.NEXT_PUBLIC_FIREBASE_API_KEY),
    authDomain: required("NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN", process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN),
    projectId: required("NEXT_PUBLIC_FIREBASE_PROJECT_ID", process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID),
    appId: required("NEXT_PUBLIC_FIREBASE_APP_ID", process.env.NEXT_PUBLIC_FIREBASE_APP_ID),
  },
} as const;
