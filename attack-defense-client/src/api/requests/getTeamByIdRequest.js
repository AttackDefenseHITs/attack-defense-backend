import {api} from "../axiosInstance.js";
import {API_URLS} from "../../constants/apiUrls.js";
import {showError } from "../../utils/messageHandler";
import { handleRequest } from "./handleRequest.js";

export const axiosGetTeamById = async (id) => {
    return handleRequest(() => api.get(`${API_URLS.TEAM}/${id}`), {
        errorMessage: 'Ошибка при получении данных команды:',
        onError: (error) => {
            const errorMessage = error.response?.data?.message || 'Ошибка при получении данных команды';
            showError(errorMessage);
        },
    });
};
