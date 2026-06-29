import type { LoginRequest, RegisterRequest } from "../types/auth";

const BASE_URL = 'http://localhost:8080/api/auth'

export async function login(request: LoginRequest): Promise<void> {
    const response = await fetch(`${BASE_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify(request)
    });

    if (!response.ok) {
        throw new Error('failed to login');
    }
}

export async function register(request: RegisterRequest): Promise<void> {
    const response = await fetch(`${BASE_URL}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'applicatin/json',
        },
        credentials: 'include',
        body: JSON.stringify(request),
    });

    if (!response.ok) {
        throw new Error('failed to register');
    }
}


