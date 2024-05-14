export const bookmarkService = (() => {

    const artistPost = async (postId, status) => {
        const response = await fetch(`/api/bookmark/post?postId=${postId}&status=${status}`, {
            method: 'PATCH'
        });
        return response.json();
    }

    return {
        artistPost: artistPost,
    }
})();

