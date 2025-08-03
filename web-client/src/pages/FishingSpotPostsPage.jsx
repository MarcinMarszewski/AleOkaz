import { useParams } from "react-router-dom";

export default function FishingSpotPostsPage() {
    const { fishingSpotId } = useParams();
    return (
        <div className="post-page-container">
            <h1>Post</h1>
            <p>Post content will be displayed here.</p>
            <p>Fishing Spot ID: {fishingSpotId}</p>
        </div>
    );
}