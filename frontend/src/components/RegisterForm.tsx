import { useState } from "react";
import type { Role } from "../types/auth";
import { register } from "../apis/auth";

export function RegisterForm() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState<Role>('CUSTOMER');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const isPasswordTooShort = password.length < 8;

    async function handleSubmit(e: React.SubmitEvent) {
        e.preventDefault();
        if (isPasswordTooShort) {
            setError('Password length must be equal to or more than 8.')
            return;
        }
        setError(null);
        setIsLoading(true);

        try {
            await register({email, password, role});
            console.log('succeed to register');
        } catch {
            setError('failed to register');
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
                {password.length > 0 && isPasswordTooShort && <p style={{color: 'red'}}>Password length must be equal to or more than 8.</p>}
            </div>
            <div>
                <label>
                    <input
                    type="radio"
                    name="role"
                    value="CUSTOMER"
                    checked={role === 'CUSTOMER'}
                    onChange={() => setRole('CUSTOMER')}
                    />
                    CUSTOMER
                </label>
                <label>
                    <input
                    type="radio"
                    name="role"
                    value="MANAGER"
                    checked={role === 'MANAGER'}
                    onChange={() => setRole('MANAGER')}
                    />
                    MANAGER
                </label>
                
            </div>
            {error && <p style={{color: 'red'}}>{error}</p>}
            <button type="submit" disabled={isLoading}>
                {isLoading ? "registering..." : "Register"}
            </button>
        </form>
    )
}