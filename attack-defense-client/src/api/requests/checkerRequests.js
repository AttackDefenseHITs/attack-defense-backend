import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { showError } from "../../utils/messageHandler";
import { message } from 'antd';
import { handleRequest } from "./handleRequest.js";

export const axiosGetCheckerForService = async (serviceId) => {
    return handleRequest(() => api.get(`${API_URLS.CHECKERS}/${serviceId}`), {
        rethrow: true,
    });
};

export const axiosPostCheckerForService = async (serviceId, scriptText) => {
    return handleRequest(async () => {
        const response = await api.post(
            `${API_URLS.CHECKERS}/${serviceId}/upload`,
            scriptText,
            {
                headers: {
                    'Content-Type': 'text/plain',
                },
            }
        );
        
        message.success("Скрипт отправлен!");
        return response;
    }, {
        onError: (error) => {
            if (error.response && error.response.status === 400) {
            showError('Ошибка валидации чекера: некорректный скрипт.');
            } else {
            showError('Произошла неизвестная ошибка при отправке скрипта.');
            }
        },
    });
};
