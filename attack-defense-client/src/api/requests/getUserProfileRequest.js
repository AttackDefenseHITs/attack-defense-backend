import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { showError } from "../../utils/messageHandler";
import { handleRequest } from "./handleRequest.js";

export const axiosGetUserProfile = async () => {
    return handleRequest(() => api.get(API_URLS.PROFILE), {
        errorMessage: 'Ошибка получения профиля:',
        onError: (error) => {
        const errorMessage = error.response?.data?.message || 'Ошибка получения профиля';
        showError(errorMessage);
        },
    });
};
