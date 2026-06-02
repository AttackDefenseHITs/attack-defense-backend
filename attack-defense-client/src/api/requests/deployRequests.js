import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetDeploymentData = async () => {
    return handleRequest(() => api.get(`${API_URLS.DEPLOY}/status`), {
        errorMessage: 'Ошибка при получении данных деплоя:',
    });
};

export const axiosDeployAllServices = async () => {
    return handleRequest(() => api.post(`${API_URLS.DEPLOY}/all`), {
        errorMessage: 'Ошибка при деплое сервисов:',
    });
};

export const axiosDeploySpecificService = async (serviceId, virtualMachineId) => {
    return handleRequest(
        () => api.post(`${API_URLS.DEPLOY}`, null, {
            params: {
                serviceId,
                virtualMachineId,
            },
        }),
        {
            errorMessage: 'Ошибка при деплое конкретного сервиса:',
            rethrow: true,
        }
    );
};

export const axiosGetDeployPossibility = async () => {
    return handleRequest(() => api.get(`${API_URLS.DEPLOY}/check`), {
        errorMessage: 'Ошибка при получении возможности деплоя:',
    });
};
