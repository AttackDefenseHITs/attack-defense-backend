import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetAllStatuses = async () => {
    return handleRequest(() => api.get(`${API_URLS.STATUSES}`), {
        errorMessage: 'Ошибка при получении статусов сервисов:',
    });
};

export const axiosPostStartCheckerForService = async (serviceId, teamId, commands) => {
    return handleRequest(async () => {
      const url = `${API_URLS.CHECKERS}/${serviceId}/${teamId}/run`;
      const response = await api.post(url, {
        commands,
      });
      return response.data;
    }, {
      errorMessage: 'Ошибка запуска чекера',
    });
};
