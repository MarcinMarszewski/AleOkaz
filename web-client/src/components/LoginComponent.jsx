import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { authenticate } from "../services/fetchWithAuth";


export default function LoginComponent() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            await authenticate(username, password);
            navigate("/user-profile");
        } catch (err) {
            setError(err.message || "Login failed");
        }
    };

    return (
        <div>
            <h1 className="text-xl font-bold mb-4">Login</h1>
            <form onSubmit={handleLogin} className="space-y-4">
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    required
                    onChange={(e) => setUsername(e.target.value)}
                    className="w-full border p-2 rounded"
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    required
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full border p-2 rounded"
                />
                {error && <p className="text-red-600">{error}</p>}
                <button
                    type="submit"
                    className="w-full bg-blue-600 text-white py-2 rounded"
                >
                    Log In
                </button>
            </form>
        </div>
    );
}