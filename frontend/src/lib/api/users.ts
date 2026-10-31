import { apiCommand, apiJson } from "./http";

export type MyProfile = {
  id: string;
  name: string;
  phone: string;
  municipality: string;
  state: "ACTIVE" | "SUSPENDED";
  role: string;
  email: string;
  emailVerified: boolean;
};

export type RegisterUserInput = {
  name: string;
  phone: string;
  municipality: string;
};

const USERS_PATH = "/users/api/v1/users";

export function getMyProfile(): Promise<MyProfile> {
  return apiJson<MyProfile>(`${USERS_PATH}/me`);
}

export function registerUser(input: RegisterUserInput): Promise<void> {
  return apiCommand(USERS_PATH, { method: "POST", body: input });
}
