export type Role = 'CUSTOMER' | 'MANAGER';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: Role;
}