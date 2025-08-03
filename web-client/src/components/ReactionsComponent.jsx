export default function ReactionsComponent({ reactions }) {
    return (
        <div className="flex space-x-2">
            <p className="text-sm text-gray-600">
                {reactions.likes} Likes
            </p>
            <p className="text-sm text-gray-600">
                {reactions.wows} Wows
            </p>
            <p className="text-sm text-gray-600">
                {reactions.hearts} Hearts
            </p>
            <p className="text-sm text-gray-600">
                {reactions.laughs} Laughs
            </p>
            <p className="text-sm text-gray-600">
                {reactions.fish} Fish
            </p>
            <p className="text-sm text-gray-600">
                Your reaction: {reactions.userReaction || "None"}
            </p>
        </div>
    );
}