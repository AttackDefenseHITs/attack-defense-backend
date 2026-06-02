import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { showError } from "../../utils/messageHandler";
import { handleRequest } from "./handleRequest.js";

export const axiosJoinTeam = async (teamId) => {
    return handleRequest(() => api.post(`${API_URLS.TEAM}/${teamId}/join`), {
        errorMessage: 'Ошибка при подключении к команде:',
        onError: (error) => {
        const errorMessage = error.response?.data?.message || 'Ошибка при подключении к команде';
        showError(errorMessage);
        },
    });
};
