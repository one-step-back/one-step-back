export const likeService = async (postId, status) => {
    const response = await fetch(`/api/like/post?postId=${postId}&status=${status}`, {
        method: 'PATCH'
    });
    return response.json();
}