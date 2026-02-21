export const registerReply = async (postId, content) => {
    const postReplyWriteDTO = {
        postId: postId,
        content: content,
    }

    const response = await fetch(`/api/reply/write`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(postReplyWriteDTO),
    });

    return response.json();
}

export const getReplies = async (postId, page) => {
    const response = await fetch(`/api/reply/replies?postId=${postId}&page=${page}`);
    return response.json();
}