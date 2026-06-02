import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { showError } from "../../utils/messageHandler";
import { handleRequest } from "./handleRequest.js";

export const axiosLeaveTeam = async (teamId) => {
    return handleRequest(() => api.delete(`${API_URLS.TEAM}/${teamId}/leave`), {
        errorMessage: 'Ошибка при выходе из команды:',
        onError: (error) => {
        const errorMessage = error.response?.data?.message || 'Ошибка при выходе из команды';
        showError(errorMessage);
        },
    });
};
