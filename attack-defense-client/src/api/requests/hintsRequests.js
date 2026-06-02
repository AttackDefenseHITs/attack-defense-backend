import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosBuyHint = async (templateId) => {
    return handleRequest(() => api.post(`${API_URLS.HINTS}/${templateId}/buy`), {
        errorMessage: "Ошибка при покупке подсказки:",
        rethrow: true,
    });
};

export const axiosGetHintsByServiceId = async (serviceId) => {
    return handleRequest(() => api.get(`${API_URLS.HINTS}/service/${serviceId}`), {
        errorMessage: "Ошибка при получении подсказок по сервису:",
        rethrow: true,
    });
};
