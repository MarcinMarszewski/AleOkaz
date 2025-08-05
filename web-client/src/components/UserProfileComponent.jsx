import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { fetchWithAuth } from "../services/fetchWithAuth";

import './UserProfileComponent.css';

export default function UserProfileComponent({ userId, isMe = true }) {
    const [user, setUser] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadUserInfo();
    }, [isMe]);

    const loadUserInfo = async () => {
        setError(null);
        setUser(null);

        let url = "http://localhost:8080/api/users/info";
        if (!isMe) {
            url += + "/" + userId;
        }

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
        <>
            {user &&
                <div className="user-profile-component-container">
                    <h1>User Profile</h1>
                    {error && <p className="error">{error}</p>}
                    <div className="user-profile-details">
                        <h2>Profile Details</h2>
                        <p>Username: {user.username}</p>
                        <p>Email: {user.email}</p>
                        <img src={user.profilePicture} alt="Profile" className="user-profile-picture" />
                    </div>
                </div>}
        </>
    );
}
