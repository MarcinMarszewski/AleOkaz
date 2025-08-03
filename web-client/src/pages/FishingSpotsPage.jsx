import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import FishingSpotComponent from "../components/FishingSpotComponent";
import CreateFishingSpotComponent from "../components/CreateFishingSpotComponent";
import { fetchWithAuth } from "../services/fetchWithAuth";

export default function FishingSpotsPage() {
    const [fishingSpotItems, setFishingSpotItems] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetchFishingSpots();
    }, []);

    const fetchFishingSpots = async () => {
        setError(null);
        setFishingSpotItems(null);

        try {
            const res = await fetchWithAuth("http://localhost:8080/api/fishingspots/all", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                }
            }, navigate);
            const data = await res.json();
            setFishingSpotItems(data);
        } catch (err) {
            setError(err.message || "Loading fishing spots failed");
        }
    }

    return (
        <div className="fishing-spots-page-container">
            <CreateFishingSpotComponent />
            <h1>Fishing Spots</h1>
            {error && <p>{error}</p>}
            {fishingSpotItems && fishingSpotItems.map((spot) => (
                <FishingSpotComponent spot={spot} key={spot.id}/>
            ))}
        </div>
    );
}