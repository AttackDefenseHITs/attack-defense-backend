import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { showError } from "../../utils/messageHandler";
import { handleRequest } from "./handleRequest.js";

export const axiosUpdateUserProfile = async (updatedProfile) => {
    return handleRequest(() => api.put(API_URLS.PROFILE, updatedProfile), {
        errorMessage: 'Ошибка обновления профиля:',
        onError: (error) => {
        const errorMessage = error.response?.data?.message || 'Ошибка обновления профиля';
        showError(errorMessage);
        },
    });
};
