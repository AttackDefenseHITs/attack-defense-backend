import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetAdminHints = async () => {
    return handleRequest(() => api.get(`${API_URLS.ADMIN_HINTS}`), {
        errorMessage: "Ошибка при получении подсказок администратора:",
    });
};

export const axiosCreateAdminHint = async ({ serviceId, text, multiplier }) => {
    return handleRequest(
        () => api.post(`${API_URLS.ADMIN_HINTS}`, {
            serviceId,
            text,
            multiplier,
        }),
        {
            errorMessage: "Ошибка при создании подсказки:",
            rethrow: true,
        }
    );
};

export const axiosSetAdminHintEnabled = async (hintTemplateId, enabled) => {
    return handleRequest(
        () => api.put(
            `${API_URLS.ADMIN_HINTS}/${hintTemplateId}/enabled`,
            { enabled }
        ),
        {
            errorMessage: "Ошибка при включении/выключении подсказки:",
            rethrow: true,
        }
    );
};

export const axiosDeleteAdminHint = async (hintTemplateId) => {
    return handleRequest(() => api.delete(`${API_URLS.ADMIN_HINTS}/${hintTemplateId}`), {
        errorMessage: "Ошибка при удалении подсказки:",
        rethrow: true,
    });
};

export const axiosGetAdminHintsByService = async (serviceId) => {
    return handleRequest(() => api.get(`${API_URLS.ADMIN_HINTS}/service/${serviceId}`), {
        errorMessage: "Ошибка при получении подсказок по сервису:",
        rethrow: true,
    });
};
