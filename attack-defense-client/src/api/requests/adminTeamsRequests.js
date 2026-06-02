import { api } from "../axiosInstance.js";
import { API_URLS } from "../../constants/apiUrls.js";
import { handleRequest } from "./handleRequest.js";

export const axiosCreateTeam = async (teamData) => {
    return handleRequest(() => api.post(API_URLS.TEAM, teamData), {
        errorMessage: 'Ошибка при создании команды:',
        rethrow: true,
    });
};

export const axiosDeleteTeam = async (teamId) => {
    return handleRequest(() => api.delete(`${API_URLS.TEAM}/${teamId}`), {
        errorMessage: 'Ошибка при удалении команды:',
        rethrow: true,
    });
};

export const axiosCreateManyTeams = async (request) => {
    return handleRequest(() => api.post(`${API_URLS.TEAM}/bulk`, request), {
        errorMessage: 'Ошибка при создании нескольких команд:',
        rethrow: true,
    });
};

export const axiosUpdateTeam = async (teamId, teamData) => {
    return handleRequest(() => api.put(`${API_URLS.TEAM}/${teamId}`, teamData), {
        errorMessage: 'Ошибка при обновлении данных команды:',
        rethrow: true,
    });
};

export const axiosRemoveMemberFromTeam = async (teamId, userId) => {
    return handleRequest(() => api.delete(`${API_URLS.TEAM}/${teamId}/members/${userId}`), {
        errorMessage: 'Ошибка при удалении участника из команды:',
        rethrow: true,
    });
};
