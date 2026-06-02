import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetAllServices = async () => {
    return handleRequest(
        () => api.get(API_URLS.SERVICES),
        { errorMessage: 'Ошибка при загрузке сервисов:' }
    );
};

export const axiosCreateService = async (serviceData) => {
    return handleRequest(
        () => api.post(API_URLS.SERVICES, serviceData),
        { errorMessage: 'Ошибка при создании сервиса:' }
    );
};

export const axiosDeleteService = async (serviceId) => {
    return handleRequest(
        () => api.delete(`${API_URLS.SERVICES}/${serviceId}`),
        { errorMessage: 'Ошибка при удалении сервиса:' }
    );
};

export const axiosGetServiceDetails = async (serviceId) => {
    return handleRequest(
        () => api.get(`${API_URLS.SERVICES}/${serviceId}`),
        { errorMessage: 'Ошибка при получении данных сервиса:' }
    );
};

export const axiosUpdateService = async (serviceId, serviceData) => {
    return handleRequest(
        () => api.put(`${API_URLS.SERVICES}/${serviceId}`, serviceData),
        { errorMessage: 'Ошибка при обновлении данных сервиса:' }
    );
};
