export const handleRequest = async (
    request,
    { errorMessage, onError, rethrow = false } = {}
) => {
    try {
        return await request();
    } catch (error) {
        if (errorMessage) {
            console.error(errorMessage, error);
        } else if (!onError) {
            console.error(error);
        }

        if (onError) {
            onError(error);
        }

        if (rethrow) {
            throw error;
        }

        return undefined;
    }
};
