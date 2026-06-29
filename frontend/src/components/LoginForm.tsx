import React, { useState } from "react";
import { login } from "../apis/auth";

export function LoginForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    async function handleSubmit(e: React.SubmitEvent) {
        e.preventDefault();
        setError(null);
        setIsLoading(true);

        try {
            await login({ email, password });
            console.log('succeed to log in');
        } catch {
            setError('failed to log in');
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label htmlFor="email">Email</label>
                <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                />
            </div>
            <div>
                <label htmlFor="password">Password</label>
                <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                />
            </div>
            {error && <p style={{color: 'red'}}>{error}</p>}
            <button type="submit" disabled={isLoading}>
                {isLoading ? 'logging in...' : 'Log in'}
            </button>
        </form>
    )
}