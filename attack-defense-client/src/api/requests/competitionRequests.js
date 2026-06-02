import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosGetCompetitionSettings = async () => {
    return handleRequest(() => api.get(API_URLS.COMPETITION), {
        errorMessage: 'Ошибка при получении настроек соревнования:',
        rethrow: true,
    });
};

export const axiosGetCompetitionSettingsExtended = async () => {
    return handleRequest(() => api.get(`${API_URLS.COMPETITION}/settings`), {
        errorMessage: 'Ошибка при получении настроек соревнования:',
        rethrow: true,
    });
};

export const axiosUpdateCompetitionSettings = async (settingsData) => {
    return handleRequest(() => api.put(`${API_URLS.COMPETITION}/update`, settingsData), {
        errorMessage: 'Ошибка при обновлении настроек соревнования:',
        rethrow: true,
    });
};

export const axiosChangeCompetitionStatus = async (action) => {
    const actionValue = typeof action === 'object' && action.action ? action.action : action;

    return handleRequest(
        () => api.post(`${API_URLS.COMPETITION}/status`, { action: actionValue }, {
            headers: {
                'Content-Type': 'application/json',
            },
        }),
        {
            errorMessage: 'Ошибка при обновлении статуса соревнования:',
            rethrow: true,
        }
    );
};

export const axiosGetAvailableCompetitionActions = async () => {
    return handleRequest(() => api.get(`${API_URLS.COMPETITION}/available`), {
        errorMessage: 'Ошибка при получении доступных действий соревнования:',
        rethrow: true,
    });
};

export const axiosPostRestartCompetition = async () => {
    return handleRequest(() => api.post(`${API_URLS.COMPETITION}/restart`), {
        errorMessage: 'Ошибка при обновлении:',
        rethrow: true,
    });
};

export const axiosPostRepoSync = async () => {
    return handleRequest(() => api.post(`${API_URLS.ADMIN_REPO}/sync`), {
        errorMessage: 'Ошибка при синхронизации репозитория:',
        rethrow: true,
    });
}

export const axiosPostRepoCreate = async () => {
    return handleRequest(() => api.post(`${API_URLS.ADMIN_REPO}/create`), {
        errorMessage: 'Ошибка при синхронизации репозитория:',
        rethrow: true,
    });
}
