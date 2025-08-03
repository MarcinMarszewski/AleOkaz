import PostComponent from "./PostComponent";

export default function ListPostsComponent({ posts }) {
    return (
        <div className="space-y-4">
            {posts.map(post => (
                <PostComponent key={post.id} post={post} />
            ))}
        </div>
    );
}