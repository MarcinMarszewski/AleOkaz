import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { fetchWithAuth } from "../services/fetchWithAuth";

import './MiniUserComponent.css';

export default function MiniUserComponent({ userId }) {
    const [username, setUsername] = useState("");
    const [profilePicture, setProfilePicture] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadUserInfo();
    }, []);

    const loadUserInfo = async () => {
        setError(null);
        setUsername("");
        setProfilePicture("");
        let url = "http://localhost:8080/api/users/info/" + userId;
        try {
            const res = await fetchWithAuth(url, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            }, navigate);

            const data = await res.json();
            setUsername(data.username);
            setProfilePicture(data.profilePicture);
        } catch (err) {
            setError(err.message || "Loading profile failed");
        }
    };

    return (
        <div className="mini-user-component-container">
            {profilePicture && (
                <img
                    src={profilePicture}
                    alt="Profile"
                    className="profile-picture"
                />
            )}
            <span className="username">{username}</span>
        </div>
    );
}
