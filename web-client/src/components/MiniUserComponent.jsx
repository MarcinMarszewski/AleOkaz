import { useEffect, useState } from "react";
import { UNSAFE_useFogOFWarDiscovery, useNavigate, useRevalidator } from "react-router-dom";

import { fetchWithAuth } from "../services/fetchWithAuth";

import './MiniUserComponent.css';

export default function MiniUserComponent({ userId }) {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadUserInfo();
    }, []);

    const loadUserInfo = async () => {
        setError(null);
        setUser(null);

        let url = "http://localhost:8080/api/users/info/" + userId;
        try {
            const res = await fetchWithAuth(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            }, navigate);

            if (res.status === 200) {
                setUser(await res.json());
            }
        } catch (err) {
            setError(err.message || "Loading profile failed");
        }
    };

    return (
        <div className="mini-user-component-container">
            {user && (<>
                <img
                    src={user.profilePicture}
                    alt="Profile"
                    className="profile-picture"
                />
            <span className="username">{user.username}</span></>)}
        </div>
    );
}
