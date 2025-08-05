import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

import ListPostsComponent from "../components/ListPostsComponent";
import UserProfileComponent from "../components/UserProfileComponent";
import { fetchWithAuth } from "../services/fetchWithAuth";

export default function UserPosts() {
    const { usernameParam } = useParams();
    const [isMe, setIsMe] = useState(true);
    const [userPostItems, setUserPostItems] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        loadPosts();
    }, [usernameParam]);

    const loadPosts = async () => {
        setError(null);
        setUserPostItems([]);

        try {
            setIsMe(usernameParam !== null);
            const res = await fetchWithAuth("http://localhost:8080/api/posts/all", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            }, navigate);

            if (res.status === 200) {
                setUserPostItems(await res.json());
            }
        } catch (err) {
            setError(err.message || "Loading profile failed");
        }
    };

    return (
        <div className="user-profile-page-container">
            <UserProfileComponent username={usernameParam} isMe={isMe} />
            <h1>User Posts</h1>
            <div>
                {error && <p>{error}</p>}
                {userPostItems.length > 0 ? (
                    <ListPostsComponent posts={userPostItems} />
                ) : (
                    <p>No posts available.</p>
                )}
            </div>
        </div>
    );
}