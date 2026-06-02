import { showError } from "./messageHandler";

export const saveTokensAndNavigate = (tokenData, navigate) => {
    localStorage.setItem("accessToken", tokenData.accessToken);
    localStorage.setItem("refreshToken", tokenData.refreshToken);

    navigate('/');
};

export const handleApiError = (error, defaultErrorMessage, specificHandler) => {
    console.error(error);

    if (error.response?.status === 400 && specificHandler) {
        specificHandler();
    } else {
        const errorMessage = error.response?.data?.message || defaultErrorMessage;
        showError(errorMessage);
    }
};
