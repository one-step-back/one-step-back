export const subscriptionService = async (artistId, status) => {
    const response = await fetch(`/api/subscription/subscribe?artistId=${artistId}&status=${status}`, {
        method: 'GET'
    });
    return response.json();
}