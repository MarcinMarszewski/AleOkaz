import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { fetchWithAuth } from "../services/fetchWithAuth";

export default function CreatePostSpotComponent() {
    const [content, setContent] = useState("");
    const [image, setImage] = useState(null);
    const [fishingSpotId, setFishingSpotId] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        const formData = new FormData();
        formData.append("post", new Blob([JSON.stringify({
            content,
            fishingSpotId,
        })], { type: "application/json" }));
        console.log(formData);
        if (image) {
            formData.append("image", image);
        }

        try {
            const res = await fetchWithAuth("http://localhost:8080/api/posts", {
                method: "POST",
                body: formData,
            }, navigate);

            window.location.reload();
        } catch (err) {
            setError(err.message || "Creating fishing spot failed");
        }
    }

    return (
        <div className="p-4 max-w-sm mx-auto">
            <h1 className="text-xl font-bold mb-4">Create Fishing Spot</h1>
            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="text"
                    placeholder="Content"
                    value={content}
                    required
                    onChange={(e) => setContent(e.target.value)}
                    className="w-full border p-2 rounded"
                />
                <input
                    type="text"
                    placeholder="Fishing Spot ID"
                    value={fishingSpotId}
                    required
                    onChange={(e) => setFishingSpotId(e.target.value)}
                    className="w-full border p-2 rounded"
                />
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setImage(e.target.files[0])}
                    className="w-full border p-2 rounded"
                />
                {error && <p className="text-red-600">{error}</p>}
                <button type="submit" className="w-full bg-blue-500 text-white p-2 rounded">Create Post</button>
            </form>
        </div>
    );
}