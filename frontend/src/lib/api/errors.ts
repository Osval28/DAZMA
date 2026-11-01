export type ProblemDetail = {
  status?: number;
  code?: string;
  detail?: string;
};

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;

  constructor(status: number, code: string) {
    super(code);
    this.name = "ApiError";
    this.status = status;
    this.code = code;
  }
}

export class NetworkError extends Error {
  constructor() {
    super("No se pudo conectar con el servidor");
    this.name = "NetworkError";
  }
}

export class CsrfUnavailableError extends Error {
  constructor() {
    super("El servidor no entregó el token CSRF");
    this.name = "CsrfUnavailableError";
  }
}

export class PhotoUploadError extends Error {
  constructor() {
    super("El almacenamiento rechazó la fotografía");
    this.name = "PhotoUploadError";
  }
}

export function isUnauthenticated(error: unknown): boolean {
  return error instanceof ApiError && error.status === 401;
}

export function hasErrorCode(error: unknown, code: string): boolean {
  return error instanceof ApiError && error.code === code;
}
